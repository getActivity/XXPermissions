package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/07/18
 *    desc   : 国产手机具体的权限设置页（兼容大部分国产手机）
 */
final class PermissionDetailsPage {

    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    static Intent getIntent(Context context) {
        Intent intent = null;
        if (MARK.contains("huawei")) {
            intent = huawei(context);
        } else if (MARK.contains("xiaomi")) {
            intent = xiaomi(context);
        } else if (MARK.contains("oppo")) {
            intent = oppo(context);
        } else if (MARK.contains("vivo")) {
            intent = vivo(context);
        } else if (MARK.contains("meizu")) {
            intent = meizu(context);
        }
        return intent;
    }

    private static Intent huawei(Context context) {
        Intent intent = new Intent();

        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    private static Intent xiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    private static Intent oppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());

        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.securitypermission", "com.coloros.securitypermission.permission.PermissionGroupsActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.securitypermission", "com.coloros.securitypermission.permission.PermissionManagerActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    private static Intent vivo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());

        // vivo x7 Y67 Y85
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        // vivo Y66 x20 x9
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        // Y85
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        // 跳转会报 java.lang.SecurityException: Permission Denial
        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }

    private static Intent meizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");

        if (PermissionUtils.hasActivityIntent(context, intent)) {
            return intent;
        }

        return null;
    }
}