package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.AndroidVersionTools;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

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

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList, currentPermissionManifestInfo);
        // 判断有没有 Service 类注册了 android:permission="android.permission.BIND_VPN_SERVICE" 属性
        List<ServiceManifestInfo> serviceManifestInfoList = androidManifestInfo.serviceManifestInfoList;
        for (int i = 0; i < serviceManifestInfoList.size(); i++) {
            String permission = serviceManifestInfoList.get(i).permission;
            if (permission == null) {
                continue;
            }
            if (PermissionUtils.equalsPermission(getPermissionName(), permission)) {
                // 发现有 Service 注册过，终止循环并返回，避免走到抛异常的情况
                return;
            }
        }

        /*
         没有找到有任何 Service 注册过 android:permission="android.permission.BIND_VPN_SERVICE" 属性，
         请注册该属性给 VpnService 的子类到 AndroidManifest.xml 文件中
         */
        throw new IllegalArgumentException("No Service was found to have registered the android:permission=\"" + getPermissionName() +
            "\" property, Please register this property to VpnService subclass by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }
}