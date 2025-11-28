package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
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
 *    desc   : 写入外部存储权限类
 */
public final class WriteExternalStoragePermission extends DangerousPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.WRITE_EXTERNAL_STORAGE;
    /** 分区存储的 Meta Data Key（仅供内部调用） */
    static final String META_DATA_KEY_SCOPED_STORAGE = ReadExternalStoragePermission.META_DATA_KEY_SCOPED_STORAGE;

    public static final Parcelable.Creator<WriteExternalStoragePermission> CREATOR = new Parcelable.Creator<WriteExternalStoragePermission>() {

        @Override
        public WriteExternalStoragePermission createFromParcel(Parcel source) {
            return new WriteExternalStoragePermission(source);
        }

        @Override
        public WriteExternalStoragePermission[] newArray(int size) {
            return new WriteExternalStoragePermission[size];
        }
    };

    public WriteExternalStoragePermission() {
        // default implementation ignored
    }

    private WriteExternalStoragePermission(Parcel in) {
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
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_11) {
            // 这里补充一下这样写的具体原因：
            // 1. 当 targetSdk >= Android 11 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，虽然可以弹出授权框，但是没有什么实际作用
            //    相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
            //                https://developer.android.google.cn/about/versions/11/privacy/storage?hl=zh-cn#permissions-target-11
            //    开发者可能会在清单文件注册 android:maxSdkVersion="29" 属性，这样会导致 WRITE_EXTERNAL_STORAGE 权限申请失败，这里需要返回 true 给外层
            // 2. 当 targetSdk >= Android 13 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            //    不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里需要返回 true 给到外层
            // 基于上面这两个原因，所以当项目 targetSdk >= Android 11 并且运行在 Android 11 及以上的设备上面时
            // 判断 WRITE_EXTERNAL_STORAGE 权限，结果无论是否授予，最终都会直接返回 true 给外层
            return true;
        }
        // 如果当前项目 targetSdk > Android 10 并且运行在 Android 10 的设备上面，
        // 但是在适配了分区存储的情况下，就直接返回 true 给外层（表示授予了该权限）
        if (PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return true;
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_11) {
            return false;
        }
        // 如果当前项目 targetSdk > Android 10 并且运行在 Android 10 的设备上面，
        // 但是在适配了分区存储的情况下，就直接返回 false 给外层（表示没有勾选不再询问）
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return false;
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 不使用父类的方式来检查清单权限有没有注册，但是不代表不检查，这个权限比较复杂，需要自定义检查
        return false;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        // 如果当前 targetSdk 版本比较低，甚至还没有到分区存储的版本，就直接跳过后面的检查，只检查当前权限有没有在清单文件中静态注册
        if (PermissionVersion.getTargetVersion(activity) < PermissionVersion.ANDROID_10) {
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName());
            return;
        }

        // 判断：当前项目是否适配了Android 11，并且还在清单文件中是否注册了 MANAGE_EXTERNAL_STORAGE 权限
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_11 &&
            findPermissionInfoByList(permissionInfoList, PermissionNames.MANAGE_EXTERNAL_STORAGE) != null) {
            // 如果有的话，那么 maxSdkVersion 就必须是 Android 10 及以上的版本
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName(), PermissionVersion.ANDROID_10);
        } else {
            // 检查这个权限有没有在清单文件中注册，WRITE_EXTERNAL_STORAGE 权限比较特殊，要单独拎出来判断
            // 如果在清单文件中注册了 android:requestLegacyExternalStorage="true" 属性，即可延长一个 Android 版本适配
            // 所以 requestLegacyExternalStorage 属性在开启的状态下，对 maxSdkVersion 属性的要求延长一个版本
            checkPermissionRegistrationStatus(
                permissionInfoList, getPermissionName(), applicationInfo.requestLegacyExternalStorage ?
                                                        PermissionVersion.ANDROID_10 : PermissionVersion.ANDROID_9);
        }

        // 如果申请的是 Android 10 获取媒体位置权限，则跳过后面的检查
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
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
}