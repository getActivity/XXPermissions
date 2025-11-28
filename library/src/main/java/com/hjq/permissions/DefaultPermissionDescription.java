package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/01
 *    desc   : 权限说明默认实现
 */
final class DefaultPermissionDescription implements OnPermissionDescription {

    @Override
    public void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull Runnable continueRequestRunnable,
                                            @NonNull Runnable breakRequestRunnable) {
        // 继续执行请求任务
        continueRequestRunnable.run();
    }

    @Override
    public void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
    }

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
    }
}