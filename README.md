
<div style="display: flex;flex-direction: row;justify-content: center" width="100%">
      <img src="./img/logo.png"></img>
</div>

## AgentWeb 介绍

AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活，体验请下载的 
[agentweb.apk](https://github.com/Justson/AgentWeb/raw/master/agentweb.apk)，
或者你也可以到 Google Play 里面下载 [AgentWeb](https://play.google.com/store/apps/details?id=com.just.agentweb.sample) ，
详细使用请参照上面的 Sample 。
	

## 引入


* Gradle 
  
   ```
   implementation 'com.just.agentweb:agentweb:4.1.3' // (必选)
   implementation 'com.just.agentweb:filechooser:4.1.3'// (可选)
   implementation 'com.download.library:Downloader:4.1.3'// (可选)
   ```

## 相关
* [AgentWebX5](https://github.com/Justson/AgentWebX5)
* [一个炫酷的 WebView 进度条](https://github.com/Justson/CoolIndicator)
* [Downloader 一个轻量的文件下载器](https://github.com/Justson/Downloader)

	

## 使用
#### 基础用法

```java
mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(-1, -1))                
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com");

```


## 效果图 
<a href="img/img-function-list.png"><img src="img/img-function-list.png" width="30%"/></a> <a href="img/img-permission.png"><img src="img/img-permission.png" width="30%"/></a> <a href="img/img-sonic.png"><img src="img/img-sonic.png" width="30%"/></a>

<a href="img/img-scheme.png"><img src="img/img-scheme.png" width="30%"/></a> <a href="img/img-download.png"><img src="img/img-download.png" width="30%"/></a> <a href="img/img-bounce.png"><img src="img/img-bounce.png" width="30%"/></a>

<a href="img/jd.png"><img src="img/jd.png" width="30%"/></a> <a href="img/wechat pay.png"><img src="img/wechat pay.png" width="30%"/></a> <a href="img/alipay.png"><img src="img/alipay.png" width="30%"/></a>

<a href="img/js.png"><img src="img/js.png" width="30%"/></a> <a href="img/custom setting.png"><img src="img/custom setting.png" width="30%"/></a> <a href="img/video.png"><img src="img/video.png" width="30%"/></a>



* #### 调用 Javascript 方法拼接太麻烦 ？ 请看 。
```javascript
function callByAndroid(){
      console.log("callByAndroid")
  }
mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroid");
```

* #### Javascript 调 Java ?
```java
mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,this));
window.android.callAndroid();
```


* #### 事件处理
```java
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
```

* #### 跟随 Activity Or Fragment 生命周期 ， 释放 CPU 更省电 。
```java
    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause(); 
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }    
```


* #### 全屏视频播放
```
<!--如果你的应用需要用到视频 ， 那么请你在使用 AgentWeb 的 Activity 对应的清单文件里加入如下配置-->
android:hardwareAccelerated="true"
android:configChanges="orientation|screenSize"
```

* #### 定位
```
<!--AgentWeb 是默认允许定位的 ，如果你需要该功能 ， 请在你的 AndroidManifest 文件里面加入如下权限 。-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

* #### WebChromeClient 与 WebViewClient 
```java
AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout,new LinearLayout.LayoutParams(-1,-1) )
                .useDefaultIndicator()
                .setReceivedTitleCallback(mCallback)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()
                .ready()
                .go(getUrl());
private WebViewClient mWebViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
           //do you  work
        }
    };
private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };                
```
* #### 返回上一页
```java
if (!mAgentWeb.back()){
       AgentWebFragment.this.getActivity().finish();
}
```

* #### 获取 WebView
```java
	mAgentWeb.getWebCreator().getWebView();
```

* #### 查看 Cookies
```java
String cookies=AgentWebConfig.getCookiesByUrl(targetUrl);
```

* #### 同步 Cookie
```java
AgentWebConfig.syncCookie("http://www.jd.com","ID=XXXX");
```

* #### MiddlewareWebChromeBase 支持多个 WebChromeClient
```java
//略，请查看 Sample
```
* #### MiddlewareWebClientBase 支持多个 WebViewClient
```java
//略，请查看 Sample
```

* ####  清空缓存 
```java
AgentWebConfig.clearDiskCache(this.getContext());
```

* #### 权限拦截
```java
protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
```

* #### AgentWeb 完整用法
```java
 //略，请查看 Sample
```

* #### AgentWeb 所需要的权限(在你工程中根据需求选择加入权限)
```
    <uses-permission android:name="android.permission.INTERNET"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
    <uses-permission android:name="android.permission.CAMERA"></uses-permission>
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
```

* #### AgentWeb 所依赖的库
```
    compile "com.android.support:design:${SUPPORT_LIB_VERSION}" // (3.0.0开始该库可选)
    compile "com.android.support:support-v4:${SUPPORT_LIB_VERSION}"
    SUPPORT_LIB_VERSION=27.0.2(该值会更新)
```


## 混淆
如果你的项目需要加入混淆 ， 请加入如下配置

```java
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

```
Java 注入类不要混淆 ， 例如 sample 里面的 AndroidInterface 类 ， 需要 Keep 。

```java
-keepclassmembers class com.just.agentweb.sample.common.AndroidInterface{ *; }
```

## 注意事项
* 支付宝使用需要引入支付宝SDK ，并在项目中依赖 ， 微信支付不需要做任何操作。
* AgentWeb 内部使用了 `AlertDialog` 需要依赖 `AppCompat` 主题 。 
* `setAgentWebParent` 不支持  `ConstraintLayout` 。
* `mAgentWeb.getWebLifeCycle().onPause();`会暂停应用内所有`WebView` 。
* `minSdkVersion` 低于等于16以下自定义`WebView`请注意与 `JS` 之间通信安全。
* AgentWeb v3.0.0以上版本更新了包名，混淆的朋友们，请更新你的混淆配置。
* 多进程无法取消下载，[解决方案](https://github.com/Justson/AgentWeb/issues/294)。

## 常见问题

#### 修改 AgentWeb 默认的背景色 
```java
		FrameLayout frameLayout = mAgentWeb.getWebCreator().getWebParentLayout();
		frameLayout.setBackgroundColor(Color.BLACK);
```


## 文档帮助
* [Wiki](https://github.com/Justson/AgentWeb/wiki)(不全)
* `Sample`(推荐，详细) 

## [更新日志](./releasenote.md)

## 致谢
* [SafeWebView](https://github.com/seven456/SafeWebView)

* [WebView 参考文献](https://juejin.im/post/58a037df86b599006b3fade4)


## 有问题或者有更好的建议
* [![QQ0Group][qq0groupsvg]][qq0group]
* 欢迎提 [Issues](https://github.com/Justson/AgentWeb/issues)


## 关于我
一个位于深圳的 Android 开发者 ， 如果你有问题 ， 请联系 Email : xiaozhongcen@gmail.com

## 赞赏
如果你喜欢了 `AgentWeb` 的设计 ， 你也可以请作者喝一杯咖啡。

<a href="img/alipay.jpg"><img src="img/alipay.jpg" width="30%"/></a> <a href="img/wechat_pay.jpg"><img src="img/wechat_pay.jpg" width="30%"/></a> <a href="img/alipay.jpg"><img src="img/alipay.jpg" width="30%"/></a>


[licensesvg]: https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg
[license]: https://github.com/Justson/AgentWeb/blob/master/LICENSE

[qq0groupsvg]: https://img.shields.io/badge/QQ群-599471474-fba7f9.svg
[qq0group]: http://qm.qq.com/cgi-bin/qm/qr?k=KpyfInzI2nr-Lh4StG0oh68GpbcD0vMG


###  [AgentWeb](https://github.com/Justson/AgentWeb)  


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

​	

​	  




