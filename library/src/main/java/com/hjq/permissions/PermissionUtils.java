package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager.ResolveInfoFlags;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.permission.base.IPermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.xmlpull.v1.XmlPullParserException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限相关工具类
 */
public final class PermissionUtils {

    /**
     * 当前是否处于 debug 模式
     */
    static boolean isDebugMode(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Nullable
    static AndroidManifestInfo getAndroidManifestInfo(Context context) {
        int apkPathCookie = PermissionUtils.findApkPathCookie(context, context.getApplicationInfo().sourceDir);
        // 如果 cookie 为 0，证明获取失败
        if (apkPathCookie == 0) {
            return null;
        }

        AndroidManifestInfo androidManifestInfo = null;
        try {
            androidManifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie);
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!TextUtils.equals(context.getPackageName(), androidManifestInfo.packageName)) {
                return null;
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return androidManifestInfo;
    }

    /**
     * 将数组转换成 ArrayList
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    @SuppressWarnings("all")
    @NonNull
    static <T> ArrayList<T> asArrayList(@Nullable T... array) {
        int initialCapacity = 0;
        if (array != null) {
            initialCapacity = array.length;
        }
        ArrayList<T> list = new ArrayList<>(initialCapacity);
        if (array == null || array.length == 0) {
            return list;
        }
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    @Nullable
    static Activity findActivity(@Nullable Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                // android.content.ContextWrapper
                // android.content.MutableContextWrapper
                // android.support.v7.view.ContextThemeWrapper
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /**
     * 判断 Activity 是不是不可用
     */
    static boolean isActivityUnavailable(@Nullable Activity activity) {
        return activity == null || activity.isDestroyed()  || activity.isFinishing();
    }

    /**
     * 判断 Fragment 是不是不可用（Support 包版本）
     */
    @SuppressWarnings("deprecation")
    static boolean isFragmentUnavailable(@Nullable android.support.v4.app.Fragment supportFragment) {
        return supportFragment == null || !supportFragment.isAdded() || supportFragment.isRemoving();
    }

    /**
     * 判断 Fragment 是不是不可用（App 包版本）
     */
    @SuppressWarnings("deprecation")
    static boolean isFragmentUnavailable(@Nullable Fragment appFragment) {
        return appFragment == null || !appFragment.isAdded() || appFragment.isRemoving();
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    static int findApkPathCookie(@NonNull Context context, @NonNull String apkPath) {
        AssetManager assets = context.getAssets();
        Integer cookie;

        try {

            if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_9) &&
                AndroidVersionTools.getCurrentAndroidVersionCode() < AndroidVersionTools.ANDROID_11) {

                // 反射套娃操作：实测这种方式只在 Android 9.0 和 Android 10.0 有效果，在 Android 11 上面就失效了
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);
                metaGetDeclaredMethod.setAccessible(true);
                // 注意 AssetManager.findCookieForPath 是 Android 9.0（API 28）的时候才添加的方法
                // 而 Android 9.0 用的是 AssetManager.addAssetPath 来获取 cookie
                // 具体可以参考 PackageParser.parseBaseApk 方法源码的实现
                Method findCookieForPathMethod = (Method) metaGetDeclaredMethod.invoke(AssetManager.class,
                    "findCookieForPath", new Class[]{String.class});
                if (findCookieForPathMethod != null) {
                    findCookieForPathMethod.setAccessible(true);
                    cookie = (Integer) findCookieForPathMethod.invoke(context.getAssets(), apkPath);
                    if (cookie != null) {
                        return cookie;
                    }
                }
            }

            Method addAssetPathMethod = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            cookie = (Integer) addAssetPathMethod.invoke(assets, apkPath);
            if (cookie != null) {
                return cookie;
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 获取失败直接返回 0
        // 为什么不直接返回 Integer，而是返回 int 类型？
        // 去看看 AssetManager.findCookieForPath 获取失败会返回什么就知道了
        return 0;
    }

    /**
     * 通过 MetaData 获得布尔值
     *
     * @param metaKey               Meta Key 值
     * @param defaultValue          当获取不到时返回的默认值
     */
    public static boolean getBooleanByMetaData(@NonNull Context context, @NonNull String metaKey, boolean defaultValue) {
        try {
            Bundle metaData = context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null && metaData.containsKey(metaKey)) {
                return metaData.getBoolean(metaKey);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean areActivityIntent(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null) {
            return false;
        }
        // 这里为什么不用 Intent.resolveActivity(intent) != null 来判断呢？
        // 这是因为在 OPPO R7 Plus （Android 5.0）会出现误判，明明没有这个 Activity，却返回了 ComponentName 对象
        PackageManager packageManager = context.getPackageManager();
        if (AndroidVersionTools.isAndroid13()) {
            return !packageManager.queryIntentActivities(intent,
                    ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)).isEmpty();
        }
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 判断两个权限字符串是否为同一个
     */
    public static boolean equalsPermission(@NonNull String permission1, @NonNull String permission2) {
        int length = permission1.length();
        if (length != permission2.length()) {
            return false;
        }

        // 因为权限字符串都是 android.permission 开头
        // 所以从最后一个字符开始判断，可以提升 equals 的判断效率
        for (int i = length - 1; i >= 0; i--) {
            if (permission1.charAt(i) != permission2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两个权限字符串是否为同一个
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        return equalsPermission(permission1.getPermissionName(), permission2);
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull IPermission permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(item.getPermissionName(), permission.getPermissionName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull String permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(item.getPermissionName(), permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将 List<IPermission> 转换成 List<String> 对象
     */
    @NonNull
    static List<String> convertPermissionList(@Nullable List<IPermission> permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null || permissions.isEmpty()) {
            return list;
        }
        for (IPermission permission : permissions) {
            list.add(permission.getPermissionName());
        }
        return list;
    }

    @NonNull
    static List<String> convertPermissionList(@Nullable IPermission[] permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null) {
            return list;
        }
        for (IPermission permission : permissions) {
            list.add(permission.getPermissionName());
        }
        return list;
    }

    /**
     * 将 List<IPermission> 转换成 String[] 对象
     */
    @NonNull
    static String[] convertPermissionArray(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new String[0];
        }
        String[] list = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            list[i] = permissions.get(i).getPermissionName();
        }
        return list;
    }

    /**
     * 获取包名 uri
     */
    public static Uri getPackageNameUri(@NonNull Context context) {
        return Uri.parse("package:" + context.getPackageName());
    }

    /**
     * 判断某个类的类名是否存在
     */
    public static boolean isClassExist(@Nullable String className) {
        if (className == null) {
            return false;
        }
        if (className.isEmpty()) {
            return false;
        }
        try {
            // 判断这个类有是否存在，如果存在的话，证明是有效的
            // 如果不存在的话，证明无效的，也是需要重新授权的
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取系统属性值（多种方式）
     */
    @NonNull
    public static String getSystemPropertyValue(final String propertyName) {
        String prop;
        try {
            prop = getSystemPropertyByReflect(propertyName);
            if (prop != null && !prop.isEmpty()) {
                return prop;
            }
        } catch (Exception ignored) {}

        try {
            prop = getSystemPropertyByShell(propertyName);
            if (prop != null && !prop.isEmpty()) {
                return prop;
            }
        } catch (IOException ignored) {}

        try {
            prop = getSystemPropertyByStream(propertyName);
            if (prop != null && !prop.isEmpty()) {
                return prop;
            }
        } catch (IOException ignored) {}

        return "";
    }

    /**
     * 获取系统属性值（通过反射系统类）
     */
    @SuppressLint("PrivateApi")
    private static String getSystemPropertyByReflect(String key) throws ClassNotFoundException, InvocationTargetException,
                                                                        NoSuchMethodException, IllegalAccessException  {
        Class<?> clz = Class.forName("android.os.SystemProperties");
        Method getMethod = clz.getMethod("get", String.class, String.class);
        return (String) getMethod.invoke(clz, key, "");
    }

    /**
     * 获取系统属性值（通过 shell 命令）
     */
    private static String getSystemPropertyByShell(final String propName) throws IOException {
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String firstLine = input.readLine();
            if (firstLine != null) {
                return firstLine;
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {}
            }
        }
        return null;
    }

    /**
     * 获取系统属性值（通过读取系统文件）
     */
    private static String getSystemPropertyByStream(final String key) throws IOException {
        FileInputStream inputStream = null;
        try {
            Properties prop = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");
            inputStream = new FileInputStream(file);
            prop.load(inputStream);
            return prop.getProperty(key, "");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
        }
    }
}