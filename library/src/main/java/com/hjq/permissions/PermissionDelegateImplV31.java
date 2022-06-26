package com.hjq.permissions;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
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
   public boolean isGrantedPermission(Context context, String permission) {
      // 检测闹钟权限
      if (Permission.SCHEDULE_EXACT_ALARM.equals(permission)) {
         return isGrantedAlarmPermission(context);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public Intent getPermissionIntent(Context context, String permission) {
      if (Permission.SCHEDULE_EXACT_ALARM.equals(permission)) {
         return getAlarmPermissionIntent(context);
      }
      return super.getPermissionIntent(context, permission);
   }

   /**
    * 是否有闹钟权限
    */
   static boolean isGrantedAlarmPermission(Context context) {
      if (AndroidVersion.isAndroid12()) {
         return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
      }
      return true;
   }

   /**
    * 获取闹钟权限设置界面意图
    */
   static Intent getAlarmPermissionIntent(Context context) {
      Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
      intent.setData(PermissionDelegate.getPackageNameUri(context));
      if (!PermissionUtils.areActivityIntent(context, intent)) {
         intent = PermissionDelegate.getApplicationDetailsIntent(context);
      }
      return intent;
   }
}