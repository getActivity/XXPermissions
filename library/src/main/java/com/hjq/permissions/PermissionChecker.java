package com.hjq.permissions;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.AndroidManifestInfo.ActivityInfo;
import com.hjq.permissions.AndroidManifestInfo.ApplicationInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidManifestInfo.ServiceInfo;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/02/09
 *    desc   : 权限错误检测类
 */
final class PermissionChecker {

    /**
     * 检查 Activity 的状态是否正常
     */
    static void checkActivityStatus(@Nullable Activity activity) {
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if (activity == null) {
            // Context 的实例必须是 Activity 对象
            throw new IllegalArgumentException("The instance of the context must be an activity object");
        }

        if (activity.isFinishing()) {
            // 这个 Activity 对象当前不能是关闭状态，这种情况常出现在执行异步请求后申请权限
            // 请自行在外层判断 Activity 状态是否正常之后再进入权限申请
            throw new IllegalStateException("The activity has been finishing, " +
                "please manually determine the status of the activity");
        }

        if (activity.isDestroyed()) {
            // 这个 Activity 对象当前不能是销毁状态，这种情况常出现在执行异步请求后申请权限
            // 请自行在外层判断 Activity 状态是否正常之后再进入权限申请
            throw new IllegalStateException("The activity has been destroyed, " +
                "please manually determine the status of the activity");
        }
    }

    /**
     * 检查 Fragment 的状态是否正常（Support 包版本）
     */
    static void checkSupportFragmentStatus(@NonNull android.support.v4.app.Fragment supportFragment) {
        if (!supportFragment.isAdded()) {
            // 这个 Fragment 没有添加绑定
            throw new IllegalStateException("This support fragment has no binding added, " +
                "please manually determine the status of the support fragment");
        }

        if (supportFragment.isRemoving()) {
            // 这个 Fragment 已经被移除
            throw new IllegalStateException("This support fragment has been removed, " +
                "please manually determine the status of the support fragment");
        }
    }

    /**
     * 检查 Fragment 的状态是否正常（App 包版本）
     */
    @SuppressWarnings("deprecation")
    static void checkAppFragmentStatus(@NonNull Fragment appFragment) {
        if (!appFragment.isAdded()) {
            // 这个 Fragment 没有添加绑定
            throw new IllegalStateException("This app fragment has no binding added, " +
                "please manually determine the status of the app fragment");
        }

        if (appFragment.isRemoving()) {
            // 这个 Fragment 已经被移除
            throw new IllegalStateException("This app fragment has been removed, " +
                "please manually determine the status of the app fragment");
        }
    }

    /**
     * 检查传入的权限是否符合要求
     */
    static void checkPermissionList(@Nullable List<IPermission> requestPermissions) {
        if (requestPermissions == null || requestPermissions.isEmpty()) {
            // 不传任何权限，就想动态申请权限？
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }

        for (IPermission permission : requestPermissions) {

            Class<? extends IPermission> clazz = permission.getClass();
            String className = clazz.getName();

            // 获取 CREATOR 字段
            Field creatorField = null;
            try {
                creatorField = permission.getClass().getDeclaredField("CREATOR");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            if (creatorField == null) {
                // 这个权限类没有定义 CREATOR 字段
                throw new IllegalArgumentException("This permission class does not define the CREATOR field");
            }

            // 获取 CREATOR 对象
            Object creatorObject;
            try {
                // 静态字段使用 null 作为实例
                creatorObject = creatorField.get(null);
            } catch (Exception e) {
                // 访问权限类中的 CREATOR 字段异常，请用 public static final 来修饰 CREATOR 字段
                throw new IllegalArgumentException("The CREATOR field in the " + className +
                    " has an access exception. Please modify CREATOR field with \"public static final\"");
            }

            if (!(creatorObject instanceof Parcelable.Creator)) {
                // 这个权限类中的 CREATOR 字段不是 android.os.Parcelable.Creator 类型
                throw new IllegalArgumentException("The CREATOR field in this " + className +
                                                    " is not of type " + Parcelable.Creator.class.getName());
            }

            // 获取字段的泛型类型
            Type genericType = creatorField.getGenericType();

            // 检查是否为参数化类型
            if (!(genericType instanceof ParameterizedType)) {
                // 这个权限类中的 CREATOR 字段定义的泛型为空
                throw new IllegalArgumentException("The generic type defined for the CREATOR field in this " + className + " is empty");
            }

            // 获取泛型参数
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            // 检查是否只有一个泛型参数
            if (typeArguments.length != 1) {
                // 这个权限类中的 CREATOR 字段定义的泛型数量只能有一个
                throw new IllegalArgumentException("The number of generics defined in the CREATOR field of this " + className + " can only be one");
            }

            // 获取泛型参数类型
            Type typeArgument = typeArguments[0];

            // 检查泛型参数是否为当前类
            if (!(typeArgument instanceof Class && clazz.isAssignableFrom((Class<?>) typeArgument))) {
                // 这个权限类中的 CREATOR 字段定义的泛型类型错误
                throw new IllegalArgumentException("The generic type defined in the CREATOR field of this " + className + " is incorrect");
            }

            // 直接调用 newArray 方法创建数组
            Parcelable.Creator<?> parcelableCreator = (Parcelable.Creator<?>) creatorObject;
            Object[] array = parcelableCreator.newArray(0);
            if (array == null) {
                // 这个权限类中的 CREATOR 字段的 newArray 方法返回了空，此方法返回不能为空
                throw new IllegalArgumentException("The newArray method of the CREATOR field in this " + className +
                                                   " returns an empty value. This method cannot return an empty value");
            }
        }
    }

    /**
     * 检查读取媒体位置权限
     */
    static void checkMediaLocationPermission(@NonNull Context context, @NonNull List<IPermission> requestPermissions) {
        // 如果请求的权限中没有包含外部读取媒体位置权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        if (AndroidVersionTools.getTargetSdkVersionCode(context) >= AndroidVersionTools.ANDROID_13) {
            if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_IMAGES) &&
                !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_VIDEO) &&
                !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE)) {
                // 你需要在外层手动添加 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO、MANAGE_EXTERNAL_STORAGE 任一权限才可以申请 ACCESS_MEDIA_LOCATION 权限
                throw new IllegalArgumentException("You must add " + PermissionManifest.READ_MEDIA_IMAGES + " or " + PermissionManifest.READ_MEDIA_VIDEO + " or " +
                    PermissionManifest.MANAGE_EXTERNAL_STORAGE + " rights to apply for " + PermissionManifest.ACCESS_MEDIA_LOCATION + " rights");
            }
        } else {
            if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_EXTERNAL_STORAGE) &&
                !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE)) {
                // 你需要在外层手动添加 READ_EXTERNAL_STORAGE 或者 MANAGE_EXTERNAL_STORAGE 才可以申请 ACCESS_MEDIA_LOCATION 权限
                throw new IllegalArgumentException("You must add " + PermissionManifest.READ_EXTERNAL_STORAGE + " or " +
                    PermissionManifest.MANAGE_EXTERNAL_STORAGE + " rights to apply for " + PermissionManifest.ACCESS_MEDIA_LOCATION + " rights");
            }
        }
    }

    /**
     * 检查存储权限
     */
    static void checkStoragePermission(@NonNull Context context, @NonNull List<IPermission> requestPermissions,
                                        @Nullable AndroidManifestInfo androidManifestInfo) {
        // 如果请求的权限中没有包含外部存储相关的权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_IMAGES) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_VIDEO) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_AUDIO) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_EXTERNAL_STORAGE) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        if (AndroidVersionTools.getTargetSdkVersionCode(context) >= AndroidVersionTools.ANDROID_13 &&
            PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_EXTERNAL_STORAGE)) {
            // 当 targetSdkVersion >= 33 应该使用 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO、READ_MEDIA_AUDIO 来代替 READ_EXTERNAL_STORAGE
            // 因为经过测试，如果当 targetSdkVersion >= 33 申请 READ_EXTERNAL_STORAGE 或者 WRITE_EXTERNAL_STORAGE 会被系统直接拒绝，不会弹出任何授权框
            throw new IllegalArgumentException("When targetSdkVersion >= 33 should use " +
                PermissionManifest.READ_MEDIA_IMAGES + ", " + PermissionManifest.READ_MEDIA_VIDEO + ", " + PermissionManifest.READ_MEDIA_AUDIO +
                ", rather than " + PermissionManifest.READ_EXTERNAL_STORAGE);
        }

        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_VIDEO) ||
            PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_AUDIO)) {

            if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_EXTERNAL_STORAGE)) {
                // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这个权限
                // 框架会在 Android 13 以下的版本上自动添加并申请这两个权限
                throw new IllegalArgumentException("If you have applied for media permissions, " +
                    "do not apply for the READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions");
            }

            if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE)) {
                // 因为 MANAGE_EXTERNAL_STORAGE 权限范围很大，有了它就可以读取媒体文件，不需要再叠加申请媒体权限
                throw new IllegalArgumentException("Because the MANAGE_EXTERNAL_STORAGE permission range is very large, "
                    + "you can read media files with it, and there is no need to apply for additional media permissions.");
            }

            // 到此结束，不需要往下走是否有分区存储的判断
            return;
        }

        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE)) {

            if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_EXTERNAL_STORAGE) ||
                PermissionUtils.containsPermission(requestPermissions, PermissionManifest.WRITE_EXTERNAL_STORAGE)) {
                // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
                // 框架会在 Android 10 以下的版本上自动添加并申请这两个权限
                throw new IllegalArgumentException("If you have applied for MANAGE_EXTERNAL_STORAGE permissions, " +
                    "do not apply for the READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions");
            }
        }

        // 如果申请的是 Android 10 获取媒体位置权限，则绕过本次检查
        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        if (androidManifestInfo == null) {
            return;
        }

        ApplicationInfo applicationInfo = androidManifestInfo.applicationInfo;

        if (applicationInfo == null) {
            return;
        }

        // 是否适配了分区存储
        boolean scopedStorage = PermissionUtils.isScopedStorage(context);

        int targetSdkVersion = AndroidVersionTools.getTargetSdkVersionCode(context);

        boolean requestLegacyExternalStorage = applicationInfo.requestLegacyExternalStorage;
        // 如果在已经适配 Android 10 的情况下
        if (targetSdkVersion >= AndroidVersionTools.ANDROID_10 && !requestLegacyExternalStorage &&
            (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE) || !scopedStorage)) {
            // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性
            // 否则就算申请了权限，也无法在 Android 10 的设备上正常读写外部存储上的文件
            // 如果你的项目已经全面适配了分区存储，请在清单文件中注册一个 meta-data 属性
            // <meta-data android:name="ScopedStorage" android:value="true" /> 来跳过该检查
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }

        // 如果在已经适配 Android 11 的情况下
        if (targetSdkVersion >= AndroidVersionTools.ANDROID_11 &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.MANAGE_EXTERNAL_STORAGE) && !scopedStorage) {
            // 1. 适配分区存储的特性，并在清单文件中注册一个 meta-data 属性
            // <meta-data android:name="ScopedStorage" android:value="true" />
            // 2. 如果不想适配分区存储，则需要使用 Permission.MANAGE_EXTERNAL_STORAGE 来申请权限
            // 上面两种方式需要二选一，否则无法在 Android 11 的设备上正常读写外部存储上的文件
            // 如果不知道该怎么选择，可以看文档：https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
            throw new IllegalArgumentException("The storage permission application is abnormal. If you have adapted the scope storage, " +
                "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                "If there is no adaptation scope storage, please use " + PermissionManifest.MANAGE_EXTERNAL_STORAGE + " to apply for permission");
        }
    }

    /**
     * 检查传感器权限
     */
    static void checkBodySensorsPermission(@NonNull List<IPermission> requestPermissions) {
        // 判断是否包含后台传感器权限
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.BODY_SENSORS_BACKGROUND)) {
            return;
        }

        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.BODY_SENSORS_BACKGROUND) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.BODY_SENSORS)) {
            // 必须要申请前台传感器权限才能申请后台传感器权限
            throw new IllegalArgumentException("Applying for background sensor permissions must contain " + PermissionManifest.BODY_SENSORS);
        }
    }

    /**
     * 检查定位权限
     */
    static void checkLocationPermission(@NonNull List<IPermission> requestPermissions) {
        /*
        为什么要注释这段代码，因为经过测试，没有官方说得那么严重，我用 Android 模拟器做测试
        愣是没测出来只申请 ACCESS_FINE_LOCATION 会有什么异常，估计是 Google 将代码改回去了，但是文档忘记改了
        总结出来：耳听为虚，眼见不一定为实，要自己动手实践，实践出真理，光说不练假把式
        if (AndroidVersion.getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_12) {
            if (PermissionUtils.containsPermission(requestPermissions, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.containsPermission(requestPermissions, Permission.ACCESS_COARSE_LOCATION) ) {
                // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
                // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
                // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
                // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
                // 官方适配文档：https://developer.android.google.cn/develop/sensors-and-location/location/permissions?hl=zh-cn#approximate-request
                throw new IllegalArgumentException("If your app targets Android 12 or higher " +
                        "and requests the ACCESS_FINE_LOCATION runtime permission, " +
                        "you must also request the ACCESS_COARSE_LOCATION permission. " +
                        "You must include both permissions in a single runtime request.");
            }
        }
        */

        // 判断是否包含后台定位权限
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_BACKGROUND_LOCATION)) {
            return;
        }

        // 申请后台定位权限可以不包含模糊定位权限，但是一定要包含精确定位权限，否则后台定位权限会无法申请
        // 也就是会导致无法弹出授权弹窗，经过实践，在 Android 12 上这个问题已经被解决了
        // 但是为了兼容 Android 12 以下的设备还是要那么做，否则在 Android 11 及以下设备会出现异常
        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_COARSE_LOCATION) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_FINE_LOCATION)) {
            throw new IllegalArgumentException("Applying for background positioning permissions must include " +
                PermissionManifest.ACCESS_FINE_LOCATION);
        }
    }


    /**
     * 检查蓝牙和 WIFI 权限申请是否符合规范
     */
    static void checkNearbyDevicesPermission(@NonNull List<IPermission> requestPermissions,
                                                @Nullable AndroidManifestInfo androidManifestInfo) {
        // 如果请求的权限中没有蓝牙权限并且 WIFI 权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.BLUETOOTH_SCAN) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionManifest.NEARBY_WIFI_DEVICES)) {
            return;
        }

        // 如果请求的权限已经包含了精确定位权限，那么就需要检查了
        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.ACCESS_FINE_LOCATION)) {
            return;
        }

        if (androidManifestInfo == null) {
            return;
        }

        List<PermissionInfo> permissionInfoList = androidManifestInfo.permissionInfoList;

        for (PermissionInfo permissionInfo : permissionInfoList) {
            // 必须是蓝牙扫描权限或者 WIFI 权限才需要走这个检查
            if (!PermissionUtils.equalsPermission(permissionInfo.name, PermissionManifest.BLUETOOTH_SCAN.getName()) &&
                !PermissionUtils.equalsPermission(permissionInfo.name, PermissionManifest.NEARBY_WIFI_DEVICES.getName())) {
                continue;
            }

            if (!permissionInfo.neverForLocation()) {
                // WIFI 权限：https://developer.android.google.cn/about/versions/13/features/nearby-wifi-devices-permission?hl=zh-cn#assert-never-for-location
                // 在以 Android 13 为目标平台时，请考虑您的应用是否会通过 WIFI API 推导物理位置，如果不会，则应坚定声明此情况。
                // 如需做出此声明，请在应用的清单文件中将 usesPermissionFlags 属性设为 neverForLocation

                // 蓝牙权限：https://developer.android.google.cn/guide/topics/connectivity/bluetooth/permissions?hl=zh-cn#assert-never-for-location
                // 如果您的应用不使用蓝牙扫描结果来获取物理位置，则您可以断言您的应用从不使用蓝牙权限来获取物理位置。为此，请完成以下步骤：
                // 将该属性添加 android:usesPermissionFlags 到您的 BLUETOOTH_SCAN 权限声明中，并将该属性的值设置为 neverForLocation
                String maxSdkVersionString = (permissionInfo.maxSdkVersion != Integer.MAX_VALUE) ?
                    "android:maxSdkVersion=\"" + permissionInfo.maxSdkVersion + "\" " : "";
                // 根据不同的需求场景决定，解决方法分为两种：
                //   1. 不需要使用蓝牙权限或者 WIFI 权限来获取物理位置：只需要在清单文件中注册的权限上面加上 android:usesPermissionFlags="neverForLocation" 即可
                //   2. 需要使用蓝牙权限或者 WIFI 权限来获取物理位置：在申请蓝牙权限或者 WIFI 权限时，还需要动态申请 ACCESS_FINE_LOCATION 权限
                // 通常情况下，我们都不需要使用蓝牙权限或者 WIFI 权限来获取物理位置，所以选择第一种方法即可
                throw new IllegalArgumentException("If your app doesn't use " + permissionInfo.name +
                    " to get physical location, " + "please change the <uses-permission android:name=\"" +
                    permissionInfo.name + "\" " + maxSdkVersionString + "/> node in the " +
                    "manifest file to <uses-permission android:name=\"" + permissionInfo.name +
                    "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/> node, " +
                    "if your app need use " + permissionInfo.name + " to get physical location, " +
                    "also need to add " + PermissionManifest.ACCESS_FINE_LOCATION + " permissions");
            }
        }
    }

    /**
     * 检查通知栏监听权限
     */
    static void checkNotificationListenerPermission(@NonNull List<IPermission> requestPermissions,
                                                    @Nullable AndroidManifestInfo androidManifestInfo) {
        // 如果请求的权限中没有通知栏监听权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return;
        }

        if (androidManifestInfo == null) {
            return;
        }

        List<ServiceInfo> serviceInfoList = androidManifestInfo.serviceInfoList;
        for (int i = 0; i < serviceInfoList.size(); i++) {
            String permission = serviceInfoList.get(i).permission;
            if (TextUtils.equals(permission, PermissionManifest.BIND_NOTIFICATION_LISTENER_SERVICE.getName())) {
                // 终止循环并返回
                return;
            }
        }

        // 在 AndroidManifest.xml 中没有发现任何 Service 注册过 permission 属性
        // 请在 AndroidManifest.xml 中注册 <service android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" >
        throw new IllegalArgumentException("No service registered permission attribute, " +
            "please register <service android:permission=\"" +
            PermissionManifest.BIND_NOTIFICATION_LISTENER_SERVICE + "\" > in AndroidManifest.xml");
    }

    /**
     * 检查画中画权限
     */
    static void checkPictureInPicturePermission(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
        @Nullable AndroidManifestInfo androidManifestInfo) {
        // 如果请求的权限中没有画中画权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.PICTURE_IN_PICTURE)) {
            return;
        }

        if (androidManifestInfo == null) {
            return;
        }

        List<ActivityInfo> activityInfoList = androidManifestInfo.activityInfoList;
        for (int i = 0; i < activityInfoList.size(); i++) {
            boolean supportsPictureInPicture = activityInfoList.get(i).supportsPictureInPicture;
            if (supportsPictureInPicture) {
                // 终止循环并返回
                return;
            }
        }

        String activityName = activity.getClass().getName().replace(activity.getPackageName(), "");
        // 在 AndroidManifest.xml 中没有发现任何 Activity 注册过 supportsPictureInPicture 属性
        // 请在 AndroidManifest.xml 中注册 <activity android:supportsPictureInPicture="true" >
        throw new IllegalArgumentException("No activity registered supportsPictureInPicture attribute, please register \n" +
            "<activity android:name=\"" + activityName + "\" android:supportsPictureInPicture=\"true\" > in AndroidManifest.xml");
    }

    /**
     * 检查对照片和视频的部分访问权限申请是否符合规范
     */
    static void checkReadMediaVisualUserSelectedPermission(@NonNull List<IPermission> requestPermissions) {
        // 如果请求的权限中没有对照片和视频的部分访问权限，那么就不符合条件，停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_VISUAL_USER_SELECTED)) {
            return;
        }

        if (PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestPermissions, PermissionManifest.READ_MEDIA_VIDEO)) {
            return;
        }

        // 不能单独请求 READ_MEDIA_VISUAL_USER_SELECTED 权限，需要加上 READ_MEDIA_IMAGES 或者 READ_MEDIA_VIDEO 任一权限，又或者两个都有，否则权限申请会被系统直接拒绝
        throw new IllegalArgumentException("You cannot request the " + PermissionManifest.READ_MEDIA_VISUAL_USER_SELECTED + " permission alone. "
            + "must add either " + PermissionManifest.READ_MEDIA_IMAGES + " or " + PermissionManifest.READ_MEDIA_VIDEO + " permission, or maybe both");
    }

    /**
     * 检查读取应用列表权限是否是否符合规范
     */
    static void checkGetInstallAppsPermission(@NonNull Context context, @NonNull List<IPermission> requestPermissions,
                                                @Nullable AndroidManifestInfo androidManifestInfo) {
        if (androidManifestInfo == null) {
            return;
        }

        // 如果请求的权限中没有读取应用列表权限，那么就不符合条件，否则停止检查
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionManifest.GET_INSTALLED_APPS)) {
            return;
        }

        // 当前 targetSdk 必须大于 Android 11，否则停止检查
        if (AndroidVersionTools.getTargetSdkVersionCode(context) < AndroidVersionTools.ANDROID_11) {
            return;
        }

        String queryAllPackagesPermissionName;
        if (AndroidVersionTools.isAndroid11()) {
            queryAllPackagesPermissionName = permission.QUERY_ALL_PACKAGES;
        } else {
            queryAllPackagesPermissionName = "android.permission.QUERY_ALL_PACKAGES";
        }

        PermissionInfo permissionInfo = findPermissionInfoByList(androidManifestInfo.permissionInfoList, queryAllPackagesPermissionName);
        if (permissionInfo != null || !androidManifestInfo.queriesPackageList.isEmpty()) {
            return;
        }

        // 在 targetSdk >= 30 的时候，申请读取应用列表权限需要做一下处理
        // 1. 读取所有的应用：在清单文件中注册 QUERY_ALL_PACKAGES 权限
        // 2. 读取部分特定的应用：添加需要读取应用的包名到 <queries> 标签中
        // 以上两种解决方案需要二选一，否则就算申请 GET_INSTALLED_APPS 权限成功也是白搭，也是获取不到第三方安装列表信息的
        // 一般情况选择第一种解决方案，但是如果你要兼顾 GooglePlay 商店，直接注册 QUERY_ALL_PACKAGES 权限可能没办法上架，那么就需要用到第二种办法
        // Github issue：https://github.com/getActivity/XXPermissions/issues/359
        throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
            "<uses-permission android:name=\"" + queryAllPackagesPermissionName + "\" />, "
            + "or add the app package name to the <queries> tag in the AndroidManifest.xml file");
    }

    /**
     * 检查 targetSdkVersion 是否符合要求
     */
    static void checkTargetSdkVersion(@NonNull Context context, @NonNull List<IPermission> requestPermissions) {
        for (IPermission permission : requestPermissions) {
            // targetSdk 最低版本要求
            int targetSdkMinVersion;
            if (PermissionUtils.equalsPermission(permission, PermissionManifest.READ_MEDIA_VISUAL_USER_SELECTED)) {
                // 授予对照片和视频的部分访问权限：https://developer.android.google.cn/about/versions/14/changes/partial-photo-video-access?hl=zh-cn
                // READ_MEDIA_VISUAL_USER_SELECTED 这个权限比较特殊，不需要调高 targetSdk 的版本才能申请，但是需要和 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 组合使用
                // 这个权限不能单独申请，只能和 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO 一起申请，否则会有问题，所以这个权限的 targetSdk 最低要求为 33 及以上
                targetSdkMinVersion = AndroidVersionTools.ANDROID_13;
            } else if (PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_SCAN) ||
                        PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_CONNECT) ||
                        PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_ADVERTISE)) {
                // 部分厂商修改了蓝牙权限机制，在 targetSdk 不满足条件的情况下（小于 31），仍需要让应用申请这个权限
                // 相关的 issue 地址：
                // 1. https://github.com/getActivity/XXPermissions/issues/123
                // 2. https://github.com/getActivity/XXPermissions/issues/302
                targetSdkMinVersion = AndroidVersionTools.ANDROID_6;
            } else {
                targetSdkMinVersion = permission.getFromAndroidVersion();
            }

            // 必须设置正确的 targetSdkVersion 才能正常检测权限
            if (AndroidVersionTools.getTargetSdkVersionCode(context) >= targetSdkMinVersion) {
                continue;
            }

            throw new IllegalStateException("Request " + permission + " permission, " +
                "The targetSdkVersion SDK must be " + targetSdkMinVersion +
                " or more, if you do not want to upgrade targetSdkVersion, " +
                "please apply with the old permission");
        }
    }

    /**
     * 检查清单文件中所注册的权限是否正常
     */
    static void checkManifestPermissions(@NonNull Context context, @NonNull List<IPermission> requestPermissions,
                                            @Nullable AndroidManifestInfo androidManifestInfo) {
        if (androidManifestInfo == null) {
            return;
        }

        List<PermissionInfo> permissionInfoList = androidManifestInfo.permissionInfoList;
        if (permissionInfoList.isEmpty()) {
            throw new IllegalStateException("No permissions are registered in the AndroidManifest.xml file");
        }

        int minSdkVersion;
        if (AndroidVersionTools.isAndroid7()) {
            minSdkVersion = context.getApplicationInfo().minSdkVersion;
        } else {
            if (androidManifestInfo.usesSdkInfo != null) {
                minSdkVersion = androidManifestInfo.usesSdkInfo.minSdkVersion;
            } else {
                minSdkVersion = AndroidVersionTools.ANDROID_4_2;
            }
        }

        for (IPermission permission : requestPermissions) {

            if (!permission.isMandatoryStaticRegister()) {
                continue;
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.WRITE_EXTERNAL_STORAGE)) {
                checkWriteExternalStoragePermission(context, androidManifestInfo.applicationInfo, permissionInfoList);
            } else if (PermissionUtils.equalsPermission(permission, PermissionManifest.SCHEDULE_EXACT_ALARM)) {
                checkScheduleExactAlarmPermission(context, permissionInfoList);
            } else {
                checkManifestPermission(permissionInfoList, permission);
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.BODY_SENSORS_BACKGROUND)) {
                // 申请后台的传感器权限必须要先注册前台的传感器权限
                checkManifestPermission(permissionInfoList, PermissionManifest.BODY_SENSORS);
                continue;
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.ACCESS_BACKGROUND_LOCATION)) {
                // 在 Android 11 及之前的版本，申请后台定位权限需要精确定位权限
                // 在 Android 12 及之后的版本，申请后台定位权限即可以用精确定位权限也可以用模糊定位权限
                if (AndroidVersionTools.getTargetSdkVersionCode(context) >= AndroidVersionTools.ANDROID_12) {
                    checkManifestPermission(permissionInfoList, PermissionManifest.ACCESS_FINE_LOCATION, AndroidVersionTools.ANDROID_11);
                    checkManifestPermission(permissionInfoList, PermissionManifest.ACCESS_COARSE_LOCATION);
                } else {
                    checkManifestPermission(permissionInfoList, PermissionManifest.ACCESS_FINE_LOCATION);
                }
                continue;
            }

            // 如果 minSdkVersion 已经大于等于权限出现的版本，则不需要做向下兼容
            if (minSdkVersion >= permission.getFromAndroidVersion()) {
                return;
            }

            // Android 13
            if (PermissionUtils.equalsPermission(permission, PermissionManifest.READ_MEDIA_IMAGES) ||
                PermissionUtils.equalsPermission(permission, PermissionManifest.READ_MEDIA_VIDEO) ||
                PermissionUtils.equalsPermission(permission, PermissionManifest.READ_MEDIA_AUDIO)) {
                checkManifestPermission(permissionInfoList, PermissionManifest.READ_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_12_L);
                continue;
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.NEARBY_WIFI_DEVICES)) {
                checkManifestPermission(permissionInfoList, PermissionManifest.ACCESS_FINE_LOCATION, AndroidVersionTools.ANDROID_12_L);
                continue;
            }

            // Android 12

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_SCAN)) {
                checkManifestPermission(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, AndroidVersionTools.ANDROID_11);
                // 这是 Android 12 之前遗留的问题，获取扫描蓝牙的结果需要精确定位权限
                checkManifestPermission(permissionInfoList, PermissionManifest.ACCESS_FINE_LOCATION, AndroidVersionTools.ANDROID_11);
                continue;
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_CONNECT)) {
                checkManifestPermission(permissionInfoList, Manifest.permission.BLUETOOTH, AndroidVersionTools.ANDROID_11);
                continue;
            }

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.BLUETOOTH_ADVERTISE)) {
                checkManifestPermission(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, AndroidVersionTools.ANDROID_11);
                continue;
            }

            // Android 11

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.MANAGE_EXTERNAL_STORAGE)) {
                checkManifestPermission(permissionInfoList, PermissionManifest.READ_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_10);
                checkManifestPermission(permissionInfoList, PermissionManifest.WRITE_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_10);
                continue;
            }

            // Android 8.0

            if (PermissionUtils.equalsPermission(permission, PermissionManifest.READ_PHONE_NUMBERS)) {
                checkManifestPermission(permissionInfoList, PermissionManifest.READ_PHONE_STATE, AndroidVersionTools.ANDROID_7_1);
            }
        }
    }

    /**
     * 检查 {@link PermissionManifest#WRITE_EXTERNAL_STORAGE } 权限
     */
    static void checkWriteExternalStoragePermission(@NonNull Context context, @Nullable ApplicationInfo applicationInfo,
                                                    @NonNull List<PermissionInfo> permissionInfoList) {
        if (applicationInfo == null) {
            return;
        }

        IPermission checkPermission = PermissionManifest.WRITE_EXTERNAL_STORAGE;

        if (AndroidVersionTools.getTargetSdkVersionCode(context) < AndroidVersionTools.ANDROID_10) {
            checkManifestPermission(permissionInfoList, checkPermission);
            return;
        }

        // 判断清单文件中是否注册了 MANAGE_EXTERNAL_STORAGE 权限，如果有的话，那么 maxSdkVersion 就必须是 Android 10 及以上的版本
        if (AndroidVersionTools.getTargetSdkVersionCode(context) >= AndroidVersionTools.ANDROID_11 &&
                findPermissionInfoByList(permissionInfoList, PermissionManifest.MANAGE_EXTERNAL_STORAGE.getName()) != null) {
            checkManifestPermission(permissionInfoList, checkPermission, AndroidVersionTools.ANDROID_10);
            return;
        }

        // 检查这个权限有没有在清单文件中注册，WRITE_EXTERNAL_STORAGE 权限比较特殊，要单独拎出来判断
        // 如果在清单文件中注册了 android:requestLegacyExternalStorage="true" 属性，即可延长一个 Android 版本适配
        // 所以 requestLegacyExternalStorage 属性在开启的状态下，对 maxSdkVersion 属性的要求延长一个版本
        if (applicationInfo.requestLegacyExternalStorage) {
            checkManifestPermission(permissionInfoList, checkPermission, AndroidVersionTools.ANDROID_10);
        } else {
            checkManifestPermission(permissionInfoList, checkPermission, AndroidVersionTools.ANDROID_9);
        }
    }

    /**
     * 检查 {@link PermissionManifest#SCHEDULE_EXACT_ALARM } 权限
     */
    static void checkScheduleExactAlarmPermission(@NonNull Context context, @NonNull List<PermissionInfo> permissionInfoList) {
        String useExactAlarmPermissionName;
        if (AndroidVersionTools.isAndroid13()) {
            useExactAlarmPermissionName = permission.USE_EXACT_ALARM;
        } else {
            useExactAlarmPermissionName = "android.permission.USE_EXACT_ALARM";
        }

        if (AndroidVersionTools.getTargetSdkVersionCode(context) >= AndroidVersionTools.ANDROID_13 &&
                        findPermissionInfoByList(permissionInfoList, useExactAlarmPermissionName) != null) {
            // 如果当前项目适配了 Android 13 的话，并且在清单文件中注册了 USE_EXACT_ALARM 权限，那么 SCHEDULE_EXACT_ALARM 权限在清单文件中可以这样注册
            // <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" android:maxSdkVersion="32" />
            // 相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#USE_EXACT_ALARM
            // 如果你的应用要上架 GooglePlay，那么需要慎重添加 USE_EXACT_ALARM 权限，因为不是日历、闹钟、时钟这类应用添加 USE_EXACT_ALARM 权限很难通过 GooglePlay 上架审核
            checkManifestPermission(permissionInfoList, PermissionManifest.SCHEDULE_EXACT_ALARM, AndroidVersionTools.ANDROID_12_L);
            return;
        }

        checkManifestPermission(permissionInfoList, PermissionManifest.SCHEDULE_EXACT_ALARM);
    }

    static void checkManifestPermission(@NonNull List<PermissionInfo> permissionInfoList,
                                        @NonNull IPermission checkPermission) {
        checkManifestPermission(permissionInfoList, checkPermission, Integer.MAX_VALUE);
    }

    /**
     * 检查某个权限注册是否正常，如果是则会抛出异常
     *
     * @param permissionInfoList        清单权限组
     * @param checkPermission           被检查的权限
     * @param lowestMaxSdkVersion       最低要求的 maxSdkVersion
     */
    static void checkManifestPermission(@NonNull List<PermissionInfo> permissionInfoList,
                                        @NonNull String checkPermission, int lowestMaxSdkVersion) {
        PermissionInfo permissionInfo = findPermissionInfoByList(permissionInfoList, checkPermission);

        if (permissionInfo == null) {
            // 动态申请的权限没有在清单文件中注册，分为以下两种情况：
            // 1. 如果你的项目没有在清单文件中注册这个权限，请直接在清单文件中注册一下即可
            // 2. 如果你的项目明明已注册这个权限，可以检查一下编译完成的 apk 包中是否包含该权限，如果里面没有，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" tools:node="remove"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/98
            throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission + "\" />");
        }

        int manifestMaxSdkVersion = permissionInfo.maxSdkVersion;
        if (manifestMaxSdkVersion < lowestMaxSdkVersion) {
            // 清单文件中所注册的权限 maxSdkVersion 大小不符合最低要求，分为以下两种情况：
            // 1. 如果你的项目中注册了该属性，请根据报错提示修改 maxSdkVersion 属性值或者删除 maxSdkVersion 属性
            // 2. 如果你明明没有注册过 maxSdkVersion 属性，可以检查一下编译完成的 apk 包中是否有该属性，如果里面存在，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" android:maxSdkVersion="xx"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            throw new IllegalArgumentException("The AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission +
                "\" android:maxSdkVersion=\"" + manifestMaxSdkVersion +
                "\" /> does not meet the requirements, " +
                (lowestMaxSdkVersion != Integer.MAX_VALUE ?
                    "the minimum requirement for maxSdkVersion is " + lowestMaxSdkVersion :
                    "please delete the android:maxSdkVersion=\"" + manifestMaxSdkVersion + "\" attribute"));
        }
    }

    static void checkManifestPermission(@NonNull List<PermissionInfo> permissionInfoList,
                                        @NonNull IPermission checkPermission, int lowestMaxSdkVersion) {
        checkManifestPermission(permissionInfoList, checkPermission.getName(), lowestMaxSdkVersion);
    }

    /**
     * 从权限列表中获取指定的权限信息
     */
    @Nullable
    static PermissionInfo findPermissionInfoByList(@NonNull List<PermissionInfo> permissionInfoList,
                                                                        @NonNull String permissionName) {
        PermissionInfo permissionInfo = null;
        for (PermissionInfo info : permissionInfoList) {
            if (TextUtils.equals(info.name, permissionName)) {
                permissionInfo = info;
                break;
            }
        }
        return permissionInfo;
    }
}