package com.hjq.permissions.manager;

import android.support.annotation.IntRange;
import com.hjq.permissions.XXPermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : 权限请求码管理类
 */
public final class PermissionRequestCodeManager {

    /** 请求码限制：低值 */
    public static final int REQUEST_CODE_LIMIT_LOW_VALUE = 255;

    /** 请求码限制：高值 */
    public static final int REQUEST_CODE_LIMIT_HIGH_VALUE = 65535;

    /** 权限请求码存放集合 */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /** 随机数对象 */
    private static final Random RANDOM = new Random();

    /** 私有化构造函数 */
    private PermissionRequestCodeManager() {}

    /**
     * 随机生成一个请求码
     */
    @IntRange(from = 1, to = 65535)
    public static synchronized int generateRandomRequestCode(@IntRange(from = 1, to = 65535) int maxRequestCode) {
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        // 1. 请求码不能为 0 和负数
        // 2. 请求码不能等于 XXPermissions.REQUEST_CODE
        // 3. 尽量避免和当前项目的请求码出现冲突，所以需要抛弃小值的请求码，经过测试，发现以下问题：
        //    a. 使用 App 包下的 Fragment 进行权限申请，不会触发宿主 Activity 回调 onActivityResult 和 onRequestPermissionsResult
        //    b. 使用 Support 包下的 Fragment 进行权限申请，会触发宿主 Activity 回调 onActivityResult 和 onRequestPermissionsResult
        //    这是因为 Support 包 Fragment 权限相关的回调是通过重写 Activity 类的 onActivityResult 和 onRequestPermissionsResult 实现的
        //    而 App 包下 Fragment 的 onActivityResult 和 onRequestPermissionsResult 回调是直接在 Activity 类中的 dispatchActivityResult 中实现的
        do {
            // maxRequestCode 目前只有两种值，255 和 65535
            // 1. 如果外层传入的是 255（可能性较低），那么请求码的取值范围为：(255 / 2 + 1) ~ (255 - 1) = 128 ~ 254
            // 2. 如果外层传入的是 65535（可能性较大），那么请求码的取值范围为：(65535 - 10000 + 1) ~ (65535 - 1) = 55536 ~ 65534
            // 代码已经写得那么严谨了，但是仍然可能会出现请求码冲突的问题，虽然概率极低，但是这个时候就只能寄希望给外层的开发者，不要把请求码设定得那么大
            // 否则会和框架的请求码相冲突，但是出现这种情况的可能性比较低，外层的开发者如果把请求码定太大，会发现调用 startActivityForResult 没反应
            // 所以基于这个原因，一般设定大值请求码的可能性会比较小，退一万步讲，就算有这种情况，框架会随机从将近一万个数中选择一个，简称万里挑一
            // 就算最终出现了问题，因为数量占比会很少，加上不是必现（因为是通过随机数生成的），这个问题的影响程度会大大降低，这也是目前能想到的最佳处理方案
            int minRequestCode = maxRequestCode > 20000 ? maxRequestCode - 10000 : maxRequestCode / 2;
            requestCode = RANDOM.nextInt(maxRequestCode - minRequestCode) + minRequestCode;
        } while (requestCode == XXPermissions.REQUEST_CODE ||
                REQUEST_CODE_ARRAY.contains(requestCode));

        // 标记这个请求码已经被占用
        REQUEST_CODE_ARRAY.add(requestCode);
        return requestCode;
    }

    /**
     * 释放对某个请求码的占用
     */
    public static synchronized void releaseRequestCode(int requestCode) {
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);
    }
}