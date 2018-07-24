package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HJQ on 2018-6-15.
 */
final class PermissionUtils {

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static String[] getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 是否是6.0以上版本
     */
    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions           需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, String[] permissions) {

        //如果是安卓6.0以下版本就返回null
        if(!PermissionUtils.isOverMarshmallow()) {
            return null;
        }

        ArrayList<String> failPermissions = null;
        for (String permission : permissions) {
            //把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                if (failPermissions == null) {
                    failPermissions = new ArrayList<>();
                }
                failPermissions.add(permission);
            }
        }
        return failPermissions;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

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
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

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
     * @param activity                  Activity对象
     * @param requestPermissions        请求的权限组
     */
    static void checkPermissions(Activity activity, String[] requestPermissions) {
        String[] permissions = PermissionUtils.getManifestPermissions(activity);
        if (permissions != null && permissions.length != 0) {
            List<String> manifest = Arrays.asList(permissions);
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new ManifestPermissionException(permission);
                }
            }
        }else {
            throw new ManifestPermissionException(null);
        }
    }
}
