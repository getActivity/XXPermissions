package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限说明接口
 */
public interface OnPermissionDescription {

    /**
     * 询问是否要发起权限请求
     *
     * @param requestList                   请求的权限列表
     * @param continueRequestRunnable       继续请求任务对象
     * @param breakRequestRunnable          中断请求任务对象
     */
    void askWhetherRequestPermission(@NonNull Activity activity,
                                     @NonNull List<IPermission> requestList,
                                     @NonNull Runnable continueRequestRunnable,
                                     @NonNull Runnable breakRequestRunnable);

    /**
     * 权限请求开始
     *
     * @param requestList                   请求的权限列表
     */
    void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList);

    /**
     * 权限请求结束
     *
     * @param requestList                   请求的权限列表
     */
    void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList);
}