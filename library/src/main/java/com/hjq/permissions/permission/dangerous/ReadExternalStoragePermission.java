package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.common.DangerousPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 读取外部存储权限类
 */
public final class ReadExternalStoragePermission extends DangerousPermission {

    public static final Parcelable.Creator<ReadExternalStoragePermission> CREATOR = new Parcelable.Creator<ReadExternalStoragePermission>() {

        @Override
        public ReadExternalStoragePermission createFromParcel(Parcel source) {
            return new ReadExternalStoragePermission(source);
        }

        @Override
        public ReadExternalStoragePermission[] newArray(int size) {
            return new ReadExternalStoragePermission[size];
        }
    };

    public ReadExternalStoragePermission() {
        // default implementation ignored
    }

    private ReadExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.READ_EXTERNAL_STORAGE;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_6;
    }

    @Override
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_13)) {
            return PermissionManifest.READ_MEDIA_IMAGES.isGranted(context, skipRequest) &&
                PermissionManifest.READ_MEDIA_VIDEO.isGranted(context, skipRequest) &&
                PermissionManifest.READ_MEDIA_AUDIO.isGranted(context, skipRequest);
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_13)) {
            return PermissionManifest.READ_MEDIA_IMAGES.isDoNotAskAgain(activity) &&
                PermissionManifest.READ_MEDIA_VIDEO.isDoNotAskAgain(activity) &&
                PermissionManifest.READ_MEDIA_AUDIO.isDoNotAskAgain(activity);
        }
        return super.isDoNotAskAgainByStandardVersion(activity);
    }
}