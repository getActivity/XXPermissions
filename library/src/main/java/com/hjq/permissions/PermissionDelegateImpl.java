package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.support.annotation.NonNull;
import java.util.Collections;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : 权限委托基础实现
 */
class PermissionDelegateImpl implements IPermissionDelegate {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
            return isGrantedVpnPermission(context);
        }

        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
            return false;
        }

        return false;
    }

    @Override
    public boolean recheckPermissionResult(@NonNull Context context, @NonNull String permission, boolean grantResult) {
        return grantResult;
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
            return getVpnPermissionIntent(context);
        }

        return PermissionIntentManager.getApplicationDetailsIntent(context, Collections.singletonList(permission));
    }

    /**
     * 是否有 VPN 权限
     */
    private static boolean isGrantedVpnPermission(@NonNull Context context) {
        return VpnService.prepare(context) == null;
    }

    /**
     * 获取 VPN 权限设置界面意图
     */
    private static Intent getVpnPermissionIntent(@NonNull Context context) {
        Intent intent = VpnService.prepare(context);
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = PermissionIntentManager.getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 获取应用详情页 Intent
     */
    static Intent getApplicationDetailsIntent(@NonNull Context context) {
        return PermissionIntentManager.getApplicationDetailsIntent(context);
    }
}