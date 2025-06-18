package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 安装应用权限类
 */
public final class RequestInstallPackagesPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.REQUEST_INSTALL_PACKAGES;

    public static final Parcelable.Creator<RequestInstallPackagesPermission> CREATOR = new Parcelable.Creator<RequestInstallPackagesPermission>() {

        @Override
        public RequestInstallPackagesPermission createFromParcel(Parcel source) {
            return new RequestInstallPackagesPermission(source);
        }

        @Override
        public RequestInstallPackagesPermission[] newArray(int size) {
            return new RequestInstallPackagesPermission[size];
        }
    };

    public RequestInstallPackagesPermission() {
        // default implementation ignored
    }

    private RequestInstallPackagesPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_8;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid8()) {
            return true;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 表示当前权限需要在 AndroidManifest.xml 文件中进行静态注册
        return true;
    }
}