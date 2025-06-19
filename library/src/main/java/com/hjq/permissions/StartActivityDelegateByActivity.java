package com.hjq.permissions;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : startActivity 委托 Activity 实现
 */
final class StartActivityDelegateByActivity implements IStartActivityDelegate {

    @NonNull
    private final Activity mActivity;

    StartActivityDelegateByActivity(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent == null) {
            return;
        }
        mActivity.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        if (intent == null) {
            return;
        }
        mActivity.startActivityForResult(intent, requestCode);
    }
}