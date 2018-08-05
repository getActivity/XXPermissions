/*
 * Copyright 2018 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hjq.permissions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by HJQ on 2018-8-5.
 */
public class OverlaySettingPage {

    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    static void start(Activity activity, int requestCode) {
        if (MARK.contains("meizu")) {
            if (!meizu(activity, requestCode) && !google(activity, requestCode)) {
                details(activity, requestCode);
            }
        } else if (!google(activity, requestCode)) {
            details(activity, requestCode);
        }
    }

    private static boolean meizu(Activity activity, int requestCode) {
        Intent overlayIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        overlayIntent.putExtra("packageName", activity.getPackageName());
        overlayIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        try {
            activity.startActivityForResult(overlayIntent, requestCode);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean google(Activity activity, int requestCode) {
        Intent manageIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        manageIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        try {
            activity.startActivityForResult(manageIntent, requestCode);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void details(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivityForResult(intent, requestCode);
    }
}