package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（App 包下 Fragment）
 */
@SuppressWarnings("deprecation")
public abstract class PermissionFragmentApp extends Fragment implements IFragmentMethod<Activity, FragmentManager> {

    /**
     * 设置回调对象
     */
    public void setCallback(@Nullable Runnable callback) {
        getRequestPermissionDelegateImpl().setCallback(callback);
    }

    /**
     * 设置请求 Flag
     */
    public void setRequestFlag(boolean flag) {
        getRequestPermissionDelegateImpl().setRequestFlag(flag);
    }

    /**
     * 提交绑定
     */
    public void commitAttach(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 提交解绑
     */
    public void commitDetach() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getRequestPermissionDelegateImpl().onFragmentResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getRequestPermissionDelegateImpl().onFragmentDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getRequestPermissionDelegateImpl().onFragmentRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getRequestPermissionDelegateImpl().onFragmentActivityResult(requestCode, resultCode, data);
    }
}