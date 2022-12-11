package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/26
 *    desc   : Android 13 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_13)
class PermissionDelegateImplV33 extends PermissionDelegateImplV31 {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
         // 有后台传感器权限的前提条件是要有前台的传感器权限
         return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS) &&
                 PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS_BACKGROUND);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS) ||
              PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
         return PermissionUtils.checkSelfPermission(context, permission);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
         if (!PermissionUtils.checkSelfPermission(activity, Permission.BODY_SENSORS)) {
            return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.BODY_SENSORS);
         }
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS) ||
              PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
              PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }
      return super.isPermissionPermanentDenied(activity, permission);
   }
}