package com.hjq.permissions.demo;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 窗口生命周期管理
 */
public final class WindowLifecycleManager {

    /**
     * 将 Activity 和 Dialog 的生命周期绑定在一起
     */
    public static void bindDialogLifecycle(@NonNull Activity activity, @NonNull Dialog dialog) {
        WindowLifecycleCallbacks windowLifecycleCallbacks = new WindowLifecycleCallbacks(activity) {

            @Override
            public void onWindowDismiss() {
                if (!dialog.isShowing()) {
                    return;
                }
                dialog.dismiss();
            }
        };
        registerWindowLifecycleCallbacks(activity, windowLifecycleCallbacks);
    }

    /**
     * 将 Activity 和 PopupWindow 的生命周期绑定在一起
     */
    public static void bindPopupWindowLifecycle(@NonNull Activity activity, @NonNull PopupWindow popupWindow) {
        WindowLifecycleCallbacks windowLifecycleCallbacks = new WindowLifecycleCallbacks(activity) {

            @Override
            public void onWindowDismiss() {
                if (!popupWindow.isShowing()) {
                    return;
                }
                popupWindow.dismiss();
            }
        };
        registerWindowLifecycleCallbacks(activity, windowLifecycleCallbacks);
    }

    /**
     * 注册窗口回调
     */
    private static void registerWindowLifecycleCallbacks(@NonNull Activity activity, @NonNull WindowLifecycleCallbacks callbacks) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.registerActivityLifecycleCallbacks(callbacks);
        } else {
            activity.getApplication().registerActivityLifecycleCallbacks(callbacks);
        }
    }

    /**
     * 反注册窗口回调
     */
    private static void unregisterWindowLifecycleCallbacks(@NonNull Activity activity, @NonNull WindowLifecycleCallbacks callbacks) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.unregisterActivityLifecycleCallbacks(callbacks);
        } else {
            activity.getApplication().unregisterActivityLifecycleCallbacks(callbacks);
        }
    }

    /**
     * 窗口生命周期回调
     */
    private abstract static class WindowLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Nullable
        private Activity mActivity;

        private WindowLifecycleCallbacks(@NonNull Activity activity) {
            mActivity = activity;
        }

        public abstract void onWindowDismiss();

        @Override
        public final void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            // default implementation ignored
        }

        @Override
        public final void onActivityStarted(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public final void onActivityResumed(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public final void onActivityPaused(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public final void onActivityStopped(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public final void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            // default implementation ignored
        }

        @Override
        public final void onActivityDestroyed(@NonNull Activity activity) {
            if (activity != mActivity) {
                return;
            }
            // 释放 Activity 对象
            mActivity = null;
            // 反注册窗口监听
            unregisterWindowLifecycleCallbacks(activity, this);
            // 通知外层销毁窗口
            onWindowDismiss();
        }
    }
}