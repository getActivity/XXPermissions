package com.hjq.permissions;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求实体类
 */
public final class Permission {

    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"; // 8.0应用安装权限

    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"; // 6.0悬浮窗权限

    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR"; // 读取日程提醒
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"; // 写入日程提醒

    public static final String CAMERA = "android.permission.CAMERA"; // 拍照权限

    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS"; // 读取联系人
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"; // 写入联系人
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"; // 访问账户列表

    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"; // 获取精确位置
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"; // 获取粗略位置

    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO"; // 录音权限

    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"; // 读取电话状态
    public static final String CALL_PHONE = "android.permission.CALL_PHONE"; // 拨打电话
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG"; // 读取通话记录
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"; // 写入通话记录
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"; // 添加语音邮件
    public static final String USE_SIP = "android.permission.USE_SIP"; // 使用SIP视频
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"; // 处理拨出电话

    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS"; // 传感器

    public static final String SEND_SMS = "android.permission.SEND_SMS"; // 发送短信
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS"; // 接收短信
    public static final String READ_SMS = "android.permission.READ_SMS"; // 读取短信
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"; // 接收WAP PUSH信息
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS"; // 接收彩信

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"; // 读取外部存储
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"; // 写入外部存储

    public static final class Group {

        // 安装
        public static final String[] INSTALL = new String[]{Permission.REQUEST_INSTALL_PACKAGES};

        // 悬浮窗
        public static final String[] WINDOW = new String[]{Permission.SYSTEM_ALERT_WINDOW};

        // 日历
        public static final String[] CALENDAR = new String[]{
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR};

        // 摄像头
        public static final String[] CAMERA = new String[]{Permission.CAMERA};

        // 联系人
        public static final String[] CONTACTS = new String[]{
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS,
                Permission.GET_ACCOUNTS};

        // 位置
        public static final String[] LOCATION = new String[]{
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION};

        // 话筒
        public static final String[] MICROPHONE = new String[]{Permission.RECORD_AUDIO};

        // 电话
        public static final String[] PHONE = new String[]{
                Permission.READ_PHONE_STATE,
                Permission.CALL_PHONE,
                Permission.READ_CALL_LOG,
                Permission.WRITE_CALL_LOG,
                Permission.ADD_VOICEMAIL,
                Permission.USE_SIP,
                Permission.PROCESS_OUTGOING_CALLS};

        // 传感器
        public static final String[] SENSORS = new String[]{Permission.BODY_SENSORS};

        // 短信
        public static final String[] SMS = new String[]{
                Permission.SEND_SMS,
                Permission.RECEIVE_SMS,
                Permission.READ_SMS,
                Permission.RECEIVE_WAP_PUSH,
                Permission.RECEIVE_MMS};

        // 存储
        public static final String[] STORAGE = new String[]{
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE};
    }
}