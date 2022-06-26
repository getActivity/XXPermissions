package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : 权限委托接口
 */
public interface PermissionDelegate {

   /**
    * 判断某个权限是否授予了
    */
   boolean isGrantedPermission(Context context, String permission);

   /**
    * 判断某个权限是否永久拒绝了
    */
   boolean isPermissionPermanentDenied(Context context, String permission);

   /**
    * 获取权限设置页的意图
    */
   Intent getPermissionIntent(Context context, String permission);

   /**
    * 判断某个权限是否是特殊权限
    */
   static boolean isSpecialPermission(String permission) {
      return Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
              Permission.REQUEST_INSTALL_PACKAGES.equals(permission) ||
              Permission.SYSTEM_ALERT_WINDOW.equals(permission) ||
              Permission.WRITE_SETTINGS.equals(permission) ||
              Permission.NOTIFICATION_SERVICE.equals(permission) ||
              Permission.PACKAGE_USAGE_STATS.equals(permission) ||
              Permission.SCHEDULE_EXACT_ALARM.equals(permission) ||
              Permission.BIND_NOTIFICATION_LISTENER_SERVICE.equals(permission) ||
              Permission.ACCESS_NOTIFICATION_POLICY.equals(permission) ||
              Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.equals(permission) ||
              Permission.BIND_VPN_SERVICE.equals(permission);
   }

   /**
    * 判断某个危险权限是否授予了
    */
   @RequiresApi(api = Build.VERSION_CODES.M)
   static boolean isGrantedDangerPermission(Context context, String permission) {
      return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
   }

   /**
    * 解决 Android 12 调用 shouldShowRequestPermissionRationale 出现内存泄漏的问题
    *
    * issues 地址：https://github.com/getActivity/XXPermissions/issues/133
    */
   @RequiresApi(api = Build.VERSION_CODES.M)
   @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
   static boolean shouldShowRequestPermissionRationale(Context context, String permission) {
      Activity activity = PermissionUtils.findActivity(context);
      if (activity == null || AndroidVersion.isAndroid12()) {
         try {
            PackageManager packageManager;
            if (activity != null) {
               packageManager = activity.getApplication().getPackageManager();
            } else {
               packageManager = context.getPackageManager();
            }
            Method method = PackageManager.class.getMethod("shouldShowRequestPermissionRationale", String.class);
            return (boolean) method.invoke(packageManager, permission);
         } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
         }
      }
      return activity.shouldShowRequestPermissionRationale(permission);
   }

   /**
    * 获取应用详情界面意图
    */
   static Intent getApplicationDetailsIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      intent.setData(getPackageNameUri(context));
      return intent;
   }

   static Uri getPackageNameUri(Context context) {
      return Uri.parse("package:" + context.getPackageName());
   }
}