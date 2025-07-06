package com.hjq.permissions.start;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.Iterator;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 跳转 Activity 代理类
 */
public final class StartActivityAgent {

    public static void startActivity(@NonNull Context context,
                                     @NonNull List<Intent> intentList) {
        startActivity(context, new StartActivityDelegateByContext(context), intentList);
    }

    public static void startActivity(@NonNull Activity activity,
                                     @NonNull List<Intent> intentList) {
        startActivity(activity, new StartActivityDelegateByActivity(activity), intentList);
    }

    @SuppressWarnings("deprecation")
    public static void startActivity(@NonNull Fragment fragment,
                                     @NonNull List<Intent> intentList) {
        startActivity(fragment.getActivity(), new StartActivityDelegateByFragmentApp(fragment), intentList);
    }

    public static void startActivity(@NonNull android.support.v4.app.Fragment fragment,
                                     @NonNull List<Intent> intentList) {
        startActivity(fragment.getActivity(), new StartActivityDelegateByFragmentSupport(fragment), intentList);
    }

    public static void startActivity(@NonNull Context context,
                                     @NonNull IStartActivityDelegate delegate,
                                     @NonNull List<Intent> intentList) {
        for (Intent intent : intentList) {
            if (!PermissionUtils.areActivityIntent(context, intent)) {
                // 如果这个 Intent 不存在，就继续循环，直到找到存在的 Intent 为止，否则就不进行跳转
                continue;
            }
            try {
                delegate.startActivity(intent);
                // 跳转成功，结束循环
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startActivityForResult(@NonNull Activity activity,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(activity, new StartActivityDelegateByActivity(activity), intentList, requestCode);
    }

    @SuppressWarnings("deprecation")
    public static void startActivityForResult(@NonNull Fragment fragment,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(fragment.getActivity(), new StartActivityDelegateByFragmentApp(fragment), intentList, requestCode);
    }

    public static void startActivityForResult(@NonNull android.support.v4.app.Fragment fragment,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(fragment.getActivity(), new StartActivityDelegateByFragmentSupport(fragment), intentList, requestCode);
    }

    public static void startActivityForResult(@NonNull Context context,
                                              @NonNull IStartActivityDelegate delegate,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(context, delegate, intentList, requestCode, null);
    }

    public static void startActivityForResult(@NonNull Context context,
                                              @NonNull IStartActivityDelegate delegate,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode,
                                              @Nullable Runnable ignoreActivityResultCallback) {
        Iterator<Intent> iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (!PermissionUtils.areActivityIntent(context, intent) && iterator.hasNext()) {
                // 如果这个 Intent 不存在，并且不是最后一个 Intent ，就继续循环
                // 假设当前是最后一个 Intent，但是 Intent 不存在，也会让它执行 startActivityForResult，
                // 虽然最终会失败，但是只有这样做才能让系统触发回调 onActivityResult 方法，才使得整个权限请求流程形成闭环
                continue;
            }
            try {
                delegate.startActivityForResult(intent, requestCode);
                // 跳转成功，结束循环
                break;
            } catch (Exception e) {
                e.printStackTrace();
                // 如果下一个 Intent 不为空才去触发失败结果的回调，这是因为如果下一个 Intent 为空，则证明已经没有下一个 Intent 可以再试了，
                // 那么就不需要记录这次跳转失败的次数，这样前面 startActivityForResult 失败就会导致系统触发 onActivityResult 回调，形成闭环
                if (iterator.hasNext() && ignoreActivityResultCallback != null) {
                    ignoreActivityResultCallback.run();
                }
            }
        }
    }
}