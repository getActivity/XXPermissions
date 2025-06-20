package com.hjq.permissions.delegate;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : startActivity 委托 App 包下的 Fragment 实现
 */
@SuppressWarnings("deprecation")
public final class StartActivityDelegateByFragmentApp implements IStartActivityDelegate {

    @NonNull
    private final Fragment mFragment;

    public StartActivityDelegateByFragmentApp(@NonNull Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent == null) {
            return;
        }
        mFragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        if (intent == null) {
            return;
        }
        mFragment.startActivityForResult(intent, requestCode);
    }
}