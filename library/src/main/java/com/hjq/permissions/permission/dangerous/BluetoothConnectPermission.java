package com.hjq.permissions.permission.dangerous;

import android.Manifest;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/14
 *    desc   : 蓝牙连接权限
 */
public final class BluetoothConnectPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.BLUETOOTH_CONNECT;

    public static final Parcelable.Creator<BluetoothConnectPermission> CREATOR = new Parcelable.Creator<BluetoothConnectPermission>() {

        @Override
        public BluetoothConnectPermission createFromParcel(Parcel source) {
            return new BluetoothConnectPermission(source);
        }

        @Override
        public BluetoothConnectPermission[] newArray(int size) {
            return new BluetoothConnectPermission[size];
        }
    };

    public BluetoothConnectPermission() {
        // default implementation ignored
    }

    private BluetoothConnectPermission(Parcel in) {
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
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionInfo> permissionInfoList,
                                            @Nullable PermissionInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion() > getMinSdkVersion(activity, androidManifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH, AndroidVersionTools.ANDROID_11);
        }
    }
}