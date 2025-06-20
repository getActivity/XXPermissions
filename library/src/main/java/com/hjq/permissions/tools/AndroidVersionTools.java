package com.hjq.permissions.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/01/22
 *    desc   : Android 版本工具
 */
@SuppressLint("AnnotateVersionCheck")
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public final class AndroidVersionTools {

    public static final int ANDROID_15 = Build.VERSION_CODES.VANILLA_ICE_CREAM;
    public static final int ANDROID_14 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    public static final int ANDROID_13 = Build.VERSION_CODES.TIRAMISU;
    public static final int ANDROID_12_L = Build.VERSION_CODES.S_V2;
    public static final int ANDROID_12 = Build.VERSION_CODES.S;
    public static final int ANDROID_11 = Build.VERSION_CODES.R;
    public static final int ANDROID_10 = Build.VERSION_CODES.Q;
    public static final int ANDROID_9 = Build.VERSION_CODES.P;
    public static final int ANDROID_8_1 = Build.VERSION_CODES.O_MR1;
    public static final int ANDROID_8 = Build.VERSION_CODES.O;
    public static final int ANDROID_7_1 = Build.VERSION_CODES.N_MR1;
    public static final int ANDROID_7 = Build.VERSION_CODES.N;
    public static final int ANDROID_6 = Build.VERSION_CODES.M;
    public static final int ANDROID_5_1 = Build.VERSION_CODES.LOLLIPOP_MR1;
    public static final int ANDROID_5 = Build.VERSION_CODES.LOLLIPOP;
    public static final int ANDROID_4_4 = Build.VERSION_CODES.KITKAT;
    public static final int ANDROID_4_3 = Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final int ANDROID_4_2 = Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final int ANDROID_4_1 = Build.VERSION_CODES.JELLY_BEAN;
    public static final int ANDROID_4_0 = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final int ANDROID_3_2 = Build.VERSION_CODES.HONEYCOMB_MR2;
    public static final int ANDROID_3_1 = Build.VERSION_CODES.HONEYCOMB_MR1;
    public static final int ANDROID_3_0 = Build.VERSION_CODES.HONEYCOMB;
    public static final int ANDROID_2_3_3 = Build.VERSION_CODES.GINGERBREAD_MR1;
    public static final int ANDROID_2_3 = Build.VERSION_CODES.GINGERBREAD;
    public static final int ANDROID_2_2 = Build.VERSION_CODES.FROYO;
    public static final int ANDROID_2_1 = Build.VERSION_CODES.ECLAIR_MR1;
    public static final int ANDROID_2_0_1 = Build.VERSION_CODES.ECLAIR_0_1;
    public static final int ANDROID_2_0 = Build.VERSION_CODES.ECLAIR;

    /**
     * 获取当前 AndroidSdk 版本码
     */
    public static int getCurrentAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取 TargetSdk 版本码
     */
    public static int getTargetSdkVersionCode(Context context) {
        return context.getApplicationInfo().targetSdkVersion;
    }

    /**
     * 判断当前环境是否需要适配特定 Android 版本新特性
     *
     * @param androidSdkVersionCode                        需要判断的 android sdk 版本码
     */
    public static boolean isAdaptationAndroidVersionNewFeatures(Context context, int androidSdkVersionCode) {
        return getCurrentAndroidVersionCode() >= androidSdkVersionCode && getTargetSdkVersionCode(context) >= androidSdkVersionCode;
    }

    /**
     * 是否是 Android 15 及以上版本
     */
    public static boolean isAndroid15() {
        return Build.VERSION.SDK_INT >= ANDROID_15;
    }

    /**
     * 是否是 Android 14 及以上版本
     */
    public static boolean isAndroid14() {
        return Build.VERSION.SDK_INT >= ANDROID_14;
    }

    /**
     * 是否是 Android 13 及以上版本
     */
    public static boolean isAndroid13() {
        return Build.VERSION.SDK_INT >= ANDROID_13;
    }

    /**
     * 是否是 Android 12 及以上版本
     */
    public static boolean isAndroid12() {
        return Build.VERSION.SDK_INT >= ANDROID_12;
    }

    /**
     * 是否是 Android 11 及以上版本
     */
    public static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= ANDROID_11;
    }

    /**
     * 是否是 Android 10 及以上版本
     */
    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= ANDROID_10;
    }

    /**
     * 是否是 Android 9.0 及以上版本
     */
    public static boolean isAndroid9() {
        return Build.VERSION.SDK_INT >= ANDROID_9;
    }

    /**
     * 是否是 Android 8.1 及以上版本
     */
    public static boolean isAndroid8_1() {
        return Build.VERSION.SDK_INT >= ANDROID_8_1;
    }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    public static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= ANDROID_8;
    }

    /**
     * 是否是 Android 7.1 及以上版本
     */
    public static boolean isAndroid7_1() {
        return Build.VERSION.SDK_INT >= ANDROID_7_1;
    }

    /**
     * 是否是 Android 7.0 及以上版本
     */
    public static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= ANDROID_7;
    }

    /**
     * 是否是 Android 6.0 及以上版本
     */
    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= ANDROID_6;
    }

    /**
     * 是否是 Android 5.1 及以上版本
     */
    public static boolean isAndroid5_1() {
        return Build.VERSION.SDK_INT >= ANDROID_5_1;
    }

    /**
     * 是否是 Android 5.0 及以上版本
     */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= ANDROID_5;
    }

    /**
     * 是否是 Android 4.4 及以上版本
     */
    public static boolean isAndroid4_4() {
        return Build.VERSION.SDK_INT >= ANDROID_4_4;
    }

    /**
     * 是否是 Android 4.3 及以上版本
     */
    public static boolean isAndroid4_3() {
        return Build.VERSION.SDK_INT >= ANDROID_4_3;
    }
}