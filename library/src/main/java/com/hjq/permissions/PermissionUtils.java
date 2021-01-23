package com.hjq.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
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

    /** 来源于 ApplicationInfo.PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE */
    private static final int PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE = 1 << 29;

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
            return asArrayList(requestedPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 是否有存储权限
     */
    @SuppressWarnings("deprecation")
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
    @SuppressWarnings("ConstantConditions")
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
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                e.printStackTrace();
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
    static boolean isGrantedPermissions(Context context, List<String> permissions) {
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
     * 获取没有授予的权限
     */
    static List<String> getDeniedPermissions(Context context, List<String> permissions) {
        List<String> deniedPermission = new ArrayList<>(permissions.size());

        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6()) {
            return deniedPermission;
        }

        for (String permission : permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermission.add(permission);
            }
        }
        return deniedPermission;
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

        // 检测 10.0 的三个新权限
        if (!isAndroid10()) {
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return true;
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                return context.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
            }
        }

        // 检测 9.0 的一个新权限
        if (!isAndroid9()) {
            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return true;
            }
        }

        // 检测 8.0 的两个新权限
        if (!isAndroid8()) {
            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return true;
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
    static boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (!isAndroid6()) {
            return false;
        }

        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) {
            return false;
        }

        // 检测 10.0 的三个新权限
        if (!isAndroid10()) {
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return false;
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission) ) {
                return activity.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        // 检测 9.0 的一个新权限
        if (!isAndroid9()) {
            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return false;
            }
        }

        // 检测 8.0 的两个新权限
        if (!isAndroid8()) {
            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return true;
            }

            if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
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
    @SuppressWarnings("deprecation")
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

        if (!PermissionUtils.isAndroid8() &&
                permission.contains(Permission.READ_PHONE_NUMBERS) &&
                !permission.contains(Permission.READ_PHONE_STATE)) {
            // 自动添加旧版的读取电话号码权限，因为旧版的系统不支持申请新版的权限
            permission.add(Permission.READ_PHONE_STATE);
        }

        if (!PermissionUtils.isAndroid10() &&
                permission.contains(Permission.ACTIVITY_RECOGNITION) &&
                !permission.contains(Permission.BODY_SENSORS)) {
            // 自动添加传感器权限，因为这个权限是从 Android 10 开始才从传感器权限中剥离成独立权限
            permission.add(Permission.BODY_SENSORS);
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
        if (array == null || array.length == 0) {
            return null;
        }
        ArrayList<T> list = new ArrayList<>(array.length);
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    @SuppressWarnings({"JavaReflectionMemberAccess", "deprecation", "ConstantConditions"})
    static void checkStoragePermission(Context context, List<String> requestPermissions) {
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        // 在 Android 10 的手机才走这个判断，否则不进行判断，因为在 Android 9.0 及以下的手机上不会设置这个标记
        if (targetSdkVersion >= Build.VERSION_CODES.Q && isAndroid10() &&
                (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) ||
                        requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE) ||
                        requestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE))) {

            try {
                // 为什么不通过反射 ApplicationInfo.hasRequestedLegacyExternalStorage 方法来判断？因为这个 API 属于反射黑名单，反射执行不了
                Field field = ApplicationInfo.class.getDeclaredField("privateFlags");
                int privateFlags = (int) field.get(context.getApplicationInfo());
                boolean requestLegacyExternalStorage = (privateFlags & PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE) != 0;
                if (!requestLegacyExternalStorage) {
                    // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性，否则无法在 Android 10 的设备上正常读写外部存储
                    throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" attribute in the manifest file");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 在已经适配 Android 11 的情况下，不能用旧版的存储权限，而是应该用新版的存储权限
        if (targetSdkVersion >= Build.VERSION_CODES.R &&
                (requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE) ||
                        requestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE))) {
            // 请直接使用 Permission.MANAGE_EXTERNAL_STORAGE 来申请权限
            throw new IllegalArgumentException("Please use Permission.MANAGE_EXTERNAL_STORAGE to request storage permission");
        }
    }

    /**
     * 检查定位权限
     *
     * @param requestPermissions    请求的权限组
     */
    static void checkLocationPermission(List<String> requestPermissions) {
        if (!requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {
            return;
        }

        for (String permission : requestPermissions) {
            if (Permission.ACCESS_FINE_LOCATION.equals(permission)
                    || Permission.ACCESS_COARSE_LOCATION.equals(permission)
                    || Permission.ACCESS_BACKGROUND_LOCATION.equals(permission)) {
                continue;
            }

            // 因为包含了后台定位权限，所以请不要申请和定位无关的权限，因为在 Android 11 上面，后台定位权限不能和其他非定位的权限一起申请
            // 否则会出现只申请了后台定位权限，其他权限会被回绝掉，因为在 Android 11 上面，后台定位权限是要跳 Activity，并非弹 Dialog
            throw new IllegalArgumentException("Because it includes background location permissions, do not apply for permissions unrelated to location");
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
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
     * 检测权限有没有在清单文件中注册
     *
     * @param requestPermissions    请求的权限组
     */
    @SuppressWarnings({"deprecation"})
    static void checkPermissionManifest(Context context, List<String> requestPermissions) {
        List<String> manifestPermissions = getManifestPermissions(context);
        if (manifestPermissions == null || manifestPermissions.isEmpty()) {
            throw new ManifestRegisterException();
        }

        int minSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minSdkVersion = context.getApplicationInfo().minSdkVersion;
        } else {
            minSdkVersion = Build.VERSION_CODES.M;
        }

        for (String permission : requestPermissions) {

            if (minSdkVersion < Build.VERSION_CODES.R) {
                if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {

                    if (!manifestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestRegisterException(Permission.READ_EXTERNAL_STORAGE);
                    }

                    if (!manifestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                        // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                        throw new ManifestRegisterException(Permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.Q) {
                if (Permission.ACTIVITY_RECOGNITION.equals(permission) &&
                        !manifestPermissions.contains(Permission.BODY_SENSORS)) {
                    // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                    throw new ManifestRegisterException(Permission.BODY_SENSORS);
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.O) {
                if (Permission.READ_PHONE_NUMBERS.equals(permission) &&
                        !manifestPermissions.contains(Permission.READ_PHONE_STATE)) {
                    // 为了保证能够在旧版的系统上正常运行，必须要在清单文件中注册此权限
                    throw new ManifestRegisterException(Permission.READ_PHONE_STATE);
                }
            }

            if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                // 不检测通知栏权限有没有在清单文件中注册，因为这个权限是框架虚拟出来的，有没有在清单文件中注册都没关系
                continue;
            }

            if (!manifestPermissions.contains(permission)) {
                throw new ManifestRegisterException(permission);
            }
        }
    }

    /**
     * 获得随机的 RequestCode
     */
    static int getRandomRequestCode() {
        // 新版本的 Support 库限制请求码必须小于 65536
        // 旧版本的 Support 库限制请求码必须小于 256
        return new Random().nextInt((int) Math.pow(2, 8));
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    static FragmentActivity findFragmentActivity(Context context) {
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