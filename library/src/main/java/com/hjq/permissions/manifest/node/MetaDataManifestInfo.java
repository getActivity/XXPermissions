package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/08/10
 *    desc   : MetaData 清单信息类
 */
public final class MetaDataManifestInfo {

    /** MetaData 的名称 */
    @NonNull
    public String name = "";

    /** MetaData 的值 */
    @Nullable
    public String value;

    /** MetaData 的资源 ID */
    public int resource;
}