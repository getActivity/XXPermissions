package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求处理类
 */
public final class PermissionFragment extends Fragment implements Runnable {

    /** 全局的 Handler 对象 */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    /** 请求的权限 */
    private static final String PERMISSION_GROUP = "permission_group";
    /** 请求码（自动生成） */
    private static final String REQUEST_CODE = "request_code";
    /** 是否不断请求 */
    private static final String REQUEST_CONSTANT = "request_constant";
    /** 回调对象存放 */
    private static SparseArray<SoftReference<OnPermission>> sCallbacks = new SparseArray<>();

    /** 是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调 */
    private boolean mCallback;

    public static PermissionFragment newInstance(ArrayList<String> permissions, boolean constant) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // Studio 编译的 APK 请求码必须小于 65536
            // Eclipse 编译的 APK 请求码必须小于 256
            requestCode = new Random().nextInt(255);
        } while (sCallbacks.get(requestCode) != null);
        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, permissions);
        bundle.putBoolean(REQUEST_CONSTANT, constant);
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

    @SuppressLint("InlinedApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);

        if (permissions == null) {
            return;
        }

        // 是否有特殊权限
        boolean specialPermission = false;

        if (permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.hasStoragePermission()) {
            // 跳转到存储权限设置界面
            startActivityForResult(PermissionSettingPage.getStoragePermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
            specialPermission = true;
        }

        if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.hasInstallPermission(getActivity())) {
            // 跳转到安装权限设置界面
            startActivityForResult(PermissionSettingPage.getInstallPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
            specialPermission = true;
        }

        if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.hasWindowPermission(getActivity())) {
            // 跳转到悬浮窗设置页面
            startActivityForResult(PermissionSettingPage.getWindowPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
            specialPermission = true;
        }

        if (permissions.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.hasNotifyPermission(getActivity())) {
            // 跳转到通知栏权限设置页面
            startActivityForResult(PermissionSettingPage.getNotifyPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
            specialPermission = true;
        }

        if (permissions.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.hasSettingPermission(getActivity())) {
            // 跳转到系统设置权限设置页面
            startActivityForResult(PermissionSettingPage.getSettingPermissionIntent(getActivity()), getArguments().getInt(REQUEST_CODE));
            specialPermission = true;
        }

        // 当前必须没有跳转到悬浮窗或者安装权限界面
        if (!specialPermission) {
            requestPermission();
        }
    }

    /**
     * 请求权限
     */
    public void requestPermission() {
        if (PermissionUtils.isAndroid6()) {
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
            if (permissions != null && permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
            }
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
            
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                // 重新检查安装权限
                grantResults[i] = PermissionUtils.hasInstallPermission(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                // 重新检查悬浮窗权限
                grantResults[i] = PermissionUtils.hasWindowPermission(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            } else if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                // 重新检查通知栏权限
                grantResults[i] = PermissionUtils.hasNotifyPermission(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            } else if (Permission.WRITE_SETTINGS.equals(permission)) {
                // 重新检查系统设置权限
                grantResults[i] = PermissionUtils.hasSettingPermission(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
            } else {
                // 重新检查 Android 11 的存储新权限
                if (PermissionUtils.isAndroid11() && PermissionUtils.containsPermission(permissions, Permission.MANAGE_EXTERNAL_STORAGE)) {
                    if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) || Permission.READ_EXTERNAL_STORAGE.equals(permission) || Permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        grantResults[i] = PermissionUtils.hasStoragePermission() ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                    }
                } else if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    grantResults[i] = XXPermissions.hasPermission(getActivity(), Permission.Group.STORAGE) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                }

                // 重新检查 Android 8.0 的两个新权限
                if (!PermissionUtils.isAndroid8()) {
                    if (Permission.ANSWER_PHONE_CALLS.equals(permission) || Permission.READ_PHONE_NUMBERS.equals(permission)) {
                        // 检查当前的安卓版本是否符合要求，如果不符合要求，那么默认没有权限
                        grantResults[i] = PackageManager.PERMISSION_DENIED;
                    }
                }
            }
        }

        // 获取授予权限
        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            // 代表申请的所有的权限都授予了
            callback.hasPermission(succeedPermissions, true);
        } else {

            // 获取拒绝权限
            List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);

            // 检查是否开启了继续申请模式，如果是则检查没有授予的权限是否还能继续申请
            if (getArguments().getBoolean(REQUEST_CONSTANT)
                    && PermissionUtils.isRequestDeniedPermission(getActivity(), failPermissions)) {

                // 如果有的话就继续申请权限，直到用户授权或者永久拒绝
                requestPermission();
                return;
            }

            // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回true给开发人员，让开发者引导用户去设置界面开启权限
            callback.noPermission(failPermissions, PermissionUtils.checkMorePermissionPermanentDenied(getActivity(), failPermissions));

            // 证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                callback.hasPermission(succeedPermissions, false);
            }
        }

        // 权限回调结束后要删除集合中的对象，避免重复请求
        sCallbacks.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mCallback && requestCode == getArguments().getInt(REQUEST_CODE) ) {
            mCallback = true;
            // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
            HANDLER.postDelayed(this, 500);
        }
    }

    @Override
    public void run() {
        // 如果用户离开太久，会导致 Activity 被回收掉，所以这里要判断当前 Fragment 是否有被添加到 Activity（可在开发者模式中开启不保留活动复现崩溃的 Bug）
        if (isAdded()) {
            // 请求其他危险权限
            requestPermission();
        }
    }
}