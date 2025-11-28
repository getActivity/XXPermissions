package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : 读取以往的健康数据权限类
 */
public final class ReadHealthDataHistoryPermission extends HealthDataBasePermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_HEALTH_DATA_HISTORY;

    public static final Creator<ReadHealthDataHistoryPermission> CREATOR = new Creator<ReadHealthDataHistoryPermission>() {

        @Override
        public ReadHealthDataHistoryPermission createFromParcel(Parcel source) {
            return new ReadHealthDataHistoryPermission(source);
        }

        @Override
        public ReadHealthDataHistoryPermission[] newArray(int size) {
            return new ReadHealthDataHistoryPermission[size];
        }
    };

    public ReadHealthDataHistoryPermission() {
        // default implementation ignored
    }

    private ReadHealthDataHistoryPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_15;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        int thisPermissionIndex = -1;
        int otherHealthDataPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionApi.isHealthPermission(permission) &&
                !PermissionUtils.equalsPermission(permission, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                otherHealthDataPermissionIndex = i;
            }
        }

        if (otherHealthDataPermissionIndex != -1 && otherHealthDataPermissionIndex > thisPermissionIndex) {
            // 请把 READ_HEALTH_DATA_HISTORY 权限放置在其他健康数据权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + requestList.get(otherHealthDataPermissionIndex) + "\" permission");
        }
    }
}