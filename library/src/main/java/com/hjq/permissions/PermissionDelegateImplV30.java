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
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            if (!AndroidVersion.isAndroid11()) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (AndroidVersion.isAndroid10() && !isUseDeprecationExternalStorage()) {
                    return false;
                }
                return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    PermissionUtils.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
            }
            return isGrantedManageStoragePermission();
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return false;
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AndroidVersion.isAndroid11()) {
                return getApplicationDetailsIntent(context);
            }
            return getManageStoragePermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission);
    }

    /**
     * 是否有所有文件的管理权限
     */
    @RequiresApi(AndroidVersion.ANDROID_11)
    private static boolean isGrantedManageStoragePermission() {
        return Environment.isExternalStorageManager();
    }

    /**
     * 获取所有文件的管理权限设置界面意图
     */
    @RequiresApi(AndroidVersion.ANDROID_11)
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
    @RequiresApi(AndroidVersion.ANDROID_10)
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isUseDeprecationExternalStorage() {
        return Environment.isExternalStorageLegacy();
    }
}