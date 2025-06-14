package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/07/17
 *    desc   : 权限辅助判断类
 */
final class PermissionHelper {

    /** 新旧权限映射集合 */
    private static final Map<String, List<IPermission>> NEW_AND_OLD_PERMISSION_MAP = new HashMap<>(10);

    /** 低等级权限列表（排序时放最后） */
    private static final List<String> LOW_LEVEL_PERMISSION_LIST = new ArrayList<>(1);

    static {

        /* ---------------------------------------------------------------------------------------------------- */

        // Android 13 以下开启通知栏服务，需要用到旧的通知栏权限（框架自己虚拟出来的）
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.POST_NOTIFICATIONS, PermissionUtils.asArrayList(PermissionManifest.getNotificationServicePermission()));
        // Android 13 以下使用 WIFI 功能需要用到精确定位的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.NEARBY_WIFI_DEVICES, PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission()));
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_IMAGES, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_VIDEO, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_AUDIO, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        // Android 12 以下扫描蓝牙需要精确定位权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.BLUETOOTH_SCAN, PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission()));
        // Android 11 以下访问完整的文件管理需要用到读写外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.MANAGE_EXTERNAL_STORAGE, PermissionUtils.asArrayList(
                                                                                PermissionManifest.getReadExternalStoragePermission(),
                                                                                PermissionManifest.getWriteExternalStoragePermission()));
        // Android 8.0 以下读取电话号码需要用到读取电话状态的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_PHONE_NUMBERS, PermissionUtils.asArrayList(PermissionManifest.getReadPhoneStatePermission()));

        /* ---------------------------------------------------------------------------------------------------- */

        // 将读取图片位置权限定义为低等级权限
        LOW_LEVEL_PERMISSION_LIST.add(PermissionConstants.ACCESS_MEDIA_LOCATION);
    }

    /**
     * 通过新权限查询到对应的旧权限
     */
    @Nullable
    static List<IPermission> queryOldPermissionByNewPermission(@NonNull IPermission permission) {
        return NEW_AND_OLD_PERMISSION_MAP.get(permission.getPermissionName());
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