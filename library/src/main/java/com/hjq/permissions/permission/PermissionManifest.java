package com.hjq.permissions.permission;

import android.Manifest;
import android.app.Service;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.StandardDangerousPermission;
import com.hjq.permissions.permission.dangerous.AccessBackgroundLocationPermission;
import com.hjq.permissions.permission.dangerous.BluetoothAdvertisePermission;
import com.hjq.permissions.permission.dangerous.BluetoothConnectPermission;
import com.hjq.permissions.permission.dangerous.BluetoothScanPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsBackgroundPermission;
import com.hjq.permissions.permission.dangerous.GetInstalledAppsPermission;
import com.hjq.permissions.permission.dangerous.MediaLocationPermission;
import com.hjq.permissions.permission.dangerous.NearbyWifiDevicesPermission;
import com.hjq.permissions.permission.dangerous.PostNotificationsPermission;
import com.hjq.permissions.permission.dangerous.ReadExternalStoragePermission;
import com.hjq.permissions.permission.dangerous.ReadMediaAudioPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaImagesPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVideoPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVisualUserSelectedPermission;
import com.hjq.permissions.permission.dangerous.ReadPhoneNumbersPermission;
import com.hjq.permissions.permission.dangerous.WriteExternalStoragePermission;
import com.hjq.permissions.permission.special.ManageExternalStoragePermission;
import com.hjq.permissions.permission.special.NotificationListenerServicePermission;
import com.hjq.permissions.permission.special.NotificationPolicyPermission;
import com.hjq.permissions.permission.special.NotificationServicePermission;
import com.hjq.permissions.permission.special.PackageUsageStatsPermission;
import com.hjq.permissions.permission.special.PictureInPicturePermission;
import com.hjq.permissions.permission.special.RequestIgnoreBatteryOptimizationsPermission;
import com.hjq.permissions.permission.special.RequestInstallPackagesPermission;
import com.hjq.permissions.permission.special.ScheduleExactAlarmPermission;
import com.hjq.permissions.permission.special.SystemAlertWindowPermission;
import com.hjq.permissions.permission.special.UseFullScreenIntentPermission;
import com.hjq.permissions.permission.special.VpnServicePermission;
import com.hjq.permissions.permission.special.WriteSettingsPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限和特殊权限清单，参考 {@link Manifest.permission}
 *    doc    : https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn
 *             https://developer.android.google.cn/guide/topics/permissions/overview?hl=zh-cn#normal-dangerous
 *             http://www.taf.org.cn/upload/AssociationStandard/TTAF%20004-2017%20Android%E6%9D%83%E9%99%90%E8%B0%83%E7%94%A8%E5%BC%80%E5%8F%91%E8%80%85%E6%8C%87%E5%8D%97.pdf
 */
public final class PermissionManifest {

    private PermissionManifest() {}

    /** 权限数量 */
    private static final int PERMISSION_COUNT = 54;

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
     *   1. 需要在清单文件中注册 QUERY_ALL_PACKAGES 权限，否则在 Android 11 上面就算申请成功也是获取不到第三方安装列表信息的
     *   2. 这个权限在有的手机上面是授予状态，在有的手机上面是还没有授予，在有的手机上面是无法申请，能支持申请该权限的的厂商系统版本有：
     *      华为：Harmony 3.0.0 及以上版本，Harmony 2.0.1 实测不行
     *      荣耀：Magic UI 6.0 及以上版本，Magic UI 5.0 实测不行
     *      小米：Miui 13 及以上版本，Miui 12 实测不行，经过验证 miui 上面默认会授予此权限
     *      OPPO：(ColorOs 12 及以上版本 && Android 11+) || (ColorOs 11.1 及以上版本 && Android 12+)
     *      VIVO：虽然没有申请这个权限的通道，但是读取已安装第三方应用列表是没有问题的，没有任何限制
     *      真我：realme UI 3.0 及以上版本，realme UI 2.0 实测不行
     */
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
     * 2. Google Play 对 Android 14 全屏 Intent 的要求：https://orangeoma.zendesk.com/hc/en-us/articles/14126775576988-Google-Play-requirements-on-Full-screen-intent-for-Android-14
     */
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
     */
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
    public static IPermission getAccessNotificationPolicyPermission() {
        IPermission permission = getCachePermission(NotificationPolicyPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NotificationPolicyPermission());
    }

    /**
     * 查看应用使用情况权限，简称使用统计权限（特殊权限，Android 5.0 新增的权限）
     */
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
     * @param clazz             通知监听的 Service 类型
     */
    public static IPermission getNotificationListenerServicePermission(@Nullable Class<? extends Service> clazz) {
        // 该对象不会纳入到缓存的集合中，这是它携带了具体的参数，只有无参的才能丢到缓存的集合中
        return new NotificationListenerServicePermission(clazz);
    }

    public static IPermission getNotificationListenerServicePermission() {
        IPermission permission = getCachePermission(NotificationListenerServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NotificationListenerServicePermission());
    }

    /**
     * VPN 权限（特殊权限，Android 4.0 新增的权限，注意此权限不需要在清单文件中注册也能申请）
     */
    public static IPermission getVpnServicePermission() {
        IPermission permission = getCachePermission(VpnServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new VpnServicePermission());
    }

    /**
     * 通知栏权限（特殊权限，只有 Android 4.4 及以上设备才能判断到权限状态，注意此权限不需要在清单文件中注册也能申请）
     *
     * @param channelId         通知渠道 id
     */
    public static IPermission getNotificationServicePermission(@NonNull String channelId) {
        // 该对象不会纳入到缓存的集合中，这是它携带了具体的参数，只有无参的才能丢到缓存的集合中
        return new NotificationServicePermission(channelId);
    }

    public static IPermission getNotificationServicePermission() {
        IPermission permission = getCachePermission(NotificationServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NotificationServicePermission());
    }

    /* ------------------------------------ 我是一条华丽的分割线 ------------------------------------ */

    /**
     * 授予对照片和视频的部分访问权限（Android 14.0 新增的权限）
     */
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
     * 为了兼容 Android 13 以下版本，框架会自动申请 {@link PermissionNames#NOTIFICATION_SERVICE} 权限
     */
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
     * 还有 Android 13 以下设备，使用 WIFI 需要 {@link PermissionNames#ACCESS_FINE_LOCATION} 权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
     */
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
     */
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
     */
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
     */
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
     * 为了兼容 Android 12 以下版本，需要清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
     * 还有 Android 12 以下设备，获取蓝牙扫描结果需要 {@link PermissionNames#ACCESS_FINE_LOCATION} 权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
     */
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
    public static IPermission getActivityRecognitionPermission() {
        String permissionName = PermissionNames.ACTIVITY_RECOGNITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, AndroidVersionTools.ANDROID_10));
    }

    /**
     * 读取媒体文件的位置权限（Android 10.0 新增的权限）
     *
     * 需要注意的是：如果这个权限申请成功了但是不能正常读取照片的地理信息，那么需要先申请存储权限，具体可分别下面两种情况：
     *
     * 1. 如果适配了分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 32 需要申请 {@link PermissionNames#READ_EXTERNAL_STORAGE}
     *     2) 如果项目 targetSdkVersion >= 33 需要申请 {@link PermissionNames#READ_MEDIA_IMAGES} 或 {@link PermissionNames#READ_MEDIA_VIDEO}，并且需要全部授予，不能部分授予
     *
     * 2. 如果没有适配分区存储的情况下：
     *     1) 如果项目 targetSdkVersion <= 29 需要申请 {@link PermissionNames#READ_EXTERNAL_STORAGE}
     *     2) 如果项目 targetSdkVersion >= 30 需要申请 {@link PermissionNames#MANAGE_EXTERNAL_STORAGE}
     */
    public static IPermission getMediaLocationPermission() {
        IPermission permission = getCachePermission(MediaLocationPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new MediaLocationPermission());
    }

    /**
     * 允许呼叫应用继续在另一个应用中启动的呼叫权限（Android 9.0 新增的权限）
     */
    public static IPermission getAcceptHandoverPermission() {
        String permissionName = PermissionNames.ACCEPT_HANDOVER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_9));
    }

    /**
     * 读取手机号码权限（Android 8.0 新增的权限）
     *
     * 为了兼容 Android 8.0 以下版本，需要在清单文件中注册 {@link PermissionNames#READ_PHONE_STATE} 权限
     */
    public static IPermission getReadPhoneNumbersPermission() {
        IPermission permission = getCachePermission(ReadPhoneNumbersPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadPhoneNumbersPermission());
    }

    /**
     * 接听电话权限（Android 8.0 新增的权限，Android 8.0 以下可以采用模拟耳机按键事件来实现接听电话，这种方式不需要权限）
     */
    public static IPermission getAnswerPhoneCallsPermission() {
        String permissionName = PermissionNames.ANSWER_PHONE_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_8));
    }

    /**
     * 读取外部存储权限
     */
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
    public static IPermission getCameraPermission() {
        String permissionName = PermissionNames.CAMERA;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 麦克风权限
     */
    public static IPermission getRecordAudioPermission() {
        String permissionName = PermissionNames.RECORD_AUDIO;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 获取精确位置权限
     */
    public static IPermission getAccessFineLocationPermission() {
        String permissionName = PermissionNames.ACCESS_FINE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.LOCATION, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 获取粗略位置权限
     */
    public static IPermission getAccessCoarseLocationPermission() {
        String permissionName = PermissionNames.ACCESS_COARSE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.LOCATION, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 读取联系人权限
     */
    public static IPermission getReadContactsPermission() {
        String permissionName = PermissionNames.READ_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.CONTACTS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 修改联系人权限
     */
    public static IPermission getWriteContactsPermission() {
        String permissionName = PermissionNames.WRITE_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.CONTACTS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 访问账户列表权限
     */
    public static IPermission getGetAccountsPermission() {
        String permissionName = PermissionNames.GET_ACCOUNTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 读取日历权限
     */
    public static IPermission getReadCalendarPermission() {
        String permissionName = PermissionNames.READ_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.CALENDAR, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 修改日历权限
     */
    public static IPermission getWriteCalendarPermission() {
        String permissionName = PermissionNames.WRITE_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.CALENDAR, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 读取电话状态权限
     *
     * 需要注意的是：这个权限在某些手机上面是没办法获取到的，因为某些系统禁止应用获得该权限
     *             所以你要是申请了这个权限之后没有弹授权框，而是直接回调授权失败方法
     *             请不要惊慌，这个不是 Bug、不是 Bug、不是 Bug，而是正常现象
     *
     * 后续情况汇报：有人反馈在 iQOO 手机上面获取不到该权限，在清单文件加入下面这个权限就可以了（这里只是做记录，并不代表这种方式就一定有效果）
     *             <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     */
    public static IPermission getReadPhoneStatePermission() {
        String permissionName = PermissionNames.READ_PHONE_STATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 拨打电话权限
     */
    public static IPermission getCallPhonePermission() {
        String permissionName = PermissionNames.CALL_PHONE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 读取通话记录权限
     */
    public static IPermission getReadCallLogPermission() {
        String permissionName = PermissionNames.READ_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = AndroidVersionTools.isAndroid9() ? PermissionGroupConstants.CALL_LOG : PermissionGroupConstants.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 修改通话记录权限
     */
    public static IPermission getWriteCallLogPermission() {
        String permissionName = PermissionNames.WRITE_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = AndroidVersionTools.isAndroid9() ? PermissionGroupConstants.CALL_LOG : PermissionGroupConstants.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 添加语音邮件权限
     */
    public static IPermission getAddVoicemailPermission() {
        String permissionName = PermissionNames.ADD_VOICEMAIL;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 使用 SIP 视频权限
     */
    public static IPermission getUseSipPermission() {
        String permissionName = PermissionNames.USE_SIP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.PHONE, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 处理拨出电话权限
     *
     * @deprecated         在 Android 10 已经过时，请见：https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS
     */
    public static IPermission getProcessOutgoingCallsPermission() {
        String permissionName = PermissionNames.PROCESS_OUTGOING_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // 注意：在 Android 9.0 的时候，通话记录相关的权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        String permissionGroup = AndroidVersionTools.isAndroid9() ? PermissionGroupConstants.CALL_LOG : PermissionGroupConstants.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 使用传感器权限
     */
    public static IPermission getBodySensorsPermission() {
        String permissionName = PermissionNames.BODY_SENSORS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SENSORS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 发送短信权限
     */
    public static IPermission getSendSmsPermission() {
        String permissionName = PermissionNames.SEND_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SMS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 接收短信权限
     */
    public static IPermission getReceiveSmsPermission() {
        String permissionName = PermissionNames.RECEIVE_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SMS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 读取短信权限
     */
    public static IPermission getReadSmsPermission() {
        String permissionName = PermissionNames.READ_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SMS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 接收 WAP 推送消息权限
     */
    public static IPermission getReceiveWapPushPermission() {
        String permissionName = PermissionNames.RECEIVE_WAP_PUSH;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SMS, AndroidVersionTools.ANDROID_6));
    }

    /**
     * 接收彩信权限
     */
    public static IPermission getReceiveMmsPermission() {
        String permissionName = PermissionNames.RECEIVE_MMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroupConstants.SMS, AndroidVersionTools.ANDROID_6));
    }
}