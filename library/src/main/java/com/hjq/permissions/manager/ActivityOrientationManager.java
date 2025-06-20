package com.hjq.permissions.manager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.hjq.permissions.tools.AndroidVersionTools;
import java.util.HashMap;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Activity 屏幕方向管理类
 */
public final class ActivityOrientationManager {

    /** 存放 Activity 屏幕方向集合 */
    private static final Map<Integer, Integer> ACTIVITY_ORIENTATION_MAP = new HashMap<>();

    /** 私有化构造函数 */
    private ActivityOrientationManager() {}

    /**
     * 锁定 Activity 方向
     */
    public static synchronized void lockActivityOrientation(@NonNull Activity activity) {
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        int sourceScreenOrientation = activity.getRequestedOrientation();
        if (sourceScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }

        int targetScreenOrientation;
        // 锁定当前 Activity 方向
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            switch (activity.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    targetScreenOrientation = isActivityReverse(activity) ?
                                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE :
                                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    activity.setRequestedOrientation(targetScreenOrientation);
                    ACTIVITY_ORIENTATION_MAP.put(getIntKeyByActivity(activity), targetScreenOrientation);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    targetScreenOrientation = isActivityReverse(activity) ?
                                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT :
                                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    activity.setRequestedOrientation(targetScreenOrientation);
                    ACTIVITY_ORIENTATION_MAP.put(getIntKeyByActivity(activity), targetScreenOrientation);
                    break;
                default:
                    break;
            }
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace();
        }
    }

    /**
     * 解锁 Activity 方向
     */
    public static synchronized void unlockActivityOrientation(@NonNull Activity activity) {
        // 如果当前 Activity 没有锁定，就直接返回
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        Integer targetScreenOrientation = ACTIVITY_ORIENTATION_MAP.get(getIntKeyByActivity(activity));
        if (targetScreenOrientation == null) {
            return;
        }
        // 判断 Activity 之前是不是设置的屏幕自适应（这个判断可能永远为 false，但是为了代码的严谨性，还是要做一下判断）
        if (targetScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        // 为什么这里不用跟上面一样 try catch ？因为这里是把 Activity 方向取消固定，只有设置横屏或竖屏的时候才可能触发 crash
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * 判断 Activity 是否反方向旋转了
     */
    @SuppressWarnings("deprecation")
    private static boolean isActivityReverse(@NonNull Activity activity) {
        Display display = null;
        if (AndroidVersionTools.isAndroid11()) {
            display = activity.getDisplay();
        } else {
            WindowManager windowManager = activity.getWindowManager();
            if (windowManager != null) {
                display = windowManager.getDefaultDisplay();
            }
        }

        if (display == null) {
            return false;
        }

        // 获取 Activity 旋转的角度
        int activityRotation = display.getRotation();
        switch (activityRotation) {
            case Surface.ROTATION_180:
            case Surface.ROTATION_270:
                return true;
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
            default:
                return false;
        }
    }

    /**
     * 通过 Activity 获得一个 int 值的 key
     */
    private static int getIntKeyByActivity(@NonNull Activity activity) {
        // 这里取 Activity 的 hashCode 作为 key 值，这样就不会出现重复
        return activity.hashCode();
    }
}