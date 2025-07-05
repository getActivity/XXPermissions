package com.hjq.permissions.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.ActivityOrientationManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.start.IStartActivityDelegate;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionTaskHandler;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 请求权限实现类
 */
public abstract class RequestPermissionDelegateImpl implements IFragmentCallback {

    /** 请求的权限 */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    /** 请求码（自动生成）*/
    public static final String REQUEST_CODE = "request_code";

    /** 任务令牌 */
    @NonNull
    private final Object mTaskToken = new Object();

    /** 权限申请标记（防止系统杀死应用后重新触发请求的问题） */
    private boolean mRequestFlag;

    /** 权限请求是否已经发起 */
    private boolean mAlreadyRequest;

    /** 当前 Fragment 是否为手动解绑 */
    private boolean mManualDetach;

    /** Fragment 方法对象 */
    @NonNull
    private final IFragmentMethod<?, ?> mFragmentMethod;

    /** 权限回调对象 */
    @Nullable
    private OnPermissionFlowCallback mCallBack;

    RequestPermissionDelegateImpl(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        mFragmentMethod = fragmentMethod;
    }

    public void setRequestFlag(boolean flag) {
        mRequestFlag = flag;
    }

    public void setCallback(@Nullable OnPermissionFlowCallback callback) {
        mCallBack = callback;
    }

    @Nullable
    OnPermissionFlowCallback getCallBack() {
        return mCallBack;
    }

    @Nullable
    Activity getActivity() {
        return mFragmentMethod.getActivity();
    }

    void commitDetach() {
        mManualDetach = true;
        mFragmentMethod.commitDetach();
    }

    boolean isFragmentUnavailable() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        return !mFragmentMethod.isAdded() || mFragmentMethod.isRemoving();
    }

    void requestPermissions(@NonNull String[] permissions, @IntRange(from = 1, to = 65535) int requestCode) {
        try {
            mFragmentMethod.requestPermissions(permissions, requestCode);
        } catch (Exception e) {
            // 在某些极端情况下，调用系统的 requestPermissions 方法时会出现崩溃，刚开始我还以为是 Android 6.0 以下的设备触发的 Bug，
            // 结果发现 Android 6.0 及以上也有这个问题，你永远无法想象现实到底有多魔幻，经过分析得出结论，出现这种情况只有两种可能：
            // 1. 厂商开发工程师修改了 com.android.packageinstaller 系统应用的包名，但是没有自测好就上线了（概率较小）
            // 2. 用户有 Root 权限，在精简系统 App 的时候不小心删掉了 com.android.packageinstaller 这个系统应用（概率较大）
            // 经过分析 Activity.requestPermissions 的源码，它本质上还是调用 startActivityForResult，只不过 Activity 找不到了而已，
            // 目前能想到最好的解决方式，就是用 try catch 避免它出现崩溃，看到这里你可能会有一个疑问，就简单粗暴 try catch？你确定没问题？
            // 会不会导致 onRequestPermissionsResult 没有回调？从而导致权限请求流程卡住的情况？虽然这个问题没有办法测试，但理论上是不会的，
            // 因为我用了错误的 Intent 进行 startActivityForResult 并进行 try catch 做实验，结果 onActivityResult 还是有被系统正常回调，
            // 证明对 startActivityForResult 进行 try catch 并不会影响 onActivityResult 的回调，我还分析了 Activity 回调方面的源码实现，
            // 发现无论是 onRequestPermissionsResult 还是 onActivityResult，回调它们的都是 dispatchActivityResult 方法，
            // 在那种极端情况下，既然 onActivityResult 能被回调，那么就证明 dispatchActivityResult 肯定有被系统正常调用的，
            // 同理 onRequestPermissionsResult 也肯定会被 dispatchActivityResult 正常调用，从而形成一个完整的逻辑闭环。
            // 如果真的出现这种极端情况，所有危险权限的申请必然会走失败的回调，但是框架要做的是：尽量让应用不要崩溃，并且能走完整个权限申请的流程。
            // 涉及到此问题相关 Github issue 地址：
            // 1. https://github.com/getActivity/XXPermissions/issues/153
            // 2. https://github.com/getActivity/XXPermissions/issues/126
            // 3. https://github.com/getActivity/XXPermissions/issues/327
            // 4. https://github.com/getActivity/XXPermissions/issues/339
            // android.content.ActivityNotFoundException: No Activity found to handle Intent
            // { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.android.packageinstaller (has extras) }
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    List<IPermission> getPermissionRequestList() {
        Bundle arguments = mFragmentMethod.getArguments();
        if (arguments == null) {
            return null;
        }
        if (AndroidVersion.isAndroid13()) {
            return arguments.getParcelableArrayList(REQUEST_PERMISSIONS, IPermission.class);
        } else {
            return arguments.getParcelableArrayList(REQUEST_PERMISSIONS);
        }
    }

    int getPermissionRequestCode() {
        Bundle arguments = mFragmentMethod.getArguments();
        if (arguments == null) {
            return 0;
        }
        return arguments.getInt(REQUEST_CODE);
    }

    void sendTask(@NonNull Runnable runnable, long delayMillis) {
        PermissionTaskHandler.sendTask(runnable, mTaskToken, delayMillis);
    }

    void cancelTask() {
        PermissionTaskHandler.cancelTask(mTaskToken);
    }

    IStartActivityDelegate getStartActivityDelegate() {
        return mFragmentMethod;
    }

    /**
     * 开启权限请求
     */
    abstract void startPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> permissions,
                                         @IntRange(from = 1, to = 65535) int requestCode);

    @Override
    public void onFragmentResume() {
        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            mFragmentMethod.commitDetach();
            return;
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mAlreadyRequest) {
            return;
        }

        mAlreadyRequest = true;
        Activity activity = getActivity();
        // 检查 Activity 是不是不可用
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        final int requestCode = getPermissionRequestCode();
        if (requestCode <= 0) {
            return;
        }
        List<IPermission> permissions = getPermissionRequestList();
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        startPermissionRequest(activity, permissions, requestCode);
        OnPermissionFlowCallback callback = getCallBack();
        if (callback == null) {
            return;
        }
        callback.onRequestPermissionNow();
    }

    @Override
    public void onFragmentDestroy() {
        // 取消执行任务
        cancelTask();
        OnPermissionFlowCallback callBack = getCallBack();
        // 如果回调还没有置空，则证明前面没有回调权限回调完成
        if (callBack != null) {
            // 告诉外层本次权限回调有异常
            callBack.onRequestPermissionAnomaly();
            // 释放回调对象，避免内存泄漏
            setCallback(null);
        }
        if (mManualDetach) {
            return;
        }
        Activity activity = getActivity();
        // 检查 Activity 是不是不可用
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // 如果不是手动解绑绑定，则证明是系统解除绑定，这里需要恢复 Activity 屏幕方向
        // 如果是手动解除绑定，则会在所有的权限都申请完了之后恢复 Activity 屏幕方向
        ActivityOrientationManager.unlockActivityOrientation(activity);
    }

    /**
     * 通知权限回调
     */
    protected void notificationPermissionCallback() {
        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // 延迟处理权限请求的结果
        sendTask(this::handlerPermissionCallback, PermissionApi.getMaxWaitTimeByPermissions(activity, getPermissionRequestList()));
    }

    /**
     * 处理权限回调
     */
    protected void handlerPermissionCallback() {
        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        OnPermissionFlowCallback callback = getCallBack();
        // 释放监听对象的引用
        setCallback(null);

        if (callback != null) {
            callback.onRequestPermissionFinish();
        }

        // 将 Fragment 从 Activity 移除
        commitDetach();
    }
}