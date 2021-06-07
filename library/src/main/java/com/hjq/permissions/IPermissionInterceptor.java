package com.hjq.permissions;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2020/12/26
 *    desc   : 权限请求拦截器
 */
public interface IPermissionInterceptor {

    /**
     * 权限申请拦截，可在此处先弹 Dialog 再申请权限
     */
    default void requestPermissions(Activity activity, OnPermissionCallback callback, List<String> permissions) {
        PermissionFragment.beginRequest(activity, new ArrayList<>(permissions), callback);
    }

    /**
     * 权限授予回调拦截，参见 {@link OnPermissionCallback#onGranted(List, boolean)}
     */
    default void grantedPermissions(Activity activity, OnPermissionCallback callback, List<String> permissions, boolean all) {
        callback.onGranted(permissions, all);
    }

    /**
     * 权限拒绝回调拦截，参见 {@link OnPermissionCallback#onDenied(List, boolean)}
     */
    default void deniedPermissions(Activity activity, OnPermissionCallback callback, List<String> permissions, boolean never) {
        callback.onDenied(permissions, never);
    }
}