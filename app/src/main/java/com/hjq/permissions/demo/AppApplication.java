package com.hjq.permissions.demo;

import android.app.Application;

import com.hjq.toast.Toaster;
import com.hjq.toast.style.WhiteToastStyle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 应用入口
 */
public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化吐司工具类
        Toaster.init(this, new WhiteToastStyle());
    }
}