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
     * 访问部分照片和视频的权限字符串常量，如需权限对象请调用 {@link PermissionLists#getReadMediaVisualUserSelectedPermission()} 获取
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

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 在后台读取健康数据权限（任何类型），如需权限对象请调用 {@link PermissionLists#getReadHealthDataInBackgroundPermission()} 获取
     */
    public static final String READ_HEALTH_DATA_IN_BACKGROUND = "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND";

    /**
     * 读取以往的健康数据权限（任何类型），如需权限对象请调用 {@link PermissionLists#getReadHealthDataHistoryPermission()} 获取
     */
    public static final String READ_HEALTH_DATA_HISTORY = "android.permission.health.READ_HEALTH_DATA_HISTORY";

    /**
     * 读取运动消耗的卡路里数据权限，如需权限对象请调用 {@link PermissionLists#getReadActiveCaloriesBurnedPermission()} 获取
     */
    public static final String READ_ACTIVE_CALORIES_BURNED = "android.permission.health.READ_ACTIVE_CALORIES_BURNED";

    /**
     * 写入运动消耗的卡路里数据权限，如需权限对象请调用 {@link PermissionLists#getWriteActiveCaloriesBurnedPermission()} 获取
     */
    public static final String WRITE_ACTIVE_CALORIES_BURNED = "android.permission.health.WRITE_ACTIVE_CALORIES_BURNED";

    /**
     * 读取活动强度数据权限，如需权限对象请调用 {@link PermissionLists#getReadActivityIntensityPermission()} 获取
     */
    public static final String READ_ACTIVITY_INTENSITY = "android.permission.health.READ_ACTIVITY_INTENSITY";

    /**
     * 写入活动强度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteActivityIntensityPermission()} 获取
     */
    public static final String WRITE_ACTIVITY_INTENSITY = "android.permission.health.WRITE_ACTIVITY_INTENSITY";

    /**
     * 读取基础体温数据权限，如需权限对象请调用 {@link PermissionLists#getReadBasalBodyTemperaturePermission()} 获取
     */
    public static final String READ_BASAL_BODY_TEMPERATURE = "android.permission.health.READ_BASAL_BODY_TEMPERATURE";

    /**
     * 写入基础体温数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBasalBodyTemperaturePermission()} 获取
     */
    public static final String WRITE_BASAL_BODY_TEMPERATURE = "android.permission.health.WRITE_BASAL_BODY_TEMPERATURE";

    /**
     * 读取基础代谢率数据权限，如需权限对象请调用 {@link PermissionLists#getReadBasalMetabolicRatePermission()} 获取
     */
    public static final String READ_BASAL_METABOLIC_RATE = "android.permission.health.READ_BASAL_METABOLIC_RATE";

    /**
     * 写入基础代谢率数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBasalMetabolicRatePermission()} 获取
     */
    public static final String WRITE_BASAL_METABOLIC_RATE = "android.permission.health.WRITE_BASAL_METABOLIC_RATE";

    /**
     * 读取血糖数据权限，如需权限对象请调用 {@link PermissionLists#getReadBloodGlucosePermission()} 获取
     */
    public static final String READ_BLOOD_GLUCOSE = "android.permission.health.READ_BLOOD_GLUCOSE";

    /**
     * 写入血糖数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBloodGlucosePermission()} 获取
     */
    public static final String WRITE_BLOOD_GLUCOSE = "android.permission.health.WRITE_BLOOD_GLUCOSE";

    /**
     * 读取血压数据权限，如需权限对象请调用 {@link PermissionLists#getReadBloodPressurePermission()} 获取
     */
    public static final String READ_BLOOD_PRESSURE = "android.permission.health.READ_BLOOD_PRESSURE";

    /**
     * 写入血压数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBloodPressurePermission()} 获取
     */
    public static final String WRITE_BLOOD_PRESSURE = "android.permission.health.WRITE_BLOOD_PRESSURE";

    /**
     * 读取体脂数据权限，如需权限对象请调用 {@link PermissionLists#getReadBodyFatPermission()} 获取
     */
    public static final String READ_BODY_FAT = "android.permission.health.READ_BODY_FAT";

    /**
     * 写入体脂数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBodyFatPermission()} 获取
     */
    public static final String WRITE_BODY_FAT = "android.permission.health.WRITE_BODY_FAT";

    /**
     * 读取体温数据权限，如需权限对象请调用 {@link PermissionLists#getReadBodyTemperaturePermission()} 获取
     */
    public static final String READ_BODY_TEMPERATURE = "android.permission.health.READ_BODY_TEMPERATURE";

    /**
     * 写入体温数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBodyTemperaturePermission()} 获取
     */
    public static final String WRITE_BODY_TEMPERATURE = "android.permission.health.WRITE_BODY_TEMPERATURE";

    /**
     * 读取身体含水量数据权限，如需权限对象请调用 {@link PermissionLists#getReadBodyWaterMassPermission()} 获取
     */
    public static final String READ_BODY_WATER_MASS = "android.permission.health.READ_BODY_WATER_MASS";

    /**
     * 写入身体含水量数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBodyWaterMassPermission()} 获取
     */
    public static final String WRITE_BODY_WATER_MASS = "android.permission.health.WRITE_BODY_WATER_MASS";

    /**
     * 读取骨质密度数据权限，如需权限对象请调用 {@link PermissionLists#getReadBoneMassPermission()} 获取
     */
    public static final String READ_BONE_MASS = "android.permission.health.READ_BONE_MASS";

    /**
     * 写入骨质密度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteBoneMassPermission()} 获取
     */
    public static final String WRITE_BONE_MASS = "android.permission.health.WRITE_BONE_MASS";

    /**
     * 读取宫颈粘液数据权限，如需权限对象请调用 {@link PermissionLists#getReadCervicalMucusPermission()} 获取
     */
    public static final String READ_CERVICAL_MUCUS = "android.permission.health.READ_CERVICAL_MUCUS";

    /**
     * 写入宫颈粘液数据权限，如需权限对象请调用 {@link PermissionLists#getWriteCervicalMucusPermission()} 获取
     */
    public static final String WRITE_CERVICAL_MUCUS = "android.permission.health.WRITE_CERVICAL_MUCUS";

    /**
     * 读取距离数据权限，如需权限对象请调用 {@link PermissionLists#getReadDistancePermission()} 获取
     */
    public static final String READ_DISTANCE = "android.permission.health.READ_DISTANCE";

    /**
     * 写入距离数据权限，如需权限对象请调用 {@link PermissionLists#getWriteDistancePermission()} 获取
     */
    public static final String WRITE_DISTANCE = "android.permission.health.WRITE_DISTANCE";

    /**
     * 读取爬升高度数据权限，如需权限对象请调用 {@link PermissionLists#getReadElevationGainedPermission()} 获取
     */
    public static final String READ_ELEVATION_GAINED = "android.permission.health.READ_ELEVATION_GAINED";

    /**
     * 写入爬升高度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteElevationGainedPermission()} 获取
     */
    public static final String WRITE_ELEVATION_GAINED = "android.permission.health.WRITE_ELEVATION_GAINED";

    /**
     * 读取锻炼数据权限，如需权限对象请调用 {@link PermissionLists#getReadExercisePermission()} 获取
     */
    public static final String READ_EXERCISE = "android.permission.health.READ_EXERCISE";

    /**
     * 写入锻炼数据权限，如需权限对象请调用 {@link PermissionLists#getWriteExercisePermission()} 获取
     */
    public static final String WRITE_EXERCISE = "android.permission.health.WRITE_EXERCISE";

    /**
     * 读取锻炼路线数据权限，如需权限对象请调用 {@link PermissionLists#getReadExerciseRoutesPermission()} 获取
     */
    public static final String READ_EXERCISE_ROUTES = "android.permission.health.READ_EXERCISE_ROUTES";

    /**
     * 写入锻炼路线数据权限，如需权限对象请调用 {@link PermissionLists#getWriteExerciseRoutePermission()} 获取
     */
    public static final String WRITE_EXERCISE_ROUTE = "android.permission.health.WRITE_EXERCISE_ROUTE";

    /**
     * 读取爬楼层数数据权限，如需权限对象请调用 {@link PermissionLists#getReadFloorsClimbedPermission()} 获取
     */
    public static final String READ_FLOORS_CLIMBED = "android.permission.health.READ_FLOORS_CLIMBED";

    /**
     * 写入爬楼层数数据权限，如需权限对象请调用 {@link PermissionLists#getWriteFloorsClimbedPermission()} 获取
     */
    public static final String WRITE_FLOORS_CLIMBED = "android.permission.health.WRITE_FLOORS_CLIMBED";

    /**
     * 读取心率数据权限，如需权限对象请调用 {@link PermissionLists#getReadHeartRatePermission()} 获取
     */
    public static final String READ_HEART_RATE = "android.permission.health.READ_HEART_RATE";

    /**
     * 写入心率数据权限，如需权限对象请调用 {@link PermissionLists#getWriteHeartRatePermission()} 获取
     */
    public static final String WRITE_HEART_RATE = "android.permission.health.WRITE_HEART_RATE";

    /**
     * 读取心率变异性数据权限，如需权限对象请调用 {@link PermissionLists#getReadHeartRateVariabilityPermission()} 获取
     */
    public static final String READ_HEART_RATE_VARIABILITY = "android.permission.health.READ_HEART_RATE_VARIABILITY";

    /**
     * 写入心率变异性数据权限，如需权限对象请调用 {@link PermissionLists#getWriteHeartRateVariabilityPermission()} 获取
     */
    public static final String WRITE_HEART_RATE_VARIABILITY = "android.permission.health.WRITE_HEART_RATE_VARIABILITY";

    /**
     * 读取身高数据权限，如需权限对象请调用 {@link PermissionLists#getReadHeightPermission()} 获取
     */
    public static final String READ_HEIGHT = "android.permission.health.READ_HEIGHT";

    /**
     * 写入身高数据权限，如需权限对象请调用 {@link PermissionLists#getWriteHeightPermission()} 获取
     */
    public static final String WRITE_HEIGHT = "android.permission.health.WRITE_HEIGHT";

    /**
     * 读取饮水量权限，如需权限对象请调用 {@link PermissionLists#getReadHydrationPermission()} 获取
     */
    public static final String READ_HYDRATION = "android.permission.health.READ_HYDRATION";

    /**
     * 写入饮水量权限，如需权限对象请调用 {@link PermissionLists#getWriteHydrationPermission()} 获取
     */
    public static final String WRITE_HYDRATION = "android.permission.health.WRITE_HYDRATION";

    /**
     * 读取点状出血数据权限，如需权限对象请调用 {@link PermissionLists#getReadIntermenstrualBleedingPermission()} 获取
     */
    public static final String READ_INTERMENSTRUAL_BLEEDING = "android.permission.health.READ_INTERMENSTRUAL_BLEEDING";

    /**
     * 写入点状出血数据权限，如需权限对象请调用 {@link PermissionLists#getWriteIntermenstrualBleedingPermission()} 获取
     */
    public static final String WRITE_INTERMENSTRUAL_BLEEDING = "android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING";

    /**
     * 读取净体重数据权限，如需权限对象请调用 {@link PermissionLists#getReadLeanBodyMassPermission()} 获取
     */
    public static final String READ_LEAN_BODY_MASS = "android.permission.health.READ_LEAN_BODY_MASS";

    /**
     * 写入净体重数据权限，如需权限对象请调用 {@link PermissionLists#getWriteLeanBodyMassPermission()} 获取
     */
    public static final String WRITE_LEAN_BODY_MASS = "android.permission.health.WRITE_LEAN_BODY_MASS";

    /**
     * 读取经期数据权限，如需权限对象请调用 {@link PermissionLists#getReadMenstruationPermission()} 获取
     */
    public static final String READ_MENSTRUATION = "android.permission.health.READ_MENSTRUATION";

    /**
     * 写入经期数据权限，如需权限对象请调用 {@link PermissionLists#getWriteMenstruationPermission()} 获取
     */
    public static final String WRITE_MENSTRUATION = "android.permission.health.WRITE_MENSTRUATION";

    /**
     * 读取正念数据权限，如需权限对象请调用 {@link PermissionLists#getReadMindfulnessPermission()} 获取
     */
    public static final String READ_MINDFULNESS = "android.permission.health.READ_MINDFULNESS";

    /**
     * 写入正念数据权限，如需权限对象请调用 {@link PermissionLists#getWriteMindfulnessPermission()} 获取
     */
    public static final String WRITE_MINDFULNESS = "android.permission.health.WRITE_MINDFULNESS";

    /**
     * 读取营养数据权限，如需权限对象请调用 {@link PermissionLists#getReadNutritionPermission()} 获取
     */
    public static final String READ_NUTRITION = "android.permission.health.READ_NUTRITION";

    /**
     * 写入营养数据权限，如需权限对象请调用 {@link PermissionLists#getWriteNutritionPermission()} 获取
     */
    public static final String WRITE_NUTRITION = "android.permission.health.WRITE_NUTRITION";

    /**
     * 读取排卵检测数据权限，如需权限对象请调用 {@link PermissionLists#getReadOvulationTestPermission()} 获取
     */
    public static final String READ_OVULATION_TEST = "android.permission.health.READ_OVULATION_TEST";

    /**
     * 写入排卵检测数据权限，如需权限对象请调用 {@link PermissionLists#getWriteOvulationTestPermission()} 获取
     */
    public static final String WRITE_OVULATION_TEST = "android.permission.health.WRITE_OVULATION_TEST";

    /**
     * 读取血氧饱和度数据权限，如需权限对象请调用 {@link PermissionLists#getReadOxygenSaturationPermission()} 获取
     */
    public static final String READ_OXYGEN_SATURATION = "android.permission.health.READ_OXYGEN_SATURATION";

    /**
     * 写入血氧饱和度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteOxygenSaturationPermission()} 获取
     */
    public static final String WRITE_OXYGEN_SATURATION = "android.permission.health.WRITE_OXYGEN_SATURATION";

    /**
     * 读取训练计划数据权限，如需权限对象请调用 {@link PermissionLists#getReadPlannedExercisePermission()} 获取
     */
    public static final String READ_PLANNED_EXERCISE = "android.permission.health.READ_PLANNED_EXERCISE";

    /**
     * 写入训练计划数据权限，如需权限对象请调用 {@link PermissionLists#getWritePlannedExercisePermission()} 获取
     */
    public static final String WRITE_PLANNED_EXERCISE = "android.permission.health.WRITE_PLANNED_EXERCISE";

    /**
     * 读取体能数据权限，如需权限对象请调用 {@link PermissionLists#getReadPowerPermission()} 获取
     */
    public static final String READ_POWER = "android.permission.health.READ_POWER";

    /**
     * 写入体能数据权限，如需权限对象请调用 {@link PermissionLists#getWritePowerPermission()} 获取
     */
    public static final String WRITE_POWER = "android.permission.health.WRITE_POWER";

    /**
     * 读取呼吸频率数据权限，如需权限对象请调用 {@link PermissionLists#getReadRespiratoryRatePermission()} 获取
     */
    public static final String READ_RESPIRATORY_RATE = "android.permission.health.READ_RESPIRATORY_RATE";

    /**
     * 写入呼吸频率数据权限，如需权限对象请调用 {@link PermissionLists#getWriteRespiratoryRatePermission()} 获取
     */
    public static final String WRITE_RESPIRATORY_RATE = "android.permission.health.WRITE_RESPIRATORY_RATE";

    /**
     * 读取静息心率数据权限，如需权限对象请调用 {@link PermissionLists#getReadRestingHeartRatePermission()} 获取
     */
    public static final String READ_RESTING_HEART_RATE = "android.permission.health.READ_RESTING_HEART_RATE";

    /**
     * 写入静息心率数据权限，如需权限对象请调用 {@link PermissionLists#getWriteRestingHeartRatePermission()} 获取
     */
    public static final String WRITE_RESTING_HEART_RATE = "android.permission.health.WRITE_RESTING_HEART_RATE";

    /**
     * 读取性活动数据权限，如需权限对象请调用 {@link PermissionLists#getReadSexualActivityPermission()} 获取
     */
    public static final String READ_SEXUAL_ACTIVITY = "android.permission.health.READ_SEXUAL_ACTIVITY";

    /**
     * 写入性活动数据权限，如需权限对象请调用 {@link PermissionLists#getWriteSexualActivityPermission()} 获取
     */
    public static final String WRITE_SEXUAL_ACTIVITY = "android.permission.health.WRITE_SEXUAL_ACTIVITY";

    /**
     * 读取体表温度数据权限，如需权限对象请调用 {@link PermissionLists#getReadSkinTemperaturePermission()} 获取
     */
    public static final String READ_SKIN_TEMPERATURE = "android.permission.health.READ_SKIN_TEMPERATURE";

    /**
     * 写入体表温度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteSkinTemperaturePermission()} 获取
     */
    public static final String WRITE_SKIN_TEMPERATURE = "android.permission.health.WRITE_SKIN_TEMPERATURE";

    /**
     * 读取睡眠数据权限，如需权限对象请调用 {@link PermissionLists#getReadSleepPermission()} 获取
     */
    public static final String READ_SLEEP = "android.permission.health.READ_SLEEP";

    /**
     * 写入睡眠数据权限，如需权限对象请调用 {@link PermissionLists#getWriteSleepPermission()} 获取
     */
    public static final String WRITE_SLEEP = "android.permission.health.WRITE_SLEEP";

    /**
     * 读取速度数据权限，如需权限对象请调用 {@link PermissionLists#getReadSpeedPermission()} 获取
     */
    public static final String READ_SPEED = "android.permission.health.READ_SPEED";

    /**
     * 写入速度数据权限，如需权限对象请调用 {@link PermissionLists#getWriteSpeedPermission()} 获取
     */
    public static final String WRITE_SPEED = "android.permission.health.WRITE_SPEED";

    /**
     * 读取步数数据权限，如需权限对象请调用 {@link PermissionLists#getReadStepsPermission()} 获取
     */
    public static final String READ_STEPS = "android.permission.health.READ_STEPS";

    /**
     * 写入步数数据权限，如需权限对象请调用 {@link PermissionLists#getWriteStepsPermission()} 获取
     */
    public static final String WRITE_STEPS = "android.permission.health.WRITE_STEPS";

    /**
     * 读取消耗的卡路里总数数据权限，如需权限对象请调用 {@link PermissionLists#getReadTotalCaloriesBurnedPermission()} 获取
     */
    public static final String READ_TOTAL_CALORIES_BURNED = "android.permission.health.READ_TOTAL_CALORIES_BURNED";

    /**
     * 写入消耗的卡路里总数数据权限，如需权限对象请调用 {@link PermissionLists#getWriteTotalCaloriesBurnedPermission()} 获取
     */
    public static final String WRITE_TOTAL_CALORIES_BURNED = "android.permission.health.WRITE_TOTAL_CALORIES_BURNED";

    /**
     * 读取最大摄氧量数据权限，如需权限对象请调用 {@link PermissionLists#getReadVo2MaxPermission()} 获取
     */
    public static final String READ_VO2_MAX = "android.permission.health.READ_VO2_MAX";

    /**
     * 写入最大摄氧量数据权限，如需权限对象请调用 {@link PermissionLists#getWriteVo2MaxPermission()} 获取
     */
    public static final String WRITE_VO2_MAX = "android.permission.health.WRITE_VO2_MAX";

    /**
     * 读取体重数据权限，如需权限对象请调用 {@link PermissionLists#getReadWeightPermission()} 获取
     */
    public static final String READ_WEIGHT = "android.permission.health.READ_WEIGHT";

    /**
     * 写入体重数据权限，如需权限对象请调用 {@link PermissionLists#getWriteWeightPermission()} 获取
     */
    public static final String WRITE_WEIGHT = "android.permission.health.WRITE_WEIGHT";

    /**
     * 读取推轮椅次数数据权限，如需权限对象请调用 {@link PermissionLists#getReadWheelchairPushesPermission()} 获取
     */
    public static final String READ_WHEELCHAIR_PUSHES = "android.permission.health.READ_WHEELCHAIR_PUSHES";

    /**
     * 写入推轮椅次数数据权限，如需权限对象请调用 {@link PermissionLists#getWriteWheelchairPushesPermission()} 获取
     */
    public static final String WRITE_WHEELCHAIR_PUSHES = "android.permission.health.WRITE_WHEELCHAIR_PUSHES";

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 读取过敏反应数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataAllergiesIntolerancesPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES = "android.permission.health.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES";

    /**
     * 读取病症数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataConditionsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_CONDITIONS = "android.permission.health.READ_MEDICAL_DATA_CONDITIONS";

    /**
     * 读取化验结果数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataLaboratoryResultsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_LABORATORY_RESULTS = "android.permission.health.READ_MEDICAL_DATA_LABORATORY_RESULTS";

    /**
     * 读取用药情况数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataMedicationsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_MEDICATIONS = "android.permission.health.READ_MEDICAL_DATA_MEDICATIONS";

    /**
     * 读取个人详细信息数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataPersonalDetailsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_PERSONAL_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PERSONAL_DETAILS";

    /**
     * 读取就医情况数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataPractitionerDetailsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_PRACTITIONER_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PRACTITIONER_DETAILS";

    /**
     * 读取怀孕情况数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataPregnancyPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_PREGNANCY = "android.permission.health.READ_MEDICAL_DATA_PREGNANCY";

    /**
     * 读取医疗程序数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataProceduresPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_PROCEDURES = "android.permission.health.READ_MEDICAL_DATA_PROCEDURES";

    /**
     * 读取个人生活史数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataSocialHistoryPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_SOCIAL_HISTORY = "android.permission.health.READ_MEDICAL_DATA_SOCIAL_HISTORY";

    /**
     * 读取疫苗接种数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataVaccinesPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_VACCINES = "android.permission.health.READ_MEDICAL_DATA_VACCINES";

    /**
     * 读取医师详细信息数据权限，包括地点、预约时间以及就诊组织名称等数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataVisitsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_VISITS = "android.permission.health.READ_MEDICAL_DATA_VISITS";

    /**
     * 读取生命体征数据权限，如需权限对象请调用 {@link PermissionLists#getReadMedicalDataVitalSignsPermission()} 获取
     */
    public static final String READ_MEDICAL_DATA_VITAL_SIGNS = "android.permission.health.READ_MEDICAL_DATA_VITAL_SIGNS";

    /**
     * 写入所有健康记录数据权限，如需权限对象请调用 {@link PermissionLists#getWriteMedicalDataPermission()} 获取
     */
    public static final String WRITE_MEDICAL_DATA = "android.permission.health.WRITE_MEDICAL_DATA";
}