## AgentWeb 介绍
AgentWeb 是一个高度封装的 WebView  ，简单易用 ， 带有进度条 、 支持文件上传 、 下载 、 简化 Javascript 通信 ，加强 Web 安全的库 。


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
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(-1, -1))// 设置 AgentWeb 的父控件 ， 这里的view 是 LinearLayout ， 那么需要传入 LinearLayout.LayoutParams ， -1 ， 表示满屏也就是 MATCH_PARENT
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
AgentWeb 是一个高度解耦的小型浏览器 ，里面的每一个组件都支持自定义传入 。

### 最后如果该库对你有帮助不妨对右上角点点 Star 对我支持 ， 感谢万分 ! 当然我更喜欢你 Fork PR 成为项目贡献者 . [AgentWeb](https://github.com/Justson/AgentWeb)
	
	

	  


   

