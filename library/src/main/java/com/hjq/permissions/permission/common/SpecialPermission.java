package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionType;
import com.hjq.permissions.PhoneRomUtils;
import com.hjq.permissions.permission.base.BasePermission;

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
    public PermissionType getPermissionType() {
        return PermissionType.SPECIAL;
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
        if (AndroidVersionTools.isAndroid11()) {
            waitTime = 200;
        } else {
            waitTime = 300;
        }

        if (PhoneRomUtils.isEmui() || PhoneRomUtils.isHarmonyOs()) {
            // 需要加长时间等待，不然某些华为机型授权了但是获取不到权限
            if (AndroidVersionTools.isAndroid8()) {
                waitTime = 300;
            } else {
                waitTime = 500;
            }
        }
        return waitTime;
    }
}