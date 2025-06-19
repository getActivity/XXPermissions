package com.hjq.permissions.demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Insets;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.demo.example.ExampleAccessibilityService;
import com.hjq.permissions.demo.example.ExampleDeviceAdminReceiver;
import com.hjq.permissions.demo.example.ExampleNotificationListenerService;
import com.hjq.permissions.demo.permission.PermissionConverter;
import com.hjq.permissions.demo.permission.PermissionDescription;
import com.hjq.permissions.demo.permission.PermissionInterceptor;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.toast.Toaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

        TitleBar titleBar = findViewById(R.id.tb_main_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onTitleClick(TitleBar titleBar) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/getActivity/XXPermissions"));
                startActivity(intent);
            }
        });

        // 适配 Android 15 EdgeToEdge 特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            titleBar.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener()  {

                @NonNull
                @Override
                public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                    Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                    // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    v.setPadding(0, systemBars.top, 0, 0);
                    return insets;
                }
            });
        }

        findViewById(R.id.btn_main_request_single_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_group_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_multiple_type_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_location_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_sensors_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_activity_recognition_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bluetooth_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_wifi_devices_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_read_media_location_information_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_read_media_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_manage_storage_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_install_packages_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_system_alert_window_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_write_settings_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_notification_service_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_post_notifications_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bind_notification_listener_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_usage_stats_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_schedule_exact_alarm_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_access_notification_policy_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_ignore_battery_optimizations_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_picture_in_picture_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bind_vpn_service_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_full_screen_notifications_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_device_admin_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_accessibility_service_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_get_installed_apps_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_start_permission_activity).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_main_request_single_permission) {

            XXPermissions.with(this)
                .permission(PermissionManifest.getCameraPermission())
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                            PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                    }
                });

        } else if (viewId == R.id.btn_main_request_group_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getRecordAudioPermission())
                    .permission(PermissionManifest.getReadCalendarPermission())
                    .permission(PermissionManifest.getWriteCalendarPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_location_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getAccessCoarseLocationPermission())
                    .permission(PermissionManifest.getAccessFineLocationPermission())
                    // 如果不需要在后台使用定位功能，请不要申请此权限
                    .permission(PermissionManifest.getAccessBackgroundLocationPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_sensors_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getBodySensorsPermission())
                    .permission(PermissionManifest.getBodySensorsBackgroundPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_activity_recognition_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getActivityRecognitionPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                            addCountStepListener();
                        }
                    });

        } else if (viewId == R.id.btn_main_request_bluetooth_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_12_bluetooth_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            .permission(PermissionManifest.getBluetoothScanPermission())
                            .permission(PermissionManifest.getBluetoothConnectPermission())
                            .permission(PermissionManifest.getBluetoothAdvertisePermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_wifi_devices_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_13_wifi_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            .permission(PermissionManifest.getNearbyWifiDevicesPermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_read_media_location_information_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_10_read_media_location_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            // 申请 ACCESS_MEDIA_LOCATION 的前提条件：
                            // 1. 如果 targetSdk >= 33，有两种方案选择（二选一）：
                            //    a. 申请 READ_MEDIA_IMAGES 或 READ_MEDIA_VIDEO 权限，需要注意的点是
                            //       如果是在 Android 14 申请，只能选择允许访问全部的照片和视频，不能选择部分
                            //    b. 申请 MANAGE_EXTERNAL_STORAGE 权限
                            // 2. 如果 targetSdk < 33，，有两种方案选择（二选一）：
                            //    a. 则添加 READ_EXTERNAL_STORAGE
                            //    b. MANAGE_EXTERNAL_STORAGE 二选一
                            .permission(PermissionManifest.getReadMediaImagesPermission())
                            .permission(PermissionManifest.getReadMediaVideoPermission())
                            .permission(PermissionManifest.getAccessMediaLocationPermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getAllImagesFromGallery(true);
                                        }
                                    }).start();
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_read_media_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_13_read_media_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            // 不适配分区存储应该这样写
                            //.permission(PermissionManifest.getManageExternalStoragePermission())
                            // 适配分区存储应该这样写
                            .permission(PermissionManifest.getReadMediaImagesPermission())
                            .permission(PermissionManifest.getReadMediaVideoPermission())
                            .permission(PermissionManifest.getReadMediaAudioPermission())
                            .permission(PermissionManifest.getReadMediaVisualUserSelectedPermission())
                            .permission(PermissionManifest.getWriteExternalStoragePermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                    getAllImagesFromGallery(false);
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_manage_storage_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_11_manage_storage_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            // 适配分区存储应该这样写
                            //.permission(PermissionManifest.getReadExternalStoragePermission())
                            //.permission(PermissionManifest.getWriteExternalStoragePermission())
                            // 不适配分区存储应该这样写
                            .permission(PermissionManifest.getManageExternalStoragePermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_install_packages_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getRequestInstallPackagesPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_system_alert_window_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getSystemAlertWindowPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_write_settings_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getWriteSettingsPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_notification_service_permission) {


            String channelId = getString(R.string.test_notification_channel_id);
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.test_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            XXPermissions.with(this)
                    // 不需要指定通知渠道 id 这样写（两种写法只能二选一，不可以两种都写）
                    //.permission(PermissionManifest.getNotificationServicePermission())
                    // 需要指定通知渠道 id 这样写（两种写法只能二选一，不可以两种都写）
                    .permission(PermissionManifest.getNotificationServicePermission(channelId))
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_post_notifications_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_13_post_notification_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            .permission(PermissionManifest.getPostNotificationsPermission())
                            .interceptor(new PermissionInterceptor())
                            .description(new PermissionDescription())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_bind_notification_listener_permission) {

            XXPermissions.with(this)
                    // 需要指定通知监听类（推荐，两种写法只能二选一，不可以两种都写）
                    .permission(PermissionManifest.getBindNotificationListenerServicePermission(
                                                            ExampleNotificationListenerService.class))
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                            toggleNotificationListenerService();
                        }
                    });

        } else if (viewId == R.id.btn_main_request_usage_stats_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getPackageUsageStatsPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_schedule_exact_alarm_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getScheduleExactAlarmPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_access_notification_policy_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getAccessNotificationPolicyPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_ignore_battery_optimizations_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getRequestIgnoreBatteryOptimizationsPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_picture_in_picture_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getPictureInPicturePermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_bind_vpn_service_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getBindVpnServicePermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_full_screen_notifications_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_14_full_screen_notifications_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                        // 请求全屏通知权限需要携带通知权限（发送通知权限或者通知服务权限任意一个即可）同时申请
                        .permission(PermissionManifest.getPostNotificationsPermission())
                        //.permission(PermissionManifest.getNotificationServicePermission())
                        .permission(PermissionManifest.getUseFullScreenIntentPermission())
                        .interceptor(new PermissionInterceptor())
                        .description(new PermissionDescription())
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                if (!allGranted) {
                                    return;
                                }
                                toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                            }
                        });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_device_admin_permission) {

            XXPermissions.with(this)
                .permission(PermissionManifest.getBindDeviceAdminPermission(
                                                ExampleDeviceAdminReceiver.class,
                                                getString(R.string.test_device_admin_extra_add_explanation)))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                            PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                    }
                });

        } else if (viewId == R.id.btn_main_request_accessibility_service_permission) {

            XXPermissions.with(this)
                .permission(PermissionManifest.getBindAccessibilityServicePermission(
                                                        ExampleAccessibilityService.class))
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                            PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                    }
                });

        } else if (viewId == R.id.btn_main_request_get_installed_apps_permission) {

            XXPermissions.with(this)
                    .permission(PermissionManifest.getGetInstalledAppsPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                            getAppList();
                        }
                    });

        } else if (viewId == R.id.btn_main_request_multiple_type_permission) {

            XXPermissions.with(this)
                .permission(PermissionManifest.getReadCallLogPermission())
                .permission(PermissionManifest.getWriteCallLogPermission())
                .permission(PermissionManifest.getSystemAlertWindowPermission())
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                            PermissionConverter.getNickNamesByPermissions(MainActivity.this, permissions)));
                    }
                });

        } else if (viewId == R.id.btn_main_start_permission_activity) {

            XXPermissions.startPermissionActivity(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != XXPermissions.REQUEST_CODE) {
            return;
        }
        toast(getString(R.string.demo_return_activity_result_hint));
    }

    public void toast(CharSequence text) {
        Toaster.show(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void toggleNotificationListenerService() {
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, ExampleNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        packageManager.setComponentEnabledSetting(
                new ComponentName(this, ExampleNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 获取所有图片媒体
     *
     * @param acquireLatitudeAndLongitude           是否获取图片拍摄时的经纬度
     */
    private void getAllImagesFromGallery(boolean acquireLatitudeAndLongitude) {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                MediaStore.MediaColumns.TITLE, MediaStore.Images.Media.SIZE,
                MediaStore.Images.ImageColumns.LATITUDE, MediaStore.Images.ImageColumns.LONGITUDE};

        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, orderBy + " DESC");

        int idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
        int titleIndex = cursor.getColumnIndex(MediaStore.MediaColumns.TITLE);

        while (cursor.moveToNext()) {

            String filePath = cursor.getString(pathIndex);

            float[] latLong = new float[2];

            // 谷歌官方文档：https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn#location-media-captured
            Uri photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor.getString(idIndex));
            String photoTitle = cursor.getString(titleIndex);

            Log.i("XXPermissions", photoTitle + " = " + filePath);

            if (acquireLatitudeAndLongitude) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    photoUri = MediaStore.setRequireOriginal(photoUri);
                    try {
                        InputStream inputStream = getApplicationContext()
                            .getContentResolver().openInputStream(photoUri);
                        if (inputStream == null) {
                            continue;
                        }
                        ExifInterface exifInterface = new ExifInterface(inputStream);
                        // 获取图片的经纬度
                        exifInterface.getLatLong(latLong);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnsupportedOperationException e) {
                        // java.lang.UnsupportedOperationException:
                        // Caller must hold ACCESS_MEDIA_LOCATION permission to access original
                        // 经过测试，在部分手机上面申请获取媒体位置权限，如果用户选择的是 "仅在使用中允许"
                        // 那么就会导致权限是授予状态，但是调用 openInputStream 时会抛出此异常
                        e.printStackTrace();
                    }
                } else {
                    int latitudeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE);
                    int longitudeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE);
                    latLong = new float[]{cursor.getFloat(latitudeIndex), cursor.getFloat(longitudeIndex)};
                }
            }

            if (latLong[0] != 0 && latLong[1] != 0) {
                Log.i("XXPermissions", "获取到图片的经纬度：" + filePath + "，" +  Arrays.toString(latLong));
                Log.i("XXPermissions", "图片经纬度所在的地址：" + latLongToAddressString(latLong[0], latLong[1]));
            } else {
                Log.i("XXPermissions", "该图片获取不到经纬度：" + filePath);
            }
        }
        cursor.close();
    }

    /**
     * 将经纬度转换成地址
     */
    private String latLongToAddressString(float latitude, float longitude) {
        String addressString = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                addressString = strReturnedAddress.toString();
            } else {
                Log.w("XXPermissions", "没有返回地址");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("XXPermissions", "无法获取到地址");
        }
        return addressString;
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.w("onSensorChanged", "event = " + event);
            switch (event.sensor.getType()) {
                case Sensor.TYPE_STEP_COUNTER:
                    Log.w("XXPermissions", "开机以来当天总步数：" + event.values[0]);
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    if (event.values[0] == 1) {
                        Log.w("XXPermissions", "当前走了一步");
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.w("onAccuracyChanged", String.valueOf(accuracy));
        }
    };

    /**
     * 添加步数监听
     */
    private void addCountStepListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor stepSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepSensor != null) {
            manager.registerListener(mSensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (detectorSensor != null) {
            manager.registerListener(mSensorEventListener, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void getAppList() {
        try {
            PackageManager packageManager = getPackageManager();
            int flags = PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES;
            List<PackageInfo> packageInfoList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfoList = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags));
            } else {
                packageInfoList = packageManager.getInstalledPackages(flags);
            }

            for (PackageInfo info : packageInfoList) {
                Log.i("XXPermissions", "应用包名：" + info.packageName);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}