package com.hjq.permissions.tools;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/08/12
 *    desc   : 厂商品牌判断
 */
public final class DeviceBrand {

    private static final String BRAND_NAME_HUAWEI = "HuaWei";
    private static final String[] BRAND_ID_HUAWEI = { "huawei" };

    private static final String BRAND_NAME_VIVO = "Vivo";
    private static final String[] BRAND_ID_VIVO = { "vivo" };

    private static final String BRAND_NAME_XIAOMI = "XiaoMi";
    private static final String[] BRAND_ID_XIAOMI = { "xiaomi" };

    private static final String BRAND_NAME_OPPO = "Oppo";
    private static final String[] BRAND_ID_OPPO = { "oppo" };

    private static final String BRAND_NAME_REALME = "RealMe";
    private static final String[] BRAND_ID_REALME = { "realme" };

    private static final String BRAND_NAME_LEECO  = "LeEco";
    private static final String[] BRAND_ID_LEECO  = { "leeco", "letv" };

    private static final String BRAND_NAME_360 = "360";
    private static final String[] BRAND_ID_360 = { "360", "qiku" };

    private static final String BRAND_NAME_ZTE = "ZTE";
    private static final String[] BRAND_ID_ZTE = { "zte" };

    private static final String BRAND_NAME_ONEPLUS = "OnePlus";
    private static final String[] BRAND_ID_ONEPLUS = { "oneplus" };

    private static final String BRAND_NAME_NUBIA = "Nubia";
    private static final String[] BRAND_ID_NUBIA = { "nubia" };

    private static final String BRAND_NAME_SAMSUNG = "Samsung";
    private static final String[] BRAND_ID_SAMSUNG = { "samsung" };

    private static final String BRAND_NAME_HONOR = "Honor";
    private static final String[] BRAND_ID_HONOR = { "honor" };

    private static final String BRAND_NAME_SMARTISAN = "Smartisan";
    private static final String[] BRAND_ID_SMARTISAN = { "smartisan", "deltainno" };

    private static final String BRAND_NAME_COOLPAD = "CoolPad";
    private static final String[] BRAND_ID_COOLPAD = { "coolpad", "yulong" };

    private static final String BRAND_NAME_LG = "LG";
    private static final String[] BRAND_ID_LG = { "lg", "lge" };

    private static final String BRAND_NAME_GOOGLE = "Google";
    private static final String[] BRAND_ID_GOOGLE = { "google" };

    private static final String BRAND_NAME_MEIZU = "MeiZu";
    private static final String[] BRAND_ID_MEIZU = { "meizu" };

    private static final String BRAND_NAME_LENOVO = "Lenovo";
    private static final String[] BRAND_ID_LENOVO = { "lenovo" };

    private static final String BRAND_NAME_HTC = "HTC";
    private static final String[] BRAND_ID_HTC = { "htc" };

    private static final String BRAND_NAME_SONY = "Sony";
    private static final String[] BRAND_ID_SONY = { "sony" };

    private static final String BRAND_NAME_GIONEE = "Gionee";
    private static final String[] BRAND_ID_GIONEE = { "gionee", "amigo" };

    private static final String BRAND_NAME_MOTOROLA = "Motorola";
    private static final String[] BRAND_ID_MOTOROLA = { "motorola" };

    private static final String BRAND_NAME_ASUS = "Asus";
    private static final String[] BRAND_ID_ASUS = { "asus" };

    private static final String BRAND_NAME_TRANSSION = "Transsion";
    private static final String[] BRAND_ID_TRANSSION = { "INFINIX MOBILITY LIMITED", "itel", "TECNO" };

    @NonNull
    private static final String CURRENT_BRAND_NAME;

    static {
        String brand = Build.BRAND.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        if (compareBrand(brand, manufacturer, BRAND_ID_HUAWEI)) {
            CURRENT_BRAND_NAME = BRAND_NAME_HUAWEI;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_VIVO)) {
            CURRENT_BRAND_NAME = BRAND_NAME_VIVO;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_XIAOMI)) {
            CURRENT_BRAND_NAME = BRAND_NAME_XIAOMI;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_OPPO)) {
            CURRENT_BRAND_NAME = BRAND_NAME_OPPO;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_REALME)) {
            CURRENT_BRAND_NAME = BRAND_NAME_REALME;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_LEECO)) {
            CURRENT_BRAND_NAME = BRAND_NAME_LEECO;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_360)) {
            CURRENT_BRAND_NAME = BRAND_NAME_360;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_ZTE)) {
            CURRENT_BRAND_NAME = BRAND_NAME_ZTE;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_ONEPLUS)) {
            CURRENT_BRAND_NAME = BRAND_NAME_ONEPLUS;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_NUBIA)) {
            CURRENT_BRAND_NAME = BRAND_NAME_NUBIA;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_SAMSUNG)) {
            CURRENT_BRAND_NAME = BRAND_NAME_SAMSUNG;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_HONOR)) {
            CURRENT_BRAND_NAME = BRAND_NAME_HONOR;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_SMARTISAN)) {
            CURRENT_BRAND_NAME = BRAND_NAME_SMARTISAN;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_COOLPAD)) {
            CURRENT_BRAND_NAME = BRAND_NAME_COOLPAD;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_LG)) {
            CURRENT_BRAND_NAME = BRAND_NAME_LG;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_GOOGLE)) {
            CURRENT_BRAND_NAME = BRAND_NAME_GOOGLE;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_MEIZU)) {
            CURRENT_BRAND_NAME = BRAND_NAME_MEIZU;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_LENOVO)) {
            CURRENT_BRAND_NAME = BRAND_NAME_LENOVO;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_HTC)) {
            CURRENT_BRAND_NAME = BRAND_NAME_HTC;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_SONY)) {
            CURRENT_BRAND_NAME = BRAND_NAME_SONY;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_GIONEE)) {
            CURRENT_BRAND_NAME = BRAND_NAME_GIONEE;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_MOTOROLA)) {
            CURRENT_BRAND_NAME = BRAND_NAME_MOTOROLA;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_ASUS)) {
            CURRENT_BRAND_NAME = BRAND_NAME_ASUS;
        } else if (compareBrand(brand, manufacturer, BRAND_ID_TRANSSION)) {
            CURRENT_BRAND_NAME = BRAND_NAME_TRANSSION;
        } else {
            if (!TextUtils.isEmpty(brand)) {
                CURRENT_BRAND_NAME = brand;
            } else if (!TextUtils.isEmpty(manufacturer)) {
                CURRENT_BRAND_NAME = manufacturer;
            } else {
                CURRENT_BRAND_NAME = "Unknown";
            }
        }
    }

    /**
     * 判断当前设备是否为华为
     */
    public static boolean isHuaWei() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_HUAWEI);
    }

    /**
     * 判断当前设备是否为荣耀
     */
    public static boolean isHonor() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_HONOR);
    }

    /**
     * 判断当前设备是否为 vivo
     */
    public static boolean isVivo() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_VIVO);
    }

    /**
     * 判断当前设备是否为小米
     */
    public static boolean isXiaoMi() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_XIAOMI);
    }

    /**
     * 判断当前设备是否为 oppo
     */
    public static boolean isOppo() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_OPPO);
    }

    /**
     * 判断当前设备是否为真我
     */
    public static boolean isRealMe() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_REALME);
    }

    /**
     * 判断当前设备是否为乐视
     */
    public static boolean isLeEco() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_LEECO);
    }

    /**
     * 判断当前设备是否为 360
     */
    public static boolean is360() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_360);
    }

    /**
     * 判断当前设备是否为中兴
     */
    public static boolean isZte() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_ZTE);
    }

    /**
     * 判断当前设备是否为一加
     */
    public static boolean isOnePlus() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_ONEPLUS);
    }

    /**
     * 判断当前设备是否为努比亚
     */
    public static boolean isNubia() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_NUBIA);
    }

    /**
     * 判断当前设备是否为酷派
     */
    public static boolean isCoolPad() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_COOLPAD);
    }

    /**
     * 判断当前设备是否为 LG
     */
    public static boolean isLg() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_LG);
    }

    /**
     * 判断当前设备是否为 Google
     */
    public static boolean isGoogle() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_GOOGLE);
    }

    /**
     * 判断当前设备是否为三星
     */
    public static boolean isSamsung() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_SAMSUNG);
    }

    /**
     * 判断当前设备是否为魅族
     */
    public static boolean isMeiZu() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_MEIZU);
    }

    /**
     * 判断当前设备是否为联想
     */
    public static boolean isLenovo() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_LENOVO);
    }

    /**
     * 判断当前设备是否为锤子
     */
    public static boolean isSmartisan() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_SMARTISAN);
    }

    /**
     * 判断当前设备是否为 HTC
     */
    public static boolean isHtc() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_HTC);
    }

    /**
     * 判断当前设备是否为索尼
     */
    public static boolean isSony() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_SONY);
    }

    /**
     * 判断当前设备是否为金立
     */
    public static boolean isGionee() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_GIONEE);
    }

    /**
     * 判断当前设备是否为摩托罗拉
     */
    public static boolean isMotorola() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_MOTOROLA);
    }

    /**
     * 判断当前设备是否为传音
     */
    public static boolean isTranssion() {
        return TextUtils.equals(CURRENT_BRAND_NAME, BRAND_NAME_TRANSSION);
    }

    /**
     * 获取当前设备品牌名称
     */
    public static String getBrandName() {
        return CURRENT_BRAND_NAME;
    }

    /**
     * 比较品牌或者制造商名称是否包含指定的名称
     */
    private static boolean compareBrand(String brand, String manufacturer, String... names) {
        for (String name : names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true;
            }
        }
        return false;
    }
}