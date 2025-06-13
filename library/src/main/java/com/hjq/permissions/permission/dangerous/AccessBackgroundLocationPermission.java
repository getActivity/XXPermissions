package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.common.DangerousPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 后台定位权限类
 */
public final class AccessBackgroundLocationPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.ACCESS_BACKGROUND_LOCATION;

    public static final Parcelable.Creator<AccessBackgroundLocationPermission> CREATOR = new Parcelable.Creator<AccessBackgroundLocationPermission>() {

        @Override
        public AccessBackgroundLocationPermission createFromParcel(Parcel source) {
            return new AccessBackgroundLocationPermission(source);
        }

        @Override
        public AccessBackgroundLocationPermission[] newArray(int size) {
            return new AccessBackgroundLocationPermission[size];
        }
    };

    public AccessBackgroundLocationPermission() {
        // default implementation ignored
    }

    private AccessBackgroundLocationPermission(Parcel in) {
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
        // 判断后台定位权限授予之前，需要先判断前台定位权限是否授予，如果前台定位权限没有授予，那么后台定位权限就算授予了也没用
        if (!PermissionManifest.getAccessFineLocationPermission().isGranted(context, skipRequest)) {
            return false;
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getAccessFineLocationPermission().isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        // 如果前台定位权限被用户勾选了不再询问选项，那么后台定位权限也要跟着同步
        if (PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgain(activity)) {
            return true;
        }
        return super.isDoNotAskAgainByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgain(activity);
    }

    @Override
    public int getRequestIntervalTime() {
        if (isLowVersionRunning()) {
            return super.getRequestIntervalTime();
        }
        // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
        // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
        // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
        return 150;
    }
}