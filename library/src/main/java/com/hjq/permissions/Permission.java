package com.hjq.permissions;

import android.Manifest;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 危险权限和特殊权限常量集，参考 {@link Manifest.permission}
 *    doc    : https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn
 *             https://developer.android.google.cn/guide/topics/permissions/overview?hl=zh-cn#normal-dangerous
 *             http://www.taf.org.cn/upload/AssociationStandard/TTAF%20004-2017%20Android%E6%9D%83%E9%99%90%E8%B0%83%E7%94%A8%E5%BC%80%E5%8F%91%E8%80%85%E6%8C%87%E5%8D%97.pdf
 */
@SuppressWarnings("unused")
public final class Permission {

    private Permission() {}

    /**
     * 读取应用列表权限（危险权限，电信终端产业协会联合各大中国手机厂商搞的一个权限）
     *
     * Github issue 地址：https://github.com/getActivity/XXPermissions/issues/175
     * 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/StdDetail.aspx?uid=3A7D6656-43B8-4C46-8871-E379A3EA1D48&stdType=TAF
     *
     * 需要注意的是：
     *   1. 需要在清单文件中注册 QUERY_ALL_PACKAGES 权限，否则在 Android 11 上面就算申请成功也是获取不到第三方安装列表信息的
     *   2. 这个权限在有的手机上面是授予状态，在有的手机上面是还没有授予，在有的手机上面是无法申请，能支持申请该权限的的厂商系统版本有：
     *      华为：Harmony 3.0.0 及以上版本，Harmony 2.0.1 实测不行
     *      荣耀：Magic UI 6.0 及以上版本，Magic UI 5.0 实测不行
     *      小米：Miui 13 及以上版本，Miui 12 实测不行，经过验证 miui 上面默认会授予此权限
     *      OPPO：(ColorOs 12 及以上版本 && Android 11+) || (ColorOs 11.1 及以上版本 && Android 12+)
     *      VIVO：虽然没有申请这个权限的通道，但是读取已安装第三方应用列表是没有问题的，没有任何限制
     *      真我：realme UI 3.0 及以上版本，realme UI 2.0 实测不行
     */
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";

    /**
     * 闹钟权限（特殊权限，Android 12 新增的权限）
     *
     * 需要注意的是：这个权限和其他特殊权限不同的是，默认已经是授予状态，用户也可以手动撤销授权
     * 官方文档介绍：https://developer.android.google.cn/about/versions/12/behavior-changes-12?hl=zh_cn#exact-alarm-permission
     */
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";

    /**
     * 文件管理权限（特殊权限，Android 11 新增的权限）
     *
     * 为了兼容 Android 11 以下版本，需要在清单文件中注册
     * {@link Permission#READ_EXTERNAL_STORAGE} 和 {@link Permission#WRITE_EXTERNAL_STORAGE} 权限
     *
     * 如果你的应用需要上架 GooglePlay，那么需要详细阅读谷歌应用商店的政策：
     * https://support.google.com/googleplay/android-developer/answer/9956427
     */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /**
     * 安装应用权限（特殊权限，Android 8.0 新增的权限）
     *
     * Android 11 特性调整，安装外部来源应用需要重启 App：https://cloud.tencent.com/developer/news/637591
     * 经过实践，Android 12 已经修复了此问题，授权或者取消授权后应用并不会重启
     */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /**
     * 画中画权限（特殊权限，Android 8.0 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     *
     * 需要注意的是：这个权限和其他特殊权限不同的是，默认已经是授予状态，用户也可以手动撤销授权
     */
    public static final String PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE";

    /**
     * 悬浮窗权限（特殊权限，Android 6.0 新增的权限）
     *
     * 在 Android 10 及之前的版本能跳转到应用悬浮窗设置页面，而在 Android 11 及之后的版本只能跳转到系统设置悬浮窗管理列表了
     * 官方解释：https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
     */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /** 系统设置权限（特殊权限，Android 6.0 新增的权限） */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /** 请求忽略电池优化选项权限（特殊权限，Android 6.0 新增的权限）*/
    public static final String REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";

    /** 勿扰权限，可控制手机响铃模式【静音，震动】（特殊权限，Android 6.0 新增的权限）*/
    public static final String ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /** 查看应用使用情况权限，简称使用统计权限（特殊权限，Android 5.0 新增的权限） */
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    /** 通知栏监听权限（特殊权限，Android 4.3 新增的权限，注意此权限不需要在清单文件中注册也能申请） */
    public static final String BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";

    /** VPN 权限（特殊权限，Android 4.0 新增的权限，注意此权限不需要在清单文件中注册也能申请） */
    public static final String BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";

    /** 通知栏权限（特殊权限，注意此权限不需要在清单文件中注册也能申请） */
    public static final String NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE";

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 发送通知权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，框架会自动申请 {@link #NOTIFICATION_SERVICE} 权限
     */
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    /**
     * WIFI 权限（Android 13.0 新增的权限）
     *
     * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
     * 否则就会导致在没有定位权限的情况下扫描不到附近的 WIFI 设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
     * <uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * 为了兼容 Android 13 以下版本，需要清单文件中注册 {@link #ACCESS_FINE_LOCATION} 权限
     * 还有 Android 13 以下设备，使用 WIFI 需要精确定位权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
     */
    public static final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";

    /**
     * 后台传感器权限（Android 13.0 新增的权限）
     *
     * 需要注意的是：
     * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
     * 2. 如果你的 App 只在前台状态下使用传感器功能，请不要申请该权限（后台传感器权限）
     */
    public static final String BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND";

    /**
     * 读取图片权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link #READ_EXTERNAL_STORAGE} 权限
     */
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";

    /**
     * 读取视频权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link #READ_EXTERNAL_STORAGE} 权限
     */
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /**
     * 读取音频权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link #READ_EXTERNAL_STORAGE} 权限
     */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

    /**
     * 蓝牙扫描权限（Android 12.0 新增的权限）
     *
     * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
     * 否则就会导致在没有定位权限的情况下扫描不到附近的蓝牙设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
     * <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * 为了兼容 Android 12 以下版本，需要清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
     * 还有 Android 12 以下设备，获取蓝牙扫描结果需要精确定位权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
     */
    public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";

    /**
     * 蓝牙连接权限（Android 12.0 新增的权限）
     *
     * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH} 权限
     */
    public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    /**
     * 蓝牙广播权限（Android 12.0 新增的权限）
     *
     * 将当前设备的蓝牙进行广播，供其他设备扫描时需要用到该权限
     * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
     */
    public static final String BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE";

    /**
     * 在后台获取位置（Android 10.0 新增的权限）
     *
     * 需要注意的是：
     * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
     * 2. 如果你的 App 只在前台状态下使用定位功能，没有在后台使用的场景，请不要申请该权限
     */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /**
     * 获取活动步数（Android 10.0 新增的权限）
     *
     * 需要注意的是：Android 10 以下不需要传感器（BODY_SENSORS）权限也能获取到步数
     */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * 读取照片中的地理位置（Android 10.0 新增的权限）
     *
     * 需要注意的是：如果这个权限申请成功了但是不能正常读取照片的地理信息，那么需要先申请存储权限，具体可分别下面两种情况：
     *
     * 1. 如果适配了分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 32 需要申请 {@link Permission#READ_EXTERNAL_STORAGE}
     *     2) 如果项目 targetSdkVersion >= 33 需要申请 {@link Permission#READ_MEDIA_IMAGES}
     *
     * 2. 如果没有适配分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 29 需要申请 {@link Permission#READ_EXTERNAL_STORAGE}
     *     2) 如果项目 targetSdkVersion >= 30 需要申请 {@link Permission#MANAGE_EXTERNAL_STORAGE}
     */
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";

    /** 允许呼叫应用继续在另一个应用中启动的呼叫（Android 9.0 新增的权限） */
    public static final String ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER";

    /**
     * 读取手机号码（Android 8.0 新增的权限）
     *
     * 为了兼容 Android 8.0 以下版本，需要在清单文件中注册 {@link #READ_PHONE_STATE} 权限
     */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /**
     * 接听电话（Android 8.0 新增的权限，Android 8.0 以下可以采用模拟耳机按键事件来实现接听电话，这种方式不需要权限）
     */
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

    /**
     * 读取电话状态
     *
     * 需要注意的是：这个权限在某些手机上面是没办法获取到的，因为某些系统禁止应用获得该权限
     *             所以你要是申请了这个权限之后没有弹授权框，而是直接回调授权失败方法
     *             请不要惊慌，这个不是 Bug、不是 Bug、不是 Bug，而是正常现象
     *
     * 后续情况汇报：有人反馈在 iQOO 手机上面获取不到该权限，在清单文件加入下面这个权限就可以了（这里只是做记录，并不代表这种方式就一定有效果）
     *             <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     */
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

    /**
     * 处理拨出电话
     *
     * @deprecated         在 Android 10 已经过时，请见：https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS
     */
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

    /**
     * 权限组
     */
    public static final class Group {

        /** 存储权限 */
        public static final String[] STORAGE = new String[] {
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE};

        /** 日历权限 */
        public static final String[] CALENDAR = new String[] {
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR};

        /** 联系人权限 */
        public static final String[] CONTACTS = new String[] {
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS,
                Permission.GET_ACCOUNTS};

        /** 蓝牙权限 */
        public static final String[] BLUETOOTH = new String[] {
                Permission.BLUETOOTH_SCAN,
                Permission.BLUETOOTH_CONNECT,
                Permission.BLUETOOTH_ADVERTISE};
    }
}