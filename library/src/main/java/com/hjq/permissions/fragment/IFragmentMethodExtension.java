package com.hjq.permissions.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.core.PermissionChannelImpl;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment 扩展接口方法
 */
public interface IFragmentMethodExtension<M> {

    /**
     * 获取权限请求通道的实现逻辑
     */
    @NonNull
    PermissionChannelImpl getPermissionChannelImpl();

    /**
     * 提交 Fragment 绑定
     */
    void commitFragmentAttach(@Nullable M fragmentManager);

    /**
     * 提交 Fragment 解绑
     */
    void commitFragmentDetach();

    /**
     * 设置权限请求流程回调
     */
    void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback);

    /**
     * 设置非系统重启标记
     */
    void setNonSystemRestartMark(boolean nonSystemRestartMark);
}