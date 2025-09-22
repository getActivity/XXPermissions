package com.hjq.permissions.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/12
 *    desc   : 权限设置页
 */
public final class PermissionSettingPage {

    /** 华为手机管家 App 包名 */
    private static final String HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.huawei.systemmanager";

    /** 小米手机管家 App 包名 */
    private static final String XiAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.miui.securitycenter";

    /** OPPO 安全中心 App 包名 */
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_1 = "com.coloros.safecenter";
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_2 = "com.color.safecenter";
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_3 = "com.oplus.safecenter";
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_4 = "com.oppo.safe";

    /** vivo 安全中心 App 包名 */
    private static final String VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME_1 = "com.bairenkeji.icaller";
    private static final String VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME_2 = "com.iqoo.secure";

    /** 锤子安全中心包名 */
    private static final String SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAME = "com.smartisanos.securitycenter";
    /** 锤子安全组件包名 */
    private static final String SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME = "com.smartisanos.security";

    /**
     * 获取三星权限设置意图
     */
    @NonNull
    public static Intent getOneUiPermissionPageIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsDetailsActivity");
        Bundle extraShowFragmentArguments = new Bundle();
        extraShowFragmentArguments.putString("package", context.getPackageName());
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        return intent;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 返回华为手机管家 App 意图
     */
    @NonNull
    public static List<Intent> getHuaWeiMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(1);
        Intent intent;

        intent = context.getPackageManager().getLaunchIntentForPackage(HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * 返回小米手机管家 App 意图
     */
    @NonNull
    public static List<Intent> getXiaoMiMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        // 小米手机管家 App -> 应用管理
        intent = new Intent("miui.intent.action.APP_MANAGER");
        intentList.add(intent);

        // 小米手机管家 App -> 主页（隐式意图的形式）
        intent = new Intent("miui.intent.action.SECURITY_CENTER");
        intentList.add(intent);

        // 小米手机管家 App -> 主页（指定包名的形式）
        intent = context.getPackageManager().getLaunchIntentForPackage(XiAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * 获取 oppo 安全中心 App 意图
     */
    @NonNull
    public static List<Intent> getOppoSafeCenterAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_1);
        if (intent != null) {
            intentList.add(intent);
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_2);
        if (intent != null) {
            intentList.add(intent);
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_3);
        if (intent != null) {
            intentList.add(intent);
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_4);
        if (intent != null) {
            intentList.add(intent);
        }

        return intentList;
    }

    /**
     * 获取 vivo 管家手机意图
     */
    @NonNull
    public static List<Intent> getVivoMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(1);
        Intent intent;

        intent = context.getPackageManager().getLaunchIntentForPackage(VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME_1);
        if (intent != null) {
            intentList.add(intent);
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME_2);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * 获取锤子手机安全中心 App
     */
    @NonNull
    public static List<Intent> getSmartisanSecurityCenterAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(2);
        Intent intent;

        intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 获取小米应用具体的权限设置页意图
     */
    @NonNull
    public static Intent getXiaoMiApplicationPermissionPageIntent(Context context) {
        return new Intent("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.getPackageName());
    }

    /**
     * 获取锤子安全中心权限设置页意图
     */
    @NonNull
    public static List<Intent> getSmartisanPermissionPageIntent() {
        List<Intent> intentList = new ArrayList<>(2);
        Intent intent;

        intent = new Intent(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".action.PACKAGE_OVERVIEW");
        intentList.add(intent);

        intent = new Intent();
        intent.setClassName(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME, SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".PackagesOverview");
        intentList.add(intent);

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 获取通用的权限设置页
     */
    @NonNull
    public static List<Intent> getCommonPermissionSettingIntent(@NonNull Context context) {
        return getCommonPermissionSettingIntent(context, (IPermission[]) null);
    }

    @NonNull
    public static List<Intent> getCommonPermissionSettingIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        List<Intent> intentList = new ArrayList<>(4);
        intentList.add(getApplicationDetailsSettingsIntent(context, permissions));
        intentList.add(getManageApplicationSettingsIntent());
        intentList.add(getApplicationSettingsIntent());
        intentList.add(getAndroidSettingsIntent());
        return intentList;
    }

    /**
     * 获取应用详情界面意图
     */
    @NonNull
    public static Intent getApplicationDetailsSettingsIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (permissions != null && permissions.length > 0 && DeviceOs.isColorOs()) {
            // OPPO 应用权限受阻跳转优化适配：https://open.oppomobile.com/new/developmentDoc/info?id=12983
            Bundle bundle = new Bundle();
            List<String> permissionList = PermissionUtils.convertPermissionList(permissions);
            // 元素为受阻权限的原生权限名字符串常量
            bundle.putStringArrayList("permissionList", permissionList instanceof ArrayList ?
                (ArrayList<String>) permissionList : new ArrayList<>(permissionList));
            intent.putExtras(bundle);
            // 传入跳转优化标识
            intent.putExtra("isGetPermission", true);
        }
        return intent;
    }

    /**
     * 获取管理所有应用意图
     */
    @NonNull
    public static Intent getManageApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
    }

    /**
     * 获取所有应用详情页意图
     */
    @NonNull
    public static Intent getApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_APPLICATION_SETTINGS);
    }

    /**
     * 获取系统设置意图
     */
    @NonNull
    public static Intent getAndroidSettingsIntent() {
        return new Intent(Settings.ACTION_SETTINGS);
    }
}