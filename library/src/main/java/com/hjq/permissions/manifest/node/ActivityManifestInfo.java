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

    /** Activity 的类名 */
    @NonNull
    public String name = "";

    /** 是否支持画中画 */
    public boolean supportsPictureInPicture = false;

    /** 意图过滤器列表 */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /** MetaData 列表 */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}