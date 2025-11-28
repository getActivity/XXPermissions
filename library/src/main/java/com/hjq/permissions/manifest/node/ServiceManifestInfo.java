package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : Service 清单信息类
 */
public final class ServiceManifestInfo {

    /** Service 的类名 */
    @NonNull
    public String name = "";

    /** 所使用到的权限 */
    @Nullable
    public String permission;

    /** 意图过滤器列表 */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /** MetaData 列表 */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}