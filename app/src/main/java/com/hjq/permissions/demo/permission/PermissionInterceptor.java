package com.hjq.permissions.demo.permission;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.demo.R;
import com.hjq.permissions.demo.WindowLifecycleManager;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
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
            // 回调失败给外层监听器
            callback.onDenied(deniedPermissions, doNotAskAgain);
        }

        String permissionHint = generatePermissionHint(activity, deniedPermissions, doNotAskAgain);
        if (!doNotAskAgain) {
            // 如果没有勾选不再询问选项，就弹 Toast 提示给用户
            Toaster.show(permissionHint);
            return;
        }

        // 如果勾选了不再询问选项，就弹 Dialog 引导用户去授权
        showPermissionSettingDialog(activity, requestPermissions, deniedPermissions, callback, permissionHint);
    }

    private void showPermissionSettingDialog(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions,
                                            @NonNull List<IPermission> deniedPermissions, @Nullable OnPermissionCallback callback,
                                            @NonNull String permissionHint) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        String dialogTitle  = activity.getString(R.string.common_permission_alert);
        String confirmButtonText = activity.getString(R.string.common_permission_go_to_authorization);
        DialogInterface.OnClickListener confirmListener = (dialog, which) -> {
            dialog.dismiss();
            XXPermissions.startPermissionActivity(activity, deniedPermissions, new OnPermissionPageCallback() {

                @Override
                public void onGranted() {
                    if (callback == null) {
                        return;
                    }
                    // 用户全部授权了，回调成功给外层监听器，免得用户还要再发起权限申请
                    callback.onGranted(requestPermissions, true);
                }

                @Override
                public void onDenied() {
                    List<IPermission> latestDeniedPermissions = XXPermissions.getDeniedPermissions(activity, requestPermissions);
                    // 递归显示对话框，让提示用户授权，只不过对话框是可取消的，用户不想授权了，随时可以点击返回键或者对话框蒙层来取消显示
                    showPermissionSettingDialog(activity, requestPermissions, latestDeniedPermissions, callback,
                        generatePermissionHint(activity, latestDeniedPermissions, true));
                }
            });
        };

        // 另外这里需要判断 Activity 的类型来申请权限，这是因为只有 AppCompatActivity 才能调用 Support 包的 AlertDialog 来显示，否则会出现报错
        // java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity
        // 为什么不直接用 App 包 AlertDialog 来显示，而是两套规则？因为 App 包 AlertDialog 是系统自带的类，不同 Android 版本展现的样式可能不太一样
        // 如果这个 Android 版本比较低，那么这个对话框的样式就会变得很丑，准确来讲也不能说丑，而是当时系统的 UI 设计就是那样，它只是跟随系统的样式而已
        Dialog dialog;
        if (activity instanceof AppCompatActivity) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(permissionHint)
                // 这里需要设置成可取消的，这样用户不想授权了，随时可以点击返回键或者对话框蒙层来取消显示 Dialog
                .setCancelable(true)
                .setPositiveButton(confirmButtonText, confirmListener)
                .create();
        } else {
            dialog = new Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(permissionHint)
                // 这里需要设置成可取消的，这样用户不想授权了，随时可以点击返回键或者对话框蒙层来取消显示 Dialog
                .setCancelable(true)
                .setPositiveButton(confirmButtonText, confirmListener)
                .create();
        }
        dialog.show();
        // 将 Activity 和 Dialog 生命周期绑定在一起，避免可能会出现的内存泄漏
        // 当然如果上面创建的 Dialog 已经有做了生命周期管理，则不需要执行下面这行代码
        WindowLifecycleManager.bindDialogLifecycle(activity, dialog);
    }

    /**
     * 生成权限提示文案
     */
    @NonNull
    private String generatePermissionHint(@NonNull Activity activity, @NonNull List<IPermission> deniedPermissions, boolean doNotAskAgain) {
        int deniedPermissionCount = deniedPermissions.size();
        int deniedLocationPermissionCount = 0;
        int deniedSensorsPermissionCount = 0;
        int deniedHealthPermissionCount = 0;
        for (IPermission deniedPermission : deniedPermissions) {
            String permissionGroup = deniedPermission.getPermissionGroup();
            if (TextUtils.isEmpty(permissionGroup)) {
                continue;
            }
            if (PermissionGroups.LOCATION.equals(permissionGroup)) {
                deniedLocationPermissionCount++;
            } else if (PermissionGroups.SENSORS.equals(permissionGroup)) {
                deniedSensorsPermissionCount++;
            } else if (XXPermissions.isHealthPermission(deniedPermission)) {
                deniedHealthPermissionCount++;
            }
        }

        if (deniedLocationPermissionCount == deniedPermissionCount && VERSION.SDK_INT >= VERSION_CODES.Q) {
            if (deniedLocationPermissionCount == 1) {
                if (XXPermissions.equalsPermission(deniedPermissions.get(0), PermissionNames.ACCESS_BACKGROUND_LOCATION)) {
                    return activity.getString(R.string.common_permission_fail_hint_1,
                                            activity.getString(R.string.common_permission_location_background),
                                            getBackgroundPermissionOptionLabel(activity));
                } else if (VERSION.SDK_INT >= VERSION_CODES.S &&
                    XXPermissions.equalsPermission(deniedPermissions.get(0), PermissionNames.ACCESS_FINE_LOCATION)) {
                    // 如果请求的定位权限中，既包含了精确定位权限，又包含了模糊定位权限或者后台定位权限，
                    // 但是用户只同意了模糊定位权限的情况或者后台定位权限，并没有同意精确定位权限的情况，就提示用户开启确切位置选项
                    // 需要注意的是 Android 12 才将模糊定位权限和精确定位权限的授权选项进行分拆，之前的版本没有区分得那么仔细
                    return activity.getString(R.string.common_permission_fail_hint_3,
                                            activity.getString(R.string.common_permission_location_fine),
                                            activity.getString(R.string.common_permission_location_fine_option));
                }
            } else {
                if (XXPermissions.containsPermission(deniedPermissions, PermissionNames.ACCESS_BACKGROUND_LOCATION)) {
                    if (VERSION.SDK_INT >= VERSION_CODES.S &&
                        XXPermissions.containsPermission(deniedPermissions, PermissionNames.ACCESS_FINE_LOCATION)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                                                activity.getString(R.string.common_permission_location),
                                                getBackgroundPermissionOptionLabel(activity),
                                                activity.getString(R.string.common_permission_location_fine_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                                                activity.getString(R.string.common_permission_location),
                                                getBackgroundPermissionOptionLabel(activity));
                    }
                }
            }
        } else if (deniedSensorsPermissionCount == deniedPermissionCount && VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (deniedPermissionCount == 1) {
                if (XXPermissions.equalsPermission(deniedPermissions.get(0), PermissionNames.BODY_SENSORS_BACKGROUND)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                                                activity.getString(R.string.common_permission_health_data_background),
                                                activity.getString(R.string.common_permission_health_data_background_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                                                activity.getString(R.string.common_permission_body_sensors_background),
                                                getBackgroundPermissionOptionLabel(activity));
                    }
                }
            } else {
                if (doNotAskAgain) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                                                activity.getString(R.string.common_permission_health_data),
                                                activity.getString(R.string.common_permission_allow_all_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                                                activity.getString(R.string.common_permission_body_sensors),
                                                getBackgroundPermissionOptionLabel(activity));
                    }
                }
            }
        } else if (deniedHealthPermissionCount == deniedPermissionCount && VERSION.SDK_INT >= VERSION_CODES.BAKLAVA) {
            
            switch (deniedPermissionCount) {
                case 1:
                    if (XXPermissions.equalsPermission(deniedPermissions.get(0), PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                                                activity.getString(R.string.common_permission_health_data_background),
                                                activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.equalsPermission(deniedPermissions.get(0), PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                                                activity.getString(R.string.common_permission_health_data_past),
                                                activity.getString(R.string.common_permission_health_data_past_option));
                    }
                    break;
                case 2:
                    if (XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_HISTORY) &&
                        XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                            activity.getString(R.string.common_permission_health_data_past) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_health_data_past_option) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                                        activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_past),
                                                    activity.getString(R.string.common_permission_allow_all_option),
                                                    activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                                                activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                                                activity.getString(R.string.common_permission_allow_all_option),
                                                activity.getString(R.string.common_permission_health_data_background_option));
                    }
                    break;
                default:
                    if (XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_HISTORY) &&
                        XXPermissions.containsPermission(deniedPermissions, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                            activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_past) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_allow_all_option),
                            activity.getString(R.string.common_permission_health_data_past_option) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background_option));
                    }
                    break;
            }
            return activity.getString(R.string.common_permission_fail_hint_1,
                                    activity.getString(R.string.common_permission_health_data),
                                    activity.getString(R.string.common_permission_allow_all_option));
        }

        return activity.getString(doNotAskAgain ? R.string.common_permission_fail_assign_hint_1 :
                                                R.string.common_permission_fail_assign_hint_2,
                                                PermissionConverter.getNickNamesByPermissions(activity, deniedPermissions));
    }

    /**
     * 获取后台权限的《始终允许》选项的文案
     */
    @NonNull
    private String getBackgroundPermissionOptionLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            CharSequence backgroundPermissionOptionLabel = packageManager.getBackgroundPermissionOptionLabel();
            if (!TextUtils.isEmpty(backgroundPermissionOptionLabel)) {
                return backgroundPermissionOptionLabel.toString();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return context.getString(R.string.common_permission_allow_all_the_time_option_api30);
        } else {
            return context.getString(R.string.common_permission_allow_all_the_time_option_api29);
        }
    }
}