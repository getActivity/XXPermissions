package com.hjq.permissions.demo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.toast.Toaster;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 权限申请拦截器
 */
public final class PermissionInterceptor implements OnPermissionInterceptor {

    @Override
    public void deniedPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
                                        @NonNull List<IPermission> deniedPermissions, boolean doNotAskAgain,
                                        @Nullable OnPermissionCallback callback) {
        if (callback != null) {
            callback.onDenied(deniedPermissions, doNotAskAgain);
        }

        if (doNotAskAgain) {
            if (deniedPermissions.size() == 1 && PermissionManifest.ACCESS_MEDIA_LOCATION.equals(deniedPermissions.get(0))) {
                Toaster.show(R.string.common_permission_media_location_hint_fail);
                return;
            }

            showPermissionSettingDialog(activity, requestPermissions, deniedPermissions, callback);
            return;
        }

        if (deniedPermissions.size() == 1) {

            IPermission deniedPermission = deniedPermissions.get(0);

            String backgroundPermissionOptionLabel = getBackgroundPermissionOptionLabel(activity);

            if (PermissionManifest.ACCESS_BACKGROUND_LOCATION.equals(deniedPermission)) {
                Toaster.show(activity.getString(R.string.common_permission_background_location_fail_hint, backgroundPermissionOptionLabel));
                return;
            }

            if (PermissionManifest.BODY_SENSORS_BACKGROUND.equals(deniedPermission)) {
                Toaster.show(activity.getString(R.string.common_permission_background_sensors_fail_hint, backgroundPermissionOptionLabel));
                return;
            }
        }

        final String message;
        String permissionNames = PermissionConverter.getNickNamesByPermissions(activity, deniedPermissions);
        if (!permissionNames.isEmpty()) {
            message = activity.getString(R.string.common_permission_fail_assign_hint, permissionNames);
        } else {
            message = activity.getString(R.string.common_permission_fail_hint);
        }
        Toaster.show(message);
    }

    private void showPermissionSettingDialog(Activity activity, List<IPermission> requestPermissions,
                                             List<IPermission> deniedPermissions, OnPermissionCallback callback) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        String message = null;

        String permissionNames = PermissionConverter.getNickNamesByPermissions(activity, deniedPermissions);
        if (!permissionNames.isEmpty()) {
            if (deniedPermissions.size() == 1) {
                IPermission deniedPermission = deniedPermissions.get(0);

                if (PermissionManifest.ACCESS_BACKGROUND_LOCATION.equals(deniedPermission)) {
                    message = activity.getString(R.string.common_permission_manual_assign_fail_background_location_hint, getBackgroundPermissionOptionLabel(activity));
                } else if (PermissionManifest.BODY_SENSORS_BACKGROUND.equals(deniedPermission)) {
                    message = activity.getString(R.string.common_permission_manual_assign_fail_background_sensors_hint, getBackgroundPermissionOptionLabel(activity));
                }
            }
            if (TextUtils.isEmpty(message)) {
                message = activity.getString(R.string.common_permission_manual_assign_fail_hint, permissionNames);
            }
        } else {
            message = activity.getString(R.string.common_permission_manual_fail_hint);
        }

        showDialog(activity, activity.getString(R.string.common_permission_alert), message, true, activity.getString(R.string.common_permission_goto_setting_page), (dialog, which) -> {
            dialog.dismiss();
            XXPermissions.startPermissionActivity(activity, deniedPermissions, new OnPermissionPageCallback() {

                @Override
                public void onGranted() {
                    if (callback == null) {
                        return;
                    }
                    callback.onGranted(requestPermissions, true);
                }

                @Override
                public void onDenied() {
                    showPermissionSettingDialog(activity, requestPermissions,
                        XXPermissions.getDeniedPermissions(activity, requestPermissions), callback);
                }
            });
        });
    }

    /**
     * 获取后台权限的《始终允许》选项的文案
     */
    @NonNull
    private String getBackgroundPermissionOptionLabel(Context context) {
        String backgroundPermissionOptionLabel = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            backgroundPermissionOptionLabel = String.valueOf(context.getPackageManager().getBackgroundPermissionOptionLabel());
        }
        if (TextUtils.isEmpty(backgroundPermissionOptionLabel)) {
            backgroundPermissionOptionLabel = context.getString(R.string.common_permission_background_default_option_label);
        }
        return backgroundPermissionOptionLabel;
    }

    private void showDialog(@NonNull Activity activity, @Nullable String dialogTitle, @Nullable String dialogMessage, boolean dialogCancelable,
                            @Nullable String confirmButtonText, @Nullable DialogInterface.OnClickListener confirmListener) {
        showDialog(activity, dialogTitle, dialogMessage, dialogCancelable, confirmButtonText, confirmListener, null, null);
    }

    /**
     * 显示对话框
     *
     * @param activity                  Activity 对象
     * @param dialogTitle               对话框标题
     * @param dialogMessage             对话框消息
     * @param dialogCancelable          对话框是否可取消
     * @param confirmButtonText         对话框确认按钮文本
     * @param confirmListener           对话框确认按钮点击事件
     * @param cancelButtonText          对话框取消按钮文本
     * @param cancelListener            对话框取消按钮点击事件
     */
    private void showDialog(@NonNull Activity activity, @Nullable String dialogTitle, @Nullable String dialogMessage, boolean dialogCancelable,
                            @Nullable String confirmButtonText, @Nullable DialogInterface.OnClickListener confirmListener,
                            @Nullable String cancelButtonText, @Nullable DialogInterface.OnClickListener cancelListener) {
        // 另外这里需要判断 Activity 的类型来申请权限，这是因为只有 AppCompatActivity 才能调用 Support 包的 AlertDialog 来显示，否则会出现报错
        // java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity
        // 为什么不直接用 App 包 AlertDialog 来显示，而是两套规则？因为 App 包 AlertDialog 是系统自带的类，不同 Android 版本展现的样式可能不太一样
        // 如果这个 Android 版本比较低，那么这个对话框的样式就会变得很丑，准确来讲也不能说丑，而是当时系统的 UI 设计就是那样，它只是跟随系统的样式而已
        Dialog dialog;
        if (activity instanceof AppCompatActivity) {
            dialog = new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(dialogCancelable)
                .setPositiveButton(confirmButtonText, confirmListener)
                .setNegativeButton(cancelButtonText, cancelListener)
                .create();
        } else {
            dialog = new Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(dialogCancelable)
                .setPositiveButton(confirmButtonText, confirmListener)
                .setNegativeButton(cancelButtonText, cancelListener)
                .create();
        }
        dialog.show();
        // 将 Activity 和 Dialog 生命周期绑定在一起，避免可能会出现的内存泄漏
        // 当然如果上面创建的 Dialog 已经有做了生命周期管理，则不需要执行下面这行代码
        WindowLifecycleManager.bindDialogLifecycle(activity, dialog);
    }
}