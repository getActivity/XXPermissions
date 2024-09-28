package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 运行时权限（包含危险权限和特殊权限）申请专用的 Fragment
 */
@SuppressWarnings("deprecation")
public final class RequestRuntimePermissionFragment extends Fragment {

    /** 请求的权限组 */
    private static final String REQUEST_PERMISSIONS = "request_permissions";

    /** 请求码（自动生成）*/
    private static final String REQUEST_CODE = "request_code";

    /** 权限请求码存放集合 */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /**
     * 开启权限申请
     */
    public static void launch(@NonNull Activity activity, @NonNull List<String> permissions,
                                @NonNull OnPermissionInterceptor interceptor,
                                @Nullable OnPermissionCallback callback) {
        RequestRuntimePermissionFragment fragment = new RequestRuntimePermissionFragment();
        int requestCode;
        Random random = new Random();
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // 新版本的 Support 库限制请求码必须小于 65536
            // 旧版本的 Support 库限制请求码必须小于 256
            requestCode = random.nextInt((int) Math.pow(2, 8));
        } while (REQUEST_CODE_ARRAY.contains(requestCode));
        // 标记这个请求码已经被占用
        REQUEST_CODE_ARRAY.add(requestCode);

        Bundle bundle = new Bundle();
        bundle.putInt(REQUEST_CODE, requestCode);
        if (permissions instanceof ArrayList) {
            bundle.putStringArrayList(REQUEST_PERMISSIONS, (ArrayList<String>) permissions);
        } else {
            bundle.putStringArrayList(REQUEST_PERMISSIONS, new ArrayList<>(permissions));
        }
        fragment.setArguments(bundle);
        // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
        fragment.setRetainInstance(true);
        // 设置权限申请标记
        fragment.setRequestFlag(true);
        // 设置权限回调监听
        fragment.setOnPermissionCallback(callback);
        // 设置权限请求拦截器
        fragment.setOnPermissionInterceptor(interceptor);
        // 绑定到 Activity 上面
        fragment.attachByActivity(activity);
    }

    /** 权限请求是否已经发起 */
    private boolean mAlreadyRequest;

    /** 权限申请标记 */
    private boolean mRequestFlag;

    /** 权限回调对象 */
    @Nullable
    private OnPermissionCallback mCallBack;

    /** 权限请求拦截器 */
    @Nullable
    private OnPermissionInterceptor mInterceptor;

    /** Activity 屏幕方向 */
    private int mScreenOrientation;

    /**
     * 绑定 Activity
     */
    public void attachByActivity(@NonNull Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 解绑 Activity
     */
    public void detachByActivity(@NonNull Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    /**
     * 设置权限监听回调监听
     */
    public void setOnPermissionCallback(@Nullable OnPermissionCallback callback) {
        mCallBack = callback;
    }

    /**
     * 权限申请标记（防止系统杀死应用后重新触发请求的问题）
     */
    public void setRequestFlag(boolean flag) {
        mRequestFlag = flag;
    }

    /**
     * 设置权限请求拦截器
     */
    public void setOnPermissionInterceptor(@Nullable OnPermissionInterceptor interceptor) {
        mInterceptor = interceptor;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        mScreenOrientation = activity.getRequestedOrientation();
        if (mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }

        // 锁定当前 Activity 方向
        PermissionUtils.lockActivityOrientation(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ||
            activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        // 为什么这里不用跟上面一样 try catch ？因为这里是把 Activity 方向取消固定，只有设置横屏或竖屏的时候才可能触发 crash
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消引用监听器，避免内存泄漏
        mCallBack = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachByActivity(getActivity());
            return;
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mAlreadyRequest) {
            return;
        }

        mAlreadyRequest = true;
        startPermissionRequest();
    }

    /**
     * 开始权限请求
     */
    private void startPermissionRequest() {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (arguments == null || activity == null) {
            return;
        }

        List<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        final int requestCode = arguments.getInt(REQUEST_CODE);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        List<String> needRequestSpecialPermissions = new ArrayList<>(allPermissions.size());

        for (String permission : allPermissions) {
            if (!PermissionApi.isSpecialPermission(permission)) {
                continue;
            }

            if (PermissionApi.isGrantedPermission(activity, permission)) {
                // 已经授予过了，可以跳过
                continue;
            }

            // 如果当前设备的版本还没有出现过这个特殊权限，并且权限还没有授权的情况，证明这个特殊权限有向下兼容的权限
            // 这种情况就不要跳转到权限设置页，例如 MANAGE_EXTERNAL_STORAGE 权限
            if (AndroidVersion.getAndroidVersionCode() < PermissionHelper.findAndroidVersionByPermission(permission)) {
                continue;
            }

            needRequestSpecialPermissions.add(permission);
        }

        if (needRequestSpecialPermissions.isEmpty()) {
            // 如果没有需要申请的特殊权限，就直接申请危险权限
            requestAllDangerousPermission(activity, requestCode, allPermissions);
            return;
        }

        // 请求所有的特殊权限
        requestAllSpecialPermission(activity, needRequestSpecialPermissions, () -> {
            // 请求完特殊权限后，接下来请求危险权限
            requestAllDangerousPermission(activity, requestCode, allPermissions);
        });
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
    private void requestAllDangerousPermission(@NonNull Activity activity, int requestCode,
                                                @NonNull List<String> allPermissions) {
        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念，则直接回调监听
            int[] grantResults = new int[allPermissions.size()];
            for (int i = 0; i < grantResults.length; i++) {
                grantResults[i] = PermissionApi.isGrantedPermission(activity, allPermissions.get(i)) ?
                    PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            }
            onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[allPermissions.size()]), grantResults);
            return;
        }

        List<String> separateRequestPermissionMap = PermissionHelper.getSeparateRequestPermissionList();
        Iterator<String> iterator = separateRequestPermissionMap.iterator();
        List<String> firstRequestPermissions = new ArrayList<>(allPermissions);
        List<List<String>> multiplePermissions = new ArrayList<>();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            if (PermissionUtils.containsPermission(allPermissions, permission) &&
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

        // 判断是否需要拆分多次申请权限
        if (multiplePermissions.size() > 1) {
            startMultipleRequestPermission(activity, multiplePermissions, () -> {
                // 请求完成后，直接回调 onRequestPermissionsResult 方法
                int[] grantResults = new int[allPermissions.size()];
                for (int i = 0; i < allPermissions.size(); i++) {
                    grantResults[i] = PermissionApi.isGrantedPermission(activity, allPermissions.get(i)) ?
                        PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                }
                onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[allPermissions.size()]), grantResults);
            });
            return;
        }

        // 如果不需要的话就直接申请全部的危险权限
        requestPermissions(allPermissions.toArray(new String[allPermissions.size()]), requestCode);
    }

    /**
     * 拆分多次请求权限（有些权限需要单独申请，不能和其他权限混合申请，例如后台定位权限，后台传感器权限，读取媒体文件位置权限）
     */
    private void startMultipleRequestPermission(@NonNull Activity activity,
                                                @NonNull List<List<String>> multiplePermissions,
                                                @NonNull Runnable finishRunnable) {
        AtomicInteger index = new AtomicInteger();
        requestSingleDangerousPermission(activity, multiplePermissions.get(index.get()), new Runnable() {
            @Override
            public void run() {
                index.incrementAndGet();
                if (index.get() < multiplePermissions.size()) {
                    long delayMillis = AndroidVersion.isAndroid13() ? 150 : 0;
                    // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
                    // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
                    // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
                    PermissionUtils.postDelayed(() ->
                        requestSingleDangerousPermission(activity,
                            multiplePermissions.get(index.get()), this), delayMillis);
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
                                                    @NonNull List<String> permissions,
                                                    @NonNull Runnable finishRunnable) {
        RequestRuntimePermissionFragment.launch(activity, permissions, new OnPermissionInterceptor() {}, new OnPermissionCallback() {

            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (!allGranted || !isAdded()) {
                    return;
                }
                finishRunnable.run();
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                if (!isAdded()) {
                    return;
                }
                finishRunnable.run();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (activity == null || arguments == null || mInterceptor == null ||
            requestCode != arguments.getInt(REQUEST_CODE)) {
            return;
        }

        OnPermissionCallback callback = mCallBack;
        // 释放监听对象的引用
        mCallBack = null;

        OnPermissionInterceptor interceptor = mInterceptor;
        // 释放拦截器对象的引用
        mInterceptor = null;

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);

        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/236
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            grantResults[i] = PermissionApi.recheckPermissionResult(
                activity, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED)
                ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        }

        // 将数组转换成 ArrayList
        List<String> allPermissions = PermissionUtils.asArrayList(permissions);

        // 将 Fragment 从 Activity 移除
        detachByActivity(activity);

        // 获取已授予的权限
        List<String> grantedPermissions = PermissionApi.getGrantedPermissions(allPermissions, grantResults);

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size() == allPermissions.size()) {
            // 代表申请的所有的权限都授予了
            interceptor.grantedPermissionRequest(activity, allPermissions, grantedPermissions, true, callback);
            // 权限申请结束
            interceptor.finishPermissionRequest(activity, allPermissions, false, callback);
            return;
        }

        // 获取被拒绝的权限
        List<String> deniedPermissions = PermissionApi.getDeniedPermissions(allPermissions, grantResults);

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor.deniedPermissionRequest(activity, allPermissions, deniedPermissions,
            PermissionApi.isDoNotAskAgainPermissions(activity, deniedPermissions), callback);

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermissions.isEmpty()) {
            interceptor.grantedPermissionRequest(activity, allPermissions, grantedPermissions, false, callback);
        }

        // 权限申请结束
        interceptor.finishPermissionRequest(activity, allPermissions, false, callback);
    }
}