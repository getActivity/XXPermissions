package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
 *    desc   : 发送通知权限类
 */
public final class PostNotificationsPermission extends DangerousPermission {

    public static final Parcelable.Creator<PostNotificationsPermission> CREATOR = new Parcelable.Creator<PostNotificationsPermission>() {

        @Override
        public PostNotificationsPermission createFromParcel(Parcel source) {
            return new PostNotificationsPermission(source);
        }

        @Override
        public PostNotificationsPermission[] newArray(int size) {
            return new PostNotificationsPermission[size];
        }
    };

    public PostNotificationsPermission() {
        // default implementation ignored
    }

    private PostNotificationsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.POST_NOTIFICATIONS;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.NOTIFICATION_SERVICE.isGranted(context, skipRequest);
    }

    @Override
    public boolean isDoNotAskAgain(@NonNull Activity activity) {
        if (isLowVersionRunning()) {
            return false;
        }
        return super.isDoNotAskAgain(activity);
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS 要跳转到权限设置页和 NOTIFICATION_SERVICE 权限是一样的
        return PermissionManifest.NOTIFICATION_SERVICE.getSettingIntent(context);
    }
}