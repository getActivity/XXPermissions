package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
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
 *    desc   : 后台传感器权限类
 */
public final class BodySensorsBackgroundPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.BODY_SENSORS_BACKGROUND;

    public static final Parcelable.Creator<BodySensorsBackgroundPermission> CREATOR = new Parcelable.Creator<BodySensorsBackgroundPermission>() {

        @Override
        public BodySensorsBackgroundPermission createFromParcel(Parcel source) {
            return new BodySensorsBackgroundPermission(source);
        }

        @Override
        public BodySensorsBackgroundPermission[] newArray(int size) {
            return new BodySensorsBackgroundPermission[size];
        }
    };

    public BodySensorsBackgroundPermission() {
        // default implementation ignored
    }

    private BodySensorsBackgroundPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_13;
    }

    @Override
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        // 判断后台传感器权限授予之前，需要先判断前台传感器权限是否授予，如果前台传感器权限没有授予，那么后台传感器权限就算授予了也没用
        if (!PermissionManifest.getBodySensorsPermission().isGranted(context, skipRequest)) {
            return false;
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getBodySensorsPermission().isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        // 如果前台传感器权限被用户勾选了不再询问选项，那么后台传感器权限也要跟着同步
        if (PermissionManifest.getBodySensorsPermission().isDoNotAskAgain(activity)) {
            return true;
        }
        return super.isDoNotAskAgainByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getBodySensorsPermission().isDoNotAskAgain(activity);
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

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionInfo> permissionInfoList,
                                            @Nullable PermissionInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        // 申请后台的传感器权限必须要先注册前台的传感器权限
        checkPermissionRegistrationStatus(permissionInfoList, PermissionConstants.BODY_SENSORS);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);
        // 必须要申请前台传感器权限才能申请后台传感器权限
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionConstants.BODY_SENSORS)) {
            throw new IllegalArgumentException("Applying for background sensor permissions must contain " + PermissionConstants.BODY_SENSORS);
        }
    }
}