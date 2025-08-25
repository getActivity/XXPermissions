package com.hjq.permissions.start;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : {@link android.support.v4.app.Fragment} 跳转 Activity 实现
 */
public final class StartActivityDelegateByFragmentSupport implements IStartActivityDelegate {

    @NonNull
    private final Fragment mFragment;

    public StartActivityDelegateByFragmentSupport(@NonNull Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void startActivity(@NonNull Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        mFragment.startActivityForResult(intent, requestCode);
    }
}