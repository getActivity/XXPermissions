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
import com.hjq.permissions.permission.PermissionGroupConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import java.util.List;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @NonNull
    @Override
    public String getPermissionGroup() {
        return PermissionGroupConstants.LOCATION;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_10;
    }

    @NonNull
    @Override
    public List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // 判断当前应用适配且运行在 Android 12 及以上
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_12)) {
            // 如果是的话，那么这个前台定位权限可以是精确定位权限，也可以是模糊定位权限
            return PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission(), PermissionManifest.getAccessCoarseLocationPermission());
        } else {
            // 如果不是的话，那么这个前台定位权限只能是精确定位权限
            return PermissionUtils.asArrayList(PermissionManifest.getAccessFineLocationPermission());
        }
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        // 判断后台定位权限授予之前，需要先判断前台定位权限是否授予，如果前台定位权限没有授予，那么后台定位权限就算授予了也没用
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_12)) {
            // 在 Android 12 及之后的版本，申请后台定位权限既可以用精确定位权限也可以用模糊定位权限
            if (!PermissionManifest.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest) &&
                !PermissionManifest.getAccessCoarseLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        } else {
            // 在 Android 11 及之前的版本，申请后台定位权限需要精确定位权限
            if (!PermissionManifest.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        // 如果前台定位权限被用户勾选了不再询问选项，那么后台定位权限也要跟着同步
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_12)) {
            // 在 Android 12 及之后的版本，申请后台定位权限既可以用精确定位权限也可以用模糊定位权限
            if (PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity) &&
                PermissionManifest.getAccessCoarseLocationPermission().isDoNotAskAgainPermission(activity)) {
                return true;
            }
        } else {
            // 在 Android 11 及之前的版本，申请后台定位权限需要精确定位权限
            if (PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity)) {
                return true;
            }
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
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
        // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
        // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
        // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // 官方适配文档：https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_12) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionConstants.ACCESS_COARSE_LOCATION);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionConstants.ACCESS_FINE_LOCATION);
        } else {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionConstants.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);
        // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
        // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
        // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // 官方适配文档：https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (AndroidVersionTools.getTargetSdkVersionCode(activity) >= AndroidVersionTools.ANDROID_12 &&
            PermissionUtils.containsPermission(requestPermissions, PermissionConstants.ACCESS_COARSE_LOCATION) &&
            !PermissionUtils.containsPermission(requestPermissions, PermissionConstants.ACCESS_FINE_LOCATION)) {
            // 申请后台定位权限可以不包含模糊定位权限，但是一定要包含精确定位权限，否则后台定位权限会无法申请
            // 也就是会导致无法弹出授权弹窗，经过实践，在 Android 12 上这个问题已经被解决了
            // 在 Android 12 及之后的版本，申请后台定位权限既可以用精确定位权限也可以用模糊定位权限作为前台定位权限
            // 但是为了兼容 Android 12 以下的设备还是要那么做，否则在 Android 11 及以下设备会出现异常
            // 另外这里解释一下为什么不直接判断有没有包含精确定位权限，而是要判断有模糊定位权限的情况下但是没有精确定位权限的情况
            // 这是因为框架考虑到外部的调用者会将前台定位权限（包含精确定位和模糊定位权限）和后台定位权限拆成独立的两次权限申请
            throw new IllegalArgumentException("Applying for background positioning permissions must include " + PermissionConstants.ACCESS_FINE_LOCATION);
        }
    }
}