![](./img/logo.png)

[![License][licensesvg]][license]

### [README of English](./README_ENGLISH.md)

## AgentWeb 介绍

AgentWeb 是一个高度封装的 Android WebView ，简单易用 ， 带有进度条 、 支持文件上传 、 下载 、 简化 Javascript 通信 、 链式调用 、 加强 Web 安全的库 。让你几行代码集成一个轻量级浏览器在你的应用 。更多使用请参照上面的 sample 。 [下载 AgentWeb ](./agentweb.apk)

## 前言 
WebView 可谓是每个应用必备的一个控件了 ，但是它不是一个完善的控件 ， 比如说自身就不支持下载和上传文件以及全屏视频等等 ， 在这些地方或多或少都会踩到坑 ，AgentWeb 就是为了帮用户减少没必要踩的坑 ， 让用户轻轻松松一句话就完成所有 Web 页面的渲染与交互 。   


## AgentWeb 功能
1. 支持进度条以及自定义进度条
2. 支持文件下载以及文件下载断点续传
3. 支持下载通知形式提示进度
4. 简化 `Javascript` 通信 
5. 支持 `Android 4.4 Kitkat` 以及其他版本文件上传
6. 支持注入 `Cookies`
7. 加强 `Web` 安全
8. 支持全屏播放视频
9. 兼容低版本 `Js` 安全通信
10. 支持调起支付宝，微信支付
11. 默认支持定位
12. 支持传入 `WebLayout`（下拉回弹效果）
13. 支持自定义 `WebView` 
14. 支持 `JsBridge`


## 为什么要使用 AgentWeb ？

|     Web     |  文件下载  |  文件上传 |   Js 通信  |  断点续传  |   使用简易度 |  进度条      | 线程安全    |全屏视频|
|:-----------:|:---------:|:---------|:---------|:---------|:----------- |:-----------|:-----------|:--------|
| WebView     |  不支持    | 不支持		|  支持    |     不支持 |    麻烦      | 没有        | 不安全      |不支持|
| AgentWeb	 |  支持		| 支持		|  更简洁   |   支持    |    简洁      | 有         |  安全       |支持|	

## 引入



* Gradle 
   
   ```
   compile 'com.just.agentweb:agentweb:2.0.1'
   ```
* Maven
	
	```
	<dependency>
 	  <groupId>com.just.agentweb</groupId>
 	  <artifactId>agentweb</artifactId>
	  <version>2.0.1</version>
	  <type>pom</type>
	</dependency>
	
	```

## 腾讯X5
如果你更喜欢腾讯X5内核 ，请切换到这个仓库
[AgentWebX5](https://github.com/Justson/AgentWebX5)

## 使用
#### 为什么说它简洁易用吗 ？ 下面京东效果图 ， 只需一句话 ！

```
mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```
里面没有一句 Setting ， 甚至连 WebChromeClient 都不用配置就有进度条 。 




## 效果图 
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
```
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

* #### <del>文件上传处理<del>
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
<!--AgentWeb 是默认启动定位的 ， 请在你的 AndroidManifest 文件里面加入如下权限 。-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

* #### WebChromeClient Or WebViewClient 处理业务逻辑
```
// AgentWeb 保持了 WebView 的使用 ， 
mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mLinearLayout,new LinearLayout.LayoutParams(-1,-1) )//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
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

* #### 获取 WebView
```
 WebView mWebView=mAgentWeb.getWebCreator().get();
```

* #### 同步 Cookie
```
AgentWebConfig.syncCookie("http://www.jd.com","ID=XXXX")
```

* #### 查看 Cookies
```
String cookies=AgentWebConfig.getCookiesByUrl(targetUrl);
```
* #### 权限拦截
```
protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        //AgentWeb 在触发某些敏感的 Action 时候会回调该方法， 比如定位触发 。
        //例如 https//:www.baidu.com 该 Url 需要定位权限， 返回false ，如果版本大于等于23 ， agentWeb 会动态申请权限 ，true 该Url对应页面请求定位失败。
        //该方法是每次都会优先触发的 ， 开发者可以做一些敏感权限拦截 。
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
```

* #### AgentWeb 所需要的权限
```
 	<uses-permission android:name="android.permission.INTERNET"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
```

* #### AgentWeb 所依赖的库
```
    compile "com.android.support:design:${SUPPORT_LIB_VERSION}"
    compile "com.android.support:support-v4:${SUPPORT_LIB_VERSION}"
    SUPPORT_LIB_VERSION=25.2.0(该值会更新)
```


## 混淆
如果你的项目需要加入混淆 ， 请加入如下配置

```
-keep class com.just.library.** {
    *;
}
-dontwarn com.just.library.**

```
Java 注入类不要混淆 ， 例如 sample 里面的 AndroidInterface 类 ， 需要 Keep 。

```
-keepclassmembers class com.just.library.agentweb.AndroidInterface{ *; }
```

## 注意事项
* 支付宝使用需要引入支付宝SDK ，并在项目中依赖 ， 微信支付不需要做任何操作。
* `AgentWeb` 内部使用了 AlertDialog 需要依赖 `AppCompat` 主题 。 
* `setAgentWebParent` 不支持  `ConstraintLayout` 。


## 文档帮助
* [Wiki](https://github.com/Justson/AgentWeb/wiki)(不全)
* `Sample`(推荐，详细) 

## 更新日志
* v_2.0.1 支持并行下载 ， 修复 #114 #109 。
* v_2.0.0 加入动态权限 ，拍照等功能 。
* v_1.2.6 修复Android 4.4以下布局错乱 。
* v_1.2.5 提示信息支持配置 。
* v_1.2.4 支持传入 IWebLayout ，支持下拉回弹，下拉刷新效果 。
* v_1.2.3 新增下载结果回调 。 
* v_1.2.2 修复已知 Bug 。
* v_1.2.1 支持调起支付宝 ， 微信支付 。
* v_1.2.0 全面支持全屏视频 。
* v_1.1.2 完善功能 。


## 下个版本预告
* 加入默认的错误页 。
* 支持自定义错误页 。

## 致谢
* [SafeWebView](https://github.com/seven456/SafeWebView)

* [WebView 参考文献](https://juejin.im/post/58a037df86b599006b3fade4)


## 有问题或者有更好的建议
* [![QQ0Group][qq0groupsvg]][qq0group]
* 欢迎提 [Issues](https://github.com/Justson/AgentWeb/issues)


## 关于我
一个位于深圳的 Android 开发者 ， 如果你有问题 ， 请联系 Email : xiaozhongcen@gmail.com


[licensesvg]: https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg
[license]: https://github.com/Justson/AgentWeb/blob/master/LICENSE

[qq0groupsvg]: https://img.shields.io/badge/QQ群-599471474-fba7f9.svg
[qq0group]: http://qm.qq.com/cgi-bin/qm/qr?k=KpyfInzI2nr-Lh4StG0oh68GpbcD0vMG


### 最后如果该库对你有帮助不妨对右上角点点 Star 对我支持 ， 感谢万分 ! 当然我更喜欢你 Fork PR 成为项目贡献者 。 [AgentWeb](https://github.com/Justson/AgentWeb)  


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
	
	

	  


   

