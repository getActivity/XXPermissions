package com.hjq.permissions;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/12/31
 *    desc   : 权限判断类
 */
final class PermissionApi {

    /**
     * 是否有存储权限
     */
    static boolean isGrantedStoragePermission(Context context) {
        if (AndroidVersion.isAndroid11()) {
            return Environment.isExternalStorageManager();
        }
        return isGrantedPermissions(context, PermissionUtils.asArrayList(Permission.Group.STORAGE));
    }

    /**
     * 是否有安装权限
     */
    static boolean isGrantedInstallPermission(Context context) {
        if (AndroidVersion.isAndroid8()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean isGrantedWindowPermission(Context context) {
        if (AndroidVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 是否有系统设置权限
     */
    static boolean isGrantedSettingPermission(Context context) {
        if (AndroidVersion.isAndroid6()) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    /**
     * 是否有通知栏权限
     */
    static boolean isGrantedNotifyPermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 是否通知栏监听的权限
     */
    static boolean isGrantedNotificationListenerPermission(Context context) {
        if (AndroidVersion.isAndroid4_3()) {
            Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
            return packageNames.contains(context.getPackageName());
        }
        return true;
    }

    /**
     * 是否有使用统计权限
     */
    static boolean isGrantedPackagePermission(Context context) {
        if (AndroidVersion.isAndroid5()) {
            AppOpsManager appOps = (AppOpsManager)
                    context.getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            if (AndroidVersion.isAndroid10()) {
                mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        context.getApplicationInfo().uid, context.getPackageName());
            } else {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        context.getApplicationInfo().uid, context.getPackageName());
            }
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return true;
    }

    /**
     * 是否有闹钟权限
     */
    static boolean isGrantedAlarmPermission(Context context) {
        if (AndroidVersion.isAndroid12()) {
            return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
        }
        return true;
    }

    /**
     * 是否有勿扰模式权限
     */
    static boolean isGrantedNotDisturbPermission(Context context) {
        if (AndroidVersion.isAndroid6()) {
            return context.getSystemService(NotificationManager.class).isNotificationPolicyAccessGranted();
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
                Permission.WRITE_SETTINGS.equals(permission) ||
                Permission.NOTIFICATION_SERVICE.equals(permission) ||
                Permission.PACKAGE_USAGE_STATS.equals(permission) ||
                Permission.SCHEDULE_EXACT_ALARM.equals(permission) ||
                Permission.BIND_NOTIFICATION_LISTENER_SERVICE.equals(permission) ||
                Permission.ACCESS_NOTIFICATION_POLICY.equals(permission);
    }

    /**
     * 判断某些权限是否全部被授予
     */
    static boolean isGrantedPermissions(Context context, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取已经授予的权限
     */
    static List<String> getGrantedPermissions(Context context, List<String> permissions) {
        List<String> grantedPermission = new ArrayList<>(permissions.size());
        for (String permission : permissions) {
            if (isGrantedPermission(context, permission)) {
                grantedPermission.add(permission);
            }
        }
        return grantedPermission;
    }

    /**
     * 获取已经拒绝的权限
     */
    static List<String> getDeniedPermissions(Context context, List<String> permissions) {
        List<String> deniedPermission = new ArrayList<>(permissions.size());
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
        // 检测通知栏权限
        if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
            return isGrantedNotifyPermission(context);
        }

        // 检测获取使用统计权限
        if (Permission.PACKAGE_USAGE_STATS.equals(permission)) {
            return isGrantedPackagePermission(context);
        }

        // 检测通知栏监听权限
        if (Permission.BIND_NOTIFICATION_LISTENER_SERVICE.equals(permission)) {
            return isGrantedNotificationListenerPermission(context);
        }

        // 其他权限在 Android 6.0 以下版本就默认授予
        if (!AndroidVersion.isAndroid6()) {
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

        // 检测系统权限
        if (Permission.WRITE_SETTINGS.equals(permission)) {
            return isGrantedSettingPermission(context);
        }

        // 检测闹钟权限
        if (Permission.SCHEDULE_EXACT_ALARM.equals(permission)) {
            return isGrantedAlarmPermission(context);
        }

        // 检测勿扰权限
        if (Permission.ACCESS_NOTIFICATION_POLICY.equals(permission)) {
            return isGrantedNotDisturbPermission(context);
        }

        // 检测 Android 12 的三个新权限
        if (!AndroidVersion.isAndroid12()) {

            if (Permission.BLUETOOTH_SCAN.equals(permission)) {
                return context.checkSelfPermission(Permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
            }

            if (Permission.BLUETOOTH_CONNECT.equals(permission) ||
                    Permission.BLUETOOTH_ADVERTISE.equals(permission)) {
                return true;
            }
        }

        // 检测 Android 10 的三个新权限
        if (!AndroidVersion.isAndroid10()) {

            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission)) {
                return context.checkSelfPermission(Permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                return context.checkSelfPermission(Permission.BODY_SENSORS) ==
                        PackageManager.PERMISSION_GRANTED;
            }

            if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return true;
            }
        }

        // 检测 Android 9.0 的一个新权限
        if (!AndroidVersion.isAndroid9()) {

            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return true;
            }
        }

        // 检测 Android 8.0 的两个新权限
        if (!AndroidVersion.isAndroid8()) {

            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return true;
            }

            if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
                return context.checkSelfPermission(Permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED;
            }
        }

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
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
        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) {
            return false;
        }

        if (!AndroidVersion.isAndroid6()) {
            return false;
        }

        // 检测 Android 12 的三个新权限
        if (!AndroidVersion.isAndroid12()) {

            if (Permission.BLUETOOTH_SCAN.equals(permission)) {
                return !isGrantedPermission(activity, Permission.ACCESS_COARSE_LOCATION) &&
                        !activity.shouldShowRequestPermissionRationale(Permission.ACCESS_COARSE_LOCATION);
            }

            if (Permission.BLUETOOTH_CONNECT.equals(permission) ||
                    Permission.BLUETOOTH_ADVERTISE.equals(permission)) {
                return false;
            }
        }

        if (AndroidVersion.isAndroid10()) {

            // 重新检测后台定位权限是否永久拒绝
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) &&
                    !isGrantedPermission(activity, Permission.ACCESS_BACKGROUND_LOCATION) &&
                    !isGrantedPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                return !activity.shouldShowRequestPermissionRationale(Permission.ACCESS_FINE_LOCATION);
            }
        }

        // 检测 Android 10 的三个新权限
        if (!AndroidVersion.isAndroid10()) {

            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission)) {
                return !isGrantedPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !activity.shouldShowRequestPermissionRationale(Permission.ACCESS_FINE_LOCATION);
            }

            if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                return !isGrantedPermission(activity, Permission.BODY_SENSORS) &&
                        !activity.shouldShowRequestPermissionRationale(Permission.BODY_SENSORS);
            }

            if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return false;
            }
        }

        // 检测 Android 9.0 的一个新权限
        if (!AndroidVersion.isAndroid9()) {

            if (Permission.ACCEPT_HANDOVER.equals(permission)) {
                return false;
            }
        }

        // 检测 Android 8.0 的两个新权限
        if (!AndroidVersion.isAndroid8()) {

            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return false;
            }

            if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
                return !isGrantedPermission(activity, Permission.READ_PHONE_STATE) &&
                        !activity.shouldShowRequestPermissionRationale(Permission.READ_PHONE_STATE);
            }
        }

        return !isGrantedPermission(activity, permission) &&
                !activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getDeniedPermissions(List<String> permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions.get(i));
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
    static List<String> getGrantedPermissions(List<String> permissions, int[] grantResults) {
        List<String> grantedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions.get(i));
            }
        }
        return grantedPermissions;
    }
}