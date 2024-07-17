package com.hjq.permissions;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 6.0 权限委托实现
 */
class PermissionDelegateImplV23 extends PermissionDelegateImplV21 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (!PermissionHelper.isSpecialPermission(permission)) {
            // 读取应用列表权限是比较特殊的危险权限，它和其他危险权限的判断方式不太一样，所以需要放在这里来判断
            if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
                return GetInstalledAppsPermissionCompat.isGrantedPermission(context);
            }

            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.isGrantedPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.WRITE_SETTINGS)) {
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            return isGrantedSettingPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_NOTIFICATION_POLICY)) {
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            return isGrantedNotDisturbPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            return isGrantedIgnoreBatteryPermission(context);
        }

        // Android 6.0 及以下还有一些特殊权限需要判断
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (!PermissionHelper.isSpecialPermission(permission)) {
            // 读取应用列表权限是比较特殊的危险权限，它和其他危险权限的判断方式不太一样，所以需要放在这里来判断
            if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
                return GetInstalledAppsPermissionCompat.isDoNotAskAgainPermission(activity);
            }

            if (!AndroidVersion.isAndroid6()) {
                return false;
            }
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.containsPermission(new String[] {
            Permission.SYSTEM_ALERT_WINDOW,
            Permission.WRITE_SETTINGS,
            Permission.ACCESS_NOTIFICATION_POLICY,
            Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        }, permission)) {
            return false;
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public boolean recheckPermissionResult(@NonNull Context context, @NonNull String permission, boolean grantResult) {
        // 如果是读取应用列表权限（国产权限），则需要重新检查权限的状态
        if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
            return isGrantedPermission(context, permission);
        }

        return super.recheckPermissionResult(context, permission, grantResult);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
            return GetInstalledAppsPermissionCompat.getPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.getPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.WRITE_SETTINGS)) {
            if (!AndroidVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context);
            }
            return getSettingPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_NOTIFICATION_POLICY)) {
            if (!AndroidVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context);
            }
            return getNotDisturbPermissionIntent(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            if (!AndroidVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context);
            }
            return getIgnoreBatteryPermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有系统设置权限
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static boolean isGrantedSettingPermission(@NonNull Context context) {
        return Settings.System.canWrite(context);
    }

    /**
     * 获取系统设置权限界面意图
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static Intent getSettingPermissionIntent(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 是否有勿扰模式权限
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static boolean isGrantedNotDisturbPermission(@NonNull Context context) {
        return context.getSystemService(NotificationManager.class).isNotificationPolicyAccessGranted();
    }

    /**
     * 获取勿扰模式设置界面意图
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static Intent getNotDisturbPermissionIntent(@NonNull Context context) {
        Intent intent;
        if (AndroidVersion.isAndroid10()) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
            intent.setData(PermissionUtils.getPackageNameUri(context));

            // issue 地址：https://github.com/getActivity/XXPermissions/issues/190
            // 这里解释一下，为什么要排除鸿蒙系统，因为用代码能检测到有这个 Intent，也能跳转过去，但是会被马上拒绝
            // 测试过了其他厂商系统及 Android 原生系统都没有这个问题，就只有鸿蒙有这个问题
            // 只因为这个 Intent 是隐藏的意图，所以就不让用，鸿蒙 2.0 和 3.0 都有这个问题
            // 别问鸿蒙 1.0 有没有问题，问就是鸿蒙一发布就 2.0 了，1.0 版本都没有问世过
            // ------------------------ 我是一条华丽的分割线 ----------------------------
            // issue 地址：https://github.com/getActivity/XXPermissions/issues/233
            // 经过测试，荣耀下面这些机子都会出现加包名跳转不过去的问题
            // 荣耀 magic4 Android 13  MagicOs 7.0
            // 荣耀 80 Pro Android 12  MagicOs 7.0
            // 荣耀 X20 SE Android 11  MagicOs 4.1
            // 荣耀 Play5 Android 10  MagicOs 4.0
            if (PhoneRomUtils.isHarmonyOs() || PhoneRomUtils.isMagicOs()) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
        } else {
            intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 是否忽略电池优化选项
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static boolean isGrantedIgnoreBatteryPermission(@NonNull Context context) {
        return context.getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(context.getPackageName());
    }

    /**
     * 获取电池优化选项设置界面意图
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private static Intent getIgnoreBatteryPermissionIntent(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}