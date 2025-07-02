package com.hjq.permissions.permission.special;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PhoneRomUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 勿扰权限类
 */
public final class AccessNotificationPolicyPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_NOTIFICATION_POLICY;

    public static final Creator<AccessNotificationPolicyPermission> CREATOR = new Creator<AccessNotificationPolicyPermission>() {

        @Override
        public AccessNotificationPolicyPermission createFromParcel(Parcel source) {
            return new AccessNotificationPolicyPermission(source);
        }

        @Override
        public AccessNotificationPolicyPermission[] newArray(int size) {
            return new AccessNotificationPolicyPermission[size];
        }
    };

    public AccessNotificationPolicyPermission() {
        // default implementation ignored
    }

    private AccessNotificationPolicyPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersion.ANDROID_6;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersion.isAndroid6()) {
            return true;
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (notificationManager == null) {
            return false;
        }
        return notificationManager.isNotificationPolicyAccessGranted();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        // issue 地址：https://github.com/getActivity/XXPermissions/issues/190
        // 这里解释一下，为什么要排除 HarmonyOs 和 Magic，因为用代码能检测到有这个 Intent，也能跳转过去，但是会被马上拒绝
        // 测试过了其他厂商系统及 Android 原生系统都没有这个问题，就只有鸿蒙有这个问题
        // 只因为这个 Intent 是隐藏的意图，所以就不让用，鸿蒙 2.0 和 3.0 都有这个问题
        // 别问鸿蒙 1.0 有没有问题，问就是鸿蒙一发布就 2.0 了，1.0 版本都没有问世过
        // ------------------------ 我是一条华丽的分割线 ----------------------------
        // 相关的 issue 地址：
        // 1. https://github.com/getActivity/XXPermissions/issues/190
        // 2. https://github.com/getActivity/XXPermissions/issues/233
        // 经过测试，荣耀下面这些机子都会出现加包名跳转不过去的问题
        // 荣耀 magic4 Android 13  MagicOs 7.0
        // 荣耀 80 Pro Android 12  MagicOs 7.0
        // 荣耀 X20 SE Android 11  MagicOs 4.1
        // 荣耀 Play5 Android 10  MagicOs 4.0
        if (AndroidVersion.isAndroid10() && !PhoneRomUtils.isHarmonyOs() && !PhoneRomUtils.isMagicOs()) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        if (AndroidVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
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
}