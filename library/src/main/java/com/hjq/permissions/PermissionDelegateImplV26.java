package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 8.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_8)
class PermissionDelegateImplV26 extends PermissionDelegateImplV23 {

   @Override
   public boolean isGrantedPermission(Context context, String permission) {
      // 检测安装权限
      if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
         return isGrantedInstallPermission(context);
      }

      // 检测 Android 8.0 的两个新权限
      if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
         return true;
      }

      if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
         return context.checkSelfPermission(Permission.READ_PHONE_STATE) ==
                 PackageManager.PERMISSION_GRANTED;
      }

      return super.isGrantedPermission(context, permission);
   }

   @Override
   public Intent getPermissionIntent(Context context, String permission) {
      if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
         return getInstallPermissionIntent(context);
      }
      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有安装权限
    */
   static boolean isGrantedInstallPermission(Context context) {
      return context.getPackageManager().canRequestPackageInstalls();
   }

   /**
    * 获取安装权限设置界面意图
    */
   static Intent getInstallPermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
      intent.setData(PermissionDelegate.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}