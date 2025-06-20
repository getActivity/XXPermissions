package com.hjq.permissions.delegate;

import android.content.Intent;
import android.support.annotation.IntRange;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : startActivity 委托接口
 */
public interface IStartActivityDelegate {

    /** 跳转 Activity */
    void startActivity(Intent intent);

    /** 跳转 Activity（需要返回结果） */
    void startActivityForResult(Intent intent, @IntRange(from = 1, to = 65535) int requestCode);
}