package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/09/12
 *    desc   : 管理媒体权限
 */
public final class ManageMediaPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.MANAGE_MEDIA;

    public static final Creator<ManageMediaPermission> CREATOR = new Creator<ManageMediaPermission>() {

        @Override
        public ManageMediaPermission createFromParcel(Parcel source) {
            return new ManageMediaPermission(source);
        }

        @Override
        public ManageMediaPermission[] newArray(int size) {
            return new ManageMediaPermission[size];
        }
    };

    public ManageMediaPermission() {
        // default implementation ignored
    }

    private ManageMediaPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid12()) {
            return true;
        }
        return MediaStore.canManageMedia(context);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid12()) {
            intent = new Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            intent = new Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA);
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 表示当前权限需要在 AndroidManifest.xml 文件中进行静态注册
        return true;
    }
}