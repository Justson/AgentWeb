package com.just.agentweb;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;
import java.util.Map;


/**
 * https://github.com/Justson/AgentWeb
 * Author Just -- cxz
 */
public final class AgentWeb {
    /**
     * AgentWeb TAG
     */
    private static final String TAG = AgentWeb.class.getSimpleName();
    /**
     * 使用 AgentWeb 的 Activity
     */
    private Activity mActivity;
    /**
     * 承载 WebParentLayout 的 ViewGroup
     */
    private ViewGroup mViewGroup;
    /**
     * 负责创建布局 WebView ，WebParentLayout  Indicator等。
     */
    private WebCreator mWebCreator;
    /**
     * 管理 WebSettings
     */
    private IAgentWebSettings mAgentWebSettings;
    /**
     * AgentWeb
     */
    private AgentWeb mAgentWeb = null;
    /**
     * IndicatorController 控制Indicator
     */
    private IndicatorController mIndicatorController;
    /**
     * Client 传过来的 WebChromeClient
     */
    private WebChromeClient mWebChromeClient;
    /**
     * Client 传过来的 WebViewClient
     */
    private WebViewClient mWebViewClient;
    /**
     * 是否启动进度条
     */
    private boolean enableIndicator;
    /**
     * Fragment
     */
    private Fragment mFragment;
    /**
     * IEventHandler 处理WebView相关返回事件
     */
    private IEventHandler mIEventHandler;
    /**
     * WebView 注入对象
     */
    private ArrayMap<String, Object> mJavaObjects = new ArrayMap<>();
    /**
     * 用于表示当前在 Fragment 使用还是 Activity 上使用
     */
    private int TAG_TARGET = 0;
    /**
     * WebListenerManager
     */
    private WebListenerManager mWebListenerManager;
    /**
     * 安全把控
     */
    private WebSecurityController<WebSecurityCheckLogic> mWebSecurityController = null;
    /**
     * 检查逻辑
     */
    private WebSecurityCheckLogic mWebSecurityCheckLogic = null;
    /**
     * WebChromeClient
     */
    private WebChromeClient mTargetChromeClient;
    /**
     * 安全类型
     */
    private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
    /**
     * Activity  标识
     */
    private static final int ACTIVITY_TAG = 0;
    /**
     * Fragment 标识
     */
    private static final int FRAGMENT_TAG = 1;
    /**
     * AgentWeb 注入对象
     */
    private AgentWebJsInterfaceCompat mAgentWebJsInterfaceCompat = null;
    /**
     * JsAccessEntrace 提供快速的JS调用
     */
    private JsAccessEntrace mJsAccessEntrace = null;
    /**
     * URL Loader ， 封装了 mWebView.loadUrl(url) reload() stopLoading（） postUrl()等方法
     */
    private IUrlLoader mIUrlLoader = null;
    /**
     * WebView 生命周期 ， 适当的释放CPU
     */
    private WebLifeCycle mWebLifeCycle;
    /**
     * Video 视屏播放类
     */
    private IVideo mIVideo = null;
    /**
     * WebViewClient 辅助控制开关
     */
    private boolean webClientHelper = true;
    /**
     * PermissionInterceptor 权限拦截
     */
    private PermissionInterceptor mPermissionInterceptor;
    /**
     * 是否拦截未知的scheme， 用于 DefaultWebClient
     */
    private boolean isInterceptUnkownScheme = false;
    /**
     * 该变量控制了是否咨询用户页面跳转，或者直接拦截
     */
    private int openOtherAppWays = -1;
    /**
     * MiddlewareWebClientBase WebViewClient 中间件
     */
    private MiddlewareWebClientBase mMiddleWrareWebClientBaseHeader;
    /**
     * MiddlewareWebChromeBase WebChromeClient 中间件
     */
    private MiddlewareWebChromeBase mMiddlewareWebChromeBaseHeader;
    /**
     * 事件拦截
     */
    private EventInterceptor mEventInterceptor;


    private JsInterfaceHolder mJsInterfaceHolder = null;


    private AgentWeb(AgentBuilder agentBuilder) {
        TAG_TARGET = agentBuilder.tag;
        this.mActivity = agentBuilder.mActivity;
        this.mFragment = agentBuilder.mFragment;
        this.mViewGroup = agentBuilder.mViewGroup;
        this.mIEventHandler = agentBuilder.mIEventHandler;
        this.enableIndicator = agentBuilder.enableIndicator;
        mWebCreator = agentBuilder.mWebCreator == null ? configWebCreator(agentBuilder.v, agentBuilder.index, agentBuilder.mLayoutParams, agentBuilder.mIndicatorColor, agentBuilder.height_dp, agentBuilder.mWebView, agentBuilder.mWebLayout) : agentBuilder.mWebCreator;
        mIndicatorController = agentBuilder.mIndicatorController;
        this.mWebChromeClient = agentBuilder.mWebChromeClient;
        this.mWebViewClient = agentBuilder.mWebViewClient;
        mAgentWeb = this;
        this.mAgentWebSettings = agentBuilder.mAgentWebSettings;

        if (agentBuilder.mJavaObject != null && !agentBuilder.mJavaObject.isEmpty()) {
            this.mJavaObjects.putAll((Map<? extends String, ?>) agentBuilder.mJavaObject);
            LogUtils.i(TAG, "mJavaObject size:" + this.mJavaObjects.size());

        }
        this.mPermissionInterceptor = agentBuilder.mPermissionInterceptor == null ? null : new PermissionInterceptorWrapper(agentBuilder.mPermissionInterceptor);
        this.mSecurityType = agentBuilder.mSecurityType;
        this.mIUrlLoader = new UrlLoaderImpl(mWebCreator.create().getWebView(), agentBuilder.mHttpHeaders);
        if (this.mWebCreator.getWebParentLayout() instanceof WebParentLayout) {
            WebParentLayout mWebParentLayout = (WebParentLayout) this.mWebCreator.getWebParentLayout();
            mWebParentLayout.bindController(agentBuilder.mAgentWebUIController == null ? AgentWebUIControllerImplBase.build() : agentBuilder.mAgentWebUIController);
            mWebParentLayout.setErrorLayoutRes(agentBuilder.errorLayout, agentBuilder.reloadId);
            mWebParentLayout.setErrorView(agentBuilder.errorView);
        }
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.getWebView());
        mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.getWebView(), this.mAgentWeb.mJavaObjects, this.mSecurityType);
        this.webClientHelper = agentBuilder.webClientHelper;
        this.isInterceptUnkownScheme = agentBuilder.isInterceptUnkownScheme;
        if (agentBuilder.openOtherPage != null) {
            this.openOtherAppWays = agentBuilder.openOtherPage.code;
        }
        this.mMiddleWrareWebClientBaseHeader = agentBuilder.header;
        this.mMiddlewareWebChromeBaseHeader = agentBuilder.mChromeMiddleWareHeader;
        init();
    }


    /**
     * @return PermissionInterceptor 权限控制者
     */
    public PermissionInterceptor getPermissionInterceptor() {
        return this.mPermissionInterceptor;
    }


    public WebLifeCycle getWebLifeCycle() {
        return this.mWebLifeCycle;
    }


    public JsAccessEntrace getJsAccessEntrace() {

        JsAccessEntrace mJsAccessEntrace = this.mJsAccessEntrace;
        if (mJsAccessEntrace == null) {
            this.mJsAccessEntrace = mJsAccessEntrace = JsAccessEntraceImpl.getInstance(mWebCreator.getWebView());
        }
        return mJsAccessEntrace;
    }


    public AgentWeb clearWebCache() {

        if (this.getWebCreator().getWebView() != null) {
            Log.i(TAG, "清空 webview 缓存");
            AgentWebUtils.clearWebViewAllCache(mActivity, this.getWebCreator().getWebView());
        } else {
            AgentWebUtils.clearWebViewAllCache(mActivity);
        }
        return this;
    }


    public static AgentBuilder with(@NonNull Activity activity) {
        if (activity == null)
            throw new NullPointerException("activity can not be null .");
        return new AgentBuilder(activity);
    }

    public static AgentBuilder with(@NonNull Fragment fragment) {


        Activity mActivity = null;
        if ((mActivity = fragment.getActivity()) == null)
            throw new NullPointerException("activity can not be null .");
        return new AgentBuilder(mActivity, fragment);
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
        }
        return mIEventHandler.onKeyDown(keyCode, keyEvent);
    }

    public boolean back() {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
        }
        return mIEventHandler.back();
    }


    public WebCreator getWebCreator() {
        return this.mWebCreator;
    }

    public IEventHandler getIEventHandler() {
        return this.mIEventHandler == null ? (this.mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor())) : this.mIEventHandler;
    }


    public IAgentWebSettings getAgentWebSettings() {
        return this.mAgentWebSettings;
    }

    public IndicatorController getIndicatorController() {
        return this.mIndicatorController;
    }

    public JsInterfaceHolder getJsInterfaceHolder() {
        return this.mJsInterfaceHolder;
    }

    public IUrlLoader getLoader() {
        return this.mIUrlLoader;
    }

    public void destroy() {
        this.mWebLifeCycle.onDestroy();
    }

    public static class PreAgentWeb {
        private AgentWeb mAgentWeb;
        private boolean isReady = false;

        PreAgentWeb(AgentWeb agentWeb) {
            this.mAgentWeb = agentWeb;
        }


        public PreAgentWeb ready() {
            if (!isReady) {
                mAgentWeb.ready();
                isReady = true;
            }
            return this;
        }

        public AgentWeb go(@Nullable String url) {
            if (!isReady) {
//                throw new IllegalStateException(" please call ready before go to finish all webview settings");  //i want to do this , but i cannot;
                ready();
            }
            return mAgentWeb.go(url);
        }


    }


    private void doSafeCheck() {

        WebSecurityCheckLogic mWebSecurityCheckLogic = this.mWebSecurityCheckLogic;
        if (mWebSecurityCheckLogic == null) {
            this.mWebSecurityCheckLogic = mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
        }
        mWebSecurityController.check(mWebSecurityCheckLogic);

    }

    private void doCompat() {
        mJavaObjects.put("agentWeb", mAgentWebJsInterfaceCompat = new AgentWebJsInterfaceCompat(this, mActivity));
    }

    private WebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int mIndicatorColor, int height_dp, WebView webView, IWebLayout webLayout) {

        if (progressView != null && enableIndicator) {
            return new DefaultWebCreator(mActivity, mViewGroup, lp, index, progressView, webView, webLayout);
        } else {
            return enableIndicator ?
                    new DefaultWebCreator(mActivity, mViewGroup, lp, index, mIndicatorColor, height_dp, webView, webLayout)
                    : new DefaultWebCreator(mActivity, mViewGroup, lp, index, webView, webLayout);
        }
    }

    private void loadData(String data, String mimeType, String encoding) {
        mWebCreator.getWebView().loadData(data, mimeType, encoding);
    }

    private void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String history) {
        mWebCreator.getWebView().loadDataWithBaseURL(baseUrl, data, mimeType, encoding, history);
    }

    private AgentWeb go(String url) {
        this.getLoader().loadUrl(url);
        IndicatorController mIndicatorController = null;
        if (!TextUtils.isEmpty(url) && (mIndicatorController = getIndicatorController()) != null && mIndicatorController.offerIndicator() != null) {
            getIndicatorController().offerIndicator().show();
        }
        return this;
    }

    private EventInterceptor getInterceptor() {

        if (this.mEventInterceptor != null)
            return this.mEventInterceptor;

        if (mIVideo instanceof VideoImpl) {
            return this.mEventInterceptor = (EventInterceptor) this.mIVideo;
        }

        return null;

    }

    private void init() {
        doCompat();
        doSafeCheck();
    }

    private IVideo getIVideo() {
        return mIVideo == null ? new VideoImpl(mActivity, mWebCreator.getWebView()) : mIVideo;
    }

    private WebViewClient getWebViewClient() {

        LogUtils.i(TAG, "getWebViewClient:" + this.mMiddleWrareWebClientBaseHeader);
        DefaultWebClient mDefaultWebClient = DefaultWebClient
                .createBuilder()
                .setActivity(this.mActivity)
                .setClient(this.mWebViewClient)
                .setWebClientHelper(this.webClientHelper)
                .setPermissionInterceptor(this.mPermissionInterceptor)
                .setWebView(this.mWebCreator.getWebView())
                .setInterceptUnkownScheme(this.isInterceptUnkownScheme)
                .setSchemeHandleType(this.openOtherAppWays)
                .build();
        MiddlewareWebClientBase header = this.mMiddleWrareWebClientBaseHeader;
        if (header != null) {
            MiddlewareWebClientBase tail = header;
            int count = 1;
            MiddlewareWebClientBase tmp = header;
            while (tmp.next() != null) {
                tail = tmp = tmp.next();
                count++;
            }
            LogUtils.i(TAG, "MiddlewareWebClientBase middleware count:" + count);
            tail.setWebViewClient(mDefaultWebClient);
            return header;
        } else {
            return mDefaultWebClient;
        }

    }


    private AgentWeb ready() {

        AgentWebConfig.initCookiesManager(mActivity.getApplicationContext());
        IAgentWebSettings mAgentWebSettings = this.mAgentWebSettings;
        if (mAgentWebSettings == null) {
            this.mAgentWebSettings = mAgentWebSettings = AgentWebSettingsImpl.getInstance();
        }

        if (mAgentWebSettings instanceof AgentWebSettingsImpl) {
            ((AgentWebSettingsImpl) mAgentWebSettings).bindAgentWeb(this);
        }
        if (mWebListenerManager == null && mAgentWebSettings instanceof AbsAgentWebSettings) {
            mWebListenerManager = (WebListenerManager) mAgentWebSettings;
        }
        mAgentWebSettings.toSetting(mWebCreator.getWebView());
        if (mJsInterfaceHolder == null) {
            mJsInterfaceHolder = JsInterfaceHolderImpl.getJsInterfaceHolder(mWebCreator.getWebView(), this.mSecurityType);
        }
        LogUtils.i(TAG, "mJavaObjects:" + mJavaObjects.size());
        if (mJavaObjects != null && !mJavaObjects.isEmpty()) {
            mJsInterfaceHolder.addJavaObjects(mJavaObjects);
        }

        if (mWebListenerManager != null) {
            mWebListenerManager.setDownloader(mWebCreator.getWebView(), null);
            mWebListenerManager.setWebChromeClient(mWebCreator.getWebView(), getChromeClient());
            mWebListenerManager.setWebViewClient(mWebCreator.getWebView(), getWebViewClient());
        }

        return this;
    }


    /*private android.webkit.DownloadListener getLoadListener() {
        android.webkit.DownloadListener mDownloadListener = this.mDownloadListener;
        if (mDownloadListener != null) {
            return mDownloadListener;
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.just.agentweb.download.DefaultDownloadImpl");
            Object mDefaultDownloadImpl$Extra = clazz.getDeclaredMethod("newExtra", Activity.class).invoke(null, this.mActivity);

            clazz = mDefaultDownloadImpl$Extra.getClass();
            LogUtils.i(TAG, "clazz:" + clazz.toString());

//            Method mMethod = clazz.getDeclaredMethod("setEnableIndicator", boolean.class);
//            mMethod.setAccessible(true);
//            mMethod.invoke(mDefaultDownloadImpl$Extra, new Boolean(true));
//
//            Method mMethod = clazz.getDeclaredMethod("setForceDownload", boolean.class);
//            mMethod.setAccessible(true);
//            mMethod.invoke(mDefaultDownloadImpl$Extra, new Boolean(false));

            Method mMethod = clazz.getDeclaredMethod("setDownloadListener", DownloadListener.class);
            mMethod.setAccessible(true);
            mMethod.invoke(mDefaultDownloadImpl$Extra, this.mAgentWebDownloadListener);

//            mMethod = clazz.getDeclaredMethod("setOpenBreakPointDownload", boolean.class);
//            mMethod.setAccessible(true);
//            mMethod.invoke(mDefaultDownloadImpl$Extra, new Boolean(true));

            mMethod = clazz.getDeclaredMethod("setPermissionInterceptor", PermissionInterceptor.class);
            mMethod.setAccessible(true);
            mMethod.invoke(mDefaultDownloadImpl$Extra, this.mPermissionInterceptor);

            mMethod = clazz.getDeclaredMethod("setWebView", WebView.class);
            mMethod.setAccessible(true);
            mMethod.invoke(mDefaultDownloadImpl$Extra, this.mWebCreator.getWebView());


            mMethod = clazz.getDeclaredMethod("create");
            mMethod.setAccessible(true);
            return this.mDownloadListener = mDownloadListener = (android.webkit.DownloadListener) mMethod.invoke(mDefaultDownloadImpl$Extra);
        } catch (Throwable ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }
        return null;
    }*/


    private WebChromeClient getChromeClient() {
        IndicatorController mIndicatorController = (this.mIndicatorController == null) ? IndicatorHandler.getInstance().inJectProgressView(mWebCreator.offer()) : this.mIndicatorController;

        DefaultChromeClient mDefaultChromeClient =
                new DefaultChromeClient(this.mActivity,
                        this.mIndicatorController = mIndicatorController,
                        this.mWebChromeClient, this.mIVideo = getIVideo(),
                        this.mPermissionInterceptor, mWebCreator.getWebView());

        LogUtils.i(TAG, "WebChromeClient:" + this.mWebChromeClient);
        MiddlewareWebChromeBase header = this.mMiddlewareWebChromeBaseHeader;
        if (header != null) {
            MiddlewareWebChromeBase tail = header;
            int count = 1;
            MiddlewareWebChromeBase tmp = header;
            for (; tmp.next() != null; ) {
                tail = tmp = tmp.next();
                count++;
            }
            LogUtils.i(TAG, "MiddlewareWebClientBase middleware count:" + count);
            tail.setWebChromeClient(mDefaultChromeClient);
            return this.mTargetChromeClient = header;
        } else {
            return this.mTargetChromeClient = mDefaultChromeClient;
        }
    }


    public enum SecurityType {
        DEFAULT_CHECK, STRICT_CHECK;
    }


    public static final class AgentBuilder {
        private Activity mActivity;
        private Fragment mFragment;
        private ViewGroup mViewGroup;
        private boolean isNeedDefaultProgress;
        private int index = -1;
        private BaseIndicatorView v;
        private IndicatorController mIndicatorController = null;
        /*默认进度条是显示的*/
        private boolean enableIndicator = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private IAgentWebSettings mAgentWebSettings;
        private WebCreator mWebCreator;
        private HttpHeaders mHttpHeaders = null;
        private IEventHandler mIEventHandler;
        private int height_dp = -1;
        private ArrayMap<String, Object> mJavaObject;
        private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
        private WebView mWebView;
        private boolean webClientHelper = true;
        private IWebLayout mWebLayout = null;
        private PermissionInterceptor mPermissionInterceptor = null;
        private AgentWebUIController mAgentWebUIController;
        private DefaultWebClient.OpenOtherPageWays openOtherPage = null;
        private boolean isInterceptUnkownScheme = false;
        private MiddlewareWebClientBase header;
        private MiddlewareWebClientBase tail;
        private MiddlewareWebChromeBase mChromeMiddleWareHeader = null;
        private MiddlewareWebChromeBase mChromeMiddleWareTail = null;
        private View errorView;
        private int errorLayout;
        private int reloadId;
        private int tag = -1;


        public AgentBuilder(@NonNull Activity activity, @NonNull Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
            tag = AgentWeb.FRAGMENT_TAG;
        }

        public AgentBuilder(@NonNull Activity activity) {
            mActivity = activity;
            tag = AgentWeb.ACTIVITY_TAG;
        }


        public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilder(this);
        }

        private PreAgentWeb buildAgentWeb() {
            if (tag == AgentWeb.FRAGMENT_TAG && this.mViewGroup == null)
                throw new NullPointerException("ViewGroup is null,please check you params");
            return new PreAgentWeb(HookManager.hookAgentWeb(new AgentWeb(this), this));
        }

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null)
                mJavaObject = new ArrayMap<>();
            mJavaObject.put(key, o);
        }

        private void addHeader(String k, String v) {

            if (mHttpHeaders == null)
                mHttpHeaders = HttpHeaders.create();
            mHttpHeaders.additionalHttpHeader(k, v);

        }
    }

    public static class IndicatorBuilder {
        AgentBuilder mAgentBuilder = null;

        public IndicatorBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder useDefaultIndicator(int color) {
            this.mAgentBuilder.enableIndicator = true;
            this.mAgentBuilder.mIndicatorColor = color;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder useDefaultIndicator() {
            this.mAgentBuilder.enableIndicator = true;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder closeDefaultIndicator() {
            this.mAgentBuilder.enableIndicator = false;
            this.mAgentBuilder.mIndicatorColor = -1;
            this.mAgentBuilder.height_dp = -1;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder setCustomIndicator(@NonNull BaseIndicatorView v) {
            if (v != null) {
                this.mAgentBuilder.enableIndicator = true;
                this.mAgentBuilder.v = v;
                this.mAgentBuilder.isNeedDefaultProgress = false;
            } else {
                this.mAgentBuilder.enableIndicator = true;
                this.mAgentBuilder.isNeedDefaultProgress = true;
            }

            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder setIndicatorColorWithHeight(@ColorInt int color, int height_dp) {
            this.mAgentBuilder.mIndicatorColor = color;
            this.mAgentBuilder.height_dp = height_dp;
            return new CommonBuilder(this.mAgentBuilder);
        }

    }


    public static class CommonBuilder {
        private AgentBuilder mAgentBuilder;

        public CommonBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder setEventHanadler(@Nullable IEventHandler iEventHandler) {
            mAgentBuilder.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonBuilder closeWebViewClientHelper() {
            mAgentBuilder.webClientHelper = false;
            return this;
        }


        public CommonBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mAgentBuilder.mWebChromeClient = webChromeClient;
            return this;

        }

        public CommonBuilder setWebViewClient(@Nullable WebViewClient webChromeClient) {
            this.mAgentBuilder.mWebViewClient = webChromeClient;
            return this;
        }

        public CommonBuilder useMiddlewareWebClient(@NonNull MiddlewareWebClientBase middleWrareWebClientBase) {
            if (middleWrareWebClientBase == null) {
                return this;
            }
            if (this.mAgentBuilder.header == null) {
                this.mAgentBuilder.header = this.mAgentBuilder.tail = middleWrareWebClientBase;
            } else {
                this.mAgentBuilder.tail.enq(middleWrareWebClientBase);
                this.mAgentBuilder.tail = middleWrareWebClientBase;
            }
            return this;
        }

        public CommonBuilder useMiddlewareWebChrome(@NonNull MiddlewareWebChromeBase middlewareWebChromeBase) {
            if (middlewareWebChromeBase == null) {
                return this;
            }
            if (this.mAgentBuilder.mChromeMiddleWareHeader == null) {
                this.mAgentBuilder.mChromeMiddleWareHeader = this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            } else {
                this.mAgentBuilder.mChromeMiddleWareTail.enq(middlewareWebChromeBase);
                this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            }
            return this;
        }

        public CommonBuilder setMainFrameErrorView(@NonNull View view) {
            this.mAgentBuilder.errorView = view;
            return this;
        }

        public CommonBuilder setMainFrameErrorView(@LayoutRes int errorLayout, @IdRes int reloadId) {
            this.mAgentBuilder.errorLayout = errorLayout;
            this.mAgentBuilder.reloadId = reloadId;
            return this;
        }

        public CommonBuilder setAgentWebWebSettings(@Nullable IAgentWebSettings agentWebSettings) {
            this.mAgentBuilder.mAgentWebSettings = agentWebSettings;
            return this;
        }

        public PreAgentWeb createAgentWeb() {
            return this.mAgentBuilder.buildAgentWeb();
        }


        public CommonBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
            this.mAgentBuilder.addJavaObject(name, o);
            return this;
        }

        public CommonBuilder setSecurityType(@NonNull SecurityType type) {
            this.mAgentBuilder.mSecurityType = type;
            return this;
        }

//        public CommonBuilder openParallelDownload() {
//            this.mAgentBuilder.isParallelDownload = true;
//            return this;
//        }
//
//        public CommonBuilder setNotifyIcon(@DrawableRes int icon) {
//            this.mAgentBuilder.icon = icon;
//            return this;
//        }

        public CommonBuilder setWebView(@Nullable WebView webView) {
            this.mAgentBuilder.mWebView = webView;
            return this;
        }

        public CommonBuilder setWebLayout(@Nullable IWebLayout iWebLayout) {
            this.mAgentBuilder.mWebLayout = iWebLayout;
            return this;
        }

        public CommonBuilder additionalHttpHeader(String k, String v) {
            this.mAgentBuilder.addHeader(k, v);

            return this;
        }

        public CommonBuilder setPermissionInterceptor(@Nullable PermissionInterceptor permissionInterceptor) {
            this.mAgentBuilder.mPermissionInterceptor = permissionInterceptor;
            return this;
        }

//        public CommonBuilder setDownloadListener(@Nullable DownloadListener downloadListener) {
//
//            this.mAgentBuilder.mDownloadListener = downloadListener;
//            return this;
//        }

        public CommonBuilder setAgentWebUIController(@Nullable AgentWebUIControllerImplBase agentWebUIController) {
            this.mAgentBuilder.mAgentWebUIController = agentWebUIController;
            return this;
        }

        public CommonBuilder setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
            this.mAgentBuilder.openOtherPage = openOtherPageWays;
            return this;
        }

        public CommonBuilder interceptUnkownUrl() {
            this.mAgentBuilder.isInterceptUnkownScheme = true;
            return this;
        }

    }

    private static final class PermissionInterceptorWrapper implements PermissionInterceptor {

        private WeakReference<PermissionInterceptor> mWeakReference;

        private PermissionInterceptorWrapper(PermissionInterceptor permissionInterceptor) {
            this.mWeakReference = new WeakReference<PermissionInterceptor>(permissionInterceptor);
        }

        @Override
        public boolean intercept(String url, String[] permissions, String a) {
            if (this.mWeakReference.get() == null) {
                return false;
            }
            return mWeakReference.get().intercept(url, permissions, a);
        }
    }


}
