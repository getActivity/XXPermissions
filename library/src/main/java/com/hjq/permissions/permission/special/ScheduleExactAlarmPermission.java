package com.hjq.permissions.permission.special;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.tools.AndroidVersionTools;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 闹钟权限类
 */
public final class ScheduleExactAlarmPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.SCHEDULE_EXACT_ALARM;

    public static final Parcelable.Creator<ScheduleExactAlarmPermission> CREATOR = new Parcelable.Creator<ScheduleExactAlarmPermission>() {

        @Override
        public ScheduleExactAlarmPermission createFromParcel(Parcel source) {
            return new ScheduleExactAlarmPermission(source);
        }

        @Override
        public ScheduleExactAlarmPermission[] newArray(int size) {
            return new ScheduleExactAlarmPermission[size];
        }
    };

    public ScheduleExactAlarmPermission() {
        // default implementation ignored
    }

    private ScheduleExactAlarmPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_12;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid12()) {
            return true;
        }
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (alarmManager == null) {
            return false;
        }
        return alarmManager.canScheduleExactAlarms();
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid12()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        // 如果是因为加包名的数据后导致不能跳转，就把包名的数据移除掉
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent.setData(null);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 不使用父类的方式来检查清单权限有没有注册，但是不代表不检查，这个权限比较复杂，需要自定义检查
        return false;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList,
            currentPermissionManifestInfo);
        String useExactAlarmPermissionName;
        if (AndroidVersionTools.isAndroid13()) {
            useExactAlarmPermissionName = permission.USE_EXACT_ALARM;
        } else {
            useExactAlarmPermissionName = "android.permission.USE_EXACT_ALARM";
        }

        if (AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_13 &&
            findPermissionInfoByList(permissionManifestInfoList, useExactAlarmPermissionName) != null) {
            // 如果当前项目适配了 Android 13 的话，并且在清单文件中注册了 USE_EXACT_ALARM 权限，那么 SCHEDULE_EXACT_ALARM 权限在清单文件中可以这样注册
            // <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" android:maxSdkVersion="32" />
            // 相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#USE_EXACT_ALARM
            // 如果你的应用要上架 GooglePlay，那么需要慎重添加 USE_EXACT_ALARM 权限，因为不是日历、闹钟、时钟这类应用添加 USE_EXACT_ALARM 权限很难通过 GooglePlay 上架审核
            checkPermissionRegistrationStatus(permissionManifestInfoList, getPermissionName(), AndroidVersionTools.ANDROID_12_L);
            return;
        }

        checkPermissionRegistrationStatus(permissionManifestInfoList, getPermissionName());
    }
}