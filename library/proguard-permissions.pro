# 禁止混淆 IStartActivityDelegate 和 IFragmentMethodNative 接口及实现类涉及的方法名称
-keepclassmembers interface com.hjq.permissions.delegate.IStartActivityDelegate {
    <methods>;
}
-keepclassmembers interface com.hjq.permissions.fragment.IFragmentMethodNative {
    <methods>;
}

# 禁止混淆支持库中 Fragment 的 getActivity 方法名称
# 看到这里你可能会有一些几个疑问，我会在此处依次解答这些疑问：
# 1. 前面对 IFragmentMethodNative 接口的混淆规则不是已经包含了 getActivity 方法？
#    在这里为什么还要单独添加一遍重复作用的混淆规则，这不是脱裤子放屁多此一举？
#    这是因为这个方法比较特殊，混淆编译的时候没有正确识别到这个方法，
#    从而导致把支持库中 Fragment 类定义的 getActivity 方法给混淆了，
#    但是 IFragmentMethodNative 定义的 getActivity 方法没有给混淆；
#    这是因为 IFragmentMethodNative 接口定义是 A getActivity()，
#    而支持库中 Fragment 类定义的是 FragmentActivity getActivity()，
#    如果不禁止混淆 Fragment 中 getActivity 方法名，会导致它被混淆掉。
# 2. 为什么只写了 Support 库的混淆规则，你确定这个混淆规则在 AndroidX 上面能用？
#    结论是没有问题的，放心大胆用，我帮大家测试过了，是完全可行的，不信可以拿 apk 反编译看看，
#    在开启 android.enableJetifier=true 的时候编译器会自动帮你转换成 AndroidX 的包名，
#    所以完全没有必要在这里多写一个 AndroidX 的版本混淆规则，那样做才是多此一举，没有任何意义。
-keepclassmembers class android.support.v4.app.Fragment {
    android.support.v4.app.FragmentActivity getActivity();
}
#-keepclassmembers class androidx.fragment.app.Fragment {
#    androidx.fragment.app.FragmentActivity getActivity();
#}