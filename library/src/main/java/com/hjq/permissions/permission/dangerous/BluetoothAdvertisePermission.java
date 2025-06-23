package com.hjq.permissions.permission.dangerous;

import android.Manifest;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.AndroidVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/14
 *    desc   : 蓝牙广播权限
 */
public final class BluetoothAdvertisePermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BLUETOOTH_ADVERTISE;

    public static final Parcelable.Creator<BluetoothAdvertisePermission> CREATOR = new Parcelable.Creator<BluetoothAdvertisePermission>() {

        @Override
        public BluetoothAdvertisePermission createFromParcel(Parcel source) {
            return new BluetoothAdvertisePermission(source);
        }

        @Override
        public BluetoothAdvertisePermission[] newArray(int size) {
            return new BluetoothAdvertisePermission[size];
        }
    };

    public BluetoothAdvertisePermission() {
        // default implementation ignored
    }

    private BluetoothAdvertisePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersion.ANDROID_12;
    }

    @Override
    public String getPermissionGroup() {
        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        return AndroidVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getMinTargetSdkVersion() {
        // 部分厂商修改了蓝牙权限机制，在 targetSdk 不满足条件的情况下（小于 31），仍需要让应用申请这个权限，相关的 issue 地址：
        // 1. https://github.com/getActivity/XXPermissions/issues/123
        // 2. https://github.com/getActivity/XXPermissions/issues/302
        return AndroidVersion.ANDROID_6;
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
            checkPermissionRegistrationStatus(permissionManifestInfoList, Manifest.permission.BLUETOOTH_ADMIN, AndroidVersion.ANDROID_11);
        }
    }
}