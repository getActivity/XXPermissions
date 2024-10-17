package com.hjq.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/09/29
 *    desc   : 危险权限申请专用的 Fragment
 */
@SuppressWarnings("deprecation")
public final class RequestDangerousPermissionFragment extends RequestBasePermissionFragment {

    /** 请求码（自动生成）*/
    private static final String REQUEST_CODE = "request_code";

    /** 权限请求码存放集合 */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /**
     * 开启权限申请
     */
    public static void launch(@NonNull Activity activity, @NonNull List<String> permissions,
                                @Nullable OnRequestPermissionsResultCallback callback) {
        RequestDangerousPermissionFragment fragment = new RequestDangerousPermissionFragment();
        int requestCode;
        Random random = new Random();
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // 新版本的 Support 库限制请求码必须小于 65536
            // 旧版本的 Support 库限制请求码必须小于 256
            requestCode = random.nextInt((int) Math.pow(2, 8));
        } while (REQUEST_CODE_ARRAY.contains(requestCode));
        // 标记这个请求码已经被占用
        REQUEST_CODE_ARRAY.add(requestCode);

        Bundle bundle = new Bundle();
        bundle.putInt(REQUEST_CODE, requestCode);
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
        fragment.setOnRequestPermissionsResultCallback(callback);
        // 绑定到 Activity 上面
        fragment.attachByActivity(activity);
    }

    /** 权限回调对象 */
    @Nullable
    private OnRequestPermissionsResultCallback mCallBack;

    /**
     * 设置权限监听回调监听
     */
    public void setOnRequestPermissionsResultCallback(@Nullable OnRequestPermissionsResultCallback callback) {
        mCallBack = callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消引用监听器，避免内存泄漏
        mCallBack = null;
    }

    /**
     * 开始权限请求
     */
    @Override
    public void startPermissionRequest() {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (arguments == null || activity == null) {
            return;
        }

        List<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        final int requestCode = arguments.getInt(REQUEST_CODE);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        // 请求危险权限
        requestAllDangerousPermission(activity, requestCode, allPermissions);
    }

    /**
     * 申请所有危险权限
     */
    private void requestAllDangerousPermission(@NonNull Activity activity, int requestCode,
                                                @NonNull List<String> allPermissions) {
        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念，则直接回调监听
            int[] grantResults = new int[allPermissions.size()];
            for (int i = 0; i < grantResults.length; i++) {
                grantResults[i] = PermissionApi.isGrantedPermission(activity, allPermissions.get(i)) ?
                    PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            }
            onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[allPermissions.size()]), grantResults);
            return;
        }

        // 如果不需要的话就直接申请全部的危险权限
        requestPermissions(allPermissions.toArray(new String[allPermissions.size()]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (activity == null || arguments == null ||
            requestCode != arguments.getInt(REQUEST_CODE)) {
            return;
        }

        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/236
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }

        OnRequestPermissionsResultCallback callback = mCallBack;
        // 释放监听对象的引用
        mCallBack = null;

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);

        if (callback != null) {
            callback.onRequestPermissionsResult(permissions, grantResults);
        }

        // 将 Fragment 从 Activity 移除
        detachByActivity(activity);
    }
}