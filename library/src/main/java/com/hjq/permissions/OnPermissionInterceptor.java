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
     * @param requestList               请求的权限列表
     * @param fragmentFactory           权限 Fragment 工厂类
     * @param permissionDescription     权限描述器
     * @param callback                  权限申请回调
     */
    default void onRequestPermissionStart(@NonNull Activity activity,
                                          @NonNull List<IPermission> requestList,
                                          @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                          @NonNull OnPermissionDescription permissionDescription,
                                          @Nullable OnPermissionCallback callback) {
        dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback);
    }

    /**
     * 权限请求完成
     *
     * @param grantedList               授予权限列表
     * @param deniedList                拒绝权限列表
     * @param skipRequest               是否跳过了申请过程
     * @param callback                  权限申请回调
     */
    default void onRequestPermissionEnd(@NonNull Activity activity, boolean skipRequest,
                                        @NonNull List<IPermission> requestList,
                                        @NonNull List<IPermission> grantedList,
                                        @NonNull List<IPermission> deniedList,
                                        @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onResult(grantedList, deniedList);
    }

    /**
     * 派发权限请求
     *
     * @param requestList               请求的权限列表
     * @param callback                  权限申请回调
     */
    default void dispatchPermissionRequest(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                           @NonNull OnPermissionDescription permissionDescription,
                                           @Nullable OnPermissionCallback callback) {
        new RequestPermissionLogicPresenter(activity, requestList, fragmentFactory, this, permissionDescription, callback)
            .request();
    }
}