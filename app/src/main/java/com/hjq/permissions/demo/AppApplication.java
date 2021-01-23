package com.hjq.permissions.demo;

import android.app.Application;

import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastWhiteStyle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 应用入口
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化吐司工具类
        ToastUtils.init(this, new ToastWhiteStyle(getApplicationContext()));

        // 设置权限申请拦截器
        XXPermissions.setPermissionInterceptor(new PermissionInterceptor());
    }
}