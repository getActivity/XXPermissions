package com.hjq.permissions.demo;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/15
 *    desc   : 设备管理器广播实现类
 */
public class DemoDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        // Toaster.show("设备管理器：可用");
        log("设备管理器：可用");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        // Toaster.show("设备管理器：不可用");
        log("设备管理器：不可用");
    }

    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        return "这是一个可选的消息，警告有关禁止用户的请求";
    }

    @Override
    public void onPasswordChanged(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        super.onPasswordChanged(context, intent, user);
        // Toaster.show("设备管理器：密码己经改变");
        log("设备管理器：密码己经改变");
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordFailed(context, intent);
        // Toaster.show("设备管理器：改变密码失败");
        log("设备管理器：改变密码失败");
    }

    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        super.onPasswordSucceeded(context, intent, user);
        // Toaster.show("设备管理器：改变密码成功");
        log("设备管理器：改变密码成功");
    }

    private void log(@NonNull String message) {
        Log.i("XXPermissions", message);
    }
}