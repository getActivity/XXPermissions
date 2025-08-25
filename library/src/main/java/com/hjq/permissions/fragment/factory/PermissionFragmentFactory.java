package com.hjq.permissions.fragment.factory;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 生产工厂
 */
public abstract class PermissionFragmentFactory<A extends Activity, M> {

    /*
     * 这里解释一下为什么要用抽象工厂模式来创建 Fragment，而不是沿用之前版本做法，直接用 App 包下的 Fragment 来申请权限
     *
     * 问题一：直接使用 App 包下的 Fragment，调用 fragment.requestPermissions 在极少数机型可能会出现崩溃，经过多轮排查最终确定问题原因
     *        这是由于厂商修改了系统源码导致的 Bug，经过验证发现 ActivityCompat.requestPermissions（最终调用的是 activity.requestPermissions） 没问题
     *        如果来连 activity.requestPermissions 申请权限都有问题，我也就没有办法了，负责这个功能改动的厂商的开发人员和测试人员可能要被公司打包送去祭天了
     *        针对这个问题比较好的解决方案是：假设外层在 XXPermissions.with 传入的是 FragmentActivity 或者 Support 库的 Fragment 对象
     *        则换成 Support 库中的 Fragment 来申请权限，其他情况则用 App 包下的 Fragment 来申请权限，最大限度规避此类问题
     * 相关的 Github issue：
     * 1. https://github.com/getActivity/XXPermissions/issues/339
     * 2. https://github.com/getActivity/XXPermissions/issues/126
     * 3. https://github.com/getActivity/XXPermissions/issues/357
     *
     * 问题二：直接使用 App 包下的 Fragment 来获取权限回调结果，如果外层是在 Support 库 Fragment 发起的权限申请，在权限回调时
     *        假设此时 Support 库 Fragment 对象已销毁，但是仍然会触发回调给外层，这是因为不同 Fragment 对象的生命周期会不同步
     *        这样就会就会导致权限回调给状态不正常的 Fragment，如果外层没有在权限回调中先对 Fragment 状态进行判断，就往下写代码逻辑
     *        那么此时很可能会出现崩溃：java.lang.IllegalStateException: Fragment XxxFragment not attached to a context
     *        针对这个问题比较好的解决方案是：对外层传入的宿主对象进行判断，然后创建依附于宿主的 Fragment 对象，以达到生命周期绑定的效果
     *        1. 如果外层传入的宿主类型是 FragmentActivity，则创建 Support 库中的 Fragment 对象，并且绑定到 FragmentActivity 对象上面
     *        2. 如果外层传入的宿主类型是 Activity，则创建 App 包下的 Fragment 对象，并且绑定到 Activity 对象上面
     *        3. 如果外层传入的宿主类型是 Support 库的 Fragment，则创建相同类型的 Fragment 对象，并且作为子 Fragment 绑定到父 Fragment 上面
     *        4. 如果外层传入的宿主类型是 App 包的 Fragment，则创建相同类型的 Fragment 对象，并且作为子 Fragment 绑定到父 Fragment 上面
     * 相关的 Github issue：https://github.com/getActivity/XXPermissions/issues/365
     */

    @NonNull
    private final A mActivity;

    @NonNull
    private final M mFragmentManager;

    protected PermissionFragmentFactory(@NonNull A activity, @NonNull M fragmentManager) {
        mActivity = activity;
        mFragmentManager = fragmentManager;
    }

    /**
     * 获得 Activity 对象
     */
    @NonNull
    protected A getActivity() {
        return mActivity;
    }

    /**
     * 获得 FragmentManager 对象
     */
    @NonNull
    protected M getFragmentManager() {
        return mFragmentManager;
    }

    /**
     * 创建并提交 Fragment
     */
    public abstract void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                                 @NonNull PermissionChannel permissionChannel,
                                                 @Nullable OnPermissionFragmentCallback callback);

    /**
     * 生成权限请求的参数
     */
    @NonNull
    protected Bundle generatePermissionArguments(@NonNull List<IPermission> permissions, @IntRange(from = 1, to = 65535) int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(PermissionChannelImpl.REQUEST_CODE, requestCode);
        if (permissions instanceof ArrayList) {
            bundle.putParcelableArrayList(PermissionChannelImpl.REQUEST_PERMISSIONS, (ArrayList<IPermission>) permissions);
        } else {
            bundle.putParcelableArrayList(PermissionChannelImpl.REQUEST_PERMISSIONS, new ArrayList<>(permissions));
        }
        return bundle;
    }
}