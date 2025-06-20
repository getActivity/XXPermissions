package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.tools.AndroidVersionTools;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 读取音频媒体权限类
 */
public final class ReadMediaAudioPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_AUDIO;

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
        // Android 13 以下访问媒体文件需要用到读取外部存储的权限
        return PermissionUtils.asArrayList(PermissionManifest.getReadExternalStoragePermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getReadExternalStoragePermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList,
            currentPermissionManifestInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion() > getMinSdkVersion(activity, androidManifestInfo)) {
            checkPermissionRegistrationStatus(permissionManifestInfoList, PermissionNames.READ_EXTERNAL_STORAGE, AndroidVersionTools.ANDROID_12_L);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);
        // 检测是否有添加读取外部存储权限，有的话直接抛出异常，请不要自己手动添加这个权限，框架会在 Android 13 以下的版本上自动添加并申请这个权限
        if (PermissionUtils.containsPermission(requestPermissions, PermissionNames.READ_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("You have added the \"" + getPermissionName() + "\" permission, "
                + "please do not add the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission, "
                + "this conflicts with the framework's automatic compatibility policy.");
        }
    }
}