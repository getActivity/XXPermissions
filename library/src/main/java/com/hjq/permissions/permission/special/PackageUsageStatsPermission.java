package com.hjq.permissions.permission.special;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 查看应用使用情况权限类
 */
public final class PackageUsageStatsPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.PACKAGE_USAGE_STATS;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_5;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid5()) {
            return true;
        }
        return checkOpPermission(context, AppOpsManager.OPSTR_GET_USAGE_STATS, false);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid10()) {
            intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            // 经过测试，只有在 Android 10 及以上加包名才有效果
            // 如果在 Android 10 以下加包名会导致无法跳转
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid5()) {
            intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intentList.add(intent);
        }

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