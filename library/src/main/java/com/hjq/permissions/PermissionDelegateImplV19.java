package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/11
 *    desc   : Android 4.4 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_4)
class PermissionDelegateImplV19 extends PermissionDelegateImplV18 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        // 检测悬浮窗权限
        if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.isGrantedPermission(context);
        }

        // 检查读取应用列表权限
        if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
            return GetInstalledAppsPermissionCompat.isGrantedPermission(context);
        }

        // 检测通知栏权限
        if (PermissionUtils.equalsPermission(permission, Permission.NOTIFICATION_SERVICE)) {
            return NotificationPermissionCompat.isGrantedPermission(context);
        }
        // 向下兼容 Android 13 新权限
        if (!AndroidVersion.isAndroid13()) {

            if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
                return NotificationPermissionCompat.isGrantedPermission(context);
            }
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return false;
        }

        if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
            return GetInstalledAppsPermissionCompat.isPermissionPermanentDenied(activity);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.NOTIFICATION_SERVICE)) {
            return false;
        }
        // 向下兼容 Android 13 新权限
        if (!AndroidVersion.isAndroid13()) {

            if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
                return false;
            }
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }

    @Override
    public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.getPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
            return GetInstalledAppsPermissionCompat.getPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.NOTIFICATION_SERVICE)) {
            return NotificationPermissionCompat.getPermissionIntent(context);
        }

        // 向下兼容 Android 13 新权限
        if (!AndroidVersion.isAndroid13()) {

            if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
                return NotificationPermissionCompat.getPermissionIntent(context);
            }
        }
        return super.getPermissionIntent(context, permission);
    }
}