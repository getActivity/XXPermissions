package com.hjq.permissions.manifest.node;

import android.content.pm.PackageInfo;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : 权限清单信息类
 */
public final class PermissionManifestInfo {

    /**
     * 不需要请求地理位置标志
     */
    private static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION;

    static {
        if (PermissionVersion.isAndroid12()) {
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION;
        } else {
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000;
        }
    }

    /**
     * 权限名称
     */
    public String name;
    /**
     * 最大生效 sdk 版本
     */
    public int maxSdkVersion;
    /**
     * 权限使用标志
     */
    public int usesPermissionFlags;

    /**
     * 是否不会用当前权限需要推导地理位置
     */
    public boolean neverForLocation() {
        return (usesPermissionFlags & REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0;
    }
}