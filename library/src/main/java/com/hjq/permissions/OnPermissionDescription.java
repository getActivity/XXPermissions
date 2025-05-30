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
     * @param confirmRequestRunnable        确认按钮
     * @param cancelRequestRunnable
     */
    default void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<String> requestPermissions,
                                            @NonNull Runnable confirmRequestRunnable,
                                            @NonNull Runnable cancelRequestRunnable) {
        confirmRequestRunnable.run();
    }

    default void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<String> requestPermissions) {}

    default void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<String> requestPermissions) {}
}