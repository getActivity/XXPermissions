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

   /** Application 节点信息 */
   ApplicationInfo applicationInfo;

   /** Activity 节点信息 */
   @NonNull
   final List<ActivityInfo> activityInfoList = new ArrayList<>();

   /** Service 节点信息 */
   @NonNull
   final List<ServiceInfo> serviceInfoList = new ArrayList<>();

   static final class UsesSdkInfo {

      public int minSdkVersion;
   }

   static final class PermissionInfo {

      /** {@link PackageInfo#REQUESTED_PERMISSION_NEVER_FOR_LOCATION} */
      private static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000;

      public String name;
      public int maxSdkVersion;
      public int usesPermissionFlags;

      public boolean neverForLocation() {
         return (usesPermissionFlags & REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0;
      }
   }

   final static class ApplicationInfo {
      public String name;
      public boolean requestLegacyExternalStorage;
   }

   final static class ActivityInfo {
      public String name;
      public boolean supportsPictureInPicture;
   }

   final static class ServiceInfo {
      public String name;
      public String permission;
   }
}