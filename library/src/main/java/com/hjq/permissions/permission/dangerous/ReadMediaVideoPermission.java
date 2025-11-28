package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 读取视频媒体权限类
 */
public final class ReadMediaVideoPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_VIDEO;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.IMAGE_AND_VIDEO_MEDIA;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission());
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid14() && !skipRequest) {
            // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
            // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
            // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
            // 为了权限回调不出现失败，这里只能返回 true，这样告诉外层请求其实是成功的
            return PermissionLists.getReadMediaVisualUserSelectedPermission().isGrantedPermission(context, false);
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getReadExternalStoragePermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_12_L);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // 检测是否有添加读取外部存储权限，有的话直接抛出异常，请不要自己手动添加这个权限，框架会在 Android 13 以下的版本上自动添加并申请这个权限
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("You have added the \"" + getPermissionName() + "\" permission, "
                + "please do not add the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission, "
                + "this conflicts with the framework's automatic compatibility policy.");
        }
    }
}