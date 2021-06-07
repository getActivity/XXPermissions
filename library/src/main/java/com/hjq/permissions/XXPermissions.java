package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 危险权限请求类
 */
@SuppressWarnings("deprecation, unused")
public final class XXPermissions {

    /** 权限设置页跳转请求码 */
    public static final int REQUEST_CODE = 1024 + 1;

    /** 权限请求拦截器 */
    private static IPermissionInterceptor sPermissionInterceptor;

    /** 调试模式 */
    private static Boolean sDebugMode;

    /** 分区存储 */
    private static boolean sScopedStorage;

    /**
     * 设置请求的对象
     *
     * @param context          当前 Activity，可以传入栈顶的 Activity
     */
    public static XXPermissions with(Context context) {
        return new XXPermissions(context);
    }

    public static XXPermissions with(Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static XXPermissions with(android.support.v4.app.Fragment fragment) {
        return with(fragment.getActivity());
    }

    /**
     * 是否为调试模式
     */
    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
    }

    private static boolean isDebugMode(Context context) {
        if (sDebugMode == null) {
            sDebugMode = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return sDebugMode;
    }

    /**
     * 是否已经适配了 Android 10 分区存储特性
     */
    public static void setScopedStorage(boolean scopedStorage) {
        sScopedStorage = scopedStorage;
    }

    private static boolean isScopedStorage() {
        return sScopedStorage;
    }

    /**
     * 设置权限请求拦截器
     */
    public static void setInterceptor(IPermissionInterceptor interceptor) {
        sPermissionInterceptor = interceptor;
    }

    /**
     * 获取权限请求拦截器
     */
    static IPermissionInterceptor getInterceptor() {
        if (sPermissionInterceptor == null) {
            sPermissionInterceptor = new IPermissionInterceptor() {};
        }
        return sPermissionInterceptor;
    }

    /** Context 对象 */
    private final Context mContext;

    /** 权限列表 */
    private List<String> mPermissions;

    /**
     * 私有化构造函数
     */
    private XXPermissions(Context context) {
        mContext = context;
    }

    /**
     * 添加权限组
     */
    public XXPermissions permission(String... permissions) {
        return permission(PermissionUtils.asArrayList(permissions));
    }

    public XXPermissions permission(String[]... permissions) {
        return permission(PermissionUtils.asArrayLists(permissions));
    }

    public XXPermissions permission(List<String> permissions) {
        if (mPermissions == null) {
            mPermissions = permissions;
        } else {
            mPermissions.addAll(permissions);
        }
        return this;
    }

    /**
     * 请求权限
     */
    public void request(OnPermissionCallback callback) {
        if (mContext == null) {
            return;
        }

        // 当前是否为调试模式
        boolean debugMode = isDebugMode(mContext);

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        Activity activity = PermissionUtils.findActivity(mContext);
        if (!PermissionChecker.checkActivityStatus(activity, debugMode)) {
            return;
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionChecker.checkPermissionArgument(mPermissions, debugMode)) {
            return;
        }

        if (debugMode) {
            // 检查申请的存储权限是否符合规范
            PermissionChecker.checkStoragePermission(mContext, mPermissions, isScopedStorage());
            // 检查申请的定位权限是否符合规范
            PermissionChecker.checkLocationPermission(mPermissions);
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionChecker.checkTargetSdkVersion(mContext, mPermissions);
        }

        // 优化所申请的权限列表
        PermissionChecker.optimizeDeprecatedPermission(mPermissions);

        if (debugMode) {
            // 检测权限有没有在清单文件中注册
            PermissionChecker.checkPermissionManifest(mContext, mPermissions);
        }

        if (PermissionUtils.isGrantedPermissions(mContext, mPermissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                callback.onGranted(mPermissions, true);
            }
            return;
        }

        // 申请没有授予过的权限
        getInterceptor().requestPermissions(activity, callback, mPermissions);
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean isGranted(Context context, String... permissions) {
        return isGranted(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGranted(Context context, String[]... permissions) {
        return isGranted(context, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isGranted(Context context, List<String> permissions) {
        return PermissionUtils.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDenied(Context context, String... permissions) {
        return getDenied(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDenied(Context context, String[]... permissions) {
        return getDenied(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getDenied(Context context, List<String> permissions) {
        return PermissionUtils.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    public static boolean isSpecial(String permission) {
        return PermissionUtils.isSpecialPermission(permission);
    }

    /**
     * 判断一个或多个权限是否被永久拒绝了（注意不能在请求权限之前调用，应该在 {@link OnPermissionCallback#onDenied(List, boolean)} 方法中调用）
     */
    public static boolean isPermanentDenied(Activity activity, String... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, String[]... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, List<String> permissions) {
        return PermissionUtils.isPermissionPermanentDenied(activity, permissions);
    }

    /* android.content.Context */

    public static void startPermissionActivity(Context context) {
        startPermissionActivity(context, (List<String>) null);
    }

    public static void startPermissionActivity(Context context, String... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Context context, String[]... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, List<String> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionSettingPage.getSmartPermissionIntent(context, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /* android.app.Activity */

    public static void startPermissionActivity(Activity activity) {
        startPermissionActivity(activity, (List<String>) null);
    }

    public static void startPermissionActivity(Activity activity, String... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Activity activity, String[]... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Activity activity, List<String> permissions) {
        activity.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), REQUEST_CODE);
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Fragment fragment, List<String> permissions) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), REQUEST_CODE);
    }

    /* android.support.v4.app.Fragment */

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, List<String> permissions) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), REQUEST_CODE);
    }
}