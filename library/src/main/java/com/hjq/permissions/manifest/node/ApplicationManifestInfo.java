package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : Application 清单信息类
 */
public final class ApplicationManifestInfo {

    /** Application 的类名 */
    @NonNull
    public String name = "";

    /** 是否忽略分区存储特性 */
    public boolean requestLegacyExternalStorage;

    /** MetaData 列表 */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}