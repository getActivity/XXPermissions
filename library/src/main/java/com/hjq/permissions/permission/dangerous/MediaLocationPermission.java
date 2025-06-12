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
 *    desc   : 媒体位置权限类
 */
public final class MediaLocationPermission extends DangerousPermission {

    public static final Parcelable.Creator<MediaLocationPermission> CREATOR = new Parcelable.Creator<MediaLocationPermission>() {

        @Override
        public MediaLocationPermission createFromParcel(Parcel source) {
            return new MediaLocationPermission(source);
        }

        @Override
        public MediaLocationPermission[] newArray(int size) {
            return new MediaLocationPermission[size];
        }
    };

    public MediaLocationPermission() {
        // default implementation ignored
    }

    private MediaLocationPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.ACCESS_MEDIA_LOCATION;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_10;
    }

    @Override
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return isGrantedReadMediaPermission(context, skipRequest) &&
                super.isGrantedByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        return isGrantedReadMediaPermission(activity, true) &&
                super.isDoNotAskAgainByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(activity) &&
                super.isDoNotAskAgainByLowVersion(activity);
    }

    /**
     * 判断是否授予了读取媒体的权限
     */
    private boolean isGrantedReadMediaPermission(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_13)) {
            // 这里为什么加上 Android 14 和 READ_MEDIA_VISUAL_USER_SELECTED 权限判断？这是因为如果获取部分照片和视频
            // 然后申请 Permission.ACCESS_MEDIA_LOCATION 系统会返回失败，必须要选择获取全部照片和视频才可以申请该权限
            return PermissionManifest.READ_MEDIA_IMAGES.isGranted(context, skipRequest) ||
                PermissionManifest.READ_MEDIA_VIDEO.isGranted(context, skipRequest) ||
                PermissionManifest.MANAGE_EXTERNAL_STORAGE.isGranted(context, skipRequest);
        }
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11)) {
            return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(context, skipRequest) ||
                PermissionManifest.MANAGE_EXTERNAL_STORAGE.isGranted(context, skipRequest);
        }
        return PermissionManifest.READ_EXTERNAL_STORAGE.isGranted(context, skipRequest);
    }
}