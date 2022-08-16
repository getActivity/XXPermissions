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
    default void requestPermissions(Activity activity, List<String> allPermissions,
                                    OnPermissionCallback callback) {
        PermissionFragment.beginRequest(activity, new ArrayList<>(allPermissions), this, callback);
    }

    /**
     * 权限授予回调拦截，参见 {@link OnPermissionCallback#onGranted(List, boolean)}
     */
    default void grantedPermissions(Activity activity, List<String> allPermissions,
                                    List<String> grantedPermissions, boolean all,
                                    OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onGranted(grantedPermissions, all);
    }

    /**
     * 权限拒绝回调拦截，参见 {@link OnPermissionCallback#onDenied(List, boolean)}
     */
    default void deniedPermissions(Activity activity, List<String> allPermissions,
                                   List<String> deniedPermissions, boolean never,
                                   OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onDenied(deniedPermissions, never);
    }
}