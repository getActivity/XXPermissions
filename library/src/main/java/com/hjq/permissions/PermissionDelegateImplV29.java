package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 10 权限委托实现
 */
class PermissionDelegateImplV29 extends PermissionDelegateImplV28 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            if (!AndroidVersion.isAndroid10()) {
                return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
            }
            return isGrantedReadStoragePermission(context) &&
                PermissionUtils.checkSelfPermission(context, Permission.ACCESS_MEDIA_LOCATION);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AndroidVersion.isAndroid6()) {
                return true;
            }
            if (!AndroidVersion.isAndroid10()) {
                return PermissionUtils.checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            if (!AndroidVersion.isAndroid10()) {
                return true;
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AndroidVersion.isAndroid6()) {
                return false;
            }
            if (!AndroidVersion.isAndroid10()) {
                return !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
            }
            // 先检查前台的定位权限是否拒绝了
            if (!PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                // 如果是的话就判断前台的定位权限是否被永久拒绝了
                return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
            }
            // 如果不是的话再去判断后台的定位权限是否被拒永久拒绝了
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            if (!AndroidVersion.isAndroid6()) {
                return false;
            }
            if (!AndroidVersion.isAndroid10()) {
                return !PermissionUtils.checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.READ_EXTERNAL_STORAGE);
            }
            return isGrantedReadStoragePermission(activity) &&
                !PermissionUtils.checkSelfPermission(activity, permission) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            if (!AndroidVersion.isAndroid10()) {
                return false;
            }
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    /**
     * 判断是否授予了读取文件的权限
     */
    @RequiresApi(AndroidVersion.ANDROID_6)
    private boolean isGrantedReadStoragePermission(@NonNull Context context) {
        if (AndroidVersion.isAndroid13() && AndroidVersion.getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_13) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_IMAGES) ||
                isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (AndroidVersion.isAndroid11() && AndroidVersion.getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_11) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) ||
                isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE);
        }
        return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
    }
}