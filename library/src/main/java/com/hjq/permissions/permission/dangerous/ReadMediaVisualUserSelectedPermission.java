package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/13
 *    desc   : 访问部分照片和视频的权限类
 */
public final class ReadMediaVisualUserSelectedPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED;

    public static final Parcelable.Creator<ReadMediaVisualUserSelectedPermission> CREATOR = new Parcelable.Creator<ReadMediaVisualUserSelectedPermission>() {

        @Override
        public ReadMediaVisualUserSelectedPermission createFromParcel(Parcel source) {
            return new ReadMediaVisualUserSelectedPermission(source);
        }

        @Override
        public ReadMediaVisualUserSelectedPermission[] newArray(int size) {
            return new ReadMediaVisualUserSelectedPermission[size];
        }
    };

    public ReadMediaVisualUserSelectedPermission() {
        // default implementation ignored
    }

    private ReadMediaVisualUserSelectedPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.IMAGE_AND_VIDEO_MEDIA;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_14;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // 授予对照片和视频的部分访问权限：https://developer.android.google.cn/about/versions/14/changes/partial-photo-video-access?hl=zh-cn
        // READ_MEDIA_VISUAL_USER_SELECTED 这个权限比较特殊，不需要调高 targetSdk 的版本才能申请，但是需要和 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 组合使用
        // 这个权限不能单独申请，只能和 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO 一起申请，否则会有问题，所以这个权限的 targetSdk 最低要求为 33 及以上
        return PermissionVersion.ANDROID_13;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO)) {
            return;
        }
        // 不能单独请求 READ_MEDIA_VISUAL_USER_SELECTED 权限，需要加上 READ_MEDIA_IMAGES 或者 READ_MEDIA_VIDEO 任一权限，又或者两个都有，否则权限申请会被系统直接拒绝
        throw new IllegalArgumentException("You cannot request the \"" + getPermissionName() + "\" permission alone. " +
                                            "must add either \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                                            PermissionNames.READ_MEDIA_VIDEO + "\" permission, or maybe both");
    }
}