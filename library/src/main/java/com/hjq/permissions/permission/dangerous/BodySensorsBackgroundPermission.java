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
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 后台传感器权限类
 */
public final class BodySensorsBackgroundPermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BODY_SENSORS_BACKGROUND;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup() {
        return PermissionGroups.SENSORS;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getForegroundPermissions(@NonNull Context context) {
        return PermissionUtils.asArrayList(PermissionLists.getBodySensorsPermission());
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        // 判断后台传感器权限授予之前，需要先判断前台传感器权限是否授予，如果前台传感器权限没有授予，那么后台传感器权限就算授予了也没用
        if (!PermissionLists.getBodySensorsPermission().isGrantedPermission(context, skipRequest)) {
            return false;
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getBodySensorsPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        // 如果前台传感器权限没有授予，那么后台传感器权限不再询问的状态要跟随前台传感器权限
        if (!PermissionLists.getBodySensorsPermission().isGrantedPermission(activity)) {
            return PermissionLists.getBodySensorsPermission().isDoNotAskAgainPermission(activity);
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getBodySensorsPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    public int getRequestIntervalTime(@NonNull Context context) {
        // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
        // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
        // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList,
            currentPermissionManifestInfo);
        // 申请后台的传感器权限必须要先注册前台的传感器权限
        checkPermissionRegistrationStatus(permissionManifestInfoList, PermissionNames.BODY_SENSORS);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        super.checkSelfByRequestPermissions(activity, requestPermissions);
        // 必须要申请前台传感器权限才能申请后台传感器权限
        if (!PermissionUtils.containsPermission(requestPermissions, PermissionNames.BODY_SENSORS)) {
            throw new IllegalArgumentException("Applying for background sensor permissions must contain \"" + PermissionNames.BODY_SENSORS + "\"");
        }

        int thisPermissionIndex = -1;
        int bodySensorsPermissionindex = -1;
        for (int i = 0; i < requestPermissions.size(); i++) {
            IPermission permission = requestPermissions.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.BODY_SENSORS)) {
                bodySensorsPermissionindex = i;
            }
        }

        if (bodySensorsPermissionindex != -1 && bodySensorsPermissionindex > thisPermissionIndex) {
            // 请把 BODY_SENSORS_BACKGROUND 权限放置在 BODY_SENSORS 权限的后面
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.BODY_SENSORS + "\" permission");
        }
    }
}