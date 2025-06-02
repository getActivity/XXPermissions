package com.hjq.permissions.demo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
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
        PERMISSION_NAME_MAP.put(Permission.READ_EXTERNAL_STORAGE, R.string.common_permission_storage);
        PERMISSION_NAME_MAP.put(Permission.WRITE_EXTERNAL_STORAGE, R.string.common_permission_storage);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_storage, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.READ_MEDIA_IMAGES, R.string.common_permission_image_and_video);
        PERMISSION_NAME_MAP.put(Permission.READ_MEDIA_VIDEO, R.string.common_permission_image_and_video);
        PERMISSION_NAME_MAP.put(Permission.READ_MEDIA_VISUAL_USER_SELECTED, R.string.common_permission_image_and_video);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_image_and_video, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.READ_MEDIA_AUDIO, R.string.common_permission_music_and_audio);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_music_and_audio, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.CAMERA, R.string.common_permission_camera);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_camera, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.RECORD_AUDIO, R.string.common_permission_microphone);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_microphone, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.ACCESS_FINE_LOCATION, R.string.common_permission_location);
        PERMISSION_NAME_MAP.put(Permission.ACCESS_COARSE_LOCATION, R.string.common_permission_location);

        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  {
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_SCAN, R.string.common_permission_nearby_devices);
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_CONNECT, R.string.common_permission_nearby_devices);
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_ADVERTISE, R.string.common_permission_nearby_devices);
            // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
                PERMISSION_NAME_MAP.put(Permission.NEARBY_WIFI_DEVICES, R.string.common_permission_nearby_devices);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_description_demo);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
            } else {
                PERMISSION_NAME_MAP.put(Permission.NEARBY_WIFI_DEVICES, R.string.common_permission_location);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_description_demo);
                PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
            }
        } else {
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_SCAN, R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_CONNECT, R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(Permission.BLUETOOTH_ADVERTISE, R.string.common_permission_location);
            PERMISSION_NAME_MAP.put(Permission.NEARBY_WIFI_DEVICES, R.string.common_permission_location);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_description_demo);
        }

        PERMISSION_NAME_MAP.put(Permission.ACCESS_BACKGROUND_LOCATION, R.string.common_permission_location_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location_background, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.BODY_SENSORS, R.string.common_permission_body_sensors);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_body_sensors, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.BODY_SENSORS_BACKGROUND, R.string.common_permission_body_sensors_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_body_sensors_background, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.READ_PHONE_STATE, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.CALL_PHONE, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.ADD_VOICEMAIL, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.USE_SIP, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.READ_PHONE_NUMBERS, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.ANSWER_PHONE_CALLS, R.string.common_permission_phone);
        PERMISSION_NAME_MAP.put(Permission.ACCEPT_HANDOVER, R.string.common_permission_phone);
        // 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)  {
            PERMISSION_NAME_MAP.put(Permission.READ_CALL_LOG, R.string.common_permission_call_logs);
            PERMISSION_NAME_MAP.put(Permission.WRITE_CALL_LOG, R.string.common_permission_call_logs);
            PERMISSION_NAME_MAP.put(Permission.PROCESS_OUTGOING_CALLS, R.string.common_permission_call_logs);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_call_logs, R.string.common_permission_description_demo);
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_description_demo);
        } else {
            PERMISSION_NAME_MAP.put(Permission.READ_CALL_LOG, R.string.common_permission_phone);
            PERMISSION_NAME_MAP.put(Permission.WRITE_CALL_LOG, R.string.common_permission_phone);
            PERMISSION_NAME_MAP.put(Permission.PROCESS_OUTGOING_CALLS, R.string.common_permission_phone);
            // 需要注意：这里的电话权限需要补充一下前面三个通话记录权限的用途
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_description_demo);
        }

        PERMISSION_NAME_MAP.put(Permission.GET_ACCOUNTS, R.string.common_permission_contacts);
        PERMISSION_NAME_MAP.put(Permission.READ_CONTACTS, R.string.common_permission_contacts);
        PERMISSION_NAME_MAP.put(Permission.WRITE_CONTACTS, R.string.common_permission_contacts);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_contacts, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.READ_CALENDAR, R.string.common_permission_calendar);
        PERMISSION_NAME_MAP.put(Permission.WRITE_CALENDAR, R.string.common_permission_calendar);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_calendar, R.string.common_permission_description_demo);

        // 注意：在 Android 10 的版本，这个权限的名称为《健身运动权限》，但是到了 Android 11 的时候，这个权限的名称被修改成了《身体活动权限》
        // 没错就改了一下权限的叫法，其他的一切没有变，Google 产品经理真的是闲的蛋疼，但是吐槽归吐槽，框架也要灵活应对一下，避免小白用户跳转到设置页找不到对应的选项
        int activityRecognitionPermissionNameStringId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? R.string.common_permission_activity_recognition_api30 : R.string.common_permission_activity_recognition_api29;
        PERMISSION_NAME_MAP.put(Permission.ACTIVITY_RECOGNITION, activityRecognitionPermissionNameStringId);
        PERMISSION_DESCRIPTION_MAP.put(activityRecognitionPermissionNameStringId, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.ACCESS_MEDIA_LOCATION, R.string.common_permission_access_media_location);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_access_media_location, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.SEND_SMS, R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(Permission.RECEIVE_SMS, R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(Permission.READ_SMS, R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(Permission.RECEIVE_WAP_PUSH, R.string.common_permission_sms);
        PERMISSION_NAME_MAP.put(Permission.RECEIVE_MMS, R.string.common_permission_sms);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_sms, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.GET_INSTALLED_APPS, R.string.common_permission_get_installed_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_get_installed_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.MANAGE_EXTERNAL_STORAGE, R.string.common_permission_all_file_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_all_file_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.REQUEST_INSTALL_PACKAGES, R.string.common_permission_install_unknown_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_install_unknown_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.SYSTEM_ALERT_WINDOW, R.string.common_permission_display_over_other_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_display_over_other_apps, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.WRITE_SETTINGS, R.string.common_permission_modify_system_settings);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_modify_system_settings, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.NOTIFICATION_SERVICE, R.string.common_permission_allow_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.POST_NOTIFICATIONS, R.string.common_permission_post_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_post_notifications, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.BIND_NOTIFICATION_LISTENER_SERVICE, R.string.common_permission_allow_notifications_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.PACKAGE_USAGE_STATS, R.string.common_permission_apps_with_usage_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_apps_with_usage_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.SCHEDULE_EXACT_ALARM, R.string.common_permission_alarms_reminders);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_alarms_reminders, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.ACCESS_NOTIFICATION_POLICY, R.string.common_permission_do_not_disturb_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_do_not_disturb_access, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, R.string.common_permission_ignore_battery_optimize);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_ignore_battery_optimize, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.BIND_VPN_SERVICE, R.string.common_permission_vpn);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_vpn, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.PICTURE_IN_PICTURE, R.string.common_permission_picture_in_picture);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_picture_in_picture, R.string.common_permission_description_demo);

        PERMISSION_NAME_MAP.put(Permission.USE_FULL_SCREEN_INTENT, R.string.common_permission_full_screen_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_full_screen_notifications, R.string.common_permission_description_demo);
    }

    /**
     * 通过权限获得名称
     */
    @NonNull
    public static String getNamesByPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        List<String> permissionNameList = getNameListByPermissions(context, permissions, true);

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
    public static List<String> getNameListByPermissions(@NonNull Context context, @NonNull List<String> permissions, boolean filterHighVersionPermissions) {
        List<String> permissionNameList = new ArrayList<>();
        for (String permission : permissions) {
            // 如果当前设置了过滤高版本权限，并且这个权限是高版本系统才出现的权限，则不继续往下执行
            // 避免出现在低版本上面执行拒绝权限后，连带高版本的名称也一起显示出来，但是在低版本上面是没有这个权限的
            if (filterHighVersionPermissions && XXPermissions.isHighVersionPermission(permission)) {
                continue;
            }
            String permissionName = getNameByPermission(context, permission);
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (permissionNameList.contains(permissionName)) {
                continue;
            }
            permissionNameList.add(permissionName);
        }
        return permissionNameList;
    }

    public static String getNameByPermission(@NonNull Context context, @NonNull String permission) {
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        return context.getString(permissionNameStringId);
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionsByPermissions(@NonNull Context context, @NonNull List<String> permissions) {
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
    public static List<String> getDescriptionListByPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        List<String> descriptionList = new ArrayList<>();
        for (String permission : permissions) {
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
    public static String getDescriptionByPermission(@NonNull Context context, @NonNull String permission) {
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        String permissionName = context.getString(permissionNameStringId);
        Integer permissionDescriptionStringId = PERMISSION_DESCRIPTION_MAP.get(permissionNameStringId);
        String permissionDescription;
        if (permissionDescriptionStringId == null || permissionDescriptionStringId == 0) {
            permissionDescription = "";
        } else {
            permissionDescription = context.getString(permissionDescriptionStringId);
        }
        return permissionName + context.getString(R.string.common_permission_colon) + permissionDescription;
    }
}