package com.hjq.permissions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限请求流程回调
 */
public interface OnPermissionFlowCallback {

    /**
     * 权限请求时回调
     */
    default void onRequestPermissionNow() {}

    /**
     * 权限请求完成回调
     */
    void onRequestPermissionFinish();

    /**
     * 权限请求异常回调
     */
    default void onRequestPermissionAnomaly() {}
}