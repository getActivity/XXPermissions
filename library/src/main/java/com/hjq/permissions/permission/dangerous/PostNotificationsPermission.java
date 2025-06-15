package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 发送通知权限类
 */
public final class PostNotificationsPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.POST_NOTIFICATIONS;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 13 以下开启通知栏服务，需要用到旧的通知栏权限（框架自己虚拟出来的）
        return PermissionUtils.asArrayList(PermissionManifest.getNotificationServicePermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getNotificationServicePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        if (isLowVersionRunning()) {
            return false;
        }
        return super.isDoNotAskAgainPermission(activity);
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS 要跳转到权限设置页和 NOTIFICATION_SERVICE 权限是一样的
        return PermissionManifest.getNotificationServicePermission().getPermissionSettingIntent(context);
    }
}