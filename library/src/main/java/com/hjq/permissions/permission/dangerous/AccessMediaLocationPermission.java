package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 访问媒体的位置信息权限类
 */
public final class AccessMediaLocationPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_MEDIA_LOCATION;

    public static final Parcelable.Creator<AccessMediaLocationPermission> CREATOR = new Parcelable.Creator<AccessMediaLocationPermission>() {

        @Override
        public AccessMediaLocationPermission createFromParcel(Parcel source) {
            return new AccessMediaLocationPermission(source);
        }

        @Override
        public AccessMediaLocationPermission[] newArray(int size) {
            return new AccessMediaLocationPermission[size];
        }
    };

    public AccessMediaLocationPermission() {
        // default implementation ignored
    }

    private AccessMediaLocationPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_10;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return isGrantedReadMediaPermission(context, skipRequest) &&
                super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        return isGrantedReadMediaPermission(activity, true) &&
                super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getReadExternalStoragePermission().isDoNotAskAgainPermission(activity);
    }

    /**
     * 判断是否授予了读取媒体的权限
     */
    private boolean isGrantedReadMediaPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_13) {
            // 这里为什么不加上 Android 14 和 READ_MEDIA_VISUAL_USER_SELECTED 权限判断？这是因为如果获取部分照片和视频
            // 然后申请 Permission.ACCESS_MEDIA_LOCATION 系统会返回失败，必须要选择获取全部照片和视频才可以申请该权限
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) ||
                PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) ||
                PermissionLists.getManageExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_11) {
            return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest) ||
                PermissionLists.getManageExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);

        int thisPermissionIndex = -1;
        int readMediaImagesPermissionIndex = -1;
        int readMediaVideoPermissionIndex = -1;
        int readMediaVisualUserSelectedPermissionIndex = -1;
        int manageExternalStoragePermissionIndex = -1;
        int readExternalStoragePermissionIndex = -1;
        int writeExternalStoragePermissionIndex = -1;
        for (int i = 0; i < requestPermissions.size(); i++) {
            IPermission permission = requestPermissions.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_IMAGES)) {
                readMediaImagesPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_VIDEO)) {
                readMediaVideoPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED)) {
                readMediaVisualUserSelectedPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
                manageExternalStoragePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_EXTERNAL_STORAGE)) {
                readExternalStoragePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.WRITE_EXTERNAL_STORAGE)) {
                writeExternalStoragePermissionIndex = i;
            }
        }

        if (readMediaImagesPermissionIndex != -1 && readMediaImagesPermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 READ_MEDIA_IMAGES 权限的后面
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_IMAGES + "\" permission");
        }

        if (readMediaVideoPermissionIndex != -1 && readMediaVideoPermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 READ_MEDIA_VIDEO 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_VIDEO + "\" permission");
        }

        if (readMediaVisualUserSelectedPermissionIndex != -1 && readMediaVisualUserSelectedPermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 READ_MEDIA_VISUAL_USER_SELECTED 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED + "\" permission");
        }

        if (manageExternalStoragePermissionIndex != -1 && manageExternalStoragePermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 MANAGE_EXTERNAL_STORAGE 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" permission");
        }

        if (readExternalStoragePermissionIndex != -1 && readExternalStoragePermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 READ_EXTERNAL_STORAGE 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission");
        }

        if (writeExternalStoragePermissionIndex != -1 && writeExternalStoragePermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_MEDIA_LOCATION 权限放置在 WRITE_EXTERNAL_STORAGE 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permission");
        }

        // 判断当前项目是否适配了 Android 13
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            // 判断请求的权限中是否包含了某些特定权限
            if (PermissionUtils.containsPermission(requestPermissions, PermissionNames.READ_MEDIA_IMAGES) ||
                PermissionUtils.containsPermission(requestPermissions, PermissionNames.READ_MEDIA_VIDEO) ||
                PermissionUtils.containsPermission(requestPermissions, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
                // 如果请求的权限中，包含了上面这些权限，就不往下执行
                return;
            }

            // 如果不包含，你需要在外层手动添加 READ_MEDIA_IMAGES、READ_MEDIA_VIDEO、MANAGE_EXTERNAL_STORAGE 任一权限才可以申请 ACCESS_MEDIA_LOCATION 权限
            throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                PermissionNames.READ_MEDIA_VIDEO + "\" or \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE +
                "\" rights to apply for \"" + getPermissionName() + "\" rights");
        }

        // 如果当前项目还没有适配 Android 13，就判断请求的权限中是否包含了某些特定权限
        if (PermissionUtils.containsPermission(requestPermissions, PermissionNames.READ_EXTERNAL_STORAGE) ||
            PermissionUtils.containsPermission(requestPermissions, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
            // 如果请求的权限中，包含了上面这些权限，就不往下执行
            return;
        }

        // 如果不包含，你需要在外层手动添加 READ_EXTERNAL_STORAGE 或者 MANAGE_EXTERNAL_STORAGE 才可以申请 ACCESS_MEDIA_LOCATION 权限
        throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" or \"" +
            PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" rights to apply for \"" + getPermissionName() + "\" rights");
    }
}