package com.hjq.permissions;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
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
class PermissionDelegateImplV23 extends PermissionDelegateImplV14 {

   @Override
   public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {

      // 判断是否是特殊权限
      if (PermissionUtils.isSpecialPermission(permission)) {

         // 检测悬浮窗权限
         if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
            return isGrantedWindowPermission(context);
         }

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

         if (!AndroidVersion.isAndroid11()) {
            // 检测管理所有文件权限
            if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
               return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                       PermissionUtils.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
            }
         }

         return super.isGrantedPermission(context, permission);
      }

      /* ---------------------------------------------------------------------------------------- */

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
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    PermissionUtils.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
         }
      }

      /* ---------------------------------------------------------------------------------------- */

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

      /* ---------------------------------------------------------------------------------------- */

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

      /* ---------------------------------------------------------------------------------------- */

      // 向下兼容 Android 9.0 新权限
      if (!AndroidVersion.isAndroid9()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return true;
         }
      }

      /* ---------------------------------------------------------------------------------------- */

      // 向下兼容 Android 8.0 新权限
      if (!AndroidVersion.isAndroid8()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ANSWER_PHONE_CALLS)) {
            return true;
         }

         if (PermissionUtils.equalsPermission(permission, Permission.READ_PHONE_NUMBERS)) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_PHONE_STATE);
         }
      }

      /* ---------------------------------------------------------------------------------------- */

      if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
         // 判断是否支持申请该权限
         if (isSupportGetInstalledAppsPermission(context)) {
            // 如果支持申请，那么再去判断权限是否授予
            return PermissionUtils.checkSelfPermission(context, permission);
         }
         // 如果不支持申请，则直接返回 true（代表有这个权限），反正也不会崩溃，顶多就是获取不到其他应用列表
         return true;
      }

      /* ---------------------------------------------------------------------------------------- */

      return PermissionUtils.checkSelfPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
      if (PermissionUtils.isSpecialPermission(permission)) {
         // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
         return false;
      }

      /* ---------------------------------------------------------------------------------------- */

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
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.READ_EXTERNAL_STORAGE) &&
                    !PermissionUtils.checkSelfPermission(activity, Permission.WRITE_EXTERNAL_STORAGE) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.WRITE_EXTERNAL_STORAGE);
         }
      }

      /* ---------------------------------------------------------------------------------------- */

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

      /* ---------------------------------------------------------------------------------------- */

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

      /* ---------------------------------------------------------------------------------------- */

      // 向下兼容 Android 9.0 新权限
      if (!AndroidVersion.isAndroid9()) {

         if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return false;
         }
      }

      /* ---------------------------------------------------------------------------------------- */

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

      /* ---------------------------------------------------------------------------------------- */

      if (PermissionUtils.equalsPermission(permission, Permission.GET_INSTALLED_APPS)) {
         // 判断是否支持申请该权限
         if (isSupportGetInstalledAppsPermission(activity)) {
            // 如果支持申请，那么再去判断权限是否永久拒绝
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
         }
         // 如果不支持申请，则直接返回 false（代表没有永久拒绝）
         return false;
      }

      /* ---------------------------------------------------------------------------------------- */

      return !PermissionUtils.checkSelfPermission(activity, permission) &&
              !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
   }

   @Override
   public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.SYSTEM_ALERT_WINDOW)) {
         return getWindowPermissionIntent(context);
      }

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
    * 是否授予了悬浮窗权限
    */
   private static boolean isGrantedWindowPermission(@NonNull Context context) {
      return Settings.canDrawOverlays(context);
   }

   /**
    * 获取悬浮窗权限设置界面意图
    */
   private static Intent getWindowPermissionIntent(@NonNull Context context) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
      // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
      // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
      intent.setData(PermissionUtils.getPackageNameUri(context));

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionUtils.getApplicationDetailsIntent(context);
      }
      return intent;
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
         intent = PermissionUtils.getApplicationDetailsIntent(context);
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

      if (AndroidVersion.isAndroid10()) {
         // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
         intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
         intent.setData(PermissionUtils.getPackageNameUri(context));
      }

      if (intent == null || !PermissionUtils.areActivityIntent(context, intent)) {
         intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
      }

      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionUtils.getApplicationDetailsIntent(context);
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
         intent = PermissionUtils.getApplicationDetailsIntent(context);
      }
      return intent;
   }

   /**
    * 判断是否支持获取应用列表权限
    */
   private boolean isSupportGetInstalledAppsPermission(Context context) {
      try {
         PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(Permission.GET_INSTALLED_APPS, 0);
         if (permissionInfo != null) {
            if (AndroidVersion.isAndroid9()) {
               return permissionInfo.getProtection() == PermissionInfo.PROTECTION_DANGEROUS;
            } else {
               return (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS;
            }
         }
      } catch (PackageManager.NameNotFoundException e) {
         e.printStackTrace();
      }

      try {
         // 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/upload/AssociationStandard/TTAF%20108-2022%20%E7%A7%BB%E5%8A%A8%E7%BB%88%E7%AB%AF%E5%BA%94%E7%94%A8%E8%BD%AF%E4%BB%B6%E5%88%97%E8%A1%A8%E6%9D%83%E9%99%90%E5%AE%9E%E6%96%BD%E6%8C%87%E5%8D%97.pdf
         // 这是兜底方案，因为测试了大量的机型，除了荣耀的 Magic UI 有按照这个规范去做，其他厂商（包括华为的 HarmonyOS）都没有按照这个规范去做
         // 虽然可以只用上面那种判断权限是不是危险权限的方式，但是避免不了有的手机厂商用下面的这种，所以两种都写比较好，小孩子才做选择，大人我全都要
         return Settings.Secure.getInt(context.getContentResolver(), "oem_installed_apps_runtime_permission_enable") == 1;
      } catch (Settings.SettingNotFoundException e) {
         e.printStackTrace();
      }

      return false;
   }
}