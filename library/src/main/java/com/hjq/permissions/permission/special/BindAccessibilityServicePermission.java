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
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        this(Objects.requireNonNull(in.readString()));
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
                // 精准匹配：匹配应用包名及 Service 类名
                if (context.getPackageName().equals(componentName.getPackageName()) &&
                    serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // 模糊匹配：仅匹配应用包名
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
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        super.checkCompliance(activity, requestList, manifestInfo);
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
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        List<ServiceManifestInfo> serviceInfoList = manifestInfo.serviceInfoList;
        for (ServiceManifestInfo serviceInfo : serviceInfoList) {

            if (serviceInfo == null) {
                continue;
            }

            if (!PermissionUtils.reverseEqualsString(mAccessibilityServiceClassName, serviceInfo.name)) {
                // 不是目标的 Service，继续循环
                continue;
            }

            if (serviceInfo.permission == null || !PermissionUtils.equalsPermission(this, serviceInfo.permission)) {
                // 这个 Service 组件注册的 permission 节点为空或者错误
                throw new IllegalArgumentException("Please register permission node in the AndroidManifest.xml file, for example: "
                    + "<service android:name=\"" + mAccessibilityServiceClassName + "\" android:permission=\"" + getPermissionName() + "\" />");
            }

            String action = "android.accessibilityservice.AccessibilityService";
            // 当前是否注册了无障碍服务的意图
            boolean registeredAccessibilityServiceAction = false;
            List<IntentFilterManifestInfo> intentFilterInfoList = serviceInfo.intentFilterInfoList;
            if (intentFilterInfoList != null) {
                for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                    if (intentFilterInfo.actionList.contains(action)) {
                        registeredAccessibilityServiceAction = true;
                        break;
                    }
                }
            }

            if (!registeredAccessibilityServiceAction) {
                String xmlCode = "\t\t<intent-filter>\n"
                               + "\t\t    <action android:name=\"" + action + "\" />\n"
                               + "\t\t</intent-filter>";
                throw new IllegalArgumentException("Please add an intent filter for \"" + mAccessibilityServiceClassName +
                                                    "\" in the AndroidManifest.xml file.\n" + xmlCode);
            }

            String metaDataName = AccessibilityService.SERVICE_META_DATA;
            // 当前是否注册了无障碍服务的 MetaData
            boolean registeredAccessibilityServiceMetaData = false;
            List<MetaDataManifestInfo> metaDataInfoList = serviceInfo.metaDataInfoList;
            if (metaDataInfoList != null) {
                for (MetaDataManifestInfo metaDataInfo : metaDataInfoList) {
                    if (metaDataName.equals(metaDataInfo.name) && metaDataInfo.resource != 0) {
                        registeredAccessibilityServiceMetaData = true;
                        break;
                    }
                }
            }

            if (!registeredAccessibilityServiceMetaData) {
                String xmlCode = "\t\t<meta-data>\n"
                               + "\t\t    android:name=\"" + metaDataName + "\"\n"
                               + "\t\t    android:resource=\"@xml/accessibility_service_config" + "\""+ " />";
                throw new IllegalArgumentException("Please add an meta data for \"" + mAccessibilityServiceClassName +
                                                   "\" in the AndroidManifest.xml file.\n" + xmlCode);
            }

            // 符合要求，中断所有的循环并返回，避免走到后面的抛异常代码
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