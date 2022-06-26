package com.hjq.permissions.demo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import com.hjq.permissions.IPermissionInterceptor;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 权限申请拦截器
 */
public final class PermissionInterceptor implements IPermissionInterceptor {

//    @Override
//    public void requestPermissions(Activity activity, OnPermissionCallback callback, List<String> allPermissions) {
//        List<String> deniedPermissions = XXPermissions.getDenied(activity, allPermissions);
//        String permissionString = PermissionNameConvert.getPermissionString(activity, deniedPermissions);
//
//        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
//        new AlertDialog.Builder(activity)
//                .setTitle(R.string.common_permission_hint)
//                .setMessage(activity.getString(R.string.common_permission_message, permissionString))
//                .setPositiveButton(R.string.common_permission_granted, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        PermissionFragment.beginRequest(activity, new ArrayList<>(allPermissions), PermissionInterceptor.this, callback);
//                    }
//                })
//                .setNegativeButton(R.string.common_permission_denied, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        if (callback != null) {
//                            callback.onDenied(deniedPermissions, false);
//                        }
//                    }
//                })
//                .show();
//    }

    @Override
    public void grantedPermissions(Activity activity, List<String> allPermissions, List<String> grantedPermissions,
                                   boolean all, OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onGranted(grantedPermissions, all);
    }

    @Override
    public void deniedPermissions(Activity activity, List<String> allPermissions, List<String> deniedPermissions,
                                  boolean never, OnPermissionCallback callback) {
        if (callback != null) {
            callback.onDenied(deniedPermissions, never);
        }

        if (never) {
            if (deniedPermissions.size() == 1 && Permission.ACCESS_MEDIA_LOCATION.equals(deniedPermissions.get(0))) {
                ToastUtils.show(R.string.common_permission_media_location_hint_fail);
                return;
            }

            showPermissionSettingDialog(activity, allPermissions, deniedPermissions, callback);
            return;
        }

        if (deniedPermissions.size() == 1 && Permission.ACCESS_BACKGROUND_LOCATION.equals(deniedPermissions.get(0))) {
            ToastUtils.show(R.string.common_permission_background_location_fail_hint);
            return;
        }

        ToastUtils.show(R.string.common_permission_fail_hint);
    }

    /**
     * 显示授权对话框
     */
    private void showPermissionSettingDialog(Activity activity, List<String> allPermissions,
                                             List<String> deniedPermissions, OnPermissionCallback callback) {
        if (activity == null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return;
        }

        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
        new AlertDialog.Builder(activity)
                .setTitle(R.string.common_permission_alert)
                .setMessage(getPermissionHint(activity, deniedPermissions))
                .setPositiveButton(R.string.common_permission_goto_setting_page, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        XXPermissions.startPermissionActivity(activity,
                                deniedPermissions, new OnPermissionPageCallback() {

                            @Override
                            public void onGranted() {
                                if (callback == null) {
                                    return;
                                }
                                callback.onGranted(allPermissions, true);
                            }

                            @Override
                            public void onDenied() {
                                showPermissionSettingDialog(activity, allPermissions,
                                        XXPermissions.getDenied(activity, allPermissions), callback);
                            }
                        });
                    }
                })
                .show();
    }

    /**
     * 根据权限获取提示
     */
    private String getPermissionHint(Context context, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return context.getString(R.string.common_permission_manual_fail_hint);
        }

        List<String> hints = PermissionNameConvert.permissionsToStrings(context, permissions);

        if (!hints.isEmpty()) {
            return context.getString(R.string.common_permission_manual_assign_fail_hint, PermissionNameConvert.listToString(hints));
        }
        return context.getString(R.string.common_permission_manual_fail_hint);
    }
}