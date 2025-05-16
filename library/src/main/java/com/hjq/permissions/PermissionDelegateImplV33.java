package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/26
 *    desc   : Android 13 权限委托实现
 */
class PermissionDelegateImplV33 extends PermissionDelegateImplV31 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS);
            }
            // 有后台传感器权限的前提条件是授予了前台的传感器权限
            return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS) &&
                PermissionUtils.checkSelfPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
            if (!AndroidVersionTools.isAndroid13()) {
                return NotificationPermissionCompat.isGrantedPermission(context);
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (PermissionUtils.containsPermission(new String[] {
            Permission.READ_MEDIA_IMAGES,
            Permission.READ_MEDIA_VIDEO,
            Permission.READ_MEDIA_AUDIO
        }, permission)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_13) &&
                                PermissionUtils.equalsPermission(permission, Permission.READ_EXTERNAL_STORAGE)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_IMAGES) &&
                PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VIDEO) &&
                PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_AUDIO);
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.BODY_SENSORS);
            }
            // 先检查前台的传感器权限是否拒绝了
            if (!PermissionUtils.checkSelfPermission(activity, Permission.BODY_SENSORS)) {
                // 如果是的话就判断前台的传感器权限是否被永久拒绝了
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.BODY_SENSORS);
            }
            // 如果不是的话再去判断后台的传感器权限是否被拒永久拒绝了
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
            if (!AndroidVersionTools.isAndroid13()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.containsPermission(new String[] {
            Permission.READ_MEDIA_IMAGES,
            Permission.READ_MEDIA_VIDEO,
            Permission.READ_MEDIA_AUDIO
        }, permission)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid13()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_EXTERNAL_STORAGE);
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_13) &&
                            PermissionUtils.equalsPermission(permission, Permission.READ_EXTERNAL_STORAGE)) {
            return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_MEDIA_IMAGES) &&
                PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_MEDIA_VIDEO) &&
                PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_MEDIA_AUDIO);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS 要跳转到权限设置页和 NOTIFICATION_SERVICE 权限是一样的
        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
            return NotificationPermissionCompat.getPermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }
}