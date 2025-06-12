package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 所有文件访问权限
 */
public final class ManageExternalStoragePermission extends SpecialPermission {

    public static final Parcelable.Creator<ManageExternalStoragePermission> CREATOR = new Parcelable.Creator<ManageExternalStoragePermission>() {

        @Override
        public ManageExternalStoragePermission createFromParcel(Parcel source) {
            return new ManageExternalStoragePermission(source);
        }

        @Override
        public ManageExternalStoragePermission[] newArray(int size) {
            return new ManageExternalStoragePermission[size];
        }
    };

    public ManageExternalStoragePermission() {
        // default implementation ignored
    }

    private ManageExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.MANAGE_EXTERNAL_STORAGE;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_11;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid11()) {
            // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
            // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
            // Environment.isExternalStorageLegacy API 解释：是否采用的是非分区存储的模式
            if (AndroidVersionTools.isAndroid10() && !Environment.isExternalStorageLegacy()) {
                return false;
            }
            return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(context, skipRequest) &&
                PermissionManifest.WRITE_EXTERNAL_STORAGE.isGranted(context, skipRequest);
        }
        // 是否有所有文件的管理权限
        return Environment.isExternalStorageManager();
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid11()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}