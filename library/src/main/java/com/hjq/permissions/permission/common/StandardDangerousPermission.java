package com.hjq.permissions.permission.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限标准实现类
 */
public final class StandardDangerousPermission extends DangerousPermission {

    public static final Parcelable.Creator<StandardDangerousPermission> CREATOR = new Parcelable.Creator<StandardDangerousPermission>() {

        @Override
        public StandardDangerousPermission createFromParcel(Parcel source) {
            return new StandardDangerousPermission(source);
        }

        @Override
        public StandardDangerousPermission[] newArray(int size) {
            return new StandardDangerousPermission[size];
        }
    };

    /** 权限名称 */
    @NonNull
    private final String mPermissionName;
    /** 权限出现的 Android 版本 */
    private final int mFromAndroidVersion;

    private StandardDangerousPermission(Parcel in) {
        this(in.readString(), in.readInt());
    }

    public StandardDangerousPermission(@NonNull String permissionName, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mFromAndroidVersion = fromAndroidVersion;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPermissionName);
        dest.writeInt(mFromAndroidVersion);
    }

    @NonNull
    @Override
    public String getName() {
        return mPermissionName;
    }

    @Override
    public int getFromAndroidVersion() {
        return mFromAndroidVersion;
    }
}