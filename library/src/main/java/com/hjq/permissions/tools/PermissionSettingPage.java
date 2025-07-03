package com.hjq.permissions.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.start.IntentNestedHandler;
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
    private static final String EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.huawei.systemmanager";

    /** 小米手机管家 App 包名 */
    private static final String MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.miui.securitycenter";

    /** OPPO 安全中心 App 包名 */
    private static final String COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1 = "com.oppo.safe";
    private static final String COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2 = "com.color.safecenter";
    private static final String COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3 = "com.oplus.safecenter";

    /** vivo 安全中心 App 包名 */
    private static final String ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.iqoo.secure";

    /** 锤子安全中心包名 */
    private static final String SMARTISAN_OS_SECURITY_CENTER_APP_PACKAGE_NAME = "com.smartisanos.securitycenter";

    /** 锤子安全组件包名 */
    private static final String SMARTISAN_OS_SECURITY_COMPONENT_APP_PACKAGE_NAME = "com.smartisanos.security";

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 获取小米应用具体的权限设置页意图
     */
    @Nullable
    public static Intent getXiaoMiApplicationPermissionPageIntent(Context context) {
        Intent appPermEditorActionIntent = new Intent()
            .setAction("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.getPackageName());

        Intent xiaoMiMobileManagerAppIntent = getXiaoMiMobileManagerAppIntent(context);

        Intent intent = null;
        if (PermissionUtils.areActivityIntent(context, appPermEditorActionIntent)) {
            intent = appPermEditorActionIntent;
        }

        if (PermissionUtils.areActivityIntent(context, xiaoMiMobileManagerAppIntent)) {
            intent = IntentNestedHandler.addSubIntentForMainIntent(intent, xiaoMiMobileManagerAppIntent);
        }

        return intent;
    }

    /**
     * 获取三星权限设置意图
     */
    @Nullable
    public static Intent getOneUiPermissionPageIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsDetailsActivity");
        Bundle extraShowFragmentArguments = new Bundle();
        extraShowFragmentArguments.putString("package", context.getPackageName());
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 返回华为手机管家 App 意图
     */
    @Nullable
    public static Intent getHuaWeiMobileManagerAppIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /**
     * 返回小米手机管家 App 意图
     */
    @Nullable
    public static Intent getXiaoMiMobileManagerAppIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /**
     * 获取 oppo 安全中心 App 意图
     */
    @Nullable
    public static Intent getOppoSafeCenterAppIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        intent = context.getPackageManager().getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        intent = context.getPackageManager().getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /**
     * 获取 vivo 管家手机意图
     */
    @Nullable
    public static Intent getVivoMobileManagerAppIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /**
     * 获取锤子安全中心权限设置页意图
     */
    @Nullable
    public static Intent getSmartisanPermissionPageIntent(Context context) {
        Intent intent = new Intent(SMARTISAN_OS_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".action.PACKAGE_OVERVIEW");
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        intent = new Intent();
        intent.setClassName(SMARTISAN_OS_SECURITY_COMPONENT_APP_PACKAGE_NAME, SMARTISAN_OS_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".PackagesOverview");
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_OS_SECURITY_COMPONENT_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_OS_SECURITY_CENTER_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * 获取通用的权限设置页
     */
    @NonNull
    public static Intent getCommonPermissionSettingIntent(@NonNull Context context) {
        return getCommonPermissionSettingIntent(context, (IPermission[]) null);
    }

    @NonNull
    public static Intent getCommonPermissionSettingIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        Intent mainIntent = null;

        Intent applicationDetailsSettingIntent = getApplicationDetailsSettingsIntent(context, permissions);
        if (PermissionUtils.areActivityIntent(context, applicationDetailsSettingIntent)) {
            mainIntent = IntentNestedHandler.addSubIntentForMainIntent(mainIntent, applicationDetailsSettingIntent);
        }

        Intent manageApplicationSettingIntent = getManageApplicationSettingsIntent();
        if (PermissionUtils.areActivityIntent(context, manageApplicationSettingIntent)) {
            mainIntent = IntentNestedHandler.addSubIntentForMainIntent(mainIntent, manageApplicationSettingIntent);
        }

        Intent applicationSettingIntent = getApplicationSettingsIntent();
        if (PermissionUtils.areActivityIntent(context, applicationSettingIntent)) {
            mainIntent = IntentNestedHandler.addSubIntentForMainIntent(mainIntent, applicationSettingIntent);
        }

        Intent androidSettingIntent = getAndroidSettingsIntent();
        mainIntent = IntentNestedHandler.addSubIntentForMainIntent(mainIntent, androidSettingIntent);
        return mainIntent;
    }

    /**
     * 获取应用详情界面意图
     */
    @NonNull
    public static Intent getApplicationDetailsSettingsIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (permissions != null && permissions.length > 0 && PhoneRomUtils.isColorOs()) {
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