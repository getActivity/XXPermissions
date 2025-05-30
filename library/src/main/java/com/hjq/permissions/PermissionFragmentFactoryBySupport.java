package com.hjq.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限 Fragment 生产工厂（Support 包下的 Fragment）
 */
final class PermissionFragmentFactoryBySupport extends PermissionFragmentFactory<FragmentActivity, FragmentManager> {

    PermissionFragmentFactoryBySupport(@NonNull FragmentActivity activity, @NonNull FragmentManager fragmentManager) {
        super(activity, fragmentManager);
    }

    @Override
    void createAndCommitFragment(@NonNull List<String> permissions, @NonNull PermissionType permissionType, @Nullable OnPermissionFlowCallback callback) {
        IFragmentMethod<FragmentActivity, FragmentManager> fragment;
        if (permissionType == PermissionType.SPECIAL) {
            fragment = new PermissionFragmentSupportBySpecial();
        } else {
            fragment = new PermissionFragmentSupportByDangerous();
        }
        // 新版本的 Support 库限制请求码必须小于 65536（不能包含 65536），所以实际的取值区间在：1 ~ 65535
        // java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
        // 旧版本的 Support 库限制请求码必须小于 256（不能包含 256），所以实际的取值区间在：1 ~ 255
        // java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
        // 相关问题地址：
        // 1. https://stackoverflow.com/questions/33331073/android-what-to-choose-for-requestcode-values
        // 2. https://github.com/domoticz/domoticz-android/issues/92
        // 3. https://github.com/journeyapps/zxing-android-embedded/issues/117
        int maxRequestCode;
        // 判断当前是不是申请的危险权限
        if (permissionType == PermissionType.DANGEROUS) {
            try {
                FragmentActivity activity = getActivity();
                // 检查一下大值的 requestCode 会不会超过 FragmentActivity 类中的设定
                // 如果是，则证明当前的 Support 的版本是比较旧的，如果不是，则证明当前的 Support 的版本不是很旧
                // 因为新版的 Support 版本已经纠错了这个问题，将 requestCode 最大值限制从 255 已经调整到了 65535
                // 相关 Commit 地址：
                // Github：https://github.com/androidx/androidx/commit/86f3b80ddf7f9aa5c5b7afe77217cb75632d62a2
                // Google Git：https://android.googlesource.com/platform/frameworks/support/+/86f3b80ddf7f9aa5c5b7afe77217cb75632d62a2
                activity.validateRequestPermissionsRequestCode(PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE);
                // 如果能安全走完 validateRequestPermissionsRequestCode 的调用，则证明了对传入 65535 的值是在限制范围内或者是没有限制的
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
            } catch (IllegalArgumentException ignore) {
                // 当 requestCode 的值超过了 FragmentActivity 类中的设定，会触发此异常报错，这里进行了捕获
                // 则证明了对传入的 requestCode 的值是有限制的，并且只能用小一点的值，处理方案是将 maxRequestCode 换成小一点的值
                // java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_LOW_VALUE;
            } catch (Exception ignore) {
                // 如果是其他的异常报错，很有可能是 validateRequestPermissionsRequestCode 这个 API 从 FragmentActivity 删除了
                // 证明了 FragmentActivity 对传入的 requestCode 的值没有进行限制，所以 maxRequestCode 换成大一点的值
                // 但是上面说的没有限制，并不是真的没有限制，而是 FragmentActivity 中没有限制，但是 Activity 本身就是有限制的
                // Activity 的 requestPermissions 传入的 requestCode 参数都不能超过 65535，否则会无法申请权限
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
            }
        } else {
            // 如果是特殊权限则没有这个限制，因为特殊权限是通过 startActivityForResult 实现的
            // 新旧 Support 版本的 FragmentActivity 源码都对 startActivityForResult 传入的 requestCode 值没有限制
            // 但是上面说的没有限制，并不是真的没有限制，而是 FragmentActivity 中没有限制，但是 Activity 本身就是有限制的
            // Activity 的 startActivityForResult 传入的 requestCode 参数 requestCode 不能超过 65535，否则会无法进行页面跳转
            maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
        }
        int requestCode = PermissionRequestCodeManager.generateRandomRequestCode(maxRequestCode);
        fragment.setArguments(generatePermissionArguments(permissions, requestCode));
        fragment.setRetainInstance(true);
        fragment.setRequestFlag(true);
        fragment.setCallback(callback);
        fragment.commitAttach(getFragmentManager());
    }
}