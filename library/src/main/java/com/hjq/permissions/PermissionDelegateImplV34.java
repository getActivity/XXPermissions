package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/08/11
 *    desc   : Android 14 权限委托实现
 */
class PermissionDelegateImplV34 extends PermissionDelegateImplV33 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AndroidVersion.isAndroid14()) {
                return true;
            }
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AndroidVersion.isAndroid14()) {
                return false;
            }
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    @Override
    public boolean recheckPermissionResult(@NonNull Context context, @NonNull String permission, boolean grantResult) {
        // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
        // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
        // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
        if (AndroidVersion.isAndroid14() &&
            PermissionUtils.containsPermission(
                new String[] {Permission.READ_MEDIA_IMAGES, Permission.READ_MEDIA_VIDEO}, permission)) {
            return isGrantedPermission(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED);
        }

        return super.recheckPermissionResult(context, permission, grantResult);
    }
}