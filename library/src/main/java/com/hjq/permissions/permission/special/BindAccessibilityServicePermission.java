package com.hjq.permissions.permission.special;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
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
 *    time   : 2025/06/15
 *    desc   : 无障碍服务权限类
 */
public final class BindAccessibilityServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BIND_ACCESSIBILITY_SERVICE;

    public static final Creator<BindAccessibilityServicePermission> CREATOR = new Creator<BindAccessibilityServicePermission>() {

        @Override
        public BindAccessibilityServicePermission createFromParcel(Parcel source) {
            return new BindAccessibilityServicePermission(source);
        }

        @Override
        public BindAccessibilityServicePermission[] newArray(int size) {
            return new BindAccessibilityServicePermission[size];
        }
    };

    /** 无障碍 Service 类名 */
    @NonNull
    private final String mAccessibilityServiceClassName;

    public BindAccessibilityServicePermission(@NonNull Class<? extends AccessibilityService> accessibilityServiceClass) {
        this(accessibilityServiceClass.getName());
    }

    public BindAccessibilityServicePermission(@NonNull String accessibilityServiceClassName) {
        mAccessibilityServiceClassName = accessibilityServiceClassName;
    }

    private BindAccessibilityServicePermission(Parcel in) {
        this(in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mAccessibilityServiceClassName);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_4_1;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        final String enabledNotificationListeners = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false;
        }

        String serviceClassName = PermissionUtils.isClassExist(mAccessibilityServiceClassName) ? mAccessibilityServiceClassName : null;
        // hello.litiaotiao.app/hello.litiaotiao.app.LttService:com.hjq.permissions.demo/com.hjq.permissions.demo.DemoAccessibilityService
        final String[] allComponentNameArray = enabledNotificationListeners.split(":");
        for (String component : allComponentNameArray) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName == null) {
                continue;
            }
            if (serviceClassName != null) {
                // 精准匹配
                if (serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // 模糊匹配
                if (context.getPackageName().equals(componentName.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(2);
        // 这里解释一下为什么只能跳转到无障碍设置页？而不是当前应用的无障碍设置页？
        // 这是因为系统没有开放这个途径给应用层去实现，所以实现不了，你可能会说，这不是瞎扯？
        // 我明明看到 Settings 类中有一个意图叫 ACTION_ACCESSIBILITY_DETAILS_SETTINGS，怎么就实现不了？
        // 能看到不代表能用，OK？这个 Action 我已经帮大家试过了，普通应用没有办法跳转的，放弃吧
        intentList.add(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        intentList.add(getAndroidSettingIntent());
        return intentList;
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestList, @Nullable AndroidManifestInfo androidManifestInfo) {
        super.checkCompliance(activity, requestList, androidManifestInfo);
        if (TextUtils.isEmpty(mAccessibilityServiceClassName)) {
            throw new IllegalArgumentException("Pass the ServiceClass parameter as empty");
        }
        if (!PermissionUtils.isClassExist(mAccessibilityServiceClassName)) {
            throw new IllegalArgumentException("The passed-in " + mAccessibilityServiceClassName + " is an invalid class");
        }
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestList, androidManifestInfo, permissionManifestInfoList, currentPermissionManifestInfo);

        List<ServiceManifestInfo> serviceManifestInfoList = androidManifestInfo.serviceManifestInfoList;
        for (ServiceManifestInfo serviceManifestInfo : serviceManifestInfoList) {
            if (serviceManifestInfo == null) {
                continue;
            }
            if (!PermissionUtils.reverseEqualsString(mAccessibilityServiceClassName, serviceManifestInfo.name)) {
                // 不是目标的 Service，继续循环
                continue;
            }
            if (serviceManifestInfo.permission == null || !PermissionUtils.equalsPermission(this, serviceManifestInfo.permission)) {
                // 这个 Service 组件注册的 permission 节点为空或者错误
                throw new IllegalArgumentException("Please register permission node in the AndroidManifest.xml file, for example: "
                    + "<service android:name=\"" + mAccessibilityServiceClassName + "\" android:permission=\"" + getPermissionName() + "\" />");
            }
            return;
        }

        // 这个 Service 组件没有在清单文件中注册
        throw new IllegalArgumentException("The \"" + mAccessibilityServiceClassName + "\" component is not registered in the AndroidManifest.xml file");
    }

    @NonNull
    public String getAccessibilityServiceClassName() {
        return mAccessibilityServiceClassName;
    }
}