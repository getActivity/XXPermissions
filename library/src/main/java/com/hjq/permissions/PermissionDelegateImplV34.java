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
}