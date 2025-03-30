package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/12
 *    desc   : 国内手机厂商权限设置页管理器
 */
final class PermissionIntentManager {

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

    /**
     * 获取华为悬浮窗权限设置意图
     */
    @Nullable
    static Intent getEmuiWindowPermissionPageIntent(Context context) {
        // EMUI 发展史：http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
        // android 华为版本历史,一文看完华为EMUI发展史：https://blog.csdn.net/weixin_39959369/article/details/117351161

        Intent addViewMonitorActivityIntent = new Intent();
        // emui 3.1 的适配（华为荣耀 7 Android 5.0、华为揽阅 M2 青春版 Android 5.1、华为畅享 5S Android 5.1）
        addViewMonitorActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME + ".addviewmonitor.AddViewMonitorActivity");

        Intent notificationManagementActivityIntent = new Intent();
        // emui 3.0 的适配（华为麦芒 3S Android 4.4）
        notificationManagementActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, "com.huawei.notificationmanager.ui.NotificationManagmentActivity");

        // 华为手机管家主页
        Intent huaWeiMobileManagerAppIntent = getHuaWeiMobileManagerAppIntent(context);

        // 获取厂商版本号
        String romVersionName = PhoneRomUtils.getRomVersionName();
        if (romVersionName == null) {
            romVersionName = "";
        }

        Intent intent = null;
        if (romVersionName.startsWith("3.0")) {
            // 3.0、3.0.1
            if (PermissionUtils.areActivityIntent(context, notificationManagementActivityIntent)) {
                intent = notificationManagementActivityIntent;
            }

            if (PermissionUtils.areActivityIntent(context, addViewMonitorActivityIntent)) {
                intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, addViewMonitorActivityIntent);
            }
        } else {
            // 3.1、其他的
            if (PermissionUtils.areActivityIntent(context, addViewMonitorActivityIntent)) {
                intent = addViewMonitorActivityIntent;
            }

            if (PermissionUtils.areActivityIntent(context, notificationManagementActivityIntent)) {
                intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, notificationManagementActivityIntent);
            }
        }

        if (PermissionUtils.areActivityIntent(context, huaWeiMobileManagerAppIntent)) {
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, huaWeiMobileManagerAppIntent);
        }

        return intent;
    }

    /**
     * 获取小米悬浮窗权限设置意图
     */
    @Nullable
    static Intent getMiuiWindowPermissionPageIntent(Context context) {
        return getMiuiPermissionPageIntent(context);
    }

    /**
     * 获取 oppo 悬浮窗权限设置意图
     */
    @Nullable
    static Intent getColorOsWindowPermissionPageIntent(Context context) {
        // com.color.safecenter 是之前 oppo 安全中心的包名，而 com.oppo.safe 是 oppo 后面改的安全中心的包名
        // 经过测试发现是在 ColorOs 2.1 的时候改的，Android 4.4 还是 com.color.safecenter，到了 Android 5.0 变成了 com.oppo.safe

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.oppo.safe/.permission.floatwindow.FloatWindowListActivity (has extras) } from
        // ProcessRecord{839a7c5 10595:com.hjq.permissions.demo/u0a3781} (pid=10595, uid=13781) not exported from uid 1000
        // intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.color.safecenter/.permission.floatwindow.FloatWindowListActivity (has extras) } from
        // ProcessRecord{42b660b0 31279:com.hjq.permissions.demo/u0a204} (pid=31279, uid=10204) not exported from uid 1000
        // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.color.safecenter/.permission.PermissionAppAllPermissionActivity (has extras) } from
        // ProcessRecord{42c49dd8 1791:com.hjq.permissions.demo/u0a204} (pid=1791, uid=10204) not exported from uid 1000
        // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionAppAllPermissionActivity");

        // 虽然不能直接到达悬浮窗界面，但是到达它的上一级页面（权限隐私页面）还是可以的，所以做了简单的取舍
        // 测试机是 OPPO R7 Plus（Android 5.0，ColorOs 2.1）、OPPO R7s（Android 4.4，ColorOs 2.1）
        // com.oppo.safe.permission.PermissionTopActivity
        // com.oppo.safe..permission.PermissionAppListActivity
        // com.color.safecenter.permission.PermissionTopActivity
        Intent permissionTopActivityActionIntent = new Intent("com.oppo.safe.permission.PermissionTopActivity");

        Intent oppoSafeCenterAppIntent = getOppoSafeCenterAppIntent(context);

        Intent intent = null;

        if (PermissionUtils.areActivityIntent(context, permissionTopActivityActionIntent)) {
            intent = permissionTopActivityActionIntent;
        }

        if (PermissionUtils.areActivityIntent(context, oppoSafeCenterAppIntent)) {
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, oppoSafeCenterAppIntent);;
        }

        return intent;
    }

    /**
     * 获取 vivo 悬浮窗权限设置意图
     */
    @Nullable
    static Intent getOriginOsWindowPermissionPageIntent(Context context) {
        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.iqoo.secure/.ui.phoneoptimize.FloatWindowManager (has extras) } from
        // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has extras) } from
        // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");

        // 经过测试在 vivo x7 Plus（Android 5.1）上面能跳转过去，但是显示却是一个空白页面
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");

        Intent intent = getVivoMobileManagerAppIntent(context);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    @Nullable
    static Intent getOneUiWindowPermissionPageIntent(Context context) {
        return getOneUiPermissionPageIntent(context);
    }

    /* ---------------------------------------------------------------------------------------- */

    @Nullable
    static Intent getMiuiPermissionPageIntent(Context context) {
        Intent appPermEditorActionIntent = new Intent()
            .setAction("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.getPackageName());

        Intent xiaoMiMobileManagerAppIntent = getXiaoMiMobileManagerAppIntent(context);

        Intent intent = null;
        if (PermissionUtils.areActivityIntent(context, appPermEditorActionIntent)) {
            intent = appPermEditorActionIntent;
        }

        if (PermissionUtils.areActivityIntent(context, xiaoMiMobileManagerAppIntent)) {
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, xiaoMiMobileManagerAppIntent);
        }

        return intent;
    }

    @Nullable
    static Intent getOriginOsPermissionPageIntent(Context context) {
        // vivo iQOO 9 Pro（OriginOs 2.0 Android 12）
        Intent intent = new Intent("permission.intent.action.softPermissionDetail");
        intent.putExtra("packagename", context.getPackageName());
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /**
     * 获取三星权限设置意图
     */
    @Nullable
    static Intent getOneUiPermissionPageIntent(Context context) {
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
    static Intent getHuaWeiMobileManagerAppIntent(Context context) {
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
    static Intent getXiaoMiMobileManagerAppIntent(Context context) {
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
    static Intent getOppoSafeCenterAppIntent(Context context) {
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
    static Intent getVivoMobileManagerAppIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return null;
    }

    /* ---------------------------------------------------------------------------------------- */

    @NonNull
    static Intent getApplicationDetailsIntent(@NonNull Context context) {
        return getApplicationDetailsIntent(context, null);
    }

    /**
     * 获取应用详情界面意图
     */
    @NonNull
    static Intent getApplicationDetailsIntent(@NonNull Context context, @Nullable List<String> permissions) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (permissions != null && !permissions.isEmpty() && PhoneRomUtils.isColorOs()) {
            // OPPO 应用权限受阻跳转优化适配：https://open.oppomobile.com/new/developmentDoc/info?id=12983
            Bundle bundle = new Bundle();
            // 元素为受阻权限的原生权限名字符串常量
            bundle.putStringArrayList("permissionList", permissions instanceof ArrayList ?
                (ArrayList<String>) permissions : new ArrayList<>(permissions));
            intent.putExtras(bundle);
            // 传入跳转优化标识
            intent.putExtra("isGetPermission", true);
        }
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }

        intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
        if (PermissionUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        return getAndroidSettingAppIntent();
    }

    /** 跳转到系统设置页面 */
    @NonNull
    static Intent getAndroidSettingAppIntent() {
        return new Intent(Settings.ACTION_SETTINGS);
    }
}