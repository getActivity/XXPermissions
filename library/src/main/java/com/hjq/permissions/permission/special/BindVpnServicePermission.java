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
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PhoneRomUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : VPN 权限类
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

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // VPN 权限在 Android 15 及以上版本的 OPPO 系统上面是一个不透明的 Activity 页面
        if (PhoneRomUtils.isColorOs() && PermissionVersion.isAndroid15()) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        return VpnService.prepare(context) != null ? PermissionPageType.TRANSPARENT_ACTIVITY : PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_4_0;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        return VpnService.prepare(context) == null;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(2);
        intentList.add(VpnService.prepare(context));
        intentList.add(getAndroidSettingIntent());
        return intentList;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 判断有没有 Service 类注册了 android:permission="android.permission.BIND_VPN_SERVICE" 属性
        List<ServiceManifestInfo> serviceInfoList = manifestInfo.serviceInfoList;
        for (int i = 0; i < serviceInfoList.size(); i++) {

            ServiceManifestInfo serviceInfo = serviceInfoList.get(i);
            String permission = serviceInfo.permission;

            if (permission == null) {
                continue;
            }

            if (!PermissionUtils.equalsPermission(this, permission)) {
                continue;
            }

            String action = "android.net.VpnService";
            // 当前是否注册了 VPN 服务的意图
            boolean registeredVpnServiceAction = false;
            List<IntentFilterManifestInfo> intentFilterInfoList = serviceInfo.intentFilterInfoList;
            if (intentFilterInfoList != null) {
                for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                    if (intentFilterInfo.actionList.contains(action)) {
                        registeredVpnServiceAction = true;
                        break;
                    }
                }
            }
            if (registeredVpnServiceAction) {
                // 符合要求，中断所有的循环并返回，避免走到后面的抛异常代码
                return;
            }

            String xmlCode = "\t\t<intent-filter>\n"
                           + "\t\t    <action android:name=\"" + action + "\" />\n"
                           + "\t\t</intent-filter>";
            throw new IllegalArgumentException("Please add an intent filter for \"" + serviceInfo.name +
                                               "\" in the AndroidManifest.xml file.\n" + xmlCode);
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