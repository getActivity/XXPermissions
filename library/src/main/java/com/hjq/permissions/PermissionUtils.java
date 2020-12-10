package com.hjq.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求工具类
 */
final class PermissionUtils {

    /**
     * 是否是 Android 11 及以上版本
     */
    static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    /**
     * 是否是 Android 10 及以上版本
     */
    static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 是否是 Android 9.0 及以上版本
     */
    static boolean isAndroid9() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否是 Android 7.0 及以上版本
     */
    static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 是否是 Android 6.0 及以上版本
     */
    static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        try {
            String[] requestedPermissions = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
            // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
            // https://github.com/getActivity/XXPermissions/issues/35
            if (requestedPermissions != null) {
                return asArrayList(requestedPermissions);
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    /**
     * 是否有存储权限
     */
    static boolean isGrantedStoragePermission(Context context) {
        if (isAndroid11()) {
            return Environment.isExternalStorageManager();
        }
        return XXPermissions.isGrantedPermission(context, Permission.Group.STORAGE);
    }

    /**
     * 是否有安装权限
     */
    static boolean isGrantedInstallPermission(Context context) {
        if (isAndroid8()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean isGrantedWindowPermission(Context context) {
        if (isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 是否有通知栏权限
     */
    static boolean isGrantedNotifyPermission(Context context) {
        if (isAndroid7()) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        }

        if (isAndroid6()) {
            // 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled()
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = appOps.getClass().getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field field = appOps.getClass().getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) field.get(Integer.class);
                return ((int) method.invoke(appOps, value, context.getApplicationInfo().uid, context.getPackageName())) == AppOpsManager.MODE_ALLOWED;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException ignored) {
                return true;
            }
        }

        return true;
    }

    /**
     * 是否有系统设置权限
     */
    static boolean isGrantedSettingPermission(Context context) {
        if (isAndroid6()) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    static boolean containsSpecialPermission(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (isSpecialPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    static boolean isSpecialPermission(String permission) {
        return Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
                Permission.REQUEST_INSTALL_PACKAGES.equals(permission) ||
                Permission.SYSTEM_ALERT_WINDOW.equals(permission) ||
                Permission.NOTIFICATION_SERVICE.equals(permission) ||
                Permission.WRITE_SETTINGS.equals(permission);
    }

    /**
     * 判断某些权限是否全部被授予
     */
    static boolean isGrantedPermission(Context context, List<String> permissions) {
        // 如果是安卓 6.0 以下版本就直接返回 true
        if (!isAndroid6()) {
            return true;
        }

        for (String permission : permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断某个权限是否授予
     */
    static boolean isGrantedPermission(Context context, String permission) {
        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6()) {
            return true;
        }

        // 检测存储权限
        if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
            return isGrantedStoragePermission(context);
        }

        // 检测安装权限
        if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
            return isGrantedInstallPermission(context);
        }

        // 检测悬浮窗权限
        if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return isGrantedWindowPermission(context);
        }

        // 检测通知栏权限
        if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
            return isGrantedNotifyPermission(context);
        }

        // 检测系统权限
        if (Permission.WRITE_SETTINGS.equals(permission)) {
            return isGrantedSettingPermission(context);
        }

        if (!isAndroid10()) {
            // 检测 10.0 的三个新权限
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return true;
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                return context.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (!isAndroid9()) {
            // 检测 9.0 的一个新权限
            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return true;
            }
        }

        if (!isAndroid8()) {
            // 检测 8.0 的两个新权限
            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return context.checkSelfPermission(Permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
            }

            if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
                return context.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            }
        }

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取某个权限的状态
     *
     * @return        已授权返回  {@link PackageManager#PERMISSION_GRANTED}
     *                未授权返回  {@link PackageManager#PERMISSION_DENIED}
     */
    static int getPermissionStatus(Context context, String permission) {
        return PermissionUtils.isGrantedPermission(context, permission) ?
                PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    static boolean isPermissionPermanentDenied(Activity activity, List<String> permissions) {
        for (String permission : permissions) {
            if (isPermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    private static boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (!isAndroid6()) {
            return false;
        }

        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) {
            return false;
        }

        if (!isAndroid10()) {
            // 检测 10.0 的三个新权限
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return false;
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission) ) {
                return activity.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!isAndroid9()) {
            // 检测 9.0 的一个新权限
            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return false;
            }
        }

        if (!isAndroid8()) {
            // 检测 8.0 的两个新权限，如果当前版本不符合最低要求，那么就用旧权限进行检测
            if (Permission.ANSWER_PHONE_CALLS.equals(permission) ) {
                return activity.checkSelfPermission(Permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }

            if (Permission.READ_PHONE_NUMBERS.equals(permission) ) {
                return activity.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED &&
                !activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    static List<String> getGrantedPermissions(String[] permissions, int[] grantResults) {
        List<String> grantedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i]);
            }
        }
        return grantedPermissions;
    }

    /**
     * 处理和优化已经过时的权限
     */
    static void optimizeDeprecatedPermission(List<String> permission) {
        // 如果本次申请包含了 Android 11 存储权限
        if (permission.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {

            if (permission.contains(Permission.READ_EXTERNAL_STORAGE) ||
                    permission.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
                throw new IllegalArgumentException("Please do not apply for these two permissions dynamically");
            }

            if (!PermissionUtils.isAndroid11()) {
                // 自动添加旧版的存储权限，因为旧版的系统不支持申请新版的存储权限
                permission.add(Permission.READ_EXTERNAL_STORAGE);
                permission.add(Permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (permission.contains(Permission.ANSWER_PHONE_CALLS)) {
            if (permission.contains(Permission.PROCESS_OUTGOING_CALLS)) {
                // 检测是否有旧版的接听电话权限，有的话直接抛出异常，请不要自己动态申请这个权限
                throw new IllegalArgumentException("Please do not apply for these two permissions dynamically");
            }

            if (!PermissionUtils.isAndroid10() && !permission.contains(Permission.PROCESS_OUTGOING_CALLS)) {
                // 自动添加旧版的接听电话权限，因为旧版的系统不支持申请新版的权限
                permission.add(Permission.PROCESS_OUTGOING_CALLS);
            }
        }

        if (permission.contains(Permission.ACTIVITY_RECOGNITION)) {
            if (!PermissionUtils.isAndroid10() && !permission.contains(Permission.BODY_SENSORS)) {
                // 自动添加传感器权限，因为这个权限是从 Android 10 开始才从传感器权限中剥离成独立权限
                permission.add(Permission.BODY_SENSORS);
            }
        }
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    static boolean areActivityIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 当前是否运行在 Debug 模式下
     */
    static boolean isDebugMode(Context context) {
        return context.getApplicationInfo() != null &&
                (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 将数组转换成 ArrayList
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    static <T> ArrayList<T> asArrayList(T... array) {
        ArrayList<T> list = null;
        if (array != null) {
            list = new ArrayList<>(array.length);
            for (T t : array) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity              Activity对象
     * @param requestPermissions    请求的权限组
     */
    static void checkPermissionManifest(Activity activity, List<String> requestPermissions) {
        List<String> manifestPermissions = getManifestPermissions(activity);
        if (manifestPermissions == null || manifestPermissions.isEmpty()) {
            throw new ManifestException();
        }

        int minSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minSdkVersion = activity.getApplicationInfo().minSdkVersion;
        } else {
            minSdkVersion = Build.VERSION_CODES.M;
        }

        for (String permission : requestPermissions) {

            if (minSdkVersion < Build.VERSION_CODES.R) {
                if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    if (!manifestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestException(Permission.READ_EXTERNAL_STORAGE);
                    }

                    if (!manifestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestException(Permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.Q) {
                if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                    if (!manifestPermissions.contains(Permission.BODY_SENSORS)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestException(Permission.BODY_SENSORS);
                    }
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.O) {
                if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                    if (!manifestPermissions.contains(Permission.PROCESS_OUTGOING_CALLS)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestException(Permission.PROCESS_OUTGOING_CALLS);
                    }
                }

                if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
                    if (!manifestPermissions.contains(Permission.READ_PHONE_STATE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestException(Permission.READ_PHONE_STATE);
                    }
                }
            }

            if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                // 不检测通知栏权限有没有在清单文件中注册，因为这个权限是虚拟出来的，有没有在清单文件中注册都没关系
                continue;
            }

            if (!manifestPermissions.contains(permission)) {
                throw new ManifestException(permission);
            }
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
     * @param context                   上下文对象
     * @param requestPermissions        请求的权限组
     */
    static void checkTargetSdkVersion(Context context, List<String> requestPermissions) {
        // targetSdk 最低版本要求
        int targetSdkMinVersion;
        if (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            // 必须设置 targetSdkVersion >= 30 才能正常检测权限，否则请使用 Permission.Group.STORAGE 来申请存储权限
            targetSdkMinVersion = Build.VERSION_CODES.R;
        } else if (requestPermissions.contains(Permission.ACCEPT_HANDOVER)) {
            targetSdkMinVersion = Build.VERSION_CODES.P;
        } else if (requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION) ||
                requestPermissions.contains(Permission.ACTIVITY_RECOGNITION) ||
                requestPermissions.contains(Permission.ACCESS_MEDIA_LOCATION)) {
            targetSdkMinVersion = Build.VERSION_CODES.Q;
        } else if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES) ||
                requestPermissions.contains(Permission.ANSWER_PHONE_CALLS) ||
                requestPermissions.contains(Permission.READ_PHONE_NUMBERS)) {
            targetSdkMinVersion = Build.VERSION_CODES.O;
        } else {
            targetSdkMinVersion = Build.VERSION_CODES.M;
        }

        // 必须设置正确的 targetSdkVersion 才能正常检测权限
        if (context.getApplicationInfo().targetSdkVersion < targetSdkMinVersion) {
            throw new RuntimeException("The targetSdkVersion SDK must be " + targetSdkMinVersion + " or more");
        }
    }

    /**
     * 获得随机的 RequestCode
     */
    static int getRandomRequestCode() {
        // 请求码必须在 2 的 16 次方以内
        return new Random().nextInt((int) Math.pow(2, 16));
    }

    /**
     * 获取上下文中的 Activity 对象
     */
    static FragmentActivity getFragmentActivity(Context context) {
        do {
            if (context instanceof FragmentActivity) {
                return (FragmentActivity) context;
            } else if (context instanceof ContextWrapper){
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }
}