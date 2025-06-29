package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.AndroidVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限的基类
 */
public abstract class DangerousPermission extends BasePermission {

    protected DangerousPermission() {
        super();
    }

    protected DangerousPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionType getPermissionType() {
        return PermissionType.DANGEROUS;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (getFromAndroidVersion() > AndroidVersion.getCurrentVersion()) {
            return isGrantedPermissionByLowVersion(context, skipRequest);
        }
        return isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    /**
     * 在标准版本的系统上面判断权限是否授予
     */
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return checkSelfPermission(context, getPermissionName());
    }

    /**
     * 在低版本的系统上面判断权限是否授予
     */
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (getFromAndroidVersion() > AndroidVersion.getCurrentVersion()) {
            return isDoNotAskAgainPermissionByLowVersion(activity);
        }
        return isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    /**
     * 在标准版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        return !checkSelfPermission(activity, getPermissionName()) &&
            !shouldShowRequestPermissionRationale(activity, getPermissionName());
    }

    /**
     * 在低版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        return getApplicationDetailsIntent(context);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 危险权限默认需要在清单文件中注册，这样定义是为了避免外层在自定义特殊权限的时候，还要去重写此方法
        return true;
    }
}