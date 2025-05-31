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
 *    desc   : Android 5.0 权限委托实现
 */
class PermissionDelegateImplV21 extends PermissionDelegateImplV19 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission, boolean skipRequest) {
        if (PermissionUtils.equalsPermission(permission, Permission.PACKAGE_USAGE_STATS)) {
            return isGrantedPackagePermission(context);
        }

        return super.isGrantedPermission(context, permission, skipRequest);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.PACKAGE_USAGE_STATS)) {
            return false;
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.PACKAGE_USAGE_STATS)) {
            return getPackagePermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有使用统计权限
     */
    private static boolean isGrantedPackagePermission(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid5()) {
            return true;
        }
        return PermissionUtils.checkOpNoThrow(context, AppOpsManager.OPSTR_GET_USAGE_STATS);
    }

    /**
     * 获取使用统计权限设置界面意图
     */
    private static Intent getPackagePermissionIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid5()) {
            return getApplicationDetailsIntent(context);
        }
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        if (AndroidVersionTools.isAndroid10()) {
            // 经过测试，只有在 Android 10 及以上加包名才有效果
            // 如果在 Android 10 以下加包名会导致无法跳转
            intent.setData(PermissionUtils.getPackageNameUri(context));
        }
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}