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
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
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

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.SCHEDULE_EXACT_ALARM;

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
    public String getName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_12;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid12()) {
            return true;
        }
        return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid12()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionInfo> permissionInfoList,
                                            @Nullable PermissionInfo currentPermissionInfo) {
        // 不使用父类的方式来检查清单权限有没有注册，但是不代表不检查，这个权限比较复杂，需要自定义检查
        // super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        String useExactAlarmPermissionName;
        if (AndroidVersionTools.isAndroid13()) {
            useExactAlarmPermissionName = permission.USE_EXACT_ALARM;
        } else {
            useExactAlarmPermissionName = "android.permission.USE_EXACT_ALARM";
        }

        if (AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_13 &&
            findPermissionInfoByList(permissionInfoList, useExactAlarmPermissionName) != null) {
            // 如果当前项目适配了 Android 13 的话，并且在清单文件中注册了 USE_EXACT_ALARM 权限，那么 SCHEDULE_EXACT_ALARM 权限在清单文件中可以这样注册
            // <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" android:maxSdkVersion="32" />
            // 相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#USE_EXACT_ALARM
            // 如果你的应用要上架 GooglePlay，那么需要慎重添加 USE_EXACT_ALARM 权限，因为不是日历、闹钟、时钟这类应用添加 USE_EXACT_ALARM 权限很难通过 GooglePlay 上架审核
            checkPermissionRegistrationStatus(permissionInfoList, getName(), AndroidVersionTools.ANDROID_12_L);
            return;
        }

        checkPermissionRegistrationStatus(permissionInfoList, getName());
    }
}