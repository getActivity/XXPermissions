package com.hjq.permissions.tools;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.ResolveInfoFlags;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限相关工具类
 */
public final class PermissionUtils {

    /**
     * 当前是否处于 debug 模式
     */
    public static boolean isDebugMode(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 将数组转换成 ArrayList
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    @SuppressWarnings("all")
    @NonNull
    public static <T> ArrayList<T> asArrayList(@Nullable T... array) {
        int initialCapacity = 0;
        if (array != null) {
            initialCapacity = array.length;
        }
        ArrayList<T> list = new ArrayList<>(initialCapacity);
        if (array == null || array.length == 0) {
            return list;
        }
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    @Nullable
    public static Activity findActivity(@Nullable Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                // android.content.ContextWrapper
                // android.content.MutableContextWrapper
                // androidx.appcompat.view.ContextThemeWrapper
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /**
     * 判断 Activity 是不是不可用
     */
    public static boolean isActivityUnavailable(@Nullable Activity activity) {
        return activity == null || activity.isDestroyed()  || activity.isFinishing();
    }

    /**
     * 判断 Fragment 是不是不可用（AndroidX 库的版本）
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable androidx.fragment.app.Fragment xFragment) {
        return xFragment == null || !xFragment.isAdded() || xFragment.isRemoving();
    }

    /**
     * 判断 Fragment 是不是不可用（系统包的版本）
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable Fragment fragment) {
        return fragment == null || !fragment.isAdded() || fragment.isRemoving();
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean areActivityIntent(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null) {
            return false;
        }
        // 这里为什么不用 Intent.resolveActivity(intent) != null 来判断呢？
        // 这是因为在 OPPO R7 Plus （Android 5.0）会出现误判，明明没有这个 Activity，却返回了 ComponentName 对象
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        if (PermissionVersion.isAndroid13()) {
            return !packageManager.queryIntentActivities(intent,
                    ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)).isEmpty();
        }
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 比较字符串是否相等（从第一个字符串开始比较）
     */
    public static boolean equalsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较字符串是否相等（从最后一个字符串开始比较）
     */
    public static boolean reverseEqualsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = length - 1; i >= 0; i--) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两个权限是否为同一个
     */
    public static boolean equalsPermission(@NonNull String permission1, @NonNull String permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1, permission2);
    }

    /**
     * 判断两个权限是否为同一个
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1.getPermissionName(), permission2);
    }

    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull IPermission permission2) {
        // 因为权限字符串大多数都是以 android.permission 开头
        // 所以从最后一个字符开始判断，可以大大提升 equals 的判断效率
        return reverseEqualsString(permission1.getPermissionName(), permission2.getPermissionName());
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull IPermission permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(permission, item.getPermissionName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull List<String> permissions, @NonNull String permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (String item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(permission, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull String permissionName) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // 使用 equalsPermission 来判断可以提升代码执行效率
            if (equalsPermission(item.getPermissionName(), permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将 IPermission[] 转换成 List<String> 对象
     */
    @NonNull
    public static List<String> convertPermissionList(@Nullable IPermission[] permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null) {
            return list;
        }
        for (IPermission permission : permissions) {
            list.add(permission.getPermissionName());
        }
        return list;
    }

    /**
     * 将 List<IPermission> 转换成 String[] 对象
     */
    @NonNull
    public static String[] convertPermissionArray(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new String[0];
        }
        String[] list = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            list[i] = permissions.get(i).getPermissionName();
        }
        return list;
    }

    /**
     * 获取包名 uri
     */
    public static Uri getPackageNameUri(@NonNull Context context) {
        return Uri.parse("package:" + context.getPackageName());
    }

    /**
     * 判断某个类的类名是否存在
     */
    public static boolean isClassExist(@Nullable String className) {
        if (className == null) {
            return false;
        }
        if (className.isEmpty()) {
            return false;
        }
        try {
            // 判断这个类有是否存在，如果存在的话，证明是有效的
            // 如果不存在的话，证明无效的，也是需要重新授权的
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较两个 Intent 列表的内容是否一致
     */
    public static boolean equalsIntentList(@NonNull List<Intent> intentList1, @NonNull List<Intent> intentList2) {
        if (intentList1.size() != intentList2.size() ) {
            return false;
        }

        for (int i = 0; i < intentList1.size(); i++) {
            if (!intentList1.get(i).filterEquals(intentList2.get(i))) {
                return false;
            }
        }
        return true;
    }
}