package com.hjq.permissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment 原生接口方法
 */
public interface IFragmentMethodNative<A extends Activity> extends IStartActivityDelegate {

    /** 获得 Activity 对象 */
    A getActivity();

    /** 请求权限 */
    void requestPermissions(@NonNull String[] permissions, int requestCode);

    /** 获得参数集 */
    Bundle getArguments();

    /** 设置参数集 */
    void setArguments(Bundle args);

    /** 设置是否保存实例，如果设置保存，则不会因为屏幕方向或配置变化而重新创建 */
    void setRetainInstance(boolean retain);

    /** 当前 Fragment 是否已添加绑定 */
    boolean isAdded();

    /** 当前 Fragment 是否已移除 */
    boolean isRemoving();
}