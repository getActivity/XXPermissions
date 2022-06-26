package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限相关工具类
 */
final class PermissionUtils {

    /** Handler 对象 */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 延迟一段时间执行 OnActivityResult，避免有些机型明明授权了，但还是回调失败的问题
     */
    static void postActivityResult(List<String> permissions, Runnable runnable) {
        long delayMillis;
        if (AndroidVersion.isAndroid11()) {
            delayMillis = 200;
        } else {
            delayMillis = 300;
        }

        String manufacturer = Build.MANUFACTURER.toLowerCase();
        if (manufacturer.contains("huawei")) {
            // 需要加长时间等待，不然某些华为机型授权了但是获取不到权限
            if (AndroidVersion.isAndroid8()) {
                delayMillis = 300;
            } else {
                delayMillis = 500;
            }
        } else if (manufacturer.contains("xiaomi")) {
            // 经过测试，发现小米 Android 11 及以上的版本，申请这个权限需要 1 秒钟才能判断到
            // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
            // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
            // 测试了原生的模拟器和 vivo 云测并发现没有这个问题，所以断定这个 Bug 就是小米特有的
            if (AndroidVersion.isAndroid11() &&
                    permissions.contains(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
                delayMillis = 1000;
            }
        }

        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 获取 Android 属性命名空间
     */
    static String getAndroidNamespace() {
        return "http://schemas.android.com/apk/res/android";
    }

    /**
     * 当前是否处于 debug 模式
     */
    static boolean isDebugMode(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static HashMap<String, Integer> getManifestPermissions(Context context) {
        HashMap<String, Integer> manifestPermissions = new HashMap<>();

        XmlResourceParser parser = PermissionUtils.parseAndroidManifest(context);

        if (parser != null) {
            try {

                do {
                    // 当前节点必须为标签头部
                    if (parser.getEventType() != XmlResourceParser.START_TAG) {
                        continue;
                    }

                    // 当前标签必须为 uses-permission
                    if (!"uses-permission".equals(parser.getName())) {
                        continue;
                    }

                    manifestPermissions.put(parser.getAttributeValue(getAndroidNamespace(), "name"),
                            parser.getAttributeIntValue(getAndroidNamespace(), "maxSdkVersion", Integer.MAX_VALUE));

                } while (parser.next() != XmlResourceParser.END_DOCUMENT);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                parser.close();
            }
        }

        if (manifestPermissions.isEmpty()) {
            try {
                // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
                // https://github.com/getActivity/XXPermissions/issues/35
                String[] requestedPermissions = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
                if (requestedPermissions != null) {
                    for (String permission : requestedPermissions) {
                        manifestPermissions.put(permission, Integer.MAX_VALUE);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return manifestPermissions;
    }

    /**
     * 优化权限回调结果
     */
    static void optimizePermissionResults(Activity activity, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {

            boolean recheck = false;

            String permission = permissions[i];

            // 如果这个权限是特殊权限，那么就重新进行权限检测
            if (PermissionApi.isSpecialPermission(permission)) {
                recheck = true;
            }

            // 重新检查 Android 12 的三个新权限
            if (!AndroidVersion.isAndroid12() &&
                    (Permission.BLUETOOTH_SCAN.equals(permission) ||
                            Permission.BLUETOOTH_CONNECT.equals(permission) ||
                            Permission.BLUETOOTH_ADVERTISE.equals(permission))) {
                recheck = true;
            }

            // 重新检查 Android 10.0 的三个新权限
            if (!AndroidVersion.isAndroid10() &&
                    (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                            Permission.ACTIVITY_RECOGNITION.equals(permission) ||
                            Permission.ACCESS_MEDIA_LOCATION.equals(permission))) {
                recheck = true;
            }

            // 重新检查 Android 9.0 的一个新权限
            if (!AndroidVersion.isAndroid9() &&
                    Permission.ACCEPT_HANDOVER.equals(permission)) {
                recheck = true;
            }

            // 重新检查 Android 8.0 的两个新权限
            if (!AndroidVersion.isAndroid8() &&
                    (Permission.ANSWER_PHONE_CALLS.equals(permission) ||
                            Permission.READ_PHONE_NUMBERS.equals(permission))) {
                recheck = true;
            }

            if (recheck) {
                grantResults[i] = PermissionApi.isGrantedPermission(activity, permission) ?
                        PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            }
        }
    }

    /**
     * 将数组转换成 ArrayList
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    @SuppressWarnings("all")
    static <T> ArrayList<T> asArrayList(T... array) {
        ArrayList<T> list = new ArrayList<>(array.length);
        if (array == null || array.length == 0) {
            return list;
        }
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    @SafeVarargs
    static <T> ArrayList<T> asArrayLists(T[]... arrays) {
        ArrayList<T> list = new ArrayList<>();
        if (arrays == null || arrays.length == 0) {
            return list;
        }
        for (T[] ts : arrays) {
            list.addAll(asArrayList(ts));
        }
        return list;
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    static Activity findActivity(Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper){
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    static int findApkPathCookie(Context context) {
        AssetManager assets = context.getAssets();
        String apkPath = context.getApplicationInfo().sourceDir;
        try {
            // 为什么不直接通过反射 AssetManager.findCookieForPath 方法来判断？因为这个 API 属于反射黑名单，反射执行不了
            // 为什么不直接通过反射 AssetManager.addAssetPathInternal 这个非隐藏的方法来判断？因为这个也反射不了
            Method method = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            Integer cookie = (Integer) method.invoke(assets, apkPath);
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
        // 获取失败
        return 0;
    }

    /**
     * 解析清单文件
     */
    static XmlResourceParser parseAndroidManifest(Context context) {
        int cookie = PermissionUtils.findApkPathCookie(context);
        if (cookie == 0) {
            // 如果 cookie 为 0，证明获取失败，直接 return
            return null;
        }

        try {
            XmlResourceParser parser = context.getAssets().openXmlResourceParser(cookie, "AndroidManifest.xml");

            do {
                // 当前节点必须为标签头部
                if (parser.getEventType() != XmlResourceParser.START_TAG) {
                    continue;
                }

                if ("manifest".equals(parser.getName())) {
                    // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
                    // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
                    if (TextUtils.equals(context.getPackageName(),
                            parser.getAttributeValue(null, "package"))) {
                        return parser;
                    }
                }

            } while (parser.next() != XmlResourceParser.END_DOCUMENT);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否适配了分区存储
     */
    static boolean isScopedStorage(Context context) {
        try {
            String metaKey = "ScopedStorage";
            Bundle metaData = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null && metaData.containsKey(metaKey)) {
                return Boolean.parseBoolean(String.valueOf(metaData.get(metaKey)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 锁定当前 Activity 的方向
     */
    @SuppressLint("SwitchIntDef")
    static void lockActivityOrientation(Activity activity) {
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            switch (activity.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    activity.setRequestedOrientation(PermissionUtils.isActivityReverse(activity) ?
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE :
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    activity.setRequestedOrientation(PermissionUtils.isActivityReverse(activity) ?
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT :
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                default:
                    break;
            }
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace();
        }
    }

    /**
     * 判断 Activity 是否反方向旋转了
     */
    static boolean isActivityReverse(Activity activity) {
        // 获取 Activity 旋转的角度
        int activityRotation;
        if (AndroidVersion.isAndroid11()) {
            activityRotation = activity.getDisplay().getRotation();
        } else {
            activityRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        }
        switch (activityRotation) {
            case Surface.ROTATION_180:
            case Surface.ROTATION_270:
                return true;
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
            default:
                return false;
        }
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    static boolean areActivityIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     *
     * @param permissions                 请求失败的权限
     */
    static Intent getSmartPermissionIntent(Context context, List<String> permissions) {
        // 如果失败的权限里面不包含特殊权限
        if (permissions == null || permissions.isEmpty() ||
                !PermissionApi.containsSpecialPermission(permissions)) {
            return PermissionDelegate.getApplicationDetailsIntent(context);
        }

        if (AndroidVersion.isAndroid11() && permissions.size() == 3 &&
                (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) &&
                        permissions.contains(Permission.READ_EXTERNAL_STORAGE) &&
                        permissions.contains(Permission.WRITE_EXTERNAL_STORAGE))) {
            return PermissionDelegateImplV30.getStoragePermissionIntent(context);
        }

        // 如果当前只有一个权限被拒绝了
        if (permissions.size() == 1) {
            return PermissionApi.getPermissionIntent(context, permissions.get(0));
        }

        return PermissionDelegate.getApplicationDetailsIntent(context);
    }
}