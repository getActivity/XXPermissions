package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.health.connect.HealthConnectManager;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : 健康数据通用权限类
 *    doc    : https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/get-started?hl=zh-cn
 *             https://developer.android.google.cn/health-and-fitness/guides/health-connect/plan/data-types?hl=zh-cn
 *             https://developer.android.google.cn/health-and-fitness/guides/health-connect/plan/availability?hl=zh-cn
 *             https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_m5mZzWneZ6MbLDBhKcyMS
 */
public abstract class HealthDataBasePermission extends DangerousPermission {

    protected HealthDataBasePermission() {
        super();
    }

    protected HealthDataBasePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.HEALTH;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = super.getPermissionSettingIntents(context, skipRequest);

        Intent intent;
        // 在某些 Android 14 ~ Android 15 手机上面，权限设置页是没有健康数据共享权限的入口的，
        // 所以这里直接跳转到健康数据共享的权限设置页，Android 16 则直接跳转到应用详情页就可以了
        if (PermissionVersion.isAndroid14() && !PermissionVersion.isAndroid16()) {
            List<Intent> healthIntentList = new ArrayList<>(3);

            // 亲测 ACTION_MANAGE_HEALTH_PERMISSIONS 这个意图在 Android 14 可以正常跳转，但是 Android 15 跳转会出现异常，意思是没有权限可以跳转到这个页面
            // java.lang.SecurityException: Permission Denial: starting Intent { act=android.health.connect.action.MANAGE_HEALTH_PERMISSIONS xflg=0x4
            // cmp=com.google.android.healthconnect.controller/com.android.healthconnect.controller.PermissionControllerEntryPoint (has extras) } from
            // ProcessRecord{18b95b4 25796:com.hjq.permissions.demo/u0a222} (pid=25796, uid=10222) requires android.permission.GRANT_RUNTIME_PERMISSIONS
            if (!PermissionVersion.isAndroid15()) {
                String action = HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS;
                intent = new Intent(action);
                intent.putExtra(Intent.EXTRA_PACKAGE_NAME, context.getPackageName());
                healthIntentList.add(intent);

                // 如果是因为加包名的数据后导致不能跳转，就把包名的数据移除掉
                intent = new Intent(action);
                healthIntentList.add(intent);
            }

            // android.provider.Settings.ACTION_HEALTH_HOME_SETTINGS
            intent = new Intent("android.health.connect.action.HEALTH_HOME_SETTINGS");
            healthIntentList.add(intent);

            // 将健康数据共享的权限设置页添加到意图列表中，放在集合的最前面，才会优先去跳转到这些意图
            intentList.addAll(0, healthIntentList);
        }

        return intentList;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        final String healthAction;
        if (PermissionVersion.isAndroid16()) {
            healthAction = Intent.ACTION_VIEW_PERMISSION_USAGE;
        } else {
            healthAction = "android.intent.action.VIEW_PERMISSION_USAGE";
        }

        final String healthCategory;
        if (PermissionVersion.isAndroid16()) {
            healthCategory = HealthConnectManager.CATEGORY_HEALTH_PERMISSIONS;
        } else {
            healthCategory = "android.intent.category.HEALTH_PERMISSIONS";
        }

        // 当前是否注册了健康隐私政策页面的意图
        boolean registeredHealthPrivacyPolicyAction = false;
        for (ActivityManifestInfo activityInfo : manifestInfo.activityInfoList) {
            List<IntentFilterManifestInfo> intentFilterInfoList = activityInfo.intentFilterInfoList;
            if (intentFilterInfoList == null) {
                continue;
            }
            for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                if (intentFilterInfo.actionList.contains(healthAction) &&
                    intentFilterInfo.categoryList.contains(healthCategory)) {
                    registeredHealthPrivacyPolicyAction = true;
                    break;
                }
            }
            if (registeredHealthPrivacyPolicyAction) {
                // 如果已经注册，就不再往下遍历
                break;
            }
        }

        if (!registeredHealthPrivacyPolicyAction) {
            String xmlCode = "\t\t<intent-filter>\n"
                           + "\t\t    <action android:name=\"" + healthAction + "\" />\n"
                           + "\t\t    <category android:name=\"" + healthCategory + "\" />\n"
                           + "\t\t</intent-filter>";
            // 必须指定显示应用的隐私权政策对话框
            // https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/get-started?hl=zh-cn#show-privacy-policy
            // 入口点，用户可以在以下方法进入：
            //   1. 应用详情页 > 权限 > 健康数据共享 > 阅读隐私政策
            //   2. 转到设置 > 安全与隐私权 > 隐私权 > Health Connect > 选定应用 > 阅读隐私政策
            //   3. 转到设置 > 安全与隐私权 > 隐私权 > 隐私信息中心 > 查看其他权限 > Health Connect > 选定应用 > 阅读隐私政策
            //   4. 转到设置 > 安全与隐私权 > 隐私权 > 权限管理器 > Health Connect > 选定应用 > 阅读隐私政策
            throw new IllegalArgumentException("Please add an intent filter for \"" + activity.getClass() +
                                                "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }
    }
}