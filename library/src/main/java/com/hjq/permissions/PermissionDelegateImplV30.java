package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 11 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_11)
class PermissionDelegateImplV30 extends PermissionDelegateImplV29 {

   @Override
   public boolean isGrantedPermission(Context context, String permission) {
      // 检测存储权限
      if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
         return isGrantedStoragePermission();
      }

      if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
         boolean hasStorage = isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE) ||
                 PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_EXTERNAL_STORAGE);
         // 获取图片位置权限的前提是需要有文件权限
         return PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_MEDIA_LOCATION) && hasStorage;
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public Intent getPermissionIntent(Context context, String permission) {
      if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
         return getStoragePermissionIntent(context);
      }
      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有存储权限
    */
   static boolean isGrantedStoragePermission() {
      return Environment.isExternalStorageManager();
   }

   /**
    * 获取存储权限设置界面意图
    */
   static Intent getStoragePermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
      intent.setData(PermissionDelegate.getPackageNameUri(context));

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}