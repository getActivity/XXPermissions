package com.hjq.permissions;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 6.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_6)
class PermissionDelegateImplV23 extends PermissionDelegateImplV21 {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
      // 向下兼容 Android 13 新权限
      if (!AndroidVersion.isAndroid13()) {

         if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
            // 交给父类处理
            return super.isGrantedPermission(context, permission);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)) {
            return PermissionUtils.checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
                 PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
                 PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 向下兼容 Android 12 新权限
      if (!AndroidVersion.isAndroid12()) {

         if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN)) {
            return PermissionUtils.checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
                 PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
            return true;
         }
      }

      // 向下兼容 Android 11 新权限
      if (!AndroidVersion.isAndroid11()) {

         // 检测管理所有文件权限
         if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    PermissionUtils.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
         }
      }

      // 向下兼容 Android 10 新权限
      if (!AndroidVersion.isAndroid10()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            return PermissionUtils.checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            return true;
         }

         if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 向下兼容 Android 9.0 新权限
      if (!AndroidVersion.isAndroid9()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return true;
         }
      }

      // 向下兼容 Android 8.0 新权限
      if (!AndroidVersion.isAndroid8()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
            return true;
         }

         if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_PHONE_STATE);
         }
      }

      // 交给父类处理
      if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS) ||
              PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
         return super.isGrantedPermission(context, permission);
      }

      if (PermissionUtils.isSpecialPermission(permission)) {
         // 检测系统权限
         if (PermissionUtils.equalsPermission(permission, Permission.WRITE_SETTINGS)) {
            return isGrantedSettingPermission(context);
         }

         // 检测勿扰权限
         if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_NOTIFICATION_POLICY)) {
            return isGrantedNotDisturbPermission(context);
         }

         // 检测电池优化选项权限
         if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            return isGrantedIgnoreBatteryPermission(context);
         }

         return super.isGrantedPermission(context, permission);
      }

      return PermissionUtils.checkSelfPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      // 向下兼容 Android 13 新权限
      if (!AndroidVersion.isAndroid13()) {

         if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
            return super.isPermissionPermanentDenied(activity, permission);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.BODY_SENSORS) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.BODY_SENSORS);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
                 PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
                 PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 向下兼容 Android 12 新权限
      if (!AndroidVersion.isAndroid12()) {

         if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
                 PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
            return false;
         }
      }

      // 向下兼容 Android 10 新权限
      if (!AndroidVersion.isAndroid10()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
         }

         if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            return false;
         }

         if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.READ_EXTERNAL_STORAGE);
         }
      }

      // 向下兼容 Android 9.0 新权限
      if (!AndroidVersion.isAndroid9()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return false;
         }
      }

      // 向下兼容 Android 8.0 新权限

      if (!AndroidVersion.isAndroid8()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
            return false;
         }

         if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS)) {
            return !PermissionUtils.checkSelfPermission(activity, Permission.READ_PHONE_STATE) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.READ_PHONE_STATE);
         }
      }

      // 交给父类处理
      if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS) ||
              PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)) {
         return super.isPermissionPermanentDenied(activity, permission);
      }

      if (PermissionUtils.isSpecialPermission(permission)) {
         // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
         return false;
      }

      return !PermissionUtils.checkSelfPermission(activity, permission) &&
              !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
   }

   @Override
   public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.WRITE_SETTINGS)) {
         return getSettingPermissionIntent(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_NOTIFICATION_POLICY)) {
         return getNotDisturbPermissionIntent(context);
      }

      if (PermissionUtils.equalsPermission(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
         return getIgnoreBatteryPermissionIntent(context);
      }

      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有系统设置权限
    */
   private static boolean isGrantedSettingPermission(@NonNull Context context) {
      if (AndroidVersion.isAndroid6()) {
         return Settings.System.canWrite(context);
      }
      return true;
   }

   /**
    * 获取系统设置权限界面意图
    */
   private static Intent getSettingPermissionIntent(@NonNull Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      intent.setData(PermissionUtils.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionIntentManager.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否有勿扰模式权限
    */
   private static boolean isGrantedNotDisturbPermission(@NonNull Context context) {
      return context.getSystemService(NotificationManager.class).isNotificationPolicyAccessGranted();
   }

   /**
    * 获取勿扰模式设置界面意图
    */
   private static Intent getNotDisturbPermissionIntent(@NonNull Context context) {
      Intent intent = null;

      // issue 地址：https://github.com/getActivity/XXPermissions/issues/190
      // 这里解释一下，为什么要排除鸿蒙系统，因为用代码能检测到有这个 Intent，也能跳转过去，但是会被马上拒绝
      // 测试过了其他厂商系统及 Android 原生系统都没有这个问题，就只有鸿蒙有这个问题
      // 只因为这个 Intent 是隐藏的意图，所以就不让用，鸿蒙 2.0 和 3.0 都有这个问题
      // 别问鸿蒙 1.0 有没有问题，问就是鸿蒙一发布就 2.0 了，1.0 版本都没有问世过
      if (AndroidVersion.isAndroid10() && !PhoneRomUtils.isHarmonyOs()) {
         // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
         intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
         intent.setData(PermissionUtils.getPackageNameUri(context));
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionIntentManager.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 是否忽略电池优化选项
    */
   private static boolean isGrantedIgnoreBatteryPermission(@NonNull Context context) {
      return context.getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(context.getPackageName());
   }

   /**
    * 获取电池优化选项设置界面意图
    */
   private static Intent getIgnoreBatteryPermissionIntent(@NonNull Context context) {
      Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
      intent.setData(PermissionUtils.getPackageNameUri(context));

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionIntentManager.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}