package com.hjq.permissions;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 生产工厂（App 包下的 Fragment）
 */
@SuppressWarnings("deprecation")
final class PermissionFragmentFactoryByApp extends PermissionFragmentFactory<Activity, FragmentManager> {

    PermissionFragmentFactoryByApp(@NonNull Activity activity, @NonNull FragmentManager fragmentManager) {
        super(activity, fragmentManager);
    }

    @Override
    void createAndCommitFragment(@NonNull List<String> permissions, @NonNull PermissionType permissionType, @Nullable OnPermissionFlowCallback callback) {
        IFragmentMethod<Activity, FragmentManager> fragment;
        if (permissionType == PermissionType.SPECIAL) {
            fragment = new PermissionFragmentAppBySpecial();
        } else {
            fragment = new PermissionFragmentAppByDangerous();
        }
        int maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
        int requestCode = PermissionRequestCodeManager.generateRandomRequestCode(maxRequestCode);
        fragment.setArguments(generatePermissionArguments(permissions, requestCode));
        fragment.setRetainInstance(true);
        fragment.setRequestFlag(true);
        fragment.setCallback(callback);
        fragment.commitAttach(getFragmentManager());
    }
}