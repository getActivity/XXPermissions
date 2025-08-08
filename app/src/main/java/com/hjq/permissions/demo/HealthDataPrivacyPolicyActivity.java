package com.hjq.permissions.demo;

import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/28
 *    desc   : 健康数据隐私政策界面
 */
public class HealthDataPrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_data_privacy_policy_activity);

        TitleBar titleBar = findViewById(R.id.tb_health_data_privacy_policy_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {

            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }
        });

        // 适配 Android 15 EdgeToEdge 特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            titleBar.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener()  {

                @NonNull
                @Override
                public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                    Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                    // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    v.setPadding(0, systemBars.top, 0, 0);
                    return insets;
                }
            });
        }
    }
}