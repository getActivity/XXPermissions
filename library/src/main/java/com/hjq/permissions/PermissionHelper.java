package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    /** 新旧权限映射集合 */
    private static final Map<String, String[]> NEW_AND_OLD_PERMISSION_MAP = new HashMap<>(10);

    /** 需要单独申请的权限列表 */
    private static final List<String> SEPARATE_REQUEST_PERMISSION_LIST = new ArrayList<>(3);

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

        // Android 13 以下开启通知栏服务，需要用到旧的通知栏权限（框架自己虚拟出来的）
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.POST_NOTIFICATIONS, new String[] { Permission.NOTIFICATION_SERVICE });
        // Android 13 以下使用 WIFI 功能需要用到精确定位的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.NEARBY_WIFI_DEVICES, new String[] { Permission.ACCESS_FINE_LOCATION });
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_IMAGES, new String[] { Permission.READ_EXTERNAL_STORAGE });
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_VIDEO, new String[] { Permission.READ_EXTERNAL_STORAGE });
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_AUDIO, new String[] { Permission.READ_EXTERNAL_STORAGE });
        // Android 12 以下扫描蓝牙需要精确定位权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.BLUETOOTH_SCAN, new String[] { Permission.ACCESS_FINE_LOCATION });
        // Android 11 以下访问完整的文件管理需要用到读写外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.MANAGE_EXTERNAL_STORAGE, new String[] {
                                                    Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE });
        // Android 10 以下获取运动步数需要用到传感器权限（因为 ACTIVITY_RECOGNITION 是从 Android 10 开始才从传感器权限中剥离成独立权限）
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.ACTIVITY_RECOGNITION, new String[] { Permission.BODY_SENSORS });
        // Android 8.0 以下读取电话号码需要用到读取电话状态的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_PHONE_NUMBERS, new String[] { Permission.READ_PHONE_STATE });

        // 后台传感器权限需要单独申请
        SEPARATE_REQUEST_PERMISSION_LIST.add(Permission.BODY_SENSORS_BACKGROUND);
        // 后台定位权限需要单独申请
        SEPARATE_REQUEST_PERMISSION_LIST.add(Permission.ACCESS_BACKGROUND_LOCATION);
        // 媒体文件地理位置需要单独申请
        SEPARATE_REQUEST_PERMISSION_LIST.add(Permission.ACCESS_MEDIA_LOCATION);
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

    /**
     * 通过新权限查询到对应的旧权限
     */
    @Nullable
    static String[] queryOldPermissionByNewPermission(@NonNull String permission) {
        return NEW_AND_OLD_PERMISSION_MAP.get(permission);
    }

    /**
     * 获取需要单独申请的权限列表
     */
    static List<String> getSeparateRequestPermissionList() {
        return SEPARATE_REQUEST_PERMISSION_LIST;
    }
}