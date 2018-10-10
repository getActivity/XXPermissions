package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求处理类
 */
public final class PermissionFragment extends Fragment implements Runnable {

    private static final String PERMISSION_GROUP = "permission_group";//请求的权限
    private static final String REQUEST_CODE = "request_code";
    private static final String REQUEST_CONSTANT = "request_constant";

    private final static SparseArray<OnPermission> sContainer = new SparseArray<>();

    public static PermissionFragment newInstant(ArrayList<String> permissions, boolean constant) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();

        int requestCode;
        //请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            //requestCode = new Random().nextInt(65535);//Studio编译的APK请求码必须小于65536
            requestCode = new Random().nextInt(255);//Eclipse编译的APK请求码必须小于256
        } while (sContainer.get(requestCode) != null);

        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, permissions);
        bundle.putBoolean(REQUEST_CONSTANT, constant);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 准备请求
     */
    public void prepareRequest(Activity activity, OnPermission call) {
        //将当前的请求码和对象添加到集合中
        sContainer.put(getArguments().getInt(REQUEST_CODE), call);
        activity.getFragmentManager().beginTransaction().add(this, activity.getClass().getName()).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);

        if (permissions == null) return;

        if ((permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isHasInstallPermission(getActivity()))
                || (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isHasOverlaysPermission(getActivity()))) {

            if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isHasInstallPermission(getActivity())) {
                //跳转到允许安装未知来源设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, getArguments().getInt(REQUEST_CODE));
            }

            if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isHasOverlaysPermission(getActivity())) {
                //跳转到悬浮窗设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, getArguments().getInt(REQUEST_CODE));
            }

        } else {
            requestPermission();
        }
    }

    /**
     * 请求权限
     */
    public void requestPermission() {
        if (PermissionUtils.isOverMarshmallow()) {
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
            requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        OnPermission call = sContainer.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (call == null) return;

        for (int i = 0; i < permissions.length; i++) {

            //重新检查安装权限
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permissions[i])) {
                if (PermissionUtils.isHasInstallPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
            }

            //重新检查悬浮窗权限
            if (Permission.SYSTEM_ALERT_WINDOW.equals(permissions[i])) {
                if (PermissionUtils.isHasOverlaysPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
            }

            //重新检查8.0的两个新权限
            if (permissions[i].equals(Permission.ANSWER_PHONE_CALLS) || permissions[i].equals(Permission.READ_PHONE_NUMBERS)) {

                //检查当前的安卓版本是否符合要求
                if (!PermissionUtils.isOverOreo()) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
            }
        }

        //获取授予权限
        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        //如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            //代表申请的所有的权限都授予了
            call.hasPermission(succeedPermissions, true);
        } else {

            //获取拒绝权限
            List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);

            //检查是否开启了继续申请模式，如果是则检查没有授予的权限是否还能继续申请
            if (getArguments().getBoolean(REQUEST_CONSTANT)
                    && PermissionUtils.isRequestDeniedPermission(getActivity(), failPermissions)) {

                //如果有的话就继续申请权限，直到用户授权或者永久拒绝
                requestPermission();
                return;
            }

            //代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回true给开发人员，让开发者引导用户去设置界面开启权限
            call.noPermission(failPermissions, PermissionUtils.checkMorePermissionPermanentDenied(getActivity(), failPermissions));

            //证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                call.hasPermission(succeedPermissions, false);
            }
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        sContainer.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    private boolean isBackCall;//是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (!isBackCall && requestCode == getArguments().getInt(REQUEST_CODE) ) {
            isBackCall = true;
            //需要延迟执行，不然有些华为机型授权了但是获取不到权限
            getActivity().getWindow().getDecorView().postDelayed(this, 500);
        }
    }

    /**
     * {@link Runnable#run()}
     */
    @Override
    public void run() {
        //请求其他危险权限
        requestPermission();
    }
}