package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final Map<String, List<String>> NEW_AND_OLD_PERMISSION_MAP = new HashMap<>(10);

    /** 后台权限列表 */
    private static final Map<String, List<String>> BACKGROUND_PERMISSION_MAP = new HashMap<>(2);

    /** 危险权限组集合 */
    private static final Map<PermissionGroupType, List<String>> DANGEROUS_PERMISSION_GROUP_MAP = new EnumMap<>(PermissionGroupType.class);

    /** 危险权限对应的类型集合 */
    private static final Map<String, PermissionGroupType> DANGEROUS_PERMISSION_GROUP_TYPE_MAP = new HashMap<>(25);

    /** 低等级权限列表（排序时放最后） */
    private static final List<String> LOW_LEVEL_PERMISSION_LIST = new ArrayList<>(3);

    /** 权限请求间隔时长 */
    private static final Map<String, Integer> PERMISSIONS_REQUEST_INTERVAL_TIME = new HashMap<>(2);

    /** 权限结果等待时长 */
    private static final Map<String, Integer> PERMISSIONS_RESULT_WAIT_TIME = new HashMap<>(25);

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

        /* ---------------------------------------------------------------------------------------------------- */

        PERMISSION_VERSION_MAP.put(Permission.SCHEDULE_EXACT_ALARM, AndroidVersionTools.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.MANAGE_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_11);
        PERMISSION_VERSION_MAP.put(Permission.REQUEST_INSTALL_PACKAGES, AndroidVersionTools.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.PICTURE_IN_PICTURE, AndroidVersionTools.ANDROID_8);
        // 虽然悬浮窗权限是在 Android 6.0 新增的权限，但是有些国产的厂商在 Android 6.0 之前的版本就自己加了，并且框架已经有做兼容了
        // 所以为了兼容更低的 Android 版本，这里需要将悬浮窗权限出现的 Android 版本成 API 17（即框架要求 minSdkVersion 版本）
        PERMISSION_VERSION_MAP.put(Permission.SYSTEM_ALERT_WINDOW, AndroidVersionTools.ANDROID_4_2);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_SETTINGS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_NOTIFICATION_POLICY, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.PACKAGE_USAGE_STATS, AndroidVersionTools.ANDROID_5);
        PERMISSION_VERSION_MAP.put(Permission.NOTIFICATION_SERVICE, AndroidVersionTools.ANDROID_4_4);
        PERMISSION_VERSION_MAP.put(Permission.BIND_NOTIFICATION_LISTENER_SERVICE, AndroidVersionTools.ANDROID_4_3);
        PERMISSION_VERSION_MAP.put(Permission.BIND_VPN_SERVICE, AndroidVersionTools.ANDROID_4_0);

        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_VISUAL_USER_SELECTED, AndroidVersionTools.ANDROID_14);
        PERMISSION_VERSION_MAP.put(Permission.POST_NOTIFICATIONS, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.NEARBY_WIFI_DEVICES, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.BODY_SENSORS_BACKGROUND, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_IMAGES, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_VIDEO, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.READ_MEDIA_AUDIO, AndroidVersionTools.ANDROID_13);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_SCAN, AndroidVersionTools.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_CONNECT, AndroidVersionTools.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.BLUETOOTH_ADVERTISE, AndroidVersionTools.ANDROID_12);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_BACKGROUND_LOCATION, AndroidVersionTools.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACTIVITY_RECOGNITION, AndroidVersionTools.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_MEDIA_LOCATION, AndroidVersionTools.ANDROID_10);
        PERMISSION_VERSION_MAP.put(Permission.ACCEPT_HANDOVER, AndroidVersionTools.ANDROID_9);
        PERMISSION_VERSION_MAP.put(Permission.ANSWER_PHONE_CALLS, AndroidVersionTools.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.READ_PHONE_NUMBERS, AndroidVersionTools.ANDROID_8);
        PERMISSION_VERSION_MAP.put(Permission.GET_INSTALLED_APPS, AndroidVersionTools.ANDROID_4_2);
        PERMISSION_VERSION_MAP.put(Permission.READ_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.CAMERA, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECORD_AUDIO, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_FINE_LOCATION, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ACCESS_COARSE_LOCATION, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CONTACTS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CONTACTS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.GET_ACCOUNTS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CALENDAR, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CALENDAR, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_PHONE_STATE, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.CALL_PHONE, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_CALL_LOG, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.WRITE_CALL_LOG, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.ADD_VOICEMAIL, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.USE_SIP, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.PROCESS_OUTGOING_CALLS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.BODY_SENSORS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.SEND_SMS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_SMS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.READ_SMS, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_WAP_PUSH, AndroidVersionTools.ANDROID_6);
        PERMISSION_VERSION_MAP.put(Permission.RECEIVE_MMS, AndroidVersionTools.ANDROID_6);

        /* ---------------------------------------------------------------------------------------------------- */

        VIRTUAL_PERMISSION_LIST.add(Permission.NOTIFICATION_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.BIND_VPN_SERVICE);
        VIRTUAL_PERMISSION_LIST.add(Permission.PICTURE_IN_PICTURE);

        /* ---------------------------------------------------------------------------------------------------- */

        // Android 13 以下开启通知栏服务，需要用到旧的通知栏权限（框架自己虚拟出来的）
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.POST_NOTIFICATIONS, PermissionUtils.asArrayList(Permission.NOTIFICATION_SERVICE));
        // Android 13 以下使用 WIFI 功能需要用到精确定位的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.NEARBY_WIFI_DEVICES, PermissionUtils.asArrayList(Permission.ACCESS_FINE_LOCATION));
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_IMAGES, PermissionUtils.asArrayList(Permission.READ_EXTERNAL_STORAGE));
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_VIDEO, PermissionUtils.asArrayList(Permission.READ_EXTERNAL_STORAGE));
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_MEDIA_AUDIO, PermissionUtils.asArrayList(Permission.READ_EXTERNAL_STORAGE));
        // Android 12 以下扫描蓝牙需要精确定位权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.BLUETOOTH_SCAN, PermissionUtils.asArrayList(Permission.ACCESS_FINE_LOCATION));
        // Android 11 以下访问完整的文件管理需要用到读写外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.MANAGE_EXTERNAL_STORAGE, PermissionUtils.asArrayList(Permission.READ_EXTERNAL_STORAGE,
                                                                                                        Permission.WRITE_EXTERNAL_STORAGE));
        // Android 8.0 以下读取电话号码需要用到读取电话状态的权限
        NEW_AND_OLD_PERMISSION_MAP.put(Permission.READ_PHONE_NUMBERS, PermissionUtils.asArrayList(Permission.READ_PHONE_STATE));

        /* ---------------------------------------------------------------------------------------------------- */
        
        // 后台定位权限
        BACKGROUND_PERMISSION_MAP.put(Permission.ACCESS_BACKGROUND_LOCATION, PermissionUtils.asArrayList(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION));
        // 后台传感器权限
        BACKGROUND_PERMISSION_MAP.put(Permission.BODY_SENSORS_BACKGROUND, PermissionUtils.asArrayList(Permission.BODY_SENSORS));
        // 后台权限列表（先获取，后面的代码会用到）
        Set<String> backgroundPermissions = BACKGROUND_PERMISSION_MAP.keySet();

        /* ---------------------------------------------------------------------------------------------------- */

        // 存储权限组
        List<String> storagePermissionGroup = PermissionUtils.asArrayList(Permission.READ_EXTERNAL_STORAGE,
                                                            Permission.WRITE_EXTERNAL_STORAGE);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.STORAGE, storagePermissionGroup);
        for (String permission : storagePermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.STORAGE);
        }
        // 日历权限组
        List<String> calendarPermissionGroup = PermissionUtils.asArrayList(Permission.READ_CALENDAR,
                                                            Permission.WRITE_CALENDAR);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALENDAR, calendarPermissionGroup);
        for (String permission : calendarPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALENDAR);
        }
        // 联系人权限组
        List<String> contactsPermissionGroup = PermissionUtils.asArrayList(Permission.READ_CONTACTS,
                                                            Permission.WRITE_CONTACTS);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CONTACTS, contactsPermissionGroup);
        for (String permission : contactsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CONTACTS);
        }
        // 短信权限组
        List<String> smsPermissionGroup = PermissionUtils.asArrayList(Permission.SEND_SMS,
                                                        Permission.READ_SMS,
                                                        Permission.RECEIVE_SMS,
                                                        Permission.RECEIVE_WAP_PUSH,
                                                        Permission.RECEIVE_MMS);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SMS, smsPermissionGroup);
        for (String permission : smsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SMS);
        }
        // 定位权限组
        List<String> locationPermissionGroup = PermissionUtils.asArrayList(Permission.ACCESS_COARSE_LOCATION,
                                                            Permission.ACCESS_FINE_LOCATION,
                                                            Permission.ACCESS_BACKGROUND_LOCATION);
        // 蓝牙相关的权限组
        List<String> bluetoothPermissions = PermissionUtils.asArrayList(Permission.BLUETOOTH_SCAN,
                                                            Permission.BLUETOOTH_CONNECT,
                                                            Permission.BLUETOOTH_ADVERTISE);

        // WIFI 相关的权限组
        String wifiPermission = Permission.NEARBY_WIFI_DEVICES;

        // 附近设备权限
        List<String> nearbyDevicesPermissionGroup;
        if (AndroidVersionTools.isAndroid13())  {
            nearbyDevicesPermissionGroup = new ArrayList<>(bluetoothPermissions.size() + 1);
        } else {
            nearbyDevicesPermissionGroup = new ArrayList<>(bluetoothPermissions.size());
        }

        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        if (AndroidVersionTools.isAndroid12())  {
            nearbyDevicesPermissionGroup.addAll(bluetoothPermissions);
        } else {
            locationPermissionGroup.addAll(bluetoothPermissions);
        }

        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        if (AndroidVersionTools.isAndroid13())  {
            nearbyDevicesPermissionGroup.add(wifiPermission);
        } else {
            locationPermissionGroup.add(wifiPermission);
        }

        if (!nearbyDevicesPermissionGroup.isEmpty()) {
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.NEARBY_DEVICES, nearbyDevicesPermissionGroup);
            for (String permission : nearbyDevicesPermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.NEARBY_DEVICES);
            }
        }

        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.LOCATION, locationPermissionGroup);
        for (String permission : locationPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.LOCATION);
        }

        // 传感器权限组
        List<String> sensorsPermissionGroup = PermissionUtils.asArrayList(Permission.BODY_SENSORS,
                                                            Permission.BODY_SENSORS_BACKGROUND);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SENSORS, sensorsPermissionGroup);
        for (String permission : sensorsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SENSORS);
        }
        // 电话权限组和通话记录权限组
        List<String> phonePermissionGroup = PermissionUtils.asArrayList(Permission.READ_PHONE_STATE,
                                                            Permission.CALL_PHONE,
                                                            Permission.ADD_VOICEMAIL,
                                                            Permission.USE_SIP,
                                                            Permission.READ_PHONE_NUMBERS,
                                                            Permission.ANSWER_PHONE_CALLS,
                                                            Permission.ACCEPT_HANDOVER);
        List<String> callLogPermissionGroup = PermissionUtils.asArrayList(Permission.READ_CALL_LOG,
                                                        Permission.WRITE_CALL_LOG,
                                                        Permission.PROCESS_OUTGOING_CALLS);

        // 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        if (AndroidVersionTools.isAndroid9()) {
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.PHONE, phonePermissionGroup);
            for (String permission : phonePermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.PHONE);
            }
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALL_LOG, callLogPermissionGroup);
            for (String permission : callLogPermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALL_LOG);
            }
        } else {
            List<String> oldPhonePermissionGroup = new ArrayList<>();
            oldPhonePermissionGroup.addAll(phonePermissionGroup);
            oldPhonePermissionGroup.addAll(callLogPermissionGroup);
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.PHONE, oldPhonePermissionGroup);

            for (String permission : oldPhonePermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.PHONE);
            }
        }

        // 读取照片和视频媒体文件权限组
        List<String> imageAndVideoPermissionGroup = PermissionUtils.asArrayList(Permission.READ_MEDIA_IMAGES,
                                                                Permission.READ_MEDIA_VIDEO,
                                                                Permission.READ_MEDIA_VISUAL_USER_SELECTED);
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.IMAGE_AND_VIDEO_MEDIA, imageAndVideoPermissionGroup);
        for (String permission : imageAndVideoPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.IMAGE_AND_VIDEO_MEDIA);
        }

        /* ---------------------------------------------------------------------------------------------------- */

        // 将后台权限定义为低等级权限
        LOW_LEVEL_PERMISSION_LIST.addAll(BACKGROUND_PERMISSION_MAP.keySet());
        // 将读取图片位置权限定义为低等级权限
        LOW_LEVEL_PERMISSION_LIST.add(Permission.ACCESS_MEDIA_LOCATION);

        /* ---------------------------------------------------------------------------------------------------- */

        // 设置权限请求间隔时间
        for (String permission : backgroundPermissions) {
            if (AndroidVersionTools.getCurrentAndroidVersionCode() < PermissionHelper.findAndroidVersionByPermission(permission)) {
                continue;
            }
            // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
            // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
            // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
            PERMISSIONS_REQUEST_INTERVAL_TIME.put(permission, 150);
        }

        /* ---------------------------------------------------------------------------------------------------- */

        // 设置权限回调等待的时间
        int normalSpecialPermissionWaitTime;
        if (AndroidVersionTools.isAndroid11()) {
            normalSpecialPermissionWaitTime = 200;
        } else {
            normalSpecialPermissionWaitTime = 300;
        }

        if (PhoneRomUtils.isEmui() || PhoneRomUtils.isHarmonyOs()) {
            // 需要加长时间等待，不然某些华为机型授权了但是获取不到权限
            if (AndroidVersionTools.isAndroid8()) {
                normalSpecialPermissionWaitTime = 300;
            } else {
                normalSpecialPermissionWaitTime = 500;
            }
        }

        for (String permission : SPECIAL_PERMISSION_LIST) {
            // 特殊权限一律需要一定的等待时间
            if (AndroidVersionTools.getCurrentAndroidVersionCode() < PermissionHelper.findAndroidVersionByPermission(permission)) {
                continue;
            }
            PERMISSIONS_RESULT_WAIT_TIME.put(permission, normalSpecialPermissionWaitTime);
        }

        if (PhoneRomUtils.isMiui() && AndroidVersionTools.isAndroid11() &&
                AndroidVersionTools.getCurrentAndroidVersionCode() >= PermissionHelper.findAndroidVersionByPermission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            // 经过测试，发现小米 Android 11 及以上的版本，申请这个权限需要 1000 毫秒才能判断到（测试了 800 毫秒还不行）
            // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
            // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
            // 测试了原生的模拟器和 vivo 云测并发现没有这个问题，所以断定这个 Bug 就是小米特有的
            PERMISSIONS_RESULT_WAIT_TIME.put(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, 1000);
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
    static List<String> queryOldPermissionByNewPermission(@NonNull String permission) {
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
     * 判断某个权限是否为后台权限
     */
    static boolean isBackgroundPermission(String permission) {
        return BACKGROUND_PERMISSION_MAP.containsKey(permission);
    }

    /**
     * 根据后台权限获得前台权限
     */
    @Nullable
    static List<String> queryForegroundPermissionByBackgroundPermission(String permission) {
        return BACKGROUND_PERMISSION_MAP.get(permission);
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

    /**
     * 获取低等级权限列表
     */
    @NonNull
    static List<String> getLowLevelPermissions() {
        return LOW_LEVEL_PERMISSION_LIST;
    }

    /**
     * 通过权限请求间隔时间
     */
    static int getMaxIntervalTimeByPermissions(@NonNull List<String> permissions) {
        int maxWaitTime = 0;
        for (String permission : permissions) {
            Integer time = PERMISSIONS_REQUEST_INTERVAL_TIME.get(permission);
            if (time == null) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }

    /**
     * 通过权限集合获取最大的回调等待时间
     */
    static int getMaxWaitTimeByPermissions(@NonNull List<String> permissions) {
        int maxWaitTime = 0;
        for (String permission : permissions) {
            Integer time = PERMISSIONS_RESULT_WAIT_TIME.get(permission);
            if (time == null) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }
}