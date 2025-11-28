package com.hjq.permissions.manager;

import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/13
 *    desc   : 已请求权限的管理类
 */
public final class AlreadyRequestPermissionsManager {

    /** 已请求过的权限集 */
    private static final List<String> ALREADY_REQUEST_PERMISSIONS_LIST = new ArrayList<>();

    /** 私有化构造函数 */
    private AlreadyRequestPermissionsManager() {
        // default implementation ignored
    }

    /**
     * 添加已申请过的权限
     */
    public static void addAlreadyRequestPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        for (IPermission permission : permissions) {
            String permissionName = permission.getPermissionName();
            if (PermissionUtils.containsPermission(ALREADY_REQUEST_PERMISSIONS_LIST, permissionName)) {
                continue;
            }
            ALREADY_REQUEST_PERMISSIONS_LIST.add(permissionName);
        }
    }

    /**
     * 判断某些权限是否申请过
     */
    public static boolean isAlreadyRequestPermissions(@Nullable IPermission permission) {
        if (permission == null) {
            return false;
        }
        return PermissionUtils.containsPermission(ALREADY_REQUEST_PERMISSIONS_LIST, permission.getPermissionName());
    }
}