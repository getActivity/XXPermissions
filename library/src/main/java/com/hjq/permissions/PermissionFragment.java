package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求 Fragment
 */
@SuppressWarnings("deprecation")
public final class PermissionFragment extends Fragment implements Runnable {

    /** 请求的权限组 */
    private static final String REQUEST_PERMISSIONS = "request_permissions";

    /** 请求码（自动生成）*/
    private static final String REQUEST_CODE = "request_code";

    /** 权限请求码存放集合 */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /**
     * 开启权限申请
     */
    public static void launch(@NonNull Activity activity, @NonNull ArrayList<String> permissions,
                              @NonNull IPermissionInterceptor interceptor, @Nullable OnPermissionCallback callback) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // 新版本的 Support 库限制请求码必须小于 65536
            // 旧版本的 Support 库限制请求码必须小于 256
            requestCode = new Random().nextInt((int) Math.pow(2, 8));
        } while (REQUEST_CODE_ARRAY.contains(requestCode));
        // 标记这个请求码已经被占用
        REQUEST_CODE_ARRAY.add(requestCode);
        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions);
        fragment.setArguments(bundle);
        // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
        fragment.setRetainInstance(true);
        // 设置权限申请标记
        fragment.setRequestFlag(true);
        // 设置权限回调监听
        fragment.setCallBack(callback);
        // 设置权限请求拦截器
        fragment.setInterceptor(interceptor);
        // 绑定到 Activity 上面
        fragment.attachActivity(activity);
    }

    /** 是否申请了特殊权限 */
    private boolean mSpecialRequest;

    /** 是否申请了危险权限 */
    private boolean mDangerousRequest;

    /** 权限申请标记 */
    private boolean mRequestFlag;

    /** 权限回调对象 */
    @Nullable
    private OnPermissionCallback mCallBack;

    /** 权限请求拦截器 */
    @Nullable
    private IPermissionInterceptor mInterceptor;

    /** Activity 屏幕方向 */
    private int mScreenOrientation;

    /**
     * 绑定 Activity
     */
    public void attachActivity(@NonNull Activity activity) {
        activity.getFragmentManager().beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 解绑 Activity
     */
    public void detachActivity(@NonNull Activity activity) {
        activity.getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    /**
     * 设置权限监听回调监听
     */
    public void setCallBack(@Nullable OnPermissionCallback callback) {
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
    public void setInterceptor(IPermissionInterceptor interceptor) {
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
            detachActivity(getActivity());
            return;
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mSpecialRequest) {
            return;
        }

        mSpecialRequest = true;
        requestSpecialPermission();
    }

    /**
     * 申请特殊权限
     */
    public void requestSpecialPermission() {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (arguments == null || activity == null) {
            return;
        }

        List<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);

        // 是否需要申请特殊权限
        boolean requestSpecialPermission = false;

        // 判断当前是否包含特殊权限
        for (String permission : allPermissions) {
            if (!PermissionApi.isSpecialPermission(permission)) {
                continue;
            }

            if (PermissionApi.isGrantedPermission(activity, permission)) {
                // 已经授予过了，可以跳过
                continue;
            }
            if (!AndroidVersion.isAndroid11() && PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 当前必须是 Android 11 及以上版本，因为在旧版本上是拿旧权限做的判断
                continue;
            }
            // 跳转到特殊权限授权页面
            startActivityForResult(PermissionUtils.getSmartPermissionIntent(activity,
                    PermissionUtils.asArrayList(permission)), getArguments().getInt(REQUEST_CODE));
            requestSpecialPermission = true;
        }

        if (requestSpecialPermission) {
            return;
        }
        // 如果没有跳转到特殊权限授权页面，就直接申请危险权限
        requestDangerousPermission();
    }

    /**
     * 申请危险权限
     */
    public void requestDangerousPermission() {
        Activity activity = getActivity();
        Bundle arguments = getArguments();
        if (activity == null || arguments == null) {
            return;
        }

        final int requestCode = arguments.getInt(REQUEST_CODE);

        final ArrayList<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念，则直接回调监听
            int[] grantResults = new int[allPermissions.size()];
            for (int i = 0; i < grantResults.length; i++) {
                grantResults[i] = PermissionApi.isGrantedPermission(activity, allPermissions.get(i)) ?
                        PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            }
            onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
            return;
        }

        // Android 13 传感器策略发生改变，申请后台传感器权限的前提是要有前台传感器权限
        if (AndroidVersion.isAndroid13() && allPermissions.size() >= 2 &&
                PermissionUtils.containsPermission(allPermissions, Permission.BODY_SENSORS_BACKGROUND)) {
            ArrayList<String> bodySensorsPermission = new ArrayList<>(allPermissions);
            bodySensorsPermission.remove(Permission.BODY_SENSORS_BACKGROUND);

            // 在 Android 13 的机型上，需要先申请前台传感器权限，再申请后台传感器权限
            splitTwiceRequestPermission(activity, allPermissions, bodySensorsPermission, requestCode);
            return;
        }

        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        if (AndroidVersion.isAndroid10() && allPermissions.size() >= 2 &&
                PermissionUtils.containsPermission(allPermissions, Permission.ACCESS_BACKGROUND_LOCATION)) {
            ArrayList<String> locationPermission = new ArrayList<>(allPermissions);
            locationPermission.remove(Permission.ACCESS_BACKGROUND_LOCATION);

            // 在 Android 10 的机型上，需要先申请前台定位权限，再申请后台定位权限
            splitTwiceRequestPermission(activity, allPermissions, locationPermission, requestCode);
            return;
        }

        // 必须要有文件读取权限才能申请获取媒体位置权限
        if (AndroidVersion.isAndroid10() &&
                PermissionUtils.containsPermission(allPermissions, Permission.ACCESS_MEDIA_LOCATION) &&
                PermissionUtils.containsPermission(allPermissions, Permission.READ_EXTERNAL_STORAGE)) {

            ArrayList<String> storagePermission = new ArrayList<>(allPermissions);
            storagePermission.remove(Permission.ACCESS_MEDIA_LOCATION);

            // 在 Android 10 的机型上，需要先申请存储权限，再申请获取媒体位置权限
            splitTwiceRequestPermission(activity, allPermissions, storagePermission, requestCode);
            return;
        }

        requestPermissions(allPermissions.toArray(new String[allPermissions.size() - 1]), requestCode);
    }

    /**
     * 拆分两次请求权限（有些情况下，需要先申请 A 权限，才能再申请 B 权限）
     */
    public void splitTwiceRequestPermission(@NonNull Activity activity, @NonNull ArrayList<String> allPermissions,
                                            @NonNull ArrayList<String> firstPermissions, int requestCode) {

        ArrayList<String> secondPermissions = new ArrayList<>(allPermissions);
        for (String permission : firstPermissions) {
            secondPermissions.remove(permission);
        }

        PermissionFragment.launch(activity, firstPermissions, new IPermissionInterceptor() {}, new OnPermissionCallback() {

            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (!allGranted || !isAdded()) {
                    return;
                }

                // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
                // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
                // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
                long delayMillis = AndroidVersion.isAndroid13() ? 150 : 0;
                PermissionUtils.postDelayed(() -> PermissionFragment.launch(activity, secondPermissions,
                        new IPermissionInterceptor() {}, new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted || !isAdded()) {
                            return;
                        }

                        // 所有的权限都授予了
                        int[] grantResults = new int[allPermissions.size()];
                        Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
                        onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (!isAdded()) {
                            return;
                        }

                        // 第二次申请的权限失败了，但是第一次申请的权限已经授予了
                        int[] grantResults = new int[allPermissions.size()];
                        for (int i = 0; i < allPermissions.size(); i++) {
                            grantResults[i] = PermissionUtils.containsPermission(secondPermissions, allPermissions.get(i)) ?
                                    PackageManager.PERMISSION_DENIED : PackageManager.PERMISSION_GRANTED;
                        }
                        onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
                    }

                }), delayMillis);
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                if (!isAdded()) {
                    return;
                }

                // 第一次申请的权限失败了，没有必要进行第二次申请
                int[] grantResults = new int[allPermissions.size()];
                Arrays.fill(grantResults, PackageManager.PERMISSION_DENIED);
                onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }

        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (activity == null || arguments == null || mInterceptor == null ||
                requestCode != arguments.getInt(REQUEST_CODE)) {
            return;
        }

        OnPermissionCallback callback = mCallBack;
        mCallBack = null;

        IPermissionInterceptor interceptor = mInterceptor;
        mInterceptor = null;

        // 优化权限回调结果
        PermissionUtils.optimizePermissionResults(activity, permissions, grantResults);

        // 将数组转换成 ArrayList
        List<String> allPermissions = PermissionUtils.asArrayList(permissions);

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);
        // 将 Fragment 从 Activity 移除
        detachActivity(activity);

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
                PermissionApi.isPermissionPermanentDenied(activity, deniedPermissions), callback);

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermissions.isEmpty()) {
            interceptor.grantedPermissionRequest(activity, allPermissions, grantedPermissions, false, callback);
        }

        // 权限申请结束
        interceptor.finishPermissionRequest(activity, allPermissions, false, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Activity activity = getActivity();
        Bundle arguments = getArguments();
        if (activity == null || arguments == null || mDangerousRequest ||
                requestCode != arguments.getInt(REQUEST_CODE)) {
            return;
        }

        final ArrayList<String> allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);
        if (allPermissions == null || allPermissions.isEmpty()) {
            return;
        }

        mDangerousRequest = true;
        PermissionUtils.postActivityResult(allPermissions, this);
    }

    @Override
    public void run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded()) {
            return;
        }
        // 请求其他危险权限
        requestDangerousPermission();
    }
}