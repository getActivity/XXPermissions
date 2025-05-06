package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 危险权限请求类
 */
@SuppressWarnings({"unused", "deprecation"})
public final class XXPermissions {

    /** 权限设置页跳转请求码 */
    public static final int REQUEST_CODE = 1024 + 1;

    /** 权限请求拦截器 */
    private static OnPermissionInterceptor sInterceptor;

    /** 当前是否为检查模式 */
    private static Boolean sCheckMode;

    /**
     * 设置请求的对象
     *
     * @param context          当前 Activity，可以传入栈顶的 Activity
     */
    public static XXPermissions with(@NonNull Context context) {
        return new XXPermissions(context);
    }

    public static XXPermissions with(@NonNull Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static XXPermissions with(@NonNull android.support.v4.app.Fragment fragment) {
        return with(fragment.getActivity());
    }

    /**
     * 设置全局的检查模式
     */
    public static void setCheckMode(boolean checkMode) {
        sCheckMode = checkMode;
    }

    /**
     * 设置全局的权限请求拦截器
     */
    public static void setInterceptor(OnPermissionInterceptor interceptor) {
        sInterceptor = interceptor;
    }

    /**
     * 获取全局权限请求拦截器
     */
    public static OnPermissionInterceptor getInterceptor() {
        if (sInterceptor == null) {
            sInterceptor = new OnPermissionInterceptor() {};
        }
        return sInterceptor;
    }

    /** 申请的权限列表 */
    @NonNull
    private final List<String> mPermissions = new ArrayList<>();

    /** Context 对象 */
    @Nullable
    private final Context mContext;

    /** 权限请求拦截器 */
    @Nullable
    private OnPermissionInterceptor mInterceptor;

    /** 设置不检查 */
    @Nullable
    private Boolean mCheckMode;

    /**
     * 私有化构造函数
     */
    private XXPermissions(@Nullable Context context) {
        mContext = context;
    }

    /**
     * 添加权限组
     */
    public XXPermissions permission(@PermissionLimit @Nullable String permission) {
        if (permission == null) {
            return this;
        }
        if (PermissionUtils.containsPermission(mPermissions, permission)) {
            return this;
        }
        mPermissions.add(permission);
        return this;
    }

    public XXPermissions permission(@Nullable String... permissions) {
        return permission(PermissionUtils.asArrayList(permissions));
    }

    public XXPermissions permission(@Nullable String[]... permissions) {
        return permission(PermissionUtils.asArrayLists(permissions));
    }

    public XXPermissions permission(@Nullable List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return this;
        }

        for (String permission : permissions) {
            if (PermissionUtils.containsPermission(mPermissions, permission)) {
                continue;
            }
            mPermissions.add(permission);
        }
        return this;
    }

    /**
     * 设置权限请求拦截器
     */
    public XXPermissions interceptor(@Nullable OnPermissionInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    /**
     * 设置不触发错误检测机制
     */
    public XXPermissions unchecked() {
        mCheckMode = false;
        return this;
    }

    /**
     * 请求权限
     */
    public void request(@Nullable OnPermissionCallback callback) {
        if (mContext == null) {
            return;
        }

        if (mInterceptor == null) {
            mInterceptor = getInterceptor();
        }

        final Context context = mContext;

        final OnPermissionInterceptor interceptor = mInterceptor;

        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请，为了避免重复添加）
        List<String> permissions = new ArrayList<>(mPermissions);

        boolean checkMode = isCheckMode(context);

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        Activity activity = PermissionUtils.findActivity(context);
        if (!PermissionChecker.checkActivityStatus(activity, checkMode)) {
            return;
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionChecker.checkPermissionArgument(permissions, checkMode)) {
            return;
        }

        if (checkMode) {
            // 获取清单文件信息
            AndroidManifestInfo androidManifestInfo = PermissionUtils.getAndroidManifestInfo(context);
            // 检查申请的读取媒体位置权限是否符合规范
            PermissionChecker.checkMediaLocationPermission(context, permissions);
            // 检查申请的存储权限是否符合规范
            PermissionChecker.checkStoragePermission(context, permissions, androidManifestInfo);
            // 检查申请的传感器权限是否符合规范
            PermissionChecker.checkBodySensorsPermission(permissions);
            // 检查申请的定位权限是否符合规范
            PermissionChecker.checkLocationPermission(permissions);
            // 检查申请的画中画权限是否符合规范
            PermissionChecker.checkPictureInPicturePermission(activity, permissions, androidManifestInfo);
            // 检查申请的通知栏监听权限是否符合规范
            PermissionChecker.checkNotificationListenerPermission(permissions, androidManifestInfo);
            // 检查蓝牙和 WIFI 权限申请是否符合规范
            PermissionChecker.checkNearbyDevicesPermission(permissions, androidManifestInfo);
            // 检查对照片和视频的部分访问权限申请是否符合规范
            PermissionChecker.checkReadMediaVisualUserSelectedPermission(permissions);
            // 检查读取应用列表权限是否符合规范
            PermissionChecker.checkGetInstallAppsPermission(context, permissions, androidManifestInfo);
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionChecker.checkTargetSdkVersion(context, permissions);
            // 检测权限有没有在清单文件中注册
            PermissionChecker.checkManifestPermissions(context, permissions, androidManifestInfo);
        }

        // 优化所申请的权限列表
        permissions = PermissionApi.compatibleOldPermissionByNewPermission(permissions);

        if (PermissionApi.isGrantedPermissions(context, permissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            interceptor.grantedPermissionRequest(activity, permissions, permissions, true, callback);
            interceptor.finishPermissionRequest(activity, permissions, true, callback);
            return;
        }

        // 申请没有授予过的权限
        interceptor.launchPermissionRequest(activity, permissions, callback);
    }

    /**
     * 撤销权限并杀死当前进程
     *
     * @return          返回 true 代表成功，返回 false 代表失败
     */
    public boolean revokeOnKill() {
        if (mContext == null) {
            return false;
        }

        final Context context = mContext;

        final List<String> permissions = mPermissions;

        if (permissions.isEmpty()) {
            return false;
        }

        if (!AndroidVersion.isAndroid13()) {
            return false;
        }

        try {
            if (permissions.size() == 1) {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionOnKill(java.lang.String)
                context.revokeSelfPermissionOnKill(permissions.get(0));
            } else {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionsOnKill(java.util.Collection%3Cjava.lang.String%3E)
                context.revokeSelfPermissionsOnKill(permissions);
            }
            return true;
        } catch (IllegalArgumentException e) {
            if (isCheckMode(context)) {
                throw e;
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 当前是否为检测模式
     */
    private boolean isCheckMode(@NonNull Context context) {
        if (mCheckMode == null) {
            if (sCheckMode == null) {
                sCheckMode = PermissionUtils.isDebugMode(context);
            }
            mCheckMode = sCheckMode;
        }
        return mCheckMode;
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean isGranted(@NonNull Context context, @NonNull String... permissions) {
        return isGranted(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGranted(@NonNull Context context, @NonNull String[]... permissions) {
        return isGranted(context, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isGranted(@NonNull Context context, @NonNull List<String> permissions) {
        return PermissionApi.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDenied(@NonNull Context context, @NonNull String... permissions) {
        return getDenied(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDenied(@NonNull Context context, @NonNull String[]... permissions) {
        return getDenied(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getDenied(@NonNull Context context, @NonNull List<String> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否为特殊权限
     */
    public static boolean isSpecial(@NonNull String permission) {
        return PermissionApi.isSpecialPermission(permission);
    }

    /**
     * 判断权限列表中是否包含特殊权限
     */
    public static boolean containsSpecial(@NonNull String... permissions) {
        return containsSpecial(PermissionUtils.asArrayList(permissions));
    }

    public static boolean containsSpecial(@NonNull List<String> permissions) {
        return PermissionApi.containsSpecialPermission(permissions);
    }

    /**
     * 判断一个或多个权限是否被勾选了不再询问的选项
     *
     * 注意不能在请求权限之前调用，一定要在 {@link OnPermissionCallback#onDenied(List, boolean)} 方法中调用
     * 如果你在应用启动后，没有申请过这个权限，然后去判断它有没有勾选不再询问的选项，这样系统会一直返回 true，也就是不再询问
     * 但是实际上还能继续申请，系统只是不想让你知道权限是否勾选了不再询问的选项，你必须要申请过这个权限，才能去判断这个权限是否勾选了不再询问的选项
     */
    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        return isDoNotAskAgainPermissions(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull String[]... permissions) {
        return isDoNotAskAgainPermissions(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<String> permissions) {
        return PermissionApi.isDoNotAskAgainPermissions(activity, permissions);
    }

    /* android.content.Context */

    public static void startPermissionActivity(@NonNull Context context) {
        startPermissionActivity(context, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Context context, @NonNull String... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Context context, @NonNull String[]... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(@NonNull Context context, @NonNull List<String> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionApi.getSmartPermissionIntent(context, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        PermissionActivityIntentHandler.startActivity(context, intent);
    }

    /* android.app.Activity */

    public static void startPermissionActivity(@NonNull Activity activity) {
        startPermissionActivity(activity, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull String... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull String[]... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<String> permissions) {
        startPermissionActivity(activity, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<String> permissions,
                                               int requestCode) {
        Intent intent = PermissionApi.getSmartPermissionIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(activity, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull String permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull String[] permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<String> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(activity, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        RequestSpecialPermissionFragment.launch(activity, permissions, callback);
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(@NonNull Fragment fragment) {
        startPermissionActivity(fragment, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<String> permissions,
                                               int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(fragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getSmartPermissionIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(fragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull String permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull String[] permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<String> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        Activity activity = fragment.getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (AndroidVersion.isAndroid4_2() && activity.isDestroyed()) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(fragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        RequestSpecialPermissionFragment.launch(activity, permissions, callback);
    }

    /* android.support.v4.app.Fragment */

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment) {
        startPermissionActivity(fragment, new ArrayList<>());
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull List<String> permissions,
                                               int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(fragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getSmartPermissionIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(fragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull String permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull String[] permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment fragment,
                                               @NonNull List<String> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        Activity activity = fragment.getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (AndroidVersion.isAndroid4_2() && activity.isDestroyed()) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(fragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        RequestSpecialPermissionFragment.launch(activity, permissions, callback);
    }
}