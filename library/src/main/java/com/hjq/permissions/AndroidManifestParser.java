package com.hjq.permissions;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : 清单文件解析器
 */
final class AndroidManifestParser {

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
     * 解析 apk 包中的清单文件
     *
     * @param context          上下文
     * @param apkCookie        要解析 apk 的 cookie
     */
    @NonNull
    static AndroidManifestInfo parseAndroidManifest(@NonNull Context context, int apkCookie) throws IOException, XmlPullParserException {
        AndroidManifestInfo manifestInfo = new AndroidManifestInfo();

        try (XmlResourceParser parser = context.getAssets().
            openXmlResourceParser(apkCookie, ANDROID_MANIFEST_FILE_NAME)) {

            do {
                // 当前节点必须为标签头部
                if (parser.getEventType() != XmlResourceParser.START_TAG) {
                    continue;
                }

                String tagName = parser.getName();

                if (TextUtils.equals(TAG_MANIFEST, tagName)) {
                    manifestInfo.packageName = parsePackageFromXml(parser);
                }

                if (TextUtils.equals(TAG_USES_SDK, tagName)) {
                    manifestInfo.usesSdkInfo = parseUsesSdkFromXml(parser);
                }

                if (TextUtils.equals(TAG_USES_PERMISSION, tagName) ||
                    TextUtils.equals(TAG_USES_PERMISSION_SDK_23, tagName) ||
                    TextUtils.equals(TAG_USES_PERMISSION_SDK_M, tagName)) {
                    manifestInfo.permissionInfoList.add(parsePermissionFromXml(parser));
                }

                if (TextUtils.equals(TAG_QUERIES, tagName)) {
                    manifestInfo.queriesPackageList.add(parsePackageFromXml(parser));
                }

                if (TextUtils.equals(TAG_APPLICATION, tagName)) {
                    manifestInfo.applicationInfo = parseApplicationFromXml(parser);
                }

                if (TextUtils.equals(TAG_ACTIVITY, tagName) ||
                    TextUtils.equals(TAG_ACTIVITY_ALIAS, tagName)) {
                    manifestInfo.activityInfoList.add(parseActivityFromXml(parser));
                }

                if (TextUtils.equals(TAG_SERVICE, tagName)) {
                    manifestInfo.serviceInfoList.add(parseServerFromXml(parser));
                }

            } while (parser.next() != XmlResourceParser.END_DOCUMENT);
        }

        return manifestInfo;
    }

    @NonNull
    private static String parsePackageFromXml(@NonNull XmlResourceParser parser) {
        return parser.getAttributeValue(null, ATTR_PACKAGE);
    }

    @NonNull
    private static AndroidManifestInfo.UsesSdkInfo parseUsesSdkFromXml(@NonNull XmlResourceParser parser) {
        AndroidManifestInfo.UsesSdkInfo usesSdkInfo = new AndroidManifestInfo.UsesSdkInfo();
        usesSdkInfo.minSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_MIN_SDK_VERSION, 0);
        return usesSdkInfo;
    }

    @NonNull
    private static AndroidManifestInfo.PermissionInfo parsePermissionFromXml(@NonNull XmlResourceParser parser) {
        AndroidManifestInfo.PermissionInfo permissionInfo = new AndroidManifestInfo.PermissionInfo();
        permissionInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        permissionInfo.maxSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_MAX_SDK_VERSION, Integer.MAX_VALUE);
        permissionInfo.usesPermissionFlags = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI,
            ATTR_USES_PERMISSION_FLAGS, 0);
        return permissionInfo;
    }

    @NonNull
    private static AndroidManifestInfo.ApplicationInfo parseApplicationFromXml(@NonNull XmlResourceParser parser) {
        AndroidManifestInfo.ApplicationInfo applicationInfo = new AndroidManifestInfo.ApplicationInfo();
        applicationInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        applicationInfo.requestLegacyExternalStorage = parser.getAttributeBooleanValue(
            ANDROID_NAMESPACE_URI, ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE, false);
        return applicationInfo;
    }

    @NonNull
    private static AndroidManifestInfo.ActivityInfo parseActivityFromXml(@NonNull XmlResourceParser parser) {
        AndroidManifestInfo.ActivityInfo activityInfo = new AndroidManifestInfo.ActivityInfo();
        activityInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        activityInfo.supportsPictureInPicture = parser.getAttributeBooleanValue(
            ANDROID_NAMESPACE_URI, ATTR_SUPPORTS_PICTURE_IN_PICTURE, false);
        return activityInfo;
    }

    @NonNull
    private static AndroidManifestInfo.ServiceInfo parseServerFromXml(@NonNull XmlResourceParser parser) {
        AndroidManifestInfo.ServiceInfo serviceInfo = new AndroidManifestInfo.ServiceInfo();
        serviceInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        serviceInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);
        return serviceInfo;
    }
}