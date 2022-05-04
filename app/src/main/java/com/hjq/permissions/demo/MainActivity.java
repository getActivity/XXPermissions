package com.hjq.permissions.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限申请演示
 */
public final class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_main_request_single).setOnClickListener(this);
        findViewById(R.id.btn_main_request_group).setOnClickListener(this);
        findViewById(R.id.btn_main_request_location).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bluetooth).setOnClickListener(this);
        findViewById(R.id.btn_main_request_storage).setOnClickListener(this);
        findViewById(R.id.btn_main_request_install).setOnClickListener(this);
        findViewById(R.id.btn_main_request_window).setOnClickListener(this);
        findViewById(R.id.btn_main_request_setting).setOnClickListener(this);
        findViewById(R.id.btn_main_request_notification).setOnClickListener(this);
        findViewById(R.id.btn_main_request_notification_listener).setOnClickListener(this);
        findViewById(R.id.btn_main_request_package).setOnClickListener(this);
        findViewById(R.id.btn_main_request_alarm).setOnClickListener(this);
        findViewById(R.id.btn_main_request_not_disturb).setOnClickListener(this);
        findViewById(R.id.btn_main_request_ignore_battery).setOnClickListener(this);
        findViewById(R.id.btn_main_app_details).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_main_request_single) {

            XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                toast("获取拍照权限成功");
                            }
                        }
                    });

        } else if (viewId == R.id.btn_main_request_group) {

            XXPermissions.with(this)
                    .permission(Permission.RECORD_AUDIO)
                    .permission(Permission.Group.CALENDAR)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                toast("获取录音和日历权限成功");
                            }
                        }
                    });

        } else if (viewId == R.id.btn_main_request_location) {

            XXPermissions.with(this)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    // 如果不需要在后台使用定位功能，请不要申请此权限
                    .permission(Permission.ACCESS_BACKGROUND_LOCATION)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                toast("获取定位权限成功");
                            }
                        }
                    });

        } else if (viewId == R.id.btn_main_request_bluetooth) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delayMillis = 2000;
                toast("当前版本不是 Android 12 及以上，旧版本的需要定位权限才能进行扫描蓝牙");
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            .permission(Permission.BLUETOOTH_SCAN)
                            .permission(Permission.BLUETOOTH_CONNECT)
                            .permission(Permission.BLUETOOTH_ADVERTISE)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        toast("获取蓝牙权限成功");
                                    }
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_storage) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                delayMillis = 2000;
                toast("当前版本不是 Android 11 及以上，会自动变更为旧版的请求方式");
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            // 适配 Android 11 分区存储这样写
                            //.permission(Permission.Group.STORAGE)
                            // 不适配 Android 11 分区存储这样写
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        toast("获取存储权限成功");
                                    }
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_install) {

            XXPermissions.with(this)
                    .permission(Permission.REQUEST_INSTALL_PACKAGES)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取安装包权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_window) {

            XXPermissions.with(this)
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取悬浮窗权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_setting) {

            XXPermissions.with(this)
                    .permission(Permission.WRITE_SETTINGS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取系统设置权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_notification) {

            XXPermissions.with(this)
                    .permission(Permission.NOTIFICATION_SERVICE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取通知栏权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_notification_listener) {

            XXPermissions.with(this)
                    .permission(Permission.BIND_NOTIFICATION_LISTENER_SERVICE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取通知栏监听权限成功");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                toggleNotificationListenerService();
                            }
                        }
                    });

        } else if (viewId == R.id.btn_main_request_package) {

            XXPermissions.with(this)
                    .permission(Permission.PACKAGE_USAGE_STATS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取使用统计权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_alarm) {

            XXPermissions.with(this)
                    .permission(Permission.SCHEDULE_EXACT_ALARM)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取闹钟权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_not_disturb) {

            XXPermissions.with(this)
                    .permission(Permission.ACCESS_NOTIFICATION_POLICY)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取勿扰权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_request_ignore_battery) {

            XXPermissions.with(this)
                    .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            toast("获取忽略电池优化权限成功");
                        }
                    });

        } else if (viewId == R.id.btn_main_app_details) {

            XXPermissions.startPermissionActivity(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            toast("检测到你刚刚从权限设置界面返回回来");
        }
    }

    public void toast(CharSequence text) {
        ToastUtils.show(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void toggleNotificationListenerService() {
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        packageManager.setComponentEnabledSetting(
                new ComponentName(this, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}