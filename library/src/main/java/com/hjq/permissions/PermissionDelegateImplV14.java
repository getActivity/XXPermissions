package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 4.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_0)
class PermissionDelegateImplV14 implements PermissionDelegate {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      // 检测 VPN 权限
      if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
         return isGrantedVpnPermission(context);
      }

      return true;
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      return false;
   }

   @Override
   public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
         return getVpnPermissionIntent(context);
      }

      return PermissionIntentManager.getApplicationDetailsIntent(context);
   }

   /**
    * 是否有 VPN 权限
    */
   private static boolean isGrantedVpnPermission(@NonNull Context context) {
      return VpnService.prepare(context) == null;
   }

   /**
    * 获取 VPN 权限设置界面意图
    */
   private static Intent getVpnPermissionIntent(@NonNull Context context) {
      Intent intent = VpnService.prepare(context);
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionIntentManager.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}