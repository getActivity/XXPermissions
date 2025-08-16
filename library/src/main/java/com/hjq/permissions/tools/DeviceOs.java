package com.hjq.permissions.tools;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/08/12
 *    desc   : 厂商系统判断
 */
public final class DeviceOs {

    /* ---------------------------------------- 我是一条华丽的分割线 ---------------------------------------- */

    static final String SYSTEM_PROPERTY_BUILD_VERSION_INCREMENTAL = "ro.build.version.incremental";
    static final String SYSTEM_PROPERTY_BUILD_DISPLAY_ID = "ro.build.display.id";

    /* ---------------------------------------- 我是一条华丽的分割线 ---------------------------------------- */

    static final String[] OS_VERSION_NAME_UNKNOWN = { SYSTEM_PROPERTY_BUILD_DISPLAY_ID,
                                                      SYSTEM_PROPERTY_BUILD_VERSION_INCREMENTAL };

    /* ---------------------------------------- 下面是小米或者红米的系统 ---------------------------------------- */

    /**
     * 国内版本：
     * [ro.miui.build.region]: [cn]
     * [ro.miui.region]: [CN]
     * [ro.vendor.miui.region]: [CN]
     *
     * 国际版本：
     * [ro.miui.build.region]: [global]
     * [ro.miui.region]: [HK]
     * [ro.vendor.miui.region]: [HK]
     */
    static final String[] OS_REGION_MI = { "ro.miui.build.region",
                                           "ro.miui.region",
                                           "ro.vendor.miui.region" };

    static final String OS_NAME_HYPER_OS = "HyperOS";
    /**
     * [ro.mi.os.version.incremental]: [OS1.0.3.0.TKXCNXM]
     */
    static final String OS_VERSION_NAME_HYPER_OS = "ro.mi.os.version.incremental";
    /**
     * [ro.mi.os.version.incremental]: [OS1.0.3.0.TKXCNXM]
     * [ro.mi.os.version.name]: [OS1.0]
     * [ro.mi.os.version.code]: [1]
     */
    static final String[] OS_CONDITIONS_HYPER_OS = { "ro.mi.os.version.name",
                                                     "ro.mi.os.version.code",
                                                     OS_VERSION_NAME_HYPER_OS };

    static final String[] OS_REGION_HYPER_OS = OS_REGION_MI;

    static final String OS_NAME_MIUI = "MIUI";

    /**
     * [ro.build.version.incremental]: [V9.6.1.0.MHOCNFD]
     * [ro.build.description]: [kenzo-user 6.0.1 MMB29M V9.6.1.0.MHOCNFD release-keys]
     * [ro.build.fingerprint]:[Xiaomi/kenzo/kenzo:6.0.1/MMB29M| V9.6.1.0.MH0cNFD:user/release-keys]
     * [ro.bootimage.build.fingerprint]: [Xiaomi/kenzo/kenzo:6.0.1/MMB29M/ V9.6.1.0.MHOCNFD:user/release-keys]
     */
    static final String OS_VERSION_NAME_MIUI = SYSTEM_PROPERTY_BUILD_VERSION_INCREMENTAL;

    /**
     * miui 9.6.1.0：[ro.miui.ui.version.name]: [V9]
     * miui 13.0.12：[ro.miui.ui.version.name]: [V130]
     *
     * miui 9.6.1：[ro.miui.ui.version.code]: [7]
     * miui 13.0.12：[ro.miui.ui.version.code]: [13]
     *
     * 如何识别小米设备/MIUI系统：https://dev.mi.com/console/doc/detail?pId=915
     */
    static final String[] OS_CONDITIONS_MIUI = { "ro.miui.ui.version.name",
                                                 "ro.miui.ui.version.code" };

    /**
     * 国内版本：
     * [ro.miui.region]: [CN]
     * [ro.vendor.miui.region]: [CN]
     * [ro.miui.build.region]: [cn]
     *
     * 国际版本：
     * [ro.miui.region]: [HK]
     * [ro.vendor.miui.region]: [HK]
     * [ro.miui.build.region]: [global]
     */
    static final String[] OS_REGION_MIUI = OS_REGION_MI;

    static final String OS_NAME_COLOR_OS = "ColorOS";
    static final String[] OS_VERSION_NAME_COLOR_OS = { "ro.build.version.opporom",
                                                       "ro.build.version.oplusrom.display" };

    /* ---------------------------------------- 下面是 VIVO 的系统 ---------------------------------------- */

    /**
     * [ro.vivo.os.build.display.id]: [OriginOS 4]
     * [ro.vivo.os.build.display.id]: [OriginOS 5]
     * [ro.vivo.os.build.display.id]:[Funtouch 0S_2.5]
     */
    static final String OS_CONDITIONS_VIVO_OS = "ro.vivo.os.build.display.id";

    static final String OS_NAME_ORIGIN_OS = "OriginOS";
    /**
     * [ro.vivo.os.build.display.id]: [OriginOS 4]
     * [ro.vivo.os.build.display.id]: [OriginOS 5]
     */
    static final String[] OS_VERSION_NAME_ORIGIN_OS = { OS_CONDITIONS_VIVO_OS };

    static final String OS_NAME_FUNTOUCH_OS = "FuntouchOS";
    // [ro.vivo.os.name]: [Funtouch]
    // 不要用 ro.vivo.os.name 属性判断是否为 FuntouchOs 系统，因为在 FuntouchOs 和 OriginOs 系统上面获取到的值是 Funtouch
    // static final String OS_CONDITIONS_FUNTOUCH_OS = "ro.vivo.os.name";
    /**
     * [ro.vivo.os.version]: [2.5]
     * [ro.vivo.rom.version]: [rom_2.5]
     * [ro.vivo.rom]: [rom_ 2.5]
     * [ro.vivo.os.build.display.id]: [Funtouch OS_2.5]
     */
    static final String[] OS_VERSION_NAME_FUNTOUCH_OS = { "ro.vivo.os.version",
                                                          "ro.vivo.rom.version",
                                                          "ro.vivo.rom",
                                                          OS_CONDITIONS_VIVO_OS };

    /* ---------------------------------------- 下面是真我的系统 ---------------------------------------- */

    static final String OS_NAME_REALME_UI = "RealmeUI";
    /**
     * [ro.build.version.realmeui]: [V5.0]
     */
    static final String OS_VERSION_NAME_REALME_UI = "ro.build.version.realmeui";

    /* ---------------------------------------- 下面是华为或者荣耀的系统 ---------------------------------------- */

    static final String OS_NAME_MAGIC_OS = "MagicOs";
    /**
     * 经过测试，得出以下结论
     * Magic 7.0 存放系统版本的属性是 msc.config.magic.version，
     * Magic 4.0 和 Magic 4.1 用的是 ro.build.version.magic 属性
     */
    static final String[] OS_VERSION_NAME_MAGIC_OS = { "msc.config.magic.version",
                                                       "ro.build.version.magic" };

    static final String OS_NAME_HARMONY_OS = "HarmonyOs";
    /**
     * [ro.huawei.build.display.id]: [NOH-AN00 2.0.0.165(C00E160R6P2)]
     * [ro.build.display.id]: [NOH-AN00 2.0.0.165(C00E160R6P2)]
     * [hwouc.hwpatch.version]: [2.0.0.165(C00E160R6P2patch04)]
     * [persist.mygote.build.id]: [NOH-AN00 2.0.0.165(C00E160R6P2)]
     * [persist.sys.hiview.base_version]: [NOH-LGRP1-CHN 2.0.0.165]
     * [ro.comp.hl.product_base_version]: [NOH-LGRP1-CHN 2.0.0.165]
     */
    static final String[] OS_VERSION_NAME_HARMONY_OS = { "hw_sc.build.platform.version",
                                                         "ro.huawei.build.display.id",
                                                         "hwouc.hwpatch.version",
                                                         "persist.mygote.build.id",
                                                         "persist.sys.hiview.base_version",
                                                         "ro.comp.hl.product_base_version" };

    static final String OS_NAME_EMUI = "EMUI";
    static final String OS_VERSION_NAME_EMUI = "ro.build.version.emui";

    /* ---------------------------------------- 下面是三星的系统 ---------------------------------------- */

    static final String OS_NAME_ONE_UI = "OneUI";

    /**
     * OneUi 高版本
     * OneUi 8.0：[ro.build.version.oneui]: [80000]
     * OneUi 7.0： [ro.build.version.oneui]: [70000]
     * OneUi 6.1：[ro.build.version.oneui]: [60101]
     * OneUi 5.1.1：[ro.build.version.oneui]: [50101]
     */
    static final String OS_VERSION_NAME_ONE_UI_NEW = "ro.build.version.oneui";
    /**
     * OneUi 低版本：https://github.com/the-ntf/xspstarterkit/blob/7d6fcce101edd35a5fe3c6df99c894f9570023a1/extlib/com.ibm.xsp.extlib.core/src/com/ibm/xsp/extlib/util/ThemeUtil.java#L61
     * [extlib.oneui.Version]: [oneuiv2]
     * [extlib.oneui.Version]: [oneuiv2.1]
     * [extlib.oneui.Version]: [oneuiv3]
     * [extlib.oneui.Version]: [oneuiv3.0.2]
     */
    static final String OS_VERSION_NAME_ONE_UI_OLD = "extlib.oneui.Version";

    /* ---------------------------------------- 下面是一加的系统 ---------------------------------------- */

    static final String OS_NAME_OXYGEN_OS = "OxygenOS";
    static final String OS_VERSION_NAME_OXYGEN_OS = "ro.oxygen.version";
    /**
     * Android 7.1.1：[ro.rom.version]: [H2OS V3.5]
     */
    static final String OS_NAME_H2_OS = "H2OS";
    static final String OS_VERSION_NAME_H2_OS = "ro.rom.version";

    /* ---------------------------------------- 下面是魅族的系统 ---------------------------------------- */

    static final String OS_NAME_FLYME = "Flyme";
    /**
     * Android 5.1 [ro.build.display.id]: [Flyme OS 5.1.3.0A]
     * Android 11 [ro.flyme.version.id]: [Flyme 9.3.1.0A]
     */
    static final String[] OS_VERSION_NAME_FLYME = { "ro.flyme.version.id",
                                                    SYSTEM_PROPERTY_BUILD_DISPLAY_ID };
    /**
     * [ro.flyme.published]: [true]
     * [ro.flyme.version.id]: [Flyme 9.3.1.0A]
     */
    static final String[] OS_CONDITIONS_FLYME = { "ro.flyme.published",
                                                  "ro.flyme.version.id" };

    /* ---------------------------------------- 下面是中兴或者努比亚的系统 ---------------------------------------- */

    /**
     * MyOs 系统返回：[ro.build.MiFavor_version]: [12]
     * MiFavor 系统返回：[ro.build.MiFavor_version]: [10.1]
     */
    static final String OS_VERSION_ZTE_OS = "ro.build.MiFavor_version" ;

    /**
     * [ro.vendor.mifavor.custom]: [home]
     * [ro.vendor.mifavor.mfvkeyguard.type]: [2]
     * [ro.vendor.mifavor.voicetotext]: [1]
     */
    static final String[] OS_CONDITIONS_ZTE_OS = { OS_VERSION_ZTE_OS,
                                                   "ro.vendor.mifavor.custom",
                                                   "ro.vendor.mifavor.mfvkeyguard.type",
                                                   "ro.vendor.mifavor.voicetotext" };

    static final String OS_NAME_MY_OS = "MyOS";
    /**
     * [ro.build.display.id]: [MyOS12.0.14_A2121]
     */
    static final String OS_VERSION_NAME_MY_OS = SYSTEM_PROPERTY_BUILD_DISPLAY_ID;

    static final String OS_NAME_MIFAVOR_UI = "MifavorUI";
    /**
     * Android 10 返回：[ro.build.MiFavor_version]: [10.1]
     *
     * 注意不能用 ro.build.display.id 获取，获取到的与实际不符合，获取到的值为：[ro.build.display.id]: [ZTE_A2021_PROV1.0.2B05]
     */
    static final String OS_VERSION_NAME_MIFAVOR_UI = OS_VERSION_ZTE_OS;

    /* ---------------------------------------- 下面是锤子的系统 ---------------------------------------- */

    static final String OS_NAME_SMARTISAN_OS = "SmartisanOS";
    static final String OS_VERSION_NAME_SMARTISAN_OS = "ro.smartisan.version";
    static final String[] OS_CONDITIONS_SMARTISAN_OS = { "ro.smartisan.sa",
                                                         OS_VERSION_NAME_SMARTISAN_OS };

    /* ---------------------------------------- 下面是乐视的系统 ---------------------------------------- */

    static final String OS_NAME_EUI_OS = "EUI";
    /**
     * [ro.letv.release.version]: [6.0.030S]
     */
    static final String OS_VERSION_NAME_EUI_OS = "ro.letv.release.version";
    /**
     * [persist.sys.leui.bootreason]: [0]
     * [ro.config.leui_ringtone_slot2]: [Default.ogg] [ro.leui_oem_unlock_enable]:[1]
     * [ro.letv.release.version_date]: [5.8.001D_09093]
     * [ro.product.letv_model]: [Le X620]
     * [ro.product.letv_name]：[乐2]
     * [sys.letv.fmodelaid]: [10120]
     */
    static final String[] OS_CONDITIONS_EUI_OS = { "persist.sys.leui.bootreason", "ro.config.leui_ringtone_slot2",
                                                   OS_VERSION_NAME_SMARTISAN_OS,
                                                   "ro.letv.release.version_date",
                                                   "ro.product.letv_model",
                                                   "ro.product.letv_name",
                                                   "sys.letv.fmodelaid" };

    /* ---------------------------------------- 下面是 360 的系统 ---------------------------------------- */

    static final String OS_NAME_360_UI = "360UI";
    /**
     * Android 8.0：[ro.build.uiversion]:[360UI:V3.0]
     */
    static final String OS_VERSION_NAME_360_UI = "ro.build.uiversion";

    @Nullable
    private static String sCurrentOsName;
    @Nullable
    private static String sCurrentOriginalOsVersionName;

    private DeviceOs() {
        // 私有化构造方法，禁止外部实例化
    }

    static {
        // 需要注意的是：该逻辑需要在判断 miui 系统之前判断，因为在 HyperOs 系统上面判断当前系统是否为 miui 系统也会返回 true
        // 这是因为 HyperOs 系统本身就是从 miui 系统演变而来，有这个问题也很正常，主要是厂商为了系统兼容性而保留的
        if (SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_HYPER_OS)) {
            sCurrentOsName = OS_NAME_HYPER_OS;
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_HYPER_OS);
        } else if (SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_MIUI)) {
            sCurrentOsName = OS_NAME_MIUI;
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_MIUI);
        }

        if (sCurrentOsName == null) {
            String vivoOsName = SystemPropertyCompat.getSystemPropertyValue(OS_CONDITIONS_VIVO_OS);
            if (!TextUtils.isEmpty(vivoOsName)) {
                if (vivoOsName.toLowerCase().contains("origin")) {
                    sCurrentOsName = OS_NAME_ORIGIN_OS;
                    sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_ORIGIN_OS);
                } else if (vivoOsName.toLowerCase().contains("funtouch")) {
                    sCurrentOsName = OS_NAME_FUNTOUCH_OS;
                    sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_FUNTOUCH_OS);
                }
            }
        }

        if (sCurrentOsName == null) {
            String colorOsVersion = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_COLOR_OS);
            if (!TextUtils.isEmpty(colorOsVersion)) {
                sCurrentOsName = OS_NAME_COLOR_OS;
                sCurrentOriginalOsVersionName = colorOsVersion;
            }
        }

        if (sCurrentOsName == null) {
            String realmeUiVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_REALME_UI);
            if (!TextUtils.isEmpty(realmeUiVersion)) {
                sCurrentOsName = OS_NAME_REALME_UI;
                sCurrentOriginalOsVersionName = realmeUiVersion;
            }
        }

        if (sCurrentOsName == null) {
            String magicOsVersion = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_MAGIC_OS);
            if (!TextUtils.isEmpty(magicOsVersion)) {
                sCurrentOsName = OS_NAME_MAGIC_OS;
                sCurrentOriginalOsVersionName = magicOsVersion;
            }
        }

        if (sCurrentOsName == null) {
            try {
                Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
                Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
                if ("Harmony".equalsIgnoreCase(String.valueOf(osBrand))) {
                    sCurrentOsName = OS_NAME_HARMONY_OS;
                    sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_HARMONY_OS);
                }
            } catch (ClassNotFoundException ignore) {
                // 如果是类找不到的问题，就不打印日志，否则会影响看 Logcat 的体验
                // 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/368
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        if (sCurrentOsName == null) {
            String emuiVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_EMUI);
            if (!TextUtils.isEmpty(emuiVersion)) {
                sCurrentOsName = OS_NAME_EMUI;
                sCurrentOriginalOsVersionName = emuiVersion;
            }
        }

        if (sCurrentOsName == null) {
            String oneUiVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_ONE_UI_NEW);
            if (!TextUtils.isEmpty(oneUiVersion)) {
                sCurrentOsName = OS_NAME_ONE_UI;
                try {
                    // OneUi 5.1.1 获取到的值是 50101 再经过一通计算得出 5.1.1
                    int oneUiVersionCode = Integer.parseInt(oneUiVersion);
                    sCurrentOriginalOsVersionName = getOneUiVersionNameByVersionCode(oneUiVersionCode);
                } catch (Exception e) {
                    sCurrentOriginalOsVersionName = oneUiVersion;
                }
            }
        }
        if (sCurrentOsName == null) {
            String oneUiVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_ONE_UI_OLD);
            if (!TextUtils.isEmpty(oneUiVersion)) {
                sCurrentOsName = OS_NAME_ONE_UI;
                sCurrentOriginalOsVersionName = oneUiVersion;
            }
        }
        if (sCurrentOsName == null) {
            try {
                Field semPlatformIntField = Build.VERSION.class.getDeclaredField("SEM_PLATFORM_INT");
                semPlatformIntField.setAccessible(true);
                int semPlatformVersion = semPlatformIntField.getInt(null);
                sCurrentOsName = OS_NAME_ONE_UI;
                int superfluousValue = 90000;
                if (semPlatformVersion >= superfluousValue) {
                    // https://stackoverflow.com/questions/60122037/how-can-i-detect-samsung-one-ui
                    // OneUi 7.0 获取到的值是 160000，160000 - 90000 = 70000，70000 再经过一通计算得出 7.0 的版本号
                    // OneUi 5.1.1 获取到的值是 140500，无法通过计算得出 5.1.1 的版本号，所以这种方法不是最佳的答案
                    int oneUiVersionCode = semPlatformVersion - superfluousValue;
                    sCurrentOriginalOsVersionName = getOneUiVersionNameByVersionCode(oneUiVersionCode);
                } else {
                    sCurrentOriginalOsVersionName = String.valueOf(semPlatformVersion);
                }
            } catch (Exception ignore) {
                // 走到这里来证明不是三星手机
                // default implementation ignored
            }
        }

        if (sCurrentOsName == null) {
            String oxygenOsVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_OXYGEN_OS);
            if (!TextUtils.isEmpty(oxygenOsVersion)) {
                sCurrentOsName = OS_NAME_OXYGEN_OS;
                sCurrentOriginalOsVersionName = oxygenOsVersion;
            }
        }

        if (sCurrentOsName == null) {
            String h2OsVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_H2_OS);
            if (!TextUtils.isEmpty(h2OsVersion) && h2OsVersion.contains("H2OS")) {
                sCurrentOsName = OS_NAME_H2_OS;
                sCurrentOriginalOsVersionName = h2OsVersion;
            }
        }

        if (sCurrentOsName == null && SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_FLYME)) {
            sCurrentOsName = OS_NAME_FLYME;
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_FLYME);
        }

        if (sCurrentOsName == null && SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_ZTE_OS)) {
            String myOsVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_MY_OS);
            if (!TextUtils.isEmpty(myOsVersion) && myOsVersion.toLowerCase().contains("myos")) {
                sCurrentOsName = OS_NAME_MY_OS;
                sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_MY_OS);
            } else {
                sCurrentOsName = OS_NAME_MIFAVOR_UI;
                sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_MIFAVOR_UI);
            }
        }

        if (sCurrentOsName == null && SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_SMARTISAN_OS)) {
            sCurrentOsName = OS_NAME_SMARTISAN_OS;
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_SMARTISAN_OS);
        }

        if (sCurrentOsName == null && SystemPropertyCompat.isSystemPropertyAnyOneExist(OS_CONDITIONS_EUI_OS)) {
            sCurrentOsName = OS_NAME_EUI_OS;
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_EUI_OS);
        }

        if (sCurrentOsName == null) {
            String osVersion = SystemPropertyCompat.getSystemPropertyValue(OS_VERSION_NAME_360_UI);
            if (!TextUtils.isEmpty(osVersion) && osVersion.toLowerCase().contains("360ui")) {
                sCurrentOsName = OS_NAME_360_UI;
                sCurrentOriginalOsVersionName = osVersion;
            }
        }

        if (TextUtils.isEmpty(sCurrentOsName)) {
            sCurrentOsName = "";
        }

        if (TextUtils.isEmpty(sCurrentOriginalOsVersionName)) {
            sCurrentOriginalOsVersionName = SystemPropertyCompat.getSystemPropertyAnyOneValue(OS_VERSION_NAME_UNKNOWN);
        }
    }

    /**
     * 判断当前厂商系统是否为澎湃系统（小米或者红米手机的系统）
     */
    public static boolean isHyperOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_HYPER_OS);
    }

    /**
     * 判断澎湃是否为国内版本
     */
    public static boolean isHyperOsChina() {
        if (!isHyperOs()) {
            return false;
        }
        String[] propertyValues = SystemPropertyCompat.getSystemPropertyValue(OS_REGION_HYPER_OS);
        for (String propertyValue : propertyValues) {
            if (propertyValue.equalsIgnoreCase("cn")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断澎湃是否为国际版本
     */
    public static boolean isHyperOsByGlobal() {
        if (!isHyperOs()) {
            return false;
        }
        String[] propertyValues = SystemPropertyCompat.getSystemPropertyValue(OS_REGION_HYPER_OS);
        for (String propertyValue : propertyValues) {
            if (propertyValue.equalsIgnoreCase("global")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前厂商系统是否为 miui（小米或者红米手机的系统）
     */
    public static boolean isMiui() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_MIUI);
    }

    /**
     * 判断 miui 是否为国内版本
     */
    public static boolean isMiuiByChina() {
        // https://github.com/getActivity/XXPermissions/issues/398#issuecomment-3181978796
        // https://xiaomi.eu/community/threads/how-to-enable-the-region-option-in-settings-for-eu-roms.56303/
        // https://github.com/search?q=+ro.miui.region+&type=code
        if (!isMiui()) {
            return false;
        }
        String[] propertyValues = SystemPropertyCompat.getSystemPropertyValue(OS_REGION_MIUI);
        for (String propertyValue : propertyValues) {
            if (propertyValue.equalsIgnoreCase("cn")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 miui 是否为国际版本
     */
    public static boolean isMiuiByGlobal() {
        // https://github.com/getActivity/XXPermissions/issues/398#issuecomment-3181978796
        // https://xiaomi.eu/community/threads/how-to-enable-the-region-option-in-settings-for-eu-roms.56303/
        // https://github.com/search?q=+ro.miui.region+&type=code
        if (!isMiui()) {
            return false;
        }
        String[] propertyValues = SystemPropertyCompat.getSystemPropertyValue(OS_REGION_MIUI);
        for (String propertyValue : propertyValues) {
            if (propertyValue.equalsIgnoreCase("global")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否开启了 Miui 优化选项
     */
    public static boolean isMiuiOptimization() {
        return isXiaoMiSystemOptimization();
    }

    /**
     * 判断是否开启了澎湃的系统优化选项
     */
    public static boolean isHyperOsOptimization() {
        return isXiaoMiSystemOptimization();
    }

    /**
     * 判断当前厂商系统是否为澎湃或者 MIUI
     */
    public static boolean isHyperOsOrMiui() {
        return isHyperOs() || isMiui();
    }

    /**
     * 判断是否开启了澎湃或者 MIUI 的系统优化选项
     */
    public static boolean isHyperOsOrMiuiOptimization() {
        return isXiaoMiSystemOptimization();
    }

    /**
     * 判断小米手机是否开启了系统优化（默认开启）
     *
     * Miui 关闭步骤为：开发者选项-> 启动 MIUI 优化 -> 点击关闭
     * 澎湃的关闭步骤为：开发者选项-> 启用系统优化 -> 点击关闭
     *
     * 需要注意的是，关闭优化后，可以跳转到小米定制的权限请求页面，但是开启权限仍然是没有效果的
     * 另外关于 miui 国际版开发者选项中是没有优化选项的，但是代码判断是有开启优化选项，也就是默认开启，这样是正确的
     * 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/38
     */
    @SuppressLint("PrivateApi")
    private static boolean isXiaoMiSystemOptimization() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method getMethod = clazz.getMethod("get", String.class, String.class);
            String ctsValue = String.valueOf(getMethod.invoke(clazz, "ro.miui.cts", ""));
            Method getBooleanMethod = clazz.getMethod("getBoolean", String.class, boolean.class);
            return Boolean.parseBoolean(
                String.valueOf(getBooleanMethod.invoke(clazz, "persist.sys.miui_optimization", !"1".equals(ctsValue))));
        } catch (Exception ignored) {
            // default implementation ignored
        }
        return true;
    }

    /**
     * 判断当前厂商系统是否为 ColorOS（ OPPO 手机的系统））
     */
    public static boolean isColorOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_COLOR_OS);
    }

    /**
     * 判断当前厂商系统是否为 OriginOS（ vivo 手机的系统）
     */
    public static boolean isOriginOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_ORIGIN_OS);
    }

    /**
     * 判断当前厂商系统是否为 FuntouchOS（（vivo 老手机的系统））
     */
    public static boolean isFuntouchOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_FUNTOUCH_OS);
    }

    /**
     * 判断当前是否为 RealmeUI（真我手机的系统）
     */
    public static boolean isRealmeUi() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_REALME_UI);
    }

    /**
     * 判断当前是否为 MagicOs（荣耀手机的系统）
     */
    public static boolean isMagicOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_MAGIC_OS);
    }

    /**
     * 判断当前是否为鸿蒙系统（华为手机的系统）
     */
    public static boolean isHarmonyOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_HARMONY_OS);
    }

    /**
     * 判断当前厂商系统是否为 EMUI（华为老手机的系统）
     */
    public static boolean isEmui() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_EMUI);
    }

    /**
     * 判断当前厂商系统是否为 OneUI（三星手机的系统）
     */
    public static boolean isOneUi() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_ONE_UI);
    }

    /**
     * 判断当前厂商系统是否为 OxygenOS（一加手机的系统）
     */
    public static boolean isOxygenOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_OXYGEN_OS);
    }

    /**
     * 判断当前厂商系统是否为 H2OS（一加老手机的系统）
     */
    public static boolean isH2Os() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_H2_OS);
    }

    /**
     * 判断当前厂商系统是否为 Flyme（魅族手机的系统）
     */
    public static boolean isFlyme() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_FLYME);
    }

    /**
     * 判断当前厂商系统是否为 MyOS（中兴或者努比亚手机的系统）
     */
    public static boolean isMyOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_MY_OS);
    }

    /**
     * 判断当前厂商系统是否为 MifavorUI（中兴老手机的系统）
     */
    public static boolean isMifavorUi() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_MIFAVOR_UI);
    }

    /**
     * 判断当前厂商系统是否为 SmartisanOS（锤子手机的系统）
     */
    public static boolean isSmartisanOs() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_SMARTISAN_OS);
    }

    /**
     * 判断当前厂商系统是否为 EUI（乐视手机的系统）
     */
    public static boolean isEui() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_EUI_OS);
    }

    /**
     * 判断当前厂商系统是否为 360UI（360 手机的系统）
     */
    public static boolean is360Ui() {
        return PermissionUtils.equalsString(sCurrentOsName, OS_NAME_360_UI);
    }

    @NonNull
    public static String getOsName() {
        return sCurrentOsName != null ? sCurrentOsName : "";
    }

    /**
     * 获取厂商系统版本的大版本号
     *
     * @return               如果获取不到则返回 0
     */
    public static int getOsBigVersionCode() {
        String osVersionName = getOsVersionName();
        if (TextUtils.isEmpty(osVersionName)) {
            return 0;
        }
        String[] array = osVersionName.split("\\.");
        if (array.length == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(array[0]);
        } catch (Exception e) {
            // java.lang.NumberFormatException: Invalid int: "0 "
            return 0;
        }
    }

    /**
     * 返回经过美化的厂商系统版本号
     */
    @NonNull
    public static String getOsVersionName() {
        String originalOsVersionName = getOriginalOsVersionName();
        // 使用正则表达式匹配数字和点号组成的版本号
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)+)");
        Matcher matcher = pattern.matcher(originalOsVersionName);

        if (matcher.find()) {
            String result = matcher.group(1);
            return result != null ? result : "";
        }

        // 需要注意的是 华为畅享 5S Android 5.1 获取到的厂商版本号是 EmotionUI 3，而不是 3.1 或者 3.0 这种
        // 使用正则表达式匹配数字和点号组成的版本号
        pattern = Pattern.compile("(\\d+)");
        matcher = pattern.matcher(originalOsVersionName);

        if (matcher.find()) {
            String result = matcher.group(1);
            return result != null ? result : "";
        }
        return "";
    }

    /**
     * 返回原始的厂商系统版本号
     */
    @NonNull
    public static String getOriginalOsVersionName() {
        return sCurrentOriginalOsVersionName != null ? sCurrentOriginalOsVersionName : "";
    }

    /**
     * 根据 OneUi 的版本号计算出来 OneUi 的版本号
     */
    @NonNull
    private static String getOneUiVersionNameByVersionCode(int oneUiVersionCode) {
        // OneUi 8.0：[ro.build.version.oneui]: [80000]
        // OneUi 7.0： [ro.build.version.oneui]: [70000]
        // OneUi 6.1：[ro.build.version.oneui]: [60101]
        // OneUi 5.1.1：[ro.build.version.oneui]: [50101]
        int oneVersion = oneUiVersionCode / 10000;
        int twoVersion = oneUiVersionCode % 10000;
        int threeVersion = oneUiVersionCode % 100;
        if (threeVersion > 0) {
            // OneUi 5.1.1 的版本号是 50101，计算出来的结果是 5.1.1
            // OneUi 6.1 的版本号是 60101，计算出来的结果是 6.1.1，虽然不太准但也是没有办法
            return oneVersion + "." + (twoVersion / 100) + "." + threeVersion;
        } else {
            // OneUi 8.0 的版本号是 80000，计算出来的结果是 8.0
            return oneVersion + "." + (twoVersion / 100);
        }
    }
}