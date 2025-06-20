package com.hjq.permissions.permission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限和特殊权限的名称常量集
 */
@SuppressWarnings("unused")
public final class PermissionNames {

    private PermissionNames() {}

    /**
     * 读取应用列表权限字符串常量，如需权限对象请调用 {@link PermissionLists#getGetInstalledAppsPermission()} 获取
     */
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";

    /**
     * 全屏通知权限字符串常量，如需权限对象请调用 {@link PermissionLists#getUseFullScreenIntentPermission()} 获取
     */
    public static final String USE_FULL_SCREEN_INTENT = "android.permission.USE_FULL_SCREEN_INTENT";

    /**
     * 闹钟权限字符串常量，如需权限对象请调用 {@link PermissionLists#getScheduleExactAlarmPermission()} 获取
     */
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";

    /**
     * 所有文件访问权限字符串常量，如需权限对象请调用 {@link PermissionLists#getManageExternalStoragePermission()} 获取
     */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /**
     * 安装应用权限字符串常量，如需权限对象请调用 {@link PermissionLists#getRequestInstallPackagesPermission()} 获取
     */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /**
     * 画中画权限字符串常量，如需权限对象请调用 {@link PermissionLists#getPictureInPicturePermission()} 获取
     */
    public static final String PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE";

    /**
     * 悬浮窗权限字符串常量，如需权限对象请调用 {@link PermissionLists#getSystemAlertWindowPermission()} 获取
     */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /**
     * 写入系统设置权限字符串常量，如需权限对象请调用 {@link PermissionLists#getWriteSettingsPermission()} 获取
     */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /**
     * 请求忽略电池优化选项权限字符串常量，如需权限对象请调用 {@link PermissionLists#getRequestIgnoreBatteryOptimizationsPermission()} 获取
     */
    public static final String REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";

    /**
     * 勿扰权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAccessNotificationPolicyPermission()} 获取
     */
    public static final String ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /**
     * 查看应用使用情况权限字符串常量，如需权限对象请调用 {@link PermissionLists#getPackageUsageStatsPermission()} 获取
     */
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    /**
     * 通知栏监听权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBindNotificationListenerServicePermission(Class)} 获取
     */
    public static final String BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";

    /**
     * VPN 权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBindVpnServicePermission()} 获取
     */
    public static final String BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";

    /**
     * 通知栏权限字符串常量，如需权限对象请调用 {@link PermissionLists#getNotificationServicePermission(String)} 获取
     */
    public static final String NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE";

    /**
     * 无障碍服务权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBindAccessibilityServicePermission(Class)} 获取
     */
    public static final String BIND_ACCESSIBILITY_SERVICE = "android.permission.BIND_ACCESSIBILITY_SERVICE";

    /**
     * 设备管理器权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBindDeviceAdminPermission(Class, String)} 获取
     */
    public static final String BIND_DEVICE_ADMIN = "android.permission.BIND_DEVICE_ADMIN";

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 授予对照片和视频的部分访问权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadMediaVisualUserSelectedPermission()} 获取
     */
    public static final String READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED";

    /**
     * 发送通知权限字符串常量，如需权限对象请调用 {@link PermissionLists#getPostNotificationsPermission()} 获取
     */
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    /**
     * WIFI 权限字符串常量，如需权限对象请调用 {@link PermissionLists#getNearbyWifiDevicesPermission()} 获取
     */
    public static final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";

    /**
     * 后台传感器权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBodySensorsBackgroundPermission()} 获取
     */
    public static final String BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND";

    /**
     * 读取图片权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadMediaImagesPermission()} 获取
     */
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";

    /**
     * 读取视频权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadMediaVideoPermission()} 获取
     */
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /**
     * 读取音频权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadMediaAudioPermission()} 获取
     */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

    /**
     * 蓝牙扫描权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBluetoothScanPermission()} 获取
     */
    public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";

    /**
     * 蓝牙连接权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBluetoothConnectPermission()} 获取
     */
    public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    /**
     * 蓝牙广播权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBluetoothAdvertisePermission()} 获取
     */
    public static final String BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE";

    /**
     * 在后台获取位置权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAccessBackgroundLocationPermission()} 获取
     */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /**
     * 获取活动步数权限字符串常量，如需权限对象请调用 {@link PermissionLists#getActivityRecognitionPermission()} 获取
     */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * 访问媒体的位置信息权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAccessMediaLocationPermission()} 获取
     */
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";

    /**
     * 允许呼叫应用继续在另一个应用中启动的呼叫权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAcceptHandoverPermission()} 获取
     */
    public static final String ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER";

    /**
     * 读取手机号码权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadPhoneNumbersPermission()} 获取
     */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /**
     * 接听电话权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAnswerPhoneCallsPermission()} 获取
     */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";

    /**
     * 读取外部存储权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadExternalStoragePermission()} 获取
     */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /**
     * 写入外部存储权限字符串常量，如需权限对象请调用 {@link PermissionLists#getWriteExternalStoragePermission()} 获取
     */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 相机权限字符串常量，如需权限对象请调用 {@link PermissionLists#getCameraPermission()} 获取
     */
    public static final String CAMERA = "android.permission.CAMERA";

    /**
     * 麦克风权限字符串常量，如需权限对象请调用 {@link PermissionLists#getRecordAudioPermission()} 获取
     */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    /**
     * 获取精确位置权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAccessFineLocationPermission()} 获取
     */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    /**
     * 获取粗略位置权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAccessCoarseLocationPermission()} 获取
     */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    /**
     * 读取联系人权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadContactsPermission()} 获取
     */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";

    /**
     * 修改联系人权限字符串常量，如需权限对象请调用 {@link PermissionLists#getWriteContactsPermission()} 获取
     */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";

    /**
     * 访问账户列表权限字符串常量，如需权限对象请调用 {@link PermissionLists#getGetAccountsPermission()} 获取
     */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /**
     * 读取日历权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadCalendarPermission()} 获取
     */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";

    /**
     * 修改日历权限字符串常量，如需权限对象请调用 {@link PermissionLists#getWriteCalendarPermission()} 获取
     */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    /**
     * 读取电话状态权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadPhoneStatePermission()} 获取
     */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    /**
     * 拨打电话权限字符串常量，如需权限对象请调用 {@link PermissionLists#getCallPhonePermission()} 获取
     */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";

    /**
     * 读取通话记录权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadCallLogPermission()} 获取
     */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";

    /**
     * 修改通话记录权限字符串常量，如需权限对象请调用 {@link PermissionLists#getWriteCallLogPermission()} 获取
     */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";

    /**
     * 添加语音邮件权限字符串常量，如需权限对象请调用 {@link PermissionLists#getAddVoicemailPermission()} 获取
     */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";

    /**
     * 使用 SIP 视频权限字符串常量，如需权限对象请调用 {@link PermissionLists#getUseSipPermission()} 获取
     */
    public static final String USE_SIP = "android.permission.USE_SIP";

    /**
     * 处理拨出电话权限字符串常量，如需权限对象请调用 {@link PermissionLists#getProcessOutgoingCallsPermission()} 获取
     */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    /**
     * 使用传感器权限字符串常量，如需权限对象请调用 {@link PermissionLists#getBodySensorsPermission()} 获取
     */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    /**
     * 发送短信权限字符串常量，如需权限对象请调用 {@link PermissionLists#getSendSmsPermission()} 获取
     */
    public static final String SEND_SMS = "android.permission.SEND_SMS";

    /**
     * 接收短信权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReceiveSmsPermission()} 获取
     */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";

    /**
     * 读取短信权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadSmsPermission()} ()} 获取
     */
    public static final String READ_SMS = "android.permission.READ_SMS";

    /**
     * 接收 WAP 推送消息权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReceiveWapPushPermission()} 获取
     */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";

    /**
     * 接收彩信权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReceiveMmsPermission()} 获取
     */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";
}