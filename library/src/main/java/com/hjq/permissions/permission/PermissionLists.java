package com.hjq.permissions.permission;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.admin.DeviceAdminReceiver;
import android.service.notification.NotificationListenerService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.dangerous.StandardDangerousPermission;
import com.hjq.permissions.permission.dangerous.AccessBackgroundLocationPermission;
import com.hjq.permissions.permission.dangerous.AccessMediaLocationPermission;
import com.hjq.permissions.permission.dangerous.BluetoothAdvertisePermission;
import com.hjq.permissions.permission.dangerous.BluetoothConnectPermission;
import com.hjq.permissions.permission.dangerous.BluetoothScanPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsBackgroundPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsPermission;
import com.hjq.permissions.permission.dangerous.GetInstalledAppsPermission;
import com.hjq.permissions.permission.dangerous.StandardFitnessAndWellnessDataPermission;
import com.hjq.permissions.permission.dangerous.NearbyWifiDevicesPermission;
import com.hjq.permissions.permission.dangerous.PostNotificationsPermission;
import com.hjq.permissions.permission.dangerous.ReadExternalStoragePermission;
import com.hjq.permissions.permission.dangerous.ReadHealthDataHistoryPermission;
import com.hjq.permissions.permission.dangerous.ReadHealthDataInBackgroundPermission;
import com.hjq.permissions.permission.dangerous.ReadHealthRatePermission;
import com.hjq.permissions.permission.dangerous.ReadMediaAudioPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaImagesPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVideoPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVisualUserSelectedPermission;
import com.hjq.permissions.permission.dangerous.ReadPhoneNumbersPermission;
import com.hjq.permissions.permission.dangerous.StandardHealthRecordsPermission;
import com.hjq.permissions.permission.dangerous.WriteExternalStoragePermission;
import com.hjq.permissions.permission.special.AccessNotificationPolicyPermission;
import com.hjq.permissions.permission.special.BindAccessibilityServicePermission;
import com.hjq.permissions.permission.special.BindDeviceAdminPermission;
import com.hjq.permissions.permission.special.BindNotificationListenerServicePermission;
import com.hjq.permissions.permission.special.BindVpnServicePermission;
import com.hjq.permissions.permission.special.ManageExternalStoragePermission;
import com.hjq.permissions.permission.special.NotificationServicePermission;
import com.hjq.permissions.permission.special.PackageUsageStatsPermission;
import com.hjq.permissions.permission.special.PictureInPicturePermission;
import com.hjq.permissions.permission.special.RequestIgnoreBatteryOptimizationsPermission;
import com.hjq.permissions.permission.special.RequestInstallPackagesPermission;
import com.hjq.permissions.permission.special.ScheduleExactAlarmPermission;
import com.hjq.permissions.permission.special.SystemAlertWindowPermission;
import com.hjq.permissions.permission.special.UseFullScreenIntentPermission;
import com.hjq.permissions.permission.special.WriteSettingsPermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限和特殊权限清单，参考 {@link Manifest.permission}
 *    doc    : https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn
 *             https://developer.android.google.cn/reference/android/health/connect/HealthPermissions
 *             https://developer.android.google.cn/guide/topics/permissions/overview?hl=zh-cn#normal-dangerous
 *             http://www.taf.org.cn/upload/AssociationStandard/TTAF%20004-2017%20Android%E6%9D%83%E9%99%90%E8%B0%83%E7%94%A8%E5%BC%80%E5%8F%91%E8%80%85%E6%8C%87%E5%8D%97.pdf
 */
public final class PermissionLists {

    private PermissionLists() {}

    /** 权限数量 */
    private static final int PERMISSION_COUNT = 151;

    /**
     * 权限对象缓存集合
     *
     * 这里解释一下为什么将 IPermission 对象缓存到集合中？而不是定义成静态变量或者常量？有几个原因：
     *
     * 1. 如果直接定义成常量或静态变量，会有一个问题，如果项目开启混淆模式（minifyEnabled = true）情况下，
     *    未使用到的常量或者静态变量仍然会被保留，不知道 Android Studio 为什么要那么做，但是问题终归还是个问题
     *    目前我能找到的最好的解决方式是定义成静态方法，后续如果静态方法没有被调用，后续代码混淆的时候就会被剔除掉。
     * 2. 如果直接定义成常量或静态变量，还有另外一个问题，就是一旦有谁第一次访问到本类，就会初始化很多对象，
     *    不管这个权限有没有用到，都会在第一次访问的时候初始化完，这样对性能其实不太好的，虽然这点性能微不足道，
     *    但是本着能省一点是一点的原则，所以搞了一个静态集合来存放这些权限对象，调用的时候发现没有再去创建。
     */
    private static final LruCache<String, IPermission> PERMISSION_CACHE_MAP = new LruCache<>(PERMISSION_COUNT);

    /**
     * 获取缓存的权限对象
     *
     * @param permissionName            权限名称
     */
    @Nullable
    private static IPermission getCachePermission(@NonNull String permissionName) {
        return PERMISSION_CACHE_MAP.get(permissionName);
    }

    /**
     * 添加权限对象到缓存中
     *
     * @param permission                权限对象
     */
    private static IPermission putCachePermission(@NonNull IPermission permission) {
        PERMISSION_CACHE_MAP.put(permission.getPermissionName(), permission);
        return permission;
    }

    /**
     * 读取应用列表权限（危险权限，电信终端产业协会联合各大中国手机厂商搞的一个权限）
     *
     * Github issue 地址：https://github.com/getActivity/XXPermissions/issues/175
     * 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/StdDetail.aspx?uid=3A7D6656-43B8-4C46-8871-E379A3EA1D48&stdType=TAF
     *
     * 需要注意的是：
     *   1. 需要在清单文件中注册 QUERY_ALL_PACKAGES 权限或者注册 <queries> 节点，否则在 Android 11 上面就算申请成功也是获取不到已安装应用列表的
     *   2. 这个权限在有的手机上面是授予状态，在有的手机上面是还没有授予，在有的手机上面是无法申请，要看手机厂商是否支持这个权限，支持了不一定默认授予
     *   3. 如果你贪图方便在清单文件中注册了 QUERY_ALL_PACKAGES 权限，并且 App 需要上架 GooglePlay，注意看一下 GooglePlay 的政策：
     *      https://support.google.com/googleplay/android-developer/answer/9888170?hl=zh-Hans
     */
    @NonNull
    public static IPermission getGetInstalledAppsPermission() {
        IPermission permission = getCachePermission(GetInstalledAppsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new GetInstalledAppsPermission());
    }

    /**
     * 全屏通知权限（特殊权限，Android 14 新增的权限）
     *
     * 需要注意的是，如果你的应用需要上架 GooglePlay，请慎重添加此权限，相关文档介绍如下：
     * 1. 了解前台服务和全屏 intent 要求：https://support.google.com/googleplay/android-developer/answer/13392821?hl=zh-Hans
     * 2. GooglePlay 对 Android 14 全屏 Intent 的要求：https://orangeoma.zendesk.com/hc/en-us/articles/14126775576988-Google-Play-requirements-on-Full-screen-intent-for-Android-14
     */
    @NonNull
    public static IPermission getUseFullScreenIntentPermission() {
        IPermission permission = getCachePermission(UseFullScreenIntentPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new UseFullScreenIntentPermission());
    }

    /**
     * 闹钟权限（特殊权限，Android 12 新增的权限）
     *
     * 需要注意的是：这个权限和其他特殊权限不同的是，默认已经是授予状态，用户也可以手动撤销授权
     * 官方文档介绍：https://developer.android.google.cn/about/versions/12/behavior-changes-12?hl=zh_cn#exact-alarm-permission
     * 应用只有在其核心功能支持精确闹钟需求的情况下才能声明此权限。请求此受限权限的应用需要接受审核；如果应用不符合可接受的用例标准，则不允许在 GooglePlay 上发布
     * 查看 GooglePlay 对闹钟权限的要求：https://support.google.com/googleplay/android-developer/answer/9888170?hl=zh-Hans
     */
    @NonNull
    public static IPermission getScheduleExactAlarmPermission() {
        IPermission permission = getCachePermission(ScheduleExactAlarmPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ScheduleExactAlarmPermission());
    }

    /**
     * 所有文件访问权限（特殊权限，Android 11 新增的权限）
     *
     * 为了兼容 Android 11 以下版本，需要在清单文件中注册
     * {@link PermissionNames#READ_EXTERNAL_STORAGE} 和 {@link PermissionNames#WRITE_EXTERNAL_STORAGE} 权限
     *
     * 如果你的应用需要上架 GooglePlay，那么需要详细阅读谷歌应用商店的政策：
     * https://support.google.com/googleplay/android-developer/answer/9956427
     */
    @NonNull
    public static IPermission getManageExternalStoragePermission() {
        IPermission permission = getCachePermission(ManageExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ManageExternalStoragePermission());
    }

    /**
     * 安装应用权限（特殊权限，Android 8.0 新增的权限）
     *
     * Android 11 特性调整，安装外部来源应用需要重启 App：https://cloud.tencent.com/developer/news/637591
     * 经过实践，Android 12 已经修复了此问题，授权或者取消授权后应用并不会重启
     */
    @NonNull
    public static IPermission getRequestInstallPackagesPermission() {
        IPermission permission = getCachePermission(RequestInstallPackagesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new RequestInstallPackagesPermission());
    }

    /**
     * 画中画权限（特殊权限，Android 8.0 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     *
     * 需要注意的是：这个权限和其他特殊权限不同的是，默认已经是授予状态，用户也可以手动撤销授权
     */
    @NonNull
    public static IPermission getPictureInPicturePermission() {
        IPermission permission = getCachePermission(PictureInPicturePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PictureInPicturePermission());
    }

    /**
     * 悬浮窗权限（特殊权限，Android 6.0 新增的权限，但是有些国产的厂商在 Android 6.0 之前的设备就兼容了）
     *
     * 在 Android 10 及之前的版本能跳转到应用悬浮窗设置页面，而在 Android 11 及之后的版本只能跳转到系统设置悬浮窗管理列表了
     * 官方解释：https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
     */
    @NonNull
    public static IPermission getSystemAlertWindowPermission() {
        IPermission permission = getCachePermission(SystemAlertWindowPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new SystemAlertWindowPermission());
    }

    /**
     * 写入系统设置权限（特殊权限，Android 6.0 新增的权限）
     */
    @SuppressWarnings("unused")
    @NonNull
    public static IPermission getWriteSettingsPermission() {
        IPermission permission = getCachePermission(WriteSettingsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new WriteSettingsPermission());
    }

    /**
     * 请求忽略电池优化选项权限（特殊权限，Android 6.0 新增的权限）
     */
    @NonNull
    public static IPermission getRequestIgnoreBatteryOptimizationsPermission() {
        IPermission permission = getCachePermission(RequestIgnoreBatteryOptimizationsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new RequestIgnoreBatteryOptimizationsPermission());
    }

    /**
     * 勿扰权限，可控制手机响铃模式【静音，震动】（特殊权限，Android 6.0 新增的权限）
     */
    @NonNull
    public static IPermission getAccessNotificationPolicyPermission() {
        IPermission permission = getCachePermission(AccessNotificationPolicyPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessNotificationPolicyPermission());
    }

    /**
     * 查看应用使用情况权限，简称使用统计权限（特殊权限，Android 5.0 新增的权限）
     */
    @NonNull
    public static IPermission getPackageUsageStatsPermission() {
        IPermission permission = getCachePermission(PackageUsageStatsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PackageUsageStatsPermission());
    }

    /**
     * 通知栏监听权限（特殊权限，Android 4.3 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     *
     * @param notificationListenerServiceClass             通知监听器的 Service 类型
     */
    @NonNull
    public static IPermission getBindNotificationListenerServicePermission(@NonNull Class<? extends NotificationListenerService> notificationListenerServiceClass) {
        // 该对象不会纳入到缓存的集合中，这是它携带了具体的参数，只有无参的才能丢到缓存的集合中
        return new BindNotificationListenerServicePermission(notificationListenerServiceClass);
    }

    /**
     * VPN 权限（特殊权限，Android 4.0 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     */
    @NonNull
    public static IPermission getBindVpnServicePermission() {
        IPermission permission = getCachePermission(BindVpnServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BindVpnServicePermission());
    }

    /**
     * 通知栏权限（特殊权限，只有 Android 4.4 及以上设备才能判断到权限状态，注意此权限不需要在清单文件中注册也能申请）
     *
     * @param channelId         通知渠道 id
     */
    @NonNull
    public static IPermission getNotificationServicePermission(@NonNull String channelId) {
        // 该对象不会纳入到缓存的集合中，这是它携带了具体的参数，只有无参的才能丢到缓存的集合中
        return new NotificationServicePermission(channelId);
    }

    /**
     * 同上
     */
    @NonNull
    public static IPermission getNotificationServicePermission() {
        IPermission permission = getCachePermission(NotificationServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NotificationServicePermission());
    }

    /**
     * 无障碍服务权限（特殊权限，Android 4.1 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     *
     * @param accessibilityServiceClass                                 无障碍 Service 类
     */
    @NonNull
    public static IPermission getBindAccessibilityServicePermission(@NonNull Class<? extends AccessibilityService> accessibilityServiceClass) {
        return new BindAccessibilityServicePermission(accessibilityServiceClass);
    }

    /**
     * 设备管理权限（特殊权限，Android 2.2 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     *
     * @param deviceAdminReceiverClass              设备管理器的 BroadcastReceiver 类
     * @param extraAddExplanation                   申请设备管理器权限的附加说明
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass, @Nullable String extraAddExplanation) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, extraAddExplanation);
    }

    /**
     * 同上
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, null);
    }

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 访问部分照片和视频的权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMediaVisualUserSelectedPermission() {
        IPermission permission = getCachePermission(ReadMediaVisualUserSelectedPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaVisualUserSelectedPermission());
    }

    /**
     * 发送通知权限（Android 13.0 新增的权限）
     *
     * 为了向下兼容，框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getNotificationServicePermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getPostNotificationsPermission() {
        IPermission permission = getCachePermission(PostNotificationsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PostNotificationsPermission());
    }

    /**
     * WIFI 权限（Android 13.0 新增的权限）
     *
     * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
     * 否则就会导致在没有定位权限的情况下扫描不到附近的 WIFI 设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
     * <uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * 为了兼容 Android 13 以下版本，需要清单文件中注册 {@link PermissionNames#ACCESS_FINE_LOCATION} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getAccessFineLocationPermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getNearbyWifiDevicesPermission() {
        IPermission permission = getCachePermission(NearbyWifiDevicesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NearbyWifiDevicesPermission());
    }

    /**
     * 后台传感器权限（Android 13.0 新增的权限）
     *
     * 需要注意的是：
     * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
     * 2. 如果你的 App 只在前台状态下使用传感器功能，请不要申请该权限（后台传感器权限）
     */
    @NonNull
    public static IPermission getBodySensorsBackgroundPermission() {
        IPermission permission = getCachePermission(BodySensorsBackgroundPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BodySensorsBackgroundPermission());
    }

    /**
     * 读取图片权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link PermissionNames#READ_EXTERNAL_STORAGE} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getReadExternalStoragePermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadMediaImagesPermission() {
        IPermission permission = getCachePermission(ReadMediaImagesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaImagesPermission());
    }

    /**
     * 读取视频权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link PermissionNames#READ_EXTERNAL_STORAGE} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getReadExternalStoragePermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadMediaVideoPermission() {
        IPermission permission = getCachePermission(ReadMediaVideoPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaVideoPermission());
    }

    /**
     * 读取音频权限（Android 13.0 新增的权限）
     *
     * 为了兼容 Android 13 以下版本，需要在清单文件中注册 {@link PermissionNames#READ_EXTERNAL_STORAGE} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getReadExternalStoragePermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadMediaAudioPermission() {
        IPermission permission = getCachePermission(ReadMediaAudioPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaAudioPermission());
    }

    /**
     * 蓝牙扫描权限（Android 12.0 新增的权限）
     *
     * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
     * 否则就会导致在没有定位权限的情况下扫描不到附近的蓝牙设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
     * <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * 为了兼容 Android 12 以下版本，需要清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 和 {@link PermissionNames#ACCESS_FINE_LOCATION} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getAccessFineLocationPermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getBluetoothScanPermission() {
        IPermission permission = getCachePermission(BluetoothScanPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothScanPermission());
    }

    /**
     * 蓝牙连接权限（Android 12.0 新增的权限）
     *
     * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH} 权限
     */
    @NonNull
    public static IPermission getBluetoothConnectPermission() {
        IPermission permission = getCachePermission(BluetoothConnectPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothConnectPermission());
    }

    /**
     * 蓝牙广播权限（Android 12.0 新增的权限）
     *
     * 将当前设备的蓝牙进行广播，供其他设备扫描时需要用到该权限
     * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
     */
    @NonNull
    public static IPermission getBluetoothAdvertisePermission() {
        IPermission permission = getCachePermission(BluetoothAdvertisePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothAdvertisePermission());
    }

    /**
     * 在后台获取位置权限（Android 10.0 新增的权限）
     *
     * 需要注意的是：
     * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
     * 2. 如果你的 App 只在前台状态下使用定位功能，没有在后台使用的场景，请不要申请该权限
     */
    @NonNull
    public static IPermission getAccessBackgroundLocationPermission() {
        IPermission permission = getCachePermission(AccessBackgroundLocationPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessBackgroundLocationPermission());
    }

    /**
     * 获取活动步数权限（Android 10.0 新增的权限）
     *
     * 需要注意的是：Android 10 以下不需要传感器（BODY_SENSORS）权限也能获取到步数
     * Github issue 地址：https://github.com/getActivity/XXPermissions/issues/150
     */
    @NonNull
    public static IPermission getActivityRecognitionPermission() {
        String permissionName = PermissionNames.ACTIVITY_RECOGNITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_10));
    }

    /**
     * 访问媒体的位置信息权限（Android 10.0 新增的权限）
     *
     * 需要注意的是：如果这个权限申请成功了但是不能正常读取照片的地理信息，那么需要先申请存储权限，具体可分别下面两种情况：
     *
     * 1. 如果适配了分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 32 需要申请 {@link PermissionLists#getReadExternalStoragePermission()}
     *     2) 如果项目 targetSdkVersion >= 33 需要申请 {@link PermissionLists#getReadMediaImagesPermission()} 或者
     *        {@link PermissionLists#getReadMediaVideoPermission()}，并且需要授予全部，不能选择授予部分
     *
     * 2. 如果没有适配分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 29 需要申请 {@link PermissionLists#getReadExternalStoragePermission()}
     *     2) 如果项目 targetSdkVersion >= 30 需要申请 {@link PermissionLists#getManageExternalStoragePermission()}
     */
    @NonNull
    public static IPermission getAccessMediaLocationPermission() {
        IPermission permission = getCachePermission(AccessMediaLocationPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessMediaLocationPermission());
    }

    /**
     * 允许呼叫应用继续在另一个应用中启动的呼叫权限（Android 9.0 新增的权限）
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getAcceptHandoverPermission() {
        String permissionName = PermissionNames.ACCEPT_HANDOVER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_9));
    }

    /**
     * 读取手机号码权限（Android 8.0 新增的权限）
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调成功，但是这非必然，如有进行申请，还需留意处理权限申请失败的情况
     *
     * 为了兼容 Android 8.0 以下版本，需要在清单文件中注册 {@link PermissionNames#READ_PHONE_STATE} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getReadPhoneStatePermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadPhoneNumbersPermission() {
        IPermission permission = getCachePermission(ReadPhoneNumbersPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadPhoneNumbersPermission());
    }

    /**
     * 接听电话权限（Android 8.0 新增的权限，Android 8.0 以下可以采用模拟耳机按键事件来实现接听电话，这种方式不需要权限）
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getAnswerPhoneCallsPermission() {
        String permissionName = PermissionNames.ANSWER_PHONE_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_8));
    }

    /**
     * 读取外部存储权限
     */
    @NonNull
    public static IPermission getReadExternalStoragePermission() {
        IPermission permission = getCachePermission(ReadExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadExternalStoragePermission());
    }

    /**
     * 写入外部存储权限（注意：这个权限在 targetSdk >= Android 11 并且 Android 11 及以上的设备上面不起作用，请适配分区存储特性代替权限申请）
     */
    @NonNull
    public static IPermission getWriteExternalStoragePermission() {
        IPermission permission = getCachePermission(WriteExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new WriteExternalStoragePermission());
    }

    /**
     * 相机权限
     */
    @NonNull
    public static IPermission getCameraPermission() {
        String permissionName = PermissionNames.CAMERA;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_6));
    }

    /**
     * 麦克风权限
     */
    @NonNull
    public static IPermission getRecordAudioPermission() {
        String permissionName = PermissionNames.RECORD_AUDIO;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_6));
    }

    /**
     * 获取精确位置权限
     */
    @NonNull
    public static IPermission getAccessFineLocationPermission() {
        String permissionName = PermissionNames.ACCESS_FINE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.LOCATION, PermissionVersion.ANDROID_6));
    }

    /**
     * 获取粗略位置权限
     */
    @NonNull
    public static IPermission getAccessCoarseLocationPermission() {
        String permissionName = PermissionNames.ACCESS_COARSE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.LOCATION, PermissionVersion.ANDROID_6));
    }

    /**
     * 读取联系人权限
     */
    @NonNull
    public static IPermission getReadContactsPermission() {
        String permissionName = PermissionNames.READ_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * 修改联系人权限
     */
    @NonNull
    public static IPermission getWriteContactsPermission() {
        String permissionName = PermissionNames.WRITE_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * 访问账户列表权限
     */
    @NonNull
    public static IPermission getGetAccountsPermission() {
        String permissionName = PermissionNames.GET_ACCOUNTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * 读取日历权限
     */
    @NonNull
    public static IPermission getReadCalendarPermission() {
        String permissionName = PermissionNames.READ_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CALENDAR, PermissionVersion.ANDROID_6));
    }

    /**
     * 修改日历权限
     */
    @NonNull
    public static IPermission getWriteCalendarPermission() {
        String permissionName = PermissionNames.WRITE_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CALENDAR, PermissionVersion.ANDROID_6));
    }

    /**
     * 读取电话状态权限，需要注意的是：
     *
     * 1. 这个权限在某些手机上面是没办法获取到的，因为某些系统禁止应用获得该权限
     *    所以你要是申请了这个权限之后没有弹授权框，而是直接回调授权失败方法
     *    请不要惊慌，这个不是 Bug、不是 Bug、不是 Bug，而是正常现象
     *    后续情况汇报：有人反馈在 iQOO 手机上面获取不到该权限，在清单文件加入下面这个权限就可以了（这里只是做记录，并不代表这种方式就一定有效果）
     *    <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     *    Github issue 地址：https://github.com/getActivity/XXPermissions/issues/98
     *
     * 2. 这个权限在某些手机上面申请是直接通过的，但是系统没有弹出授权对话框，实际上也是没有授权
     *    这个也不是 Bug，而是系统故意就是这么做的，你要问我怎么办，我只能说胳膊拗不过大腿
     *    Github issue 地址：https://github.com/getActivity/XXPermissions/issues/369
     */
    @NonNull
    public static IPermission getReadPhoneStatePermission() {
        String permissionName = PermissionNames.READ_PHONE_STATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * 拨打电话权限
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getCallPhonePermission() {
        String permissionName = PermissionNames.CALL_PHONE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * 读取通话记录权限
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getReadCallLogPermission() {
        String permissionName = PermissionNames.READ_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * 修改通话记录权限
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getWriteCallLogPermission() {
        String permissionName = PermissionNames.WRITE_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * 添加语音邮件权限
     */
    @NonNull
    public static IPermission getAddVoicemailPermission() {
        String permissionName = PermissionNames.ADD_VOICEMAIL;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * 使用 SIP 视频权限
     */
    @NonNull
    public static IPermission getUseSipPermission() {
        String permissionName = PermissionNames.USE_SIP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * 处理拨出电话权限
     *
     * 需要注意：此权限在一些无法拨打电话的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     *
     * @deprecated         在 Android 10 已经过时，请见：https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS
     */
    @NonNull
    public static IPermission getProcessOutgoingCallsPermission() {
        String permissionName = PermissionNames.PROCESS_OUTGOING_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * 使用传感器权限
     */
    @NonNull
    public static IPermission getBodySensorsPermission() {
        IPermission permission = getCachePermission(BodySensorsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BodySensorsPermission());
    }

    /**
     * 发送短信权限
     *
     * 需要注意：此权限在一些无法发送短信的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getSendSmsPermission() {
        String permissionName = PermissionNames.SEND_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * 接收短信权限
     *
     * 需要注意：此权限在一些无法发送短信的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getReceiveSmsPermission() {
        String permissionName = PermissionNames.RECEIVE_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * 读取短信权限
     *
     * 需要注意：此权限在一些无法发送短信的设备（例如：小米平板 5）上面申请，系统会直接回调失败，如有进行申请，请留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getReadSmsPermission() {
        String permissionName = PermissionNames.READ_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * 接收 WAP 推送消息权限
     *
     * 需要注意：此权限在一些无法发送短信的设备（例如：小米平板 5）上面申请，系统会直接回调成功，但是这非必然，如有进行申请，还需留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getReceiveWapPushPermission() {
        String permissionName = PermissionNames.RECEIVE_WAP_PUSH;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * 接收彩信权限
     *
     * 需要注意：此权限在一些无法发送短信的设备（例如：小米平板 5）上面申请，系统会直接回调成功，但是这非必然，如有进行申请，还需留意处理权限申请失败的情况
     */
    @NonNull
    public static IPermission getReceiveMmsPermission() {
        String permissionName = PermissionNames.RECEIVE_MMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 获取后台读取健康数据权限（Android 15.0 新增的权限）
     *
     * 为了兼容 Android 15 以下版本，需要在清单文件中注册 {@link PermissionNames#BODY_SENSORS_BACKGROUND} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getReadHealthDataInBackgroundPermission()} ()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadHealthDataInBackgroundPermission() {
        String permissionName = ReadHealthDataInBackgroundPermission.PERMISSION_NAME;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthDataInBackgroundPermission());
    }

    /**
     * 获取读取历史的健康数据权限（Android 15.0 新增的权限）
     *
     * Health Connect 可以读取授予权限前最多 30 天的数据。如果您希望应用读取 30 天之前的记录，请需要申请此权限，相关文档地址：
     * https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/read-data?hl=zh-cn#read-older-data
     */
    @NonNull
    public static IPermission getReadHealthDataHistoryPermission() {
        String permissionName = ReadHealthDataHistoryPermission.PERMISSION_NAME;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthDataHistoryPermission());
    }

    /**
     * 获取读取运动消耗的卡路里数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadActiveCaloriesBurnedPermission() {
        String permissionName = PermissionNames.READ_ACTIVE_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入运动消耗的卡路里数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteActiveCaloriesBurnedPermission() {
        String permissionName = PermissionNames.WRITE_ACTIVE_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取活动强度数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadActivityIntensityPermission() {
        String permissionName = PermissionNames.READ_ACTIVITY_INTENSITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取写入活动强度数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteActivityIntensityPermission() {
        String permissionName = PermissionNames.WRITE_ACTIVITY_INTENSITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取基础体温数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBasalBodyTemperaturePermission() {
        String permissionName = PermissionNames.READ_BASAL_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入基础体温数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBasalBodyTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_BASAL_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取基础代谢率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBasalMetabolicRatePermission() {
        String permissionName = PermissionNames.READ_BASAL_METABOLIC_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入基础代谢率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBasalMetabolicRatePermission() {
        String permissionName = PermissionNames.WRITE_BASAL_METABOLIC_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取血糖数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBloodGlucosePermission() {
        String permissionName = PermissionNames.READ_BLOOD_GLUCOSE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入血糖数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBloodGlucosePermission() {
        String permissionName = PermissionNames.WRITE_BLOOD_GLUCOSE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取血压数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBloodPressurePermission() {
        String permissionName = PermissionNames.READ_BLOOD_PRESSURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入血压数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBloodPressurePermission() {
        String permissionName = PermissionNames.WRITE_BLOOD_PRESSURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取体脂数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBodyFatPermission() {
        String permissionName = PermissionNames.READ_BODY_FAT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入体脂数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBodyFatPermission() {
        String permissionName = PermissionNames.WRITE_BODY_FAT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取体温数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBodyTemperaturePermission() {
        String permissionName = PermissionNames.READ_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入体温数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBodyTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取身体含水量数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBodyWaterMassPermission() {
        String permissionName = PermissionNames.READ_BODY_WATER_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入身体含水量数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBodyWaterMassPermission() {
        String permissionName = PermissionNames.WRITE_BODY_WATER_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取骨质密度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadBoneMassPermission() {
        String permissionName = PermissionNames.READ_BONE_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入骨质密度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteBoneMassPermission() {
        String permissionName = PermissionNames.WRITE_BONE_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取宫颈粘液数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadCervicalMucusPermission() {
        String permissionName = PermissionNames.READ_CERVICAL_MUCUS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入宫颈粘液数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteCervicalMucusPermission() {
        String permissionName = PermissionNames.WRITE_CERVICAL_MUCUS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取距离数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadDistancePermission() {
        String permissionName = PermissionNames.READ_DISTANCE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入距离数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteDistancePermission() {
        String permissionName = PermissionNames.WRITE_DISTANCE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取爬升高度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadElevationGainedPermission() {
        String permissionName = PermissionNames.READ_ELEVATION_GAINED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入爬升高度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteElevationGainedPermission() {
        String permissionName = PermissionNames.WRITE_ELEVATION_GAINED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取锻炼数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadExercisePermission() {
        String permissionName = PermissionNames.READ_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入锻炼数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteExercisePermission() {
        String permissionName = PermissionNames.WRITE_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取锻炼路线数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadExerciseRoutesPermission() {
        String permissionName = PermissionNames.READ_EXERCISE_ROUTES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取写入锻炼路线数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteExerciseRoutePermission() {
        String permissionName = PermissionNames.WRITE_EXERCISE_ROUTE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取读取爬楼层数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadFloorsClimbedPermission() {
        String permissionName = PermissionNames.READ_FLOORS_CLIMBED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入爬楼层数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteFloorsClimbedPermission() {
        String permissionName = PermissionNames.WRITE_FLOORS_CLIMBED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取心率数据权限（Android 14.0 新增的权限）
     *
     * 为了兼容 Android 14 以下版本，需要在清单文件中注册 {@link PermissionNames#BODY_SENSORS} 权限
     * 另外框架会自动在旧的安卓设备上自动添加 {@link PermissionLists#getBodySensorsPermission()} 权限进行动态申请，无需你手动添加
     */
    @NonNull
    public static IPermission getReadHeartRatePermission() {
        IPermission permission = getCachePermission(ReadHealthRatePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthRatePermission());
    }

    /**
     * 获取写入心率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteHeartRatePermission() {
        String permissionName = PermissionNames.WRITE_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取心率变异性数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadHeartRateVariabilityPermission() {
        String permissionName = PermissionNames.READ_HEART_RATE_VARIABILITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入心率变异性数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteHeartRateVariabilityPermission() {
        String permissionName = PermissionNames.WRITE_HEART_RATE_VARIABILITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取身高数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadHeightPermission() {
        String permissionName = PermissionNames.READ_HEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入身高数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteHeightPermission() {
        String permissionName = PermissionNames.WRITE_HEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取饮水量权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadHydrationPermission() {
        String permissionName = PermissionNames.READ_HYDRATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入饮水量权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteHydrationPermission() {
        String permissionName = PermissionNames.WRITE_HYDRATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取点状出血数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadIntermenstrualBleedingPermission() {
        String permissionName = PermissionNames.READ_INTERMENSTRUAL_BLEEDING;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入点状出血数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteIntermenstrualBleedingPermission() {
        String permissionName = PermissionNames.WRITE_INTERMENSTRUAL_BLEEDING;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取净体重数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadLeanBodyMassPermission() {
        String permissionName = PermissionNames.READ_LEAN_BODY_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入净体重数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteLeanBodyMassPermission() {
        String permissionName = PermissionNames.WRITE_LEAN_BODY_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取经期数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMenstruationPermission() {
        String permissionName = PermissionNames.READ_MENSTRUATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入经期数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteMenstruationPermission() {
        String permissionName = PermissionNames.WRITE_MENSTRUATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取正念数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMindfulnessPermission() {
        String permissionName = PermissionNames.READ_MINDFULNESS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取写入正念数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteMindfulnessPermission() {
        String permissionName = PermissionNames.WRITE_MINDFULNESS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取营养数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadNutritionPermission() {
        String permissionName = PermissionNames.READ_NUTRITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入营养数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteNutritionPermission() {
        String permissionName = PermissionNames.WRITE_NUTRITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取排卵检测数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadOvulationTestPermission() {
        String permissionName = PermissionNames.READ_OVULATION_TEST;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入排卵检测数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteOvulationTestPermission() {
        String permissionName = PermissionNames.WRITE_OVULATION_TEST;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取血氧饱和度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadOxygenSaturationPermission() {
        String permissionName = PermissionNames.READ_OXYGEN_SATURATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入血氧饱和度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteOxygenSaturationPermission() {
        String permissionName = PermissionNames.WRITE_OXYGEN_SATURATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取训练计划数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadPlannedExercisePermission() {
        String permissionName = PermissionNames.READ_PLANNED_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取写入训练计划数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getWritePlannedExercisePermission() {
        String permissionName = PermissionNames.WRITE_PLANNED_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取读取体能数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadPowerPermission() {
        String permissionName = PermissionNames.READ_POWER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入体能数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWritePowerPermission() {
        String permissionName = PermissionNames.WRITE_POWER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取呼吸频率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadRespiratoryRatePermission() {
        String permissionName = PermissionNames.READ_RESPIRATORY_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入呼吸频率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteRespiratoryRatePermission() {
        String permissionName = PermissionNames.WRITE_RESPIRATORY_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取静息心率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadRestingHeartRatePermission() {
        String permissionName = PermissionNames.READ_RESTING_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入静息心率数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteRestingHeartRatePermission() {
        String permissionName = PermissionNames.WRITE_RESTING_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取性活动数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadSexualActivityPermission() {
        String permissionName = PermissionNames.READ_SEXUAL_ACTIVITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入性活动数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteSexualActivityPermission() {
        String permissionName = PermissionNames.WRITE_SEXUAL_ACTIVITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取体表温度数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadSkinTemperaturePermission() {
        String permissionName = PermissionNames.READ_SKIN_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取写入体表温度数据权限（Android 15.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteSkinTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_SKIN_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * 获取读取睡眠数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadSleepPermission() {
        String permissionName = PermissionNames.READ_SLEEP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入睡眠数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteSleepPermission() {
        String permissionName = PermissionNames.WRITE_SLEEP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取速度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadSpeedPermission() {
        String permissionName = PermissionNames.READ_SPEED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入速度数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteSpeedPermission() {
        String permissionName = PermissionNames.WRITE_SPEED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取步数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadStepsPermission() {
        String permissionName = PermissionNames.READ_STEPS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入步数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteStepsPermission() {
        String permissionName = PermissionNames.WRITE_STEPS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取消耗的卡路里总数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadTotalCaloriesBurnedPermission() {
        String permissionName = PermissionNames.READ_TOTAL_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入消耗的卡路里总数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteTotalCaloriesBurnedPermission() {
        String permissionName = PermissionNames.WRITE_TOTAL_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取最大摄氧量数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadVo2MaxPermission() {
        String permissionName = PermissionNames.READ_VO2_MAX;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入最大摄氧量数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteVo2MaxPermission() {
        String permissionName = PermissionNames.WRITE_VO2_MAX;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取体重数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadWeightPermission() {
        String permissionName = PermissionNames.READ_WEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入体重数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteWeightPermission() {
        String permissionName = PermissionNames.WRITE_WEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取读取推轮椅次数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadWheelchairPushesPermission() {
        String permissionName = PermissionNames.READ_WHEELCHAIR_PUSHES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * 获取写入推轮椅次数数据权限（Android 14.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteWheelchairPushesPermission() {
        String permissionName = PermissionNames.WRITE_WHEELCHAIR_PUSHES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 获取读取过敏反应数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataAllergiesIntolerancesPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取病症数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataConditionsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_CONDITIONS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取化验结果数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataLaboratoryResultsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_LABORATORY_RESULTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取用药情况数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataMedicationsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_MEDICATIONS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取个人详细信息数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataPersonalDetailsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PERSONAL_DETAILS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取就医情况数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataPractitionerDetailsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PRACTITIONER_DETAILS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取怀孕情况数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataPregnancyPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PREGNANCY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取医疗程序数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataProceduresPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PROCEDURES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取个人生活史数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataSocialHistoryPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_SOCIAL_HISTORY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取疫苗接种数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataVaccinesPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VACCINES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取医师详细信息数据权限，包括地点、预约时间以及就诊组织名称等数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataVisitsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VISITS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取读取生命体征数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getReadMedicalDataVitalSignsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VITAL_SIGNS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * 获取写入所有健康记录数据权限（Android 16.0 新增的权限）
     */
    @NonNull
    public static IPermission getWriteMedicalDataPermission() {
        String permissionName = PermissionNames.WRITE_MEDICAL_DATA;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }
}