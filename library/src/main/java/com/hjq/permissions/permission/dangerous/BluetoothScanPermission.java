package com.hjq.permissions.permission.dangerous;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 蓝牙扫描权限类
 */
public final class BluetoothScanPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.BLUETOOTH_SCAN;

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
    public String getName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_12;
    }

    @Override
    public int getMinTargetSdkVersion() {
        // 部分厂商修改了蓝牙权限机制，在 targetSdk 不满足条件的情况下（小于 31），仍需要让应用申请这个权限，相关的 issue 地址：
        // 1. https://github.com/getActivity/XXPermissions/issues/123
        // 2. https://github.com/getActivity/XXPermissions/issues/302
        return AndroidVersionTools.ANDROID_6;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getAccessFineLocationPermission().isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgain(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestPermissions,
                                           @NonNull AndroidManifestInfo androidManifestInfo,
                                           @NonNull List<PermissionInfo> permissionInfoList,
                                           @Nullable PermissionInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion() > getMinSdkVersion(activity, androidManifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, AndroidVersionTools.ANDROID_11);
            // 这是 Android 12 之前遗留的问题，获取扫描蓝牙的结果需要精确定位权限
            checkPermissionRegistrationStatus(permissionInfoList, PermissionConstants.ACCESS_FINE_LOCATION, AndroidVersionTools.ANDROID_11);
        }

        // 如果请求的权限已经包含了精确定位权限，就跳过检查
        if (PermissionUtils.containsPermission(requestPermissions, PermissionConstants.ACCESS_FINE_LOCATION)) {
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
        String maxSdkVersionString = (currentPermissionInfo.maxSdkVersion != Integer.MAX_VALUE) ?
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
            "if your app need use " + currentPermissionInfo.name + " to get physical location, " +
            "also need to add " + PermissionConstants.ACCESS_FINE_LOCATION + " permissions");
    }
}