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
 *    desc   : 读取视频媒体权限类
 */
public final class ReadMediaVideoPermission extends DangerousPermission {

    public static final Parcelable.Creator<ReadMediaVideoPermission> CREATOR = new Parcelable.Creator<ReadMediaVideoPermission>() {

        @Override
        public ReadMediaVideoPermission createFromParcel(Parcel source) {
            return new ReadMediaVideoPermission(source);
        }

        @Override
        public ReadMediaVideoPermission[] newArray(int size) {
            return new ReadMediaVideoPermission[size];
        }
    };

    public ReadMediaVideoPermission() {
        // default implementation ignored
    }

    private ReadMediaVideoPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.READ_MEDIA_VIDEO;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @Override
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAndroid14() && !skipRequest) {
            // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
            // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
            // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
            // 为了权限回调不出现失败，这里只能返回 true，这样告诉外层请求其实是成功的
            return PermissionManifest.READ_MEDIA_VISUAL_USER_SELECTED.isGranted(context, false);
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
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