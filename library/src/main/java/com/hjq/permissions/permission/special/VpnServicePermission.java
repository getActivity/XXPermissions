package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : VPN 权限
 */
public final class VpnServicePermission extends SpecialPermission {

    public static final Parcelable.Creator<VpnServicePermission> CREATOR = new Parcelable.Creator<VpnServicePermission>() {

        @Override
        public VpnServicePermission createFromParcel(Parcel source) {
            return new VpnServicePermission(source);
        }

        @Override
        public VpnServicePermission[] newArray(int size) {
            return new VpnServicePermission[size];
        }
    };

    public VpnServicePermission() {
        // default implementation ignored
    }

    private VpnServicePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.BIND_VPN_SERVICE;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_4_0;
    }

    @Override
    public boolean isMandatoryStaticRegister() {
        // 表示该权限不需要在清单文件中静态注册
        return false;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        return VpnService.prepare(context) == null;
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        Intent intent = VpnService.prepare(context);

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}