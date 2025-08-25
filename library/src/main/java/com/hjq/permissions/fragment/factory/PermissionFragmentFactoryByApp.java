package com.hjq.permissions.fragment.factory;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.fragment.impl.app.PermissionAppFragmentByRequestPermissions;
import com.hjq.permissions.fragment.impl.app.PermissionAppFragmentByStartActivityForResult;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.PermissionChannel;
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
    public void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                        @NonNull PermissionChannel permissionChannel,
                                        @Nullable OnPermissionFragmentCallback callback) {
        IFragmentMethod<Activity, FragmentManager> fragment;
        if (permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            fragment = new PermissionAppFragmentByRequestPermissions();
        } else {
            fragment = new PermissionAppFragmentByStartActivityForResult();
        }
        int maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
        int requestCode = PermissionRequestCodeManager.generateRandomRequestCode(maxRequestCode);
        fragment.setArguments(generatePermissionArguments(permissions, requestCode));
        fragment.setRetainInstance(true);
        fragment.setNonSystemRestartMark(true);
        fragment.setPermissionFragmentCallback(callback);
        fragment.commitFragmentAttach(getFragmentManager());
    }
}