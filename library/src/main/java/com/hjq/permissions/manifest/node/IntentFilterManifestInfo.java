package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/15
 *    desc   : 意图过滤器
 */
public final class IntentFilterManifestInfo {

    /** 动作列表 */
    @NonNull
    public final List<String> actionList = new ArrayList<>();

    /** 分类列表 */
    @NonNull
    public final List<String> categoryList = new ArrayList<>();
}