package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PhoneRomUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 后台定位权限类
 */
public final class AccessBackgroundLocationPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_BACKGROUND_LOCATION;

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
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // 后台定位权限在澎湃或者 miui 上面一直是透明的 Activity
        if (PhoneRomUtils.isHyperOs() || PhoneRomUtils.isMiui()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // 后台定位权限在荣耀系统上面一直是透明的 Activity
        if (PhoneRomUtils.isMagicOs()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // 后台定位权限在鸿蒙系统上面一直是透明的 Activity
        if (PhoneRomUtils.isHarmonyOs()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // 后台定位权限申请页在 Android 10 还是透明的 Activity，到了 Android 11 就变成了不透明的 Activity
        if (PermissionVersion.isAndroid10() && !PermissionVersion.isAndroid11()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public String getPermissionGroup() {
        return PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_10;
    }

    @NonNull
    @Override
    public List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // 判断当前是否运行在 Android 12 及以上
        if (PermissionVersion.isAndroid12()) {
            // 如果是的话，那么这个前台定位权限既可以是精确定位权限也可以是模糊定位权限
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission(), PermissionLists.getAccessCoarseLocationPermission());
        } else {
            // 如果不是的话，那么这个前台定位权限只能是精确定位权限
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission());
        }
    }

    @Override
    public boolean isBackgroundPermission(@NonNull Context context) {
        // 表示当前权限是后台权限
        return true;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid12()) {
            // 在 Android 12 及之后的版本，前台定位权限既可以用精确定位权限也可以用模糊定位权限
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest) &&
                !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        } else {
            // 在 Android 11 及之前的版本，前台定位权限需要精确定位权限
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        // 如果前台定位权限没有授予，那么后台定位权限不再询问的状态要跟随前台定位权限
        if (PermissionVersion.isAndroid12()) {
            // 在 Android 12 及之后的版本，前台定位权限既可以用精确定位权限也可以用模糊定位权限
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(activity) &&
                !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(activity)) {
                return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity) &&
                        PermissionLists.getAccessCoarseLocationPermission().isDoNotAskAgainPermission(activity);
            }
        } else {
            // 在 Android 11 及之前的版本，前台定位权限需要精确定位权限
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(activity)) {
                return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
            }
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    public int getRequestIntervalTime(@NonNull Context context) {
        // 经过测试，在 Android 11 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
        // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
        // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
        // 官方的文档地址：https://developer.android.google.cn/about/versions/11/privacy?hl=zh-cn
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
        // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
        // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // 官方适配文档：https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_12) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_COARSE_LOCATION);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        } else {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
        // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
        // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // 官方适配文档：https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_12 &&
            PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_COARSE_LOCATION) &&
            !PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            // 申请后台定位权限可以不包含模糊定位权限，但是一定要包含精确定位权限，否则后台定位权限会无法申请
            // 也就是会导致无法弹出授权弹窗，经过实践，在 Android 12 上这个问题已经被解决了
            // 在 Android 12 及之后的版本，申请后台定位权限既可以用精确定位权限也可以用模糊定位权限作为前台定位权限
            // 但是为了兼容 Android 12 以下的设备还是要那么做，否则在 Android 11 及以下设备会出现异常
            // 另外这里解释一下为什么不直接判断有没有包含精确定位权限，而是要判断有模糊定位权限的情况下但是没有精确定位权限的情况
            // 这是因为框架考虑到外部的调用者会将前台定位权限（包含精确定位和模糊定位权限）和后台定位权限拆成独立的两次权限申请
            throw new IllegalArgumentException("Applying for background positioning permissions must include \"" + PermissionNames.ACCESS_FINE_LOCATION + "\"");
        }

        int thisPermissionIndex = -1;
        int accessFineLocationPermissionIndex = -1;
        int accessCoarseLocationPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.ACCESS_FINE_LOCATION)) {
                accessFineLocationPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.ACCESS_COARSE_LOCATION)) {
                accessCoarseLocationPermissionIndex = i;
            }
        }

        if (accessFineLocationPermissionIndex != -1 && accessFineLocationPermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_BACKGROUND_LOCATION 权限放置在 ACCESS_FINE_LOCATION 权限的后面
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permission");
        }

        if (accessCoarseLocationPermissionIndex != -1 && accessCoarseLocationPermissionIndex > thisPermissionIndex) {
            // 请把 ACCESS_BACKGROUND_LOCATION 权限放置在 ACCESS_COARSE_LOCATION 权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.ACCESS_COARSE_LOCATION + "\" permission");
        }
    }
}