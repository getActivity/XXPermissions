package com.hjq.permissions.demo;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastWhiteStyle;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : XXPermissions 权限请求框架使用案例
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化吐司工具类
        ToastUtils.init(getApplication(), new ToastWhiteStyle(getApplicationContext()));

        findViewById(R.id.btn_main_request_1).setOnClickListener(this);
        findViewById(R.id.btn_main_request_2).setOnClickListener(this);
        findViewById(R.id.btn_main_request_3).setOnClickListener(this);
        findViewById(R.id.btn_main_request_4).setOnClickListener(this);
        findViewById(R.id.btn_main_request_5).setOnClickListener(this);
        findViewById(R.id.btn_main_request_6).setOnClickListener(this);
        findViewById(R.id.btn_main_request_7).setOnClickListener(this);
        findViewById(R.id.btn_main_app_details).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_request_1:
                XXPermissions.with(this)
                        .permission(Permission.CAMERA)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    ToastUtils.show("获取拍照权限成功");
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                if (never) {
                                    ToastUtils.show("被永久拒绝授权，请手动授予拍照权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(MainActivity.this, denied);
                                } else {
                                    ToastUtils.show("获取拍照权限失败");
                                }
                            }
                        });
                break;
            case R.id.btn_main_request_2:
                XXPermissions.with(this)
                        .permission(Permission.RECORD_AUDIO)
                        .permission(Permission.Group.CALENDAR)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    ToastUtils.show("获取录音和日历权限成功");
                                } else {
                                    ToastUtils.show("获取权限成功，部分权限未正常授予");
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                if (never) {
                                    ToastUtils.show("被永久拒绝授权，请手动授予录音和日历权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(MainActivity.this, denied);
                                } else {
                                    ToastUtils.show("获取权限失败");
                                }
                            }
                        });
                break;
            case R.id.btn_main_request_3:
                long delayMillis = 0;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    delayMillis = 2000;
                    ToastUtils.show("当前版本不是 Android 11 以上，会自动变更为旧版的请求方式");
                }

                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        XXPermissions.with(MainActivity.this)
                                // 不适配 Android 11 可以这样写
                                //.permission(Permission.Group.STORAGE)
                                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                                .request(new OnPermission() {

                                    @Override
                                    public void hasPermission(List<String> granted, boolean all) {
                                        if (all) {
                                            ToastUtils.show("获取存储权限成功");
                                        }
                                    }

                                    @Override
                                    public void noPermission(List<String> denied, boolean never) {
                                        if (never) {
                                            ToastUtils.show("被永久拒绝授权，请手动授予存储权限");
                                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                            XXPermissions.startPermissionActivity(MainActivity.this, denied);
                                        } else {
                                            ToastUtils.show("获取存储权限失败");
                                        }
                                    }
                                });
                    }
                }, delayMillis);
                break;
            case R.id.btn_main_request_4:
                XXPermissions.with(this)
                        .permission(Permission.REQUEST_INSTALL_PACKAGES)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                ToastUtils.show("获取安装包权限成功");
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                ToastUtils.show("获取安装包权限失败，请手动授予权限");
                            }
                        });
                break;
            case R.id.btn_main_request_5:
                XXPermissions.with(this)
                        .permission(Permission.SYSTEM_ALERT_WINDOW)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                ToastUtils.show("获取悬浮窗权限成功");
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                ToastUtils.show("获取悬浮窗权限失败，请手动授予权限");
                            }
                        });
                break;
            case R.id.btn_main_request_6:
                XXPermissions.with(this)
                        .permission(Permission.NOTIFICATION_SERVICE)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                ToastUtils.show("获取通知栏权限成功");
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                ToastUtils.show("获取通知栏权限失败，请手动授予权限");
                            }
                        });
                break;
            case R.id.btn_main_request_7:
                XXPermissions.with(this)
                        .permission(Permission.WRITE_SETTINGS)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                ToastUtils.show("获取系统设置权限成功");
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                ToastUtils.show("获取系统设置权限失败，请手动授予权限");
                            }
                        });
                break;
            case R.id.btn_main_app_details:
                XXPermissions.startApplicationDetails(this);
                break;
            default:
                break;
        }
    }
}