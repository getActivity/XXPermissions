package com.hjq.permissions.tools;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.delegate.IStartActivityDelegate;
import com.hjq.permissions.delegate.StartActivityDelegateByActivity;
import com.hjq.permissions.delegate.StartActivityDelegateByContext;
import com.hjq.permissions.delegate.StartActivityDelegateByFragmentApp;
import com.hjq.permissions.delegate.StartActivityDelegateByFragmentSupport;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 权限设置页处理
 */
public final class PermissionSettingPageHandler {

    /** 存取子意图所用的 Intent Key */
    private static final String SUB_INTENT_KEY = "sub_intent_key";

    /**
     * 从父意图中获取子意图
     *
     * @param superIntent           父意图对象
     */
    private static Intent findSubIntentBySuperIntent(@NonNull Intent superIntent) {
        Intent subIntent;
        if (AndroidVersionTools.isAndroid13()) {
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
    private static Intent findDeepIntent(@NonNull Intent intent) {
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

    public static boolean startActivity(@NonNull Context context, Intent intent) {
        return startActivity(new StartActivityDelegateByContext(context), intent);
    }

    public static boolean startActivity(@NonNull Activity activity, Intent intent) {
        return startActivity(new StartActivityDelegateByActivity(activity), intent);
    }

    @SuppressWarnings("deprecation")
    public static boolean startActivity(@NonNull Fragment fragment, Intent intent) {
        return startActivity(new StartActivityDelegateByFragmentApp(fragment), intent);
    }

    public static boolean startActivity(@NonNull android.support.v4.app.Fragment fragment, Intent intent) {
        return startActivity(new StartActivityDelegateByFragmentSupport(fragment), intent);
    }

    public static boolean startActivity(@NonNull IStartActivityDelegate delegate, @NonNull Intent intent) {
        try {
            delegate.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Intent subIntent = findSubIntentBySuperIntent(intent);
            if (subIntent == null) {
                return false;
            }
            return startActivity(delegate, subIntent);
        }
    }

    public static boolean startActivityForResult(@NonNull Activity activity, @NonNull Intent intent,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        return startActivityForResult(new StartActivityDelegateByActivity(activity), intent, requestCode);
    }

    @SuppressWarnings("deprecation")
    public static boolean startActivityForResult(@NonNull Fragment fragment, @NonNull Intent intent,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        return startActivityForResult(new StartActivityDelegateByFragmentApp(fragment), intent, requestCode);
    }

    public static boolean startActivityForResult(@NonNull android.support.v4.app.Fragment fragment, @NonNull Intent intent,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        return startActivityForResult(new StartActivityDelegateByFragmentSupport(fragment), intent, requestCode);
    }

    public static boolean startActivityForResult(@NonNull IStartActivityDelegate delegate, @NonNull Intent intent,
                                                 @IntRange(from = 1, to = 65535) int requestCode) {
        try {
            delegate.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Intent subIntent = findSubIntentBySuperIntent(intent);
            if (subIntent == null) {
                return false;
            }
            return startActivityForResult(delegate, subIntent, requestCode);
        }
    }
}