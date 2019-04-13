
<div style="display: flex;flex-direction: row;justify-content: center" width="100%">
      <img src="./img/logo.png"></img>
</div>

## AgentWeb 介绍  [English](./README-ENGLISH.md)

AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活，体验请下载的 
[agentweb.apk](https://github.com/Justson/AgentWeb/raw/master/agentweb.apk)，
或者你也可以到 Google Play 里面下载 [AgentWeb](https://play.google.com/store/apps/details?id=com.just.agentweb.sample) ，
详细使用请参照上面的 Sample 。
	

## 引入


* Gradle 
   
   ```
    api 'com.just.agentweb:agentweb:4.0.3-alpha@aar' // (必选)
    api 'com.just.agentweb:filechooser:4.0.3-alpha@aar'// (可选)
    api 'com.just.agentweb:download:4.0.3-alpha@aar' // (可选)
    api 'com.github.Justson:Downloader:v4.0.3'// (可选)
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

* ### 文件下载监听
```java
protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {


		@Override
		public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
			extra.setOpenBreakPointDownload(true)
					.setIcon(R.drawable.ic_file_download_black_24dp)
					.setConnectTimeOut(6000)
					.setBlockMaxTime(2000)
					.setDownloadTimeOut(60L * 5L * 1000L)
					.setAutoOpen(true)
					.setForceDownload(false);
			return false;
		}


		@Override
		public void onBindService(String url, DownloadingService downloadingService) {
			super.onBindService(url, downloadingService);
			mDownloadingService = downloadingService;
			LogUtils.i(TAG, "onBindService:" + url + "  DownloadingService:" + downloadingService);
		}


		@Override
		public void onUnbindService(String url, DownloadingService downloadingService) {
			super.onUnbindService(url, downloadingService);
			mDownloadingService = null;
			LogUtils.i(TAG, "onUnbindService:" + url);
		}


		@Override
		public void onProgress(String url, long loaded, long length, long usedTime) {
			int mProgress = (int) ((loaded) / Float.valueOf(length) * 100);
			LogUtils.i(TAG, "onProgress:" + mProgress);
			super.onProgress(url, loaded, length, usedTime);
		}


		@Override
		public boolean onResult(String path, String url, Throwable throwable) {
			if (null == throwable) { 
				//do you work
			} else {

			}
			return false; 
		}
	};
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
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(-1, 3)
                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setPermissionInterceptor(mPermissionInterceptor) 
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) 
                .setAgentWebUIController(new UIController(getActivity())) 
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .useMiddlewareWebChrome(getMiddlewareWebChrome())
                .useMiddlewareWebClient(getMiddlewareWebClient()) 
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl() 
                .createAgentWeb()
                .ready()
                .go(getUrl()); 
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

## 更新日志

* v_4.0.3 更新
	* 部分手机下载过程中～声音一直响 [#523](https://github.com/Justson/AgentWeb/issues/523)
	* 抽离[Downloader](https://github.com/Justson/Downloader)
	* 放弃反射回调WebViewClient#methods，使用洋葱模型的Middleware代替

* v_4.0.2 更新
	* 修复断点续传时进度计算错误
	* 修复无法通过`Extra`关闭进度通知

* v_4.0.0 更新
	* `AgentWeb` 拆分出 `AgentWeb-Download` 、 `AgentWeb-FileChooser` 、`AgentWeb-core` 三个库，用户可以按需选择
	* 重新设计了 `AgentWeb-Download` 
	* 删除了 `DownloadListener` 、`DefaultMsgConfig` 以及相关API
	* 旧废弃的API，4.0.0 直接删除，不在提供兼容
	* 部分类和API重命名 
	* `Fragment`和`Activity`构建一致。[#227](https://github.com/Justson/AgentWeb/issues/227)
	* 从AgentWeb-core删除 `BaseAgentWebFragment`和`BaseAgentWebActivity` ，于Sample形式提供参考
* v_3.1.0 更新
	* `WebProgress` 进度条动画更细腻
	* 修复部分机型拍照文件大小为0情况
	* 更新了`FileUpLoadChooserImpl`
* v_3.0.0 更新
	* 加入 `MiddlewareWebChromeBase` 中间件 ，支持多个 `WebChromeClient` 
	* 加入 `MiddlewareWebClientBase`中间件 ， 支持多个 `WebViewClient` 
	* 加入了默认的错误页，并支持自定义错误页 
	* 加入 `AgentWebUIController` ，统一控制UI 
	* 支持拦截未知的页面 
	* 支持调起其他应用 
* v_2.0.1 更新
	* 支持并行下载 ， 修复 #114 #109 
* v_2.0.0 更新
	* 加入动态权限 
	* 拍照
* v_1.2.6 更新
	* 修复Android 4.4以下布局错乱 
* v_1.2.5 提示信息支持配置 
	* 提示信息支持配置 
* v_1.2.4 更新
	* 支持传入 IWebLayout ，支持下拉回弹，下拉刷新效果 
* v_1.2.3 更新
	* 新增下载结果回调 
* v_1.2.2 更新
	* 修复已知 Bug 
* v_1.2.1 更新 
	* 支持调起支付宝 ， 微信支付 
* v_1.2.0 更新
	* 全面支持全屏视频 
* v_1.1.2 更新
	* 完善功能 




## 致谢
* [SafeWebView](https://github.com/seven456/SafeWebView)

* [WebView 参考文献](https://juejin.im/post/58a037df86b599006b3fade4)


## 有问题或者有更好的建议
* [![QQ0Group][qq0groupsvg]][qq0group]
* 欢迎提 [Issues](https://github.com/Justson/AgentWeb/issues)


## 关于我
一个位于深圳的 Android 开发者 ， 如果你有问题 ，或者工作机会， 请联系 Email : xiaozhongcen@gmail.com

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
	
	

	  


   

