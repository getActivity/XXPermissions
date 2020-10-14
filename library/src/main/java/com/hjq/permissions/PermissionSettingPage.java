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
final class PermissionSettingPage {

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     */
    static Intent getSmartPermissionIntent(Context context, List<String> deniedPermissions) {
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            return PermissionSettingPage.getApplicationDetailsIntent(context);
        }

        // 如果失败的权限里面包含了特殊权限
        if (PermissionUtils.containsSpecialPermission(deniedPermissions)) {
            // 如果当前只有一个权限被拒绝了
            if (deniedPermissions.size() == 1) {
                String permission = deniedPermissions.get(0);
                if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    return getStoragePermissionIntent(context);
                } else if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                    return getInstallPermissionIntent(context);
                } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                    return getWindowPermissionIntent(context);
                } else if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                    return getNotifyPermissionIntent(context);
                } else if (Permission.WRITE_SETTINGS.equals(permission)) {
                    return getSettingPermissionIntent(context);
                } else {
                    return getApplicationDetailsIntent(context);
                }
            } else if (deniedPermissions.size() == 3) {
                if (deniedPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) &&
                        deniedPermissions.contains(Permission.READ_EXTERNAL_STORAGE) &&
                        deniedPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                    if (PermissionUtils.isAndroid11()) {
                        return getStoragePermissionIntent(context);
                    } else {
                        return PermissionDetailsPage.getIntent(context);
                    }
                }
            }

            return PermissionSettingPage.getApplicationDetailsIntent(context);
        } else {
            return PermissionDetailsPage.getIntent(context);
        }
    }

    /**
     * 获取应用详情界面意图
     */
    static Intent getApplicationDetailsIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * 获取安装权限设置界面意图
     */
    static Intent getInstallPermissionIntent(Context context) {
        Intent intent = null;
        if (PermissionUtils.isAndroid8()) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取悬浮窗权限设置界面意图
     */
    static Intent getWindowPermissionIntent(Context context) {
        Intent intent = null;
        if (PermissionUtils.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }

        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取通知栏权限设置界面意图
     */
    static Intent getNotifyPermissionIntent(Context context) {
        Intent intent = null;
        if (PermissionUtils.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取系统设置权限界面意图
     */
    static Intent getSettingPermissionIntent(Context context) {
        Intent intent = null;
        if (PermissionUtils.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取存储权限设置界面意图
     */
    static Intent getStoragePermissionIntent(Context context) {
        Intent intent = null;
        if (PermissionUtils.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}