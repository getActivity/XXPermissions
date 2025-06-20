package com.hjq.permissions.fragment.factory;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.core.OnPermissionFlowCallback;
import com.hjq.permissions.fragment.impl.app.PermissionFragmentAppByDangerous;
import com.hjq.permissions.fragment.impl.app.PermissionFragmentAppBySpecial;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.PermissionType;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 生产工厂（App 包下的 Fragment）
 */
@SuppressWarnings("deprecation")
public final class PermissionFragmentFactoryByApp extends PermissionFragmentFactory<Activity, FragmentManager> {

    public PermissionFragmentFactoryByApp(@NonNull Activity activity, @NonNull FragmentManager fragmentManager) {
        super(activity, fragmentManager);
    }

    @Override
    public void createAndCommitFragment(@NonNull List<IPermission> permissions, @NonNull PermissionType permissionType, @Nullable OnPermissionFlowCallback callback) {
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