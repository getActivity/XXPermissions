package com.hjq.permissions.permission.special;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 画中画权限类
 */
public final class PictureInPicturePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.PICTURE_IN_PICTURE;

    public static final Parcelable.Creator<PictureInPicturePermission> CREATOR = new Parcelable.Creator<PictureInPicturePermission>() {

        @Override
        public PictureInPicturePermission createFromParcel(Parcel source) {
            return new PictureInPicturePermission(source);
        }

        @Override
        public PictureInPicturePermission[] newArray(int size) {
            return new PictureInPicturePermission[size];
        }
    };

    public PictureInPicturePermission() {
        // default implementation ignored
    }

    private PictureInPicturePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_8;
    }

    @Override
    public boolean isMandatoryStaticRegister() {
        // 表示该权限不需要在清单文件中静态注册
        return false;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid8()) {
            return true;
        }
        return checkOpNoThrow(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE);
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return getApplicationDetailsIntent(context);
        }

        // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
        Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS");
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}