package com.hjq.permissions.core;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限 Fragment 回调
 */
public interface OnPermissionFragmentCallback {

    /**
     * 权限请求时回调
     */
    default void onRequestPermissionNow() {
        // default implementation ignored
    }

    /**
     * 权限请求完成回调
     */
    void onRequestPermissionFinish();

    /**
     * 权限请求异常回调
     */
    default void onRequestPermissionAnomaly() {
        // default implementation ignored
    }
}