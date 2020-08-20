package com.hjq.permissions;

import android.Manifest;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求实体类，参考 {@link Manifest.permission}
 */
public final class Permission {

    private Permission() {}

    /** 外部存储权限（特殊权限，需要 Android 11 及以上） */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /** 应用安装权限（特殊权限，需要 Android 8.0 及以上） */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /** 通知栏权限（特殊权限，需要 Android 7.0 及以上） */
    public static final String NOTIFICATION_SERVICE = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /** 悬浮窗权限（特殊权限，需要 Android 6.0 及以上） */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /** 修改系统设置权限（特殊权限，需要 Android 6.0 及以上） */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /** 允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数（危险权限，需要 Android 8.0 及以上） */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    /** 权限允许您的应用读取设备中存储的电话号码（危险权限，需要 Android 8.0 及以上） */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /** 读取日程提醒 */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    /** 写入日程提醒 */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    /** 拍照权限 */
    public static final String CAMERA = "android.permission.CAMERA";

    /** 读取联系人 */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    /** 写入联系人 */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    /** 访问账户列表 */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /** 获取精确位置 */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    /** 获取粗略位置 */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    /** 录音权限 */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    /** 读取电话状态 */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    /** 拨打电话 */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    /** 读取通话记录 */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    /** 写入通话记录 */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    /** 添加语音邮件 */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    /** 使用SIP视频 */
    public static final String USE_SIP = "android.permission.USE_SIP";
    /** 处理拨出电话 */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    /** 传感器 */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    /** 发送短信 */
    public static final String SEND_SMS = "android.permission.SEND_SMS";
    /** 接收短信 */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    /** 读取短信 */
    public static final String READ_SMS = "android.permission.READ_SMS";
    /** 接收 WAP PUSH 信息 */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    /** 接收彩信 */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    /**
     * 读取外部存储
     *
     * @deprecated         已过时，请使用{@link Permission#MANAGE_EXTERNAL_STORAGE}
     */
    @Deprecated
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    /**
     * 写入外部存储
     *
     * @deprecated         已过时，请使用{@link Permission#MANAGE_EXTERNAL_STORAGE}
     */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 权限组
     */
    public static final class Group {

        /** 日历权限 */
        public static final String[] CALENDAR = new String[]{
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR};

        /** 联系人权限 */
        public static final String[] CONTACTS = new String[]{
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS,
                Permission.GET_ACCOUNTS};

        /** 位置权限 */
        public static final String[] LOCATION = new String[]{
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION};

        /**
         * 存储权限
         *
         * @deprecated         已过时，请使用{@link Permission#MANAGE_EXTERNAL_STORAGE}
         */
        @Deprecated
        public static final String[] STORAGE = new String[]{
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE};
    }
}