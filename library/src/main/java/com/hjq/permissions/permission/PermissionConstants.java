package com.hjq.permissions.permission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限和特殊权限常量集
 */
@SuppressWarnings("unused")
public final class PermissionConstants {

    private PermissionConstants() {}

    /** 读取应用列表权限 */
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";

    /** 全屏通知权限 */
    public static final String USE_FULL_SCREEN_INTENT = "android.permission.USE_FULL_SCREEN_INTENT";

    /** 闹钟权限 */
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";

    /** 所有文件访问权限 */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /** 安装应用权限 */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /** 画中画权限 */
    public static final String PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE";

    /** 悬浮窗权限 */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /** 写入系统设置权限 */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /** 请求忽略电池优化选项权限 */
    public static final String REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";

    /** 勿扰权限 */
    public static final String ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /** 查看应用使用情况权限 */
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    /** 通知栏监听权限 */
    public static final String BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";

    /** VPN 权限 */
    public static final String BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";

    /** 通知栏权限 */
    public static final String NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE";

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /** 授予对照片和视频的部分访问权限  */
    public static final String READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED";

    /** 发送通知权限 */
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    /** WIFI 权限 */
    public static final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";

    /** 后台传感器权限 */
    public static final String BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND";

    /** 读取图片权限 */
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";

    /** 读取视频权限 */
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /** 读取音频权限 */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

    /** 蓝牙扫描权限 */
    public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";

    /** 蓝牙连接权限 */
    public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    /** 蓝牙广播权限 */
    public static final String BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE";

    /** 在后台获取位置 */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /** 获取活动步数 */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /** 读取媒体文件的位置位置 */
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";

    /** 允许呼叫应用继续在另一个应用中启动的呼叫 */
    public static final String ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER";

    /** 读取手机号码 */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /** 接听电话 */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";

    /** 读取外部存储 */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /** 写入外部存储 */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /** 相机权限 */
    public static final String CAMERA = "android.permission.CAMERA";

    /** 麦克风权限 */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    /** 获取精确位置 */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    /** 获取粗略位置 */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    /** 读取联系人 */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";

    /** 修改联系人 */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";

    /** 访问账户列表 */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /** 读取日历 */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";

    /** 修改日历 */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    /** 读取电话状态 */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    /** 拨打电话 */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";

    /** 读取通话记录 */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";

    /** 修改通话记录 */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";

    /** 添加语音邮件 */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";

    /** 使用SIP视频 */
    public static final String USE_SIP = "android.permission.USE_SIP";

    /** 处理拨出电话 */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    /** 使用传感器 */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    /** 发送短信 */
    public static final String SEND_SMS = "android.permission.SEND_SMS";

    /** 接收短信 */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";

    /** 读取短信 */
    public static final String READ_SMS = "android.permission.READ_SMS";

    /** 接收 WAP 推送消息 */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";

    /** 接收彩信 */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";
}