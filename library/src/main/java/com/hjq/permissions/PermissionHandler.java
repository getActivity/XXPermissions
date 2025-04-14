package com.hjq.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限申请处理器
 */
@SuppressWarnings("deprecation")
public final class PermissionHandler {

    /**
     * 发起权限请求
     */
    public static void request(@NonNull Activity activity, @NonNull List<String> allPermissions,
                                @Nullable OnPermissionCallback callback, @Nullable OnPermissionInterceptor interceptor) {
        PermissionHandler permissionHandler = new PermissionHandler(activity, allPermissions);
        permissionHandler.setOnPermissionInterceptor(interceptor);
        permissionHandler.setOnPermissionCallback(callback);
        permissionHandler.startPermissionRequest();
    }

    /** 权限回调对象 */
    @Nullable
    private OnPermissionCallback mCallBack;

    /** 权限请求拦截器 */
    @Nullable
    private OnPermissionInterceptor mInterceptor;

    private final Activity mActivity;

    private final List<String> mAllPermissions;

    public PermissionHandler(@NonNull Activity activity, @NonNull List<String> allPermissions) {
        mActivity = activity;
        mAllPermissions = allPermissions;
    }

    /**
     * 设置权限监听回调监听
     */
    public void setOnPermissionCallback(@Nullable OnPermissionCallback callback) {
        mCallBack = callback;
    }

    /**
     * 设置权限请求拦截器
     */
    public void setOnPermissionInterceptor(@Nullable OnPermissionInterceptor interceptor) {
        mInterceptor = interceptor;
    }

    /**
     * 开始权限请求
     */
    public void startPermissionRequest() {
        if (mAllPermissions.isEmpty()) {
            return;
        }

        List<String> allDangerousPermissions = new ArrayList<>();
        List<String> allSpecialPermissions = new ArrayList<>();

        // 对危险权限和特殊权限进行分类
        for (String permission : mAllPermissions) {
            if (PermissionApi.isSpecialPermission(permission)) {
                allSpecialPermissions.add(permission);
            } else {
                allDangerousPermissions.add(permission);
            }
        }

        List<String> unauthorizedSpecialPermissions = getUnauthorizedSpecialPermissions(mActivity, allSpecialPermissions);
        List<List<String>> unauthorizedDangerousPermissions = getUnauthorizedDangerousPermissions(mActivity, allDangerousPermissions);

        // 判断权限集合中第一个权限是特殊权限还是危险权限，如果是特殊权限就先申请所有的特殊权限，如果是危险权限就先申请所有的危险权限
        if (PermissionHelper.isSpecialPermission(mAllPermissions.get(0))) {
            // 请求所有的特殊权限
            requestAllSpecialPermission(mActivity, unauthorizedSpecialPermissions, () -> {
                // 请求完特殊权限后，接下来请求危险权限
                requestAllDangerousPermission(mActivity, unauthorizedDangerousPermissions, this::postDelayedHandlerRequestPermissionsResult);
            });
        } else {
            // 请求所有的危险权限
            requestAllDangerousPermission(mActivity, unauthorizedDangerousPermissions, () -> {
                // 请求完危险权限后，接下来请求特殊权限
                requestAllSpecialPermission(mActivity, unauthorizedSpecialPermissions, this::postDelayedHandlerRequestPermissionsResult);
            });
        }
    }

    /**
     * 获取未授权的特殊权限
     */
    private static List<String> getUnauthorizedSpecialPermissions(@NonNull Activity activity,
                                                                    @NonNull List<String> allSpecialPermissions) {
        List<String> unauthorizedSpecialPermissions = new ArrayList<>();
        for (String permission : allSpecialPermissions) {
            if (PermissionApi.isGrantedPermission(activity, permission)) {
                // 已经授予过了，可以跳过
                continue;
            }

            // 如果当前设备的版本还没有出现过这个特殊权限，并且权限还没有授权的情况，证明这个特殊权限有向下兼容的权限
            // 这种情况就不要跳转到权限设置页，例如 MANAGE_EXTERNAL_STORAGE 权限
            if (AndroidVersion.getAndroidVersionCode() < PermissionHelper.findAndroidVersionByPermission(permission)) {
                continue;
            }

            unauthorizedSpecialPermissions.add(permission);
        }
        return unauthorizedSpecialPermissions;
    }

    /**
     * 获取未授权的危险权限
     */
    private static List<List<String>> getUnauthorizedDangerousPermissions(@NonNull Activity activity,
                                                                            @NonNull List<String> allDangerousPermissions) {
        // 已处理的危险权限列表
        List<String> alreadyProcessedDangerousPermissions = new ArrayList<>();

        // 记录需要申请的危险权限或者权限组
        List<List<String>> unauthorizedDangerousPermissions = new ArrayList<>();

        for (String dangerousPermission : allDangerousPermissions) {

            // 如果这个危险权限在前面已经处理过了，就不再处理
            if (alreadyProcessedDangerousPermissions.contains(dangerousPermission)) {
                continue;
            }
            alreadyProcessedDangerousPermissions.add(dangerousPermission);

            // 查询权限所在的权限组类型
            PermissionGroupType permissionGroupType = PermissionHelper.queryDangerousPermissionGroupType(dangerousPermission);
            if (permissionGroupType == null) {
                // 如果这个权限没有组别，就直接单独做为一次权限申请
                unauthorizedDangerousPermissions.add(PermissionUtils.asArrayList(dangerousPermission));
                continue;
            }

            // 如果这个权限有组别，那么就获取这个组别的全部权限
            List<String> dangerousPermissionGroup = new ArrayList<>(PermissionHelper.getDangerousPermissionGroup(permissionGroupType));
            // 对这个组别的权限进行逐个遍历
            Iterator<String> iterator = dangerousPermissionGroup.iterator();
            while (iterator.hasNext()) {

                String permission = iterator.next();

                if (PermissionHelper.findAndroidVersionByPermission(permission) > AndroidVersion.getAndroidVersionCode()) {
                    // 如果申请的权限是新系统才出现的，但是当前是旧系统运行，就从权限组中移除
                    iterator.remove();
                    continue;
                }

                // 判断申请的权限列表中是否有包含权限组中的权限
                if (allDangerousPermissions.contains(permission)) {
                    // 如果包含的话，就加入到已处理的列表中，这样遍历到它的时候就会忽略掉
                    alreadyProcessedDangerousPermissions.add(permission);
                } else {
                    // 如果不包含的话，就从权限组中移除
                    iterator.remove();
                }
            }

            // 如果这个权限组为空，证明剩余的权限是在高版本系统才会出现，这里无需再次发起申请
            if (dangerousPermissionGroup.isEmpty()) {
                continue;
            }

            // 如果这个权限组已经全部授权，就不纳入申请的范围内
            if (PermissionApi.isGrantedPermissions(activity, dangerousPermissionGroup)) {
                continue;
            }

            // 判断申请的权限组是否包含后台权限（例如后台定位权限，后台传感器权限），如果有的话，不能在一起申请，需要进行拆分申请
            String backgroundPermission = PermissionHelper.getBackgroundPermissionByGroup(dangerousPermissionGroup);
            if (!TextUtils.isEmpty(backgroundPermission)) {
                List<String> foregroundPermissions = new ArrayList<>(dangerousPermissionGroup);
                foregroundPermissions.remove(backgroundPermission);

                // 添加前台权限（前提得是没有授权）
                if (!foregroundPermissions.isEmpty() &&
                    !PermissionApi.isGrantedPermissions(activity, foregroundPermissions)) {
                    unauthorizedDangerousPermissions.add(foregroundPermissions);
                }
                // 添加后台权限
                unauthorizedDangerousPermissions.add(PermissionUtils.asArrayList(backgroundPermission));
                continue;
            }

            // 直接申请权限组（不区分前台权限和后台权限）
            unauthorizedDangerousPermissions.add(dangerousPermissionGroup);
        }

        return unauthorizedDangerousPermissions;
    }

    /**
     * 请求所有的特殊权限
     */
    private static void requestAllSpecialPermission(@NonNull Activity activity,
                                                    @NonNull List<String> specialPermissions,
                                                    @NonNull Runnable finishRunnable) {
        if (specialPermissions.isEmpty()) {
            finishRunnable.run();
            return;
        }

        AtomicInteger index = new AtomicInteger();
        requestSingleSpecialPermission(activity, specialPermissions.get(index.get()), new Runnable() {
            @Override
            public void run() {
                index.incrementAndGet();
                if (index.get() < specialPermissions.size()) {
                    requestSingleSpecialPermission(activity, specialPermissions.get(index.get()), this);
                    return;
                }
                finishRunnable.run();
            }
        });
    }

    /**
     * 请求单个特殊权限
     */
    private static void requestSingleSpecialPermission(@NonNull Activity activity,
                                                        @NonNull String specialPermission,
                                                        @NonNull Runnable finishRunnable) {
        RequestSpecialPermissionFragment.launch(activity, Collections.singletonList(specialPermission), new OnPermissionPageCallback() {
            @Override
            public void onGranted() {
                finishRunnable.run();
            }

            @Override
            public void onDenied() {
                finishRunnable.run();
            }
        });
    }

    /**
     * 申请所有危险权限
     */
    private static void requestAllDangerousPermission(@NonNull Activity activity,
                                                        @NonNull List<List<String>> dangerousPermissions,
                                                        @NonNull Runnable finishRunnable) {
        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念
            finishRunnable.run();
            return;
        }


        if (dangerousPermissions.isEmpty()) {
            finishRunnable.run();
            return;
        }

        AtomicInteger index = new AtomicInteger();
        requestSingleDangerousPermission(activity, dangerousPermissions.get(index.get()), new Runnable() {
            @Override
            public void run() {
                index.incrementAndGet();
                if (index.get() < dangerousPermissions.size()) {
                    long delayMillis = 0;
                    List<String> permissions = dangerousPermissions.get(index.get());
                    if (PermissionHelper.containsBackgroundPermission(permissions)) {
                        // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
                        // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
                        // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
                        delayMillis = AndroidVersion.isAndroid13() ? 150 : 0;
                    }
                    if (delayMillis == 0) {
                        requestSingleDangerousPermission(activity, permissions, this);
                    } else {
                        PermissionUtils.postDelayed(() ->
                            requestSingleDangerousPermission(activity, permissions, this), delayMillis);
                    }
                    return;
                }
                finishRunnable.run();
            }
        });
    }

    /**
     * 申请单个危险权限
     */
    private static void requestSingleDangerousPermission(@NonNull Activity activity,
                                                        @NonNull List<String> dangerousPermissions,
                                                        @NonNull Runnable finishRunnable) {
        RequestDangerousPermissionFragment.launch(activity, dangerousPermissions, (permissions, grantResults) -> finishRunnable.run());
    }

    /**
     * 延迟处理权限请求结果
     */
    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionUtils.postDelayed(this::handlePermissionRequestResult, 300);
    }

    /**
     * 处理权限请求结果
     */
    private void handlePermissionRequestResult() {
        if (mInterceptor == null) {
            return;
        }

        OnPermissionCallback callback = mCallBack;

        OnPermissionInterceptor interceptor = mInterceptor;

        List<String> allPermissions = mAllPermissions;

        int[] grantResults = new int[allPermissions.size()];
        for (int i = 0; i < grantResults.length; i++) {
            String permission = allPermissions.get(i);
            grantResults[i] = PermissionApi.isGrantedPermission(mActivity, permission) ?
                PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;

            grantResults[i] = PermissionApi.recheckPermissionResult(
                mActivity, permission, grantResults[i] == PackageManager.PERMISSION_GRANTED)
                ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        }

        // 获取已授予的权限
        List<String> grantedPermissions = PermissionApi.getGrantedPermissions(allPermissions, grantResults);

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size() == allPermissions.size()) {
            // 代表申请的所有的权限都授予了
            interceptor.grantedPermissionRequest(mActivity, allPermissions, grantedPermissions, true, callback);
            // 权限申请结束
            interceptor.finishPermissionRequest(mActivity, allPermissions, false, callback);
            return;
        }

        // 获取被拒绝的权限
        List<String> deniedPermissions = PermissionApi.getDeniedPermissions(allPermissions, grantResults);

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor.deniedPermissionRequest(mActivity, allPermissions, deniedPermissions,
            PermissionApi.isDoNotAskAgainPermissions(mActivity, deniedPermissions), callback);

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermissions.isEmpty()) {
            interceptor.grantedPermissionRequest(mActivity, allPermissions, grantedPermissions, false, callback);
        }

        // 权限申请结束
        interceptor.finishPermissionRequest(mActivity, allPermissions, false, callback);
    }
}