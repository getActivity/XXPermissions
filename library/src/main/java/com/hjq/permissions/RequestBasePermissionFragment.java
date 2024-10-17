package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/09/29
 *    desc   : 权限申请专用的 Fragment 基类
 */
public abstract class RequestBasePermissionFragment extends Fragment  {

    /** 请求的权限 */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    /** 权限申请标记（防止系统杀死应用后重新触发请求的问题） */
    private boolean mRequestFlag;

    /** 权限请求是否已经发起 */
    private boolean mAlreadyRequest;

    /** Activity 屏幕方向 */
    private int mScreenOrientation;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        mScreenOrientation = activity.getRequestedOrientation();
        if (mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }

        // 锁定当前 Activity 方向
        PermissionUtils.lockActivityOrientation(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ||
            activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        // 为什么这里不用跟上面一样 try catch ？因为这里是把 Activity 方向取消固定，只有设置横屏或竖屏的时候才可能触发 crash
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachByActivity(getActivity());
            return;
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mAlreadyRequest) {
            return;
        }

        mAlreadyRequest = true;
        startPermissionRequest();
    }

    /**
     * 开启权限请求
     */
    public abstract void startPermissionRequest();

    /**
     * 设置权限申请标记
     */
    public void setRequestFlag(boolean flag) {
        mRequestFlag = flag;
    }

    /**
     * 绑定 Activity
     */
    public void attachByActivity(@NonNull Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 解绑 Activity
     */
    public void detachByActivity(@NonNull Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }
}