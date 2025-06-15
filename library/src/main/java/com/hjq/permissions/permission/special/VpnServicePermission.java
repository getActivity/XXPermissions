package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : VPN 权限
 */
public final class VpnServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.BIND_VPN_SERVICE;

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
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionInfo> permissionInfoList,
                                            @Nullable PermissionInfo currentPermissionInfo) {
        // 该权限不需要在清单文件中静态注册，所以注释掉父类的调用
        // super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
    }
}