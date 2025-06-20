package com.hjq.permissions.fragment;

import android.app.Activity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment 接口方法
 */
public interface IFragmentMethod<A extends Activity, FM> extends IFragmentMethodNative<A>,
    IFragmentMethodExtension<FM> {}