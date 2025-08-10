package com.hjq.permissions.manifest.node;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : Activity 清单信息类
 */
public final class ActivityManifestInfo {

    /**
     * 活动的类名
     */
    @NonNull
    public String name = "";

    /**
     * 窗口是否支持画中画
     */
    public boolean supportsPictureInPicture;

    /**
     * 意图过滤器列表
     */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;
}