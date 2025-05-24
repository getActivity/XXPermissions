package com.hjq.permissions;

import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 类（Support 包下 Fragment + 危险权限）
 */
public final class PermissionFragmentSupportByDangerous extends PermissionFragmentSupport {

    @NonNull
    private final RequestPermissionDelegateImpl mRequestPermissionDelegateImpl = new RequestPermissionDelegateImplByDangerous(this);

    /** @noinspection ClassEscapesDefinedScope*/
    @NonNull
    @Override
    public RequestPermissionDelegateImpl getRequestPermissionDelegateImpl() {
        return mRequestPermissionDelegateImpl;
    }
}