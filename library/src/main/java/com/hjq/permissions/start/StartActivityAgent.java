package com.hjq.permissions.start;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 跳转 Activity 代理类
 */
public final class StartActivityAgent {

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
            Intent subIntent = IntentNestedHandler.findSubIntentBySuperIntent(intent);
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
        return startActivityForResult(delegate, intent, requestCode, null);
    }

    public static boolean startActivityForResult(@NonNull IStartActivityDelegate delegate, @NonNull Intent intent,
                                                 @IntRange(from = 1, to = 65535) int requestCode,
                                                 @Nullable Runnable ignoreActivityResultCallback) {
        try {
            delegate.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Intent subIntent = IntentNestedHandler.findSubIntentBySuperIntent(intent);
            if (subIntent == null) {
                return false;
            }
            // 如果 subIntent 不为空才去触发失败的回调，这是因为如果 subIntent 为空，则证明已经没有下一个 Intent 可以再试了，
            // 那么就不需要记录这次跳转失败的次数，这样前面 startActivityForResult 失败就会导致系统触发 onActivityResult 回调，形成闭环
            if (ignoreActivityResultCallback != null) {
                ignoreActivityResultCallback.run();
            }
            return startActivityForResult(delegate, subIntent, requestCode);
        }
    }
}