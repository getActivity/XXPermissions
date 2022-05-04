package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2020/08/18
 *    desc   : 权限设置页
 */
final class PermissionPageIntent {

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     *
     * @param permissions                 请求失败的权限
     */
    static Intent getSmartPermissionIntent(Context context, List<String> permissions) {
        // 如果失败的权限里面不包含特殊权限
        if (permissions == null || permissions.isEmpty() ||
                !PermissionApi.containsSpecialPermission(permissions)) {
            return getApplicationDetailsIntent(context);
        }

        if (AndroidVersion.isAndroid11() && permissions.size() == 3 &&
                (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) &&
                        permissions.contains(Permission.READ_EXTERNAL_STORAGE) &&
                        permissions.contains(Permission.WRITE_EXTERNAL_STORAGE))) {
            return getStoragePermissionIntent(context);
        }

        // 如果当前只有一个权限被拒绝了
        if (permissions.size() == 1) {

            String permission = permissions.get(0);

            if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                return getStoragePermissionIntent(context);
            }

            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                return getInstallPermissionIntent(context);
            }

            if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                return getWindowPermissionIntent(context);
            }

            if (Permission.WRITE_SETTINGS.equals(permission)) {
                return getSettingPermissionIntent(context);
            }

            if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                return getNotifyPermissionIntent(context);
            }

            if (Permission.PACKAGE_USAGE_STATS.equals(permission)) {
                return getPackagePermissionIntent(context);
            }

            if (Permission.BIND_NOTIFICATION_LISTENER_SERVICE.equals(permission)) {
                return getNotificationListenerIntent(context);
            }

            if (Permission.SCHEDULE_EXACT_ALARM.equals(permission)) {
                return getAlarmPermissionIntent(context);
            }

            if (Permission.ACCESS_NOTIFICATION_POLICY.equals(permission)) {
                return getNotDisturbPermissionIntent(context);
            }

            if (Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.equals(permission)) {
                return getIgnoreBatteryPermissionIntent(context);
            }
        }

        return getApplicationDetailsIntent(context);
    }

    /**
     * 获取应用详情界面意图
     */
    static Intent getApplicationDetailsIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(getPackageNameUri(context));
        return intent;
    }

    /**
     * 获取安装权限设置界面意图
     */
    static Intent getInstallPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(getPackageNameUri(context));
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取悬浮窗权限设置界面意图
     */
    static Intent getWindowPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent.setData(getPackageNameUri(context));
        }

        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取通知栏权限设置界面意图
     */
    static Intent getNotifyPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取通知监听设置界面意图
     */
    static Intent getNotificationListenerIntent(Context context) {
        Intent intent;
        if (AndroidVersion.isAndroid5_1()) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        } else {
            intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取系统设置权限界面意图
     */
    static Intent getSettingPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(getPackageNameUri(context));
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取存储权限设置界面意图
     */
    static Intent getStoragePermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(getPackageNameUri(context));
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取使用统计权限设置界面意图
     */
    static Intent getPackagePermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid5()) {
            intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            if (AndroidVersion.isAndroid10()) {
                // 经过测试，只有在 Android 10 及以上加包名才有效果
                // 如果在 Android 10 以下加包名会导致无法跳转
                intent.setData(getPackageNameUri(context));
            }
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取勿扰模式设置界面意图
     */
    static Intent getNotDisturbPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取电池优化选项设置界面意图
     */
    static Intent getIgnoreBatteryPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(getPackageNameUri(context));
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取闹钟权限设置界面意图
     */
    static Intent getAlarmPermissionIntent(Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid12()) {
            intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(getPackageNameUri(context));
        }
        if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取包名 Uri 对象
     */
    private static Uri getPackageNameUri(Context context) {
        return Uri.parse("package:" + context.getPackageName());
    }
}