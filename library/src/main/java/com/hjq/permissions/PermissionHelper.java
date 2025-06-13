package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.EnumMap;
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

    /** 新旧权限映射集合 */
    private static final Map<String, List<IPermission>> NEW_AND_OLD_PERMISSION_MAP = new HashMap<>(10);

    /** 后台权限列表 */
    private static final Map<String, List<IPermission>> BACKGROUND_PERMISSION_MAP = new HashMap<>(2);

    /** 危险权限组集合 */
    private static final Map<PermissionGroupType, List<IPermission>> DANGEROUS_PERMISSION_GROUP_MAP = new EnumMap<>(PermissionGroupType.class);

    /** 危险权限对应的类型集合 */
    private static final Map<IPermission, PermissionGroupType> DANGEROUS_PERMISSION_GROUP_TYPE_MAP = new HashMap<>(25);

    /** 低等级权限列表（排序时放最后） */
    private static final List<String> LOW_LEVEL_PERMISSION_LIST = new ArrayList<>(1);

    static {

        /* ---------------------------------------------------------------------------------------------------- */

        // Android 13 以下开启通知栏服务，需要用到旧的通知栏权限（框架自己虚拟出来的）
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.POST_NOTIFICATIONS, PermissionUtils.asArrayList(PermissionManifest.getNotificationServicePermission()));
        // Android 13 以下使用 WIFI 功能需要用到精确定位的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.NEARBY_WIFI_DEVICES, PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission()));
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_IMAGES, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_VIDEO, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_MEDIA_AUDIO, PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission()));
        // Android 12 以下扫描蓝牙需要精确定位权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.BLUETOOTH_SCAN, PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission()));
        // Android 11 以下访问完整的文件管理需要用到读写外部存储的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.MANAGE_EXTERNAL_STORAGE, PermissionUtils.asArrayList(
                                                                                PermissionManifest.getReadExternalStoragePermission(),
                                                                                PermissionManifest.getWriteExternalStoragePermission()));
        // Android 8.0 以下读取电话号码需要用到读取电话状态的权限
        NEW_AND_OLD_PERMISSION_MAP.put(PermissionConstants.READ_PHONE_NUMBERS, PermissionUtils.asArrayList(PermissionManifest.getReadPhoneStatePermission()));

        /* ---------------------------------------------------------------------------------------------------- */

        /* ---------------------------------------------------------------------------------------------------- */

        // 后台定位权限
        BACKGROUND_PERMISSION_MAP.put(PermissionConstants.ACCESS_BACKGROUND_LOCATION, PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission(),
                                                                                                                PermissionManifest.getAccessCoarseLocationPermission()));
        // 后台传感器权限
        BACKGROUND_PERMISSION_MAP.put(PermissionConstants.BODY_SENSORS_BACKGROUND, PermissionUtils.asArrayList(PermissionManifest.getBodySensorsPermission()));

        /* ---------------------------------------------------------------------------------------------------- */

        // 存储权限组
        List<IPermission> storagePermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission(),
                                                                    PermissionManifest.getWriteExternalStoragePermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.STORAGE, storagePermissionGroup);
        for (IPermission permission : storagePermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.STORAGE);
        }
        // 日历权限组
        List<IPermission> calendarPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadCalendarPermission(),
                                                                            PermissionManifest.getWriteCalendarPermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALENDAR, calendarPermissionGroup);
        for (IPermission permission : calendarPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALENDAR);
        }
        // 联系人权限组
        List<IPermission> contactsPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadContactsPermission(),
                                                                            PermissionManifest.getWriteContactsPermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CONTACTS, contactsPermissionGroup);
        for (IPermission permission : contactsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CONTACTS);
        }
        // 短信权限组
        List<IPermission> smsPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getSendSmsPermission(),
                                                                    PermissionManifest.getReadSmsPermission(),
                                                                    PermissionManifest.getReceiveSmsPermission(),
                                                                    PermissionManifest.getReceiveWapPushPermission(),
                                                                    PermissionManifest.getReceiveMmsPermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SMS, smsPermissionGroup);
        for (IPermission permission : smsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SMS);
        }
        // 定位权限组
        List<IPermission> locationPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getAccessCoarseLocationPermission(),
                                                                        PermissionManifest.getAccessFineLocationPermission(),
                                                                        PermissionManifest.getAccessBackgroundLocationPermission());
        // 蓝牙相关的权限组
        List<IPermission> bluetoothPermissions = PermissionUtils.asArrayList(PermissionManifest.getBluetoothScanPermission(),
                                                                            PermissionManifest.getBluetoothConnectPermission(),
                                                                            PermissionManifest.getBluetoothAdvertisePermission());

        // WIFI 相关的权限组
        IPermission wifiPermission = PermissionManifest.getNearbyWifiDevicesPermission();

        // 附近设备权限
        List<IPermission> nearbyDevicesPermissionGroup;
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
            for (IPermission permission : nearbyDevicesPermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.NEARBY_DEVICES);
            }
        }

        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.LOCATION, locationPermissionGroup);
        for (IPermission permission : locationPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.LOCATION);
        }

        // 传感器权限组
        List<IPermission> sensorsPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getBodySensorsPermission(),
                                                                    PermissionManifest.getBodySensorsBackgroundPermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.SENSORS, sensorsPermissionGroup);
        for (IPermission permission : sensorsPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.SENSORS);
        }
        // 电话权限组和通话记录权限组
        List<IPermission> phonePermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadPhoneStatePermission(),
                                                                            PermissionManifest.getCallPhonePermission(),
                                                                            PermissionManifest.getAddVoicemailPermission(),
                                                                            PermissionManifest.getUseSipPermission(),
                                                                            PermissionManifest.getReadPhoneNumbersPermission(),
                                                                            PermissionManifest.getAnswerPhoneCallsPermission(),
                                                                            PermissionManifest.getAcceptHandoverPermission());
        List<IPermission> callLogPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadCallLogPermission(),
                                                                                PermissionManifest.getWriteCallLogPermission(),
                                                                                PermissionManifest.getProcessOutgoingCallsPermission());

        // 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        if (AndroidVersionTools.isAndroid9()) {
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.PHONE, phonePermissionGroup);
            for (IPermission permission : phonePermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.PHONE);
            }
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.CALL_LOG, callLogPermissionGroup);
            for (IPermission permission : callLogPermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.CALL_LOG);
            }
        } else {
            List<IPermission> oldPhonePermissionGroup = new ArrayList<>();
            oldPhonePermissionGroup.addAll(phonePermissionGroup);
            oldPhonePermissionGroup.addAll(callLogPermissionGroup);
            DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.PHONE, oldPhonePermissionGroup);

            for (IPermission permission : oldPhonePermissionGroup) {
                DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.PHONE);
            }
        }

        // 读取照片和视频媒体文件权限组
        List<IPermission> imageAndVideoPermissionGroup = PermissionUtils.asArrayList(PermissionManifest.getReadMediaImagesPermission(),
                                                                                    PermissionManifest.getReadMediaVideoPermission(),
                                                                                    PermissionManifest.getReadMediaVisualUserSelectedPermission());
        DANGEROUS_PERMISSION_GROUP_MAP.put(PermissionGroupType.IMAGE_AND_VIDEO_MEDIA, imageAndVideoPermissionGroup);
        for (IPermission permission : imageAndVideoPermissionGroup) {
            DANGEROUS_PERMISSION_GROUP_TYPE_MAP.put(permission, PermissionGroupType.IMAGE_AND_VIDEO_MEDIA);
        }

        /* ---------------------------------------------------------------------------------------------------- */

        // 将读取图片位置权限定义为低等级权限
        LOW_LEVEL_PERMISSION_LIST.add(PermissionConstants.ACCESS_MEDIA_LOCATION);
    }

    /**
     * 通过新权限查询到对应的旧权限
     */
    @Nullable
    static List<IPermission> queryOldPermissionByNewPermission(@NonNull IPermission permission) {
        return NEW_AND_OLD_PERMISSION_MAP.get(permission.getName());
    }

    /**
     * 查询危险权限所在的权限组类型
     */
    @Nullable
    static PermissionGroupType queryDangerousPermissionGroupType(@NonNull IPermission permission) {
        return DANGEROUS_PERMISSION_GROUP_TYPE_MAP.get(permission);
    }

    /**
     * 查询危险权限所在的权限组
     */
    @Nullable
    static List<IPermission> getDangerousPermissionGroup(@NonNull PermissionGroupType permissionsGroupType) {
        return DANGEROUS_PERMISSION_GROUP_MAP.get(permissionsGroupType);
    }

    /**
     * 判断某个权限是否为后台权限
     */
    static boolean isBackgroundPermission(@NonNull IPermission permission) {
        return BACKGROUND_PERMISSION_MAP.containsKey(permission.getName());
    }

    /**
     * 根据后台权限获得前台权限
     */
    @Nullable
    static List<IPermission> queryForegroundPermissionByBackgroundPermission(@NonNull IPermission permission) {
        return BACKGROUND_PERMISSION_MAP.get(permission.getName());
    }

    /**
     * 从权限组中获取到后台权限
     */
    @Nullable
    static IPermission getBackgroundPermissionByGroup(List<IPermission> permissions) {
        for (IPermission permission : permissions) {
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
    static int getMaxIntervalTimeByPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getRequestIntervalTime();
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }

    /**
     * 通过权限集合获取最大的回调等待时间
     */
    static int getMaxWaitTimeByPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getResultWaitTime();
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }
}