package com.hjq.permissions.start;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 意图嵌套处理类
 */
public final class IntentNestedHandler {

    /** 存取子意图所用的 Intent Key */
    private static final String SUB_INTENT_KEY = "sub_intent_key";

    /**
     * 从父意图中获取子意图
     *
     * @param superIntent           父意图对象
     */
    @SuppressWarnings("deprecation")
    static Intent findSubIntentBySuperIntent(@NonNull Intent superIntent) {
        Intent subIntent;
        if (AndroidVersion.isAndroid13()) {
            subIntent = superIntent.getParcelableExtra(SUB_INTENT_KEY, Intent.class);
        } else {
            subIntent = superIntent.getParcelableExtra(SUB_INTENT_KEY);
        }
        return subIntent;
    }

    /**
     * 获取意图中最深层的子意图
     *
     * @param intent                意图对象
     */
    static Intent findDeepIntent(@NonNull Intent intent) {
        Intent subIntent = findSubIntentBySuperIntent(intent);
        if (subIntent != null) {
            return findDeepIntent(subIntent);
        }
        return intent;
    }

    /**
     * 将子意图添加到主意图中
     *
     * @param mainIntent            主意图对象
     * @param subIntent             子意图对象
     */
    public static Intent addSubIntentForMainIntent(@Nullable Intent mainIntent, @Nullable Intent subIntent) {
        if (mainIntent == null && subIntent != null) {
            return subIntent;
        }
        if (subIntent == null) {
            return mainIntent;
        }
        Intent deepSubIntent = findDeepIntent(mainIntent);
        deepSubIntent.putExtra(SUB_INTENT_KEY, subIntent);
        return mainIntent;
    }

    /**
     * 合并多个 Intent 意图
     */
    public static Intent mergeMultipleIntent(@NonNull Context context, @NonNull List<Intent> intentList) {
        Intent mainIntent = null;
        for (Intent intent : intentList) {
            // 这个意图必须存在，才纳入到跳转的范围，否则就不考虑
            if (!PermissionUtils.areActivityIntent(context, intent)) {
                continue;
            }
            mainIntent = addSubIntentForMainIntent(mainIntent, intent);
        }
        if (mainIntent == null) {
            return PermissionSettingPage.getCommonPermissionSettingIntent(context);
        }
        return mainIntent;
    }
}