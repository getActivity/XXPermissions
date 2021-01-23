package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 危险权限请求类
 */
public final class XXPermissions {

    /** 权限设置页跳转请求码 */
    public static final int REQUEST_CODE = 1024 + 1;

    /** 权限请求拦截器 */
    private static IPermissionInterceptor sPermissionInterceptor;

    /** 调试模式 */
    private static Boolean sDebugMode;

    /**
     * 设置请求的对象
     *
     * @param activity          当前 Activity，也可以传入栈顶的 Activity
     */
    public static XXPermissions with(FragmentActivity activity) {
        return new XXPermissions(activity);
    }

    public static XXPermissions with(Context context) {
        return with(PermissionUtils.findFragmentActivity(context));
    }

    public static XXPermissions with(Fragment fragment) {
        return with(fragment.getActivity());
    }

    /**
     * 设置是否为调试模式
     */
    public static void setDebugMode(Boolean debugMode) {
        sDebugMode = debugMode;
    }

    /**
     * 当前是否为调试模式
     */
    private static boolean isDebugMode(Context context) {
        if (sDebugMode == null) {
            sDebugMode = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return sDebugMode;
    }

    /**
     * 设置权限请求拦截器
     */
    public static void setPermissionInterceptor(IPermissionInterceptor interceptor) {
        sPermissionInterceptor = interceptor;
    }

    /**
     * 获取权限请求拦截器
     */
    static IPermissionInterceptor getPermissionInterceptor() {
        if (sPermissionInterceptor == null) {
            sPermissionInterceptor = new IPermissionInterceptor() {};
        }
        return sPermissionInterceptor;
    }

    /** Activity 对象 */
    private final FragmentActivity mActivity;

    /** 权限列表 */
    private List<String> mPermissions;

    /**
     * 私有化构造函数
     */
    private XXPermissions(FragmentActivity activity) {
        mActivity = activity;
    }

    /**
     * 添加权限
     */
    public XXPermissions permission(String permission) {
        if (mPermissions == null) {
            mPermissions = new ArrayList<>(1);
        }
        mPermissions.add(permission);
        return this;
    }

    /**
     * 添加权限组
     */
    public XXPermissions permission(String[] permissions) {
        return permission(PermissionUtils.asArrayList(permissions));
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
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if (mActivity == null || mActivity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return;
        }

        // 必须要传入权限或者权限组才能申请权限
        if (mPermissions == null || mPermissions.isEmpty()) {
            if (isDebugMode(mActivity)) {
                throw new IllegalArgumentException("The requested permission cannot be empty");
            }
            return;
        }

        if (isDebugMode(mActivity)) {
            // 检查申请的存储权限是否符合规范
            PermissionUtils.checkStoragePermission(mActivity, mPermissions);
            // 检查申请的定位权限是否符合规范
            PermissionUtils.checkLocationPermission(mPermissions);
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions);
        }

        // 优化所申请的权限列表
        PermissionUtils.optimizeDeprecatedPermission(mPermissions);

        if (isDebugMode(mActivity)) {
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissionManifest(mActivity, mPermissions);
        }

        if (PermissionUtils.isGrantedPermissions(mActivity, mPermissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                callback.onGranted(mPermissions, true);
            }
            return;
        }

        // 申请没有授予过的权限
        getPermissionInterceptor().requestPermissions(mActivity, callback, mPermissions);
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean isGrantedPermission(Context context, String permission) {
        return PermissionUtils.isGrantedPermission(context, permission);
    }

    public static boolean isGrantedPermission(Context context, String[] permissions) {
        return isGrantedPermission(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGrantedPermission(Context context, List<String> permissions) {
        return PermissionUtils.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDeniedPermissions(Context context, String[] permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDeniedPermissions(Context context, List<String> permissions) {
        return PermissionUtils.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断一个或多个权限是否被永久拒绝了（注意不能在请求权限之前调用，应该在 {@link OnPermissionCallback#onDenied(List, boolean)} 方法中调用）
     */
    public static boolean isPermissionPermanentDenied(Activity activity, String permission) {
        return PermissionUtils.isPermissionPermanentDenied(activity, permission);
    }

    public static boolean isPermissionPermanentDenied(Activity activity, String[] permissions) {
        return isPermissionPermanentDenied(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isPermissionPermanentDenied(Activity activity, List<String> permissions) {
        return PermissionUtils.isPermissionPermanentDenied(activity, permissions);
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    public static boolean isSpecialPermission(String permission) {
        return PermissionUtils.isSpecialPermission(permission);
    }

    /**
     * 跳转到应用详情页
     */
    public static void startApplicationDetails(Context context) {
        Activity activity = PermissionUtils.findFragmentActivity(context);
        if (activity != null) {
            startApplicationDetails(activity);
            return;
        }
        Intent intent = PermissionSettingPage.getApplicationDetailsIntent(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startApplicationDetails(Activity activity) {
        activity.startActivityForResult(PermissionSettingPage.getApplicationDetailsIntent(activity), REQUEST_CODE);
    }

    public static void startApplicationDetails(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getApplicationDetailsIntent(activity), REQUEST_CODE);
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permission           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, String permission) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permission));
    }

    public static void startPermissionActivity(Context context, String[] permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Context context, List<String> permissions) {
        Activity activity = PermissionUtils.findFragmentActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionSettingPage.getSmartPermissionIntent(context, permissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startPermissionActivity(Activity activity, String permission) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permission));
    }

    public static void startPermissionActivity(Activity activity, String[] permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Activity activity, List<String> permissions) {
        activity.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), REQUEST_CODE);
    }

    public static void startPermissionActivity(Fragment fragment, String permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, String[] permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, List<String> permissions) {
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), REQUEST_CODE);
    }
}