package com.hjq.permissions;

import android.content.Intent;
import android.support.annotation.Nullable;

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
    default void onFragmentRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {}

    /** Fragment onActivityResult 方法回调 */
    default void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {}
}