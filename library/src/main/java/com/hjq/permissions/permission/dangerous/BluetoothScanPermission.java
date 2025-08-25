package com.hjq.permissions.permission.dangerous;

import android.Manifest;
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
 *    desc   : 蓝牙扫描权限类
 */
public final class BluetoothScanPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BLUETOOTH_SCAN;

    public static final Parcelable.Creator<BluetoothScanPermission> CREATOR = new Parcelable.Creator<BluetoothScanPermission>() {

        @Override
        public BluetoothScanPermission createFromParcel(Parcel source) {
            return new BluetoothScanPermission(source);
        }

        @Override
        public BluetoothScanPermission[] newArray(int size) {
            return new BluetoothScanPermission[size];
        }
    };

    public BluetoothScanPermission() {
        // default implementation ignored
    }

    private BluetoothScanPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        return PermissionVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // 部分厂商修改了蓝牙权限机制，在 targetSdk 不满足条件的情况下（小于 31），仍需要让应用申请这个权限，相关的 issue 地址：
        // 1. https://github.com/getActivity/XXPermissions/issues/123
        // 2. https://github.com/getActivity/XXPermissions/issues/302
        return PermissionVersion.ANDROID_6;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 12 以下扫描蓝牙需要精确定位权限
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
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, PermissionVersion.ANDROID_11);
            // 这是 Android 12 之前遗留的问题，获取扫描蓝牙的结果需要精确定位权限
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION, PermissionVersion.ANDROID_11);
        }

        // 如果请求的权限已经包含了精确定位权限，就跳过检查
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            return;
        }
        // 如果当前权限没有在清单文件中注册，就跳过检查
        if (currentPermissionInfo == null) {
            return;
        }
        // 如果当前权限有在清单文件注册，并且设置了 neverForLocation 标记，就跳过检查
        if (currentPermissionInfo.neverForLocation()) {
            return;
        }

        // 蓝牙权限：https://developer.android.google.cn/guide/topics/connectivity/bluetooth/permissions?hl=zh-cn#assert-never-for-location
        // 如果您的应用不使用蓝牙扫描结果来获取物理位置，则您可以断言您的应用从不使用蓝牙权限来获取物理位置。为此，请完成以下步骤：
        // 将该属性添加 android:usesPermissionFlags 到您的 BLUETOOTH_SCAN 权限声明中，并将该属性的值设置为 neverForLocation
        String maxSdkVersionString = (currentPermissionInfo.maxSdkVersion != PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION) ?
            "android:maxSdkVersion=\"" + currentPermissionInfo.maxSdkVersion + "\" " : "";
        // 根据不同的需求场景决定，解决方法分为两种：
        //   1. 不需要使用蓝牙权限来获取物理位置：只需要在清单文件中注册的权限上面加上 android:usesPermissionFlags="neverForLocation" 即可
        //   2. 需要使用蓝牙权限来获取物理位置：在申请蓝牙权限时，还需要动态申请 ACCESS_FINE_LOCATION 权限
        // 通常情况下，我们都不需要使用蓝牙权限来获取物理位置，所以选择第一种方法即可
        throw new IllegalArgumentException("If your app doesn't use " + currentPermissionInfo.name +
            " to get physical location, " + "please change the <uses-permission android:name=\"" +
            currentPermissionInfo.name + "\" " + maxSdkVersionString + "/> node in the " +
            "manifest file to <uses-permission android:name=\"" + currentPermissionInfo.name +
            "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/> node, " +
            "if your app need use \"" + currentPermissionInfo.name + "\" to get physical location, " +
            "also need to add \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permissions");
    }
}