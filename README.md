## AgentWeb 介绍
AgentWeb 是一个简洁易用的 Android Web 库 。 [App 下载体验](./agentweb.apk)

## 为什么要使用 AgentWeb ？
七个字 ，简洁易用体验好 。 最重要的是 WebView 很多东西不支持呀 ！ 坑太多 。 如下 WebView 问题 。

* 问题1 ， 比如公司官网简单 Web 页面 ， 我们只用一个简单的 WebView 加载 ， 不会做过多处理 ， 但是 Web 页面迭代太容易了 ， 现在公司业务需要 ， 要在 Web 页面里加一个下载公司 App 入口 ， 结果 Android 端点击下载无反应 。 原因 WebView 本身不支持下载！
* 问题2 ， 文件上传 ！ 这是 WebView 一大坑 。首先 WebView 本身也是不支持文件上传的 ，需要自己实现 ，好吧！ 自己实现 ，结果在Android 4.4机子上点击 input 标签不反应 ， 心里一万只曹尼玛在崩腾 。 其实啊! Android 由于历史原因对 WebView 内核进行过几次大的迭代 ，兼容性遗留下了很大问题 。 
* 问题3 ，使用 WebView 一不小心就存在各种安全隐患 ，比如说 WebView 任意代码执行漏洞、WebView 域控制不严格漏洞 等等很多 。
* 问题4 ， WebView 内存泄露 ，低版本泄露挺严重 ， 可以新建进程，退出时候使用 System.exit(0) 杀死进程释放内存 ， 但是在高版本内存泄露似乎没有了 ， 我用 leakcanary 测不到 。
* 等等 ...

## AgentWeb 功能
1. 支持进度条以及自定义进度条
2. 支持文件下载
3. 支持文件断点续传
4. 支持下载通知形式提示进度
5. 简化 Javascript 通信 
6. 支持返回事件处理
7. 支持注入 Cookies
8. WebView 安全




## 引入



* Gradle 
   
   ```
   compile 'com.just.agentweb:agentweb:1.0.2'
   ```
* Maven
	
	```
	<dependency>
 	  <groupId>com.just.agentweb</groupId>
 	  <artifactId>agentweb</artifactId>
	  <version>1.0.2</version>
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
## 效果图
![京东](jd.png)


## 总结
AgentWeb 是一个把 WebView 完全代理出来 ， 脱离 Activity 、 Fragment xml 布局 ， Android Web 库 。


### 最后如果该库对你有帮助不妨对右上角点点 Star 对我支持 ， 感谢万分 ! 当然我更喜欢你 Fork PR 成为项目贡献者 . [AgentWeb](https://github.com/Justson/AgentWeb)
	
	

	  


   

