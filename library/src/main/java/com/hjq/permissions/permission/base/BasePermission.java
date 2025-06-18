package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionIntentManager;
import com.hjq.permissions.PermissionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 权限基类
 */
public abstract class BasePermission implements IPermission {

    protected BasePermission() {
        // default implementation ignored
    }

    protected BasePermission(Parcel in) {
        // default implementation ignored
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}

    @NonNull
    @Override
    public String toString() {
        return getPermissionName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // 如果要对比的对象和当前对象的内存地址一样，那么就返回 true
        if (obj == this) {
            return true;
        }
        // 重写 equals 方法是为了 List 和 Map 集合有能力辨别不同的权限对象是不是来自同一个权限
        // 如果这两个权限对象的名称一样，那么就认为它们是同一个权限
        if (obj instanceof IPermission) {
            return PermissionUtils.equalsPermission(getPermissionName(), ((IPermission) obj).getPermissionName());
        } else if (obj instanceof String) {
            return PermissionUtils.equalsPermission(getPermissionName(), ((String) obj));
        }
        return false;
    }

    @NonNull
    public Intent getApplicationDetailsIntent(@NonNull Context context) {
        return PermissionIntentManager.getApplicationDetailsIntent(context, this);
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions, @Nullable AndroidManifestInfo androidManifestInfo) {
        // 检查 targetSdkVersion 是否符合要求
        checkSelfByTargetSdkVersion(activity);
        // 检查 AndroidManifest.xml 是否符合要求
        if (androidManifestInfo != null) {
            List<PermissionInfo> permissionInfoList = androidManifestInfo.permissionInfoList;
            PermissionInfo currentPermissionInfo = findPermissionInfoByList(permissionInfoList, getPermissionName());
            checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        }
        // 检查请求的权限列表是否符合要求
        checkSelfByRequestPermissions(activity, requestPermissions);
    }

    /**
     * 检查 targetSdkVersion 是否符合要求，如果不合规则会抛出异常
     */
    protected void checkSelfByTargetSdkVersion(@NonNull Context context) {
        int minTargetSdkVersion = getMinTargetSdkVersion();
        // 必须设置正确的 targetSdkVersion 才能正常检测权限
        if (AndroidVersionTools.getTargetSdkVersionCode(context) >= minTargetSdkVersion) {
            return;
        }

        throw new IllegalStateException("Request " + getPermissionName() + " permission, " +
            "The targetSdkVersion SDK must be " + minTargetSdkVersion +
            " or more, if you do not want to upgrade targetSdkVersion, " +
            "please apply with the old permission");
    }

    /**
     * 当前权限是否在清单文件中静态注册
     */
    protected abstract boolean isRegisterPermissionByManifestFile();

    /**
     * 检查 AndroidManifest.xml 是否符合要求，如果不合规则会抛出异常
     */
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestPermissions,
                                           @NonNull AndroidManifestInfo androidManifestInfo,
                                           @NonNull List<PermissionInfo> permissionInfoList,
                                           @Nullable PermissionInfo currentPermissionInfo) {
        if (!isRegisterPermissionByManifestFile()) {
            return;
        }
        // 检查当前权限有没有在清单文件中静态注册，如果有注册，还要检查注册 maxSdkVersion 属性有没有问题
        checkPermissionRegistrationStatus(currentPermissionInfo, getPermissionName());
    }

    /**
     * 检查请求的权限列表是否符合要求，如果不合规则会抛出异常
     */
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        // default implementation ignored
        // 默认无任何实现，交由子类自己去实现
    }

    /**
     * 检查权限的注册状态，如果是则会抛出异常
     */
    protected static void checkPermissionRegistrationStatus(@Nullable PermissionInfo permissionInfo, @NonNull String checkPermission) {
        checkPermissionRegistrationStatus(permissionInfo, checkPermission, Integer.MAX_VALUE);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable List<PermissionInfo> permissionInfoList, @NonNull String checkPermission) {
        checkPermissionRegistrationStatus(permissionInfoList, checkPermission, Integer.MAX_VALUE);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable List<PermissionInfo> permissionInfoList, @NonNull String checkPermission, int lowestMaxSdkVersion) {
        PermissionInfo permissionInfo = null;
        if (permissionInfoList != null) {
            permissionInfo = findPermissionInfoByList(permissionInfoList, checkPermission);
        }
        checkPermissionRegistrationStatus(permissionInfo, checkPermission, lowestMaxSdkVersion);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable PermissionInfo permissionInfo, @NonNull String checkPermission, int lowestMaxSdkVersion) {
        if (permissionInfo == null) {
            // 动态申请的权限没有在清单文件中注册，分为以下两种情况：
            // 1. 如果你的项目没有在清单文件中注册这个权限，请直接在清单文件中注册一下即可
            // 2. 如果你的项目明明已注册这个权限，可以检查一下编译完成的 apk 包中是否包含该权限，如果里面没有，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" tools:node="remove"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/98
            throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission + "\" />");
        }

        int manifestMaxSdkVersion = permissionInfo.maxSdkVersion;
        if (manifestMaxSdkVersion < lowestMaxSdkVersion) {
            // 清单文件中所注册的权限 maxSdkVersion 大小不符合最低要求，分为以下两种情况：
            // 1. 如果你的项目中注册了该属性，请根据报错提示修改 maxSdkVersion 属性值或者删除 maxSdkVersion 属性
            // 2. 如果你明明没有注册过 maxSdkVersion 属性，可以检查一下编译完成的 apk 包中是否有该属性，如果里面存在，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" android:maxSdkVersion="xx"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            throw new IllegalArgumentException("The AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission +
                "\" android:maxSdkVersion=\"" + manifestMaxSdkVersion +
                "\" /> does not meet the requirements, " +
                (lowestMaxSdkVersion != Integer.MAX_VALUE ?
                    "the minimum requirement for maxSdkVersion is " + lowestMaxSdkVersion :
                    "please delete the android:maxSdkVersion=\"" + manifestMaxSdkVersion + "\" attribute"));
        }
    }

    /**
     * 获得当前项目的 minSdkVersion
     */
    protected static int getMinSdkVersion(@NonNull Context context, @Nullable AndroidManifestInfo androidManifestInfo) {
        if (AndroidVersionTools.isAndroid7()) {
            return context.getApplicationInfo().minSdkVersion;
        }

        if (androidManifestInfo == null || androidManifestInfo.usesSdkInfo == null) {
            return AndroidVersionTools.ANDROID_4_2;
        }
        return androidManifestInfo.usesSdkInfo.minSdkVersion;
    }

    /**
     * 从权限列表中获取指定的权限信息
     */
    @Nullable
    public static PermissionInfo findPermissionInfoByList(@NonNull List<PermissionInfo> permissionInfoList, @NonNull String permissionName) {
        PermissionInfo permissionInfo = null;
        for (PermissionInfo info : permissionInfoList) {
            if (PermissionUtils.equalsPermission(info.name, permissionName)) {
                permissionInfo = info;
                break;
            }
        }
        return permissionInfo;
    }

    /**
     * 通过 AppOpsManager 判断某个权限是否授予
     *
     * @param opName               需要传入 {@link AppOpsManager} 类中的以 OPSTR 开头的字段
     */
    public static boolean checkOpNoThrow(Context context, String opName) {
        if (!AndroidVersionTools.isAndroid4_4()) {
            return true;
        }
        AppOpsManager appOpsManager;
        if (AndroidVersionTools.isAndroid6()) {
            appOpsManager = context.getSystemService(AppOpsManager.class);
        } else {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (appOpsManager == null) {
            return false;
        }
        int mode;
        if (AndroidVersionTools.isAndroid10()) {
            mode = appOpsManager.unsafeCheckOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
        } else {
            mode = appOpsManager.checkOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 判断 AppOpsManager 某个权限是否授予
     *
     * @param opFieldName               要反射 {@link AppOpsManager} 类中的字段名称
     * @param opDefaultValue            当反射获取不到对应字段的值时，该值作为替补
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean checkOpNoThrow(Context context, String opFieldName, int opDefaultValue) {
        if (!AndroidVersionTools.isAndroid4_4()) {
            return true;
        }
        AppOpsManager appOpsManager;
        if (AndroidVersionTools.isAndroid6()) {
            appOpsManager = context.getSystemService(AppOpsManager.class);
        } else {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (appOpsManager == null) {
            return false;
        }
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            int opValue;
            try {
                Field opValueField = appOpsClass.getDeclaredField(opFieldName);
                opValue = (int) opValueField.get(Integer.class);
            } catch (NoSuchFieldException e) {
                opValue = opDefaultValue;
            }
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            return ((int) checkOpNoThrowMethod.invoke(appOpsManager, opValue, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException | NoSuchMethodException |
                 InvocationTargetException | IllegalAccessException | RuntimeException e) {
            return true;
        }
    }
}