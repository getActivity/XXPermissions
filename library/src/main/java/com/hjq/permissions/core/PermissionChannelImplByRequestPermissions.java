package com.hjq.permissions.core;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 请求权限实现类（通过  {@link android.app.Activity#requestPermissions(String[], int)} 实现）
 */
public final class PermissionChannelImplByRequestPermissions extends PermissionChannelImpl {

    public PermissionChannelImplByRequestPermissions(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    protected void startPermissionRequest(@NonNull Activity activity,
                                          @NonNull List<IPermission> permissions,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        if (!PermissionVersion.isAndroid6()) {
            // 如果当前系统是 Android 6.0 以下，则没有危险权限的概念，则直接回调权限监听
            sendTask(this::handlerPermissionCallback, 0);
            return;
        }

        // 如果不需要的话就直接申请全部的危险权限
        requestPermissions(PermissionUtils.convertPermissionArray(activity, permissions), requestCode);
        // 记录一下已申请过的权限（用于更加精准地判断用户是否勾选了《不再询问》）
        AlreadyRequestPermissionsManager.addAlreadyRequestPermissions(permissions);
    }

    @Override
    public void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // 如果回调中的请求码和请求时设置的请求码不一致，则证明回调有问题，则不往下执行代码
        if (requestCode != getPermissionRequestCode()) {
            return;
        }
        // 释放对这个请求码的占用
        PermissionRequestCodeManager.releaseRequestCode(requestCode);
        // 通知权限请求回调
        notificationPermissionCallback();
    }
}