package com.hjq.permissions;

import android.app.Activity;
import android.support.annotation.IntRange;
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
    void startPermissionRequest(@NonNull Activity activity, @NonNull List<String> permissions,
                                @IntRange(from = 1, to = 65535) int requestCode) {
        if (!AndroidVersionTools.isAndroid6()) {
            // 如果当前系统是 Android 6.0 以下，则没有危险权限的概念，则直接回调权限监听
            // 有人看到这句代码，忍不住想吐槽了，你这不是太阳能手电筒，纯纯脱裤子放屁
            // 实则不然，也有例外的情况，GET_INSTALLED_APPS 权限虽然是危险权限
            // 但是框架在 miui 上面兼容到了 Android 6.0 以下，但是由于无法调用 requestPermissions
            // 只能通过跳转 Activity 授予该权限，所以只能告诉外层权限请求失败，迫使外层跳转 Activity 来授权
            sendTask(this::dispatchPermissionCallback, 0);
            return;
        }

        // 如果不需要的话就直接申请全部的危险权限
        requestPermissions(PermissionUtils.toStringArray(permissions), requestCode);
    }

    @Override
    public void onFragmentRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 如果回调中的请求码和请求时设置的请求码不一致，则证明回调有问题，则不往下执行代码
        if (requestCode != getPermissionRequestCode()) {
            return;
        }

        // 释放对这个请求码的占用
        PermissionRequestCodeManager.releaseRequestCode(requestCode);

        // 延迟处理权限请求的结果
        sendTask(this::dispatchPermissionCallback,
            PermissionHelper.getMaxWaitTimeByPermissions(PermissionUtils.asArrayList(permissions)));
    }

    private void dispatchPermissionCallback() {
        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        OnPermissionFlowCallback callback = getCallBack();
        // 释放监听对象的引用
        setCallback(null);

        if (callback != null) {
            callback.onRequestPermissionFinish();
        }

        // 将 Fragment 从 Activity 移除
        commitDetach();
    }
}