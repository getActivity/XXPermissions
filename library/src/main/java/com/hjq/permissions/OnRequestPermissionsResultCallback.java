package com.hjq.permissions;

import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/09/29
 *    desc   : 权限申请回调器
 */
public interface OnRequestPermissionsResultCallback {

    /**
     * 权限申请回调结果
     *
     * @param permissions           权限集合
     * @param grantResults          授权结果
     */
    void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults);
}