package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 全屏通知权限类
 */
public final class UseFullScreenIntentPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.USE_FULL_SCREEN_INTENT;

    public static final Parcelable.Creator<UseFullScreenIntentPermission> CREATOR = new Parcelable.Creator<UseFullScreenIntentPermission>() {

        @Override
        public UseFullScreenIntentPermission createFromParcel(Parcel source) {
            return new UseFullScreenIntentPermission(source);
        }

        @Override
        public UseFullScreenIntentPermission[] newArray(int size) {
            return new UseFullScreenIntentPermission[size];
        }
    };

    public UseFullScreenIntentPermission() {
        // default implementation ignored
    }

    private UseFullScreenIntentPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_14;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid14()) {
            return true;
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (notificationManager == null) {
            return false;
        }
        return notificationManager.canUseFullScreenIntent();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        if (PermissionVersion.isAndroid14()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        // 经过测试，MIUI 和 HyperOS 不支持在通知界面设置全屏通知权限的，但是 Android 原生是可以的
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui()) {
            intent = getAndroidSettingIntent();
            intentList.add(intent);
            return intentList;
        }

        if (PermissionVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
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

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 表示当前权限需要在 AndroidManifest.xml 文件中进行静态注册
        return true;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // 全屏通知权限需要通知权限一起使用（NOTIFICATION_SERVICE 或者 POST_NOTIFICATIONS）
        if (!PermissionUtils.containsPermission(requestList, PermissionNames.NOTIFICATION_SERVICE) &&
            !PermissionUtils.containsPermission(requestList, PermissionNames.POST_NOTIFICATIONS)) {
            throw new IllegalArgumentException("The \"" + getPermissionName() + "\" needs to be used together with the notification permission. "
                + "(\"" + PermissionNames.NOTIFICATION_SERVICE + "\" or \"" + PermissionNames.POST_NOTIFICATIONS + "\")");
        }

        int thisPermissionIndex = -1;
        int notificationServicePermissionIndex = -1;
        int postNotificationsPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, getPermissionName())) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.NOTIFICATION_SERVICE)) {
                notificationServicePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.POST_NOTIFICATIONS)) {
                postNotificationsPermissionIndex = i;
            }
        }

        if (notificationServicePermissionIndex != -1 && notificationServicePermissionIndex > thisPermissionIndex) {
            // 请把 USE_FULL_SCREEN_INTENT 权限放置在 NOTIFICATION_SERVICE 权限的后面
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.NOTIFICATION_SERVICE + "\" permission");
        }

        if (postNotificationsPermissionIndex != -1 && postNotificationsPermissionIndex > thisPermissionIndex) {
            // 请把 USE_FULL_SCREEN_INTENT 权限放置在 POST_NOTIFICATIONS 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.POST_NOTIFICATIONS + "\" permission");
        }
    }
}