package com.hjq.permissions.manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.manifest.node.UsesSdkManifestInfo;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
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

    private static final String TAG_META_DATA = "meta-data";

    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_RESOURCE = "resource";
    private static final String ATTR_MAX_SDK_VERSION = "maxSdkVersion";
    private static final String ATTR_MIN_SDK_VERSION = "minSdkVersion";
    private static final String ATTR_USES_PERMISSION_FLAGS = "usesPermissionFlags";
    private static final String ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE = "requestLegacyExternalStorage";
    private static final String ATTR_SUPPORTS_PICTURE_IN_PICTURE = "supportsPictureInPicture";
    private static final String ATTR_PERMISSION = "permission";

    /** 私有化构造函数 */
    private AndroidManifestParser() {
        // default implementation ignored
    }

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

        AndroidManifestInfo manifestInfo = null;
        try {
            manifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie);
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!PermissionUtils.reverseEqualsString(context.getPackageName(), manifestInfo.packageName)) {
                return null;
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return manifestInfo;
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

                if (PermissionUtils.equalsString(TAG_META_DATA, tagName) && manifestInfo.applicationInfo != null) {
                    if (manifestInfo.applicationInfo.metaDataInfoList == null) {
                        manifestInfo.applicationInfo.metaDataInfoList = new ArrayList<>();
                    }
                    manifestInfo.applicationInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
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
        PermissionManifestInfo permissionInfo = new PermissionManifestInfo();
        permissionInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        permissionInfo.maxSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_MAX_SDK_VERSION, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);
        permissionInfo.usesPermissionFlags = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_USES_PERMISSION_FLAGS, 0);
        return permissionInfo;
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
        ActivityManifestInfo activityInfo = new ActivityManifestInfo();
        String activityClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        activityInfo.name = activityClassName != null ? activityClassName : "";
        activityInfo.supportsPictureInPicture = parser.getAttributeBooleanValue(
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
                if (activityInfo.intentFilterInfoList == null) {
                    activityInfo.intentFilterInfoList = new ArrayList<>();
                }
                activityInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (activityInfo.metaDataInfoList == null) {
                    activityInfo.metaDataInfoList = new ArrayList<>();
                }
                activityInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return activityInfo;
    }

    @NonNull
    private static ServiceManifestInfo parseServerFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        ServiceManifestInfo serviceInfo = new ServiceManifestInfo();
        String serviceClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        serviceInfo.name = serviceClassName != null ? serviceClassName : "";
        serviceInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_SERVICE, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (serviceInfo.intentFilterInfoList == null) {
                    serviceInfo.intentFilterInfoList = new ArrayList<>();
                }
                serviceInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (serviceInfo.metaDataInfoList == null) {
                    serviceInfo.metaDataInfoList = new ArrayList<>();
                }
                serviceInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return serviceInfo;
    }

    @NonNull
    private static BroadcastReceiverManifestInfo parseBroadcastReceiverFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        BroadcastReceiverManifestInfo receiverInfo = new BroadcastReceiverManifestInfo();
        String broadcastReceiverClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        receiverInfo.name = broadcastReceiverClassName != null ? broadcastReceiverClassName : "";
        receiverInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_RECEIVER, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (receiverInfo.intentFilterInfoList == null) {
                    receiverInfo.intentFilterInfoList = new ArrayList<>();
                }
                receiverInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (receiverInfo.metaDataInfoList == null) {
                    receiverInfo.metaDataInfoList = new ArrayList<>();
                }
                receiverInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return receiverInfo;
    }

    @NonNull
    private static IntentFilterManifestInfo parseIntentFilterFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        IntentFilterManifestInfo intentFilterInfo = new IntentFilterManifestInfo();
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
                intentFilterInfo.actionList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            } else if (PermissionUtils.equalsString(TAG_CATEGORY, tagName)) {
                intentFilterInfo.categoryList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            }
        }
        return intentFilterInfo;
    }

    @NonNull
    private static MetaDataManifestInfo parseMetaDataFromXml(@NonNull XmlResourceParser parser) throws IOException, XmlPullParserException {
        MetaDataManifestInfo metaDataInfo = new MetaDataManifestInfo();
        metaDataInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        metaDataInfo.value = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_VALUE);
        metaDataInfo.resource = parser.getAttributeResourceValue(ANDROID_NAMESPACE_URI, ATTR_RESOURCE, 0);
        return metaDataInfo;
    }
}