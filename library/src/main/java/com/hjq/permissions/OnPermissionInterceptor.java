package com.hjq.permissions;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.core.RequestPermissionLogicPresenter;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2020/12/26
 *    desc   : 权限请求拦截器
 */
public interface OnPermissionInterceptor {

    /**
     * 发起权限申请（可在此处先弹 Dialog 再申请权限，如果用户已经授予权限，则不会触发此回调）
     *
     * @param requestPermissions        申请的权限
     * @param callback                  权限申请回调
     */
    default void launchPermissionRequest(@NonNull Activity activity,
                                        @NonNull List<IPermission> requestPermissions,
                                        @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                        @NonNull OnPermissionDescription permissionDescription,
                                        @Nullable OnPermissionCallback callback) {
        dispatchPermissionRequest(activity, requestPermissions, fragmentFactory, permissionDescription, callback);
    }

    /**
     * 用户授予了权限（注意需要在此处回调 {@link OnPermissionCallback#onGranted(List, boolean)}）
     *
     * @param requestPermissions         申请的权限
     * @param grantedPermissions         已授予的权限
     * @param allGranted                 是否全部授予
     * @param callback                   权限申请回调
     */
    default void grantedPermissionRequest(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull List<IPermission> grantedPermissions, boolean allGranted,
                                            @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onGranted(grantedPermissions, allGranted);
    }

    /**
     * 用户拒绝了权限（注意需要在此处回调 {@link OnPermissionCallback#onDenied(List, boolean)}）
     *
     * @param requestPermissions        申请的权限
     * @param deniedPermissions         已拒绝的权限
     * @param doNotAskAgain             是否勾选了不再询问选项
     * @param callback                  权限申请回调
     */
    default void deniedPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
                                            @NonNull List<IPermission> deniedPermissions, boolean doNotAskAgain,
                                            @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onDenied(deniedPermissions, doNotAskAgain);
    }

    /**
     * 权限请求完成
     *
     * @param requestPermissions        申请的权限
     * @param skipRequest               是否跳过了申请过程
     * @param callback                  权限申请回调
     */
    default void finishPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
                                            boolean skipRequest, @Nullable OnPermissionCallback callback) {}

    /**
     * 派发权限请求
     *
     * @param requestPermissions        申请的权限
     * @param callback                  权限申请回调
     */
    default void dispatchPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
                                            @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                            @NonNull OnPermissionDescription permissionDescription,
                                            @Nullable OnPermissionCallback callback) {
        new RequestPermissionLogicPresenter(activity, requestPermissions, fragmentFactory, this, permissionDescription, callback)
            .request();
    }
}