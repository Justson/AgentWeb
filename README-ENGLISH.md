![](./img/logo.png)

[![Backers on Open Collective](https://opencollective.com/AgentWeb/backers/badge.svg)](#backers)
 [![Sponsors on Open Collective](https://opencollective.com/AgentWeb/sponsors/badge.svg)](#sponsors) 


## Introduction to AgentWeb


AgentWeb is an Android WebView based, extremely easy to use and powerful library. Please refer to Sample above for detailed usage. 



## AgentWeb Sample Download
AgentWeb Sample shows AgentWeb library powerful features, detailed link, please click the download experience

* [Download AgentWeb](./agentweb.apk)
* [Google Play](https://play.google.com/store/apps/details?id=com.just.agentweb.sample) 

## download


* Gradle 
   
   ```
    compile 'com.just.agentweb:agentweb:4.0.2' 
    compile 'com.just.agentweb:download:4.0.2' 
    compile 'com.just.agentweb:filechooser:4.0.2'
   ```
* Maven
	
	```
	<dependency>
 	  <groupId>com.just.agentweb</groupId>
 	  <artifactId>agentweb</artifactId>
	  <version>4.0.2</version>
	  <type>pom</type>
	</dependency>
	
	```
	
	
	
## use
#### Common use

```
mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//Incoming AgentWeb parent control, if the parent control is RelativeLayout, then the second parameter needs to be passed RelativeLayout.LayoutParams, the first parameter and the second parameter should correspond.
                .useDefaultIndicator()// use the default onProgress bar
                .defaultProgressBarColor() // Use default onProgress bar color
                .setReceivedTitleCallback(mCallback) //Set the Web page title callback
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```




## renderings
<a href="img/img-function-list.png"><img src="img/img-function-list.png" width="30%"/></a> <a href="img/img-permission.png"><img src="img/img-permission.png" width="30%"/></a> <a href="img/img-sonic.png"><img src="img/img-sonic.png" width="30%"/></a>

<a href="img/img-scheme.png"><img src="img/img-scheme.png" width="30%"/></a> <a href="img/img-download.png"><img src="img/img-download.png" width="30%"/></a> <a href="img/img-bounce.png"><img src="img/img-bounce.png" width="30%"/></a>

<a href="img/jd.png"><img src="img/jd.png" width="30%"/></a> <a href="img/wechat pay.png"><img src="img/wechat pay.png" width="30%"/></a> <a href="img/alipay.png"><img src="img/alipay.png" width="30%"/></a>

<a href="img/js.png"><img src="img/js.png" width="30%"/></a> <a href="img/custom setting.png"><img src="img/custom setting.png" width="30%"/></a> <a href="img/video.png"><img src="img/video.png" width="30%"/></a>



* #### call Javascript method stitching too much trouble? Please see.
```
// Javascript method
Function callByAndroid () {
      Console.log ("callByAndroid")
  }
// Android end
MAgentWeb.getJsEntraceAccess (). QuickCallJs ("callByAndroid");
//onResult
ConsoleMessage: callByAndroid lineNumber: 27
```

* #### Javascript call Java
```
// Android side, AndroidInterface is an injection class, which has a no parameter method: callAndroid
AddjavaObject ("android", new AndroidInterface (mAgentWeb, this));
/ / In Js will be able to pass
Window.android.callAndroid () / / call the Java layer AndroidInterface class callAndroid method
```


* #### event handling
```
@Override
		Public boolean onKeyDown (int keyCode, KeyEvent event) {
        If (mAgentWeb.handleKeyEvent (keyCode, event)) {
            Return true;
        }
        Return super.onKeyDown (keyCode, event);
    }	
```

* #### Follow the Activity Or Fragment life cycle, the release of CPU more power.

```
	@Override
    Protected void onPause () {
        MAgentWeb.getWebLifeCycle (). OnPause ();
        Super.onPause ();

    }

    @Override
    Protected void onResume () {
        MAgentWeb.getWebLifeCycle (). OnResume ();
        Super.onResume ();
    }
```

* #### <del>File upload processing</del>
```java
    @Override
    Protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        MAgentWeb.uploadFileResult (requestCode, resultCode, data);
        Super.onActivityResult (requestCode, resultCode, data);
 }
```

* #### full screen video playback
```
<! - If your application needs to use the video, then please use the AgentWeb Activity corresponding to the list file to add the following configuration ->
Android: hardwareAccelerated = "true"
Android: configChanges = "orientation | screenSize"
```

* #### positioning
```
<! - AgentWeb is the default boot location, please add the following permissions in your AndroidManifest file. ->
    <Uses-permission android: name = "android.permission.ACCESS_FINE_LOCATION" />
    <Uses-permission android: name = "android.permission.ACCESS_COARSE_LOCATION" />
```

* #### WebChromeClient Or WebViewClient handles business logic
```
// AgentWeb maintains the use of WebView,
MAgentWeb = AgentWeb.with (this) //
                .setAgentWebParent (mLinearLayout, new LinearLayout.LayoutParams (-1, -1)) //
                .useDefaultIndicator () //
                .defaultProgressBarColor ()
                .setReceivedTitleCallback (mCallback)
                .setWebChromeClient (mWebChromeClient)
                .setWebViewClient (mWebViewClient)
                .setSecutityType (AgentWeb.SecurityType.strict)
                .createAgentWeb () //
                .ready ()
                .go (getUrl ());
// WebViewClient
Private WebViewClient mWebViewClient = new WebViewClient () {
        @Override
        Public void onPageStarted (WebView view, String url, Bitmap favicon) {
           // do you work
        }
    };
    // WebChromeClient
    Private WebChromeClient mWebChromeClient = new WebChromeClient () {
        @Override
        Public void onProgressChanged (WebView view, int newProgress) {
            // do you work
        }
    };
```

* #### Get WebView
```
 WebView mWebView = mAgentWeb.getWebCreator (). Get ();
```

* #### Sync cookies
```
AgentWebConfig.syncCookies ("http://www.jd.com", "ID = XXXX")
```

* #### MiddleWareWebChromeBase supports multiple WebChromeClients
```java
// Slightly, please see Sample
```
* #### MiddleWareWebClientBase supports multiple WebViewClient
```java
// Slightly, please see Sample
```

* #### View Cookies
```
String cookies = AgentWebConfig.getCookiesByUrl (targetUrl);
```


* #### AgentWeb Complete use
```java
mAgentWeb = AgentWeb.with (this) //
                .setAgentWebParent ((LinearLayout) view, new LinearLayout.LayoutParams (-1, -1)) // The AgentWeb parent passed in.
                .setIndicatorColorWithHeight (-1, 2) / / Set the color and height of the onProgress bar, -1 is the default value, the height is 2, the unit is dp.
                .setAgentWebWebSettings (getSettings ()) // Set AgentWebSettings.
                .setWebViewClient (mWebViewClient) // WebViewClient, same as WebView, but do not get WebView calling setWebViewClient (xx) method, which will override AgentWeb DefaultWebClient and the corresponding middleware will also fail.
                .setWebChromeClient (mWebChromeClient) // WebChromeClient
                .setPermissionInterceptor (mPermissionInterceptor) / / permission to intercept 2.0.0 join.
                .setReceivedTitleCallback (mCallback) // Title callback.
                . SetSecurityType (AgentWeb.SecurityType.strict) / / strict mode Android 4.2.2 The following will give up the injection of the object, use AgentWebView did not affect.
                .addDownLoadResultListener (mDownloadListener) // Download callback
                .setAgentWebUIController (new UIController (getActivity ())) // Custom UI AgentWeb3.0.0 join.
                .setMainFrameErrorView (R.layout.agentweb_error_page, -1) / / Parameter 1 is the layout of the onResult display, parameter 2 Click refresh control ID -1 Click to refresh the entire layout Click AgentWeb 3.0.0 to join.
                .useMiddleWareWebChrome (getMiddleWareWebChrome ()) // Set up WebChromeClient middleware, support multiple WebChromeClient, AgentWeb 3.0.0 join.
                .useMiddleWareWebClient (getMiddleWareWebClient ()) / / Set WebViewClient middleware, support multiple WebViewClient, AgentWeb 3.0.0 join.
                . OpenParallelDownload () / / open parallel download, the default serial download.
                .setNotifyIcon (R.mipmap.download) // Download notification icon.
                .setOpenOtherPageWays (DefaultWebClient.OpenOtherPageWays.ASK) / / open other pages, the pop-up query users to other applications AgentWeb 3.0.0 to join.
                .interceptUnkownScheme () / / Interception Scheme AgentWeb 3.0.0 can not find the relevant page to join.
                .createAgentWeb () // Create AgentWeb.
                .ready () / / Set WebSettings.
                .go (getUrl ()); // WebView Load and display the URL page.
```

## Precautions
* Alipay need to use the introduction of Alipay SDK, and dependent on the project, WeChat payment do not need to do any operation.
* AgentWeb‘s internal use of AlertDialog depends on the `AppCompat` theme.
* `setAgentWebParent` does not support `ConstraintLayout`.
* `mAgentWeb.getWebLifeCycle (). onPause ();` Will pause all `WebView` in the application.
* `minSdkVersion` 16 or less Customize` WebView` Please be aware of communication safety with `JS`.

## ProGuard rules
If your project needs to be proguard , please add the following configuration

```
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

```
Java injection class do not proguard, such as sample inside the AndroidInterface class, need Keep.

```
-keepclassmembers class com.just.library.agentweb.AndroidInterface {*;}
```

## update log
* v_3.0.0 update
	* Add MiddlewareWebChromeBase middleware, support multiple WebChromeClient.
	* Add `MiddleWareWebClientBase` middleware, support multiple` WebViewClient`.
	* Added the default onResult page, and supports custom onResult page.
	* Join `AgentWebUIController`, unified control UI.
	* Support for blocking unknown pages.
	* Support to tune other applications.
* v_2.0.1 update
	* Support for parallel download, repair # 114 # 109.
* v_2.0.0 update
	* Join dynamic rights.
	* Take pictures.
* v_1.2.6 update
	* Fixed Android 4.4 following layout disordered.
* v_1.2.5 Prompt message support configuration.
	* Prompt message support configuration.
* v_1.2.4 update
	* Support incoming IWebLayout, support pull-down rebound, pull-down refresh effect.
* v_1.2.3 update
	* Added download onResult callback.
* v_1.2.2 update
	* Fixed known bug.
* v_1.2.1 update
	* Support tune Alipay, WeChat payment.
* v_1.2.0 update
	* Full support for full screen video.
* v_1.1.2 update
	* Improve the function .



## Thank you

* [SafeWebView](https://github.com/seven456/SafeWebView)

* [WebView Reference](https://juejin.im/post/58a037df86b599006b3fade4)


## have questions or have better suggestions
* [![QQ0Group][qq0groupsvg]][qq0group]
* Welcome [Issues](https://github.com/Justson/AgentWeb/issues)


## about me
An Android developer located in Shenzhen, if you have a better job offer available to me, please contact Email: xiaozhongcen@gmail.com

[licensesvg]: https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg
[license]: https://github.com/Justson/AgentWeb/blob/master/LICENSE

[qq0groupsvg]: https://img.shields.io/badge/QQ群-599471474-fba7f9.svg
[qq0group]: http://qm.qq.com/cgi-bin/qm/qr?k=KpyfInzI2nr-Lh4StG0oh68GpbcD0vMG

## Play reward
If you like the design of AgentWeb, you can invite the author to have a cup of coffee.

<a href="img/alipay.jpg"><img src="img/alipay.jpg" width="30%"/></a> <a href="img/wechat_pay.jpg"><img src="img/wechat_pay.jpg" width="30%"/></a> <a href="img/alipay.jpg"><img src="img/alipay.jpg" width="30%"/></a>


[Licensesvg]:https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg
[License]: https://github.com/Justson/AgentWeb/blob/master/LICENSE



## AgentWeb
AgentWeb is a WebView completely out of the Act, from the Activity, Fragment xml layout, independent of the Android Web library.

#### Finally, if the library is helpful to you, I may be grateful to the top right corner of my support, thanks! Of course, I prefer you to become a contributor to Fork PR.   [AgentWeb](https://github.com/Justson/AgentWeb)

## Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.md)].
<a href="graphs/contributors"><img src="https://opencollective.com/AgentWeb/contributors.svg?width=890&button=false" /></a>


## Backers

Thank you to all our backers! 🙏 [[Become a backer](https://opencollective.com/AgentWeb#backer)]

<a href="https://opencollective.com/AgentWeb#backers" target="_blank"><img src="https://opencollective.com/AgentWeb/backers.svg?width=890"></a>


## Sponsors

Support this project by becoming a sponsor. Your logo will show up here with a link to your website. [[Become a sponsor](https://opencollective.com/AgentWeb#sponsor)]

<a href="https://opencollective.com/AgentWeb/sponsor/0/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/1/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/2/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/3/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/4/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/5/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/6/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/7/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/8/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/AgentWeb/sponsor/9/website" target="_blank"><img src="https://opencollective.com/AgentWeb/sponsor/9/avatar.svg"></a>


[![License][licensesvg]][license]
## License
```
Copyright (C)  Justson(https://github.com/Justson/AgentWeb)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

