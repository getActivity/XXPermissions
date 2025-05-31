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
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission, boolean skipRequest) {
        if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AndroidVersionTools.isAndroid14()) {
                return true;
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        if (!skipRequest && AndroidVersionTools.isAndroid14() &&
                (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
                 PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO))) {
            // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
            // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
            // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
            // 为了权限回调不出现失败，这里只能返回 true，这样告诉外层请求其实是成功的
            return isGrantedPermission(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED, skipRequest);
        }

        return super.isGrantedPermission(context, permission, skipRequest);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AndroidVersionTools.isAndroid14()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }
}