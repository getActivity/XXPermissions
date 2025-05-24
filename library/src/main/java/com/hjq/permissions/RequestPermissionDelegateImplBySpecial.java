package com.hjq.permissions;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 请求权限实现类（基于特殊权限）
 */
final class RequestPermissionDelegateImplBySpecial extends RequestPermissionDelegateImpl {

    RequestPermissionDelegateImplBySpecial(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    void startPermissionRequest(@NonNull Activity activity, @NonNull List<String> permissions, int requestCode) {
        PermissionActivityIntentHandler.startActivityForResult(getStartActivityDelegate(),
                    PermissionApi.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onFragmentActivityResult(requestCode, resultCode, data);

        // 如果回调中的请求码和请求时设置的请求码不一致，则证明回调有问题，则不往下执行代码
        if (requestCode != getPermissionRequestCode()) {
            return;
        }

        // 释放对这个请求码的占用
        PermissionRequestCodeManager.releaseRequestCode(requestCode);

        final List<String> permissions = getPermissionRequestList();
        if (permissions == null || permissions.isEmpty()) {
            return;
        }

        PermissionUtils.postActivityResult(permissions, this::dispatchPermissionCallback);
    }

    private void dispatchPermissionCallback() {
        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        Runnable callback = getCallBack();
        // 释放监听对象的引用
        setCallback(null);

        if (callback != null) {
            callback.run();
        }

        commitDetach();
    }
}