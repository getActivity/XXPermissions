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
 *    desc   : WIFI 权限类
 */
public final class NearbyWifiDevicesPermission extends DangerousPermission {

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
    public String getName() {
        return PermissionConstants.NEARBY_WIFI_DEVICES;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
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