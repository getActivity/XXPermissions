package com.hjq.permissions;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/08/19
 *    desc   : 权限常量字符串限制
 */

@StringDef({
    Permission.GET_INSTALLED_APPS,
    Permission.SCHEDULE_EXACT_ALARM,
    Permission.MANAGE_EXTERNAL_STORAGE,
    Permission.REQUEST_INSTALL_PACKAGES,
    Permission.PICTURE_IN_PICTURE,
    Permission.SYSTEM_ALERT_WINDOW,
    Permission.WRITE_SETTINGS,
    Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
    Permission.ACCESS_NOTIFICATION_POLICY,
    Permission.PACKAGE_USAGE_STATS,
    Permission.BIND_NOTIFICATION_LISTENER_SERVICE,
    Permission.BIND_VPN_SERVICE,
    Permission.NOTIFICATION_SERVICE,
    Permission.READ_MEDIA_VISUAL_USER_SELECTED,
    Permission.POST_NOTIFICATIONS,
    Permission.NEARBY_WIFI_DEVICES,
    Permission.BODY_SENSORS_BACKGROUND,
    Permission.READ_MEDIA_IMAGES,
    Permission.READ_MEDIA_VIDEO,
    Permission.READ_MEDIA_AUDIO,
    Permission.BLUETOOTH_SCAN,
    Permission.BLUETOOTH_CONNECT,
    Permission.BLUETOOTH_ADVERTISE,
    Permission.ACCESS_BACKGROUND_LOCATION,
    Permission.ACTIVITY_RECOGNITION,
    Permission.ACCESS_MEDIA_LOCATION,
    Permission.ACCEPT_HANDOVER,
    Permission.READ_PHONE_NUMBERS,
    Permission.ANSWER_PHONE_CALLS,
    Permission.READ_EXTERNAL_STORAGE,
    Permission.WRITE_EXTERNAL_STORAGE,
    Permission.CAMERA,
    Permission.RECORD_AUDIO,
    Permission.ACCESS_FINE_LOCATION,
    Permission.ACCESS_COARSE_LOCATION,
    Permission.READ_CONTACTS,
    Permission.WRITE_CONTACTS,
    Permission.GET_ACCOUNTS,
    Permission.READ_CALENDAR,
    Permission.WRITE_CALENDAR,
    Permission.READ_PHONE_STATE,
    Permission.CALL_PHONE,
    Permission.READ_CALL_LOG,
    Permission.WRITE_CALL_LOG,
    Permission.ADD_VOICEMAIL,
    Permission.USE_SIP,
    Permission.PROCESS_OUTGOING_CALLS,
    Permission.BODY_SENSORS,
    Permission.SEND_SMS,
    Permission.RECEIVE_SMS,
    Permission.READ_SMS,
    Permission.RECEIVE_WAP_PUSH,
    Permission.RECEIVE_MMS})
@Retention(RetentionPolicy.SOURCE)
public @interface PermissionLimit {}
