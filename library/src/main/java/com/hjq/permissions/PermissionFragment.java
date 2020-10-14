package com.hjq.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求处理类
 */
@TargetApi(Build.VERSION_CODES.M)
public final class PermissionFragment extends Fragment {

    /** 全局的 Handler 对象 */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    /** 请求的权限 */
    private static final String PERMISSION_GROUP = "permission_group";
    /** 请求码（自动生成） */
    private static final String REQUEST_CODE = "request_code";
    /** 回调对象存放 */
    private static SparseArray<SoftReference<OnPermission>> sCallbacks = new SparseArray<>();

    /** 是否申请了特殊权限 */
    private boolean mSpecialRequest;

    /** 是否申请了危险权限 */
    private boolean mDangerousRequest;

    public static PermissionFragment newInstance(ArrayList<String> permissions) {
        PermissionFragment fragment = new PermissionFragment();
        fragment.setRetainInstance(true);
        Bundle bundle = new Bundle();
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            requestCode = PermissionUtils.getRandomRequestCode();
        } while (sCallbacks.get(requestCode) != null);
        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, permissions);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 准备请求
     */
    public void prepareRequest(Activity activity, OnPermission callback) {
        // 将当前的请求码和对象添加到集合中
        sCallbacks.put(getArguments().getInt(REQUEST_CODE), new SoftReference<>(callback));
        activity.getFragmentManager().beginTransaction().add(this, activity.getClass().getName()).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mSpecialRequest) {
            mSpecialRequest = true;
            // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
            // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
            requestSpecialPermission();
        }
    }

    /**
     * 申请危险权限
     */
    public void requestDangerousPermission() {
        ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
        if (permissions != null && permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
        }
    }

    /**
     * 申请特殊权限
     */
    public void requestSpecialPermission() {
        List<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);

        // 是否需要申请特殊权限
        boolean requestSpecialPermission = false;

        // 判断当前是否包含特殊权限
        if (PermissionUtils.containsSpecialPermission(permissions)) {

            if (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.hasStoragePermission(getActivity())) {
                // 当前必须是 Android 11 及以上版本，因为 hasStoragePermission 在旧版本上是拿旧权限做的判断，所以这里需要多判断一次版本
                if (PermissionUtils.isAndroid11()) {
                    // 跳转到存储权限设置界面
                    startActivityForResult(PermissionSettingPage.getStoragePermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
                    requestSpecialPermission = true;
                }
            }

            if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.hasInstallPermission(getActivity())) {
                // 跳转到安装权限设置界面
                startActivityForResult(PermissionSettingPage.getInstallPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.hasWindowPermission(getActivity())) {
                // 跳转到悬浮窗设置页面
                startActivityForResult(PermissionSettingPage.getWindowPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.hasNotifyPermission(getActivity())) {
                // 跳转到通知栏权限设置页面
                startActivityForResult(PermissionSettingPage.getNotifyPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }

            if (permissions.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.hasSettingPermission(getActivity())) {
                // 跳转到系统设置权限设置页面
                startActivityForResult(PermissionSettingPage.getSettingPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
                requestSpecialPermission = true;
            }
        }

        // 当前必须没有跳转到悬浮窗或者安装权限界面
        if (!requestSpecialPermission) {
            requestDangerousPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        SoftReference<OnPermission> reference = sCallbacks.get(requestCode);
        if (reference == null) {
            return;
        }
        OnPermission callback = reference.get();
        // 根据请求码取出的对象为空，就直接返回不处理
        if (callback == null) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {

            String permission = permissions[i];

            if (PermissionUtils.isSpecialPermission(permission)) {
                // 如果这个权限是特殊权限，那么就重新进行权限检测
                grantResults[i] = PermissionUtils.isPermissionGranted(getActivity(), permission) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                continue;
            }

            if (!PermissionUtils.isAndroid10()) {
                // 重新检查 Android 10.0 的三个新权限
                if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                        Permission.ACTIVITY_RECOGNITION.equals(permission) ||
                        Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                    // 如果当前版本不符合最低要求，那么就重新进行权限检测
                    grantResults[i] = PermissionUtils.isPermissionGranted(getActivity(), permission) ?
                            PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                }
            }
            if (!PermissionUtils.isAndroid8()) {
                // 重新检查 Android 8.0 的两个新权限
                if (Permission.ANSWER_PHONE_CALLS.equals(permission) ||
                        Permission.READ_PHONE_NUMBERS.equals(permission)) {
                    // 如果当前版本不符合最低要求，那么就重新进行权限检测
                    grantResults[i] = PermissionUtils.isPermissionGranted(getActivity(), permission) ?
                            PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                }
            }
        }

        // 获取授予权限
        List<String> succeedPermissions = PermissionUtils.getGrantedPermission(permissions, grantResults);
        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            // 代表申请的所有的权限都授予了
            callback.hasPermission(succeedPermissions, true);
        } else {

            // 获取拒绝权限
            List<String> failPermissions = PermissionUtils.getDeniedPermission(permissions, grantResults);

            // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回true给开发人员，让开发者引导用户去设置界面开启权限
            callback.noPermission(failPermissions, PermissionUtils.isPermissionPermanentDenied(getActivity(), failPermissions));

            // 证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                callback.hasPermission(succeedPermissions, false);
            }
        }

        // 权限回调结束后要删除集合中的对象，避免重复请求
        sCallbacks.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mDangerousRequest && requestCode == getArguments().getInt(REQUEST_CODE) ) {
            mDangerousRequest = true;
            // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 如果用户离开太久，会导致 Activity 被回收掉
                    // 所以这里要判断当前 Fragment 是否有被添加到 Activity
                    // 可在开发者模式中开启不保留活动来复现这个 Bug
                    if (isAdded()) {
                        // 请求其他危险权限
                        requestDangerousPermission();
                    }
                }
            }, 500);
        }
    }
}