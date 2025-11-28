package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/16
 *    desc   : 传感器权限类
 */
public final class BodySensorsPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BODY_SENSORS;

    public static final Creator<BodySensorsPermission> CREATOR = new Creator<BodySensorsPermission>() {

        @Override
        public BodySensorsPermission createFromParcel(Parcel source) {
            return new BodySensorsPermission(source);
        }

        @Override
        public BodySensorsPermission[] newArray(int size) {
            return new BodySensorsPermission[size];
        }
    };

    public BodySensorsPermission() {
        // default implementation ignored
    }

    private BodySensorsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.SENSORS;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // 当项目 targetSdkVersion >= 36 时，不能申请 BODY_SENSORS 权限，应该请求在读取心率数据权限：READ_HEART_RATE
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_16) {
            throw new IllegalArgumentException("When the project targetSdkVersion is greater than or equal to " +
                PermissionVersion.ANDROID_16 + ", the \"" + getPermissionName() +
                "\" permission cannot be requested, but the \"" +
                PermissionNames.READ_HEART_RATE + "\" permission should be requested instead");
        }
    }
}