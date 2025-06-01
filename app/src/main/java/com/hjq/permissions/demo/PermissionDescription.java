package com.hjq.permissions.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.hjq.permissions.OnPermissionDescription;
import com.hjq.permissions.XXPermissions;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限请求描述实现
 */
public final class PermissionDescription implements OnPermissionDescription {

    /** 消息处理 Handler 对象 */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /** 权限请求描述弹窗显示类型：Dialog */
    private static final int DESCRIPTION_WINDOW_TYPE_DIALOG = 0;
    /** 权限请求描述弹窗显示类型：PopupWindow */
    private static final int DESCRIPTION_WINDOW_TYPE_POPUP = 1;

    /** 权限请求描述弹窗显示类型 */
    private int mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;

    /** 消息 Token */
    @NonNull
    private final Object mHandlerToken = new Object();

    /** 权限申请说明弹窗 */
    @Nullable
    private PopupWindow mPermissionPopupWindow;

    /** 权限申请说明对话框 */
    @Nullable
    private Dialog mPermissionDialog;

    @Override
    public void askWhetherRequestPermission(@NonNull Activity activity, @NonNull List<String> requestPermissions,
                                            @NonNull Runnable continueRequestRunnable, @NonNull Runnable breakRequestRunnable) {
        if (XXPermissions.containsSpecialPermission(requestPermissions)) {
            // 如果请求的权限中包含特殊权限，那么就用 Dialog 来展示权限说明弹窗
            mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;
        } else {
            int activityOrientation = activity.getResources().getConfiguration().orientation;
            // 如果当前 Activity 的屏幕是竖屏的话，就用 PopupWindow 展示权限说明弹窗，否则用 Dialog 来展示权限说明弹窗
            mDescriptionWindowType = activityOrientation == Configuration.ORIENTATION_PORTRAIT ?
                DESCRIPTION_WINDOW_TYPE_POPUP : DESCRIPTION_WINDOW_TYPE_DIALOG;
            // 如果本次申请的权限中带有后台权限（例如后台定位权限、后台传感器权限等），则改用 Dialog 来展示权限说明弹窗
            if (XXPermissions.containsBackgroundPermission(requestPermissions)) {
                mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;
            }
        }

        if (mDescriptionWindowType == DESCRIPTION_WINDOW_TYPE_POPUP) {
            continueRequestRunnable.run();
            return;
        }

        showDialog(activity, activity.getString(R.string.common_permission_description_title),
            generatePermissionDescription(activity, requestPermissions),
            activity.getString(R.string.common_permission_granted), (dialog, which) -> {
                dialog.dismiss();
                continueRequestRunnable.run();
            }, activity.getString(R.string.common_permission_denied), (dialog, which) -> {
                dialog.dismiss();
                breakRequestRunnable.run();
            });
    }

    @Override
    public void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<String> requestPermissions) {
        if (mDescriptionWindowType != DESCRIPTION_WINDOW_TYPE_POPUP) {
            return;
        }

        Runnable showPopupRunnable = () -> showPopupWindow(activity, generatePermissionDescription(activity, requestPermissions));
        // 这里解释一下为什么要延迟一段时间再显示 PopupWindow，这是因为系统没有开放任何 API 给外层直接获取权限是否永久拒绝
        // 目前只有申请过了权限才能通过 shouldShowRequestPermissionRationale 判断是不是永久拒绝，如果此前没有申请过权限，则无法判断
        // 针对这个问题能想到最佳的解决方案是：先申请权限，如果极短的时间内，权限申请没有结束，则证明权限之前没有被用户勾选了《不再询问》
        // 此时系统的权限弹窗正在显示给用户，这个时候再去显示应用的 PopupWindow 权限说明弹窗给用户看，所以这个 PopupWindow 是在发起权限申请后才显示的
        // 这样做是为了避免 PopupWindow 显示了又马上消失，这样就不会出现 PopupWindow 一闪而过的效果，提升用户的视觉体验
        // 最后补充一点：350 毫秒只是一个经验值，经过测试可覆盖大部分机型，具体可根据实际情况进行调整，这里不做强制要求
        // 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/366
        HANDLER.postAtTime(showPopupRunnable, mHandlerToken, SystemClock.uptimeMillis() + 350);
    }

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<String> requestPermissions) {
        // 移除跟这个 Token 有关但是没有还没有执行的消息
        HANDLER.removeCallbacksAndMessages(mHandlerToken);
        // 销毁当前正在显示的弹窗
        dismissPopupWindow();
        dismissDialog();
    }

    /**
     * 生成权限描述文案
     */
    private String generatePermissionDescription(@NonNull Activity activity, @NonNull List<String> requestPermissions) {
        return PermissionConverter.getDescriptionsByPermissions(activity, requestPermissions);
    }

    /**
     * 显示 Dialog
     *
     * @param activity                  Activity 对象
     * @param dialogTitle               对话框标题
     * @param dialogMessage             对话框消息
     * @param confirmButtonText         对话框确认按钮文本
     * @param confirmListener           对话框确认按钮点击事件
     * @param cancelButtonText          对话框取消按钮文本
     * @param cancelListener            对话框取消按钮点击事件
     */
    private void showDialog(@NonNull Activity activity, @Nullable String dialogTitle, @Nullable String dialogMessage,
                            @Nullable String confirmButtonText, @Nullable DialogInterface.OnClickListener confirmListener,
                            @Nullable String cancelButtonText, @Nullable DialogInterface.OnClickListener cancelListener) {
        if (mPermissionDialog != null) {
            dismissDialog();
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        // 另外这里需要判断 Activity 的类型来申请权限，这是因为只有 AppCompatActivity 才能调用 Support 包的 AlertDialog 来显示，否则会出现报错
        // java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity
        // 为什么不直接用 App 包 AlertDialog 来显示，而是两套规则？因为 App 包 AlertDialog 是系统自带的类，不同 Android 版本展现的样式可能不太一样
        // 如果这个 Android 版本比较低，那么这个对话框的样式就会变得很丑，准确来讲也不能说丑，而是当时系统的 UI 设计就是那样，它只是跟随系统的样式而已
        if (activity instanceof AppCompatActivity) {
            mPermissionDialog = new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                // 对话框一定要设置成不可取消的
                .setCancelable(false)
                .setPositiveButton(confirmButtonText, confirmListener)
                .setNegativeButton(cancelButtonText, cancelListener)
                .create();
        } else {
            mPermissionDialog = new AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                // 对话框一定要设置成不可取消的
                .setCancelable(false)
                .setPositiveButton(confirmButtonText, confirmListener)
                .setNegativeButton(cancelButtonText, cancelListener)
                .create();
        }
        mPermissionDialog.show();
        // 将 Activity 和 Dialog 生命周期绑定在一起，避免可能会出现的内存泄漏
        // 当然如果上面创建的 Dialog 已经有做了生命周期管理，则不需要执行下面这行代码
        WindowLifecycleManager.bindDialogLifecycle(activity, mPermissionDialog);
    }

    /**
     * 销毁 Dialog
     */
    private void dismissDialog() {
        if (mPermissionDialog == null) {
            return;
        }
        if (!mPermissionDialog.isShowing()) {
            return;
        }
        mPermissionDialog.dismiss();
        mPermissionDialog = null;
    }

    /**
     * 显示 PopupWindow
     *
     * @param activity              Activity 对象
     * @param content               弹窗显示的内容
     */
    private void showPopupWindow(@NonNull Activity activity, @NonNull String content) {
        if (mPermissionPopupWindow != null) {
            dismissPopupWindow();
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View contentView = LayoutInflater.from(activity)
            .inflate(R.layout.permission_description_popup, decorView, false);
        mPermissionPopupWindow = new PopupWindow(activity);
        mPermissionPopupWindow.setContentView(contentView);
        mPermissionPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPermissionPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPermissionPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPermissionPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPermissionPopupWindow.setTouchable(true);
        mPermissionPopupWindow.setOutsideTouchable(true);
        TextView messageView = mPermissionPopupWindow.getContentView().findViewById(R.id.tv_permission_description_message);
        messageView.setText(content);
        mPermissionPopupWindow.showAtLocation(decorView, Gravity.TOP, 0, 0);
        // 将 Activity 和 PopupWindow 生命周期绑定在一起，避免可能会出现的内存泄漏
        // 当然如果上面创建的 PopupWindow 已经有做了生命周期管理，则不需要执行下面这行代码
        WindowLifecycleManager.bindPopupWindowLifecycle(activity, mPermissionPopupWindow);
    }

    /**
     * 销毁 PopupWindow
     */
    private void dismissPopupWindow() {
        if (mPermissionPopupWindow == null) {
            return;
        }
        if (!mPermissionPopupWindow.isShowing()) {
            return;
        }
        mPermissionPopupWindow.dismiss();
        mPermissionPopupWindow = null;
    }
}