package com.hjq.permissions.start;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.tools.PermissionSettingPage;
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
        Iterator<Intent> iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (PermissionUtils.areActivityIntent(context, intent)) {
                continue;
            }
            // 移除那些不存在的 Intent 对象，这样做的好处是：
            // 1. 拿不存在的 Intent 去跳转必定是失败的（如果项目适配了 Android 11，需要注意适配软件包可见性的特性）
            // 2. 在 Debug 代码调试的时候，可以很直观看出来有哪些 Intent 是存在的，也可以比较过滤前后的 Intent 列表
            iterator.remove();
        }

        // 当所有的 Intent 都不存在的时候，那么就默认添加一个 Android 系统设置的 Intent，这样写的原因如下：
        // 不至于用户一点申请权限就立马提示失败，用户会一头雾水，这样的体验太差了，最起码跳转一下 Android 系统设置页，这样效果会好很多
        if (intentList.isEmpty()) {
            intentList.add(PermissionSettingPage.getAndroidSettingsIntent());
        }

        // 由于 Iterator 接口中没有重置索引的方法，所以这里只能重新获取一次 Iterator 对象
        iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (intent == null) {
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
            if (PermissionUtils.areActivityIntent(context, intent)) {
                continue;
            }
            // 移除那些不存在的 Intent 对象，这样做的好处是：
            // 1. 拿不存在的 Intent 去跳转必定是失败的（如果项目适配了 Android 11，需要注意适配软件包可见性的特性）
            // 2. 在 Debug 代码调试的时候，可以很直观看出来有哪些 Intent 是存在的，也可以比较过滤前后的 Intent 列表
            iterator.remove();
        }

        // 当所有的 Intent 都不存在的时候，那么就默认添加一个 Android 系统设置的 Intent，这样写的原因如下：
        // 1. 不至于用户一点申请权限就立马提示失败，用户会一头雾水，这样的体验太差了，最起码跳转一下 Android 系统设置页，这样效果会好很多
        // 2. 假设连 Android 系统设置页都跳转失败了，但是这样做可以让系统触发回调 onActivityResult 方法，才使得整个权限请求流程形成闭环
        if (intentList.isEmpty()) {
            intentList.add(PermissionSettingPage.getAndroidSettingsIntent());
        }

        // 由于 Iterator 接口中没有重置索引的方法，所以这里只能重新获取一次 Iterator 对象
        iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (intent == null) {
                continue;
            }
            try {
                delegate.startActivityForResult(intent, requestCode);
                // 跳转成功，结束循环
                break;
            } catch (Exception e) {
                // android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.settings.APPLICATION_DETAILS_SETTINGS dat=package:xxx.xxx.xxx }
                // java.lang.SecurityException: Permission Denial: starting Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) cmp=xxxx/.xxx }
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