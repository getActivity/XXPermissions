package com.hjq.permissions.tools;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    public static boolean isDebugMode(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
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
    public static <T> ArrayList<T> asArrayList(@Nullable T... array) {
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
    public static Activity findActivity(@Nullable Context context) {
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
    public static boolean isActivityUnavailable(@Nullable Activity activity) {
        return activity == null || activity.isDestroyed()  || activity.isFinishing();
    }

    /**
     * 判断 Fragment 是不是不可用（Support 包版本）
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable android.support.v4.app.Fragment supportFragment) {
        return supportFragment == null || !supportFragment.isAdded() || supportFragment.isRemoving();
    }

    /**
     * 判断 Fragment 是不是不可用（App 包版本）
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable Fragment appFragment) {
        return appFragment == null || !appFragment.isAdded() || appFragment.isRemoving();
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
        if (packageManager == null) {
            return false;
        }
        if (PermissionVersion.isAndroid13()) {
            return !packageManager.queryIntentActivities(intent,
                    ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)).isEmpty();
        }
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 比较字符串是否相等（从第一个字符串开始比较）
     */
    public static boolean equalsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较字符串是否相等（从最后一个字符串开始比较）
     */
    public static boolean reverseEqualsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = length - 1; i >= 0; i--) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两个权限是否为同一个
     */
    public static boolean equalsPermission(@NonNull String permission1, @NonNull String permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1, permission2);
    }

    /**
     * 判断两个权限是否为同一个
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1.getPermissionName(), permission2);
    }

    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull IPermission permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1.getPermissionName(), permission2.getPermissionName());
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
            if (equalsPermission(permission, item.getPermissionName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull List<String> permissions, @NonNull String permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (String item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(permission, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull String permissionName) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(item.getPermissionName(), permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将 List<IPermission> 转换成 List<String> 对象
     */
    @NonNull
    public static List<String> convertPermissionList(@Nullable List<IPermission> permissions) {
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
    public static List<String> convertPermissionList(@Nullable IPermission[] permissions) {
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
    public static String[] convertPermissionArray(@Nullable List<IPermission> permissions) {
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
     * 比较两个 Intent 列表的内容是否一致
     */
    public static boolean equalsIntentList(@NonNull List<Intent> intentList1, @NonNull List<Intent> intentList2) {
        if (intentList1.size() != intentList2.size() ) {
            return false;
        }

        for (int i = 0; i < intentList1.size(); i++) {
            if (!intentList1.get(i).filterEquals(intentList2.get(i))) {
                return false;
            }
        }
        return true;
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