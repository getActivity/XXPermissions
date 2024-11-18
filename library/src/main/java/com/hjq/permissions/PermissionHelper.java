package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
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

    /** 后台权限列表 */
    private static final List<String> BACKGROUND_PERMISSION_LIST = new ArrayList<>(2);

    /** 危险权限组集合 */
    private static final Map<PermissionGroupType, List<String>> DANGEROUS_PERMISSION_GROUP_MAP = new HashMap<>(9);

    /** 危险权限对应的类型集合 */
    private static final Map<String, PermissionGroupType> DANGEROUS_PERMISSION_GROUP_TYPE_MAP = new HashMap<>(25);

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

        // 后台定位权限
        BACKGROUND_PERMISSION_LIST.add(Permission.ACCESS_BACKGROUND_LOCATION);
        // 后台传感器权限
        BACKGROUND_PERMISSION_LIST.add(Permission.BODY_SENSORS_BACKGROUND);

        // 存储权限组
        List<String> storagePermissionGroup = Arrays.asList(Permission.READ_EXTERNAL_STORAGE,
                                                            Permission.WRITE_EXTERNAL_STORAGE);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.STORAGE, storagePermissionGroup);
        for (String permission : storagePermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.STORAGE);
        }
        // 日历权限组
        List<String> calendarPermissionGroup = Arrays.asList(Permission.READ_CALENDAR,
                                                            Permission.WRITE_CALENDAR);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALENDAR, calendarPermissionGroup);
        for (String permission : calendarPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALENDAR);
        }
        // 联系人权限组
        List<String> contactsPermissionGroup = Arrays.asList(Permission.READ_CONTACTS,
                                                            Permission.WRITE_CONTACTS);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CONTACTS, contactsPermissionGroup);
        for (String permission : contactsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CONTACTS);
        }
        // 短信权限组
        List<String> smsPermissionGroup = Arrays.asList(Permission.SEND_SMS,
                                                        Permission.READ_SMS,
                                                        Permission.RECEIVE_SMS,
                                                        Permission.RECEIVE_WAP_PUSH,
                                                        Permission.RECEIVE_MMS);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SMS, smsPermissionGroup);
        for (String permission : smsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SMS);
        }
        // 定位权限组
        List<String> locationPermissionGroup = Arrays.asList(Permission.ACCESS_COARSE_LOCATION,
                                                            Permission.ACCESS_FINE_LOCATION,
                                                            Permission.ACCESS_BACKGROUND_LOCATION);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.LOCATION, locationPermissionGroup);
        for (String permission : locationPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.LOCATION);
        }
        // 传感器权限组
        List<String> sensorsPermissionGroup = Arrays.asList(Permission.BODY_SENSORS,
                                                            Permission.BODY_SENSORS_BACKGROUND);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SENSORS, sensorsPermissionGroup);
        for (String permission : sensorsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SENSORS);
        }
        // 通话记录权限组
        List<String> callLogPermissionGroup = Arrays.asList(Permission.READ_CALL_LOG,
                                                            Permission.WRITE_CALL_LOG);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALL_LOG, callLogPermissionGroup);
        for (String permission : callLogPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALL_LOG);
        }
        // 附近设备权限组
        List<String> nearbyDevicesPermissionGroup = Arrays.asList(Permission.BLUETOOTH_SCAN,
                                                                Permission.BLUETOOTH_CONNECT,
                                                                Permission.BLUETOOTH_ADVERTISE,
                                                                Permission.NEARBY_WIFI_DEVICES);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.NEARBY_DEVICES, nearbyDevicesPermissionGroup);
        for (String permission : nearbyDevicesPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.NEARBY_DEVICES);
        }
        // 读取照片和视频媒体文件权限组
        List<String> imageAndVideoPermissionGroup = Arrays.asList(Permission.READ_MEDIA_IMAGES,
                                                                Permission.READ_MEDIA_VIDEO,
                                                                Permission.READ_MEDIA_VISUAL_USER_SELECTED);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.IMAGE_AND_VIDEO_MEDIA, imageAndVideoPermissionGroup);
        for (String permission : imageAndVideoPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.IMAGE_AND_VIDEO_MEDIA);
        }
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
     * 查询危险权限所在的权限组类型
     */
    @Nullable
    static PermissionGroupType queryDangerousPermissionGroupType(@NonNull String permission) {
        return DANGEROUS_PERMISSION_GROUP_TYPE_MAP.get(permission);
    }

    /**
     * 查询危险权限所在的权限组
     */
    @Nullable
    static List<String> getDangerousPermissionGroup(@NonNull PermissionGroupType permissionsGroupType) {
        return DANGEROUS_PERMISSION_GROUP_MAP.get(permissionsGroupType);
    }

    /**
     * 判断申请的权限列表中是否包含后台权限
     */
    static boolean containsBackgroundPermission(List<String> permissions) {
        for (String backgroundPermission : BACKGROUND_PERMISSION_LIST) {
            if (permissions.contains(backgroundPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限是否为后台权限
     */
    static boolean isBackgroundPermission(String permission) {
        return BACKGROUND_PERMISSION_LIST.contains(permission);
    }

    /**
     * 从权限组中获取到后台权限
     */
    @Nullable
    static String getBackgroundPermissionByGroup(List<String> permissions) {
        for (String permission : permissions) {
            if (isBackgroundPermission(permission)) {
                return permission;
            }
        }
        return null;
    }
}