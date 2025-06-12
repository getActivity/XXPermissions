package com.hjq.permissions.demo;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/01/22
 *    desc   : 通知消息监控服务
 */
public final class NotificationMonitorService extends NotificationListenerService {

    /**
     * 当系统收到新的通知后出发回调
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 需要注释掉回调 super.onNotificationPosted 的调用，测试在原生 Android 4.3 版本会触发崩溃
        // 但是测试在原生 Android 5.0 的版本却没有这个问题，证明这个是一个历史遗留问题
        // java.lang.AbstractMethodError: abstract method not implemented
        // super.onNotificationPosted(sbn);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Bundle extras = sbn.getNotification().extras;
        if (extras == null) {
            return;
        }

        //获取通知消息标题
        String title = extras.getString(Notification.EXTRA_TITLE);
        // 获取通知消息内容
        Object msgText = extras.getCharSequence(Notification.EXTRA_TEXT);
        // Toaster.show(String.format(getString(R.string.demo_notification_listener_toast), title, msgText));
        // 这里选择打 Log，而不是弹 Toast，是为了避免影响 Demo 工程的使用体验
        Log.i("XXPermissions", String.format(getString(R.string.demo_notification_listener_toast), title, msgText));
    }

    /**
     * 当系统通知被删掉后出发回调
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 需要注释掉回调 super.onNotificationRemoved 的调用，测试在原生 Android 4.3 版本会触发崩溃
        // 但是测试在原生 Android 5.0 的版本却没有这个问题，证明这个是一个历史遗留问题
        // java.lang.AbstractMethodError: abstract method not implemented
        // super.onNotificationRemoved(sbn);
    }
}