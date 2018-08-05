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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by HJQ on 2018-6-15.
 */
public final class PermissionFragment extends Fragment {

    private static final String PERMISSION_GROUP = "permission_group";//请求的权限
    private static final String REQUEST_CODE ="request_code";
    private static final String REQUEST_CONSTANT ="request_constant";

    private final static SparseArray<OnPermission> sContainer = new SparseArray<>();
    private final static int TIME_DELAY = 200;//延迟时间，用于是否是系统拒绝的
    private static long sRequestTime;//请求的时间

    private boolean requestInstall;//是否请求安装权限
    private boolean isInstallPermission;//是否有安装权限

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
        if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES)) {
            isInstallPermission = PermissionUtils.isHasInstallPermission(getActivity());
            requestInstall = !isInstallPermission;
            if (isInstallPermission) {
                handleInstallPermission();
            }else {
                //跳转到允许安装未知来源页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, getArguments().getInt(REQUEST_CODE));
            }
        }else {
            requestPermission();
        }
    }

    /**
     * 请求权限
     */
    public void requestPermission() {
        if (PermissionUtils.isOverMarshmallow()) {
            //记录本次申请时间
            sRequestTime = System.currentTimeMillis();
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
            requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        OnPermission call = sContainer.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (call == null) return;

        //是否请求了安装权限
        if (requestInstall) {

            String[] s = new String[permissions.length + 1];
            System.arraycopy(permissions, 0, s, 0, permissions.length);
            s[s.length - 1] = Permission.REQUEST_INSTALL_PACKAGES;

            int[] i = new int[grantResults.length + 1];
            System.arraycopy(grantResults, 0, i, 0, grantResults.length);
            //有请求安装权限并且被用户授予了
            if (isInstallPermission) {
                i[i.length - 1] = PackageManager.PERMISSION_GRANTED;
            }else {
                i[i.length - 1] = PackageManager.PERMISSION_DENIED;
            }

            permissions = s;
            grantResults = i;
        }

        //获取授予权限
        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        //如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            //代表申请的所有的权限都授予了
            call.hasPermission(succeedPermissions, true);
        }else {
            if (getArguments().getBoolean(REQUEST_CONSTANT) && System.currentTimeMillis() - sRequestTime > TIME_DELAY) {
                requestPermission();
                return;
            }

            //获取拒绝权限
            List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);
            //代表申请的权限中有不同意授予的，如果拒绝的时间过快证明是系统自动拒绝
            call.noPermission(failPermissions, System.currentTimeMillis() - sRequestTime < TIME_DELAY);
            //证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                call.hasPermission(succeedPermissions, false);
            }
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        sContainer.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (PermissionUtils.isOverOreo() && requestCode == getArguments().getInt(REQUEST_CODE)) {
            handleInstallPermission();
        }
    }

    private void handleInstallPermission() {

        isInstallPermission = PermissionUtils.isHasInstallPermission(getActivity());

        ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
        permissions.remove(Permission.REQUEST_INSTALL_PACKAGES);
        if (permissions.size() == 0) {
            OnPermission call = sContainer.get(getArguments().getInt(REQUEST_CODE));

            //根据请求码取出的对象为空，就直接返回不处理
            if (call == null) return;
            if (isInstallPermission) {
                //只请求了安装权限并且被授予了
                call.hasPermission(Arrays.asList(Permission.REQUEST_INSTALL_PACKAGES), true);
            }else {
                //只请求了安装权限并且被拒绝了
                call.noPermission(Arrays.asList(Permission.REQUEST_INSTALL_PACKAGES), false);
            }

            //权限回调结束后要删除集合中的对象，避免重复请求
            sContainer.remove(getArguments().getInt(REQUEST_CODE));
            getFragmentManager().beginTransaction().remove(this).commit();
        }else {
            //请求其他危险权限
            requestPermission();
        }
    }
}