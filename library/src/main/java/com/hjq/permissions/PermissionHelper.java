package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/07/17
 *    desc   : 权限辅助判断类
 */
final class PermissionHelper {

    /** 低等级权限列表（排序时放最后） */
    private static final List<String> LOW_LEVEL_PERMISSION_LIST = new ArrayList<>(1);

    static {

        /* ---------------------------------------------------------------------------------------------------- */

        // 将读取图片位置权限定义为低等级权限
        LOW_LEVEL_PERMISSION_LIST.add(PermissionConstants.ACCESS_MEDIA_LOCATION);
    }

    /**
     * 获取低等级权限列表
     */
    @NonNull
    static List<String> getLowLevelPermissions() {
        return LOW_LEVEL_PERMISSION_LIST;
    }

    /**
     * 通过权限请求间隔时间
     */
    static int getMaxIntervalTimeByPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getRequestIntervalTime();
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }

    /**
     * 通过权限集合获取最大的回调等待时间
     */
    static int getMaxWaitTimeByPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getResultWaitTime();
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }
}