package com.hjq.permissions;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限申请主要逻辑实现类
 */
@SuppressWarnings("deprecation")
final class RequestPermissionLogicPresenter {

    @NonNull
    private final Activity mActivity;

    @NonNull
    private final List<IPermission> mRequestPermissions;

    @NonNull
    private final PermissionFragmentFactory<?, ?> mFragmentFactory;

    @NonNull
    private final OnPermissionInterceptor mPermissionInterceptor;

    @NonNull
    private final OnPermissionDescription mPermissionDescription;

    @Nullable
    private final OnPermissionCallback mCallBack;

    RequestPermissionLogicPresenter(@NonNull Activity activity,
                                    @NonNull List<IPermission> requestPermissions,
                                    @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                    @NonNull OnPermissionInterceptor permissionInterceptor,
                                    @NonNull OnPermissionDescription permissionDescription,
                                    @Nullable OnPermissionCallback callback) {
        mActivity = activity;
        mRequestPermissions = requestPermissions;
        mFragmentFactory = fragmentFactory;
        mPermissionInterceptor = permissionInterceptor;
        mPermissionDescription = permissionDescription;
        mCallBack = callback;
    }

    /**
     * 开始权限请求
     */
    void request() {
        if (mRequestPermissions.isEmpty()) {
            return;
        }

        List<List<IPermission>> unauthorizedPermissions = getUnauthorizedPermissions(mActivity, mRequestPermissions);
        if (unauthorizedPermissions.isEmpty()) {
            // 证明没有权限可以请求，直接处理权限请求结果
            handlePermissionRequestResult();
            return;
        }

        Iterator<List<IPermission>> iterator = unauthorizedPermissions.iterator();
        List<IPermission> firstPermissions = null;
        while (iterator.hasNext() && (firstPermissions == null || firstPermissions.isEmpty())) {
            firstPermissions = iterator.next();
        }
        if (firstPermissions == null || firstPermissions.isEmpty()) {
            // 证明没有权限可以请求，直接处理权限请求结果
            handlePermissionRequestResult();
            return;
        }

        final Activity activity = mActivity;
        final PermissionFragmentFactory<?, ?> fragmentFactory = mFragmentFactory;
        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // 发起权限请求
        requestPermissions(activity, firstPermissions, fragmentFactory, permissionDescription, new Runnable() {
            @Override
            public void run() {
                List<IPermission> nextPermissions = null;
                while (iterator.hasNext() && (nextPermissions == null || nextPermissions.isEmpty())) {
                    nextPermissions = iterator.next();
                }

                if (nextPermissions == null || nextPermissions.isEmpty()) {
                    // 证明请求已经全部完成，延迟发送权限处理结果
                    postDelayedHandlerRequestPermissionsResult();
                    return;
                }

                // 如果下一个请求的权限是后台权限
                if (nextPermissions.size() == 1 && PermissionApi.isBackgroundPermission(nextPermissions.get(0))) {
                    List<IPermission> foregroundPermissions = PermissionHelper.queryForegroundPermissionByBackgroundPermission(nextPermissions.get(0));
                    // 如果这个后台权限对应的前台权限没有申请成功，则不要去申请后台权限，因为申请了也没有用，系统肯定不会给通过的
                    // 如果这种情况下还硬要去申请，等下还可能会触发权限说明弹窗，但是没有实际去申请权限的情况
                    if (foregroundPermissions != null && !foregroundPermissions.isEmpty() && !PermissionApi.isGrantedPermissions(activity, foregroundPermissions)) {
                        // 直接进行下一轮申请
                        this.run();
                        return;
                    }
                }

                final List<IPermission> finalPermissions = nextPermissions;
                int maxWaitTimeByPermissions = PermissionHelper.getMaxIntervalTimeByPermissions(nextPermissions);
                if (maxWaitTimeByPermissions == 0) {
                    requestPermissions(activity, finalPermissions, fragmentFactory, permissionDescription, this);
                } else {
                    PermissionTaskHandler.sendTask(() ->
                        requestPermissions(activity, finalPermissions, fragmentFactory, permissionDescription, this), maxWaitTimeByPermissions);
                }
            }
        });
    }

    /**
     * 获取未授权的危险权限
     */
    private static List<List<IPermission>> getUnauthorizedPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        // 未授权的权限列表
        List<List<IPermission>> unauthorizedPermissions = new ArrayList<>(requestPermissions.size());
        // 已处理的权限列表
        List<IPermission> alreadyDonePermissions = new ArrayList<>(requestPermissions.size());
        // 遍历需要请求的权限列表
        for (IPermission permission : requestPermissions) {

            // 如果这个权限在前面已经处理过了，就不再处理
            if (PermissionUtils.containsPermission(alreadyDonePermissions, permission)) {
                continue;
            }
            alreadyDonePermissions.add(permission);

            // 如果这个权限已授权，就不纳入申请的范围内
            if (permission.isGranted(activity)) {
                continue;
            }

            // 如果当前设备的版本还没有出现过这个特殊权限，并且权限还没有授权的情况，证明这个特殊权限有向下兼容的权限
            // 这种情况就不要跳转到权限设置页，例如 MANAGE_EXTERNAL_STORAGE 权限
            if (permission.isLowVersionRunning()) {
                continue;
            }

            // ---------------------------------- 下面处理特殊权限的逻辑 ------------------------------------------ //

            if (permission.getType() == PermissionType.SPECIAL) {
                // 如果这是一个特殊权限，那么就作为单独的一次权限进行处理
                unauthorizedPermissions.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // ---------------------------------- 下面处理危险权限的逻辑 ------------------------------------------ //

            // 查询危险权限所在的权限组类型
            PermissionGroupType permissionGroupType = PermissionHelper.queryDangerousPermissionGroupType(permission);
            if (permissionGroupType == null) {
                // 如果这个权限没有组别，就直接单独做为一次权限申请
                unauthorizedPermissions.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // 如果这个权限有组别，那么就获取这个组别的全部权限
            List<IPermission> dangerousPermissionGroup = PermissionHelper.getDangerousPermissionGroup(permissionGroupType);
            if (dangerousPermissionGroup == null) {
                // 如果权限组为空，则证明这个权限被没有被定义权限组，就直接单独做为一次权限申请
                unauthorizedPermissions.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // 待处理的权限列表
            List<IPermission> todoDangerousPermissions = null;
            // 对这个组别的权限进行逐个遍历
            Iterator<IPermission> iterator = dangerousPermissionGroup.iterator();
            while (iterator.hasNext()) {

                IPermission dangerousPermissionItem = iterator.next();

                // 判断当前权限是否在低版本（不受支持的版本）上面运行
                if (dangerousPermissionItem.isLowVersionRunning()) {
                    // 如果申请的权限是新系统才出现的，但是当前是旧系统运行，就不往下执行
                    continue;
                }

                // 判断申请的权限列表中是否有包含权限组中的权限
                if (!PermissionUtils.containsPermission(requestPermissions, dangerousPermissionItem)) {
                    // 如果不包含的话，就不往下执行
                    continue;
                }

                // 判断要申请的权限是否授予了
                if (dangerousPermissionItem.isGranted(activity)) {
                    // 如果这个权限已经授予，就不往下执行
                    // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/369
                    continue;
                }

                // 如果待处理的权限列表还没有初始化，就先进行初始化操作
                if (todoDangerousPermissions == null) {
                    todoDangerousPermissions = new ArrayList<>();
                }
                // 添加到待处理的权限列表中
                todoDangerousPermissions.add(dangerousPermissionItem);

                // 如果这个危险权限在前面已经处理过了，就不再添加
                if (PermissionUtils.containsPermission(alreadyDonePermissions, dangerousPermissionItem)) {
                    continue;
                }
                // 添加到已处理的权限列表中
                alreadyDonePermissions.add(dangerousPermissionItem);
            }

            // 如果这个待处理的权限列表为空，证明剩余的权限是在高版本系统才会出现，这里无需再次发起申请
            if (todoDangerousPermissions == null || todoDangerousPermissions.isEmpty()) {
                continue;
            }

            // 如果这个待处理的权限列表已经全部授权，就不纳入申请的范围内
            if (PermissionApi.isGrantedPermissions(activity, todoDangerousPermissions)) {
                continue;
            }

            // 判断申请的权限组是否包含后台权限（例如后台定位权限，后台传感器权限），如果有的话，不能在一起申请，需要进行拆分申请
            IPermission backgroundDangerousPermission = PermissionHelper.getBackgroundPermissionByGroup(todoDangerousPermissions);
            if (backgroundDangerousPermission == null) {
                // 如果不包含后台权限，则直接添加到待申请的列表
                unauthorizedPermissions.add(todoDangerousPermissions);
                continue;
            }

            List<IPermission> foregroundDangerousPermissions = new ArrayList<>(todoDangerousPermissions);
            foregroundDangerousPermissions.remove(backgroundDangerousPermission);

            // 添加前台权限（前提得是没有授权）
            if (!foregroundDangerousPermissions.isEmpty() &&
                !PermissionApi.isGrantedPermissions(activity, foregroundDangerousPermissions)) {
                unauthorizedPermissions.add(foregroundDangerousPermissions);
            }
            // 添加后台权限
            unauthorizedPermissions.add(PermissionUtils.asArrayList(backgroundDangerousPermission));
        }

        return unauthorizedPermissions;
    }

    /**
     * 发起一次权限请求
     */
    private static void requestPermissions(@NonNull Activity activity, List<IPermission> permissions,
                                            @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                            @NonNull OnPermissionDescription permissionDescription,
                                            @NonNull Runnable finishRunnable) {
        if (permissions.isEmpty()) {
            finishRunnable.run();
            return;
        }

        PermissionType permissionType = PermissionApi.areAllDangerousPermission(permissions) ?
                                        PermissionType.DANGEROUS : PermissionType.SPECIAL;
        if (permissionType == PermissionType.DANGEROUS && !AndroidVersionTools.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念
            finishRunnable.run();
            return;
        }

        Runnable continueRequestRunnable = () -> fragmentFactory.createAndCommitFragment(permissions, permissionType, new OnPermissionFlowCallback() {

            @Override
            public void onRequestPermissionNow() {
                permissionDescription.onRequestPermissionStart(activity, permissions);
            }

            @Override
            public void onRequestPermissionFinish() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
                finishRunnable.run();
            }

            @Override
            public void onRequestPermissionAnomaly() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
            }
        });

        permissionDescription.askWhetherRequestPermission(activity, permissions, continueRequestRunnable, finishRunnable);
    }

    /**
     * 延迟处理权限请求结果
     */
    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionTaskHandler.sendTask(this::handlePermissionRequestResult, 100);
    }

    /**
     * 延迟解锁 Activity 方向
     */
    private void postDelayedUnlockActivityOrientation(@NonNull Activity activity) {
        // 延迟执行是为了让外层回调中的代码能够顺序执行完成
        PermissionTaskHandler.sendTask(() -> ActivityOrientationControl.unlockActivityOrientation(activity), 100);
    }

    /**
     * 处理权限请求结果
     */
    private void handlePermissionRequestResult() {
        OnPermissionCallback callback = mCallBack;

        OnPermissionInterceptor interceptor = mPermissionInterceptor;

        List<IPermission> requestPermissions = mRequestPermissions;

        Activity activity = mActivity;

        // 如果当前 Activity 不可用，就不继续往下执行代码
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        List<IPermission> grantedPermissions = new ArrayList<>(requestPermissions.size());
        List<IPermission> deniedPermissions = new ArrayList<>(requestPermissions.size());
        // 遍历请求的权限，并且根据权限的授权状态进行分类
        for (IPermission permission : requestPermissions) {
            if (permission.isGranted(activity, false)) {
                grantedPermissions.add(permission);
            } else {
                deniedPermissions.add(permission);
            }
        }

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size() == requestPermissions.size()) {
            // 代表申请的所有的权限都授予了
            interceptor.grantedPermissionRequest(activity, requestPermissions, grantedPermissions, true, callback);
            // 权限申请结束
            interceptor.finishPermissionRequest(activity, requestPermissions, false, callback);
            // 延迟解锁 Activity 屏幕方向
            postDelayedUnlockActivityOrientation(activity);
            return;
        }

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor.deniedPermissionRequest(activity, requestPermissions, deniedPermissions,
            PermissionApi.isDoNotAskAgainPermissions(activity, deniedPermissions), callback);

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermissions.isEmpty()) {
            interceptor.grantedPermissionRequest(activity, requestPermissions, grantedPermissions, false, callback);
        }

        // 权限申请结束
        interceptor.finishPermissionRequest(activity, requestPermissions, false, callback);

        // 延迟解锁 Activity 屏幕方向
        postDelayedUnlockActivityOrientation(activity);
    }
}