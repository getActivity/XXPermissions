package com.hjq.permissions;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 厂商 Rom 工具类
 */
final class PhoneRomUtils {

   private static final String[] ROM_HUAWEI    = {"huawei"};
   private static final String[] ROM_VIVO      = {"vivo"};
   private static final String[] ROM_XIAOMI    = {"xiaomi"};
   private static final String[] ROM_OPPO      = {"oppo"};
   private static final String[] ROM_LEECO     = {"leeco", "letv"};
   private static final String[] ROM_360       = {"360", "qiku"};
   private static final String[] ROM_ZTE       = {"zte"};
   private static final String[] ROM_ONEPLUS   = {"oneplus"};
   private static final String[] ROM_NUBIA     = {"nubia"};
   private static final String[] ROM_SAMSUNG = {"samsung"};
   private static final String[] ROM_HONOR = {"honor"};

   private static final String ROM_NAME_MIUI = "ro.miui.ui.version.name";

   private static final String VERSION_PROPERTY_HUAWEI  = "ro.build.version.emui";
   private static final String VERSION_PROPERTY_VIVO    = "ro.vivo.os.build.display.id";
   private static final String VERSION_PROPERTY_XIAOMI  = "ro.build.version.incremental";
   private static final String[] VERSION_PROPERTY_OPPO  = {"ro.build.version.opporom", "ro.build.version.oplusrom.display"};
   private static final String VERSION_PROPERTY_LEECO   = "ro.letv.release.version";
   private static final String VERSION_PROPERTY_360     = "ro.build.uiversion";
   private static final String VERSION_PROPERTY_ZTE     = "ro.build.MiFavor_version";
   private static final String VERSION_PROPERTY_ONEPLUS = "ro.rom.version";
   private static final String VERSION_PROPERTY_NUBIA   = "ro.build.rom.id";
   /**
    * 经过测试，得出以下结论
    * Magic 7.0 存放系统版本的属性是 msc.config.magic.version，
    * Magic 4.0 和 Magic 4.1 用的是 ro.build.version.magic 属性
    */
   private static final String[] VERSION_PROPERTY_MAGIC = {"msc.config.magic.version", "ro.build.version.magic"};

   private PhoneRomUtils() {}

   /**
    * 判断当前厂商系统是否为 emui
    */
   static boolean isEmui() {
      return !TextUtils.isEmpty(getPropertyName(VERSION_PROPERTY_HUAWEI));
   }

   /**
    * 判断当前厂商系统是否为 miui
    */
   static boolean isMiui() {
      return !TextUtils.isEmpty(getPropertyName(ROM_NAME_MIUI));
   }

   /**
    * 判断当前厂商系统是否为 ColorOs
    */
   static boolean isColorOs() {
      for (String property : VERSION_PROPERTY_OPPO) {
         String versionName = getPropertyName(property);
         if (TextUtils.isEmpty(versionName)) {
            continue;
         }
         return true;
      }
      return false;
   }

   /**
    * 判断当前厂商系统是否为 OriginOS
    */
   static boolean isOriginOs() {
      return !TextUtils.isEmpty(getPropertyName(VERSION_PROPERTY_VIVO));
   }

   /**
    * 判断当前厂商系统是否为 OneUI
    */
   @SuppressLint("PrivateApi")
   static boolean isOneUi() {
      return isRightRom(getBrand(), getManufacturer(), ROM_SAMSUNG);
      // 暂时无法通过下面的方式判断是否为 OneUI，只能通过品牌和机型来判断
      // https://stackoverflow.com/questions/60122037/how-can-i-detect-samsung-one-ui
//      try {
//         Field semPlatformIntField = Build.VERSION.class.getDeclaredField("SEM_PLATFORM_INT");
//         semPlatformIntField.setAccessible(true);
//         int semPlatformVersion = semPlatformIntField.getInt(null);
//         return semPlatformVersion >= 100000;
//      } catch (NoSuchFieldException  e) {
//         e.printStackTrace();
//         return false;
//      } catch (IllegalAccessException e) {
//         e.printStackTrace();
//         return false;
//      }
   }

   /**
    * 判断当前是否为鸿蒙系统
    */
   static boolean isHarmonyOs() {
      // 鸿蒙系统没有 Android 10 以下的
      if (!AndroidVersion.isAndroid10()) {
          return false;
      }
      try {
         Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
         Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
         return "Harmony".equalsIgnoreCase(String.valueOf(osBrand));
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return false;
      }
   }

    /**
     * 判断当前是否为 MagicOs 系统（荣耀）
     */
   static boolean isMagicOs() {
       return isRightRom(getBrand(), getManufacturer(), ROM_HONOR);
   }

   /**
    * 判断 miui 优化开关（默认开启，关闭步骤为：开发者选项-> 启动 MIUI 优化 -> 点击关闭）
    * 需要注意的是，关闭 miui 优化后，可以跳转到小米定制的权限请求页面，但是开启权限仍然是没有效果的
    * 另外关于 miui 国际版开发者选项中是没有 miui 优化选项的，但是代码判断是有开启 miui 优化，也就是默认开启，这样是正确的
    * 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/38
    */
   @SuppressLint("PrivateApi")
   static boolean isMiuiOptimization() {
      try {
         Class<?> clazz = Class.forName("android.os.SystemProperties");
         Method getMethod = clazz.getMethod("get", String.class, String.class);
         String ctsValue = String.valueOf(getMethod.invoke(clazz, "ro.miui.cts", ""));
         Method getBooleanMethod = clazz.getMethod("getBoolean", String.class, boolean.class);
         return Boolean.parseBoolean(String.valueOf(getBooleanMethod.invoke(clazz, "persist.sys.miui_optimization", !"1".equals(ctsValue))));
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
      return true;
   }

   /**
    * 返回厂商系统版本号
    */
   @Nullable
   static String getRomVersionName() {
      final String brand = getBrand();
      final String manufacturer = getManufacturer();
      if (isRightRom(brand, manufacturer, ROM_HUAWEI)) {
         String version = getPropertyName(VERSION_PROPERTY_HUAWEI);
         String[] temp = version.split("_");
         if (temp.length > 1) {
            return temp[1];
         } else {
            // 需要注意的是 华为畅享 5S Android 5.1 获取到的厂商版本号是 EmotionUI 3，而不是 3.1 或者 3.0 这种
            if (version.contains("EmotionUI")) {
               return version.replaceFirst("EmotionUI\\s*", "");
            }
            return version;
         }
      }
      if (isRightRom(brand, manufacturer, ROM_VIVO)) {
         // 需要注意的是 vivo iQOO 9 Pro Android 12 获取到的厂商版本号是 OriginOS Ocean
         return getPropertyName(VERSION_PROPERTY_VIVO);
      }
      if (isRightRom(brand, manufacturer, ROM_XIAOMI)) {
         return getPropertyName(VERSION_PROPERTY_XIAOMI);
      }
      if (isRightRom(brand, manufacturer, ROM_OPPO)) {
         for (String property : VERSION_PROPERTY_OPPO) {
            String versionName = getPropertyName(property);
            if (TextUtils.isEmpty(property)) {
               continue;
            }
            return versionName;
         }
         return "";
      }
      if (isRightRom(brand, manufacturer, ROM_LEECO)) {
         return getPropertyName(VERSION_PROPERTY_LEECO);
      }

      if (isRightRom(brand, manufacturer, ROM_360)) {
         return getPropertyName(VERSION_PROPERTY_360);
      }
      if (isRightRom(brand, manufacturer, ROM_ZTE)) {
         return getPropertyName(VERSION_PROPERTY_ZTE);
      }
      if (isRightRom(brand, manufacturer, ROM_ONEPLUS)) {
         return getPropertyName(VERSION_PROPERTY_ONEPLUS);
      }
      if (isRightRom(brand, manufacturer, ROM_NUBIA)) {
         return getPropertyName(VERSION_PROPERTY_NUBIA);
      }
       if (isRightRom(brand, manufacturer, ROM_HONOR)) {
           for (String property : VERSION_PROPERTY_MAGIC) {
               String versionName = getPropertyName(property);
               if (TextUtils.isEmpty(property)) {
                   continue;
               }
               return versionName;
           }
           return "";
       }

      return getPropertyName("");
   }

   private static boolean isRightRom(final String brand, final String manufacturer, final String... names) {
      for (String name : names) {
         if (brand.contains(name) || manufacturer.contains(name)) {
            return true;
         }
      }
      return false;
   }

   private static String getBrand() {
      return Build.BRAND.toLowerCase();
   }

   private static String getManufacturer() {
      return Build.MANUFACTURER.toLowerCase();
   }

   private static String getPropertyName(final String propertyName) {
      String result = "";
      if (!TextUtils.isEmpty(propertyName)) {
         result = getSystemProperty(propertyName);
      }
      return result;
   }

   private static String getSystemProperty(final String name) {
      String prop = getSystemPropertyByShell(name);
      if (!TextUtils.isEmpty(prop)) {
         return prop;
      }
      prop = getSystemPropertyByStream(name);
      if (!TextUtils.isEmpty(prop)) {
         return prop;
      }
      if (Build.VERSION.SDK_INT < 28) {
         return getSystemPropertyByReflect(name);
      }
      return prop;
   }

   private static String getSystemPropertyByShell(final String propName) {
      BufferedReader input = null;
      try {
         Process p = Runtime.getRuntime().exec("getprop " + propName);
         input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
         String ret = input.readLine();
         if (ret != null) {
            return ret;
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      return "";
   }

   private static String getSystemPropertyByStream(final String key) {
      try {
         Properties prop = new Properties();
         FileInputStream is = new FileInputStream(
                 new File(Environment.getRootDirectory(), "build.prop")
         );
         prop.load(is);
         return prop.getProperty(key, "");
      } catch (FileNotFoundException e) {
         // java.io.FileNotFoundException: /system/build.prop (Permission denied)
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "";
   }

   @SuppressLint("PrivateApi")
   private static String getSystemPropertyByReflect(String key) {
      try {
         Class<?> clz = Class.forName("android.os.SystemProperties");
         Method getMethod = clz.getMethod("get", String.class, String.class);
         return (String) getMethod.invoke(clz, key, "");
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
      return "";
   }
}