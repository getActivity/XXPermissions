package com.hjq.permissions.start;

import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : {@link androidx.fragment.app.Fragment} 跳转 Activity 实现
 */
public final class StartActivityDelegateByFragmentAndroidX implements IStartActivityDelegate {

    @NonNull
    private final Fragment mFragment;

    public StartActivityDelegateByFragmentAndroidX(@NonNull Fragment fragment) {
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