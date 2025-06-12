package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.common.DangerousPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 蓝牙扫描权限类
 */
public final class BluetoothScanPermission extends DangerousPermission {

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
        return PermissionConstants.BLUETOOTH_SCAN;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_12;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.ACCESS_FINE_LOCATION.isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.ACCESS_FINE_LOCATION.isDoNotAskAgain(activity);
    }
}