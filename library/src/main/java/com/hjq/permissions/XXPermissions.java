package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

    public static XXPermissions with(@NonNull Fragment appFragment) {
        return new XXPermissions(appFragment);
    }

    public static XXPermissions with(@NonNull android.support.v4.app.Fragment supportFragment) {
        return new XXPermissions(supportFragment);
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
    public static void setPermissionInterceptor(OnPermissionInterceptor interceptor) {
        sInterceptor = interceptor;
    }

    /**
     * 获取全局权限请求拦截器
     */
    public static OnPermissionInterceptor getPermissionInterceptor() {
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
    private Context mContext;

    /** App 包下的 Fragment 对象 */
    @Nullable
    private Fragment mAppFragment;

    /** Support 包下的 Fragment 对象 */
    @Nullable
    private android.support.v4.app.Fragment mSupportFragment;

    /** 权限请求拦截器 */
    @Nullable
    private OnPermissionInterceptor mInterceptor;

    /** 设置不检查 */
    @Nullable
    private Boolean mCheckMode;

    private XXPermissions(@Nullable Context context) {
        mContext = context;
    }

    private XXPermissions(@Nullable Fragment appFragment) {
        mAppFragment = appFragment;
        if (appFragment == null) {
            return;
        }
        mContext = appFragment.getActivity();
    }

    private XXPermissions(@Nullable android.support.v4.app.Fragment supportFragment) {
        mSupportFragment = supportFragment;
        if (supportFragment == null) {
            return;
        }
        mContext = supportFragment.getActivity();
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
            mInterceptor = getPermissionInterceptor();
        }

        final Context context = mContext;

        final Fragment appFragment = mAppFragment;

        final android.support.v4.app.Fragment supportFragment = mSupportFragment;

        final OnPermissionInterceptor interceptor = mInterceptor;

        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请，为了避免重复添加）
        List<String> permissions = new ArrayList<>(mPermissions);

        // 从 Context 对象中获得 Activity 对象
        Activity activity = PermissionUtils.findActivity(context);

        if (isCheckMode(context)) {
            // 检查传入的 Activity 或者 Fragment 状态是否正常
            PermissionChecker.checkActivityStatus(activity);
            if (appFragment != null) {
                PermissionChecker.checkAppFragmentStatus(appFragment);
            } else if (supportFragment != null) {
                PermissionChecker.checkSupportFragmentStatus(supportFragment);
            }
            // 检查传入的权限是否正常
            PermissionChecker.checkPermissionList(permissions);
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

        // 检查 Activity 是不是不可用
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        // 判断要申请的权限是否都授予了
        if (PermissionApi.isGrantedPermissions(context, permissions)) {
            // 如果是的话，就不申请权限，而是通知权限申请成功
            interceptor.grantedPermissionRequest(activity, permissions, permissions, true, callback);
            interceptor.finishPermissionRequest(activity, permissions, true, callback);
            return;
        }

        // 检查 App 包下的 Fragment 是不是不可用
        if (appFragment != null && PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }

        // 检查 Support 包下的 Fragment 是不是不可用
        if (supportFragment != null && PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }

        // 创建 Fragment 工厂
        final PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, supportFragment, appFragment);

        // 申请没有授予过的权限
        interceptor.launchPermissionRequest(activity, fragmentFactory, permissions, callback);
    }

    /**
     * 撤销权限并杀死当前进程
     *
     * @return          返回 true 代表成功，返回 false 代表失败
     */
    public boolean revokeOnKill() {
        final Context context = mContext;

        if (context == null) {
            return false;
        }

        final List<String> permissions = mPermissions;

        if (permissions.isEmpty()) {
            return false;
        }

        if (!AndroidVersionTools.isAndroid13()) {
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
    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull String... permissions) {
        return isGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull String[]... permissions) {
        return isGrantedPermissions(context, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        return PermissionApi.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取已授予的权限
     */
    public static List<String> getGrantedPermissions(@NonNull Context context, @NonNull String... permissions) {
        return getGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getGrantedPermissions(@NonNull Context context, @NonNull String[]... permissions) {
        return getGrantedPermissions(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getGrantedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        return PermissionApi.getGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDeniedPermissions(@NonNull Context context, @NonNull String... permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDeniedPermissions(@NonNull Context context, @NonNull String[]... permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getDeniedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否为特殊权限
     */
    public static boolean isSpecialPermission(@NonNull String permission) {
        return PermissionApi.isSpecialPermission(permission);
    }

    /**
     * 判断权限列表中是否包含特殊权限
     */
    public static boolean containsSpecialPermission(@NonNull String... permissions) {
        return containsSpecialPermission(PermissionUtils.asArrayList(permissions));
    }

    public static boolean containsSpecialPermission(@NonNull List<String> permissions) {
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
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(activity, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> dispatchPermissionPageCallback(activity, permissions, callback));
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(@NonNull Fragment appFragment) {
        startPermissionActivity(appFragment, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull String... permissions) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull String[]... permissions) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<String> permissions) {
        startPermissionActivity(appFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<String> permissions,
                                               int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(appFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getSmartPermissionIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(appFragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull String permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull String[] permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<String> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (AndroidVersionTools.isAndroid4_2() && activity.isDestroyed()) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(appFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, appFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> dispatchPermissionPageCallback(activity, permissions, callback));
    }

    /* android.support.v4.app.Fragment */

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment) {
        startPermissionActivity(supportFragment, new ArrayList<>());
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull String... permissions) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull String[]... permissions) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<String> permissions) {
        startPermissionActivity(supportFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<String> permissions,
                                               int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        Activity activity = supportFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(supportFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getSmartPermissionIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(supportFragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull String permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull String[] permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<String> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        Activity activity = supportFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (AndroidVersionTools.isAndroid4_2() && activity.isDestroyed()) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(supportFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, supportFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> dispatchPermissionPageCallback(activity, permissions, callback));
    }

    /**
     * 创建 Fragment 工厂
     */
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity) {
        return generatePermissionFragmentFactory(activity, null, null);
    }

    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                    @Nullable android.support.v4.app.Fragment supportFragment) {
        return generatePermissionFragmentFactory(activity, supportFragment, null);
    }

    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                    @Nullable Fragment appFragment) {
        return generatePermissionFragmentFactory(activity, null, appFragment);
    }

    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                    @Nullable android.support.v4.app.Fragment supportFragment,
                                                                                    @Nullable Fragment appFragment) {
        final PermissionFragmentFactory<?, ?> fragmentFactory;
        if (supportFragment != null) {
            fragmentFactory = new PermissionFragmentFactoryBySupport(supportFragment.getActivity(), supportFragment.getChildFragmentManager());
        } else if (appFragment != null) {
            // appFragment.getChildFragmentManager 需要 minSdkVersion >=  17
            fragmentFactory = new PermissionFragmentFactoryByApp(appFragment.getActivity(), appFragment.getChildFragmentManager());
        } else if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = ((FragmentActivity) activity);
            fragmentFactory = new PermissionFragmentFactoryBySupport(fragmentActivity, fragmentActivity.getSupportFragmentManager());
        } else {
            fragmentFactory = new PermissionFragmentFactoryByApp(activity, activity.getFragmentManager());
        }
        return fragmentFactory;
    }

    /**
     * 派发权限设置页回调
     */
    private static void dispatchPermissionPageCallback(@NonNull Context context,
                                                        @NonNull List<String> permissions,
                                                        @Nullable OnPermissionPageCallback callback) {
        if (callback == null) {
            return;
        }
        if (isGrantedPermissions(context, permissions)) {
            callback.onGranted();
        } else {
            callback.onDenied();
        }
    }
}