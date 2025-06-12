package com.hjq.permissions.permission.special;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 闹钟权限类
 */
public final class ScheduleExactAlarmPermission extends SpecialPermission {

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
        return PermissionConstants.SCHEDULE_EXACT_ALARM;
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
}