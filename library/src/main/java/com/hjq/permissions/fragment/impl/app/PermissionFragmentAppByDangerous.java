package com.hjq.permissions.fragment.impl.app;

import android.support.annotation.NonNull;
import com.hjq.permissions.core.RequestPermissionDelegateImpl;
import com.hjq.permissions.core.RequestPermissionDelegateImplByDangerous;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（App 包下 Fragment + 危险权限）
 */
public final class PermissionFragmentAppByDangerous extends PermissionFragmentApp {

    @NonNull
    private final RequestPermissionDelegateImpl mRequestPermissionDelegateImpl = new RequestPermissionDelegateImplByDangerous(this);

    @NonNull
    @Override
    public RequestPermissionDelegateImpl getRequestPermissionDelegateImpl() {
        return mRequestPermissionDelegateImpl;
    }
}