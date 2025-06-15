package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 写入系统设置权限类
 */
public final class WriteSettingsPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.WRITE_SETTINGS;

    public static final Parcelable.Creator<WriteSettingsPermission> CREATOR = new Parcelable.Creator<WriteSettingsPermission>() {

        @Override
        public WriteSettingsPermission createFromParcel(Parcel source) {
            return new WriteSettingsPermission(source);
        }

        @Override
        public WriteSettingsPermission[] newArray(int size) {
            return new WriteSettingsPermission[size];
        }
    };

    public WriteSettingsPermission() {
        // default implementation ignored
    }

    private WriteSettingsPermission(Parcel in) {
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
        return Settings.System.canWrite(context);
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid6()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}