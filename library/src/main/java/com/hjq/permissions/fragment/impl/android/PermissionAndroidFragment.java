package com.hjq.permissions.fragment.impl.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（ {@link android.app.Fragment} ）
 */
@SuppressWarnings("deprecation")
public abstract class PermissionAndroidFragment extends Fragment implements IFragmentMethod<Activity, FragmentManager> {

    @Override
    public void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback) {
        getPermissionChannelImpl().setPermissionFragmentCallback(callback);
    }

    @Override
    public void setNonSystemRestartMark(boolean nonSystemRestartMark) {
        getPermissionChannelImpl().setNonSystemRestartMark(nonSystemRestartMark);
    }

    @Override
    public void commitFragmentAttach(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

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