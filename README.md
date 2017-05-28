![](./logo.png)
## AgentWeb 介绍
AgentWeb 是一个简洁易用的 Android Web 库 。 [App 下载体验](./agentweb.apk)

## 前言 
WebView 可谓是每个应用必备的一个控件了 ，但是谈起它的使用 ，让很多人都不是那么喜欢它 ，比如说每个 Web 页面都需要各种一大推的 setting ，好一点的可能封装成一个 BaseWebActivity 和 BaseWebFragment ，但是重复的代码总是让有洁癖的程序员不舒服 ，而且 WebView 本身功能也不是很完善 ， AgentWeb 就泥补了这些空缺 。

## AgentWeb 功能
1. 支持进度条以及自定义进度条
2. 支持文件下载
3. 支持文件断点续传
4. 支持下载通知形式提示进度
5. 简化 Javascript 通信 
6. 支持返回事件处理
7. 支持注入 Cookies
8. WebView 安全

## 为什么要使用 AgentWeb ？
七个字 ，简洁易用体验好 。 最重要的是 WebView 很多东西不支持呀 ， 坑太多！

|     Web     |  文件下载  |  文件上传 |   Js通信  |  断点续传  |   使用简易度 |  进度条      | 线程安全    |
|:-----------:|:---------:|:---------|:---------|:---------|:----------- |:-----------|:-----------|
| WebView     |  不支持    | 不支持		|  支持    |     不支持 |    麻烦      | 没有        | 不安全      |
| AgentWeb	 |  支持		| 支持		|  更简洁   |   支持    |    简洁      | 有         |  安全       |	



## 简洁易用
为什么说它简洁易用吗 ？ 下面京东效果图 ， 只需一句话 ！

```
mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```
你没看错 ，里面没有一句 Setting ， 甚至连 WebChromeClient 都不配置就有进度条 。 

Javascript 通信拼接太麻烦 ？ 请看 。

```
//Javascript 方法
function callByAndroid(){
      console.log("callByAndroid")
  }


```
Android 端

`mAgentWeb.getJsEntraceAccess().quickCallJs("callByAndroid");`

结果
```
05-27 08:27:04.945 469-469/com.just.library.agentweb:web I/Info: consoleMessage:callByAndroid  lineNumber:27
```



## 效果图 
![京东](jd.png)

#### 到了这里 ， 弱弱问一句 ， 你还有什么理由不使用 AgentWeb ？


## 浅谈进度条
为什么要谈一下进度条这个东西呢 ?  因为没有进度条的 WebView 页面体验实在太差了 ，AgentWeb 默认的进度条是一般浏览器的进度条 ，为什么采用这种进度条呢 ? 因为体验好 ，微信和QQ ，支付宝 、 UC 以及 Safari 都采用这种进度条是有他们道理的 , 我还见过应用加载 Web 页面的时候直接弹 Dialog 不可取消 ，这种恶心的做法 ，没有非常必要让用户确定情况都别弹 Dialog ，特别在用户网络不好的情况下 ，加载速度变得突奇的慢 ，那么 Dialog 就一直存在 ，用户耐性不好 ，只能把你进程杀死 。





## Agentweb 视图结构

```
	<FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <WebView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        </WebView>
		 <!--进度条-->
        <com.just.library.BaseIndicatorView
            android:layout_width="match_parent"
            android:layout_height="2dp"
            >

        </com.just.library.BaseIndicatorView>
    </FrameLayout>

```

 很清晰 AgentWeb 最外层是 FrameLayout ， 所以在使用 AgentWeb 的时候还需要给 FrameLayout 指定父控件（下面有使用方式） 。






## 引入



* Gradle 
   
   ```
   compile 'com.just.agentweb:agentweb:1.1.0'
   ```
* Maven
	
	```
	<dependency>
 	  <groupId>com.just.agentweb</groupId>
 	  <artifactId>agentweb</artifactId>
	  <version>1.1.0</version>
	  <type>pom</type>
	</dependency>
	
	```

## 用法

Activity 使用如下

```
mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```	

Fragment 使用如下

```
@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAgentWeb = AgentWeb.with(this.getActivity(), this)//这里需要把 Activity 、 和 Fragment  同时传入
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(-1, -1))// 设置 AgentWeb 的父控件 ， 这里的view 是 LinearLayout ， 那么需要传入 LinearLayout.LayoutParams
                .useDefaultIndicator()// 使用默认进度条
                .setReceivedTitleCallback(mCallback) //标题回调
                .setSecurityType(AgentWeb.SecurityType.strict) //注意这里开启 strict 模式 ， 设备低于 4.2 情况下回把注入的 Js 全部清空掉 ， 这里推荐使用 onJsPrompt 通信
                .createAgentWeb()//
                .ready()//
                .go(getUrl());
        
    }

```

## 混淆

```

-keep public class * extends android.webkit.WebChromeClient{
*;
}

```
Java 注入类不要混淆 ， 例如 App 里面的 HelloJs 对象 ， 需要 Keep 。

```
-keepclassmembers class com.just.library.agentweb.HelloJs{ *; }
```



## 致谢
* [360 大牛 SafeWebView](https://github.com/seven456/SafeWebView)

* [WebView 参考文献](https://juejin.im/post/58a037df86b599006b3fade4)

## 总结
AgentWeb 是一个把 WebView 完全代理出来 ， 脱离 Activity 、 Fragment xml 布局 ， 独立的 Android Web 库 。


### 最后如果该库对你有帮助不妨对右上角点点 Star 对我支持 ， 感谢万分 ! 当然我更喜欢你 Fork PR 成为项目贡献者 . [AgentWeb](https://github.com/Justson/AgentWeb)
	
	

	  


   

