package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 媒体位置权限类
 */
public final class MediaLocationPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.ACCESS_MEDIA_LOCATION;

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
        return PERMISSION_NAME;
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
        return PermissionManifest.getReadExternalStoragePermission().isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        return isGrantedReadMediaPermission(activity, true) &&
                super.isDoNotAskAgainByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getReadExternalStoragePermission().isGranted(activity) &&
                super.isDoNotAskAgainByLowVersion(activity);
    }

    /**
     * 判断是否授予了读取媒体的权限
     */
    private boolean isGrantedReadMediaPermission(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_13)) {
            // 这里为什么加上 Android 14 和 READ_MEDIA_VISUAL_USER_SELECTED 权限判断？这是因为如果获取部分照片和视频
            // 然后申请 Permission.ACCESS_MEDIA_LOCATION 系统会返回失败，必须要选择获取全部照片和视频才可以申请该权限
            return PermissionManifest.getReadMediaImagesPermission().isGranted(context, skipRequest) ||
                PermissionManifest.getReadMediaVideoPermission().isGranted(context, skipRequest) ||
                PermissionManifest.getManageExternalStoragePermission().isGranted(context, skipRequest);
        }
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11)) {
            return PermissionManifest.getReadExternalStoragePermission().isGranted(context, skipRequest) ||
                PermissionManifest.getManageExternalStoragePermission().isGranted(context, skipRequest);
        }
        return PermissionManifest.getReadExternalStoragePermission().isGranted(context, skipRequest);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);
        // 判断当前项目是否适配了 Android 13
        if (AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_13) {
            // 判断请求的权限中是否包含了某些特定权限
            if (PermissionUtils.containsPermission(requestPermissions, PermissionConstants.READ_MEDIA_IMAGES) ||
                PermissionUtils.containsPermission(requestPermissions, PermissionConstants.READ_MEDIA_VIDEO) ||
                PermissionUtils.containsPermission(requestPermissions, PermissionConstants.MANAGE_EXTERNAL_STORAGE)) {
                // 如果请求的权限中，包含了上面这些权限，就不往下执行
                return;
            }

            // 如果不包含，你需要在外层手动添加 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO、MANAGE_EXTERNAL_STORAGE 任一权限才可以申请 ACCESS_MEDIA_LOCATION 权限
            throw new IllegalArgumentException("You must add " + PermissionConstants.READ_MEDIA_IMAGES + " or " + PermissionConstants.READ_MEDIA_VIDEO + " or " +
                PermissionConstants.MANAGE_EXTERNAL_STORAGE + " rights to apply for " + getName() + " rights");
        }

        // 如果当前项目还没有适配 Android 13，就判断请求的权限中是否包含了某些特定权限
        if (PermissionUtils.containsPermission(requestPermissions, PermissionConstants.READ_EXTERNAL_STORAGE) ||
            PermissionUtils.containsPermission(requestPermissions, PermissionConstants.MANAGE_EXTERNAL_STORAGE)) {
            // 如果请求的权限中，包含了上面这些权限，就不往下执行
            return;
        }

        // 如果不包含，你需要在外层手动添加 READ_EXTERNAL_STORAGE 或者 MANAGE_EXTERNAL_STORAGE 才可以申请 ACCESS_MEDIA_LOCATION 权限
        throw new IllegalArgumentException("You must add " + PermissionConstants.READ_EXTERNAL_STORAGE + " or " +
            PermissionConstants.MANAGE_EXTERNAL_STORAGE + " rights to apply for " + getName() + " rights");
    }
}