package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/08/11
 *    desc   : Android 14 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_14)
class PermissionDelegateImplV34 extends PermissionDelegateImplV33 {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
         return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED);
      }

       // 如果用户授予了部分照片访问，那么 READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的，而 READ_MEDIA_IMAGES 权限状态是拒绝的
       if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) &&
           !PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_IMAGES)) {
           return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED);
       }

       // 如果用户授予了部分视频访问，那么 READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的，而 READ_MEDIA_VIDEO 权限状态是拒绝的
       if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) &&
           !PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VIDEO)) {
           return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED);
       }

      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }

      return super.isDoNotAskAgainPermission(activity, permission);
   }
}