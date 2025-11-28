package com.hjq.permissions.fragment.impl.androidx;

import androidx.annotation.NonNull;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.core.PermissionChannelImplByRequestPermissions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（ {@link androidx.fragment.app.Fragment} + {@link android.app.Activity#requestPermissions(String[], int)} ）
 */
public final class PermissionAndroidXFragmentByRequestPermissions extends PermissionAndroidXFragment {

    @NonNull
    private final PermissionChannelImpl mPermissionChannelImpl = new PermissionChannelImplByRequestPermissions(this);

    @NonNull
    @Override
    public PermissionChannelImpl getPermissionChannelImpl() {
        return mPermissionChannelImpl;
    }
}