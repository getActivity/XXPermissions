package com.hjq.permissions.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
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

        findViewById(R.id.btn_main_request_single_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_group_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_location_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_sensors_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_activity_recognition_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bluetooth_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_wifi_devices_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_read_media_location_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_read_media_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_manage_storage_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_install_packages_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_system_alert_window_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_write_settings_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_notification_service_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_post_notification).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bind_notification_listener_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_usage_stats_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_schedule_exact_alarm_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_access_notification_policy_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_ignore_battery_optimizations_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_picture_in_picture_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_bind_vpn_service_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_request_get_installed_apps_permission).setOnClickListener(this);
        findViewById(R.id.btn_main_start_permission_activity).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_main_request_single_permission) {

            XXPermissions.with(this)
                .permission(Permission.CAMERA)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                            PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                    }
                });

        } else if (viewId == R.id.btn_main_request_group_permission) {

            XXPermissions.with(this)
                    .permission(Permission.RECORD_AUDIO)
                    .permission(Permission.READ_CALENDAR)
                    .permission(Permission.WRITE_CALENDAR)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_location_permission) {

            XXPermissions.with(this)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    // 如果不需要在后台使用定位功能，请不要申请此权限
                    .permission(Permission.ACCESS_BACKGROUND_LOCATION)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_sensors_permission) {

            XXPermissions.with(this)
                    .permission(Permission.BODY_SENSORS)
                    .permission(Permission.BODY_SENSORS_BACKGROUND)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_activity_recognition_permission) {

            XXPermissions.with(this)
                    .permission(Permission.ACTIVITY_RECOGNITION)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
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
                            .permission(Permission.BLUETOOTH_SCAN)
                            .permission(Permission.BLUETOOTH_CONNECT)
                            .permission(Permission.BLUETOOTH_ADVERTISE)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
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
                            .permission(Permission.NEARBY_WIFI_DEVICES)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_read_media_location_permission) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_10_read_media_location_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            // Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
                            // 如果 targetSdk >= 33，则添加 Permission.READ_MEDIA_IMAGES 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
                            // 如果 targetSdk < 33，则添加 Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
                            .permission(Permission.READ_MEDIA_IMAGES)
                            .permission(Permission.ACCESS_MEDIA_LOCATION)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
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
                            //.permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            // 适配分区存储应该这样写
                            .permission(Permission.READ_MEDIA_IMAGES)
                            .permission(Permission.READ_MEDIA_VIDEO)
                            .permission(Permission.READ_MEDIA_AUDIO)
                            .permission(Permission.READ_MEDIA_VISUAL_USER_SELECTED)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
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
                            //.permission(Permission.Group.STORAGE)
                            // 不适配分区存储应该这样写
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_install_packages_permission) {

            XXPermissions.with(this)
                    .permission(Permission.REQUEST_INSTALL_PACKAGES)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_system_alert_window_permission) {

            XXPermissions.with(this)
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_write_settings_permission) {

            XXPermissions.with(this)
                    .permission(Permission.WRITE_SETTINGS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_notification_service_permission) {

            XXPermissions.with(this)
                    .permission(Permission.NOTIFICATION_SERVICE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_post_notification) {

            long delayMillis = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                delayMillis = 2000;
                toast(getString(R.string.demo_android_13_post_notification_permission_hint));
            }

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    XXPermissions.with(MainActivity.this)
                            .permission(Permission.POST_NOTIFICATIONS)
                            .interceptor(new PermissionInterceptor())
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }
                                    toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                                }
                            });
                }
            }, delayMillis);

        } else if (viewId == R.id.btn_main_request_bind_notification_listener_permission) {

            XXPermissions.with(this)
                    .permission(Permission.BIND_NOTIFICATION_LISTENER_SERVICE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                toggleNotificationListenerService();
                            }
                        }
                    });

        } else if (viewId == R.id.btn_main_request_usage_stats_permission) {

            XXPermissions.with(this)
                    .permission(Permission.PACKAGE_USAGE_STATS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_schedule_exact_alarm_permission) {

            XXPermissions.with(this)
                    .permission(Permission.SCHEDULE_EXACT_ALARM)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_access_notification_policy_permission) {

            XXPermissions.with(this)
                    .permission(Permission.ACCESS_NOTIFICATION_POLICY)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_ignore_battery_optimizations_permission) {

            XXPermissions.with(this)
                    .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_picture_in_picture_permission) {

            XXPermissions.with(this)
                    .permission(Permission.PICTURE_IN_PICTURE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_bind_vpn_service_permission) {

            XXPermissions.with(this)
                    .permission(Permission.BIND_VPN_SERVICE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                        }
                    });

        } else if (viewId == R.id.btn_main_request_get_installed_apps_permission) {

            XXPermissions.with(this)
                    .permission(Permission.GET_INSTALLED_APPS)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            toast(String.format(getString(R.string.demo_obtain_permission_success_hint),
                                    PermissionNameConvert.getPermissionNames(MainActivity.this, permissions)));
                            getAppList();
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
                new ComponentName(this, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        packageManager.setComponentEnabledSetting(
                new ComponentName(this, NotificationMonitorService.class),
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