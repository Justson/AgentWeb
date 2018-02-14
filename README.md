![](./img/logo.png)

## AgentWeb 介绍  [English](./README-ENGLISH.md)

AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库 。详细使用请参照上面的 Sample 。


## AgentWeb Sample 下载
AgentWeb Sample 展示了 AgentWeb 库强大的功能 ， 详细请点击下面链接下载体验。

* [GitHub 仓库里APK文件](./agentweb.apk)
* [Google Play](https://play.google.com/store/apps/details?id=com.just.agentweb.sample) 

	

## 引入


* Gradle 
   
   ```
   compile 'com.just.agentweb:agentweb:3.1.0'
   ```
   
* Maven
	
	```
	<dependency>
 	  <groupId>com.just.agentweb</groupId>
 	  <artifactId>agentweb</artifactId>
	  <version>3.1.0</version>
	  <type>pom</type>
	</dependency>
	
	```
	
* 4.0.0-beta

	```
    compile 'com.just.agentweb:agentweb:4.0.0-beta'(必选)
    compile 'com.just.agentweb:download:4.0.0-beta'(可选)
    compile 'com.just.agentweb:filechooser:4.0.0-beta'(可选) 
	```	
	

## README 版本
如果你使用的是稳定版`agentweb:3.1.0`请点击下面相应的版本查看 README ，当前 README 只会显示最新的API文档和介绍（4.0.0-beta）。

- [AgentWeb 3.1.0](https://github.com/Justson/AgentWeb/tree/3.1.0)
- [AgentWeb 2.0.1](https://github.com/Justson/AgentWeb/tree/2.0.1)


## 使用
#### 基础用法

```
mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```


## 效果图 
<a href="img/img-function-list.png"><img src="img/img-function-list.png" width="30%"/></a> <a href="img/img-permission.png"><img src="img/img-permission.png" width="30%"/></a> <a href="img/img-sonic.png"><img src="img/img-sonic.png" width="30%"/></a>

<a href="img/img-scheme.png"><img src="img/img-scheme.png" width="30%"/></a> <a href="img/img-download.png"><img src="img/img-download.png" width="30%"/></a> <a href="img/img-bounce.png"><img src="img/img-bounce.png" width="30%"/></a>

<a href="img/jd.png"><img src="img/jd.png" width="30%"/></a> <a href="img/wechat pay.png"><img src="img/wechat pay.png" width="30%"/></a> <a href="img/alipay.png"><img src="img/alipay.png" width="30%"/></a>

<a href="img/js.png"><img src="img/js.png" width="30%"/></a> <a href="img/custom setting.png"><img src="img/custom setting.png" width="30%"/></a> <a href="img/video.png"><img src="img/video.png" width="30%"/></a>



* #### 调用 Javascript 方法拼接太麻烦 ？ 请看 。
```
//Javascript 方法
function callByAndroid(){
      console.log("callByAndroid")
  }
//Android 端
mAgentWeb.getJsEntraceAccess().quickCallJs("callByAndroid");
//结果
consoleMessage:callByAndroid  lineNumber:27
```

* #### Javascript 调 Java ?
```
//Android 端 ， AndroidInterface 是一个注入类 ，里面有一个无参数方法：callAndroid 
mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,this));
//在 Js 里就能通过 
window.android.callAndroid() //调用 Java 层的 AndroidInterface 类里 callAndroid 方法
```


* #### 事件处理
```
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
        mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有 WebView ， 需谨慎。
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

* #### WebChromeClient Or WebViewClient 处理业务逻辑
```
// AgentWeb 保持了 WebView 的使用 ， 
mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mLinearLayout,new LinearLayout.LayoutParams(-1,-1) )//
                .useDefaultIndicator()//
                .setReceivedTitleCallback(mCallback)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()
                .go(getUrl());
//WebViewClient
private WebViewClient mWebViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
           //do you  work
        }
    };
//WebChromeClient
private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };                
```
* #### 返回上一页
```java
if (!mAgentWeb.back()){// true表示AgentWeb处理了该事件
       AgentWebFragment.this.getActivity().finish();
}
```

* #### 获取 WebView
```java
 WebView mWebView=mAgentWeb.getWebCreator().get();
```

* ### 文件下载监听
```java
 protected DownloadListener mDownloadListener = new DownloadListener.DownloadListenerAdapter() {

        /**
         *
         * @param url                下载链接
         * @param userAgent          userAgent
         * @param contentDisposition contentDisposition
         * @param mimetype           资源的媒体类型
         * @param contentLength      文件长度
         * @param extra              下载配置 ， 用户可以通过 Extra 修改下载icon ， 关闭进度条 ， 是否强制下载。
         * @return true 表示用户处理了该下载事件 ， false 交给 AgentWeb 下载
         */
        @Override
        public boolean start(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
//            extra.setIcon(R.mipmap.app_logo).build();
            return false;
        }

        /**
         *
         * @param path 文件的绝对路径
         * @param url  下载地址
         * @param throwable    如果异常，返回给用户异常
         * @return true 表示用户处理了下载完成后续的事件 ，false 默认交给AgentWeb 处理
         */
        @Override
        public boolean result(String path, String url, Throwable throwable) {
            if (throwable == null) { //下载成功
                //do you work
            } else {//下载失败

            }
            return false; // true  不会发出下载完成的通知
        }
    };
```


* #### 同步 Cookie
```java
AgentWebConfig.syncCookie("http://www.jd.com","ID=XXXX")
```

* #### MiddleWareWebChromeBase 支持多个 WebChromeClient
```java
//略，请查看 Sample
```
* #### MiddleWareWebClientBase 支持多个 WebViewClient
```java
//略，请查看 Sample
```

* #### 查看 Cookies
```
String cookies=AgentWebConfig.getCookiesByUrl(targetUrl);
```
* #### 权限拦截
```
protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        //AgentWeb 在触发某些敏感的权限时候会回调该方法， 比如定位触发 。
        //该方法是每次都会优先触发的 ， 开发者可以做一些敏感权限拦截 。
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
```

* #### AgentWeb 完整用法
```java
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .setIndicatorColorWithHeight(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setAgentWebWebSettings(getSettings())//设置 IAgentWebSettings。
                .setWebViewClient(mWebViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                .setWebChromeClient(mWebChromeClient) //WebChromeClient
                .setPermissionInterceptor(mPermissionInterceptor) //权限拦截 2.0.0 加入。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setAgentWebUIController(new UIController(getActivity())) //自定义UI  AgentWeb3.0.0 加入。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .useMiddlewareWebChrome(getMiddlewareWebChrome()) //设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入。
                .useMiddlewareWebClient(getMiddlewareWebClient()) //设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go(getUrl()); //WebView载入该url地址的页面并显示。
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
    compile "com.android.support:design:${SUPPORT_LIB_VERSION}"//(3.0.0开始该库可选)
    compile "com.android.support:support-v4:${SUPPORT_LIB_VERSION}"
    SUPPORT_LIB_VERSION=27.0.2(该值会更新)
```


## 混淆
如果你的项目需要加入混淆 ， 请加入如下配置

```
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

```
Java 注入类不要混淆 ， 例如 sample 里面的 AndroidInterface 类 ， 需要 Keep 。

```
-keepclassmembers class com.just.library.agentweb.AndroidInterface{ *; }
```

## 注意事项
* 支付宝使用需要引入支付宝SDK ，并在项目中依赖 ， 微信支付不需要做任何操作。
* AgentWeb 内部使用了 `AlertDialog` 需要依赖 `AppCompat` 主题 。 
* `setAgentWebParent` 不支持  `ConstraintLayout` 。
* `mAgentWeb.getWebLifeCycle().onPause();`会暂停应用内所有`WebView` 。
* `minSdkVersion` 低于等于16以下自定义`WebView`请注意与 `JS` 之间通信安全。
* AgentWeb v3.0.0以上版本更新了包名，混淆的朋友们，请更新你的混淆配置。


## 文档帮助
* [Wiki](https://github.com/Justson/AgentWeb/wiki)(不全)
* `Sample`(推荐，详细) 

## 更新日志
* v_4.0.0 更新
	* `AgentWeb` 拆分出 `AgentWeb-Download` 、 `AgentWeb-FileChooser` 、`AgentWeb-core` 三个库，用户可以按需选择。
	* 重新设计了 `AgentWeb-Download` 。
	* 删除了 `DownloadListener` 、`DefaultMsgConfig` 以及相关API。
	* 旧废弃的API，4.0.0 直接删除，不在提供兼容。
	* 部分类和API重命名 。
	* `Fragment`和`Activity`构建一致。[#227](https://github.com/Justson/AgentWeb/issues/227)
* v_3.1.0 更新
	* `WebProgress` 进度条动画更细腻。
	* 修复部分机型拍照文件大小为0情况。
	* 更新了`FileUpLoadChooserImpl`。
* v_3.0.0 更新
	* 加入 `MiddleWareWebChromeBase` 中间件 ，支持多个 `WebChromeClient`  。
	* 加入 `MiddleWareWebClientBase`中间件 ， 支持多个 `WebViewClient`  。
	* 加入了默认的错误页，并支持自定义错误页 。
	* 加入 `AgentWebUIController` ，统一控制UI 。
	* 支持拦截未知的页面 。
	* 支持调起其他应用 。 
* v_2.0.1 更新
	* 支持并行下载 ， 修复 #114 #109 。
* v_2.0.0 更新
	* 加入动态权限 。
	* 拍照。 
* v_1.2.6 更新
	* 修复Android 4.4以下布局错乱 。
* v_1.2.5 提示信息支持配置 。
	* 提示信息支持配置 。
* v_1.2.4 更新
	* 支持传入 IWebLayout ，支持下拉回弹，下拉刷新效果 。
* v_1.2.3 更新
	* 新增下载结果回调 。 
* v_1.2.2 更新
	* 修复已知 Bug 。
* v_1.2.1 更新 
	* 支持调起支付宝 ， 微信支付 。
* v_1.2.0 更新
	* 全面支持全屏视频 。
* v_1.1.2 更新
	* 完善功能 。




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
	
	

	  


   

