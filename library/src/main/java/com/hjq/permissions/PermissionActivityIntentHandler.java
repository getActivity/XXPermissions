package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/04/05
 *    desc   : 权限页意图处理器
 */
final class PermissionActivityIntentHandler {

    /** 存取子意图所用的 Intent Key */
    private static final String SUB_INTENT_KEY = "sub_intent_key";

    /**
     * 从父意图中获取子意图
     */
    private static Intent findSubIntentBySuperIntent(@NonNull Intent superIntent) {
        Intent subIntent;
        if (AndroidVersion.isAndroid13()) {
            subIntent = superIntent.getParcelableExtra(SUB_INTENT_KEY, Intent.class);
        } else {
            subIntent = superIntent.getParcelableExtra(SUB_INTENT_KEY);
        }
        return subIntent;
    }

    /**
     * 获取意图中最深层的子意图
     */
    private static Intent findDeepIntent(@NonNull Intent intent) {
        Intent subIntent = findSubIntentBySuperIntent(intent);
        if (subIntent != null) {
            return findDeepIntent(subIntent);
        }
        return intent;
    }

    /**
     * 将子意图添加到主意图中
     */
    static Intent addSubIntentForMainIntent(@Nullable Intent mainIntent, @Nullable Intent subIntent) {
        if (mainIntent == null && subIntent != null) {
            return subIntent;
        }
        if (subIntent == null) {
            return mainIntent;
        }
        Intent deepSubIntent = findDeepIntent(mainIntent);
        deepSubIntent.putExtra(SUB_INTENT_KEY, subIntent);
        return mainIntent;
    }

    static boolean startActivity(@NonNull Context context, Intent intent) {
        return startActivity(new StartActivityDelegateContextImpl(context), intent);
    }

    static boolean startActivity(@NonNull Activity activity, Intent intent) {
        return startActivity(new StartActivityDelegateActivityImpl(activity), intent);
    }

    static boolean startActivity(@NonNull Fragment fragment, Intent intent) {
        return startActivity(new StartActivityDelegateFragmentImpl(fragment), intent);
    }

    static boolean startActivity(@NonNull android.support.v4.app.Fragment fragment, Intent intent) {
        return startActivity(new StartActivityDelegateSupportFragmentImpl(fragment), intent);
    }

    static boolean startActivity(@NonNull StartActivityDelegate delegate, @NonNull Intent intent) {
        try {
            delegate.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Intent subIntent = findSubIntentBySuperIntent(intent);
            if (subIntent == null) {
                return false;
            }
            return startActivity(delegate, subIntent);
        }
    }

    static boolean startActivityForResult(@NonNull Activity activity, @NonNull Intent intent, int requestCode) {
        return startActivityForResult(new StartActivityDelegateActivityImpl(activity), intent, requestCode);
    }

    static boolean startActivityForResult(@NonNull Fragment fragment, @NonNull Intent intent, int requestCode) {
        return startActivityForResult(new StartActivityDelegateFragmentImpl(fragment), intent, requestCode);
    }

    static boolean startActivityForResult(@NonNull android.support.v4.app.Fragment fragment, @NonNull Intent intent, int requestCode) {
        return startActivityForResult(new StartActivityDelegateSupportFragmentImpl(fragment), intent, requestCode);
    }

    static boolean startActivityForResult(@NonNull StartActivityDelegate delegate, @NonNull Intent intent, int requestCode) {
        try {
            delegate.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Intent subIntent = findSubIntentBySuperIntent(intent);
            if (subIntent == null) {
                return false;
            }
            return startActivityForResult(delegate, subIntent, requestCode);
        }
    }

    private interface StartActivityDelegate {

        void startActivity(@NonNull Intent intent);

        void startActivityForResult(@NonNull Intent intent, int requestCode);
    }

    private static class StartActivityDelegateContextImpl implements StartActivityDelegate {

        private final Context mContext;

        private StartActivityDelegateContextImpl(@NonNull Context context) {
            mContext = context;
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            mContext.startActivity(intent);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            Activity activity = PermissionUtils.findActivity(mContext);
            if (activity != null) {
                activity.startActivityForResult(intent, requestCode);
                return;
            }
            startActivity(intent);
        }
    }

    private static class StartActivityDelegateActivityImpl implements StartActivityDelegate {

        private final Activity mActivity;

        private StartActivityDelegateActivityImpl(@NonNull Activity activity) {
            mActivity = activity;
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            mActivity.startActivity(intent);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            mActivity.startActivityForResult(intent, requestCode);
        }
    }

    private static class StartActivityDelegateFragmentImpl implements StartActivityDelegate {

        private final Fragment mFragment;

        private StartActivityDelegateFragmentImpl(@NonNull Fragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            mFragment.startActivity(intent);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            mFragment.startActivityForResult(intent, requestCode);
        }
    }

    private static class StartActivityDelegateSupportFragmentImpl implements StartActivityDelegate {

        private final android.support.v4.app.Fragment mFragment;

        private StartActivityDelegateSupportFragmentImpl(@NonNull android.support.v4.app.Fragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            mFragment.startActivity(intent);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            mFragment.startActivityForResult(intent, requestCode);
        }
    }
}