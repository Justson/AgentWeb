# AgentWeb

* 前言
* 什么是AgentWeb
* 浅谈进度条
* 功能介绍
* 简单使用
* 高级使用
* AgentWeb 实现原理
* 总结

## 前言 
WebView 可谓是每个应用必备的一个控件了 ，但是谈起它的使用 ，让很多人都不是那么喜欢它 ，比如说每个 Web 页面都需要各种一大推的 setting ，好一点的可能封装成一个 BaseWebActivity 和 BaseWebFragment ，但是重复的代码总是让有洁癖的程序员不舒服 ，而且 WebView 本身功能也不是很完善 ，比如说文件下载自身就不支持 。 AgentWeb 就泥补了这些空缺 。
 
## 什么是 AgentWeb
AgentWeb 是一个完全解耦 ，灵活使用的小型浏览器 ，可以让你一句话就实现复杂的 Web 页面 。Fuck , Talk is cheap , show me the code .

```
mAgentWeb = AgentWeb.with(this)//
                .setViewGroup(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .addJavascriptInterface("hello", new HelloJs())//
                .setReceivedTitleCallback(mCallback)
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```

![京东](jd.png)
简单到你不信 ，甚至 WebChromeClient 都不用配置就能实现进度条和标题 ，达到这两点基本可以满足很多业务了 . 

## 浅谈进度条
为什么要谈一下进度条这个东西呢 ?  因为没有进度条的 WebView 页面体验实在太差了 ，反正我是受不了 ，AgentWeb 默认的进度条是一般浏览器的进度条 ，为什么采用这种进度条呢 ? 因为体验好 ，微信和QQ ，支付宝 、 UC 以及 Safari 都采用这种进度条是有他们道理的 , 我还见过应用加载 Web 页面的时候直接弹 Dialog 不可取消 ，这种恶心的做法 。没有非常必要让用户确定情况都别弹 Dialog ，特别在用户网络不好的情况下 ，加载速度变得突奇的慢 ，那么 Dialog 就一直存在 ，用户耐性不好 ，只能把你进程杀死 ， 严重直接会把你 App 删掉 。

## AgentWeb 功能介绍
1. 支持进度条以及自定义进度条
2. 支持文件下载
3. 支持文件断点续传
3. 支持下载通知形式提示进度
4. Js支持
5. 支持返回事件处理
6. 支持注入 Cookies
7. WebView 安全

## AgentWeb 简单使用
对于 Activity 使用

```
AgentWeb.with(this)//
                .createContentViewTag()//
                .useDefaultIndicator()//
                .defaultProgressBarColor()//
                .createAgentWeb()//
                .ready()//
                .go("http://www.taobao.com");

```
createContentViewTag 会自动帮你 setContentView ， 所以这里 parentView 都不需要传 ，所有 WebView Setting 都默认为常用 Setting ， 可以自定义 Setting 传入 . 

对于 Fragment 用户

```
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AgentWeb.with(this.getActivity(),this)//
        .configRootView((ViewGroup) view,new LinearLayout.LayoutParams(-1,-1))//
        .useDefaultIndicator()//
        .setWebSettings(new WebDefaultSettingsManager())//
        .createAgentWeb()//
        .ready()//
        .go("http://www.mi.com");
    }

```
Fragment 需要传入 RootView 作为 AgentWeb 的父控件 ， 其他使用基本跟 Activity 一样 .

## 高级使用
WebView 本身是一个很强大的控件 ， 我想是 Android 里最复杂的一个控件了 ， 所以封装也不是一件容易的事情 ， 考虑到封装不能把 WebView 功能全部覆盖 ， 只能通过高度解耦 ， 灵活让用户自己配置了 。 例如上面的 WebDefaultSettingsManager 虽然包含了 WebView 大部分常用的设置的 ， 但是不能满足全部用户需要 ， 这时候就需要为 WebView 设置的特定的 setting ， 比如 WebDefaultSettingsManager 不能满足我

```
if (AgentWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
```
需要设置成 
```
mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
```
那么这里建议重写 WebDefaultSettingsManager 在
`toSetting(WebView webView)` 
方法里super后覆盖 . 当然你不是那么麻烦 ， 你可以 
` mAgentWeb.getWebSettings().getWebSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);`

## AgentWeb 实现原理
AgentWeb 实现并没有多复杂 , 相反很简单 , 下面用一段简单代码显示 AgentWeb 视图上结构

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
上面仅仅只是为了显示 AgentWeb 视图结构 ， 事实上除了动态创建 ， 其他的与上面的并没有多大区别 。

AgentWeb 功能实现

*  WebSettings 负责 WebView 上的各种设置
*  IEventHandler 负责返回事件处理
*  mWebSecurityController 负责 WebView 安全检查
*  IndicatorController 进度条控制器
*  WebCreator 负责 WebView 创建和布局
*  WebListenerManager 负责 WebView 上的动作监听和回调
*  JsInterfaceHolder 负责注入 Java 对象进入 Web
*  ...

AgentWeb 把 WebView 的每一个功能都细分成一个接口去控制了 ， 使得每一个功能都灵活可配置 ， 可替换 。

## 总结
AgentWeb 要做事情就是把 WebView 完全代理出来 ， 脱离 Activity 、 Fragment xml 布局， 形成一个独立库 ， 让你一句话完成 Web 页面基本的功能 。

### 最后如果该库对你有帮助不妨对右上角点点 Star 对我支持 ， 感谢万分 ! 当然我更喜欢你 Fork PR 成为项目贡献者 .
[AgentWeb](https://github.com/Justson/AgentWeb)







 