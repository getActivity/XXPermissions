package com.hjq.permissions.demo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限转换器（根据权限获取对应的名称和说明）
 */
final class PermissionConverter {

    /** 权限名称映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<String, Integer> PERMISSION_NAME_MAP = new HashMap<>();

    /** 权限描述映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<Integer, Integer> PERMISSION_DESCRIPTION_MAP = new HashMap<>();

    static {
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_EXTERNAL_STORAGE.getName(), R.string.common_permission_storage);
        PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_EXTERNAL_STORAGE.getName(), R.string.common_permission_storage);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_storage, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.READ_MEDIA_IMAGES.getName(), R.string.common_permission_image_and_video);
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_MEDIA_VIDEO.getName(), R.string.common_permission_image_and_video);
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_MEDIA_VISUAL_USER_SELECTED.getName(), R.string.common_permission_image_and_video);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_image_and_video, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.READ_MEDIA_AUDIO.getName(), R.string.common_permission_music_and_audio);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_music_and_audio, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.CAMERA.getName(), R.string.common_permission_camera);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_camera, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.RECORD_AUDIO.getName(), R.string.common_permission_microphone);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_microphone, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.ACCESS_FINE_LOCATION.getName(), R.string.common_permission_location);
        PERMISSION_NAME_MAP.put(PermissionManifest.ACCESS_COARSE_LOCATION.getName(), R.string.common_permission_location);

        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  {
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_SCAN.getName(), R.string.common_permission_nearby_devices);
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_CONNECT.getName(), R.string.common_permission_nearby_devices);
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_ADVERTISE.getName(), R.string.common_permission_nearby_devices);
            // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
                PERMISSION_NAME_MAP.put(PermissionManifest.NEARBY_WIFI_DEVICES.getName(), R.string.common_permission_nearby_devices);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_description_demo);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
            } else {
                PERMISSION_NAME_MAP.put(PermissionManifest.NEARBY_WIFI_DEVICES.getName(), R.string.common_permission_location);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_description_demo);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
            }
        } else {
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_SCAN.getName(), R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_CONNECT.getName(), R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(PermissionManifest.BLUETOOTH_ADVERTISE.getName(), R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(PermissionManifest.NEARBY_WIFI_DEVICES.getName(), R.string.common_permission_location);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
        }

        PERMISSION_NAME_MAP.put(PermissionManifest.ACCESS_BACKGROUND_LOCATION.getName(), R.string.common_permission_location_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location_background, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.BODY_SENSORS.getName(), R.string.common_permission_body_sensors);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_body_sensors, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.BODY_SENSORS_BACKGROUND.getName(), R.string.common_permission_body_sensors_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_body_sensors_background, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.READ_PHONE_STATE.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.CALL_PHONE.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.ADD_VOICEMAIL.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.USE_SIP.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_PHONE_NUMBERS.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.ANSWER_PHONE_CALLS.getName(), R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(PermissionManifest.ACCEPT_HANDOVER.getName(), R.string.common_permission_phone);
        // 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)  {
            PERMISSION_NAME_MAP.put(PermissionManifest.READ_CALL_LOG.getName(), R.string.common_permission_call_logs);
            PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_CALL_LOG.getName(), R.string.common_permission_call_logs);
            PERMISSION_NAME_MAP.put(PermissionManifest.PROCESS_OUTGOING_CALLS.getName(), R.string.common_permission_call_logs);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_call_logs, R.string.common_permission_description_demo);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_description_demo);
        } else {
            PERMISSION_NAME_MAP.put(PermissionManifest.READ_CALL_LOG.getName(), R.string.common_permission_phone);
            PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_CALL_LOG.getName(), R.string.common_permission_phone);
            PERMISSION_NAME_MAP.put(PermissionManifest.PROCESS_OUTGOING_CALLS.getName(), R.string.common_permission_phone);
            // 需要注意：这里的电话权限需要补充一下前面三个通话记录权限的用途
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_description_demo);
        }

        PERMISSION_NAME_MAP.put(PermissionManifest.GET_ACCOUNTS.getName(), R.string.common_permission_contacts);
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_CONTACTS.getName(), R.string.common_permission_contacts);
        PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_CONTACTS.getName(), R.string.common_permission_contacts);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_contacts, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.READ_CALENDAR.getName(), R.string.common_permission_calendar);
        PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_CALENDAR.getName(), R.string.common_permission_calendar);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_calendar, R.string.common_permission_description_demo);

        // 注意：在 Android 10 的版本，这个权限的名称为《健身运动权限》，但是到了 Android 11 的时候，这个权限的名称被修改成了《身体活动权限》
        // 没错就改了一下权限的叫法，其他的一切没有变，Google 产品经理真的是闲的蛋疼，但是吐槽归吐槽，框架也要灵活应对一下，避免小白用户跳转到设置页找不到对应的选项
        int activityRecognitionPermissionNameStringId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? R.string.common_permission_activity_recognition_api30 : R.string.common_permission_activity_recognition_api29;
        PERMISSION_NAME_MAP.put(PermissionManifest.ACTIVITY_RECOGNITION.getName(), activityRecognitionPermissionNameStringId);
        PERMISSION_DESCRIPTION_MAP.put(activityRecognitionPermissionNameStringId, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.ACCESS_MEDIA_LOCATION.getName(), R.string.common_permission_access_media_location);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_access_media_location, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.SEND_SMS.getName(), R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(PermissionManifest.RECEIVE_SMS.getName(), R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(PermissionManifest.READ_SMS.getName(), R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(PermissionManifest.RECEIVE_WAP_PUSH.getName(), R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(PermissionManifest.RECEIVE_MMS.getName(), R.string.common_permission_sms);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_sms, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.GET_INSTALLED_APPS.getName(), R.string.common_permission_get_installed_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_get_installed_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.MANAGE_EXTERNAL_STORAGE.getName(), R.string.common_permission_all_file_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_all_file_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.REQUEST_INSTALL_PACKAGES.getName(), R.string.common_permission_install_unknown_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_install_unknown_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.SYSTEM_ALERT_WINDOW.getName(), R.string.common_permission_display_over_other_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_display_over_other_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.WRITE_SETTINGS.getName(), R.string.common_permission_modify_system_settings);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_modify_system_settings, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.NOTIFICATION_SERVICE.getName(), R.string.common_permission_allow_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.POST_NOTIFICATIONS.getName(), R.string.common_permission_post_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_post_notifications, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.BIND_NOTIFICATION_LISTENER_SERVICE.getName(), R.string.common_permission_allow_notifications_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.PACKAGE_USAGE_STATS.getName(), R.string.common_permission_apps_with_usage_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_apps_with_usage_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.SCHEDULE_EXACT_ALARM.getName(), R.string.common_permission_alarms_reminders);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_alarms_reminders, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.ACCESS_NOTIFICATION_POLICY.getName(), R.string.common_permission_do_not_disturb_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_do_not_disturb_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.getName(), R.string.common_permission_ignore_battery_optimize);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_ignore_battery_optimize, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.BIND_VPN_SERVICE.getName(), R.string.common_permission_vpn);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_vpn, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.PICTURE_IN_PICTURE.getName(), R.string.common_permission_picture_in_picture);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_picture_in_picture, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(PermissionManifest.USE_FULL_SCREEN_INTENT.getName(), R.string.common_permission_full_screen_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_full_screen_notifications, R.string.common_permission_description_demo);
    }

    /**
     * 通过权限获得名称
     */
    @NonNull
    public static String getNickNamesByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> permissionNameList = getNickNameListByPermissions(context, permissions, true);

        StringBuilder builder = new StringBuilder();
        for (String permissionName : permissionNameList) {
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(permissionName);
            } else {
                builder.append(context.getString(R.string.common_permission_comma))
                    .append(permissionName);
            }
        }
        if (builder.length() == 0) {
            // 如果没有获得到任何信息，则返回一个默认的文本
            return context.getString(R.string.common_permission_unknown);
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getNickNameListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions, boolean filterHighVersionPermissions) {
        List<String> permissionNickNameList = new ArrayList<>();
        for (IPermission permission : permissions) {
            // 如果当前设置了过滤高版本权限，并且这个权限是高版本系统才出现的权限，则不继续往下执行
            // 避免出现在低版本上面执行拒绝权限后，连带高版本的名称也一起显示出来，但是在低版本上面是没有这个权限的
            if (filterHighVersionPermissions && XXPermissions.isLowVersionRunning(permission)) {
                continue;
            }
            String permissionName = getNickNameByPermission(context, permission);
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (permissionNickNameList.contains(permissionName)) {
                continue;
            }
            permissionNickNameList.add(permissionName);
        }
        return permissionNickNameList;
    }

    public static String getNickNameByPermission(@NonNull Context context, @NonNull IPermission permission) {
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permission.getName());
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        return context.getString(permissionNameStringId);
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionsByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = getDescriptionListByPermissions(context, permissions);

        StringBuilder builder = new StringBuilder();
        for (String description : descriptionList) {
            if (TextUtils.isEmpty(description)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(description);
            } else {
                builder.append("\n")
                    .append(description);
            }
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getDescriptionListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = new ArrayList<>();
        for (IPermission permission : permissions) {
            String permissionDescription = getDescriptionByPermission(context, permission);
            if (TextUtils.isEmpty(permissionDescription)) {
                continue;
            }
            if (descriptionList.contains(permissionDescription)) {
                continue;
            }
            descriptionList.add(permissionDescription);
        }
        return descriptionList;
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionByPermission(@NonNull Context context, @NonNull IPermission permission) {
        String permissionName = permission.getName();
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permissionName);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        String permissionNickName = context.getString(permissionNameStringId);
        Integer permissionDescriptionStringId = PERMISSION_DESCRIPTION_MAP.get(permissionNameStringId);
        String permissionDescription;
        if (permissionDescriptionStringId == null || permissionDescriptionStringId == 0) {
            permissionDescription = "";
        } else {
            permissionDescription = context.getString(permissionDescriptionStringId);
        }
        return permissionNickName + context.getString(R.string.common_permission_colon) + permissionDescription;
    }
}