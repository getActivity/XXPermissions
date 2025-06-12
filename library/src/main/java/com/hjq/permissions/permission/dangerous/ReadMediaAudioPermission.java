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
 *    desc   : 读取音频媒体权限类
 */
public final class ReadMediaAudioPermission extends DangerousPermission {

    public static final Parcelable.Creator<ReadMediaAudioPermission> CREATOR = new Parcelable.Creator<ReadMediaAudioPermission>() {

        @Override
        public ReadMediaAudioPermission createFromParcel(Parcel source) {
            return new ReadMediaAudioPermission(source);
        }

        @Override
        public ReadMediaAudioPermission[] newArray(int size) {
            return new ReadMediaAudioPermission[size];
        }
    };

    public ReadMediaAudioPermission() {
        // default implementation ignored
    }

    private ReadMediaAudioPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.READ_MEDIA_AUDIO;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.READ_EXTERNAL_STORAGE.isDoNotAskAgain(activity);
    }
}