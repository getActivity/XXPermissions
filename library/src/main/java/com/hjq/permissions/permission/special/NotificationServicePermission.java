package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 通知权限类
 */
public final class NotificationServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.NOTIFICATION_SERVICE;

    private static final String OP_POST_NOTIFICATION_FIELD_NAME = "OP_POST_NOTIFICATION";
    private static final int OP_POST_NOTIFICATION_DEFAULT_VALUE = 11;

    public static final Parcelable.Creator<NotificationServicePermission> CREATOR = new Parcelable.Creator<NotificationServicePermission>() {

        @Override
        public NotificationServicePermission createFromParcel(Parcel source) {
            return new NotificationServicePermission(source);
        }

        @Override
        public NotificationServicePermission[] newArray(int size) {
            return new NotificationServicePermission[size];
        }
    };

    @Nullable
    private final String mChannelId;

    public NotificationServicePermission() {
        this((String) null);
    }

    public NotificationServicePermission(@Nullable String channelId) {
        mChannelId = channelId;
    }

    private NotificationServicePermission(Parcel in) {
        this(in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mChannelId);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_4_4;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid4_4()) {
            return true;
        }
        if (!PermissionVersion.isAndroid7()) {
            return checkOpPermission(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE, true);
        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (notificationManager == null) {
            return checkOpPermission(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE, true);
        }
        if (!notificationManager.areNotificationsEnabled()) {
            return false;
        }
        if (TextUtils.isEmpty(mChannelId) || !PermissionVersion.isAndroid8()) {
            return true;
        }
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(mChannelId);
        return notificationChannel != null && notificationChannel.getImportance() != NotificationManager.IMPORTANCE_NONE;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(8);
        Intent intent;

        if (PermissionVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            // 添加应用的包名参数
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = null;
            // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
            if (notificationManager != null && !TextUtils.isEmpty(mChannelId)) {
                notificationChannel = notificationManager.getNotificationChannel(mChannelId);
            }
            // 设置通知渠道 id 参数的前提条件有两个
            // 1. 这个通知渠道还存在
            // 2. 当前授予了通知权限
            if (notificationChannel != null && notificationManager.areNotificationsEnabled()) {
                // 将 Action 修改成具体通知渠道的页面
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                // 指定通知渠道 id
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannel.getId());
                if (PermissionVersion.isAndroid11()) {
                    // 高版本会优先从会话 id 中找到对应的通知渠道，找不到再从渠道 id 上面找到对应的通知渠道
                    intent.putExtra(Settings.EXTRA_CONVERSATION_ID, notificationChannel.getConversationId());
                }
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid5()) {
            intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid13()) {
            intent = new Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS);
            intentList.add(intent);
        }

        intent = getApplicationDetailsSettingIntent(context);
        intentList.add(intent);

        intent = getManageApplicationSettingIntent();
        intentList.add(intent);

        intent = getApplicationSettingIntent();
        intentList.add(intent);

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Nullable
    public String getChannelId() {
        return mChannelId;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            // 如果当前项目已经适配了 Android 13，则需要在清单文件加入 POST_NOTIFICATIONS 权限，否则会导致无法申请通知栏权限
            PermissionManifestInfo postNotificationsPermission = findPermissionInfoByList(permissionInfoList, PermissionNames.POST_NOTIFICATIONS);
            checkPermissionRegistrationStatus(postNotificationsPermission, PermissionNames.POST_NOTIFICATIONS, Integer.MAX_VALUE);
        }
    }
}