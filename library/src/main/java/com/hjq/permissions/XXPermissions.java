package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 危险权限请求类
 */
public final class XXPermissions {

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

    /**
     * 设置权限组
     */
    public XXPermissions permission(String... permissions) {
        if (mPermissions == null) {
            mPermissions = new ArrayList<>(permissions.length);
        }
        mPermissions.addAll(Arrays.asList(permissions));
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
            mPermissions.addAll(Arrays.asList(group));
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
     */
    public XXPermissions constantRequest() {
        mConstant = true;
        return this;
    }

    /**
     * 请求权限
     */
    public void request(OnPermission callback) {
        // 如果没有指定请求的权限，就使用清单注册的权限进行请求
        if (mPermissions == null || mPermissions.isEmpty()) {
            mPermissions = PermissionUtils.getManifestPermissions(mActivity);
        }
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

        // 如果本次申请包含了 Android 11 存储权限，但是当前版本不是 Android 11 及以上版本
        if (mPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.isAndroid11()) {
            // 自动添加旧版的存储权限，因为旧版的系统不支持申请新版的存储权限
            mPermissions.add(Permission.READ_EXTERNAL_STORAGE);
            mPermissions.add(Permission.WRITE_EXTERNAL_STORAGE);
        }

        // 检测申请的权限和 targetSdk 版本是否符合要求
        PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions);

        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(mActivity, mPermissions);

        if (failPermissions == null || failPermissions.isEmpty()) {
            // 证明权限已经全部授予过
            callback.hasPermission(mPermissions, true);
        } else {
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissions(mActivity, mPermissions);
            // 申请没有授予过的权限
            PermissionFragment.newInstance((new ArrayList<>(mPermissions)), mConstant).prepareRequest(mActivity, callback);
        }
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param permissions 需要请求的权限组
     */
    public static boolean hasPermission(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return hasPermission(context, PermissionUtils.getManifestPermissions(context));
        } else {
            return hasPermission(context, Arrays.asList(permissions));
        }
    }

    public static boolean hasPermission(Context context, List<String> permissions) {
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, permissions);
        return failPermissions == null || failPermissions.isEmpty();
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param permissions 需要请求的权限组
     */
    public static boolean hasPermission(Context context, String[]... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(Arrays.asList(group));
        }
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, permissionList);
        return failPermissions == null || failPermissions.isEmpty();
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
     * 跳转到应用权限设置页
     *
     * @param failPermissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, List<String> failPermissions) {
        // 如果失败的权限里面包含了特殊权限，那么就直接跳转到应用详情页，否则就直接跳转到权限设置页
        if (failPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) ||
                failPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES) ||
                failPermissions.contains(Permission.SYSTEM_ALERT_WINDOW) ||
                failPermissions.contains(Permission.NOTIFICATION_SERVICE) ||
                failPermissions.contains(Permission.WRITE_SETTINGS)) {
            // 如果当前只有一个权限被拒绝了
            if (failPermissions.size() == 1) {
                String permission = failPermissions.get(0);
                if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    // 跳转到存储权限设置界面
                    context.startActivity(PermissionSettingPage.getStoragePermissionIntent(context));
                } else if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                    // 跳转到安装权限设置界面
                    context.startActivity(PermissionSettingPage.getInstallPermissionIntent(context));
                } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                    // 跳转到悬浮窗设置页面
                    context.startActivity(PermissionSettingPage.getWindowPermissionIntent(context));
                } else if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                    // 跳转到通知栏权限设置页面
                    context.startActivity(PermissionSettingPage.getNotifyPermissionIntent(context));
                } else if (Permission.WRITE_SETTINGS.equals(permission)) {
                    // 跳转到系统设置权限设置页面
                    context.startActivity(PermissionSettingPage.getSettingPermissionIntent(context));
                }
            } else {
                // 跳转到应用详情界面
                startApplicationDetails(context);
            }
        } else {
            // 跳转到具体的权限设置界面
            Intent intent = ManagePermissionPage.getIntent(context);

            if (intent == null) {
                intent = PermissionSettingPage.getApplicationDetailsIntent(context);
            }

            try {
                context.startActivity(intent);
            } catch (Exception ignored) {
                if (!Settings.ACTION_APPLICATION_DETAILS_SETTINGS.equals(intent.getAction())) {
                    intent = PermissionSettingPage.getApplicationDetailsIntent(context);
                    context.startActivity(intent);
                }
            }
        }
    }

    /**
     * 跳转到应用详情页
     */
    public static void startApplicationDetails(Context context) {
        context.startActivity(PermissionSettingPage.getApplicationDetailsIntent(context));
    }
}