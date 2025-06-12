package com.hjq.permissions.permission.special;

import android.app.AppOpsManager;
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
 *    desc   : 查看应用使用情况权限类
 */
public final class PackageUsageStatsPermission extends SpecialPermission {

    public static final Parcelable.Creator<PackageUsageStatsPermission> CREATOR = new Parcelable.Creator<PackageUsageStatsPermission>() {

        @Override
        public PackageUsageStatsPermission createFromParcel(Parcel source) {
            return new PackageUsageStatsPermission(source);
        }

        @Override
        public PackageUsageStatsPermission[] newArray(int size) {
            return new PackageUsageStatsPermission[size];
        }
    };

    public PackageUsageStatsPermission() {
        // default implementation ignored
    }

    private PackageUsageStatsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.PACKAGE_USAGE_STATS;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_5;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid5()) {
            return true;
        }
        return checkOpNoThrow(context, AppOpsManager.OPSTR_GET_USAGE_STATS);
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid5()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        if (AndroidVersionTools.isAndroid10()) {
            // 经过测试，只有在 Android 10 及以上加包名才有效果
            // 如果在 Android 10 以下加包名会导致无法跳转
            intent.setData(PermissionUtils.getPackageNameUri(context));
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}