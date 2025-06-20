package com.hjq.permissions.manifest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.manifest.node.UsesSdkManifestInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : 清单文件解析 Bean 类
 */
public final class AndroidManifestInfo {

    /** 应用包名 */
    @NonNull
    public String packageName = "";

    /** 使用 sdk 信息 */
    @Nullable
    public UsesSdkManifestInfo mUsesSdkManifestInfo;

    /** 权限节点信息 */
    @NonNull
    public final List<PermissionManifestInfo> mPermissionManifestInfoList = new ArrayList<>();

    /** 查询包名列表 */
    @NonNull
    public final List<String> queriesPackageList = new ArrayList<>();

    /** Application 节点信息 */
    @Nullable
    public ApplicationManifestInfo mApplicationManifestInfo;

    /** Activity 节点信息 */
    @NonNull
    public final List<ActivityManifestInfo> mActivityManifestInfoList = new ArrayList<>();

    /** Service 节点信息 */
    @NonNull
    public final List<ServiceManifestInfo> mServiceManifestInfoList = new ArrayList<>();

    /** BroadcastReceiver 节点信息 */
    @NonNull
    public final List<BroadcastReceiverManifestInfo> mBroadcastReceiverManifestInfoList = new ArrayList<>();
}