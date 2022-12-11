package com.hjq.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
         return isGrantedInstallPermission(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
         return isGrantedPictureInPicturePermission(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS) ||
              PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
         return PermissionUtils.checkSelfPermission(context, permission);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
         return false;
      }

      if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
         return false;
      }

      if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS) ||
              PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }
      return super.isPermissionPermanentDenied(activity, permission);
   }

   @Override
   public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_INSTALL_PACKAGES)) {
         return getInstallPermissionIntent(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.PICTURE_IN_PICTURE)) {
         return getPictureInPicturePermissionIntent(context);
      }
      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有安装权限
    */
   private static boolean isGrantedInstallPermission(@NonNull Context context) {
      return context.getPackageManager().canRequestPackageInstalls();
   }

   /**
    * 获取安装权限设置界面意图
    */
   private static Intent getInstallPermissionIntent(@NonNull Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
      intent.setData(PermissionUtils.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionUtils.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否有画中画权限
    */
   private static boolean isGrantedPictureInPicturePermission(@NonNull Context context) {
      AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
      int mode;
      if (AndroidVersion.isAndroid10()) {
         mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                 context.getApplicationInfo().uid, context.getPackageName());
      } else {
         mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                 context.getApplicationInfo().uid, context.getPackageName());
      }
      return mode == AppOpsManager.MODE_ALLOWED;
   }

   /**
    * 获取画中画权限设置界面意图
    */
   private static Intent getPictureInPicturePermissionIntent(@NonNull Context context) {
      // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
      Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS");
      intent.setData(PermissionUtils.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionUtils.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}