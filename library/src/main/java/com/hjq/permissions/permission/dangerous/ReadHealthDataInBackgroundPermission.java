package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : 后台读取健康数据权限类
 */
public final class ReadHealthDataInBackgroundPermission extends HealthDataBasePermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND;

    public static final Creator<ReadHealthDataInBackgroundPermission> CREATOR = new Creator<ReadHealthDataInBackgroundPermission>() {

        @Override
        public ReadHealthDataInBackgroundPermission createFromParcel(Parcel source) {
            return new ReadHealthDataInBackgroundPermission(source);
        }

        @Override
        public ReadHealthDataInBackgroundPermission[] newArray(int size) {
            return new ReadHealthDataInBackgroundPermission[size];
        }
    };

    public ReadHealthDataInBackgroundPermission() {
        // default implementation ignored
    }

    private ReadHealthDataInBackgroundPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_15;
    }

    @Nullable
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        if (!PermissionVersion.isAndroid14()) {
            // 这里解释一下为什么只在 Android 14 以下的版本才返回后台传感器权限，因为在 Android 14 之前，
            // Android 传感器权限本质上是为了读取心率传感器而准备的，直到 Android 14 发布将细分到健康数据权限中的读取心率权限，
            // 然而 Android 14 并没有出现与之对应的后台权限，直到 Android 15 才出现这个后台权限，这里就出现了一个兼容的问题，
            // 在这里框架认为在 Android 14 上面用 HealthConnectManager 在后台读取心率是不需要权限的，从 Android 15 开始才需要。
            return PermissionUtils.asArrayList(PermissionLists.getBodySensorsBackgroundPermission());
        }
        return null;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.BODY_SENSORS_BACKGROUND, PermissionVersion.ANDROID_14);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        int thisPermissionIndex = -1;
        int readHealthDataHistoryPermissionIndex = -1;
        int otherHealthDataPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                readHealthDataHistoryPermissionIndex = i;
            } else if (PermissionApi.isHealthPermission(permission)) {
                otherHealthDataPermissionIndex = i;
            }
        }

        if (readHealthDataHistoryPermissionIndex != -1 && readHealthDataHistoryPermissionIndex > thisPermissionIndex) {
            // 请把 READ_HEALTH_DATA_IN_BACKGROUND 权限放置在 READ_HEALTH_DATA_HISTORY 权限的后面
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_HEALTH_DATA_HISTORY + "\" permission");
        }

        if (otherHealthDataPermissionIndex != -1 && otherHealthDataPermissionIndex > thisPermissionIndex) {
            // 请把 READ_HEALTH_DATA_IN_BACKGROUND 权限放置在其他健康数据权限的后面
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + requestList.get(otherHealthDataPermissionIndex) + "\" permission");
        }
    }
}