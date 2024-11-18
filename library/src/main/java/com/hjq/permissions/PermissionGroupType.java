package com.hjq.permissions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2024/10/19
 *    desc   : 权限组分类
 */
public enum PermissionGroupType {

    /** 存储权限 */
    STORAGE,

    /** 日历权限组 */
    CALENDAR,

    /** 联系人权限组 */
    CONTACTS,

    /** 短信权限组 */
    SMS,

    /** 位置权限组 */
    LOCATION,

    /** 传感器权限组 */
    SENSORS,

    /** 通话记录权限组 */
    CALL_LOG,

    /** 附近设备权限组 */
    NEARBY_DEVICES,

    /** 照片和视频权限组 */
    IMAGE_AND_VIDEO_MEDIA
}