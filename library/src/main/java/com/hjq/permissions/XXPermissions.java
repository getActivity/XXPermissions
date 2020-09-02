package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
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

    /** 当前是否是调试模式 */
    private volatile static Boolean sDebugMode;

    private Activity mActivity;
    private List<String> mPermissions;
    private boolean mConstant;

    /**
     * 私有化构造函数
     */
    private XXPermissions(Activity activity) {
        mActivity = activity;
    }

    /**
     * 设置请求的对象
     */
    public static XXPermissions with(Activity activity) {
        return new XXPermissions(activity);
    }
    
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
     * 被拒绝后继续申请，直到授权或者永久拒绝
     *
     * @deprecated         已过时，详情请移动至：https://github.com/getActivity/XXPermissions/issues/39
     */
    @Deprecated
    public XXPermissions constantRequest() {
        mConstant = true;
        return this;
    }

    /**
     * 请求权限
     */
    public void request(OnPermission callback) {
        if (mPermissions == null || mPermissions.isEmpty()) {
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }
        if (mActivity == null) {
            throw new IllegalArgumentException("The activity is empty");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed()){
                throw new IllegalStateException("The event has been destroyed");
            } else if (mActivity.isFinishing()) {
                throw new IllegalStateException("The event has been finish");
            }
        }
        if (callback == null) {
            throw new IllegalArgumentException("The permission request callback interface must be implemented");
        }

        if (sDebugMode == null) {
            sDebugMode = PermissionUtils.isDebugMode(mActivity);
        }

        // 优化所申请的权限列表
        PermissionUtils.optimizePermission(mPermissions);

        if (sDebugMode) {
            // 检测所申请的权限和 targetSdk 版本是否符合要求
            PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions);
        }

        if (PermissionUtils.isPermissionGranted(mActivity, mPermissions)) {
            // 证明权限已经全部授予过
            callback.hasPermission(mPermissions, true);
        } else {
            if (sDebugMode) {
                // 检测权限有没有在清单文件中注册
                PermissionUtils.checkPermissionManifest(mActivity, mPermissions);
            }
            // 申请没有授予过的权限
            PermissionFragment.newInstance((new ArrayList<>(mPermissions)), mConstant).prepareRequest(mActivity, callback);
        }
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
        context.startActivity(PermissionSettingPage.getApplicationDetailsIntent(context));
    }

    public static int startApplicationDetails(Activity activity) {
        int requestCode = PermissionUtils.getRandomRequestCode();
        activity.startActivityForResult(PermissionSettingPage.getApplicationDetailsIntent(activity), requestCode);
        return requestCode;
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param deniedPermissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, List<String> deniedPermissions) {
        try {
            context.startActivity(PermissionSettingPage.getSmartPermissionIntent(context, deniedPermissions));
        } catch (Exception ignored) {
            context.startActivity(PermissionSettingPage.getApplicationDetailsIntent(context));
        }
    }

    public static int startPermissionActivity(Activity activity, List<String> deniedPermissions) {
        int requestCode = PermissionUtils.getRandomRequestCode();
        try {
            activity.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, deniedPermissions), requestCode);
        } catch (Exception ignored) {
            activity.startActivityForResult(PermissionSettingPage.getApplicationDetailsIntent(activity), requestCode);
        }
        return requestCode;
    }
}