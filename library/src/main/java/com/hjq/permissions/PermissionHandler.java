package com.hjq.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        List<String> unauthorizedSpecialPermissions = new ArrayList<>();

        for (String permission : mAllPermissions) {
            if (!PermissionApi.isSpecialPermission(permission)) {
                allDangerousPermissions.add(permission);
                continue;
            }

            if (PermissionApi.isGrantedPermission(mActivity, permission)) {
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

        if (unauthorizedSpecialPermissions.isEmpty()) {
            // 如果没有需要申请的特殊权限，就直接申请危险权限
            requestAllDangerousPermission(mActivity, mAllPermissions, this::postDelayedHandlerRequestPermissionsResult);
            return;
        }

        // 判断权限集合中第一个权限是特殊权限还是危险权限，如果是特殊权限就先申请所有的特殊权限，如果是危险权限就先申请所有的危险权限
        if (PermissionHelper.isSpecialPermission(mAllPermissions.get(0))) {
            // 请求所有的特殊权限
            requestAllSpecialPermission(mActivity, unauthorizedSpecialPermissions, () -> {
                // 请求完特殊权限后，接下来请求危险权限
                requestAllDangerousPermission(mActivity, allDangerousPermissions, this::postDelayedHandlerRequestPermissionsResult);
            });
        } else {
            // 请求所有的危险权限
            requestAllDangerousPermission(mActivity, allDangerousPermissions, () -> {
                // 请求完危险权限后，接下来请求特殊权限
                requestAllSpecialPermission(mActivity, unauthorizedSpecialPermissions, this::postDelayedHandlerRequestPermissionsResult);
            });
        }
    }

    /**
     * 请求所有的特殊权限
     */
    private void requestAllSpecialPermission(@NonNull Activity activity,
                                            @NonNull List<String> specialPermissions,
                                            @NonNull Runnable finishRunnable) {
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
    private void requestSingleSpecialPermission(@NonNull Activity activity,
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
    private void requestAllDangerousPermission(@NonNull Activity activity, @NonNull List<String> dangerousPermissions, @NonNull Runnable finishRunnable) {
        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念
            finishRunnable.run();
            return;
        }

        List<String> separateRequestPermissionMap = PermissionHelper.getSeparateRequestPermissionList();
        Iterator<String> iterator = separateRequestPermissionMap.iterator();
        List<String> firstRequestPermissions = new ArrayList<>(dangerousPermissions);
        List<List<String>> multiplePermissions = new ArrayList<>();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            if (PermissionUtils.containsPermission(dangerousPermissions, permission) &&
                AndroidVersion.getAndroidVersionCode() >= PermissionHelper.findAndroidVersionByPermission(permission) &&
                !PermissionApi.isGrantedPermission(activity, permission)) {
                firstRequestPermissions.remove(permission);
                multiplePermissions.add(Collections.singletonList(permission));
            }
        }

        if (!firstRequestPermissions.isEmpty()) {
            // 将非需要单独申请的权限放置到第一个申请的选项
            multiplePermissions.add(0, firstRequestPermissions);
        }

        // 发起权限申请
        startMultipleRequestDangerousPermission(activity, multiplePermissions, finishRunnable);
    }

    /**
     * 拆分多次请求危险权限（有些权限需要单独申请，不能和其他权限混合申请，例如后台定位权限，后台传感器权限，读取媒体文件位置权限）
     */
    private void startMultipleRequestDangerousPermission(@NonNull Activity activity,
                                                @NonNull List<List<String>> dangerousPermissions,
                                                @NonNull Runnable finishRunnable) {
        AtomicInteger index = new AtomicInteger();
        requestSingleDangerousPermission(activity, dangerousPermissions.get(index.get()), new Runnable() {
            @Override
            public void run() {
                index.incrementAndGet();
                if (index.get() < dangerousPermissions.size()) {
                    long delayMillis = AndroidVersion.isAndroid13() ? 150 : 0;
                    // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
                    // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
                    // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
                    PermissionUtils.postDelayed(() ->
                        requestSingleDangerousPermission(activity,
                            dangerousPermissions.get(index.get()), this), delayMillis);
                    return;
                }
                finishRunnable.run();
            }
        });
    }

    /**
     * 申请单个危险权限
     */
    private void requestSingleDangerousPermission(@NonNull Activity activity,
                                                    @NonNull List<String> dangerousPermissions,
                                                    @NonNull Runnable finishRunnable) {
        RequestDangerousPermissionFragment.launch(activity, dangerousPermissions, (permissions, grantResults) -> {
            finishRunnable.run();
        });
    }

    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionUtils.postDelayed(this::handlePermissionRequestResult, 300);
    }

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