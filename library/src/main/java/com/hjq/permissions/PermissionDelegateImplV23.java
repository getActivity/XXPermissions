package com.hjq.permissions;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 6.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_6)
class PermissionDelegateImplV23 extends PermissionDelegateImplV14 {

   @Override
   public boolean isGrantedPermission(Context context, String permission) {
      // 判断是否是特殊权限
      if (PermissionDelegate.isSpecialPermission(permission)) {

         // 检测悬浮窗权限
         if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return isGrantedWindowPermission(context);
         }

         // 检测系统权限
         if (Permission.WRITE_SETTINGS.equals(permission)) {
            return isGrantedSettingPermission(context);
         }

         // 检测勿扰权限
         if (Permission.ACCESS_NOTIFICATION_POLICY.equals(permission)) {
            return isGrantedNotDisturbPermission(context);
         }

         // 检测电池优化选项权限
         if (Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.equals(permission)) {
            return isGrantedIgnoreBatteryPermission(context);
         }

         return super.isGrantedPermission(context, permission);
      }

      // 兼容 Android 12 的三个新权限
      if (!AndroidVersion.isAndroid12()) {

         if (Permission.BLUETOOTH_SCAN.equals(permission)) {
            return PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_COARSE_LOCATION);
         }

         if (Permission.BLUETOOTH_CONNECT.equals(permission) ||
                 Permission.BLUETOOTH_ADVERTISE.equals(permission)) {
            return true;
         }
      }

      // 兼容 Android 10 的三个新权限
      if (!AndroidVersion.isAndroid10()) {

         if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission)) {
            return PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_FINE_LOCATION);
         }

         if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
            return PermissionDelegate.isGrantedDangerPermission(context, Permission.BODY_SENSORS);
         }

         if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
            return PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 兼容 Android 9.0 的一个新权限
      if (!AndroidVersion.isAndroid9()) {

         if (Permission.ACCEPT_HANDOVER.equals(permission)) {
            return true;
         }
      }

      // 兼容 Android 8.0 的两个新权限
      if (!AndroidVersion.isAndroid8()) {

         if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
            return true;
         }

         if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
            return PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_PHONE_STATE);
         }
      }

      return PermissionDelegate.isGrantedDangerPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(Context context, String permission) {
      if (PermissionDelegate.isSpecialPermission(permission)) {
         // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
         return false;
      }

      // 兼容 Android 12 的三个新权限
      if (!AndroidVersion.isAndroid12()) {

         if (Permission.BLUETOOTH_SCAN.equals(permission)) {
            return !PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_COARSE_LOCATION) &&
                    !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.ACCESS_COARSE_LOCATION);
         }

         if (Permission.BLUETOOTH_CONNECT.equals(permission) ||
                 Permission.BLUETOOTH_ADVERTISE.equals(permission)) {
            return false;
         }
      }

      // 兼容 Android 10 的三个新权限
      if (!AndroidVersion.isAndroid10()) {

         if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission)) {
            return !PermissionDelegate.isGrantedDangerPermission(context, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.ACCESS_FINE_LOCATION);
         }

         if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
            return !PermissionDelegate.isGrantedDangerPermission(context, Permission.BODY_SENSORS) &&
                    !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.BODY_SENSORS);
         }

         if (Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
            return !PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 兼容 Android 9.0 的一个新权限
      if (!AndroidVersion.isAndroid9()) {

         if (Permission.ACCEPT_HANDOVER.equals(permission)) {
            return false;
         }
      }

      // 兼容 Android 8.0 的两个新权限
      if (!AndroidVersion.isAndroid8()) {

         if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
            return false;
         }

         if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
            return !PermissionDelegate.isGrantedDangerPermission(context, Permission.READ_PHONE_STATE) &&
                    !PermissionDelegate.shouldShowRequestPermissionRationale(context, Permission.READ_PHONE_STATE);
         }
      }

      return !PermissionDelegate.isGrantedDangerPermission(context, permission) &&
              !PermissionDelegate.shouldShowRequestPermissionRationale(context, permission);
   }

   @Override
   public Intent getPermissionIntent(Context context, String permission) {
      if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
         return getWindowPermissionIntent(context);
      }

      if (Permission.WRITE_SETTINGS.equals(permission)) {
         return getSettingPermissionIntent(context);
      }

      if (Permission.ACCESS_NOTIFICATION_POLICY.equals(permission)) {
         return getNotDisturbPermissionIntent(context);
      }

      if (Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.equals(permission)) {
         return getIgnoreBatteryPermissionIntent(context);
      }

      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否授予了悬浮窗权限
    */
   static boolean isGrantedWindowPermission(Context context) {
      return Settings.canDrawOverlays(context);
   }

   /**
    * 获取悬浮窗权限设置界面意图
    */
   static Intent getWindowPermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
      // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
      // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
      intent.setData(PermissionDelegate.getPackageNameUri(context));

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否有系统设置权限
    */
   static boolean isGrantedSettingPermission(Context context) {
      if (AndroidVersion.isAndroid6()) {
         return Settings.System.canWrite(context);
      }
      return true;
   }

   /**
    * 获取系统设置权限界面意图
    */
   static Intent getSettingPermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      intent.setData(PermissionDelegate.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否有勿扰模式权限
    */
   static boolean isGrantedNotDisturbPermission(Context context) {
      return context.getSystemService(NotificationManager.class).isNotificationPolicyAccessGranted();
   }

   /**
    * 获取勿扰模式设置界面意图
    */
   static Intent getNotDisturbPermissionIntent(Context context) {
      Intent intent = null;

      if (AndroidVersion.isAndroid10()) {
         intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
         intent.setData(PermissionDelegate.getPackageNameUri(context));
      }

      if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否忽略电池优化选项
    */
   static boolean isGrantedIgnoreBatteryPermission(Context context) {
      return context.getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(context.getPackageName());
   }

   /**
    * 获取电池优化选项设置界面意图
    */
   static Intent getIgnoreBatteryPermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
      intent.setData(PermissionDelegate.getPackageNameUri(context));

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}