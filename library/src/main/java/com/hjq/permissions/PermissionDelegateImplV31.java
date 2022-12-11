package com.hjq.permissions;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 12 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_12)
class PermissionDelegateImplV31 extends PermissionDelegateImplV30 {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      // 检测闹钟权限
      if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
         return isGrantedAlarmPermission(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN) ||
              PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
              PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
         return PermissionUtils.checkSelfPermission(context, permission);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
         return false;
      }

      if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN) ||
              PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
              PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }

      if (activity.getApplicationInfo().targetSdkVersion >= AndroidVersion.ANDROID_12 &&
              PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
         if (!PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                 !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_COARSE_LOCATION)) {
            return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_COARSE_LOCATION);
         }

         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }
      return super.isPermissionPermanentDenied(activity, permission);
   }

   @Override
   public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
         return getAlarmPermissionIntent(context);
      }

      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有闹钟权限
    */
   private static boolean isGrantedAlarmPermission(@NonNull Context context) {
      return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
   }

   /**
    * 获取闹钟权限设置界面意图
    */
   private static Intent getAlarmPermissionIntent(@NonNull Context context) {
      Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
      intent.setData(PermissionUtils.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionUtils.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}