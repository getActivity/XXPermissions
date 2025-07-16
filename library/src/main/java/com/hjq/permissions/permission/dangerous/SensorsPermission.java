package com.hjq.permissions.permission.dangerous;

import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/16
 *    desc   : 传感器权限类
 */
public final class SensorsPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BODY_SENSORS;

    public static final Creator<SensorsPermission> CREATOR = new Creator<SensorsPermission>() {

        @Override
        public SensorsPermission createFromParcel(Parcel source) {
            return new SensorsPermission(source);
        }

        @Override
        public SensorsPermission[] newArray(int size) {
            return new SensorsPermission[size];
        }
    };

    public SensorsPermission() {
        // default implementation ignored
    }

    private SensorsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup() {
        return PermissionGroups.SENSORS;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_6;
    }
}