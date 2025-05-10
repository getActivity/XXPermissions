package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 11 权限委托实现
 */
class PermissionDelegateImplV30 extends PermissionDelegateImplV29 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid11()) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (AndroidVersionTools.isAndroid10() && !isUseDeprecationExternalStorage()) {
                    return false;
                }
                return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    PermissionUtils.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
            }
            return isGrantedManageStoragePermission();
        }

        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11) &&
            PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
            // 这里补充一下这样写的具体原因：
            // 1. 当 targetSdk >= Android 11 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，虽然可以弹出授权框，但是没有什么实际作用
            //    相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
            //    开发者可能会在清单文件注册 android:maxSdkVersion="29" 属性，这样会导致 WRITE_EXTERNAL_STORAGE 权限申请失败，这里需要返回 true 给外层
            // 2. 当 targetSdk >= Android 13 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            //    不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里需要返回 true 给到外层
            // 基于上面这两个原因，所以判断 WRITE_EXTERNAL_STORAGE 权限，结果无论是否授予，最终都会直接返回 true 给外层
            return true;
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return false;
        }

        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_11) &&
            PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
            return false;
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public boolean recheckPermissionResult(@NonNull Context context, @NonNull String permission, boolean grantResult) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11) &&
            PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
            // 具体原因自己点进去 isGrantedPermission 方法看代码注释，这次就不重复写注释了
            return isGrantedPermission(context, permission);
        }

        return super.recheckPermissionResult(context, permission, grantResult);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AndroidVersionTools.isAndroid11()) {
                return getApplicationDetailsIntent(context);
            }
            return getManageStoragePermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有所有文件的管理权限
     */
    @RequiresApi(AndroidVersionTools.ANDROID_11)
    private static boolean isGrantedManageStoragePermission() {
        return Environment.isExternalStorageManager();
    }

    /**
     * 获取所有文件的管理权限设置界面意图
     */
    @RequiresApi(AndroidVersionTools.ANDROID_11)
    private static Intent getManageStoragePermissionIntent(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * 是否采用的是非分区存储的模式
     */
    @RequiresApi(AndroidVersionTools.ANDROID_10)
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isUseDeprecationExternalStorage() {
        return Environment.isExternalStorageLegacy();
    }
}