package com.hjq.permissions.demo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.hjq.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : 权限名称转换器
 */
public class PermissionNameConvert {

    /**
     * 获取权限名称
     */
   public static String getPermissionString(Context context, List<String> permissions) {
      return listToString(permissionsToStrings(context, permissions));
   }

   /**
    * String 列表拼接成一个字符串
    */
   public static String listToString(List<String> hints) {
      if (hints == null || hints.isEmpty()) {
         return "";
      }

      StringBuilder builder = new StringBuilder();
      for (String text : hints) {
         if (builder.length() == 0) {
            builder.append(text);
         } else {
            builder.append("、")
                    .append(text);
         }
      }
      return builder.toString();
   }

   /**
    * 将权限列表转换成对应名称列表
    */
   @NonNull
   public static List<String> permissionsToStrings(Context context, List<String> permissions) {
       List<String> permissionNames = new ArrayList<>();
       for (String permission : permissions) {
           switch (permission) {
               case Permission.READ_EXTERNAL_STORAGE:
               case Permission.WRITE_EXTERNAL_STORAGE: {
                   String hint = context.getString(R.string.common_permission_storage);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.CAMERA: {
                   String hint = context.getString(R.string.common_permission_camera);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.RECORD_AUDIO: {
                   String hint = context.getString(R.string.common_permission_microphone);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.ACCESS_FINE_LOCATION:
               case Permission.ACCESS_COARSE_LOCATION:
               case Permission.ACCESS_BACKGROUND_LOCATION: {
                   String hint;
                   if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) &&
                           !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                       hint = context.getString(R.string.common_permission_location_background);
                   } else {
                       hint = context.getString(R.string.common_permission_location);
                   }
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.BLUETOOTH_SCAN:
               case Permission.BLUETOOTH_CONNECT:
               case Permission.BLUETOOTH_ADVERTISE: {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                       String hint = context.getString(R.string.common_permission_bluetooth);
                       if (!permissionNames.contains(hint)) {
                           permissionNames.add(hint);
                       }
                   }
                   break;
               }
               case Permission.READ_PHONE_STATE:
               case Permission.CALL_PHONE:
               case Permission.ADD_VOICEMAIL:
               case Permission.USE_SIP:
               case Permission.READ_PHONE_NUMBERS:
               case Permission.ANSWER_PHONE_CALLS: {
                   String hint = context.getString(R.string.common_permission_phone);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.GET_ACCOUNTS:
               case Permission.READ_CONTACTS:
               case Permission.WRITE_CONTACTS: {
                   String hint = context.getString(R.string.common_permission_contacts);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.READ_CALENDAR:
               case Permission.WRITE_CALENDAR: {
                   String hint = context.getString(R.string.common_permission_calendar);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.READ_CALL_LOG:
               case Permission.WRITE_CALL_LOG:
               case Permission.PROCESS_OUTGOING_CALLS: {
                   String hint = context.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                           R.string.common_permission_call_log : R.string.common_permission_phone);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.BODY_SENSORS: {
                   String hint = context.getString(R.string.common_permission_sensors);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.ACTIVITY_RECOGNITION: {
                   String hint = context.getString(R.string.common_permission_activity_recognition);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.ACCESS_MEDIA_LOCATION: {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                       String hint = context.getString(R.string.common_permission_media_location);
                       if (!permissionNames.contains(hint)) {
                           permissionNames.add(hint);
                       }
                   }
                   break;
               }
               case Permission.SEND_SMS:
               case Permission.RECEIVE_SMS:
               case Permission.READ_SMS:
               case Permission.RECEIVE_WAP_PUSH:
               case Permission.RECEIVE_MMS: {
                   String hint = context.getString(R.string.common_permission_sms);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.MANAGE_EXTERNAL_STORAGE: {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                       String hint = context.getString(R.string.common_permission_manage_storage);
                       if (!permissionNames.contains(hint)) {
                           permissionNames.add(hint);
                       }
                   }
                   break;
               }
               case Permission.REQUEST_INSTALL_PACKAGES: {
                   String hint = context.getString(R.string.common_permission_install);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.SYSTEM_ALERT_WINDOW: {
                   String hint = context.getString(R.string.common_permission_window);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.WRITE_SETTINGS: {
                   String hint = context.getString(R.string.common_permission_setting);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.NOTIFICATION_SERVICE: {
                   String hint = context.getString(R.string.common_permission_notification);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.BIND_NOTIFICATION_LISTENER_SERVICE: {
                   String hint = context.getString(R.string.common_permission_notification_listener);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.PACKAGE_USAGE_STATS: {
                   String hint = context.getString(R.string.common_permission_task);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.SCHEDULE_EXACT_ALARM: {
                   String hint = context.getString(R.string.common_permission_alarm);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.ACCESS_NOTIFICATION_POLICY: {
                   String hint = context.getString(R.string.common_permission_not_disturb);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: {
                   String hint = context.getString(R.string.common_permission_ignore_battery);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               case Permission.BIND_VPN_SERVICE: {
                   String hint = context.getString(R.string.common_permission_vpn);
                   if (!permissionNames.contains(hint)) {
                       permissionNames.add(hint);
                   }
                   break;
               }
               default:
                   break;
           }
       }

       return permissionNames;
   }
}