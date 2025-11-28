package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : 健身和健康数据权限标准实现类
 */
public final class StandardFitnessAndWellnessDataPermission extends HealthDataBasePermission {

    /** 权限名称 */
    @NonNull
    private final String mPermissionName;
    /** 权限出现的 Android 版本 */
    private final int mFromAndroidVersion;

    public static final Creator<StandardFitnessAndWellnessDataPermission> CREATOR = new Creator<StandardFitnessAndWellnessDataPermission>() {

        @Override
        public StandardFitnessAndWellnessDataPermission createFromParcel(Parcel source) {
            return new StandardFitnessAndWellnessDataPermission(source);
        }

        @Override
        public StandardFitnessAndWellnessDataPermission[] newArray(int size) {
            return new StandardFitnessAndWellnessDataPermission[size];
        }
    };

    public StandardFitnessAndWellnessDataPermission(@NonNull String permissionName, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mFromAndroidVersion = fromAndroidVersion;
    }

    private StandardFitnessAndWellnessDataPermission(Parcel in) {
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