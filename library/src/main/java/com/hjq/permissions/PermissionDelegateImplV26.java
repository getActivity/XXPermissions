package com.hjq.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 8.0 权限委托实现
 */
class PermissionDelegateImplV26 extends PermissionDelegateImplV23 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission, boolean skipRequest) {
        if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
            return isGrantedInstallPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
            return isGrantedPictureInPicturePermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS)) {
            if (!AndroidVersionTools.isAndroid8()) {
                return PermissionUtils.isGrantedPermission(context, Permission.READ_PHONE_STATE);
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
            if (!AndroidVersionTools.isAndroid8()) {
                return true;
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission, skipRequest);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
            return false;
        }

        if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
            return false;
        }

        if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS)) {
            if (!AndroidVersionTools.isAndroid8()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_PHONE_STATE);
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
            if (!AndroidVersionTools.isAndroid8()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
            return getInstallPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
            return getPictureInPicturePermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有安装权限
     */
    private static boolean isGrantedInstallPermission(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return true;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 获取安装权限设置界面意图
     */
    private static Intent getInstallPermissionIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return getApplicationDetailsIntent(context);
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 是否有画中画权限
     */
    private static boolean isGrantedPictureInPicturePermission(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return true;
        }
        return PermissionUtils.checkOpNoThrow(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE);
    }

    /**
     * 获取画中画权限设置界面意图
     */
    private static Intent getPictureInPicturePermissionIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return getApplicationDetailsIntent(context);
        }
        // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
        Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS");
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}