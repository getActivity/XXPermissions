package com.hjq.permissions.fragment.impl.androidx;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.core.PermissionChannelImplByStartActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（ {@link androidx.fragment.app.Fragment} + {@link android.app.Activity#startActivityForResult(Intent, int)} ）
 */
public final class PermissionAndroidXFragmentByStartActivity extends PermissionAndroidXFragment {

    @NonNull
    private final PermissionChannelImpl mPermissionChannelImpl = new PermissionChannelImplByStartActivity(this);

    @NonNull
    @Override
    public PermissionChannelImpl getPermissionChannelImpl() {
        return mPermissionChannelImpl;
    }
}