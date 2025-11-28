package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.Objects;

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
    /** 权限组别 */
    @Nullable
    private final String mPermissionGroup;
    /** 权限出现的 Android 版本 */
    private final int mFromAndroidVersion;

    private StandardDangerousPermission(Parcel in) {
        this(Objects.requireNonNull(in.readString()), in.readString(), in.readInt());
    }

    public StandardDangerousPermission(@NonNull String permissionName, int fromAndroidVersion) {
        this(permissionName, null, fromAndroidVersion);
    }

    public StandardDangerousPermission(@NonNull String permissionName, @Nullable String permissionGroup, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mPermissionGroup = permissionGroup;
        mFromAndroidVersion = fromAndroidVersion;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPermissionName);
        dest.writeString(mPermissionGroup);
        dest.writeInt(mFromAndroidVersion);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return mPermissionName;
    }

    @Nullable
    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return mPermissionGroup;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return mFromAndroidVersion;
    }
}