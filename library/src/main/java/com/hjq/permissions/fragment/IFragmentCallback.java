package com.hjq.permissions.fragment;

import android.content.Intent;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment 回调接口
 */
public interface IFragmentCallback {

    /** Fragment 可见时回调 */
    void onFragmentResume();

    /** Fragment 解绑时回调 */
    void onFragmentDestroy();

    /** Fragment onRequestPermissionsResult 方法回调 */
    default void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // default implementation ignored
    }

    /** Fragment onActivityResult 方法回调 */
    default void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // default implementation ignored
    }
}