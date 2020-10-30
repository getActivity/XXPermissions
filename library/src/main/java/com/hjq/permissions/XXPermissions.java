package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 危险权限请求类
 */
public final class XXPermissions {

    public static final int REQUEST_CODE = 1024;

    /** 调试模式 */
    private static Boolean sDebugMode;

    /** Activity 对象 */
    private Activity mActivity;

    /** 权限列表 */
    private List<String> mPermissions;

    /**
     * 私有化构造函数
     */
    private XXPermissions(Activity activity) {
        mActivity = activity;
    }

    public static XXPermissions with(Context context) {
        return with(PermissionUtils.getActivity(context));
    }

    /**
     * 设置请求的对象
     *
     * @param activity          当前 Activity，也可以传入栈顶的 Activity
     */
    public static XXPermissions with(Activity activity) {
        return new XXPermissions(activity);
    }

    /**
     * 设置是否为调试模式
     */
    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
    }

    /**
     * 设置权限组
     */
    public XXPermissions permission(String... permissions) {
        if (mPermissions == null) {
            mPermissions = PermissionUtils.asArrayList(permissions);
        } else {
            mPermissions.addAll(PermissionUtils.asArrayList(permissions));
        }
        return this;
    }

    /**
     * 设置权限组
     */
    public XXPermissions permission(String[]... permissions) {
        if (mPermissions == null) {
            int length = 0;
            for (String[] permission : permissions) {
                length += permission.length;
            }
            mPermissions = new ArrayList<>(length);
        }
        for (String[] group : permissions) {
            mPermissions.addAll(PermissionUtils.asArrayList(group));
        }
        return this;
    }

    /**
     * 设置权限组
     */
    public XXPermissions permission(List<String> permissions) {
        if (mPermissions == null) {
            mPermissions = permissions;
        }else {
            mPermissions.addAll(permissions);
        }
        return this;
    }

    /**
     * 请求权限
     */
    public void request(OnPermission callback) {
        // 如果传入 Activity 为空或者 Activity 状态非法则直接屏蔽这次权限申请
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return;
        }

        // 必须要传入权限或者权限组才能申请权限
        if (mPermissions == null || mPermissions.isEmpty()) {
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }

        if (sDebugMode == null) {
            sDebugMode = PermissionUtils.isDebugMode(mActivity);
        }

        // 优化所申请的权限列表
        PermissionUtils.optimizeDeprecatedPermission(mPermissions);

        if (sDebugMode) {
            // 检测所申请的权限和 targetSdk 版本是否符合要求
            PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions);
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissionManifest(mActivity, mPermissions);
        }

        if (PermissionUtils.isPermissionGranted(mActivity, mPermissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                callback.hasPermission(mPermissions, true);
            }
            return;
        }

        // 申请没有授予过的权限
        PermissionFragment.beginRequest(mActivity, new ArrayList<>(mPermissions), callback);
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean hasPermission(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return hasPermission(context, PermissionUtils.getManifestPermissions(context));
        } else {
            return hasPermission(context, PermissionUtils.asArrayList(permissions));
        }
    }

    public static boolean hasPermission(Context context, List<String> permissions) {
        return PermissionUtils.isPermissionGranted(context, permissions);
    }

    /**
     * 判断一个或多个权限组是否全部授予了
     */
    public static boolean hasPermission(Context context, String[]... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(PermissionUtils.asArrayList(group));
        }
        return PermissionUtils.isPermissionGranted(context, permissionList);
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @deprecated         已过时，请使用 {@link #startPermissionActivity(Context, List)}
     *                     或者使用 {@link #startApplicationDetails(Context)}
     */
    public static void startPermissionActivity(Context context) {
        startApplicationDetails(context);
    }

    /**
     * 跳转到应用详情页
     */
    public static void startApplicationDetails(Context context) {
        Activity activity = PermissionUtils.getActivity(context);
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

    /**
     * 跳转到应用权限设置页
     *
     * @param deniedPermissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, String... deniedPermissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(deniedPermissions));
    }

    public static void startPermissionActivity(Context context, List<String> deniedPermissions) {
        Activity activity = PermissionUtils.getActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, deniedPermissions);
            return;
        }
        try {
            Intent intent = PermissionSettingPage.getSmartPermissionIntent(context, deniedPermissions);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {
            Intent intent = PermissionSettingPage.getApplicationDetailsIntent(context);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void startPermissionActivity(Activity activity, String... deniedPermission) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(deniedPermission));
    }

    public static void startPermissionActivity(Activity activity, List<String> deniedPermissions) {
        try {
            activity.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, deniedPermissions), REQUEST_CODE);
        } catch (Exception ignored) {
            activity.startActivityForResult(PermissionSettingPage.getApplicationDetailsIntent(activity), REQUEST_CODE);
        }
    }
}