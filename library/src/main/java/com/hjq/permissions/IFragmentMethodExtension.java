package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment 扩展接口方法
 */
public interface IFragmentMethodExtension<FM> {

    /** 获取请求权限的实现逻辑 */
    @NonNull
    RequestPermissionDelegateImpl getRequestPermissionDelegateImpl();

    /** 提交绑定 */
    void commitAttach(@Nullable FM fragmentManager);

    /** 提交解绑 */
    void commitDetach();

    /** 设置权限回调监听 */
    void setCallback(@Nullable OnPermissionFlowCallback callback);

    /** 设置权限申请标记 */
    void setRequestFlag(boolean flag);
}