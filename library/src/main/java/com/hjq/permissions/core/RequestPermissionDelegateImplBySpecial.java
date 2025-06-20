package com.hjq.permissions.core;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionSettingPageHandler;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 请求权限实现类（基于特殊权限）
 */
public final class RequestPermissionDelegateImplBySpecial extends RequestPermissionDelegateImpl {

    public RequestPermissionDelegateImplBySpecial(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    void startPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> permissions,
                                @IntRange(from = 1, to = 65535) int requestCode) {
        PermissionSettingPageHandler.startActivityForResult(getStartActivityDelegate(),
                    PermissionApi.getBestPermissionSettingIntent(activity, permissions), requestCode);
    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        notificationPermissionCallback(requestCode);
    }
}