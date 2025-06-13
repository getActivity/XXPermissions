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
 *    desc   : 读取图片媒体权限类
 */
public final class ReadMediaImagesPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.READ_MEDIA_IMAGES;

    public static final Parcelable.Creator<ReadMediaImagesPermission> CREATOR = new Parcelable.Creator<ReadMediaImagesPermission>() {

        @Override
        public ReadMediaImagesPermission createFromParcel(Parcel source) {
            return new ReadMediaImagesPermission(source);
        }

        @Override
        public ReadMediaImagesPermission[] newArray(int size) {
            return new ReadMediaImagesPermission[size];
        }
    };

    public ReadMediaImagesPermission() {
        // default implementation ignored
    }

    private ReadMediaImagesPermission(Parcel in) {
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
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAndroid14() && !skipRequest) {
            // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
            // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
            // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
            // 为了权限回调不出现失败，这里只能返回 true，这样告诉外层请求其实是成功的
            return PermissionManifest.getReadMediaVisualUserSelectedPermission().isGranted(context, false);
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
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