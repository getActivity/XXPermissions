package com.hjq.permissions;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求工具类
 */
final class PermissionUtils {

    /**
     * 是否是 6.0 以上版本
     */
    static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是 7.0 以上版本
     */
    static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 是否是 8.0 以上版本
     */
    static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否是 10.0 以上版本
     */
    static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= 30;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        try {
            String[] requestedPermissions = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
            // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
            // https://github.com/getActivity/XXPermissions/issues/35
            if (requestedPermissions != null) {
                return Arrays.asList(requestedPermissions);
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    /**
     * 是否有存储权限
     */
    static boolean hasStoragePermission() {
        if (isAndroid11()) {
            return Environment.isExternalStorageManager();
        }
        return true;
    }

    /**
     * 是否有安装权限
     */
    static boolean hasInstallPermission(Context context) {
        if (isAndroid8()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean hasWindowPermission(Context context) {
        if (isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 是否有通知栏权限
     */
    static boolean hasNotifyPermission(Context context) {
        if (isAndroid7()) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return manager != null && manager.areNotificationsEnabled();
        }
        return true;
    }

    /**
     * 是否有系统设置权限
     */
    static boolean hasSettingPermission(Context context) {
        if (isAndroid6()) {
            return Settings.System.canWrite(context);
        }
        // 默认就是没有
        return false;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions           需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, List<String> permissions) {

        // 如果是安卓 6.0 以下版本就直接返回null
        if (!isAndroid6()) {
            return null;
        }

        ArrayList<String> failPermissions = new ArrayList<>();

        for (String permission : permissions) {

            // 检测存储权限
            if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {

                if (PermissionUtils.isAndroid11()) {
                    if (!hasStoragePermission()) {
                        failPermissions.add(permission);
                    }
                } else {
                    if (context.checkSelfPermission(Permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                            context.checkSelfPermission(Permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        failPermissions.add(permission);
                    }
                }
                continue;
            }

            // 检测安装权限
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {

                if (!hasInstallPermission(context)) {
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测悬浮窗权限
            if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {

                if (!hasWindowPermission(context)) {
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测通知栏权限
            if (Permission.NOTIFICATION_SERVICE.equals(permission)) {

                if (!hasNotifyPermission(context)) {
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测系统权限
            if (Permission.WRITE_SETTINGS.equals(permission)) {

                if (!hasSettingPermission(context)) {
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测 8.0 的两个新权限
            if (Permission.ANSWER_PHONE_CALLS.equals(permission) || Permission.READ_PHONE_NUMBERS.equals(permission)) {

                // 检查当前的安卓版本是否符合要求
                if (!isAndroid8()) {
                    continue;
                }
            }

            // 把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permission);
            }
        }

        return failPermissions;
    }

    /**
     * 是否还能继续申请没有授予的权限
     *
     * @param activity              Activity对象
     * @param failPermissions       失败的权限
     */
    static boolean isRequestDeniedPermission(Activity activity, List<String> failPermissions) {
        for (String permission : failPermissions) {
            // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
            if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
                    Permission.REQUEST_INSTALL_PACKAGES.equals(permission) ||
                    Permission.SYSTEM_ALERT_WINDOW.equals(permission) ||
                    Permission.NOTIFICATION_SERVICE.equals(permission) ||
                    Permission.WRITE_SETTINGS.equals(permission)) {
                continue;
            }

            // 检查是否还有权限还能继续申请的（这里指没有被授予的权限但是也没有被永久拒绝的）
            if (!checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    static boolean checkMorePermissionPermanentDenied(Activity activity, List<String> permissions) {
        for (String permission : permissions) {
            // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
            if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
                    Permission.REQUEST_INSTALL_PACKAGES.equals(permission) ||
                    Permission.SYSTEM_ALERT_WINDOW.equals(permission) ||
                    Permission.NOTIFICATION_SERVICE.equals(permission) ||
                    Permission.WRITE_SETTINGS.equals(permission)) {
                continue;
            }
            if (checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    private static boolean checkSinglePermissionPermanentDenied(Activity activity, String permission) {

        // 安装权限和浮窗权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
        //if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission) || Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
        //    return false;
        //}

        // 检测8.0的两个新权限
        if (Permission.ANSWER_PHONE_CALLS.equals(permission) || Permission.READ_PHONE_NUMBERS.equals(permission)) {

            // 检查当前的安卓版本是否符合要求
            if (!isAndroid8()) {
                return false;
            }
        }

        if (isAndroid6()) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED  &&
                    !activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            // 把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            // 把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity              Activity对象
     * @param requestPermissions    请求的权限组
     */
    static void checkPermissions(Activity activity, List<String> requestPermissions) {
        List<String> manifestPermissions = getManifestPermissions(activity);
        if (manifestPermissions != null && !manifestPermissions.isEmpty()) {
            for (String permission : requestPermissions) {
                if (!manifestPermissions.contains(permission) &&
                        !Permission.NOTIFICATION_SERVICE.equals(permission)) {
                    throw new ManifestException(permission);
                }
            }
        } else {
            throw new ManifestException();
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
     * @param context                   上下文对象
     * @param requestPermissions        请求的权限组
     */
    static void checkTargetSdkVersion(Context context, List<String> requestPermissions) {
        if (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            // 必须设置 targetSdkVersion >= 30 才能正常检测权限
            if (context.getApplicationInfo().targetSdkVersion < 30) {
                throw new RuntimeException("The targetSdkVersion SDK must be 30 or more");
            }
        } else if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES) ||
                requestPermissions.contains(Permission.NOTIFICATION_SERVICE) ||
                requestPermissions.contains(Permission.ANSWER_PHONE_CALLS) ||
                requestPermissions.contains(Permission.READ_PHONE_NUMBERS)) {
            // 必须设置 targetSdkVersion >= 26 才能正常检测权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.O) {
                throw new RuntimeException("The targetSdkVersion SDK must be 26 or more");
            }
        } else {
            // 必须设置 targetSdkVersion >= 23 才能正常检测权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
                throw new RuntimeException("The targetSdkVersion SDK must be 23 or more");
            }
        }
    }

    /**
     * 判断是否有这个意图
     */
    static boolean hasIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 判断某个数组里面是否包含了这个权限
     */
    static boolean containsPermission(String[] permissions, String permission) {
        for (String s : permissions) {
            if (s.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}