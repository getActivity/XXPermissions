package com.hjq.permissions;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 12 权限委托实现
 */
class PermissionDelegateImplV31 extends PermissionDelegateImplV30 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            if (!AndroidVersionTools.isAndroid12()) {
                return true;
            }
            return isGrantedAlarmPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid12()) {
                return PermissionUtils.isGrantedPermission(context, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        if (PermissionUtils.containsPermission(new String[] {
            Permission.BLUETOOTH_CONNECT,
            Permission.BLUETOOTH_ADVERTISE
        }, permission)) {
            if (!AndroidVersionTools.isAndroid12()) {
                return true;
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return false;
        }

        if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid12()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.containsPermission(new String[] {
            Permission.BLUETOOTH_CONNECT,
            Permission.BLUETOOTH_ADVERTISE
        }, permission)) {
            if (!AndroidVersionTools.isAndroid12()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION) &&
            AndroidVersionTools.isAndroid6() && AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_12) {

            if (!PermissionUtils.isGrantedPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                !PermissionUtils.isGrantedPermission(activity, Permission.ACCESS_COARSE_LOCATION)) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                        PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_COARSE_LOCATION);
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            if (!AndroidVersionTools.isAndroid12()) {
                return getApplicationDetailsIntent(context);
            }
            return getAlarmPermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有闹钟权限
     */
    @RequiresApi(AndroidVersionTools.ANDROID_12)
    private static boolean isGrantedAlarmPermission(@NonNull Context context) {
        return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
    }

    /**
     * 获取闹钟权限设置界面意图
     */
    @RequiresApi(AndroidVersionTools.ANDROID_12)
    private static Intent getAlarmPermissionIntent(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}