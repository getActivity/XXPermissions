package com.hjq.permissions.demo;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;

import com.hjq.toast.Toaster;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/01/22
 *    desc   : 通知消息监控服务
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class NotificationMonitorService extends NotificationListenerService {

    /**
     * 当系统收到新的通知后出发回调
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
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
        Toaster.show(String.format(getString(R.string.demo_notification_listener_toast), title, msgText));
    }

    /**
     * 当系统通知被删掉后出发回调
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}