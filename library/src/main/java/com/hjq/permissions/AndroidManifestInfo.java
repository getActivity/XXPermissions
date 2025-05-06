package com.hjq.permissions;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : 清单文件解析 Bean 类
 */
final class AndroidManifestInfo {

    /** 应用包名 */
    String packageName;

    /** 使用 sdk 信息 */
    @Nullable
    UsesSdkInfo usesSdkInfo;

    /** 权限节点信息 */
    @NonNull
    final List<PermissionInfo> permissionInfoList = new ArrayList<>();

    /** 查询包名列表 */
    @NonNull
    final List<String> queriesPackageList = new ArrayList<>();

    /** Application 节点信息 */
    @Nullable
    ApplicationInfo applicationInfo;

    /** Activity 节点信息 */
    @NonNull
    final List<ActivityInfo> activityInfoList = new ArrayList<>();

    /** Service 节点信息 */
    @NonNull
    final List<ServiceInfo> serviceInfoList = new ArrayList<>();

    static final class UsesSdkInfo {

        /** 最小安装版本要求 **/
        int minSdkVersion;
    }

    static final class PermissionInfo {

        /** 不需要请求地理位置标志 */
        private static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION;

        static  {
            if (AndroidVersion.isAndroid12()) {
                REQUESTED_PERMISSION_NEVER_FOR_LOCATION = PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION;
            } else {
                REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000;
            }
        }

        /** 权限名称 */
        String name;
        /** 最大生效 sdk 版本 */
        int maxSdkVersion;
        /** 权限使用标志 */
        int usesPermissionFlags;

        /**
         * 是否不会用当前权限需要推导地理位置
         */
        boolean neverForLocation() {
            return (usesPermissionFlags & REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0;
        }
    }

    static final class ApplicationInfo {

        /** 应用的类名 */
        String name;
        /** 是否忽略分区存储特性 */
        boolean requestLegacyExternalStorage;
    }

    static final class ActivityInfo {

        /** 活动的类名 */
        String name;
        /** 窗口是否支持画中画 */
        boolean supportsPictureInPicture;
    }

    static final class ServiceInfo {

        /** 服务的类名 */
        String name;

        /** 服务所使用到的权限 */
        String permission;
    }
}