#### 目录

* [Intent 跳转极限兜底机制](#intent-跳转极限兜底机制)

* [兼容请求权限 API 崩溃问题](#兼容请求权限-api-崩溃问题)

* [规避系统权限回调空指针问题](#规避系统权限回调空指针问题)

* [应用商店权限合规处理](#应用商店权限合规处理)

* [自动拆分权限进行请求](#自动拆分权限进行请求)

* [框架内部完全剥离 UI 层](#框架内部完全剥离-ui-层)

* [核心逻辑和具体权限完全解耦](#核心逻辑和具体权限完全解耦)

* [自动适配后台权限](#自动适配后台权限)

* [支持在跨平台环境中调用](#支持在跨平台环境中调用)

* [回调生命周期与宿主保持同步](#回调生命周期与宿主保持同步)

* [支持自定义权限申请](#支持自定义权限申请)

* [支持读取应用列表权限](#支持读取应用列表权限)

* [新版本权限支持向下兼容](#新版本权限支持向下兼容)

* [屏幕旋转场景适配](#屏幕旋转场景适配)

* [后台申请权限场景适配](#后台申请权限场景适配)

* [修复 Android 12 内存泄漏问题](修复-android-12-内存泄漏问题)

* [第三方厂商兼容性优化](#第三方厂商兼容性优化)

* [支持检测代码错误](#支持检测代码错误)

#### Intent 跳转极限兜底机制

* 在介绍这个功能之前，我先问大家一个问题，请你分析一下这段代码是否有什么问题？

```java
Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
intent.setData("package:" + getPackageName());
startActivityForResult(intent, 1024);
```

* 你可能会说：很简单啊，这不就是一个跳转到应用详情页的代码，还能有什么问题？你莫不是要找我的茬？

* 这段代码看似没有问题，运行起来也没有问题，但实际上是一个天坑，你没有看到或者遇到并不代表不存在，有些厂商直接阉割了 `ACTION_APPLICATION_DETAILS_SETTINGS` 这个意图，是的你没有听错，就是直接阉割，这段代码在这些设备上面运行，应用就会闪崩，没有跟你开玩笑，

```text
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.settings.APPLICATION_DETAILS_SETTINGS dat=Package Name:com.xxx.xxx }
```

* 你如果还是不信，请看这里 [Github Search `No Activity found to handle Intent  act=android.settings.APPLICATION_DETAILS_SETTINGS`](https://github.com/search?q=No+Activity+found+to+handle+Intent++act%3Dandroid.settings.APPLICATION_DETAILS_SETTINGS&type=issues)：

* 其实不止是 `ACTION_APPLICATION_DETAILS_SETTINGS` 这个意图，其他的意图也会，一个都跑不了，你如果不信可以看这里 [Github Search `No Activity found to handle Intent  act=android`](https://github.com/search?q=No+Activity+found+to+handle+Intent++act%3Dandroid&type=issues)。

```
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) }
```

* 看完你是不是想吐槽一下？但问题已经存在，非理性的抱怨永远解决不了问题，只有理性的分析和认真的思考才是唯一的出路。这个问题无非是 `Intent` 找不到了，最简单有效的方法，就是在跳转前对 `Intent` 进行判断，如果存在这个 `Intent` 再跳转，如果不存在就不跳转，你如果要是真的那么想问题就太片面了，事情往往没有你想得那么简单，不存在的 `Intent` 跳转会失败，那你有没有想过，存在的 `Intent` 也不代表一定能跳转成功，你如果不信可以看这里 [Github Search `Permission Denial: starting Intent`](https://github.com/search?q=Permission+Denial%3A+starting+Intent&type=issues)，现在知道为什么叫天坑了吧？

```text
java.lang.SecurityException: 
Permission Denial: starting Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) cmp=xxxx/xxx }
```

* 说这些并不是想让大家解决，而是让大家意识到有这个问题，当然框架内部已经处理好这个问题，你能想到的所有问题，框架已经提前想到了，并且已经帮你处理好了，只需要一句代码，调用 `XXPermissions.startPermissionActivity` 方法即可。假设你很好奇框架是怎么实现的，又懒得看源码实现，这点我也帮你想到了，在这里我介绍框架是怎么实现的，原理其实很简单，框架获取这个权限设置页的时候，把能想到的 `Intent` 写到 List 集合中，再筛选掉不存在的 `Intent`，然后挨个 `Intent` 进行跳转，如果失败就跳转到下一个，直到跳转成功或者没有下一个 `Intent` 了为止。

#### 兼容请求权限 API 崩溃问题

* 在介绍这个功能之前，我先问大家一个问题，请你分析一下这段代码是否有什么问题？

```java
activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1024);
```

* 你可能会说：这不就是一段再简单不过用系统 API 申请权限的代码吗？还能有什么问题，你只要不在 Android 6.0 以下的设备调用就肯定没有问题。

* 理论上是这样的，但是理论终究是理论，实际情况是在 Android 6.0 及以上的设备调用也有可能出现崩溃，对的你没有看错，Android 6.0 以上调用会出现崩溃，听着也太魔幻了，那么重要的系统 API 居然也会崩溃？如果不信可以看这里 [XXPermissions/issues/153](https://github.com/getActivity/XXPermissions/issues/153)、[XXPermissions/issues/126](https://github.com/getActivity/XXPermissions/issues/126)、[XXPermissions/issues/327](https://github.com/getActivity/XXPermissions/issues/327)、[XXPermissions/issues/339](https://github.com/getActivity/XXPermissions/issues/339)，如果还不够看的话可以看这里 [Github Search `act=android.content.pm.action.REQUEST_PERMISSIONS`](https://github.com/search?q=act%3Dandroid.content.pm.action.REQUEST_PERMISSIONS&type=issues)，看完是不是瞬间颠覆了你的认知？

```text
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.android.packageinstaller (has extras) }
```

* 出现这种情况有以下几种可能：

    1. 厂商开发工程师修改了 `com.android.packageinstaller` 系统应用的包名，但是没有自测好就上线了（概率较小）

    2. 厂商开发工程师删除了 `com.android.packageinstaller` 这个系统应用，但是没有自测好就上线了（概率较小）

    3. 厂商开发工程师在修改 Android 系统源码的时候，改动的代码影响到权限模块，但是没有自测好就上线了（概率较小）

    4. 厂商主动阉割掉了权限申请功能，例如在电视 TV 设备上面，间接导致请求危险权限的 App 一请求权限就闪退（概率较小）

    5. 用户有 Root 权限，在精简系统 App 的时候不小心删掉了 `com.android.packageinstaller` 这个系统应用（概率较大）

* 经过分析 `Activity.requestPermissions` 的源码，它本质上还是调用 `startActivityForResult`，只不过 `Activity` 找不到了而已，目前能想到最好的解决方式，就是用 `try catch` 避免它出现崩溃，看到这里你可能会有一个疑问，就简单粗暴 `try catch`？你确定不会引发其他问题？会不会导致 `onRequestPermissionsResult` 没有回调？从而导致权限请求流程卡住的情况？虽然这个问题没有办法测试，但理论上是不会的，因为我用了错误的 `Intent` 进行 `startActivityForResult` 并进行 `try catch` 做实验，结果 `onActivityResult` 还是有被系统正常回调，证明对 `startActivityForResult` 进行 `try catch` 并不会影响 `onActivityResult` 的回调，我还分析了 `Activity` 回调方面的源码实现，发现无论是 `onRequestPermissionsResult` 还是 `onActivityResult`，回调它们的都是 `dispatchActivityResult` 方法，在那种极端情况下，既然 `onActivityResult` 能被回调，那么就证明 `dispatchActivityResult` 肯定有被系统正常调用的，同理 `onRequestPermissionsResult` 也肯定会被 `dispatchActivityResult` 正常调用，从而形成一个完整的逻辑闭环。

* 补充后续测试结论：我在 debug 了 `Activity.requestPermissions` 方法，偷偷修改权限请求 `Intent` 的 `Action` 成错误的，结果权限回调能正常回调。

* 如果真的出现这种极端情况，所有危险权限的申请必然会走失败的回调，但是框架能做的是：尽量让应用不要崩溃，并且能走完整个权限申请的流程。

#### 规避系统权限回调空指针问题

* 在介绍这个功能之前，我先问大家一个问题，请你分析一下这段代码是否有什么问题？

```java
public final class XxxActivity extends AppCompatActivity  {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            System.out.println("获取相机权限成功");
        } else {
            System.out.println("获取相机权限失败");
        }
    }
}
```

* 你可能会说：这不是正常在权限回调中处理权限请求结果，我平时就是那么写的，怎么看都没有什么毛病啊，你是不是没事找茬？

* 如果我告诉你一件事，系统返回的参数 `permissions` 或 `grantResults` 数组对象有可能会为空，你会不会相信呢？我知道你肯定不信，因为你看到了 `permissions` 和 `grantResults` 参数上面都有 `@NonNull` 注解（点进去 Activity 源码里面看到的也是  `@NonNull` 注解），就代表系统返回的一定不为空，到这里你肯定认为我在欺骗你。

* 我知道你不信，所以证据给你备好了，请看这里 [XXPermissions/issues/191](https://github.com/getActivity/XXPermissions/issues/191)、[XXPermissions/issues/106](https://github.com/getActivity/XXPermissions/issues/106)、[XXPermissions/issues/236](https://github.com/getActivity/XXPermissions/issues/236)，如果还不够看的话可以看这里 [Github Search `NullPointerException onRequestPermissionsResult`](https://github.com/search?q=NullPointerException+onRequestPermissionsResult&type=issues);

* 看完是不是不知道你是何种想法？系统这是要闹哪样？把参数标记成不为空结果却给我返回空的，这难道不是在欺骗我的感情？问题已经存在，非理性的抱怨永远解决不了问题，只有理性的分析和认真的思考才是唯一的出路。

* 目前反馈这个问题的机型品牌有 vivo、小米、联想；就说明这个问题大概率又是 `Google` 工程师挖的坑，解决这个问题的思路有两种：

    1. 仍然要用 `permissions` 和 `grantResults` 参数来判断权限的状态：使用之前需要先对数组对象进行防空判断，然后继续使用。

    2. 不再使用  `permissions` 和 `grantResults`  参数来判断权限的状态：改用 `checkSelfPermission` 的方式来判断权限状态。

* 虽然两种都可以解决问题，但是两种略有区别，框架最终采用的是第二种，中国有一句老话叫：一次不忠终身不用，既然它能干这种毫无底线的事情，就不得不防它还有其他小动作，例如以下场景：

    1. 返回的数组对象不为空，但是数组里面没有元素，如果事先不进行判断，一调用就可能会触发角标越界异常 `ArrayIndexOutOfBoundsException`

    2. 返回的数组对象不为空，数组里面也有元素，但是 `permissions` 和 `grantResults` 两个数组的长度不一样，如果事先不进行判断，一调用就可能会触发角标越界异常 `ArrayIndexOutOfBoundsException`

    3. 返回的数组对象不为空，数组里面也有元素，两个数组的长度也是正常的，但是返回的 `grantResults` 与实际不匹配，用户明明授予了权限，但是这个数组存的却是 `-1`（`PackageManager.PERMISSION_DENIED`）

* 到这里你是不是瞬间觉得解决这个空指针的问题好像不是只是加一下防空判断那么简单？原来里面的学问那么多。在此我想跟大家说，无论是什么问题，我都会认真对待，因为我追求的从来不是能解决问题就好，而是在所有能想到的解决方案中找出最优解。

#### 应用商店权限合规处理

* 现在国内的应用商店，在申请权限的时候，需要同步告知权限申请的目的，否则会被拒绝上架或更新，框架已经帮你考虑到这点了，目前已经开放相应的接口，你可以实现接口来这一需求，具体效果如下图所示：

* 虽然这个功能自己不需要框架提供接口也能实现，只需要在权限申请前显示弹窗，权限申请完成取消弹窗就行，但是这样会使你写的代码不优雅，因为这部分的代码是直接写死在 `Activity/Fragment` 类中的，不仅会增加 `Activity/Fragment` 类的复杂度，并且每个用到权限申请的 `Activity/Fragment` 类都要写一份这样的代码，后续会变得难以维护，正是考虑到这个问题，框架才开放了这个接口，还在 Demo 工程实现了一份完整的案例供你参考，你不仅可以轻而易举实现这个功能，过程无需操心实现的细节和是否有 Bug，因为你能想到的，我都帮你想到了，你没有想到的，我也帮你想到了。

#### 自动拆分权限进行请求

* 在一些需求场景下，需要同时申请多种权限，例如麦克风权限和日历权限，这个时候产品经理想要拆分成两次权限进行申请，以便能够分开显示两个权限的说明弹窗，这样的设计会导致功能开发比较复杂，如果不拆分申请的情况下只需要在请求权限前后加一下显示和关闭弹窗的逻辑就行了，现在要分成两次权限申请后就不能这样写了，只能分开写，分开写就意味着要写各种嵌套和回调，一想到要这样做就一个头两个大，差点就把昨晚吃的宵夜给呕出来。

* 大家的苦，大家的痛，不用多说，我都懂，所以我在框架加了一套处理机制，会自动将你传入的权限进行归类分组，例如麦克风权限归为一组，日历权限归为一组，然后会拆分成两次权限申请，这个时候在搭配上框架开放的权限说明接口，这个接口会告诉你申请什么权限，你再根据权限来展示具体的权限说明弹窗就行了，至此这个功能轻松又优雅地完成了，iOS 端还吭哧吭哧实现，你已经完成并提前下班了，没有延迟，没有痛苦，有的只是实现功能的爽感。

#### 框架内部完全剥离 UI 层

* 某些权限框架内部会实现一套权限说明弹窗的 UI 和逻辑，需要实现特定的接口才能修改，但是我认为这样的设计是不合理的，因为展示权限说明弹窗并不是一个必须的操作，没有它调用权限申请 API 照样会弹出授权框，另外涉及到 UI，框架内部设计的 UI 注定无法满足所有人的需求（吃力不讨好），因为每个人拿到的设计图都是不一样的，所以最好的方案是，框架自己不要在内部写 UI 和逻辑，而是设计好这方面相关的接口，然后全权交由外层实现，当然框架 Demo 模块也会实现一份案例供外层借鉴（供外层直接抄代码），这样不仅能解决 UI 需求不一致的问题，还能减少框架的体积，一箭双雕。

#### 核心逻辑和具体权限完全解耦

* 你在市面上能看到的能同时支持申请危险权限和特殊权限的框架，它们的代码耦合度非常高，这样会导致一个问题，例如你只拿它申请了危险权限，但是最终打包的时候，会连同特殊权限的代码逻辑一起给打包到 apk 中的，这就好比你现在想吃炸鸡，但店员告诉你只有点十个人的套餐才有炸鸡，你心想自己一个人就算撑死也没有办法吃完这十个人的套餐，这种设计不是明摆着坑人吗？虽然 app 多一些代码不会跟人一样被撑死，但是也不要肆意挥霍，这里浪费一点，那里浪费一点，开发完后一看 apk 体积 250 mb，还得考虑体积优化，关键是你还没有办法优化，因为这部分代码是写死在框架中，框架又是通过远程依赖，你就得换成本地依赖去改，改了就意味着可能有 bug 要增加很多自测的工作量，重要的是改了收益不高，但是风险极高，很容易改着改着将自己送上裁员名单。

* 针对这个问题，框架有一个堪称鬼才的设计方案，就是将不同权限的实现封装成对象，你申请什么权限就传什么对象，这样没有引用的对象就会在开启代码混淆的时候一并移除，这样打正式包的时候就不会有冗余的代码，更不会占用多余的 apk 的体积，真正做到了用多少算多少，再也不用为了想吃一块炸鸡而考虑要不要买个十个套餐，无需纠结，无需徘徊，在 XXPermissions 这里可以做到分开买，想吃什么买什么，想吃多少买多少，老少兼宜，童叟无欺。

* 当然对于某些框架，它既不支持任何特殊权限，也没有针对某个危险权限做单独的处理，只是简单套用系统的 API，请求权限就直接用 `context.requestPermissions`，判断权限就直接用 `context.checkSelfPermission`，这种算不算完全解耦呢？其实是算的，因为人家确实没有在核心逻辑中直接依赖具体某个权限，但是这种框架不符合现实开发的需求，因为在一个商业化的 app 中不可能只请求危险权限，通知权限要吧？安装包权限要吧？悬浮窗权限要吧？只要这些框架支持任何一个特殊权限，就会存在这个问题，当然不支持当然就没有这个问题，关键是能不能做到既能支持，又能对代码进行解耦呢？这才是问题的关键，非常考验对框架的理解和代码的设计，截止目前只有 XXPermissions 真正做到了既要又要。

#### 自动适配后台权限

* Android 10 上面新增了[后台定位权限](https://developer.android.google.cn/about/versions/11/privacy/location?hl=zh-cn#background-location) 和 Android 13 上面新增了[后台传感器权限](https://developer.android.google.cn/about/versions/13/behavior-changes-13?hl=zh-cn#body-sensors-background-permission)，你可千万别认为这两个后台权限跟普通的危险权限没有区别，这里面的区别非常大，要是没有搞清楚容易出 Bug。

    1. 前台权限和后台权限不能放在一起申请，必须先申请前台权限，才能申请后台权限，如果在没有前台权限的前提下申请后台权限是肯定会被系统拒绝的，这是毋庸置疑的。

    2. 在 Android 11 之后，前台权限和后台权限申请必须保证一定的时间间隔，也就是拆分两次权限申请之后，还要保证这两次权限申请有一定的时间间隔，否则也会申请失败，经过测试不能低于 150 毫秒。

    3. 后台定位权限对应的前台定位权限有两个，精确定位权限（`ACCESS_FINE_LOCATION`）和模糊定位权限（`ACCESS_COARSE_LOCATION`），在 `Android 10 ~ Android 11` 的版本，后台定位权限锚定的前台权限就是精确定位权限，只有这个权限同意的时候，才能申请后台定位权限，而到了 Android 12 及之后的版本，后台定位权限锚定的前台权限既可以是精确定位权限，也可以是模糊定位权限，这两个权限任一同意的时候，就可以申请后台定位权限。

* 然而上面这些问题，框架已经帮你处理了，你无需自己再手动处理，具体处理方案如下：

    1. 自动识别后台权限和与之对应的前台权限，然后自动拆分成两次权限申请，先申请前台权限，再申请后台权限。

    2. 在申请后台权限的时候，先加一段时间的延迟，也就是前面说的 150 毫秒，再进行申请后台权限，由此规避这个问题。

    3. 在判断后台定位权限的时候，会针对不同的 Android 版本做前台定位权限判断，在 `Android 10 ~ Android 11` 的版本就用精确定位权限，`Android 12` 及之后的版本就用精确定位权限或者模糊定位权限，确保不同 Android 版本能达到同样的预期效果。

#### 支持在跨平台环境中调用

* 众所周知 [FlutterActivity](https://github.com/flutter/flutter/blob/03ef1ba910cac387f7b2af8ab09ca955d3974663/engine/src/flutter/shell/platform/android/io/flutter/embedding/android/FlutterActivity.java)、[ComposeActivity](https://github.com/androidx/androidx/blob/8d08d42d60f7cc7ec0034d0b7ff6fd953516d96a/emoji2/emoji2-emojipicker/samples/src/main/java/androidx/emoji2/emojipicker/samples/ComposeActivity.kt)、[UnityPlayerActivity](https://github.com/FabianTerhorst/PokemonGo/blob/d511b045f1492e0bae71778ef528f3d768d218cd/java/com/unity3d/player/UnityPlayerActivity.java)、[Cocos2dxActivity](https://github.com/irontec/Ikasesteka/blob/master/Ikasesteka/cocos2d/cocos/platform/android/java/src/org/cocos2dx/lib/Cocos2dxActivity.java) 这些都是 Activity 的子类，但是它们都不是 [FragmentActivity](https://github.com/androidx/androidx/blob/8d08d42d60f7cc7ec0034d0b7ff6fd953516d96a/fragment/fragment/src/main/java/androidx/fragment/app/FragmentActivity.java) 的子类，它们的继承关系是这样的：

    1. `FlutterActivity extends Activity`

    2. `ComposeActivity extends ComponentActivity extends Activity`

    3. `UnityPlayerActivity extends Activity`

    4. `Cocos2dxActivity extends Activity`

    5. `FragmentActivity extends ComponentActivity extends Activity`

* 这样就会出现一个问题，有些权限请求框架是用一个透明的 `Fragment` 获得权限申请的回调，如果这个权限请求框架用的是 `android.support.v4.app.Fragment`，那么就必须要求外层传入 `android.support.v4.app.FragmentActivity` 对象，假设这个时候你用的 `Activity` 并不是 `android.support.v4.app.FragmentActivity` 的子类，请问你该怎么办？那简单，我就修改当前 `Activity` 直接继承 `android.support.v4.app.FragmentActivity` 不就行了？那如果你目前的 `Activity` 是一定要继承 `FlutterActivity`、`ComposeActivity`、`UnityPlayerActivity`、`Cocos2dxActivity` 呢？请问你又该怎么改？难不成去改它们的源码？还是去改权限框架的源码？无论选哪种解决方案，改造的成本都会很大，后续也不好维护，这既不现实，也不科学。是不是突然感觉上天把路给你堵死了？难不成只能手写权限申请 API 的代码才能实现权限请求了？

* 其实这个问题框架已经想到了，并且已经帮你解决了，无需你做任何处理，具体的实现原理是：框架会判断你传入的 `Activity` 对象是不是 `android.support.v4.app.FragmentActivity` 的子类，如果是的话，则会用 `android.support.v4.app.Fragment` 进行权限申请，如果不是的话，则会用 `android.app.Fragment` 进行权限申请，这样无论你用哪种 `Activity`，框架都能自动进行适配。

#### 回调生命周期与宿主保持同步

* 目前市面上大多数权限请求框架都会用单种 `Fragment` 处理权限请求回调，但是这样会导致一个问题，假设某个框架用的是 `android.support.v4.app.Fragment` 处理权限请求回调，但是你却是在 `android.app.Fragment` 发起的权限请求，又或者反过来，你用 `android.support.v4.app.Fragment` 框架用 `android.app.Fragment`，你无法把你自己的 `Fragment` 当做宿主，然后传给权限请求框架，只能通过 `fragment.getActivity()` 将 `Activity` 对象传给框架，这样 `Activity` 就成了宿主对象，这样都会导致一个生命周期不同步的问题，就是你自己的 `Fragment` 已经销毁的情况，但是框架仍会回调权限请求结果的监听器，轻则导致 `Memory leak`，重则会触发 `Exception`。

* 导致这个问题的原因是，第三方框架用的 `Fragment` 和你的 `Fragment` 实际上不是一个类型的，虽然它们的类名一样，但是它们所在的包名不一样，加上刚刚说的你只能通过 `fragment.getActivity()` 将 `Activity` 对象传给框架，这样你自己的 `Fragment` 无法和框架的 `Fragment` 之间无法形成一种有效的生命周期绑定，实际你想要的是绑定你自己 `Fragment` 的生命周期，但框架最终绑定的是 `Activity` 生命周期，这样很可能会触发 Crash，具体表现你可以看一下这个 issue：[XXPermissions/issues/365](https://github.com/getActivity/XXPermissions/issues/365)。

* 其实这个问题框架已经想到了，并且已经帮你解决了，无需你做任何处理，解决这个问题的思路是：框架会根据你传入的对象类型，自动选择最佳类型的 `Fragment`，假设你传入的宿主是 `android.support.v4.app.FragmentActivity` 或者 `android.support.v4.app.Fragment` 对象，框架内部则会创建 `android.support.v4.app.Fragment` 来接收权限请求回调，假设你传入的宿主是普通的 `Activity` 或者 `android.app.Fragment`，框架内部则会创建 `android.app.Fragment` 来接收权限请求回调，这样无论你传入的是什么宿主对象，框架都会和它的生命周期做绑定，确保在回调权限请求结果给到最外层的时候，宿主对象仍处于正常的状态。

* 这个时候你可能会跳出来说，这个不用框架我也能实现，我自己在权限回调的方法中，自己手动判断一下 `Fragment` 的状态不就行了？不就是两三句代码的事情？框架为什么搞得那么麻烦？你的想法看似有道理，但实则经不起推敲，假设你的项目有十几处地方申请了权限，那么你需要在每个回调方法都考虑这个问题，另外后续申请新的权限，你也要考虑这个问题，你能确保自己改的时候不会出现漏网之鱼？还有假设这个需求是你的同事开发的，但是只有你知道这个事情，他并不知情的情况下，你知道这种情况下可能会发生什么吗？我相信你比我更懂。你提供的解决问题方法，虽然可以暂时解决问题，但是治标不治本，究其根本是解决的思路有问题，遵循的是有洞补洞的思维，而没有想从源头堵住漏洞。又或者你原本就知道怎么彻底根治，只不过选择了最轻松的方式来处理，但这无疑是给项目埋了一颗定时炸弹。

#### 支持自定义权限申请

* 顾名思义，开发者除了可以申请框架中已支持的权限，还支持申请开发者自己定义的权限，这个功能非常强大，此功能可以满足以下场景的需求：

    1. 可以定义一些框架不支持的权限并进行申请，例如开机自启权限、桌面快捷方式权限、读取剪贴板权限、写入剪贴板权限、操作外部存储 `Android/data` 权限，特定厂商的一些权限等等适配，甚至是[蓝牙开关、WIFI 开关、定位开关](https://github.com/getActivity/XXPermissions/issues/170)等等，此处请尽情发挥你的想象力，现在只需要继承框架提供的 `DangerousPermission` 或 `SpecialPermission` 类即可实现自定义权限，要知道这个功能放在之前的版本只能通过修改框架的源码才能实现，过程十分麻烦，你不仅要研究框架的源码，又要在修改后做严格的自测，而现在不需要这样做了，框架对外提供了这个扩展接口，实现一个接口即可实现。

    2. 开发者不需要再依赖权限框架作者来适配新的权限，当 Google 发布了新的 Android 版本，并且增加了新的权限，而框架来不及适配，而你又急需申请这个新的权限，那么这个时候可以使用这个功能，率先对新权限进行适配。

#### 支持读取应用列表权限

* 这个权限非常特殊，它不属于 Android 原生的权限，而是由[工信部](http://www.taf.org.cn/StdDetail.aspx?uid=3A7D6656-43B8-4C46-8871-E379A3EA1D48&stdType=TAF)牵头，联合各大中国手机厂商搞的一个权限，目前支持手机厂商有：

|     品牌    |                版本要求           | 是否默认授予 |
| :--------: | :------------------------------: | :--------: |
|     华为   |       HarmonyOS 3.0.0 及以上版本     |      否     |
|     荣耀   |       MagicOS 6.0 及以上版本         |      否     |
|     小米   |  MIUI 13 或 HyperOS 1.0.0 及以上版本  |  MIUI 默认授予 </br> HyperOS 默认没有授予   |
|     红米   |              和小米雷同               |   和小米雷同  |
|     OPPO  |       (ColorOS 12 及以上版本 && Android 11+) 或者 </br> (ColorOS 11.1 及以上版本 && Android 12+)        |      否     |
|     VIVO  |       OriginOS 4 && Android 14      |      否      |
|     一加   |           和 OPPO 雷同               |  和 OPPO 雷同 |
|     真我   |       RealmeUI 3.0 及以上版本         |      否     |

* 目前不支持的手机厂商有：

|   品牌    |   测试的手机机型    |             测试的版本                | 是否有申请该权限的入口 |
| :------: | :---------------: | :---------------------------------: | :-----------------: |
|    魅族   |     魅族 18x      |     Flyme 9.2.3.1A && Android 11    |          是         |
|    锤子   |   坚果手机 Pro 2S  | SmartisanOS 7.2.0.2 && Android 8.1  |          否         |
|    奇虎   |  360 手机 N7 Lite |      360UI 3.0 && Android 8.1       |          否         |
|   小辣椒   |     小辣椒S6      |    小辣椒 Os 3.0 && Android 7.1.1    |          否         |

* 还有一些厂商没有列出来，并不是作者没有做测试，而是他们的系统本身就是直接用 Android 的，Android 原生目前不支持申请该权限

* 另外框架还做了一些特殊处理：

    * 在小米手机的 MIUI，但是这套机制只支持 MIUI 13 及以上的版本，然而框架内部做了一些兼容手段，目前已经适配了所有 MIUI 版本读取应用列表权限的申请。

    * 三星手机从 OneUI 5.1.1 开始支持读取应用列表权限，但是这套机制完全不支持，然而框架内部做了一些兼容手段，目前已经适配了所有 OneUI 版本读取应用列表权限的申请。

    * 魅族手机从 Flyme 8.x（不知道具体是哪个版本）开始支持读取应用列表权限，但是这套机制完全不支持，然而框架内部做了一些兼容手段，目前已经适配了 Flyme 9.x 及之后的版本读取应用列表权限的申请。

#### 新版本权限支持向下兼容

* 随着 Android 版本的不断更新，危险权限和特殊权限也在增加，那么这个时候会有一个版本兼容问题，高版本的安卓设备是支持申请低版本的权限，但是低版本的安卓设备是不支持申请高版本的权限，那么这个时候会出现一个兼容性的问题。

* 经过核查，其他权限框架选择了一种最简单粗暴的方式，就是不去做兼容，而是交给外层的调用者做兼容，需要调用者在外层先判断安卓版本，在高版本上面传入新权限给框架，而在低版本上面传入旧权限给框架，这种方式看似简单粗暴，但是开发体验差，同时也暗藏了一个坑，外层的调用者他们知道这个新权限对应着的旧权限是哪个吗？我觉得不是每个人都知道，而一旦认知出现错误，必然会导致结果出现错误。

* 我觉得最好的做法是交给框架来做，**XXPermissions** 正是那么做的，外层调用者申请高版本权限的时候，那么在低版本设备上面，会自动添加低版本的权限进行申请，举个最简单的例子，Android 11 出现的 `MANAGE_EXTERNAL_STORAGE` 新权限，如果是在 Android 10 及以下的设备申请这个权限时，框架会自动添加 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE` 进行申请，在 Android 10 及以下的设备上面，我们可以直接把 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE` 当做 `MANAGE_EXTERNAL_STORAGE` 来用，因为 `MANAGE_EXTERNAL_STORAGE` 能干的事情，在 Android 10 及以下的设备上面，要用 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE` 才能做得了。

* 所以大家在使用 **XXPermissions** 的时候，直接拿新的权限去申请就可以了，完全不需要关心新旧权限的兼容问题，框架会自动帮你做处理的，与其他框架不同的是，我更想做的是让大家一句代码搞定权限请求，框架能做到的，统统交给框架做处理。

#### 屏幕旋转场景适配

* 当系统权限申请对话框弹出后对 Activity 进行屏幕旋转，会导致权限申请回调失效，因为屏幕旋转会导致框架中的 Fragment 销毁重建，这样会导致里面的回调对象直接被回收，最终导致回调不正常。解决方案有几种，一是在清单文件中添加  `android:configChanges="orientation"` 属性，这样屏幕旋转时不会导致 Activity 和 Fragment 销毁重建，二是直接在清单文件中固定 Activity 显示的方向，但是以上两种方案都要使用框架的人处理，这样显然是不够灵活的，解铃还须系铃人，框架的问题应当由框架来解决，而 **RxPermissions** 的解决方式是给 PermissionFragment 对象设置 `fragment.setRetainInstance(true)`，这样就算屏幕旋转了，Activity 对象会销毁重建，而 Fragment 也不会跟着销毁重建，还是复用着之前那个对象，但是存在一个问题，如果 Activity 重写了 `onSaveInstanceState` 方法会直接导致这种方式失效，这样做显然只是治标不治本，而 **XXPermissions** 的方式会更直接点，在 **PermissionFragment** 绑定到 Activity 上面时，把当前 Activity 的**屏幕方向固定住**，在权限申请结束后再把**屏幕方向还原回去**。

* 在所有的权限请求框架中，只要使用了 Activity/Fragment 申请权限都会出现这个问题，因为只要用户一转动屏幕，就会导致权限回调无法正常回调，目前 XXPermissions 为数不多解决这个问题的框架，而 PermissionX 则是直接借鉴了 XXPermissions 的解决方案，详情请见 [XXPermissions/issues/49](https://github.com/getActivity/XXPermissions/issues/49) 、[PermissionX/issues/51](https://github.com/guolindev/PermissionX/issues/51)。

#### 后台申请权限场景适配

* 当我们做耗时操作之后申请权限（例如在闪屏页获取隐私协议再申请权限），在网络请求的过程中将 Activity 返回桌面去（退到后台），然后会导致权限请求是在后台状态中进行，在这个时机上就可能会导致权限申请不正常，表现为不会显示授权对话框，处理不当的还会导致崩溃，例如 [RxPermissions/issues/249](https://github.com/tbruyelle/RxPermissions/issues/249)。原因在于框架中的 PermissionFragment 在 `commit` / `commitNow` 到 Activity 的时候会做一个检测，如果 Activity 的状态是不可见时则会抛出异常，而 **RxPermissions** 正是使用了 `commitNow` 才会导致崩溃 ，使用 `commitAllowingStateLoss` / `commitNowAllowingStateLoss` 则可以避开这个检测，虽然这样可以避免崩溃，但是会出现另外一个问题，系统提供的 `requestPermissions` API 在 Activity 不可见时调用也不会弹出授权对话框，**XXPermissions** 的解决方式是将 `requestPermissions` 时机从 `onCreate` 转移到了 `onResume`，这是因为 `Activity` 和 `Fragment` 的生命周期方法是捆绑在一起的，如果 `Activity` 是不可见的，那么就算创建了 `Fragment` 也只会调用 `onCreate` 方法，而不会去调用它的 `onResume` 方法，最后当 Activity 从后台返回到前台时，不仅会触发 `Activity` 的 `onResume` 方法，也会触发 `PermissionFragment` 的 `onResume` 方法，在这个方法申请权限就可以保证最终 `requestPermissions` 调用的时机是在 `Activity` 处于可见状态的情况下。

#### 修复 Android 12 内存泄漏问题

* 最近有人跟我提了一个内存泄漏的问题 [XXPermissions/issues/133](https://github.com/getActivity/XXPermissions/issues/133) ，我经过实践后确认这个问题真实存在，但是通过查看代码堆栈，发现这个问题是系统的代码引起的，引发这个问题需要以下几个条件：

    1. 在 Android 12 的设备上使用

    2. 调用了 `Activity.shouldShowRequestPermissionRationale`

    3. 在这之后又主动在代码调用了 activity.finish 方法

* 排查的过程：经过对代码的追踪，发现代码调用栈是这样的

    * Activity.shouldShowRequestPermissionRationale

    * PackageManager.shouldShowRequestPermissionRationale（实现对象为 ApplicationPackageManager）

    * PermissionManager.shouldShowRequestPermissionRationale

    * new PermissionManager(Context context)

    * new PermissionUsageHelper(Context context)

    * AppOpsManager.startWatchingStarted

* 罪魁祸首其实是 `PermissionUsageHelper` 将 `Context` 对象作为字段持有着，并在构造函数中调用 `AppOpsManager.startWatchingStarted` 开启监听，这样 PermissionUsageHelper 对象就会被添加进 `AppOpsManager#mStartedWatchers` 集合中，这样导致在 Activity 主动调用 finish 的时候，并没有使用 `stopWatchingStarted` 来移除监听，导致 `Activity` 对象一直被 `AppOpsManager#mStartedWatchers` 集合中持有着，所以间接导致了 Activity 对象无法被系统回收。

* 针对这个问题处理也很简单粗暴，就是将在外层传入的 `Context` 参数从 `Activity` 对象给替换成 `Application` 对象即可，有人可能会说了，`Activity` 里面才有 `shouldShowRequestPermissionRationale` 方法，而 Application 里面没有这个方法怎么办？看了一下这个方法的实现，其实那个方法最终会调用 `PackageManager.shouldShowRequestPermissionRationale` 方法（**隐藏 API，但是并不在黑名单中**）里面去，所以只要能获取到 `PackageManager` 对象即可，最后再使用反射去执行这个方法，这样就能避免出现内存泄漏。

* 幸好 Google 没有将 `PackageManager.shouldShowRequestPermissionRationale` 列入到反射黑名单中，否则这次想给 Google 擦屁股都没有办法了，要不然只能用修改系统源码实现的方式，但这种方式只能等谷歌在后续的 Android 版本上面修复了，不过庆幸的是，在 `Android 12 L` 的版本之后，这个问题被修复了，[具体的提交记录可以点击此处查看](https://cs.android.com/android/_/android/platform/frameworks/base/+/0d47a03bfa8f4ca54b883ff3c664cd4ea4a624d9:core/java/android/permission/PermissionUsageHelper.java;dlc=cec069482f80019c12f3c06c817d33fc5ad6151f)，但是对于 `Android 12` 而言，这仍是一个历史遗留问题。

* 值得注意的是：XXPermissions 是目前同类框架第一款也是唯一一款修复这个问题的框架，另外针对这个问题，我还给谷歌的 [AndroidX](https://github.com/androidx/androidx/pull/435) 项目无偿提供了解决方案，目前 Merge Request 已被合入主分支，我相信通过这一举措，将解决全球近 10 亿台 Android 12 设备出现的内存泄露问题。

#### 第三方厂商兼容性优化

* 虽然我填的很多坑都是 Google 工程师给挖的，但是这里面也有国内厂商工程师的一份，他们的骚操作对比 Google 有过之而不及，真是应征了那句话，世界是一个草台班子，这里就不多说了，说多了全是泪，直接进入主题：

    1. `GET_INSTALLED_APPS` 这个权限是工信部联合各大手机厂商推出的，框架除了按照这套标准进行适配，还做了额外的适配：

        * 三星的 OneUI 5.1.1 开始支持这个权限，但不是按照工信部出台的这套标准来做的，如果只是按照工信部的标准来适配，根本无法申请这个权限，但是框架内部针对 OneUI 进行了兼容，目前所有的 OneUI 版本都支持申请该权限。

        * 小米的 MIUI 13 开始按照工信部的要求进行适配，如果是之前的机型则可能无法申请该权限，但是框架内部针对低版本的 MIUI 进行了兼容，目前所有的 MIUI 版本都支持申请该权限。

        * 魅族 Flyme 8.x 有这个读取应用列表权限的入口，但是经过测试发现，Flyme 并没有按照工信部出台的这套标准来做的，也就是在这种情况是无法申请到这个权限的，然而框架内部做了一些兼容手段，目前已经适配了 Flyme 9.x 及之后的版本读取应用列表权限的申请。

    2. 悬浮窗权限兼容 Android 6.0 及以下机型：众所周知 `SYSTEM_ALERT_WINDOW` 是 Android 6.0 才开始新增的特殊权限，在此之前的版本是不支持的，但是有一些国内厂商已经提前做了这个权限，这就会导致一个兼容问题，有部分 Android 6.0 以下的用户是无法申请悬浮窗权限，针对这个问题，框架目前进行了兼容，目前所有的机型都支持申请该权限。

    3.  `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` 权限针对小米机型的优化：请求忽略电池优化选项权限在用户授权后，发现在小米机型用代码判断权限状态还是 false，然后再次点击申请权限，却发现权限已经授予，经过排查后发现，在授权后不能立即判断这个权限的状态，否则是不准确的，需要延迟一段时间判断才是准确的，经过无数次实验，确定需要延迟 1000 毫秒才没问题，众所周知 1000 毫秒等于 1 秒，这个延迟也太大了，我都怀疑他们到底有没有自测就把代码发出来了，不过有一个好消息，后续发现澎湃 2.0 修复了这个 Bug，至于为什么会修复这个问题，我对比了一下澎湃 1.0 和澎湃 2.0，结果发现请求忽略电池优化选项权限的设置页 UI 有很大的改动，我想如果他们这辈子没有进行大改版，这个 Bug 将会永远存在。

    4. `ACCESS_NOTIFICATION_POLICY` 针对华为或者荣耀机型兼容性处理：勿扰权限在华为或者荣耀机型上面无法跳转到当前应用的勿扰权限设置页，只能跳转到所有应用的勿扰权限列表页，再找到对应开启，到这里肯定有人站出来说了，这就是送分题，你在跳转之前，先判断 `Intent` 是否存在再跳不就解决了？你的想法很好，但是行不通，我用代码判断这个 `Intent` 是存在的，也能成功跳转，但是立马给你返回失败，具体情况你可以看一下 [XXPermissions/issues/190](https://github.com/getActivity/XXPermissions/issues/190)，目前框架的解决方案是，如果当前厂商系统是 `HarmonyOS`、`MagicOS`、`EMUI` 中的任一一个，就不跳转到当前应用的勿扰权限设置页去授权，而是跳转到所有应用的勿扰权限列表页去授权，虽然这样麻烦一点，用户体验也会变差，但却是目前能想到的最好的解决方案。

* 针对以上问题，都在框架内部进行了处理，虽然无需你做任何处理，但是你仍需知道有这样一件事，所以哪里有什么岁月静好，只不过有前人替你把坑踩了个遍。虽然厂商们总是填坑少挖坑多，但是也有某些厂商在某个细节做得很不错，具体如下：

    1. 小米有一个细节做得很好，就是支持当前应用跳转到具体的权限设置页，这是由于[小米自己开放](https://dev.mi.com/docs/appsmarket/technical_docs/adaptation_FAQ/#10) 了 `miui.intent.action.APP_PERM_EDITOR` 这个隐式意图才能实现，目前框架已经进行了适配，这样用户在小米机型上面手动授权的时候，就不需要先跳转到应用详情页上面，再点一下才能进去权限设置页了。

    1. 魅族有一个细节做得很好，就是支持当前应用跳转到具体的权限设置页，这是由于魅族自己开放了 `com.meizu.safe.security.SHOW_APPSEC` 这个隐式意图才能实现，目前框架已经进行了适配，这样用户在魅族机型上面手动授权的时候，就不需要先跳转到应用详情页上面，再点一下才能进去权限设置页了。

    2. OPPO 有一个细节做得很好，不仅可以直接跳转到具体的权限设置页 ，还能支持高亮要授权的权限选项，具体接入文档可以看这里 [OPPO 应用权限受阻跳转优化适配](https://open.oppomobile.com/new/developmentDoc/info?id=12983)，目前框架已经进行了适配，这样用户在 OPPO 机型上面手动授权的时候，会自动滚动并高亮要设置的权限项，用户体验会大大提升，在此希望其他国内的厂商们跟进。

#### 支持检测代码错误

* 在框架的日常维护中，有很多人跟我反馈过框架有 Bug，但是经过排查和定位发现，这其中有 95% 的问题来自于调用者一些不规范操作导致的，这不仅对我造成很大的困扰，同时也极大浪费了很多小伙伴的时间和精力，于是我在框架中加入了很多审查元素，在 **debug 模式**、**debug 模式**、**debug 模式** 下，一旦有某些操作不符合规范，那么框架会直接抛出异常给调用者，并在异常信息中正确指引调用者纠正错误，例如：

    * 如果调用者没有传入任何权限就申请权限的话，框架会抛出异常。

    * 传入的 Context 实例不是 Activity 对象，框架会抛出异常，又或者传入的 Activity 的状态异常（已经 **Finishing** 或者 **Destroyed**），这种情况一般是在异步申请权限导致的，框架也会抛出异常，请在合适的时机申请权限，如果申请的时机无法预估，请在外层做好  Activity 状态判断再进行权限申请。

    * 如果当前项目在没有适配分区存储的情况下，申请 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE` 权限

        * 当项目的 `targetSdkVersion >= 29` 时，需要在清单文件中注册 `android:requestLegacyExternalStorage="true"` 属性，否则框架会抛出异常，如果不加会导致一个问题，明明已经获取到存储权限，但是无法在 Android 10 的设备上面正常读写外部存储上的文件。

        * 当项目的 `targetSdkVersion >= 30` 时，则不能申请 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE` 权限，而是应该申请 `MANAGE_EXTERNAL_STORAGE` 权限

        * 如果当前项目已经适配了分区存储，那么只需要在清单文件中注册一个 meta-data 属性即可： `<meta-data android:name="ScopedStorage" android:value="true" />`

    * 如果申请的权限和项目中的 **targetSdkVersion** 对不上，框架会抛出异常，是因为 **targetSdkVersion** 代表着项目适配到哪个 Android 版本，系统会自动做向下兼容，假设申请的权限是 Android 11 才出现的，但是 **targetSdkVersion** 还停留在 29，那么在某些机型上的申请，会出现授权异常的情况，也就是用户明明授权了，但是系统返回的始终是 false。

    * 如果动态申请的权限没有在 `AndroidManifest.xml` 中进行注册，框架会抛出异常，因为如果不这么做，是可以进行申请权限，但是不会出现授权弹窗，直接被系统拒绝，并且系统不会给出任何弹窗和提示，并且这个问题在每个机型上面都是**必现的**。

    * 如果动态申请的权限有在 `AndroidManifest.xml` 中进行注册，但是设定了不恰当的 `android:maxSdkVersion` 属性值，框架会抛出异常，举个例子：`<uses-permission android:name="xxxx" android:maxSdkVersion="29" />`，这样的设定会导致在 Android 11 （`Build.VERSION.SDK_INT >= 30`）及以上的设备申请权限，系统会认为这个权限没有在清单文件中注册，直接拒绝本次的权限申请，并且也是不会给出任何弹窗和提示，这个问题也是必现的。

    * 如果你同时申请了 `MANAGE_EXTERNAL_STORAGE`、`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 这三个权限，框架会抛出异常，告诉你不要同时申请这三个权限，这是因为在 Android 11 及以上设备上面，申请了 `MANAGE_EXTERNAL_STORAGE` 权限，则没有申请 `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 权限的必要，这是因为申请了 `MANAGE_EXTERNAL_STORAGE` 权限，就等于拥有了比 `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 更加强大的能力，如果硬要那么做反而适得其反，假设框架允许的情况下，会同时出现两种授权方式，一种是弹窗授权，另一种是跳页面授权，用户要进行两次授权，但是实际上面有了 `MANAGE_EXTERNAL_STORAGE` 权限就满足使用了，这个时候大家可能心中有一个疑问了，你不申请 `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 权限，Android 11 以下又没有 `MANAGE_EXTERNAL_STORAGE` 这个权限，那不是会有问题？关于这个问题大家可以放心，框架会做判断，如果你申请了 `MANAGE_EXTERNAL_STORAGE` 权限，在 Android 11 以下框架会自动添加 `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 来申请，所以在低版本下也不会因为没有权限导致的无法使用。

    * 如果你不需要上面这些检测，可通过调用 `unchecked` 方法来关闭，但是需要注意的是，我并不建议你去关闭这个检测，因为在 **release 模式** 时它是关闭状态，不需要你手动关闭，而它只在 **debug 模式** 下才会触发这些检测。

* 出现这些问题的原因是，我们对这些机制不太熟悉，而如果框架不加以限制，那么引发各种奇奇怪怪的问题出现，作为框架的作者，表示不仅你们很痛苦，作为框架作者表示也很受伤。因为这些问题不是框架导致的，而是调用者的某些操作不规范导致的。我觉得这个问题最好的解决方式是，由框架做统一的检查，因为我是框架的作者，对权限申请这块知识点有**较强的专业能力和足够的经验**，知道什么该做，什么不该做，这样就可以对这些骚操作进行一一拦截。

* 当权限申请出现问题时，你希不希望能有个人过来提醒你，告诉你哪里错了？该怎么去纠正？然而这些 XXPermissions 都做到了，在所有的权限请求框架中，我算是第一个做这件事的人，我认为**做好一个框架**不仅仅是要把功能做好，把复杂的场景处理好，更重要的是要**以人为本**，因为框架本身就是为人服务的，要做的不仅仅是解决大家的需求，还要帮助大家在这个过程中少走弯路。