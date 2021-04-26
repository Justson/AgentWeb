![](./img/logo.png)


## Introduction to AgentWeb


AgentWeb is an Android WebView based, extremely easy to use and powerful library. Please refer to Sample above for detailed usage. 



## AgentWeb Sample Download
AgentWeb Sample shows AgentWeb library powerful features, detailed link, please click the download experience

* [Download AgentWeb](./agentweb.apk)
* [Google Play](https://play.google.com/store/apps/details?id=com.just.agentweb.sample) 

## download


* Gradle 
  
   ```
    api 'com.just.agentweb:agentweb:4.1.9' // (Required)
    api 'com.just.agentweb:filechooser:4.1.9'// (optional)
    api 'com.download.library:Downloader:4.1.9'// (optional)
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

