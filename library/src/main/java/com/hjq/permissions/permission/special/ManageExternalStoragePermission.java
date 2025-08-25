package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 所有文件访问权限类
 */
public final class ManageExternalStoragePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.MANAGE_EXTERNAL_STORAGE;

    public static final Parcelable.Creator<ManageExternalStoragePermission> CREATOR = new Parcelable.Creator<ManageExternalStoragePermission>() {

        @Override
        public ManageExternalStoragePermission createFromParcel(Parcel source) {
            return new ManageExternalStoragePermission(source);
        }

        @Override
        public ManageExternalStoragePermission[] newArray(int size) {
            return new ManageExternalStoragePermission[size];
        }
    };

    public ManageExternalStoragePermission() {
        // default implementation ignored
    }

    private ManageExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_11;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 11 以下访问完整的文件管理需要用到读写外部存储的权限
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission(),
                                            PermissionLists.getWriteExternalStoragePermission());
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid11()) {
            // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
            // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
            // Environment.isExternalStorageLegacy API 解释：是否采用的是非分区存储的模式
            if (PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
                return false;
            }
            return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getWriteExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        // 是否有所有文件的管理权限
        return Environment.isExternalStorageManager();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
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

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果权限出现的版本小于 minSdkVersion，则证明该权限可能会在旧系统上面申请，需要在 AndroidManifest.xml 文件注册一下旧版权限
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.WRITE_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
        }

        // 如果申请的是 Android 10 获取媒体位置权限，则绕过本次检查
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        // 如果在已经适配 Android 10 的情况下，但是 android:requestLegacyExternalStorage 的属性为 false（假设没有注册该属性的情况下则获取到的值为 false）
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage) {
            // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性
            // 否则就算申请了权限，也无法在 Android 10 的设备上正常读写外部存储上的文件
            // 如果你的项目已经全面适配了分区存储，请在清单文件中注册一个 meta-data 属性
            // <meta-data android:name="ScopedStorage" android:value="true" /> 来跳过该检查
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
        // 框架会在 Android 10 以下的版本上自动添加并申请这两个权限
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.WRITE_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("If you have applied for \"" + getPermissionName() + "\" permissions, " +
                                                "do not apply for the \"" + PermissionNames.READ_EXTERNAL_STORAGE +
                                                "\" or \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permissions");
        }

        // 因为 MANAGE_EXTERNAL_STORAGE 权限范围很大，有了它就可以读取媒体文件，不需要再叠加申请媒体权限
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_AUDIO)) {
            throw new IllegalArgumentException("Because the \"" + getPermissionName() + "\" permission range is very large, "
                + "you can read media files with it, and there is no need to apply for additional media permissions.");
        }
    }
}