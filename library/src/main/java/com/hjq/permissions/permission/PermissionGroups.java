package com.hjq.permissions.permission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/14
 *    desc   : 权限组的名称常量集
 */
public final class PermissionGroups {

    /**
     * 权限组的后缀名称
     */
    public static final String SUFFIX = "_group";

    /**
     * 存储权限组，包含以下权限
     *
     * {@link PermissionNames#READ_EXTERNAL_STORAGE}
     * {@link PermissionNames#WRITE_EXTERNAL_STORAGE}
     */
    public static final String STORAGE = "storage" + SUFFIX;

    /**
     * 日历权限组，包含以下权限
     *
     * {@link PermissionNames#READ_CALENDAR}
     * {@link PermissionNames#WRITE_CALENDAR}
     */
    public static final String CALENDAR = "calendar" + SUFFIX;

    /**
     * 通讯录权限组，包含以下权限
     *
     * {@link PermissionNames#READ_CONTACTS}
     * {@link PermissionNames#WRITE_CONTACTS}
     * {@link PermissionNames#GET_ACCOUNTS}
     */
    public static final String CONTACTS = "contacts" + SUFFIX;

    /**
     * 短信权限组，包含以下权限
     *
     * {@link PermissionNames#SEND_SMS}
     * {@link PermissionNames#READ_SMS}
     * {@link PermissionNames#RECEIVE_SMS}
     * {@link PermissionNames#RECEIVE_WAP_PUSH}
     * {@link PermissionNames#RECEIVE_MMS}
     */
    public static final String SMS = "sms" + SUFFIX;

    /**
     * 位置权限组，包含以下权限
     *
     * {@link PermissionNames#ACCESS_COARSE_LOCATION}
     * {@link PermissionNames#ACCESS_FINE_LOCATION}
     * {@link PermissionNames#ACCESS_BACKGROUND_LOCATION}
     *
     * 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
     * {@link PermissionNames#BLUETOOTH_SCAN}
     * {@link PermissionNames#BLUETOOTH_CONNECT}
     * {@link PermissionNames#BLUETOOTH_ADVERTISE}
     *
     * 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
     * {@link PermissionNames#NEARBY_WIFI_DEVICES}
     */
    public static final String LOCATION = "location" + SUFFIX;

    /**
     * 传感器权限组，包含以下权限
     *
     * {@link PermissionNames#BODY_SENSORS}
     * {@link PermissionNames#BODY_SENSORS_BACKGROUND}
     */
    public static final String SENSORS = "sensors" + SUFFIX;

    /**
     * 电话权限组，包含以下权限
     *
     * {@link PermissionNames#READ_PHONE_STATE}
     * {@link PermissionNames#CALL_PHONE}
     * {@link PermissionNames#ADD_VOICEMAIL}
     * {@link PermissionNames#USE_SIP}
     * {@link PermissionNames#READ_PHONE_NUMBERS}
     * {@link PermissionNames#ANSWER_PHONE_CALLS}
     * {@link PermissionNames#ACCEPT_HANDOVER}
     *
     * 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
     * {@link PermissionNames#READ_CALL_LOG}
     * {@link PermissionNames#WRITE_CALL_LOG}
     * {@link PermissionNames#PROCESS_OUTGOING_CALLS}
     */
    public static final String PHONE = "phone" + SUFFIX;

    /**
     * 通话记录权限组（在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组），包含以下权限
     *
     * {@link PermissionNames#READ_CALL_LOG}
     * {@link PermissionNames#WRITE_CALL_LOG}
     * {@link PermissionNames#PROCESS_OUTGOING_CALLS}
     */
    public static final String CALL_LOG = "call_log" + SUFFIX;

    /**
     * 附近设备权限组，包含以下权限
     *
     * 在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
     * {@link PermissionNames#BLUETOOTH_SCAN}
     * {@link PermissionNames#BLUETOOTH_CONNECT}
     * {@link PermissionNames#BLUETOOTH_ADVERTISE}
     *
     * 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
     * {@link PermissionNames#NEARBY_WIFI_DEVICES}
     */
    public static final String NEARBY_DEVICES = "nearby_devices" + SUFFIX;

    /**
     * 照片和视频权限组（注意：不包含音频权限） ，包含以下权限
     *
     * {@link PermissionNames#READ_MEDIA_IMAGES}
     * {@link PermissionNames#READ_MEDIA_VIDEO}
     * {@link PermissionNames#READ_MEDIA_VISUAL_USER_SELECTED}
     */
    public static final String IMAGE_AND_VIDEO_MEDIA = "image_and_video_media" + SUFFIX;
}