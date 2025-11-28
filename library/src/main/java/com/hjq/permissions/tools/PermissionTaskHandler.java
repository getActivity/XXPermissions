package com.hjq.permissions.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/26
 *    desc   : 权限任务处理类
 */
public final class PermissionTaskHandler {

    /** Handler 对象 */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 延迟发送一个任务
     */
    public static void sendTask(@NonNull Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 延迟发送一个指定令牌的任务
     */
    public static void sendTask(@NonNull Runnable runnable, @NonNull Object token, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        long uptimeMillis = SystemClock.uptimeMillis() + delayMillis;
        HANDLER.postAtTime(runnable, token, uptimeMillis);
    }

    /**
     * 取消一个指定的令牌任务
     */
    public static void cancelTask(@NonNull Object token) {
        // 移除和当前对象相关的消息回调
        HANDLER.removeCallbacksAndMessages(token);
    }
}