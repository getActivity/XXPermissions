package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 特殊权限的基类
 */
public abstract class SpecialPermission extends BasePermission {

    protected SpecialPermission() {
        super();
    }

    protected SpecialPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        return PermissionChannel.START_ACTIVITY;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        return false;
    }

    @Override
    public int getResultWaitTime(@NonNull Context context) {
        if (!isSupportRequestPermission(context)) {
            return 0;
        }

        // 特殊权限一律需要一定的等待时间
        int waitTime;
        if (PermissionVersion.isAndroid11()) {
            waitTime = 200;
        } else {
            waitTime = 300;
        }

        if (DeviceOs.isEmui() || DeviceOs.isHarmonyOs() || DeviceOs.isHarmonyOsNextAndroidCompatible()) {
            // 需要加长时间等待，不然某些华为机型授权了但是获取不到权限
            if (PermissionVersion.isAndroid8()) {
                waitTime = 300;
            } else {
                waitTime = 500;
            }
        }
        return waitTime;
    }

    /**
     * 当前权限是否强制在清单文件中静态注册
     */
    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 特殊权限默认不需要在清单文件中注册，这样定义是为了避免外层在自定义特殊权限的时候，还要去重写此方法
        return false;
    }
}