package com.hjq.permissions.permission;

import android.content.Intent;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/08/24
 *    desc   : 权限请求通道
 */
public enum PermissionChannel {

    /** {@link android.app.Activity#requestPermissions(String[], int)} */
    REQUEST_PERMISSIONS,
    /** {@link android.app.Activity#startActivityForResult(Intent, int)} */
    START_ACTIVITY_FOR_RESULT
}