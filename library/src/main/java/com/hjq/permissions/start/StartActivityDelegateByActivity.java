package com.hjq.permissions.start;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : {@link android.app.Activity} 跳转 Activity 实现
 */
public final class StartActivityDelegateByActivity implements IStartActivityDelegate {

    @NonNull
    private final Activity mActivity;

    public StartActivityDelegateByActivity(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public void startActivity(@NonNull Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }
}