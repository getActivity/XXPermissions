package com.hjq.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/12
 *    desc   : 读取应用列表权限兼容类
 */
final class GetInstalledAppsPermissionCompat {

    private static final String MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME = "OP_GET_INSTALLED_APPS";
    private static final int MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE = 10022;

    static boolean isGrantedPermission(@NonNull Context context) {
        if (!AndroidVersion.isAndroid4_4()) {
            return true;
        }

        if (AndroidVersion.isAndroid6() && isSupportGetInstalledAppsPermission(context)) {
            return PermissionUtils.checkSelfPermission(context, Permission.GET_INSTALLED_APPS);
        }

        if (PhoneRomUtils.isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!PhoneRomUtils.isMiuiOptimization()) {
                // 如果当前没有开启 miui 优化，则直接返回 true，表示已经授权，因为在这种情况下
                // 就算跳转 miui 权限设置页，用户也授权了，用代码判断权限还是没有授予的状态
                // 所以在没有开启 miui 优化的情况下，就告诉外层已经授予了，避免外层去引导用户跳转到权限设置页
                return true;
            }
            // 经过测试发现，OP_GET_INSTALLED_APPS 是小米在 Android 6.0 才加上的，看了 Android 5.0 的 miui 并没有出现读取应用列表的权限
            return PermissionUtils.checkOpNoThrow(context, MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME, MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE);
        }

        // 如果不支持申请，则直接返回 true（代表有这个权限），反正也不会崩溃，顶多就是获取不到第三方应用列表
        return true;
    }

    static boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        if (!AndroidVersion.isAndroid4_4()) {
            return false;
        }

        if (AndroidVersion.isAndroid6() && isSupportGetInstalledAppsPermission(activity)) {
            // 如果支持申请，那么再去判断权限是否永久拒绝
            return !PermissionUtils.checkSelfPermission(activity, Permission.GET_INSTALLED_APPS) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.GET_INSTALLED_APPS);
        }

        if (PhoneRomUtils.isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!PhoneRomUtils.isMiuiOptimization()) {
                return false;
            }
            // 如果在没有授权的情况下返回 true 表示永久拒绝，这样就能走后面的判断，让外层调用者跳转到小米定制的权限设置页面
            return !isGrantedPermission(activity);
        }

        // 如果不支持申请，则直接返回 false（代表没有永久拒绝）
        return false;
    }

    static Intent getPermissionIntent(@NonNull Context context) {
        if (PhoneRomUtils.isMiui()) {
            Intent intent = null;
            if (PhoneRomUtils.isMiuiOptimization()) {
                intent = PermissionIntentManager.getMiuiPermissionPageIntent(context);
            }
            // 另外跳转到应用详情页也可以开启读取应用列表权限
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        return PermissionIntentManager.getApplicationDetailsIntent(context);
    }

    /**
     * 判断是否支持获取应用列表权限
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    @SuppressWarnings("deprecation")
    private static boolean isSupportGetInstalledAppsPermission(Context context) {
        try {
            PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(Permission.GET_INSTALLED_APPS, 0);
            if (permissionInfo != null) {
                if (AndroidVersion.isAndroid9()) {
                    return permissionInfo.getProtection() == PermissionInfo.PROTECTION_DANGEROUS;
                } else {
                    return (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            // 没有这个权限时会抛出：android.content.pm.PackageManager$NameNotFoundException: com.android.permission.GET_INSTALLED_APPS
            e.printStackTrace();
        }

        try {
            // 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/upload/AssociationStandard/TTAF%20108-2022%20%E7%A7%BB%E5%8A%A8%E7%BB%88%E7%AB%AF%E5%BA%94%E7%94%A8%E8%BD%AF%E4%BB%B6%E5%88%97%E8%A1%A8%E6%9D%83%E9%99%90%E5%AE%9E%E6%96%BD%E6%8C%87%E5%8D%97.pdf
            // 这是兜底方案，因为测试了大量的机型，除了荣耀的 Magic UI 有按照这个规范去做，其他厂商（包括华为的 HarmonyOS）都没有按照这个规范去做
            // 虽然可以只用上面那种判断权限是不是危险权限的方式，但是避免不了有的手机厂商用下面的这种，所以两种都写比较好，小孩子才做选择，大人我全都要
            return Settings.Secure.getInt(context.getContentResolver(), "oem_installed_apps_runtime_permission_enable") == 1;
        } catch (Settings.SettingNotFoundException e) {
            // 没有这个系统属性时会抛出：android.provider.Settings$SettingNotFoundException: oem_installed_apps_runtime_permission_enable
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 判断当前 miui 版本是否支持申请读取应用列表权限
     */
    private static boolean isMiuiSupportGetInstalledAppsPermission() {
        if (!AndroidVersion.isAndroid4_4()) {
            return true;
        }
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            appOpsClass.getDeclaredField(MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME);
            // 证明有这个字段，返回 true
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}