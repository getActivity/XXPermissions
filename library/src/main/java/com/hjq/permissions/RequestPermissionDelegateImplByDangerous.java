package com.hjq.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 请求权限实现类（基于危险权限）
 */
final class RequestPermissionDelegateImplByDangerous extends RequestPermissionDelegateImpl {

    RequestPermissionDelegateImplByDangerous(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    void startPermissionRequest(@NonNull Activity activity, @NonNull List<String> permissions, int requestCode) {
        String[] permissionArray = permissions.toArray(new String[permissions.size()]);
        if (!AndroidVersionTools.isAndroid6()) {
            // 如果当前系统是 Android 6.0 以下，则没有危险权限的概念，则直接回调权限监听
            int[] grantResults = new int[permissions.size()];
            for (int i = 0; i < grantResults.length; i++) {
                // 这里解释一下，为什么不直接赋值 PackageManager.PERMISSION_GRANTED，而是选择动态判断
                // 这是因为要照顾一下 Permission.GET_INSTALLED_APPS 权限，这个权限还兼容了 miui 的 Android 6.0 以下的版本
                grantResults[i] = PermissionApi.isGrantedPermission(activity, permissions.get(i)) ?
                    PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            }
            onFragmentRequestPermissionsResult(requestCode, permissionArray, grantResults);
            return;
        }

        // 如果不需要的话就直接申请全部的危险权限
        requestPermissions(permissionArray, requestCode);
    }

    @Override
    public void onFragmentRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/236
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }

        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        // 如果回调中的请求码和请求时设置的请求码不一致，则证明回调有问题，则不往下执行代码
        if (requestCode != getPermissionRequestCode()) {
            return;
        }

        // 释放对这个请求码的占用
        PermissionRequestCodeManager.releaseRequestCode(requestCode);

        Runnable callback = getCallBack();
        // 释放监听对象的引用
        setCallback(null);

        if (callback != null) {
            callback.run();
        }

        // 将 Fragment 从 Activity 移除
        commitDetach();
    }
}