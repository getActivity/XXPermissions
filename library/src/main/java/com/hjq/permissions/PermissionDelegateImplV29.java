package com.hjq.permissions;

import android.content.Context;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 10 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_10)
class PermissionDelegateImplV29 extends PermissionDelegateImplV26 {

   @Override
   public boolean isGrantedPermission(Context context, String permission) {
      if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
         // 获取图片位置权限的前提是需要有文件权限
         return PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_MEDIA_LOCATION) &&
                 PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_EXTERNAL_STORAGE);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(Context context, String permission) {
      // 重新检测后台定位权限是否永久拒绝
      if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) &&
              !PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_BACKGROUND_LOCATION) &&
              !PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_FINE_LOCATION)) {
         return !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.ACCESS_FINE_LOCATION);
      }

      // 重新检测获取媒体位置权限是否永久拒绝
      if (Permission.ACCESS_MEDIA_LOCATION.equals(permission) &&
              !PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_MEDIA_LOCATION)) {
         if (!PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_EXTERNAL_STORAGE)) {
            return false;
         }
         return !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.ACCESS_MEDIA_LOCATION);
      }
      return super.isPermissionPermanentDenied(context, permission);
   }
}