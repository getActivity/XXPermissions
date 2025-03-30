package com.hjq.permissions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/01/17
 *    desc   : 特殊权限申请专用的 Fragment
 */
@SuppressWarnings("deprecation")
public final class RequestSpecialPermissionFragment extends RequestBasePermissionFragment implements Runnable {

    /**
     * 开启权限申请
     */
    public static void launch(@NonNull Activity activity, @NonNull List<String> permissions,
                                @Nullable OnPermissionPageCallback callback) {
        RequestSpecialPermissionFragment fragment = new RequestSpecialPermissionFragment();
        Bundle bundle = new Bundle();
        if (permissions instanceof ArrayList) {
            bundle.putStringArrayList(REQUEST_PERMISSIONS, (ArrayList<String>) permissions);
        } else {
            bundle.putStringArrayList(REQUEST_PERMISSIONS, new ArrayList<>(permissions));
        }
        fragment.setArguments(bundle);
        // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
        fragment.setRetainInstance(true);
        // 设置权限申请标记
        fragment.setRequestFlag(true);
        // 设置权限回调监听
        fragment.setOnPermissionPageCallback(callback);
        // 绑定到 Activity 上面
        fragment.attachByActivity(activity);
    }

    /** 权限回调对象 */
    @Nullable
    private OnPermissionPageCallback mCallBack;

    /**
     * 设置权限监听回调监听
     */
    public void setOnPermissionPageCallback(@Nullable OnPermissionPageCallback callback) {
        mCallBack = callback;
    }

    @Override
    public void startPermissionRequest() {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (arguments == null || activity == null) {
            return;
        }
        List<String> permissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        PermissionActivityIntentHandler.startActivityForResult(this, PermissionApi.getSmartPermissionIntent(activity, permissions), XXPermissions.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != XXPermissions.REQUEST_CODE) {
            return;
        }

        Activity activity = getActivity();
        Bundle arguments = getArguments();
        if (activity == null || arguments == null) {
            return;
        }
        final List<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        PermissionUtils.postActivityResult(allPermissions, this);
    }

    @Override
    public void run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded()) {
            return;
        }

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        OnPermissionPageCallback callback = mCallBack;
        mCallBack = null;

        if (callback == null) {
            detachByActivity(activity);
            return;
        }

        Bundle arguments = getArguments();

        List<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        List<String> grantedPermissions = PermissionApi.getGrantedPermissions(activity, allPermissions);
        if (grantedPermissions.size() == allPermissions.size()) {
            callback.onGranted();
        } else {
            callback.onDenied();
        }

        detachByActivity(activity);
    }
}