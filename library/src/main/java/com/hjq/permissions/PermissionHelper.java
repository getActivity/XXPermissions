package com.hjq.permissions;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/07/17
 *    desc   : 权限辅助判断类
 */
final class PermissionHelper {

    /** 特殊权限列表 */
    private static final List<String> SPECIAL_PERMISSION_LIST = new ArrayList<>(12);

    /** 权限和 Android 版本对应的集合 */
    private static final Map<String, Integer> PERMISSION_VERSION_MAP = new HashMap<>(53);

    /** 框架自己虚拟出来的权限列表（此类权限不需要清单文件中静态注册也能动态申请） */
    private static final List<String> VIRTUAL_PERMISSION_LIST = new ArrayList<>(4);

    static {
        SPECIAL_PERMISSION_LIST.add(Permission.SCHEDULE_EXACT_ALARM);
        SPECIAL_PERMISSION_LIST.add(Permission.MANAGE_EXTERNAL_STORAGE);
        SPECIAL_PERMISSION_LIST.add(Permission.REQUEST_INSTALL_PACKAGES);
        SPECIAL_PERMISSION_LIST.add(Permission.PICTURE_IN_PICTURE);
        SPECIAL_PERMISSION_LIST.add(Permission.SYSTEM_ALERT_WINDOW);
        SPECIAL_PERMISSION_LIST.add(Permission.WRITE_SETTINGS);
        SPECIAL_PERMISSION_LIST.add(Permission.ACCESS_NOTIFICATION_POLICY);
        SPECIAL_PERMISSION_LIST.add(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        SPECIAL_PERMISSION_LIST.add(Permission.PACKAGE_USAGE_STATS);
        SPECIAL_PERMISSION_LIST.add(Permission.NOTIFICATION_SERVICE);
        SPECIAL_PERMISSION_LIST.add(Permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        SPECIAL_PERMISSION_LIST.add(Permission.BIND_VPN_SERVICE);

        PERMISSION_VERSION_MAP.put(Permission.SCHEDULE_EXACT_ALARM, AndroidVersion.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.MANAGE_EXTERNAL_STORAGE, AndroidVersion.ANDROID_11);
        PERMISSION_VERSION_MAP.put(Permission.REQUEST_INSTALL_PACKAGES, AndroidVersion.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.PICTURE_IN_PICTURE, AndroidVersion.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.SYSTEM_ALERT_WINDOW, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_SETTINGS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_NOTIFICATION_POLICY, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.PACKAGE_USAGE_STATS, AndroidVersion.ANDROID_5);
        PERMISSION_VERSION_MAP.put(Permission.NOTIFICATION_SERVICE, AndroidVersion.ANDROID_4_4);
        PERMISSION_VERSION_MAP.put(Permission.BIND_NOTIFICATION_LISTENER_SERVICE, AndroidVersion.ANDROID_4_3);
        PERMISSION_VERSION_MAP.put(Permission.BIND_VPN_SERVICE, AndroidVersion.ANDROID_4_0);

        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_VISUAL_USER_SELECTED, AndroidVersion.ANDROID_14);
        PERMISSION_VERSION_MAP.put(Permission.POST_NOTIFICATIONS, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.NEARBY_WIFI_DEVICES, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.BODY_SENSORS_BACKGROUND, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_IMAGES, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_VIDEO, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_AUDIO, AndroidVersion.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_SCAN, AndroidVersion.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_CONNECT, AndroidVersion.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_ADVERTISE, AndroidVersion.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_BACKGROUND_LOCATION, AndroidVersion.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACTIVITY_RECOGNITION, AndroidVersion.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_MEDIA_LOCATION, AndroidVersion.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACCEPT_HANDOVER, AndroidVersion.ANDROID_9);
        PERMISSION_VERSION_MAP.put(Permission.ANSWER_PHONE_CALLS, AndroidVersion.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.READ_PHONE_NUMBERS, AndroidVersion.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.GET_INSTALLED_APPS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_EXTERNAL_STORAGE, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_EXTERNAL_STORAGE, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.CAMERA, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECORD_AUDIO, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_FINE_LOCATION, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_COARSE_LOCATION, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CONTACTS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CONTACTS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.GET_ACCOUNTS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CALENDAR, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CALENDAR, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_PHONE_STATE, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.CALL_PHONE, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CALL_LOG, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CALL_LOG, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ADD_VOICEMAIL, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.USE_SIP, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.PROCESS_OUTGOING_CALLS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.BODY_SENSORS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.SEND_SMS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_SMS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_SMS, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_WAP_PUSH, AndroidVersion.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_MMS, AndroidVersion.ANDROID_6);

        VIRTUAL_PERMISSION_LIST.add(Permission.NOTIFICATION_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.BIND_VPN_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.PICTURE_IN_PICTURE);
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    static boolean isSpecialPermission(@NonNull String permission) {
        return PermissionUtils.containsPermission(SPECIAL_PERMISSION_LIST, permission);
    }

    /**
     * 获取权限是从哪个 Android 版本新增的
     */
    static int findAndroidVersionByPermission(@NonNull String permission) {
        Integer androidVersion = PERMISSION_VERSION_MAP.get(permission);
        if (androidVersion == null) {
            return 0;
        }
        return androidVersion;
    }

    /**
     * 判断权限是否为框架自己虚拟出来的
     */
    static boolean isVirtualPermission(@NonNull String permission) {
        return PermissionUtils.containsPermission(VIRTUAL_PERMISSION_LIST, permission);
    }
}