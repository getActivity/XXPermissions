package com.hjq.permissions.permission.special;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.PhoneRomUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 勿扰权限类
 */
public final class NotificationPolicyPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.ACCESS_NOTIFICATION_POLICY;

    public static final Parcelable.Creator<NotificationPolicyPermission> CREATOR = new Parcelable.Creator<NotificationPolicyPermission>() {

        @Override
        public NotificationPolicyPermission createFromParcel(Parcel source) {
            return new NotificationPolicyPermission(source);
        }

        @Override
        public NotificationPolicyPermission[] newArray(int size) {
            return new NotificationPolicyPermission[size];
        }
    };

    public NotificationPolicyPermission() {
        // default implementation ignored
    }

    private NotificationPolicyPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_6;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid6()) {
            return true;
        }
        return context.getSystemService(NotificationManager.class).isNotificationPolicyAccessGranted();
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid6()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent;
        if (AndroidVersionTools.isAndroid10()) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
            intent.setData(PermissionUtils.getPackageNameUri(context));

            // issue 地址：https://github.com/getActivity/XXPermissions/issues/190
            // 这里解释一下，为什么要排除鸿蒙系统，因为用代码能检测到有这个 Intent，也能跳转过去，但是会被马上拒绝
            // 测试过了其他厂商系统及 Android 原生系统都没有这个问题，就只有鸿蒙有这个问题
            // 只因为这个 Intent 是隐藏的意图，所以就不让用，鸿蒙 2.0 和 3.0 都有这个问题
            // 别问鸿蒙 1.0 有没有问题，问就是鸿蒙一发布就 2.0 了，1.0 版本都没有问世过
            // ------------------------ 我是一条华丽的分割线 ----------------------------
            // issue 地址：https://github.com/getActivity/XXPermissions/issues/233
            // 经过测试，荣耀下面这些机子都会出现加包名跳转不过去的问题
            // 荣耀 magic4 Android 13  MagicOs 7.0
            // 荣耀 80 Pro Android 12  MagicOs 7.0
            // 荣耀 X20 SE Android 11  MagicOs 4.1
            // 荣耀 Play5 Android 10  MagicOs 4.0
            if (PhoneRomUtils.isHarmonyOs() || PhoneRomUtils.isMagicOs()) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
        } else {
            intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}