package com.hjq.permissions;

import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（App 包下 Fragment + 特殊权限）
 */
public final class PermissionFragmentAppBySpecial extends PermissionFragmentApp {

    @NonNull
    private final RequestPermissionDelegateImpl mRequestPermissionDelegateImpl = new RequestPermissionDelegateImplBySpecial(this);

    /** @noinspection ClassEscapesDefinedScope*/
    @NonNull
    @Override
    public RequestPermissionDelegateImpl getRequestPermissionDelegateImpl() {
        return mRequestPermissionDelegateImpl;
    }
}