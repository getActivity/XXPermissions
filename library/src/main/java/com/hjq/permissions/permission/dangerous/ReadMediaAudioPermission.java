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

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.READ_MEDIA_AUDIO;

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
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getReadExternalStoragePermission().isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getReadExternalStoragePermission().isDoNotAskAgain(activity);
    }
}