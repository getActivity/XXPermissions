package com.hjq.permissions.manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.manifest.node.UsesSdkManifestInfo;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : 清单文件解析器
 */
public final class AndroidManifestParser {

    /** 清单文件的文件名称 */
    private static final String ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml";

    /** Android 的命名空间 */
    private static final String ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android";

    private static final String TAG_MANIFEST = "manifest";

    private static final String TAG_USES_SDK = "uses-sdk";
    private static final String TAG_USES_PERMISSION = "uses-permission";
    private static final String TAG_USES_PERMISSION_SDK_23 = "uses-permission-sdk-23";
    private static final String TAG_USES_PERMISSION_SDK_M = "uses-permission-sdk-m";

    private static final String TAG_QUERIES = "queries";

    private static final String TAG_APPLICATION = "application";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_ACTIVITY_ALIAS = "activity-alias";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_RECEIVER = "receiver";

    private static final String TAG_INTENT_FILTER = "intent-filter";
    private static final String TAG_ACTION = "action";
    private static final String TAG_CATEGORY = "category";

    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_MAX_SDK_VERSION = "maxSdkVersion";
    private static final String ATTR_MIN_SDK_VERSION = "minSdkVersion";
    private static final String ATTR_USES_PERMISSION_FLAGS = "usesPermissionFlags";
    private static final String ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE = "requestLegacyExternalStorage";
    private static final String ATTR_SUPPORTS_PICTURE_IN_PICTURE = "supportsPictureInPicture";
    private static final String ATTR_PERMISSION = "permission";

    private AndroidManifestParser() {}

    /**
     * 获取当前应用的清单文件信息
     */
    @Nullable
    public static AndroidManifestInfo getAndroidManifestInfo(Context context) {
        int apkPathCookie = AndroidManifestParser.findApkPathCookie(context, context.getApplicationInfo().sourceDir);
        // 如果 cookie 为 0，证明获取失败
        if (apkPathCookie == 0) {
            return null;
        }

        AndroidManifestInfo androidManifestInfo = null;
        try {
            androidManifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie);
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!PermissionUtils.reverseEqualsString(context.getPackageName(), androidManifestInfo.packageName)) {
                return null;
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return androidManifestInfo;
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    public static int findApkPathCookie(@NonNull Context context, @NonNull String apkPath) {
        AssetManager assets = context.getAssets();
        Integer cookie;

        try {
            if (PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_9 &&
                PermissionVersion.getCurrentVersion() >= PermissionVersion.ANDROID_9 &&
                PermissionVersion.getCurrentVersion() < PermissionVersion.ANDROID_11) {

                // 反射套娃操作：实测这种方式只在 Android 9.0 和 Android 10.0 有效果，在 Android 11 上面就失效了
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);
                metaGetDeclaredMethod.setAccessible(true);
                // 注意 AssetManager.findCookieForPath 是 Android 9.0（API 28）的时候才添加的方法
                // 而 Android 9.0 用的是 AssetManager.addAssetPath 来获取 cookie
                // 具体可以参考 PackageParser.parseBaseApk 方法源码的实现
                Method findCookieForPathMethod = (Method) metaGetDeclaredMethod.invoke(AssetManager.class,
                    "findCookieForPath", new Class[]{String.class});
                if (findCookieForPathMethod != null) {
                    findCookieForPathMethod.setAccessible(true);
                    cookie = (Integer) findCookieForPathMethod.invoke(context.getAssets(), apkPath);
                    if (cookie != null) {
                        return cookie;
                    }
                }
            }

            Method addAssetPathMethod = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            cookie = (Integer) addAssetPathMethod.invoke(assets, apkPath);
            if (cookie != null) {
                return cookie;
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 获取失败直接返回 0
        // 为什么不直接返回 Integer，而是返回 int 类型？
        // 去看看 AssetManager.findCookieForPath 获取失败会返回什么就知道了
        return 0;
    }

    /**
     * 解析 apk 包中的清单文件
     *
     * @param context          上下文
     * @param apkCookie        要解析 apk 的 cookie
     */
    @NonNull
    public static AndroidManifestInfo parseAndroidManifest(@NonNull Context context, int apkCookie) throws IOException, XmlPullParserException {
        AndroidManifestInfo manifestInfo = new AndroidManifestInfo();

        try (XmlResourceParser parser = context.getAssets().
            openXmlResourceParser(apkCookie, ANDROID_MANIFEST_FILE_NAME)) {

            do {
                // 当前节点必须为标签头部
                if (parser.getEventType() != XmlResourceParser.START_TAG) {
                    continue;
                }

                String tagName = parser.getName();

                if (PermissionUtils.equalsString(TAG_MANIFEST, tagName)) {
                    manifestInfo.packageName = parsePackageFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_USES_SDK, tagName)) {
                    manifestInfo.usesSdkInfo = parseUsesSdkFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_USES_PERMISSION, tagName) ||
                    PermissionUtils.equalsString(TAG_USES_PERMISSION_SDK_23, tagName) ||
                    PermissionUtils.equalsString(TAG_USES_PERMISSION_SDK_M, tagName)) {
                    manifestInfo.permissionInfoList.add(parsePermissionFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_QUERIES, tagName)) {
                    manifestInfo.queriesPackageList.add(parsePackageFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_APPLICATION, tagName)) {
                    manifestInfo.applicationInfo = parseApplicationFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_ACTIVITY, tagName) ||
                    PermissionUtils.equalsString(TAG_ACTIVITY_ALIAS, tagName)) {
                    manifestInfo.activityInfoList.add(parseActivityFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_SERVICE, tagName)) {
                    manifestInfo.serviceInfoList.add(parseServerFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_RECEIVER, tagName)) {
                    manifestInfo.receiverInfoList.add(parseBroadcastReceiverFromXml(parser));
                }

            } while (parser.next() != XmlResourceParser.END_DOCUMENT);
        }

        return manifestInfo;
    }

    @NonNull
    private static String parsePackageFromXml(@NonNull XmlResourceParser parser) {
        String packageName = parser.getAttributeValue(null, ATTR_PACKAGE);
        return packageName != null ? packageName : "";
    }

    @NonNull
    private static UsesSdkManifestInfo parseUsesSdkFromXml(@NonNull XmlResourceParser parser) {
        UsesSdkManifestInfo usesSdkInfo = new UsesSdkManifestInfo();
        usesSdkInfo.minSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_MIN_SDK_VERSION, 0);
        return usesSdkInfo;
    }

    @NonNull
    private static PermissionManifestInfo parsePermissionFromXml(@NonNull XmlResourceParser parser) {
        PermissionManifestInfo permissionManifestInfo = new PermissionManifestInfo();
        permissionManifestInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        permissionManifestInfo.maxSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_MAX_SDK_VERSION, Integer.MAX_VALUE);
        permissionManifestInfo.usesPermissionFlags = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_USES_PERMISSION_FLAGS, 0);
        return permissionManifestInfo;
    }

    @NonNull
    private static ApplicationManifestInfo parseApplicationFromXml(@NonNull XmlResourceParser parser) {
        ApplicationManifestInfo applicationInfo = new ApplicationManifestInfo();
        String applicationClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        applicationInfo.name = applicationClassName != null ? applicationClassName : "";
        applicationInfo.requestLegacyExternalStorage = parser.getAttributeBooleanValue(
            ANDROID_NAMESPACE_URI, ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE, false);
        return applicationInfo;
    }

    @NonNull
    private static ActivityManifestInfo parseActivityFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        ActivityManifestInfo activityManifestInfo = new ActivityManifestInfo();
        String activityClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        activityManifestInfo.name = activityClassName != null ? activityClassName : "";
        activityManifestInfo.supportsPictureInPicture = parser.getAttributeBooleanValue(
            ANDROID_NAMESPACE_URI, ATTR_SUPPORTS_PICTURE_IN_PICTURE, false);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG &&
                (PermissionUtils.equalsString(TAG_ACTIVITY, tagName) ||
                    PermissionUtils.equalsString(TAG_ACTIVITY_ALIAS, tagName))) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (activityManifestInfo.intentFilterInfoList == null) {
                    activityManifestInfo.intentFilterInfoList = new ArrayList<>();
                }
                activityManifestInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            }
        }

        return activityManifestInfo;
    }

    @NonNull
    private static ServiceManifestInfo parseServerFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        ServiceManifestInfo serviceManifestInfo = new ServiceManifestInfo();
        String serviceClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        serviceManifestInfo.name = serviceClassName != null ? serviceClassName : "";
        serviceManifestInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_SERVICE, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (serviceManifestInfo.intentFilterInfoList == null) {
                    serviceManifestInfo.intentFilterInfoList = new ArrayList<>();
                }
                serviceManifestInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            }
        }

        return serviceManifestInfo;
    }

    @NonNull
    private static BroadcastReceiverManifestInfo parseBroadcastReceiverFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        BroadcastReceiverManifestInfo broadcastReceiverManifestInfo = new BroadcastReceiverManifestInfo();
        String broadcastReceiverClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        broadcastReceiverManifestInfo.name = broadcastReceiverClassName != null ? broadcastReceiverClassName : "";
        broadcastReceiverManifestInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_RECEIVER, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (broadcastReceiverManifestInfo.intentFilterInfoList == null) {
                    broadcastReceiverManifestInfo.intentFilterInfoList = new ArrayList<>();
                }
                broadcastReceiverManifestInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            }
        }

        return broadcastReceiverManifestInfo;
    }

    @NonNull
    private static IntentFilterManifestInfo parseIntentFilterFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        IntentFilterManifestInfo intentFilterManifestInfo = new IntentFilterManifestInfo();
        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                break;
            }

            if (nextTagType != XmlResourceParser.START_TAG) {
                continue;
            }

            if (PermissionUtils.equalsString(TAG_ACTION, tagName)) {
                intentFilterManifestInfo.actionList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            } else if (PermissionUtils.equalsString(TAG_CATEGORY, tagName)) {
                intentFilterManifestInfo.categoryList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            }
        }
        return intentFilterManifestInfo;
    }
}