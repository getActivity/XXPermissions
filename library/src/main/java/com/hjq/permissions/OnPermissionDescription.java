package com.hjq.permissions;

import android.app.Activity;
import android.support.annotation.NonNull;
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
     * @param activity                      Activity 对象
     * @param requestPermissions            请求的权限
     * @param continueRequestRunnable       继续请求任务对象
     * @param breakRequestRunnable          中断请求任务对象
     */
    default void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<String> requestPermissions,
                                            @NonNull Runnable continueRequestRunnable,
                                            @NonNull Runnable breakRequestRunnable) {
        continueRequestRunnable.run();
    }

    /**
     * 权限请求开始
     *
     * @param activity                      Activity 对象
     * @param requestPermissions            请求的权限
     */
    void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<String> requestPermissions);

    /**
     * 权限请求结束
     *
     * @param activity                      Activity 对象
     * @param requestPermissions            请求的权限
     */
    void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<String> requestPermissions);
}