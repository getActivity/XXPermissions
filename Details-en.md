#### Table of Contents

* [Intent Extreme Jump Fallback Mechanism](#intent-extreme-jump-fallback-mechanism)

* [Compatibility with Permission Request API Crash Issues](#compatibility-with-permission-request-api-crash-issues)

* [Avoiding System Permission Callback Null Pointer Issues](#avoiding-system-permission-callback-null-pointer-issues)

* [Automatic Permission Split Requests](#automatic-permission-split-requests)

* [Framework Completely Separates UI Layer](#framework-completely-separates-ui-layer)

* [Core Logic and Specific Permissions Completely Decoupled](#core-logic-and-specific-permissions-completely-decoupled)

* [Automatic Background Permission Adaptation](#automatic-background-permission-adaptation)

* [Support for Cross-Platform Environment Calls](#support-for-cross-platform-environment-calls)

* [Callback Lifecycle Synchronized with Host](#callback-lifecycle-synchronized-with-host)

* [Support for Custom Permission Requests](#support-for-custom-permission-requests)

* [New Version Permissions Support Backward Compatibility](#new-version-permissions-support-backward-compatibility)

* [Screen Rotation Scenario Adaptation](#screen-rotation-scenario-adaptation)

* [Background Permission Request Scenario Adaptation](#background-permission-request-scenario-adaptation)

* [Fix Android 12 Memory Leak Issue](#fix-android-12-memory-leak-issue)

* [Support for Code Error Detection](#support-for-code-error-detection)

#### Intent Extreme Jump Fallback Mechanism

* Before introducing this feature, let me ask you a question: please analyze if there's any problem with this code?

```java
Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
intent.setData("package:" + getPackageName());
startActivityForResult(intent, 1024);
```

* You might say: It's simple, this is just code to jump to the application details page, what could be wrong with it? Are you trying to trick me?

* This code seems to have no problems and runs fine, but it's actually a huge pitfall. Some manufacturers have directly removed the `ACTION_APPLICATION_DETAILS_SETTINGS` intent. Yes, you heard right - completely removed it. When this code runs on these devices, the application will crash. I'm not joking:

```text
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.settings.APPLICATION_DETAILS_SETTINGS dat=Package Name:com.xxx.xxx }
```

* If you still don't believe me, look here [Github Search `No Activity found to handle Intent  act=android.settings.APPLICATION_DETAILS_SETTINGS`](https://github.com/search?q=No+Activity+found+to+handle+Intent++act%3Dandroid.settings.APPLICATION_DETAILS_SETTINGS&type=issues):

* It's not just the `ACTION_APPLICATION_DETAILS_SETTINGS` intent; other intents have the same issue. If you don't believe me, check here [Github Search `No Activity found to handle Intent  act=android`](https://github.com/search?q=No+Activity+found+to+handle+Intent++act%3Dandroid&type=issues).

```
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) }
```

* After reading this, you might want to complain, but the problem exists, and irrational complaints never solve problems. Only rational analysis and serious thinking are the way out. The issue is that the `Intent` can't be found. The simplest and most effective solution is to check if the `Intent` exists before jumping. If it exists, then jump; if not, don't jump. But if you think that's all there is to it, you're being too simplistic. Things are rarely as simple as they seem. Non-existent `Intent` jumps will fail, but have you considered that even existing `Intent` jumps don't guarantee success? If you don't believe me, look here [Github Search `Permission Denial: starting Intent`](https://github.com/search?q=Permission+Denial%3A+starting+Intent&type=issues). Now you understand why I called it a pitfall?

```text
java.lang.SecurityException: 
Permission Denial: starting Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) cmp=xxxx/xxx }
```

* I'm not saying this to make you solve the problem, but to make you aware that it exists. Of course, the framework has already handled this issue. All the problems you can think of, the framework has already thought of and handled for you. Just one line of code, call the `XXPermissions.startPermissionActivity` method. If you're curious about how the framework implements this but too lazy to look at the source code, I've got you covered. The principle is actually very simple: when the framework gets the permission settings page, it puts all possible `Intent`s in a List collection, filters out non-existent `Intent`s, and then tries each `Intent` one by one. If one fails, it jumps to the next one, until it succeeds or there are no more `Intent`s left.

#### Compatibility with Permission Request API Crash Issues

* Before introducing this feature, let me ask you a question: please analyze if there's any problem with this code?

```java
activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1024);
```

* You might say: This is just a simple code using the system API to request permissions. What could be wrong with it? As long as you don't call it on devices below Android 6.0, it should be fine.

* Theoretically, that's correct, but theory is just theory. In reality, calling this on Android 6.0 and above devices can also cause crashes. Yes, you didn't misread - Android 6.0 and above can crash. It sounds magical that such an important system API could crash. If you don't believe me, check here [XXPermissions/issues/153](https://github.com/getActivity/XXPermissions/issues/153), [XXPermissions/issues/126](https://github.com/getActivity/XXPermissions/issues/126), [XXPermissions/issues/327](https://github.com/getActivity/XXPermissions/issues/327), [XXPermissions/issues/339](https://github.com/getActivity/XXPermissions/issues/339), or if that's not enough, look here [Github Search `act=android.content.pm.action.REQUEST_PERMISSIONS`](https://github.com/search?q=act%3Dandroid.content.pm.action.REQUEST_PERMISSIONS&type=issues). Doesn't that instantly change your understanding?

```text
android.content.ActivityNotFoundException: 
No Activity found to handle Intent { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.android.packageinstaller (has extras) }
```

* This situation can occur for several reasons:

    1. Manufacturer developers changed the package name of the `com.android.packageinstaller` system application but didn't test it properly before release (low probability)

    2. Manufacturer developers deleted the `com.android.packageinstaller` system application but didn't test it properly before release (low probability)

    3. Manufacturer developers modified Android system source code affecting the permission module but didn't test it properly before release (low probability)

    4. Manufacturers actively cut the permission request function, for example on TV devices, indirectly causing apps requesting dangerous permissions to crash when requesting permissions (low probability)

    5. Users have Root privileges and accidentally deleted the `com.android.packageinstaller` system application when streamlining system apps (higher probability)

* After analyzing the source code of `Activity.requestPermissions`, it essentially still calls `startActivityForResult`, but the `Activity` can't be found. The best solution I can think of is to use `try catch` to prevent it from crashing. You might wonder if simply using `try catch` is enough? Won't it cause other problems? Won't it cause `onRequestPermissionsResult` not to be called, leading to the permission request process getting stuck? Although this problem can't be tested, theoretically it shouldn't happen. I experimented by using an incorrect `Intent` with `startActivityForResult` and `try catch`, and found that `onActivityResult` was still normally called by the system. This proves that using `try catch` with `startActivityForResult` doesn't affect the `onActivityResult` callback. I also analyzed the source code for `Activity` callbacks and found that both `onRequestPermissionsResult` and `onActivityResult` are called by the `dispatchActivityResult` method. In that extreme case, since `onActivityResult` can be called, it proves that `dispatchActivityResult` must have been normally called by the system, and similarly, `onRequestPermissionsResult` must also be normally called by `dispatchActivityResult`, forming a complete logical loop.

* Additional test conclusion: I debugged the `Activity.requestPermissions` method and secretly modified the permission request `Intent`'s `Action` to an incorrect one, and the permission callback still worked normally.

* If this extreme situation does occur, all dangerous permission requests will necessarily go through the failure callback, but what the framework can do is: try to prevent the application from crashing and ensure it completes the entire permission request process.

#### Avoiding System Permission Callback Null Pointer Issues

* Before introducing this feature, let me ask you a question: please analyze if there's any problem with this code?

```java
public final class XxxActivity extends AppCompatActivity  {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Camera permission granted successfully");
        } else {
            System.out.println("Failed to get camera permission");
        }
    }
}
```

* You might say: This is normal handling of permission request results in the permission callback. I write it like this all the time. It looks fine to me. Are you just finding fault?

* What if I told you that the `permissions` or `grantResults` array parameters returned by the system could be null? Would you believe it? I know you probably don't, because you see that both `permissions` and `grantResults` parameters have `@NonNull` annotations (and if you look at the Activity source code, you'll also see `@NonNull` annotations), which means the system should never return null. At this point, you probably think I'm deceiving you.

* I know you don't believe me, so I've prepared evidence. Please look here [XXPermissions/issues/191](https://github.com/getActivity/XXPermissions/issues/191), [XXPermissions/issues/106](https://github.com/getActivity/XXPermissions/issues/106), [XXPermissions/issues/236](https://github.com/getActivity/XXPermissions/issues/236), or if that's not enough, look here [Github Search `NullPointerException onRequestPermissionsResult`](https://github.com/search?q=NullPointerException+onRequestPermissionsResult&type=issues);

* After reading this, what are you thinking? What is the system trying to do? Marking parameters as non-null but returning null - isn't that deceiving us? The problem exists, and irrational complaints never solve problems. Only rational analysis and serious thinking are the way out.

* Currently, the device brands reporting this issue include vivo, Xiaomi, and Lenovo, indicating that this problem is likely another pit dug by `Google` engineers. There are two approaches to solving this problem:

    1. Still use the `permissions` and `grantResults` parameters to determine the permission status: Before using them, first check the array objects for null, then continue using them.

    2. No longer use the `permissions` and `grantResults` parameters to determine the permission status: Switch to using `checkSelfPermission` to determine the permission status.

* Although both can solve the problem, there are slight differences. The framework ultimately adopts the second approach. There's a Chinese saying: "Once unfaithful, never trusted again." Since it can do such unprincipled things, we must guard against other tricks it might have, such as:

    1. The returned array objects are not null, but there are no elements in the arrays. If not checked in advance, calling them could trigger an `ArrayIndexOutOfBoundsException`.

    2. The returned array objects are not null, there are elements in the arrays, but the lengths of the `permissions` and `grantResults` arrays are different. If not checked in advance, calling them could trigger an `ArrayIndexOutOfBoundsException`.

    3. The returned array objects are not null, there are elements in the arrays, the lengths of the two arrays are normal, but the returned `grantResults` don't match reality. The user clearly granted the permission, but the array stores `-1` (`PackageManager.PERMISSION_DENIED`).

* At this point, you might suddenly realize that solving this null pointer problem isn't as simple as just adding a null check? There's so much more to it. I want to tell everyone that no matter what the problem is, I will take it seriously, because what I pursue is never just solving the problem, but finding the optimal solution among all possible solutions.

#### Automatic Permission Split Requests

* In some scenarios, you need to request multiple permissions at once, such as microphone permission and calendar permission. In this case, product managers may want to split the permissions into two separate requests to display separate explanation dialogs for each permission. This design makes feature development more complex. Without splitting the requests, you would only need to add logic to show and close the dialog before and after the permission request. Now with split requests, you can't write it that way. You have to write it separately, which means writing various nested callbacks. Just thinking about doing this makes your head spin, almost making you want to throw up last night's midnight snack.

* I understand everyone's pain and frustration, so I added a processing mechanism to the framework that automatically categorizes the permissions you pass in. For example, microphone permission is grouped into one category, calendar permission into another, and then they are split into two separate permission requests. When combined with the permission explanation interface opened by the framework, which tells you what permission is being requested, you can display the specific permission explanation dialog based on the permission. With this, the feature is completed easily and elegantly. While the iOS team is still struggling with implementation, you've already completed it and left work early. No delays, no pain, just the satisfaction of implementing the feature.

#### Framework Completely Separates UI Layer

* Some permission frameworks implement a set of permission explanation dialog UI and logic internally, requiring specific interfaces to be implemented for modification. I believe this design is unreasonable because displaying a permission explanation dialog is not a mandatory operation. Without it, calling the permission request API will still pop up the authorization box. Additionally, when it comes to UI, the UI designed within the framework inevitably cannot satisfy everyone's needs (it's a thankless task) because everyone receives different design drafts. So the best solution is for the framework not to write UI and logic internally, but to design relevant interfaces for this aspect and then hand it over to the outer layer for implementation. Of course, the framework's Demo module will also implement a case for the outer layer to reference (or directly copy the code). This not only solves the problem of inconsistent UI requirements but also reduces the size of the framework - killing two birds with one stone.

#### Core Logic and Specific Permissions Completely Decoupled

* The frameworks you see on the market that can support both dangerous permissions and special permissions have very high code coupling. This leads to a problem: for example, if you only use it to request dangerous permissions, when packaging, it will include special permissions code logic in the APK. It's like wanting to eat fried chicken, but the clerk tells you that you can only get fried chicken if you order a ten-person set meal. You think to yourself that even if you stuff yourself, you can't finish a ten-person set meal. Isn't this design clearly a trap? Although an app with more code won't "die from overeating" like a person, we shouldn't waste resources recklessly. A little waste here, a little waste there, and after development, you look at the APK size and it's 250 MB, and you have to consider size optimization. The key is that you can't optimize it because this part of the code is hardcoded in the framework, and the framework is remotely dependent. You'd have to switch to local dependencies to make changes, which means there might be bugs that increase a lot of self-testing workload. Importantly, the benefits of making changes are low, but the risks are extremely high, and you could easily end up on the layoff list while making changes.

* For this problem, the framework has a brilliant design solution: encapsulate the implementation of different permissions into objects. You pass whatever permission object you request, and objects that aren't referenced will be removed during code obfuscation. This way, when packaging the official version, there won't be redundant code, and it won't occupy extra APK size. It truly achieves "pay for what you use." You no longer have to consider whether to buy a ten-person set meal just to eat a piece of fried chicken. No need to hesitate or waver. With XXPermissions, you can buy separately, buy what you want to eat, buy as much as you want to eat, suitable for all ages, honest and fair.

* Of course, for some frameworks that don't support any special permissions or handle specific dangerous permissions separately, but simply use the system's API - using `context.requestPermissions` to request permissions and `context.checkSelfPermission` to check permissions - does this count as completely decoupled? Actually, it does, because they indeed don't directly depend on specific permissions in the core logic. But such frameworks don't meet the needs of real-world development because in a commercialized app, it's impossible to only request dangerous permissions. Need notification permission? Need installation package permission? Need floating window permission? As long as these frameworks support any special permission, this problem will exist. Of course, if they don't support it, then there's no problem. But the key is whether it's possible to both support these permissions and decouple the code? This is the key to the problem, which really tests the understanding of the framework and code design. As of now, only XXPermissions has truly achieved both support and decoupling.

#### Automatic Background Permission Adaptation

* Android 10 added [background location permission](https://developer.android.google.cn/about/versions/11/privacy/location?hl=zh-cn#background-location) and Android 13 added [background sensor permission](https://developer.android.google.cn/about/versions/13/behavior-changes-13?hl=zh-cn#body-sensors-background-permission). Don't think these background permissions are no different from ordinary dangerous permissions; the differences are very significant, and if you don't understand them clearly, it's easy to encounter bugs.

    1. Foreground and background permissions cannot be requested together. You must request foreground permissions first, then background permissions. Requesting background permissions without foreground permissions will definitely be rejected by the system, which is beyond doubt.

    2. After Android 11, there must be a certain time interval between foreground and background permission requests. That is, after splitting the two permission requests, you must ensure that there is a certain time interval between them, otherwise the request will fail. Testing shows it cannot be less than 150 milliseconds.

    3. The background location permission corresponds to two foreground location permissions: precise location permission (`ACCESS_FINE_LOCATION`) and approximate location permission (`ACCESS_COARSE_LOCATION`). In versions `Android 10 ~ Android 11`, the background location permission is anchored to the precise location permission. Only when this permission is granted can you request the background location permission. In Android 12 and later versions, the background location permission can be anchored to either the precise location permission or the approximate location permission. When either of these permissions is granted, you can request the background location permission.

* However, the framework has already handled these issues for you, and you don't need to handle them manually. The specific handling solutions are as follows:

    1. Automatically identify background permissions and their corresponding foreground permissions, then automatically split them into two permission requests: first request foreground permissions, then request background permissions.

    2. When requesting background permissions, add a time delay first, which is the 150 milliseconds mentioned earlier, then request the background permissions, thus avoiding this issue.

    3. When judging background location permissions, different Android versions are handled differently for foreground location permissions. In versions `Android 10 ~ Android 11`, precise location permission is used; in `Android 12` and later versions, either precise location permission or approximate location permission is used, ensuring the same expected effect across different Android versions.

#### Support for Cross-Platform Environment Calls

* As we all know, [FlutterActivity](https://github.com/flutter/flutter/blob/03ef1ba910cac387f7b2af8ab09ca955d3974663/engine/src/flutter/shell/platform/android/io/flutter/embedding/android/FlutterActivity.java), [ComposeActivity](https://github.com/androidx/androidx/blob/8d08d42d60f7cc7ec0034d0b7ff6fd953516d96a/emoji2/emoji2-emojipicker/samples/src/main/java/androidx/emoji2/emojipicker/samples/ComposeActivity.kt), [UnityPlayerActivity](https://github.com/FabianTerhorst/PokemonGo/blob/d511b045f1492e0bae71778ef528f3d768d218cd/java/com/unity3d/player/UnityPlayerActivity.java), [Cocos2dxActivity](https://github.com/irontec/Ikasesteka/blob/master/Ikasesteka/cocos2d/cocos/platform/android/java/src/org/cocos2dx/lib/Cocos2dxActivity.java) are all subclasses of Activity, but they are not subclasses of [FragmentActivity](https://github.com/androidx/androidx/blob/8d08d42d60f7cc7ec0034d0b7ff6fd953516d96a/fragment/fragment/src/main/java/androidx/fragment/app/FragmentActivity.java). Their inheritance relationships are as follows:

    1. `FlutterActivity extends Activity`

    2. `ComposeActivity extends ComponentActivity extends Activity`

    3. `UnityPlayerActivity extends Activity`

    4. `Cocos2dxActivity extends Activity`

    5. `FragmentActivity extends ComponentActivity extends Activity`

* This creates a problem: some permission request frameworks use a transparent `Fragment` to get permission request callbacks. If the permission request framework uses `androidx.fragment.app.Fragment`, then it must require the outer layer to pass in an `androidx.fragment.app.FragmentActivity` object. If the `Activity` you're using is not a subclass of `androidx.fragment.app.FragmentActivity`, what should you do? Simple, you might say, just modify the current `Activity` to directly inherit from `androidx.fragment.app.FragmentActivity`, right? But what if your current `Activity` must inherit from `FlutterActivity`, `ComposeActivity`, `UnityPlayerActivity`, or `Cocos2dxActivity`? What should you do then? Modify their source code? Or modify the permission framework's source code? Whichever solution you choose, the cost of adaptation will be very high and difficult to maintain in the future. This is neither realistic nor scientific. Do you suddenly feel like heaven has blocked your path? Is writing permission request API code by hand the only way to implement permission requests?

* Actually, the framework has already thought of this problem and has solved it for you without requiring any handling on your part. The specific implementation principle is: the framework will determine if the `Activity` object you pass in is a subclass of `androidx.fragment.app.FragmentActivity`. If it is, it will use `androidx.fragment.app.Fragment` for permission requests; if not, it will use `android.app.Fragment` for permission requests. This way, no matter which type of `Activity` you use, the framework can automatically adapt.

#### Callback Lifecycle Synchronized with Host

* Most permission request frameworks on the market use a single type of `Fragment` to handle permission request callbacks, but this leads to a problem. Suppose a framework uses `androidx.fragment.app.Fragment` to handle permission request callbacks, but you initiated the permission request in `android.app.Fragment`, or vice versa, you used `androidx.fragment.app.Fragment` but the framework used `android.app.Fragment`. You can't pass your own `Fragment` as the host to the permission request framework; you can only pass the `Activity` object to the framework through `fragment.getActivity()`. This makes the `Activity` the host object, leading to a lifecycle asynchronization problem: your own `Fragment` may have been destroyed, but the framework will still callback the permission request result listener, causing `Memory leak` at best and triggering `Exception` at worst.

* The reason for this problem is that the `Fragment` used by the third-party framework and your `Fragment` are not actually the same type. Although they have the same class name, they are in different packages. Plus, as just mentioned, you can only pass the `Activity` object to the framework through `fragment.getActivity()`, so your own `Fragment` cannot form an effective lifecycle binding with the framework's `Fragment`. What you actually want is to bind to your own `Fragment`'s lifecycle, but the framework ultimately binds to the `Activity`'s lifecycle, which can easily trigger a crash. You can see the specific manifestation in this issue: [XXPermissions/issues/365](https://github.com/getActivity/XXPermissions/issues/365).

* Actually, the framework has already thought of this problem and has solved it for you without requiring any handling on your part. The approach to solving this problem is: the framework will automatically select the best type of `Fragment` based on the type of object you pass in. If the host you pass in is an `androidx.fragment.app.FragmentActivity` or `androidx.fragment.app.Fragment` object, the framework will internally create an `androidx.fragment.app.Fragment` to receive permission request callbacks. If the host you pass in is a regular `Activity` or `android.app.Fragment`, the framework will internally create an `android.app.Fragment` to receive permission request callbacks. This way, no matter what host object you pass in, the framework will bind to its lifecycle, ensuring that when the permission request result is called back to the outermost layer, the host object is still in a normal state.

* At this point, you might jump in and say, I can implement this without the framework. I can manually check the `Fragment`'s state in the permission callback method. Isn't it just a matter of two or three lines of code? Why does the framework make it so complicated? Your idea seems reasonable but doesn't stand up to scrutiny. If your project requests permissions in a dozen places, you would need to consider this issue in every callback method. Additionally, when requesting new permissions in the future, you would also need to consider this issue. Can you ensure that you won't miss anything when making changes? And what if this requirement was developed by your colleague, but only you know about this issue, and they are unaware? Do you know what might happen in that case? I believe you understand better than I do. The solution you provided, although it can temporarily solve the problem, treats the symptoms but not the root cause. The fundamental issue is that the approach to solving the problem is flawed, following a patch-the-hole mentality rather than thinking about blocking the leak at the source. Or perhaps you already knew how to completely cure it but chose the easiest way to handle it, which undoubtedly plants a time bomb in the project.

#### Support for Custom Permission Requests

* As the name suggests, developers can not only request permissions already supported by the framework but also request permissions they define themselves. This feature is very powerful and can meet the needs of the following scenarios:

    1. You can define and request permissions not supported by the framework, such as boot auto-start permission, desktop shortcut permission, read clipboard permission, write clipboard permission, operate external storage `Android/data` permission, specific manufacturer permissions, and even [Bluetooth switch, WIFI switch, location switch](https://github.com/getActivity/XXPermissions/issues/170), etc. Let your imagination run wild here. Now you only need to inherit the `DangerousPermission` or `SpecialPermission` class provided by the framework to implement custom permissions. In previous versions, this could only be achieved by modifying the framework's source code, which was very cumbersome. You not only had to study the framework's source code but also had to do strict self-testing after modification. Now you don't need to do that anymore; the framework provides this extension interface, and implementing one interface can achieve it.

    2. Developers no longer need to rely on the permission framework author to adapt new permissions. When Google releases a new Android version with new permissions, and the framework hasn't had time to adapt, but you urgently need to request this new permission, you can use this feature to adapt the new permission first.

#### New Version Permissions Support Backward Compatibility

* With the continuous update of the Android version, dangerous permissions and special permissions are also increasing, so there will be a version compatibility problem at this time. Higher version Android devices support applying for lower version permissions, but lower version Android devices do not support If you apply for a higher version of the permission, then there will be a compatibility problem at this time.

* After verification, other permission frameworks chose the simplest and rude way, which is not to do compatibility, but to the caller of the outer layer for compatibility. The caller needs to judge the Android version in the outer layer first, and upload it on the higher version. Enter new permissions to the framework, and pass the old permissions to the framework on the lower version. This method seems simple and rude, but the development experience is poor. At the same time, it also hides a pit. The outer callers know that the new permissions correspond to Which is the old permission of ? I think not everyone knows it, and once the cognition is wrong, it will inevitably lead to wrong results.

* I think the best way is to leave it to the framework. **XXPermissions** does exactly that. When the outer caller applies for a higher version of the permission, then the lower version of the device will automatically add the lower version of the permission. To apply, to give the simplest example, the new `MANAGE_EXTERNAL_STORAGE` permission that appeared in Android 11, if it is applied for this permission on Android 10 and below devices, the framework will automatically add `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` to apply, in Android On Android 10 and below devices, we can directly use `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` as `MANAGE_EXTERNAL_STORAGE`, because what `MANAGE_EXTERNAL_STORAGE` can do, on Android 10 and below devices, we need to use `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` Only then can it be done.

* So when you use **XXPermissions**, you can directly apply for new permissions. You don’t need to care about the compatibility of old and new permissions. The framework will automatically handle it for you. Unlike other frameworks, What I want to do more is to let everyone handle the permission request with a single code, and let the framework handle everything that the framework can do.

#### Screen Rotation Scenario Adaptation

* When the system permission request dialog pops up and the Activity is rotated, it will cause the permission request callback to fail because screen rotation causes the Fragment in the framework to be destroyed and recreated, leading to the callback object being directly recycled and ultimately causing the callback to be abnormal. There are several solutions: one is to add the `android:configChanges="orientation"` attribute in the manifest file so that screen rotation won't cause the Activity and Fragment to be destroyed and recreated; another is to directly fix the display direction of the Activity in the manifest file. But both of these solutions require the framework user to handle them, which is obviously not flexible enough. The problem should be solved by those who created it, and framework problems should be solved by the framework. **RxPermissions**' solution is to set `fragment.setRetainInstance(true)` on the PermissionFragment object, so even if the screen rotates, the Activity object will be destroyed and recreated, but the Fragment won't be destroyed and recreated, still reusing the previous object. But there's a problem: if the Activity overrides the `onSaveInstanceState` method, it will directly cause this approach to fail. This approach obviously only treats the symptoms but not the root cause. **XXPermissions**' approach is more direct: when the **PermissionFragment** is bound to the Activity, it **fixes the screen orientation** of the current Activity, and after the permission request is completed, it **restores the screen orientation**.

* In all permission request frameworks, as long as they use Activity/Fragment to request permissions, this problem will occur because as soon as the user rotates the screen, it will cause the permission callback to not callback normally. Currently, XXPermissions is one of the few frameworks that solves this problem, while PermissionX directly borrowed XXPermissions' solution. For details, see [XXPermissions/issues/49](https://github.com/getActivity/XXPermissions/issues/49) and [PermissionX/issues/51](https://github.com/guolindev/PermissionX/issues/51).

#### Background Permission Request Scenario Adaptation

* When we apply for permissions after doing time-consuming operations (such as obtaining the privacy agreement on the splash screen page and then applying for permissions), the activity will be returned to the desktop (retired to the background) during the network request process, and then the permission request will be in the background state At this time, the permission application may be abnormal, which means that the authorization dialog box will not be displayed, and if it is not handled properly, it will cause a crash, such as [ RxPermissions/issues/249](https://github.com/tbruyelle/RxPermissions/issues/249). The reason is that the PermissionFragment in the framework will do a detection when `commit`/ `commitNow` arrives at the Activity. If the state of the Activity is invisible, an exception will be thrown, and **RxPermissions** It is the use of `commitNow` that will cause the crash, and the use of `commitAllowingStateLoss`/ `commitNowAllowingStateLoss` can avoid Enable this detection, although this can avoid crashes, but there will be another problem. The `requestPermissions` API provided by the system will not pop up the authorization dialog when the Activity is not visible. **XXPermissions** was resolved by moving the `requestPermissions` timing from `onCreate` to `onResume`, because `Activity` It is bundled with the life cycle method of `Fragment`. If `Activity` is invisible, then even if `Fragment` is created, only The `onCreate` method will be called instead of its `onResume` method. Finally, when the Activity returns from the background to the foreground, not only will the `onResume` method of `Activity` be triggered, but also the `onResume` method of `PermissionFragment` will be triggered. Applying for permissions in this method can ensure that the timing of the final `requestPermissions` call is when `Activity` is in a visible state.

#### Fix Android 12 Memory Leak Issue

* Recently someone asked me about a memory leak[ XXPermissions/issues/133 ](https://github.com/getActivity/XXPermissions/issues/133). After practice, I confirmed that this problem really exists, but by looking at the code stack, I found that this problem is caused by the code of the system, which caused this problem The following conditions are required:

    1. Use on Android 12 devices

    2. Called `Activity.shouldShowRequestPermissionRationale`

    3. After that, the activity.finish method is actively called in the code

* The process of troubleshooting: After tracing the code, it is found that the code call stack is like this

    * Activity.shouldShowRequestPermissionRationale

    * PackageManager.shouldShowRequestPermissionRationale (implementation object is ApplicationPackageManager)

    * PermissionManager.shouldShowRequestPermissionRationale

    * new PermissionManager(Context context)

    * new PermissionUsageHelper(Context context)

    * AppOpsManager.startWatchingStarted

* The culprit is that `PermissionUsageHelper` holds the `Context` object as a field, and calls `AppOpsManager.startWatchingStarted` in the constructor to start monitoring, so that PermissionUsageHelper The object will be added to the `AppOpsManager#mStartedWatchers` collection, so that when the Activity actively calls finish, it does not use `stopWatchingStarted` to remove the listener, resulting in  object has been held in the `AppOpsManager#mStartedWatchers` collection, which indirectly causes the Activity object to be unable to be recycled by the system.

* The solution to this problem is also very simple and rude, which is to replace the `Context` parameter passed in from the outer layer from the `Activity` object to the `Application` object That's right, some people may say, `Activity` only has the `shouldShowRequestPermissionRationale` method, but what should I do if there is no such method in Application? After looking at the implementation of this method, in fact, that method will eventually call the `PackageManager.shouldShowRequestPermissionRationale` method (**Hidden API, but not blacklisted**), so as long as you can get `PackageManager` object, and finally use reflection to execute this method, so that memory leaks can be avoided.

* Fortunately, Google did not include `PackageManager.shouldShowRequestPermissionRationale` in the reflection blacklist, otherwise there is no way to clean up this mess this time, or it can only be implemented by modifying the system source code, but this way I can only wait for Google to fix it in the subsequent Android version, but fortunately, after the `Android 12 L` version, this problem has been fixed, [ The specific submission record can be viewed here](https://cs.android.com/android/_/android/platform/frameworks/base/+/0d47a03bfa8f4ca54b883ff3c664cd4ea4a624d9:core/java/android/permission/PermissionUsageHelper.java;dlc=cec069482f80019c12f3c06c817d33fc5ad6151f), but for `Android 12` This is still a historical issue.

* It is worth noting that XXPermissions is the first and only framework of its kind to fix this problem. In addition, I also provided a solution to Google's [AndroidX](https://github.com/androidx/androidx/pull/435) project for free. At present, Merge Request has been merged into the main branch. I believe that through this move, the memory leak problem of nearly 1 billion Android 12 devices around the world will be solved.

#### Support for Code Error Detection

* In the daily maintenance of the framework, many people have reported to me that there are bugs in the framework, but after investigation and positioning, it is found that 95% of the problems come from some irregular operations of the caller, which not only caused great harm to me At the same time, it also greatly wasted the time and energy of many friends, so I added a lot of review elements to the framework, in **debug mode**, **debug mode**, **debug mode**, once some operations do not conform to the specification, the framework will directly throw an exception to the caller, and correctly guide the caller to correct the error in the exception information, for example:

    * If the caller applies for permissions without passing in any permissions, the framework will throw an exception.

    * The incoming Context instance is not an Activity object, the framework will throw an exception, or the state of the incoming Activity is abnormal (already **Finishing** or **Destroyed**), in this case Generally, it is caused by applying for permissions asynchronously, and the framework will also throw an exception. Please apply for permissions at the right time. If the timing of the application cannot be estimated, please make a good judgment on the activity status in the outer layer before applying for permissions.

    * If the current project is not adapted to partition storage, apply for `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions

        * When the project's `targetSdkVersion >= 29`, you need to register the `android:requestLegacyExternalStorage="true"` attribute in the manifest file, otherwise the framework will throw an exception. If you don't add it, it will cause a problem, obviously it has been obtained Storage permissions, but the files on the external storage cannot be read and written normally on the Android 10 device.

        * When the project's `targetSdkVersion >= 30`, you cannot apply for `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions, but should apply for `MANAGE_EXTERNAL_STORAGE` permissions

        * If the current project is already adapted to partitioned storage, you only need to register a meta-data attribute in the manifest file: `<meta-data android:name="ScopedStorage" android:value="true"/>`

    * If the requested permissions do not match the **targetSdkVersion** in the project, the framework will throw an exception because **targetSdkVersion** represents which Android version the project is adapted to, and the system will Automatically do backward compatibility, assuming that the application permission only appeared on Android 11, but **targetSdkVersion** is still at 29, then the application on some models will have authorization exceptions, and also That is, the user has clearly authorized, but the system always returns false.

    * If the dynamically applied permission is not registered in `AndroidManifest.xml`, the framework will throw an exception, because if you don’t do this, you can apply for permission, but there will be no authorization pop-up window, and it will be directly rejected by the system, and the system will not give any pop-up windows and prompts, and this problem is **Must-have** on every phone model.

    * If the dynamic application permission is registered in `AndroidManifest.xml`, but an inappropriate `android:maxSdkVersion` attribute value is set, the framework will throw an exception, for example: `<uses-permission android:name="xxxx" android:maxSdkVersion="29"/>`, such a setting will lead to the application of permissions on Android 11 ( `Build.VERSION.SDK_INT >= 30`) and above devices, the system will think that this permission is not registered in the manifest file, and directly reject it This permission application will not give any pop-up windows and prompts. This problem is also inevitable.

    * If you apply for the three permissions `MANAGE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` at the same time, the framework will throw an exception, telling you not to apply at the same time These three permissions are because on Android 11 and above devices, if `MANAGE_EXTERNAL_STORAGE` permission is applied, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` The necessity of permission, this is because applying for `MANAGE_EXTERNAL_STORAGE` permission is equivalent to possessing a more powerful ability than `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE`, If you insist on doing that, it will be counterproductive. Assuming that the framework allows it, there will be two authorization methods at the same time, one is pop-up authorization, and the other is page-jump authorization. The user needs to authorize twice, but in fact there are `MANAGE_EXTERNAL_STORAGE` permission is sufficient for use, at this time you may have a question in mind, you do not apply for `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` permission, Android There is no `MANAGE_EXTERNAL_STORAGE` permission below 11, isn't there a problem? Regarding this issue, you can rest assured that the framework will make judgments. If you apply for the `MANAGE_EXTERNAL_STORAGE` permission, the framework below Android 11 will automatically add `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` to apply, so it will not be unusable due to lack of permissions under lower versions.

    * If you don't need the above detections, you can turn them off by calling the `unchecked` method, but it should be noted that I don't recommend you to turn off this detection, because in **release mode** When it is closed, you don't need to close it manually, and it only triggers these detections under **debug mode**.

* The reason for these problems is that we are not familiar with these mechanisms, and if the framework does not impose restrictions, then various strange problems will arise. As the author of the framework, not only you are suffering, but also as the framework author. Injuried. Because these problems are not caused by the framework, but by some irregular operations of the caller. I think the best way to solve this problem is to do a unified inspection by the framework, because I am the author of the framework, and I have **Strong professional ability and sufficient experience** knowledge about permission application, and know what to do and what not to do. It should be done, In this way, these irregular operations can be intercepted one by one.

* When there is a problem with the permission application, do you hope that someone will come to remind you and tell you what is wrong? How to correct it? However, these XXPermissions have done it. Among all the permission request frameworks, I am the first person to do this. I think **make a frame** is not only to do a good job of function, but also to make complex The scene is handled well, and more importantly, **people oriented**, because the framework itself serves people, and what we need to do is not only to solve everyone's needs, but also to help everyone avoid detours in the process.