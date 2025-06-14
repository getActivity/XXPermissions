package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android 权限请求入口类
 */
@SuppressWarnings({"unused", "deprecation"})
public final class XXPermissions {

    /** 权限设置页跳转请求码 */
    public static final int REQUEST_CODE = 1024 + 1;

    /** 权限申请拦截器的类型（全局生效） */
    private static Class<? extends OnPermissionInterceptor> sPermissionInterceptorClass;

    /** 权限请求描述器的类型（全局生效） */
    private static Class<? extends OnPermissionDescription> sPermissionDescriptionClass;

    /** 是否为检查模式（全局生效） */
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
     * 设置是否开启错误检测模式（全局设置）
     */
    public static void setCheckMode(boolean checkMode) {
        sCheckMode = checkMode;
    }

    /**
     * 设置权限申请拦截器（全局设置）
     */
    public static void setPermissionInterceptor(Class<? extends OnPermissionInterceptor> clazz) {
        sPermissionInterceptorClass = clazz;
    }

    /**
     * 获取权限申请拦截器（全局）
     */
    @NonNull
    public static OnPermissionInterceptor getPermissionInterceptor() {
        if (sPermissionInterceptorClass != null) {
            try {
                return sPermissionInterceptorClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionInterceptor();
    }

    /**
     * 设置权限描述器（全局设置）
     *
     * 这里解释一下，为什么不开放普通对象，而是只开放 Class 对象，这是因为如果用普通对象，那么就会导致全局都复用这一个对象
     * 而这个会带来一个后果，就是可能出现类内部字段的使用冲突，为了避免这一个问题，最好的解决方案是不去复用同一个对象
     */
    public static void setPermissionDescription(Class<? extends OnPermissionDescription> clazz) {
        sPermissionDescriptionClass = clazz;
    }

    /**
     * 获取权限描述器（全局）
     */
    @NonNull
    public static OnPermissionDescription getPermissionDescription() {
        if (sPermissionDescriptionClass != null) {
            try {
                return sPermissionDescriptionClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionDescription();
    }

    /** 申请的权限列表 */
    @NonNull
    private final List<IPermission> mPermissions = new ArrayList<>();

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
    private OnPermissionInterceptor mPermissionInterceptor;

    /** 权限请求描述 */
    @Nullable
    private OnPermissionDescription mPermissionDescription;

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
     * 添加单个权限
     */
    public XXPermissions permission(@Nullable IPermission permission) {
        if (permission == null) {
            return this;
        }
        // 这种写法的作用：如果出现重复添加的权限，则以最后添加的权限为主
        mPermissions.remove(permission);
        mPermissions.add(permission);
        return this;
    }

    /**
     * 添加多个权限
     */
    public XXPermissions permission(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return this;
        }

        for (int i = 0; i < permissions.size(); i++) {
            permission(permissions.get(i));
        }
        return this;
    }

    /**
     * 设置权限请求拦截器
     */
    public XXPermissions interceptor(@Nullable OnPermissionInterceptor permissionInterceptor) {
        mPermissionInterceptor = permissionInterceptor;
        return this;
    }

    /**
     * 设置权限请求描述
     */
    public XXPermissions description(@Nullable OnPermissionDescription permissionDescription) {
        mPermissionDescription = permissionDescription;
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

        if (mPermissionInterceptor == null) {
            mPermissionInterceptor = getPermissionInterceptor();
        }

        if (mPermissionDescription == null) {
            mPermissionDescription = getPermissionDescription();
        }

        final Context context = mContext;

        final Fragment appFragment = mAppFragment;

        final android.support.v4.app.Fragment supportFragment = mSupportFragment;

        final OnPermissionInterceptor permissionInterceptor = mPermissionInterceptor;

        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请，为了避免重复添加）
        List<IPermission> permissions = new ArrayList<>(mPermissions);

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
            PermissionChecker.checkPermissionList(activity, permissions, PermissionUtils.getAndroidManifestInfo(context));
        }

        // 优化所申请的权限列表
        PermissionApi.addOldPermissionsByNewPermissions(permissions);
        // 优化申请的权限顺序
        PermissionApi.adjustPermissionsSort(permissions);

        // 检查 Activity 是不是不可用
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        // 判断要申请的权限是否都授予了
        if (PermissionApi.isGrantedPermissions(context, permissions)) {
            // 如果是的话，就不申请权限，而是通知权限申请成功
            permissionInterceptor.grantedPermissionRequest(activity, permissions, permissions, true, callback);
            permissionInterceptor.finishPermissionRequest(activity, permissions, true, callback);
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
        permissionInterceptor.launchPermissionRequest(activity, permissions, fragmentFactory, permissionDescription, callback);
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

        final List<IPermission> permissions = mPermissions;

        if (permissions.isEmpty()) {
            return false;
        }

        if (!AndroidVersionTools.isAndroid13()) {
            return false;
        }

        try {
            if (permissions.size() == 1) {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionOnKill(java.lang.String)
                context.revokeSelfPermissionOnKill(permissions.get(0).getPermissionName());
            } else {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionsOnKill(java.util.Collection%3Cjava.lang.String%3E)
                context.revokeSelfPermissionsOnKill(PermissionUtils.convertPermissionList(permissions));
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
    public static boolean isGrantedPermission(@NonNull Context context, @NonNull IPermission permission) {
        return permission.isGranted(context);
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return isGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.isGrantedPermissions(context, permissions);
    }

    /**
     * 从权限列表中获取已授予的权限
     */
    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getGrantedPermissions(context, permissions);
    }

    /**
     * 从权限列表中获取没有授予的权限
     */
    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否为特殊权限
     */
    public static boolean isSpecialPermission(@NonNull IPermission permission) {
        return permission.getPermissionType() == PermissionType.SPECIAL;
    }

    /**
     * 判断权限列表中是否包含特殊权限
     */
    public static boolean containsSpecialPermission(@NonNull IPermission[] permissions) {
        return containsSpecialPermission(PermissionUtils.asArrayList(permissions));
    }

    public static boolean containsSpecialPermission(@NonNull List<IPermission> permissions) {
        return PermissionApi.containsSpecialPermission(permissions);
    }

    /**
     * 判断某个权限是否为后台权限
     */
    public static boolean isBackgroundPermission(@NonNull IPermission permission) {
        return PermissionApi.isBackgroundPermission(permission);
    }

    /**
     * 判断权限列表中是否包含后台权限
     */
    public static boolean containsBackgroundPermission(@NonNull IPermission[] permissions) {
        return containsBackgroundPermission(PermissionUtils.asArrayList(permissions));
    }

    public static boolean containsBackgroundPermission(@NonNull List<IPermission> permissions) {
        return PermissionApi.containsBackgroundPermission(permissions);
    }

    /**
     * 判断一个或多个权限是否被勾选了不再询问的选项
     *
     * 注意不能在请求权限之前调用，一定要在 {@link OnPermissionCallback#onDenied(List, boolean)} 方法中调用
     * 如果你在应用启动后，没有申请过这个权限，然后去判断它有没有勾选不再询问的选项，这样系统会一直返回 true，也就是不再询问
     * 但是实际上还能继续申请，系统只是不想让你知道权限是否勾选了不再询问的选项，你必须要申请过这个权限，才能去判断这个权限是否勾选了不再询问的选项
     */
    public static boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull IPermission permission) {
        return permission.isDoNotAskAgain(activity);
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull IPermission[] permissions) {
        return isDoNotAskAgainPermissions(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<IPermission> permissions) {
        return PermissionApi.isDoNotAskAgainPermissions(activity, permissions);
    }

    /**
     * 判断某个权限出现的版本是否高于当前的设备的版本
     */
    public static boolean isLowVersionRunning(@NonNull IPermission permission) {
        return permission.isLowVersionRunning();
    }

    /* android.content.Context */

    public static void startPermissionActivity(@NonNull Context context) {
        startPermissionActivity(context, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Context context, @NonNull IPermission... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(@NonNull Context context, @NonNull List<IPermission> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionApi.getBestPermissionSettingIntent(context, permissions);
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
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(activity, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        Intent intent = PermissionApi.getBestPermissionSettingIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(activity, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull IPermission permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(activity, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> {
            if (PermissionUtils.isActivityUnavailable(activity)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(@NonNull Fragment appFragment) {
        startPermissionActivity(appFragment, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(appFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(appFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getBestPermissionSettingIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(appFragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                                @NonNull IPermission permission,
                                                @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(appFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, appFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> {
            if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /* android.support.v4.app.Fragment */

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment) {
        startPermissionActivity(supportFragment, new ArrayList<>());
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(supportFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        Activity activity = supportFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(supportFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        Intent intent = PermissionApi.getBestPermissionSettingIntent(activity, permissions);
        PermissionActivityIntentHandler.startActivityForResult(supportFragment, intent, requestCode);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull IPermission permission,
                                               @Nullable OnPermissionPageCallback callback) {
        startPermissionActivity(supportFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull android.support.v4.app.Fragment supportFragment,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionPageCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        Activity activity = supportFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(supportFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            PermissionActivityIntentHandler.startActivity(supportFragment, PermissionIntentManager.getApplicationDetailsIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, supportFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionType.SPECIAL, () -> {
            if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(supportFragment)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
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
                                                        @NonNull List<IPermission> permissions,
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