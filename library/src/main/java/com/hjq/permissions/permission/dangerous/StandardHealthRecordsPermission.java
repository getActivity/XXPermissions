package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : 健康记录权限标准实现类
 */
public final class StandardHealthRecordsPermission extends HealthDataBasePermission {

    /** 权限名称 */
    @NonNull
    private final String mPermissionName;
    /** 权限出现的 Android 版本 */
    private final int mFromAndroidVersion;

    public static final Creator<StandardHealthRecordsPermission> CREATOR = new Creator<StandardHealthRecordsPermission>() {

        @Override
        public StandardHealthRecordsPermission createFromParcel(Parcel source) {
            return new StandardHealthRecordsPermission(source);
        }

        @Override
        public StandardHealthRecordsPermission[] newArray(int size) {
            return new StandardHealthRecordsPermission[size];
        }
    };

    public StandardHealthRecordsPermission(@NonNull String permissionName, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mFromAndroidVersion = fromAndroidVersion;
    }

    private StandardHealthRecordsPermission(Parcel in) {
        this(Objects.requireNonNull(in.readString()), in.readInt());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPermissionName);
        dest.writeInt(mFromAndroidVersion);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return mPermissionName;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return mFromAndroidVersion;
    }
}