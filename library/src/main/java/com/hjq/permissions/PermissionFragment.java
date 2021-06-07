package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求处理类
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.M)
public final class PermissionFragment extends Fragment implements Runnable {

    /** 请求的权限组 */
    private static final String REQUEST_PERMISSIONS = "request_permissions";

    /** 请求码（自动生成）*/
    private static final String REQUEST_CODE = "request_code";

    /** 是否经过拦截器 */
    private static final String USE_INTERCEPTOR = "use_interceptor";

    /** 权限请求码存放集合 */
    private final static SparseBooleanArray REQUEST_CODE_ARRAY = new SparseBooleanArray();

    public static void beginRequest(Activity activity, ArrayList<String> permissions, OnPermissionCallback callback) {
        beginRequest(activity, permissions, true, callback);
    }

    /**
     * 开启权限申请
     */
    private static void beginRequest(Activity activity, ArrayList<String> permissions, boolean interceptor, OnPermissionCallback callback) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            requestCode = PermissionUtils.getRandomRequestCode();
        } while (REQUEST_CODE_ARRAY.get(requestCode));
        // 标记这个请求码已经被占用
        REQUEST_CODE_ARRAY.put(requestCode, true);
        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions);
        bundle.putBoolean(USE_INTERCEPTOR, interceptor);
        fragment.setArguments(bundle);
        // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
        fragment.setRetainInstance(true);
        // 设置权限回调监听
        fragment.setCallBack(callback);
        // 绑定到 Activity 上面
        fragment.attachActivity(activity);
    }

    /** 是否申请了特殊权限 */
    private boolean mSpecialRequest;

    /** 是否申请了危险权限 */
    private boolean mDangerousRequest;

    /** 权限回调对象 */
    private OnPermissionCallback mCallBack;

    /** Activity 屏幕方向 */
    private int mScreenOrientation;

    /**
     * 绑定 Activity
     */
    public void attachActivity(Activity activity) {
        activity.getFragmentManager().beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * 解绑 Activity
     */
    public void detachActivity(Activity activity) {
        activity.getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    /**
     * 设置权限监听回调监听
     */
    public void setCallBack(OnPermissionCallback callback) {
        mCallBack = callback;
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
        int activityOrientation = activity.getResources().getConfiguration().orientation;
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            if (activityOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (activityOrientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
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

        List<String> permissions = arguments.getStringArrayList(REQUEST_PERMISSIONS);

        // 是否需要申请特殊权限
        boolean requestSpecialPermission = false;

        // 判断当前是否包含特殊权限
        if (PermissionUtils.containsSpecialPermission(permissions)) {

            if (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.isGrantedStoragePermission(activity)) {
                // 当前必须是 Android 11 及以上版本，因为 hasStoragePermission 在旧版本上是拿旧权限做的判断，所以这里需要多判断一次版本
                if (PermissionUtils.isAndroid11()) {
                    // 跳转到存储权限设置界面
                    startActivityForResult(PermissionSettingPage.getStoragePermissionIntent(activity), getArguments().getInt(REQUEST_CODE));
                    requestSpecialPermission = true;
                }
            }

            if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isGrantedInstallPermission(activity)) {
                // 跳转到安装权限设置界面
                startActivityForResult(PermissionSettingPage.getInstallPermissionIntent(activity), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isGrantedWindowPermission(activity)) {
                // 跳转到悬浮窗设置页面
                startActivityForResult(PermissionSettingPage.getWindowPermissionIntent(activity), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.isGrantedNotifyPermission(activity)) {
                // 跳转到通知栏权限设置页面
                startActivityForResult(PermissionSettingPage.getNotifyPermissionIntent(activity), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.isGrantedSettingPermission(activity)) {
                // 跳转到系统设置权限设置页面
                startActivityForResult(PermissionSettingPage.getSettingPermissionIntent(activity), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }
        }

        // 当前必须没有跳转到悬浮窗或者安装权限界面
        if (!requestSpecialPermission) {
            requestDangerousPermission();
        }
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
        if (allPermissions == null || allPermissions.size() == 0) {
            return;
        }

        ArrayList<String> locationPermission = null;
        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        if (PermissionUtils.isAndroid10() && allPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {
            locationPermission = new ArrayList<>();
            if (allPermissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                locationPermission.add(Permission.ACCESS_COARSE_LOCATION);
            }

            if (allPermissions.contains(Permission.ACCESS_FINE_LOCATION)) {
                locationPermission.add(Permission.ACCESS_FINE_LOCATION);
            }
        }

        if (locationPermission == null || locationPermission.isEmpty()) {
            requestPermissions(allPermissions.toArray(new String[allPermissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
            return;
        }

        // 在 Android 10 的机型上，需要先申请前台定位权限，再申请后台定位权限
        PermissionFragment.beginRequest(activity, locationPermission, false, new OnPermissionCallback() {

            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (!all || !isAdded()) {
                    return;
                }

                // 前台定位权限授予了，现在申请后台定位权限
                PermissionFragment.beginRequest(activity,
                        PermissionUtils.asArrayList(Permission.ACCESS_BACKGROUND_LOCATION),
                        false, new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all || !isAdded()) {
                            return;
                        }

                        // 前台定位权限和后台定位权限都授予了
                        int[] grantResults = new int[allPermissions.size()];
                        Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
                        onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (!isAdded()) {
                            return;
                        }

                        // 后台定位授权失败，但是前台定位权限已经授予了
                        int[] grantResults = new int[allPermissions.size()];
                        for (int i = 0; i < allPermissions.size(); i++) {
                            grantResults[i] = Permission.ACCESS_BACKGROUND_LOCATION.equals(allPermissions.get(i)) ?
                                    PackageManager.PERMISSION_DENIED : PackageManager.PERMISSION_GRANTED;
                        }
                        onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
                    }
                });
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (!isAdded()) {
                    return;
                }

                // 前台定位授权失败，并且无法申请后台定位权限
                int[] grantResults = new int[allPermissions.size()];
                Arrays.fill(grantResults, PackageManager.PERMISSION_DENIED);
                onRequestPermissionsResult(requestCode, allPermissions.toArray(new String[0]), grantResults);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle arguments = getArguments();
        Activity activity = getActivity();
        if (activity == null || arguments == null || mCallBack == null || requestCode != arguments.getInt(REQUEST_CODE)) {
            return;
        }

        boolean useInterceptor = arguments.getBoolean(USE_INTERCEPTOR);

        OnPermissionCallback callback = mCallBack;
        mCallBack = null;

        for (int i = 0; i < permissions.length; i++) {

            String permission = permissions[i];

            if (PermissionUtils.isSpecialPermission(permission)) {
                // 如果这个权限是特殊权限，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission);
                continue;
            }

            // 重新检查 Android 10.0 的三个新权限
            if (!PermissionUtils.isAndroid10() &&
                    (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACTIVITY_RECOGNITION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission))) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission);
                continue;
            }

            // 重新检查 Android 9.0 的一个新权限
            if (!PermissionUtils.isAndroid9() &&
                    Permission.ACCEPT_HANDOVER.equals(permission)) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission);
                continue;
            }

            // 重新检查 Android 8.0 的两个新权限
            if (!PermissionUtils.isAndroid8() &&
                    (Permission.ANSWER_PHONE_CALLS.equals(permission) ||
                    Permission.READ_PHONE_NUMBERS.equals(permission))) {
                // 如果当前版本不符合最低要求，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.getPermissionStatus(activity, permission);
            }
        }

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.delete(requestCode);
        // 将 Fragment 从 Activity 移除
        detachActivity(activity);

        // 获取已授予的权限
        List<String> grantedPermission = PermissionUtils.getGrantedPermissions(permissions, grantResults);

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermission.size() == permissions.length) {
            if (useInterceptor) {
                // 代表申请的所有的权限都授予了
                XXPermissions.getInterceptor().grantedPermissions(activity, callback, grantedPermission, true);
            } else {
                callback.onGranted(grantedPermission, true);
            }
            return;
        }

        // 获取被拒绝的权限
        List<String> deniedPermission = PermissionUtils.getDeniedPermissions(permissions, grantResults);

        if (useInterceptor) {
            // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
            XXPermissions.getInterceptor().deniedPermissions(activity, callback, deniedPermission, PermissionUtils.isPermissionPermanentDenied(activity, deniedPermission));
        } else {
            callback.onDenied(deniedPermission, PermissionUtils.isPermissionPermanentDenied(activity, deniedPermission));
        }

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermission.isEmpty()) {
            if (useInterceptor) {
                XXPermissions.getInterceptor().grantedPermissions(activity, callback, grantedPermission, false);
            } else {
                callback.onDenied(grantedPermission, false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity activity = getActivity();
        Bundle arguments = getArguments();
        if (activity == null || arguments == null || requestCode != arguments.getInt(REQUEST_CODE) || mDangerousRequest) {
            return;
        }

        mDangerousRequest = true;
        // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
        activity.getWindow().getDecorView().postDelayed(this, 200);
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