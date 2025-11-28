package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 读取外部存储权限类
 */
public final class ReadExternalStoragePermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.READ_EXTERNAL_STORAGE;
    /** 分区存储的 Meta Data Key（仅供内部调用） */
    static final String META_DATA_KEY_SCOPED_STORAGE = "ScopedStorage";

    public static final Parcelable.Creator<ReadExternalStoragePermission> CREATOR = new Parcelable.Creator<ReadExternalStoragePermission>() {

        @Override
        public ReadExternalStoragePermission createFromParcel(Parcel source) {
            return new ReadExternalStoragePermission(source);
        }

        @Override
        public ReadExternalStoragePermission[] newArray(int size) {
            return new ReadExternalStoragePermission[size];
        }
    };

    public ReadExternalStoragePermission() {
        // default implementation ignored
    }

    private ReadExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.STORAGE;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_13) {
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getReadMediaAudioPermission().isGrantedPermission(context, skipRequest);
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            return PermissionLists.getReadMediaImagesPermission().isDoNotAskAgainPermission(activity) &&
                PermissionLists.getReadMediaVideoPermission().isDoNotAskAgainPermission(activity) &&
                PermissionLists.getReadMediaAudioPermission().isDoNotAskAgainPermission(activity);
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // 如果申请的是 Android 10 获取媒体位置权限，则绕过本次检查
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        int targetSdkVersion = PermissionVersion.getTargetVersion(activity);
        // 是否适配了分区存储（默认是没有的）
        boolean scopedStorage = false;
        if (applicationInfo.metaDataInfoList != null) {
            for (MetaDataManifestInfo metaDataManifestInfo : applicationInfo.metaDataInfoList) {
                if (META_DATA_KEY_SCOPED_STORAGE.equals(metaDataManifestInfo.name)) {
                    scopedStorage = Boolean.parseBoolean(metaDataManifestInfo.value);
                    break;
                }
            }
        }
        // 如果在已经适配 Android 10 的情况下
        if (targetSdkVersion >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage && !scopedStorage) {
            // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性
            // 否则就算申请了权限，也无法在 Android 10 的设备上正常读写外部存储上的文件
            // 如果你的项目已经全面适配了分区存储，请在清单文件中注册一个 meta-data 属性
            // <meta-data android:name="ScopedStorage" android:value="true" /> 来跳过该检查
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }

        // 如果在已经适配 Android 11 的情况下
        if (targetSdkVersion >= PermissionVersion.ANDROID_11 && !scopedStorage) {
            // 1. 适配分区存储的特性，并在清单文件中注册一个 meta-data 属性
            // <meta-data android:name="ScopedStorage" android:value="true" />
            // 2. 如果不想适配分区存储，则需要使用 Permission.MANAGE_EXTERNAL_STORAGE 来申请权限
            // 上面两种方式需要二选一，否则无法在 Android 11 的设备上正常读写外部存储上的文件
            // 如果不知道该怎么选择，可以看文档：https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
            throw new IllegalArgumentException("The storage permission application is abnormal. If you have adapted the scope storage, " +
                "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                "If there is no adaptation scope storage, please use \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" to apply for permission");
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            /*
               当项目 targetSdkVersion >= 33 时，则不能申请 READ_EXTERNAL_STORAGE 权限，会出现一些问题，
               因为经过测试，如果当 targetSdkVersion >= 33 申请 READ_EXTERNAL_STORAGE 或者 WRITE_EXTERNAL_STORAGE 会直接被系统拒绝，不会显示任何授权对话框，
               如果 App 已经适配了分区存储，应当请求 READ_MEDIA_IMAGES 或 READ_MEDIA_VIDEO 或 READ_MEDIA_AUDIO 权限，
               如果 App 不需要适配分区存储，应当请求 MANAGE_EXTERNAL_STORAGE 权限
             */
            throw new IllegalArgumentException("When the project targetSdkVersion >= 33, the \"" + PermissionNames.READ_EXTERNAL_STORAGE +
                "\" permission cannot be applied for, and some problems will occur." + "Because after testing, if targetSdkVersion >= 33 applies for \"" +
                PermissionNames.READ_EXTERNAL_STORAGE + "\" or \"" + PermissionNames.WRITE_EXTERNAL_STORAGE +
                "\", it will be directly rejected by the system and no authorization dialog box will be displayed."
                + "If the App has been adapted for scoped storage, the should be requested \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                PermissionNames.READ_MEDIA_VIDEO + "\" or \"" + PermissionNames.READ_MEDIA_AUDIO + "\" permission."
                + "If the App does not need to adapt scoped storage, the should be requested \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" permission");
        }
    }
}