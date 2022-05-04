package com.hjq.permissions.demo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.hjq.permissions.IPermissionInterceptor;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.PermissionFragment;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
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
//        String hints = listToString(getPermissionNameList(activity, deniedPermissions));
//
//        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
//        new AlertDialog.Builder(activity)
//                .setTitle(R.string.common_permission_hint)
//                .setMessage(activity.getString(R.string.common_permission_message, hints))
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
        if (callback != null) {
            callback.onGranted(grantedPermissions, all);
        }
    }

    @Override
    public void deniedPermissions(Activity activity, List<String> allPermissions, List<String> deniedPermissions,
                                  boolean never, OnPermissionCallback callback) {
        if (callback != null) {
            callback.onDenied(deniedPermissions, never);
        }

        if (never) {
            showPermissionSettingDialog(activity, allPermissions, deniedPermissions, callback);
            return;
        }

        if (deniedPermissions.size() == 1 && Permission.ACCESS_BACKGROUND_LOCATION.equals(deniedPermissions.get(0))) {
            ToastUtils.show(R.string.common_permission_fail_4);
            return;
        }

        ToastUtils.show(R.string.common_permission_fail_1);
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
                .setPositiveButton(R.string.common_permission_goto, new DialogInterface.OnClickListener() {

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
            return context.getString(R.string.common_permission_fail_2);
        }

        List<String> hints = getPermissionNameList(context, permissions);

        if (!hints.isEmpty()) {
            return context.getString(R.string.common_permission_fail_3, listToString(hints));
        }
        return context.getString(R.string.common_permission_fail_2);
    }

    /**
     * 获取权限名称列表
     */
    @NonNull
    private List<String> getPermissionNameList(Context context, List<String> permissions) {
        List<String> nameList = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Permission.READ_EXTERNAL_STORAGE:
                case Permission.WRITE_EXTERNAL_STORAGE: {
                    String hint = context.getString(R.string.common_permission_storage);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.CAMERA: {
                    String hint = context.getString(R.string.common_permission_camera);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.RECORD_AUDIO: {
                    String hint = context.getString(R.string.common_permission_microphone);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.ACCESS_FINE_LOCATION:
                case Permission.ACCESS_COARSE_LOCATION:
                case Permission.ACCESS_BACKGROUND_LOCATION: {
                    String hint;
                    if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) &&
                            !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                        hint = context.getString(R.string.common_permission_location_background);
                    } else {
                        hint = context.getString(R.string.common_permission_location);
                    }
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.BLUETOOTH_SCAN:
                case Permission.BLUETOOTH_CONNECT:
                case Permission.BLUETOOTH_ADVERTISE: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        String hint = context.getString(R.string.common_permission_bluetooth);
                        if (!nameList.contains(hint)) {
                            nameList.add(hint);
                        }
                    }
                    break;
                }
                case Permission.READ_PHONE_STATE:
                case Permission.CALL_PHONE:
                case Permission.ADD_VOICEMAIL:
                case Permission.USE_SIP:
                case Permission.READ_PHONE_NUMBERS:
                case Permission.ANSWER_PHONE_CALLS: {
                    String hint = context.getString(R.string.common_permission_phone);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.GET_ACCOUNTS:
                case Permission.READ_CONTACTS:
                case Permission.WRITE_CONTACTS: {
                    String hint = context.getString(R.string.common_permission_contacts);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALENDAR:
                case Permission.WRITE_CALENDAR: {
                    String hint = context.getString(R.string.common_permission_calendar);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALL_LOG:
                case Permission.WRITE_CALL_LOG:
                case Permission.PROCESS_OUTGOING_CALLS: {
                    String hint = context.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                            R.string.common_permission_call_log : R.string.common_permission_phone);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.BODY_SENSORS: {
                    String hint = context.getString(R.string.common_permission_sensors);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.ACTIVITY_RECOGNITION: {
                    String hint = context.getString(R.string.common_permission_activity_recognition);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.SEND_SMS:
                case Permission.RECEIVE_SMS:
                case Permission.READ_SMS:
                case Permission.RECEIVE_WAP_PUSH:
                case Permission.RECEIVE_MMS: {
                    String hint = context.getString(R.string.common_permission_sms);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.MANAGE_EXTERNAL_STORAGE: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        String hint = context.getString(R.string.common_permission_manage_storage);
                        if (!nameList.contains(hint)) {
                            nameList.add(hint);
                        }
                    }
                    break;
                }
                case Permission.REQUEST_INSTALL_PACKAGES: {
                    String hint = context.getString(R.string.common_permission_install);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.SYSTEM_ALERT_WINDOW: {
                    String hint = context.getString(R.string.common_permission_window);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.WRITE_SETTINGS: {
                    String hint = context.getString(R.string.common_permission_setting);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.NOTIFICATION_SERVICE: {
                    String hint = context.getString(R.string.common_permission_notification);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.BIND_NOTIFICATION_LISTENER_SERVICE: {
                    String hint = context.getString(R.string.common_permission_notification);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.PACKAGE_USAGE_STATS: {
                    String hint = context.getString(R.string.common_permission_task);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.SCHEDULE_EXACT_ALARM: {
                    String hint = context.getString(R.string.common_permission_alarm);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.ACCESS_NOTIFICATION_POLICY: {
                    String hint = context.getString(R.string.common_permission_not_disturb);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                case Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: {
                    String hint = context.getString(R.string.common_permission_ignore_battery);
                    if (!nameList.contains(hint)) {
                        nameList.add(hint);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return nameList;
    }

    /**
     * String 列表拼接成一个字符串
     */
    private String listToString(List<String> hints) {
        if (hints == null || hints.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String text : hints) {
            if (builder.length() == 0) {
                builder.append(text);
            } else {
                builder.append("、")
                        .append(text);
            }
        }
        return builder.toString();
    }
}