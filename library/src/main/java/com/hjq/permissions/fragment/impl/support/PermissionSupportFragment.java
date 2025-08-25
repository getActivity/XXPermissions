package com.hjq.permissions.fragment.impl.support;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.core.OnPermissionFragmentCallback;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（ {@link android.support.v4.app.Fragment} ）
 */
public abstract class PermissionSupportFragment extends Fragment implements IFragmentMethod<FragmentActivity, FragmentManager> {

    /**
     * 设置回调对象
     */
    @Override
    public void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback) {
        getPermissionChannelImpl().setPermissionFragmentCallback(callback);
    }

    /**
     * 设置非系统重启标记
     */
    @Override
    public void setNonSystemRestartMark(boolean nonSystemRestartMark) {
        getPermissionChannelImpl().setNonSystemRestartMark(nonSystemRestartMark);
    }

    /**
     * 提交 Fragment 绑定
     */
    @Override
    public void commitFragmentAttach(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 提交 Fragment 解绑
     */
    @Override
    public void commitFragmentDetach() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPermissionChannelImpl().onFragmentResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPermissionChannelImpl().onFragmentDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPermissionChannelImpl().onFragmentRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPermissionChannelImpl().onFragmentActivityResult(requestCode, resultCode, data);
    }
}