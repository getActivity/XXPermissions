package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : VPN 权限
 */
public final class BindVpnServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BIND_VPN_SERVICE;

    public static final Parcelable.Creator<BindVpnServicePermission> CREATOR = new Parcelable.Creator<BindVpnServicePermission>() {

        @Override
        public BindVpnServicePermission createFromParcel(Parcel source) {
            return new BindVpnServicePermission(source);
        }

        @Override
        public BindVpnServicePermission[] newArray(int size) {
            return new BindVpnServicePermission[size];
        }
    };

    public BindVpnServicePermission() {
        // default implementation ignored
    }

    private BindVpnServicePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_4_0;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        return VpnService.prepare(context) == null;
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        Intent intent = VpnService.prepare(context);

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getAndroidSettingAppIntent();
        }
        return intent;
    }
}