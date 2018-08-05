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
 * Created by HJQ on 2018-6-15.
 */
public final class XXPermissions {

    private Activity mActivity;
    private List<String> mPermissions;
    private boolean mConstant;

    /**
     * 不能被外部实例化
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
    public XXPermissions permission(String... permissions){
        mPermissions = Arrays.asList(permissions);
        return this;
    }

    /**
     * 设置权限组
     */
    public XXPermissions permission(String[]... permissions){
        mPermissions = new ArrayList<>();
        for (String[] group : permissions) {
            mPermissions.addAll(Arrays.asList(group));
        }
        return this;
    }

    /**
     * 设置权限组
     */
    public XXPermissions permission(List<String> permissions){
        mPermissions = permissions;
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
    public void request(OnPermission call) {
        //如果没有指定请求的权限，就使用清单注册的权限进行请求
        if (mPermissions == null || mPermissions.size() == 0) mPermissions = PermissionUtils.getManifestPermissions(mActivity);
        if (mPermissions == null || mPermissions.size() == 0) throw new IllegalArgumentException("The requested permission cannot be empty");
        //使用isFinishing方法Activity在熄屏状态下会导致崩溃
        //if (mActivity == null || mActivity.isFinishing()) throw new IllegalArgumentException("Illegal Activity was passed in");
        if (mActivity == null) throw new IllegalArgumentException("The activity is empty");
        if (call == null) throw new IllegalArgumentException("The permission request callback interface must be implemented");

        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(mActivity, mPermissions);

        if (failPermissions == null) {
            //证明权限已经全部授予过
            call.hasPermission(mPermissions, true);
        } else {
            //检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissions(mActivity, mPermissions);
            //申请没有授予过的权限
            PermissionFragment.newInstant((new ArrayList<>(mPermissions)), mConstant).prepareRequest(mActivity, call);
        }
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param context           上下文对象
     * @param permissions       需要请求的权限组
     */
    public static boolean isHasPermission(Context context, String... permissions) {
        return PermissionUtils.getFailPermissions(context, Arrays.asList(permissions)) == null;
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param context           上下文对象
     * @param permissions       需要请求的权限组
     */
    public static boolean isHasPermission(Context context, String[]... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(Arrays.asList(group));
        }
        return PermissionUtils.getFailPermissions(context, permissionList) == null;
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context           上下文对象
     */
    public static void gotoPermissionSettings(Context context) {
        PermissionSettingPage.start(context, false);
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context           上下文对象
     * @param newTask           是否使用新的任务栈启动
     */
    public static void gotoPermissionSettings(Context context, boolean newTask) {
        PermissionSettingPage.start(context, newTask);
    }
}