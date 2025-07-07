package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : WIFI 权限类
 */
public final class NearbyWifiDevicesPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.NEARBY_WIFI_DEVICES;

    public static final Parcelable.Creator<NearbyWifiDevicesPermission> CREATOR = new Parcelable.Creator<NearbyWifiDevicesPermission>() {

        @Override
        public NearbyWifiDevicesPermission createFromParcel(Parcel source) {
            return new NearbyWifiDevicesPermission(source);
        }

        @Override
        public NearbyWifiDevicesPermission[] newArray(int size) {
            return new NearbyWifiDevicesPermission[size];
        }
    };

    public NearbyWifiDevicesPermission() {
        // default implementation ignored
    }

    private NearbyWifiDevicesPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup() {
        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        return PermissionVersion.isAndroid13() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 13 以下使用 WIFI 功能需要用到精确定位的权限
        return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList,
            currentPermissionManifestInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion() > getMinSdkVersion(activity, androidManifestInfo)) {
            checkPermissionRegistrationStatus(permissionManifestInfoList, PermissionNames.ACCESS_FINE_LOCATION, PermissionVersion.ANDROID_12_L);
        }

        // 如果请求的权限已经包含了精确定位权限，就跳过检查
        if (PermissionUtils.containsPermission(requestPermissions, PermissionNames.ACCESS_FINE_LOCATION)) {
            return;
        }
        // 如果当前权限没有在清单文件中注册，就跳过检查
        if (currentPermissionManifestInfo == null) {
            return;
        }
        // 如果当前权限有在清单文件注册，并且设置了 neverForLocation 标记，就跳过检查
        if (currentPermissionManifestInfo.neverForLocation()) {
            return;
        }

        // WIFI 权限：https://developer.android.google.cn/about/versions/13/features/nearby-wifi-devices-permission?hl=zh-cn#assert-never-for-location
        // 在以 Android 13 为目标平台时，请考虑您的应用是否会通过 WIFI API 推导物理位置，如果不会，则应坚定声明此情况。
        // 如需做出此声明，请在应用的清单文件中将 usesPermissionFlags 属性设为 neverForLocation
        String maxSdkVersionString = (currentPermissionManifestInfo.maxSdkVersion != Integer.MAX_VALUE) ?
            "android:maxSdkVersion=\"" + currentPermissionManifestInfo.maxSdkVersion + "\" " : "";
        // 根据不同的需求场景决定，解决方法分为两种：
        //   1. 不需要使用 WIFI 权限来获取物理位置：只需要在清单文件中注册的权限上面加上 android:usesPermissionFlags="neverForLocation" 即可
        //   2. 需要使用 WIFI 权限来获取物理位置：在申请 WIFI 权限时，还需要动态申请 ACCESS_FINE_LOCATION 权限
        // 通常情况下，我们都不需要使用 WIFI 权限来获取物理位置，所以选择第一种方法即可
        throw new IllegalArgumentException("If your app doesn't use " + currentPermissionManifestInfo.name +
            " to get physical location, " + "please change the <uses-permission android:name=\"" +
            currentPermissionManifestInfo.name + "\" " + maxSdkVersionString + "/> node in the " +
            "manifest file to <uses-permission android:name=\"" + currentPermissionManifestInfo.name +
            "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/> node, " +
            "if your app need use \"" + currentPermissionManifestInfo.name + "\" to get physical location, " +
            "also need to add \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permissions");
    }
}