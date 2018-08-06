package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HJQ on 2018-6-15.
 */
final class PermissionUtils {

    /**
     * 是否是6.0以上版本
     */
    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否有安装权限
     */
    public static boolean isHasInstallPermission(Context context) {
        if (isOverOreo()) {

            //必须设置目标SDK为26及以上才能正常检测安装权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.O) {
                throw new RuntimeException("The targetSdkVersion SDK must be 26 or more");
            }

            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    public static boolean isHasOverlaysPermission(Context context) {

        if (isOverMarshmallow()) {

            //必须设置目标SDK为23及以上才能正常检测安装权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
                throw new RuntimeException("The targetSdkVersion SDK must be 23 or more");
            }

            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context     上下文对象
     * @param permissions 需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, List<String> permissions) {

        //如果是安卓6.0以下版本就返回null
        if (!PermissionUtils.isOverMarshmallow()) {
            return null;
        }

        ArrayList<String> failPermissions = new ArrayList<>();
        for (String permission : permissions) {

            //检测安装权限
            if (permission.equals(Permission.REQUEST_INSTALL_PACKAGES) && !isHasInstallPermission(context)) {
                failPermissions.add(permission);
                continue;
            }

            //检查悬浮窗权限
            if (permission.equals(Permission.SYSTEM_ALERT_WINDOW) && !isHasOverlaysPermission(context)) {
                failPermissions.add(permission);
                continue;
            }

            //把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permission);
            }
        }

        return failPermissions;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity           Activity对象
     * @param requestPermissions 请求的权限组
     */
    static void checkPermissions(Activity activity, List<String> requestPermissions) {
        List<String> manifest = PermissionUtils.getManifestPermissions(activity);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new ManifestPermissionException(permission);
                }
            }
        } else {
            throw new ManifestPermissionException(null);
        }
    }
}