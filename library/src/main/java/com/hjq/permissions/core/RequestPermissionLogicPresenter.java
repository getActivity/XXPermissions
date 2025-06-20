package com.hjq.permissions.core;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manager.ActivityOrientationManager;
import com.hjq.permissions.tools.AndroidVersionTools;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionDescription;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionTaskHandler;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.permission.PermissionType;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限申请主要逻辑实现类
 */
public final class RequestPermissionLogicPresenter {

    @NonNull
    private final Activity mActivity;

    @NonNull
    private final List<IPermission> mRequestPermissions;

    @NonNull
    private final PermissionFragmentFactory<?, ?> mFragmentFactory;

    @NonNull
    private final OnPermissionInterceptor mPermissionInterceptor;

    @NonNull
    private final OnPermissionDescription mPermissionDescription;

    @Nullable
    private final OnPermissionCallback mCallBack;

    public RequestPermissionLogicPresenter(@NonNull Activity activity,
                                    @NonNull List<IPermission> requestPermissions,
                                    @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                    @NonNull OnPermissionInterceptor permissionInterceptor,
                                    @NonNull OnPermissionDescription permissionDescription,
                                    @Nullable OnPermissionCallback callback) {
        mActivity = activity;
        mRequestPermissions = requestPermissions;
        mFragmentFactory = fragmentFactory;
        mPermissionInterceptor = permissionInterceptor;
        mPermissionDescription = permissionDescription;
        mCallBack = callback;
    }

    /**
     * 开始权限请求
     */
    public void request() {
        if (mRequestPermissions.isEmpty()) {
            return;
        }

        List<List<IPermission>> unauthorizedPermissions = getUnauthorizedPermissions(mActivity, mRequestPermissions);
        if (unauthorizedPermissions.isEmpty()) {
            // 证明没有权限可以请求，直接处理权限请求结果
            handlePermissionRequestResult();
            return;
        }

        Iterator<List<IPermission>> iterator = unauthorizedPermissions.iterator();
        List<IPermission> firstPermissions = null;
        while (iterator.hasNext() && (firstPermissions == null || firstPermissions.isEmpty())) {
            firstPermissions = iterator.next();
        }
        if (firstPermissions == null || firstPermissions.isEmpty()) {
            // 证明没有权限可以请求，直接处理权限请求结果
            handlePermissionRequestResult();
            return;
        }

        final Activity activity = mActivity;
        final PermissionFragmentFactory<?, ?> fragmentFactory = mFragmentFactory;
        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // 发起权限请求
        requestPermissions(activity, firstPermissions, fragmentFactory, permissionDescription, new Runnable() {
            @Override
            public void run() {
                List<IPermission> nextPermissions = null;
                while (iterator.hasNext()) {
                    nextPermissions = iterator.next();

                    if (nextPermissions == null || nextPermissions.isEmpty()) {
                        // 获取到的这个权限列表不符合要求，继续循环获取，虽然前面已经筛选过一波了，理论上不会走到这里来，但是为了代码的严谨性，这里还是要加一下判断
                        continue;
                    }

                    // 这里解释一下为什么之前已经判断过权限是否授予了，还要在这里再次判断，这难道不是多此一举吗？主要为了适配几种极端场景：
                    // 1. 用户发起相机权限和悬浮窗权限申请，在系统弹出相机权限授权框的时候，用户并没有授予，而是搞了一个骚操作，
                    //    直接跑去系统设置中，找到当前 App 的应用详情页的悬浮窗权限选项，然后就直接授予悬浮窗权限，最后再回到 App 上面，
                    //    此时系统还在傻傻等待用户授予相机权限，等用户授予了相机权限后，此时框架下一个申请的权限就是悬浮窗权限，
                    //    但是前面用户已经授予了悬浮窗权限，如果不在这里再次判断权限是否授予，就会导致一个问题，框架仍会跳转到悬浮窗设置页。
                    // 2. 偶然的一次测试，发现在 Android 12 的模拟器上面申请前台定位权限（包含模糊定位和精确定位）和后台定位权限有一个问题，
                    //    这个问题就是用户在授予定位权限的时候，故意选中《大致位置》（系统默认是帮你选中《确切位置》），这样前台定位权限其实不算申请成功，
                    //    这是因为选中《大致位置》会导致精确定位权限没有授予，反之则不会，如果用户选中的是《确切位置》，则精确定位权限和模糊定位权限会是都授予的状态，
                    //    此时下一个权限是后台定位权限，框架会引导用户去往权限设置页中重新授予定位权限，此时骚操作又来了，用户找到定位权限的选项，然后点进去，
                    //    此时用户勾选了《始终允许》选项，但是故意不勾选《使用确切位置》选项，然后返回到 App 中，此时用户回到 App 中重新发起权限申请，
                    //    这个时候系统会弹授权框让用户从《大致位置》更改为《确切位置》，然后用户选中《更改为切确切位置》，此时前台定位权限就算是申请完成了，
                    //    此时下一个权限就是后台定位权限了，但是前面说了用户勾选过了《始终允许》选项，如果不在这里再次判断权限是否授予，就会导致一个问题，
                    //    框架仍会发起一次新的权限申请，如果后台定位权限使用权限描述器的时候，是采用 Dialog 询问是否发起权限请求，就会出现一个奇怪的现象，
                    //    App 用 Dialog 询问了用户要不要发起权限，结果用户选了《是》，但是实际上后台定位权限已经授予了，此时系统不会弹出任何授权框，而是告诉用户授权成功。
                    // 总结：之所以会出现这个问题，是因为第一个请求的权限列表中间不会有延迟，用户根本没有机会干其他事情，所以还能相信权限还是处于没有授予的状态，
                    //      但是到了第二个要请求的权限列表情况就复杂多了，因为你永远想不到用户在前面申请第一个权限列表的时候，那段时间干了什么骚操作。
                    if (PermissionApi.isGrantedPermissions(activity, nextPermissions)) {
                        // 将下一个要请求权限列表置空，表示不会请求它
                        nextPermissions = null;
                        // 上面的权限列表不符合请求的要求，继续循环获取
                        continue;
                    }

                    // 如果代码走到这里来，则证明下一个请求的权限列表是符合要求的，这里使用 break 跳出循环，然后进行下一步操作（权限请求）
                    break;
                }

                if (nextPermissions == null || nextPermissions.isEmpty()) {
                    // 证明请求已经全部完成，延迟发送权限处理结果
                    postDelayedHandlerRequestPermissionsResult();
                    return;
                }

                // 获取第下一次要申请权限列表中的首个权限
                IPermission firstNextPermission = nextPermissions.get(0);
                // 如果下一个请求的权限是后台权限
                if (firstNextPermission.isBackgroundPermission(activity)) {
                    List<IPermission> foregroundPermissions = firstNextPermission.getForegroundPermissions(activity);
                    boolean grantedForegroundPermission = false;
                    // 如果这个后台权限对应的前台权限没有申请成功，则不要去申请后台权限，因为申请了也没有用，系统肯定不会给通过的
                    // 如果这种情况下还硬要去申请，等下还可能会触发权限说明弹窗，但是没有实际去申请权限的情况
                    if (foregroundPermissions != null && !foregroundPermissions.isEmpty()) {
                        for (IPermission foregroundPermission : foregroundPermissions) {
                            if (!foregroundPermission.isGrantedPermission(activity)) {
                                continue;
                            }
                            // 所有的前台权限中，只要有任一一个授权了，就算它是前台权限是申请通过的
                            grantedForegroundPermission = true;
                        }
                    }

                    if (!grantedForegroundPermission) {
                        // 如果前台权限没有授予，就不去申请后台权限，直接进行下一轮申请
                        this.run();
                        return;
                    }
                }

                final List<IPermission> finalPermissions = nextPermissions;
                int maxWaitTimeByPermissions = PermissionApi.getMaxIntervalTimeByPermissions(activity, nextPermissions);
                if (maxWaitTimeByPermissions == 0) {
                    requestPermissions(activity, finalPermissions, fragmentFactory, permissionDescription, this);
                } else {
                    PermissionTaskHandler.sendTask(() ->
                        requestPermissions(activity, finalPermissions, fragmentFactory, permissionDescription, this), maxWaitTimeByPermissions);
                }
            }
        });
    }

    /**
     * 获取未授权的危险权限
     */
    private static List<List<IPermission>> getUnauthorizedPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions) {
        // 未授权的权限列表
        List<List<IPermission>> unauthorizedPermissions = new ArrayList<>(requestPermissions.size());
        // 已处理的权限列表
        List<IPermission> alreadyDonePermissions = new ArrayList<>(requestPermissions.size());

        // 遍历需要请求的权限列表
        for (int i = 0; i < requestPermissions.size(); i++) {
            IPermission permission = requestPermissions.get(i);

            // 如果这个权限在前面已经处理过了，就不再处理
            if (PermissionUtils.containsPermission(alreadyDonePermissions, permission)) {
                continue;
            }
            alreadyDonePermissions.add(permission);

            // 如果这个权限不支持申请，就不纳入申请的范围内
            if (!permission.isSupportRequestPermission(activity)) {
                continue;
            }

            // 如果这个权限已授权，就不纳入申请的范围内
            if (permission.isGrantedPermission(activity)) {
                continue;
            }

            // ---------------------------------- 下面处理特殊权限的逻辑 ------------------------------------------ //

            if (permission.getPermissionType() == PermissionType.SPECIAL) {
                // 如果这是一个特殊权限，那么就作为单独的一次权限进行处理
                unauthorizedPermissions.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // ---------------------------------- 下面处理危险权限的逻辑 ------------------------------------------ //

            // 查询危险权限所在的权限组类型
            String permissionGroup = permission.getPermissionGroup();
            if (TextUtils.isEmpty(permissionGroup)) {
                // 如果权限组为空，则证明这个权限被没有被定义权限组，就直接单独做为一次权限申请
                unauthorizedPermissions.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            List<IPermission> todoPermissions = null;
            for (int j = i; j < requestPermissions.size(); j++) {
                IPermission todoPermission = requestPermissions.get(j);
                // 如果遍历到的权限对象不是同一个组别的，就继续找
                if (!TextUtils.equals(todoPermission.getPermissionGroup(), permissionGroup)) {
                    continue;
                }

                // 判断当前权限是否支持申请
                if (!todoPermission.isSupportRequestPermission(activity)) {
                    // 如果这个权限不支持申请，就不往下执行
                    continue;
                }

                // 判断要申请的权限是否授予了
                if (todoPermission.isGrantedPermission(activity)) {
                    // 如果这个权限已经授予，就不往下执行
                    // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/369
                    continue;
                }

                // 如果待处理的权限列表还没有初始化，就先进行初始化操作
                if (todoPermissions == null) {
                    todoPermissions = new ArrayList<>();
                }
                // 添加到待处理的权限列表中
                todoPermissions.add(todoPermission);

                // 如果这个危险权限在前面已经处理过了，就不再添加
                if (PermissionUtils.containsPermission(alreadyDonePermissions, todoPermission)) {
                    continue;
                }
                // 添加到已处理的权限列表中
                alreadyDonePermissions.add(todoPermission);
            }

            // 如果这个待处理的权限列表为空，证明剩余的权限是在高版本系统才会出现，这里无需再次发起申请
            if (todoPermissions == null || todoPermissions.isEmpty()) {
                continue;
            }

            // 如果这个待处理的权限列表已经全部授权，就不纳入申请的范围内
            if (PermissionApi.isGrantedPermissions(activity, todoPermissions)) {
                continue;
            }

            // 判断申请的权限组是否包含后台权限（例如后台定位权限，后台传感器权限），如果有的话，不能在一起申请，需要进行拆分申请
            List<IPermission> backgroundPermissions = null;
            Iterator<IPermission> iterator = todoPermissions.iterator();
            while (iterator.hasNext()) {
                IPermission todoPermission = iterator.next();
                // 先判断这个权限是不是后台权限，如果不是就继续找
                if (!todoPermission.isBackgroundPermission(activity)) {
                    continue;
                }
                // 将后台权限单独领出来放到另外一个集合中
                iterator.remove();
                backgroundPermissions = new ArrayList<>();
                backgroundPermissions.add(todoPermission);
                // 任务完成，跳过循环
                break;
            }

            List<IPermission> foregroundPermissions = todoPermissions;

            // 添加前台权限（前提得是没有授权）
            if (!foregroundPermissions.isEmpty()) {
                unauthorizedPermissions.add(foregroundPermissions);
            }
            // 添加后台权限（前提得是没有授权）
            if (backgroundPermissions != null && !backgroundPermissions.isEmpty()) {
                unauthorizedPermissions.add(backgroundPermissions);
            }
        }

        return unauthorizedPermissions;
    }

    /**
     * 发起一次权限请求
     */
    private static void requestPermissions(@NonNull Activity activity, List<IPermission> permissions,
                                            @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                            @NonNull OnPermissionDescription permissionDescription,
                                            @NonNull Runnable finishRunnable) {
        if (permissions.isEmpty()) {
            finishRunnable.run();
            return;
        }

        PermissionType permissionType = PermissionApi.areAllDangerousPermission(permissions) ?
                                        PermissionType.DANGEROUS : PermissionType.SPECIAL;
        if (permissionType == PermissionType.DANGEROUS && !AndroidVersionTools.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念
            finishRunnable.run();
            return;
        }

        Runnable continueRequestRunnable = () -> fragmentFactory.createAndCommitFragment(permissions, permissionType, new OnPermissionFlowCallback() {

            @Override
            public void onRequestPermissionNow() {
                permissionDescription.onRequestPermissionStart(activity, permissions);
            }

            @Override
            public void onRequestPermissionFinish() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
                finishRunnable.run();
            }

            @Override
            public void onRequestPermissionAnomaly() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
            }
        });

        permissionDescription.askWhetherRequestPermission(activity, permissions, continueRequestRunnable, finishRunnable);
    }

    /**
     * 延迟处理权限请求结果
     */
    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionTaskHandler.sendTask(this::handlePermissionRequestResult, 100);
    }

    /**
     * 延迟解锁 Activity 方向
     */
    private void postDelayedUnlockActivityOrientation(@NonNull Activity activity) {
        // 延迟执行是为了让外层回调中的代码能够顺序执行完成
        PermissionTaskHandler.sendTask(() -> ActivityOrientationManager.unlockActivityOrientation(activity), 100);
    }

    /**
     * 处理权限请求结果
     */
    private void handlePermissionRequestResult() {
        OnPermissionCallback callback = mCallBack;

        OnPermissionInterceptor interceptor = mPermissionInterceptor;

        List<IPermission> requestPermissions = mRequestPermissions;

        Activity activity = mActivity;

        // 如果当前 Activity 不可用，就不继续往下执行代码
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        List<IPermission> grantedPermissions = new ArrayList<>(requestPermissions.size());
        List<IPermission> deniedPermissions = new ArrayList<>(requestPermissions.size());
        // 遍历请求的权限，并且根据权限的授权状态进行分类
        for (IPermission permission : requestPermissions) {
            if (permission.isGrantedPermission(activity, false)) {
                grantedPermissions.add(permission);
            } else {
                deniedPermissions.add(permission);
            }
        }

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size() == requestPermissions.size()) {
            // 代表申请的所有的权限都授予了
            interceptor.grantedPermissionRequest(activity, requestPermissions, grantedPermissions, true, callback);
            // 权限申请结束
            interceptor.finishPermissionRequest(activity, requestPermissions, false, callback);
            // 延迟解锁 Activity 屏幕方向
            postDelayedUnlockActivityOrientation(activity);
            return;
        }

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor.deniedPermissionRequest(activity, requestPermissions, deniedPermissions,
            PermissionApi.isDoNotAskAgainPermissions(activity, deniedPermissions), callback);

        // 证明还有一部分权限被成功授予，回调成功接口
        if (!grantedPermissions.isEmpty()) {
            interceptor.grantedPermissionRequest(activity, requestPermissions, grantedPermissions, false, callback);
        }

        // 权限申请结束
        interceptor.finishPermissionRequest(activity, requestPermissions, false, callback);

        // 延迟解锁 Activity 屏幕方向
        postDelayedUnlockActivityOrientation(activity);
    }
}