package com.hjq.permissions;

import android.content.Intent;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : startActivity 委托接口
 */
interface IStartActivityDelegate {

    /** 跳转 Activity */
    void startActivity(Intent intent);

    /** 跳转 Activity（需要返回结果） */
    void startActivityForResult(Intent intent, int requestCode);
}