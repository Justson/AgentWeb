package com.just.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */


/**
 * FrameLayout--嵌套WebView,ProgressBar
 * https://github.com/Justson/AgentWeb
 * author just -- cxz
 */
public class AgentWeb {

    private static final String TAG = AgentWeb.class.getSimpleName();
    private Activity mActivity;
    private ViewGroup mViewGroup;
    private WebCreator mWebCreator;
    private AgentWebSettings mAgentWebSettings;
    private AgentWeb mAgentWeb = null;
    private IndicatorController mIndicatorController;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private boolean enableProgress;
    private Fragment mFragment;
    private IEventHandler mIEventHandler;
    private ArrayMap<String, Object> mJavaObjects = new ArrayMap<>();
    private int TAG_TARGET = 0;
    private WebListenerManager mWebListenerManager;
    private DownloadListener mDownloadListener = null;
    private ChromeClientCallbackManager mChromeClientCallbackManager;
    private WebSecurityController<WebSecurityCheckLogic> mWebSecurityController = null;
    private WebSecurityCheckLogic mWebSecurityCheckLogic = null;
    private WebChromeClient mTargetChromeClient;
    private SecurityType mSecurityType = SecurityType.default_check;
    private static final int ACTIVITY_TAG = 0;
    private static final int FRAGMENT_TAG = 1;
    private AgentWebJsInterfaceCompat mAgentWebJsInterfaceCompat = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private JsEntraceAccess mJsEntraceAccess = null;
    private ILoader mILoader = null;
    private WebLifeCycle mWebLifeCycle;
    private IVideo mIVideo = null;
    private boolean webClientHelper = false;
    private DefaultMsgConfig mDefaultMsgConfig;


    private AgentWeb(AgentBuilder agentBuilder) {
        this.mActivity = agentBuilder.mActivity;
        this.mViewGroup = agentBuilder.mViewGroup;
        this.enableProgress = agentBuilder.enableProgress;
        mWebCreator = agentBuilder.mWebCreator == null ? configWebCreator(agentBuilder.v, agentBuilder.index, agentBuilder.mLayoutParams, agentBuilder.mIndicatorColor, agentBuilder.mIndicatorColorWithHeight, agentBuilder.mWebView, agentBuilder.mWebLayout) : agentBuilder.mWebCreator;
        mIndicatorController = agentBuilder.mIndicatorController;
        this.mWebChromeClient = agentBuilder.mWebChromeClient;
        this.mWebViewClient = agentBuilder.mWebViewClient;
        mAgentWeb = this;
        this.mAgentWebSettings = agentBuilder.mAgentWebSettings;
        this.mIEventHandler = agentBuilder.mIEventHandler;
        TAG_TARGET = ACTIVITY_TAG;
        if (agentBuilder.mJavaObject != null && agentBuilder.mJavaObject.isEmpty())
            this.mJavaObjects.putAll((Map<? extends String, ?>) agentBuilder.mJavaObject);
        this.mChromeClientCallbackManager = agentBuilder.mChromeClientCallbackManager;
        this.mWebViewClientCallbackManager = agentBuilder.mWebViewClientCallbackManager;

        this.mSecurityType = agentBuilder.mSecurityType;
        this.mILoader = new LoaderImpl(mWebCreator.create().get(), agentBuilder.headers);
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.get());
        mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.get(), this.mAgentWeb.mJavaObjects, mSecurityType);
        this.webClientHelper = agentBuilder.webclientHelper;
        init();
        setLoadListener(agentBuilder.mDownLoadResultListeners);
    }


    private AgentWeb(AgentBuilderFragment agentBuilderFragment) {
        TAG_TARGET = FRAGMENT_TAG;
        this.mActivity = agentBuilderFragment.mActivity;
        this.mFragment = agentBuilderFragment.mFragment;
        this.mViewGroup = agentBuilderFragment.mViewGroup;
        this.mIEventHandler = agentBuilderFragment.mIEventHandler;
        this.enableProgress = agentBuilderFragment.enableProgress;
        mWebCreator = agentBuilderFragment.mWebCreator == null ? configWebCreator(agentBuilderFragment.v, agentBuilderFragment.index, agentBuilderFragment.mLayoutParams, agentBuilderFragment.mIndicatorColor, agentBuilderFragment.height_dp, agentBuilderFragment.mWebView, agentBuilderFragment.mWebLayout) : agentBuilderFragment.mWebCreator;
        mIndicatorController = agentBuilderFragment.mIndicatorController;
        this.mWebChromeClient = agentBuilderFragment.mWebChromeClient;
        this.mWebViewClient = agentBuilderFragment.mWebViewClient;
        mAgentWeb = this;
        this.mAgentWebSettings = agentBuilderFragment.mAgentWebSettings;
        if (agentBuilderFragment.mJavaObject != null && agentBuilderFragment.mJavaObject.isEmpty())
            this.mJavaObjects.putAll((Map<? extends String, ?>) agentBuilderFragment.mJavaObject);
        this.mChromeClientCallbackManager = agentBuilderFragment.mChromeClientCallbackManager;
        this.mWebViewClientCallbackManager = agentBuilderFragment.mWebViewClientCallbackManager;
        this.mSecurityType = agentBuilderFragment.mSecurityType;
        this.mILoader = new LoaderImpl(mWebCreator.create().get(), agentBuilderFragment.mHttpHeaders);
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.get());
        mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.get(), this.mAgentWeb.mJavaObjects, this.mSecurityType);
        this.webClientHelper = agentBuilderFragment.webClientHelper;
        init();
        setLoadListener(agentBuilderFragment.mDownLoadResultListeners);
    }

    private void init() {
        if (this.mDownloadListener == null)
            mDefaultMsgConfig = new DefaultMsgConfig();
        doCompat();
        doSafeCheck();
    }

    public DefaultMsgConfig getDefaultMsgConfig() {
        return this.mDefaultMsgConfig;
    }

    private void doCompat() {


        mJavaObjects.put("agentWeb", mAgentWebJsInterfaceCompat = new AgentWebJsInterfaceCompat(this, mActivity));

        LogUtils.i("Info", "AgentWebConfig.isUseAgentWebView:" + AgentWebConfig.WEBVIEW_TYPE + "  mChromeClientCallbackManager:" + mChromeClientCallbackManager);
        if (AgentWebConfig.WEBVIEW_TYPE == AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE) {
            this.mChromeClientCallbackManager.setAgentWebCompatInterface((ChromeClientCallbackManager.AgentWebCompatInterface) mWebCreator.get());
            this.mWebViewClientCallbackManager.setPageLifeCycleCallback((WebViewClientCallbackManager.PageLifeCycleCallback) mWebCreator.get());
        }

    }

    public WebLifeCycle getWebLifeCycle() {
        return this.mWebLifeCycle;
    }

    private void doSafeCheck() {

        WebSecurityCheckLogic mWebSecurityCheckLogic = this.mWebSecurityCheckLogic;
        if (mWebSecurityCheckLogic == null) {
            this.mWebSecurityCheckLogic = mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
        }
        mWebSecurityController.check(mWebSecurityCheckLogic);

    }

    private WebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int mIndicatorColor, int height_dp, WebView webView, IWebLayout webLayout) {

        if (progressView != null && enableProgress) {
            return new DefaultWebCreator(mActivity, mViewGroup, lp, index, progressView, webView, webLayout);
        } else {
            return enableProgress ?
                    new DefaultWebCreator(mActivity, mViewGroup, lp, index, mIndicatorColor, height_dp, webView, webLayout)
                    : new DefaultWebCreator(mActivity, mViewGroup, lp, index, webView, webLayout);
        }
    }

    private void loadData(String data, String mimeType, String encoding) {
        mWebCreator.get().loadData(data, mimeType, encoding);
    }

    private void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String history) {
        mWebCreator.get().loadDataWithBaseURL(baseUrl, data, mimeType, encoding, history);
    }


    public JsEntraceAccess getJsEntraceAccess() {

        JsEntraceAccess mJsEntraceAccess = this.mJsEntraceAccess;
        if (mJsEntraceAccess == null) {
            this.mJsEntraceAccess = mJsEntraceAccess = JsEntraceAccessImpl.getInstance(mWebCreator.get());
        }
        return mJsEntraceAccess;
    }


    public AgentWeb clearWebCache() {

        if(this.getWebCreator().get()!=null){
            Log.i("Info","清空 webview 缓存");
            AgentWebUtils.clearWebViewAllCache(mActivity,this.getWebCreator().get());
        }else{
            AgentWebUtils.clearWebViewAllCache(mActivity);
        }
        return this;
    }


    public static AgentBuilder with(@NonNull Activity activity) {
        if (activity == null)
            throw new NullPointerException("activity can not null");
        return new AgentBuilder(activity);
    }

    public static AgentBuilderFragment with(@NonNull Fragment fragment) {


        Activity mActivity = null;
        if ((mActivity = fragment.getActivity()) == null)
            throw new NullPointerException("activity can not null");
        return new AgentBuilderFragment(mActivity, fragment);
    }

    private EventInterceptor mEventInterceptor;

    private EventInterceptor getInterceptor() {

        if (this.mEventInterceptor != null)
            return this.mEventInterceptor;

        if (mIVideo instanceof VideoImpl) {
            return this.mEventInterceptor = (EventInterceptor) this.mIVideo;
        }

        return null;

    }

    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.get(), getInterceptor());
        }
        return mIEventHandler.onKeyDown(keyCode, keyEvent);
    }

    public boolean back() {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.get(), getInterceptor());
        }
        return mIEventHandler.back();
    }

    /*public static AgentWeb withCreatorWeb(WebCreator creatorWeb) {
        return new AgentBuilder(creatorWeb).buildAgentWeb();
    }*/

    public WebCreator getWebCreator() {
        return this.mWebCreator;
    }

    public IEventHandler getIEventHandler() {
        return this.mIEventHandler == null ? (this.mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.get(), getInterceptor())) : this.mIEventHandler;
    }

    private JsInterfaceHolder mJsInterfaceHolder = null;

    public AgentWebSettings getAgentWebSettings() {
        return this.mAgentWebSettings;
    }

    public IndicatorController getIndicatorController() {
        return this.mIndicatorController;
    }


    private AgentWeb ready() {

        AgentWebConfig.initCookiesManager(mActivity.getApplicationContext());
        AgentWebSettings mAgentWebSettings = this.mAgentWebSettings;
        if (mAgentWebSettings == null) {
            this.mAgentWebSettings = mAgentWebSettings = WebDefaultSettingsManager.getInstance();
        }
        if (mWebListenerManager == null && mAgentWebSettings instanceof WebDefaultSettingsManager) {
            mWebListenerManager = (WebListenerManager) mAgentWebSettings;
        }
        mAgentWebSettings.toSetting(mWebCreator.get());
        if (mJsInterfaceHolder == null) {
            mJsInterfaceHolder = JsInterfaceHolderImpl.getJsInterfaceHolder(mWebCreator.get(), this.mSecurityType);
        }
        if (mJavaObjects != null && !mJavaObjects.isEmpty()) {
            mJsInterfaceHolder.addJavaObjects(mJavaObjects);
        }
        mWebListenerManager.setDownLoader(mWebCreator.get(), getLoadListener());
        mWebListenerManager.setWebChromeClient(mWebCreator.get(), getChromeClient());
        mWebListenerManager.setWebViewClient(mWebCreator.get(), getClient());


        return this;
    }


    private void setLoadListener(List<DownLoadResultListener> downLoadResultListeners) {
        DownloadListener mDownloadListener = this.mDownloadListener;
        if (mDownloadListener == null) {
            this.mDownloadListener = mDownloadListener = new DefaultDownLoaderImpl(mActivity, false, true, downLoadResultListeners, mDefaultMsgConfig.getDownLoadMsgConfig());
        }
    }

    private DownloadListener getLoadListener() {
        DownloadListener mDownloadListener = this.mDownloadListener;
        if (mDownloadListener == null) {
            setLoadListener(new ArrayList<DownLoadResultListener>());
        }
        return mDownloadListener;
    }

    private WebViewClientCallbackManager mWebViewClientCallbackManager = null;

    private WebChromeClient getChromeClient() {
        IndicatorController mIndicatorController = (this.mIndicatorController == null) ? IndicatorHandler.getInstance().inJectProgressView(mWebCreator.offer()) : this.mIndicatorController;
        /*if (mWebChromeClient != null) {
            return enableProgress ? new WebChromeClientProgressWrapper(mIndicatorController, mWebChromeClient) : mWebChromeClient;
        } else {
            return new DefaultChromeClient(this.mActivity, mIndicatorController, this.mChromeClientCallbackManager);
        }*/

        return this.mTargetChromeClient = new DefaultChromeClient(this.mActivity, mIndicatorController, mWebChromeClient, this.mChromeClientCallbackManager, this.mIVideo = getIVideo());
    }

    private IVideo getIVideo() {
        return mIVideo == null ? new VideoImpl(mActivity, mWebCreator.get()) : mIVideo;
    }


    public JsInterfaceHolder getJsInterfaceHolder() {
        return this.mJsInterfaceHolder;
    }

    private WebViewClient getClient() {
        if (!webClientHelper && AgentWebConfig.WEBVIEW_TYPE != AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClient != null) {
            return mWebViewClient;
        } else {
            return new DefaultWebClient(mActivity, this.mWebViewClient, this.mWebViewClientCallbackManager, webClientHelper);
        }

    }


    public ILoader getLoader() {
        return this.mILoader;
    }


    private AgentWeb go(String url) {
        this.getLoader().loadUrl(url);
        return this;
    }

    private boolean isKillProcess = false;

    public void destroy() {
        this.mWebLifeCycle.onDestroy();
    }

    public void destroyAndKill() {
        destroy();
        if (!AgentWebUtils.isMainProcess(mActivity)) {
            LogUtils.i("Info", "退出进程");
            System.exit(0);
        }
    }

    public void uploadFileResult(int requestCode, int resultCode, Intent data) {

        IFileUploadChooser mIFileUploadChooser = null;

        if (mTargetChromeClient instanceof DefaultChromeClient) {
            DefaultChromeClient mDefaultChromeClient = (DefaultChromeClient) mTargetChromeClient;
            mIFileUploadChooser = mDefaultChromeClient.pop();
        }

        if (mIFileUploadChooser == null)
            mIFileUploadChooser = mAgentWebJsInterfaceCompat.pop();
        Log.i("Info", "file upload:" + mIFileUploadChooser);
        if (mIFileUploadChooser != null)
            mIFileUploadChooser.fetchFilePathFromIntent(requestCode, resultCode, data);

        if (mIFileUploadChooser != null)
            mIFileUploadChooser = null;
    }


    public static class AgentBuilder {

        private Activity mActivity;
        private ViewGroup mViewGroup;
        private boolean isNeedProgress;
        private int index = -1;
        private BaseIndicatorView v;
        private IndicatorController mIndicatorController = null;
        /*默认进度条是打开的*/
        private boolean enableProgress = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private AgentWebSettings mAgentWebSettings;
        private WebCreator mWebCreator;
        private WebViewClientCallbackManager mWebViewClientCallbackManager = new WebViewClientCallbackManager();
        private SecurityType mSecurityType = SecurityType.default_check;

        private ChromeClientCallbackManager mChromeClientCallbackManager = new ChromeClientCallbackManager();

        private HttpHeaders headers = null;


        private ArrayMap<String, Object> mJavaObject = null;
        private int mIndicatorColorWithHeight = -1;
        private WebView mWebView;
        private boolean webclientHelper = true;
        private ArrayList<DownLoadResultListener> mDownLoadResultListeners;
        private IWebLayout mWebLayout;

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null)
                mJavaObject = new ArrayMap<>();
            mJavaObject.put(key, o);
        }


        private void setIndicatorColor(int indicatorColor) {
            mIndicatorColor = indicatorColor;
        }

        private AgentBuilder(Activity activity) {
            this.mActivity = activity;
        }

        private AgentBuilder enableProgress() {
            this.enableProgress = true;
            return this;
        }

        private AgentBuilder closeProgress() {
            this.enableProgress = false;
            return this;
        }


        private AgentBuilder(WebCreator webCreator) {
            this.mWebCreator = webCreator;
        }


        public ConfigIndicatorBuilder setAgentWebParent(ViewGroup viewGroup, ViewGroup.LayoutParams lp) {
            this.mViewGroup = viewGroup;
            mLayoutParams = lp;
            return new ConfigIndicatorBuilder(this);
        }

        public ConfigIndicatorBuilder setAgentWebParent(ViewGroup viewGroup, ViewGroup.LayoutParams lp, int position) {
            this.mViewGroup = viewGroup;
            mLayoutParams = lp;
            this.index = position;
            return new ConfigIndicatorBuilder(this);
        }

        public ConfigIndicatorBuilder createContentViewTag() {

            this.mViewGroup = null;
            this.mLayoutParams = null;
            return new ConfigIndicatorBuilder(this);
        }


        private void addHeader(String k, String v) {
            if (headers == null)
                headers = HttpHeaders.create();
            ;
            headers.additionalHttpHeader(k, v);

        }
//



      /*  *//*如果index==-1 默认为最后*//*
        public AgentBuilder setViewIndex(int index) {
            this.index = index;
            return this;
        }*/


        private PreAgentWeb buildAgentWeb() {
            return new PreAgentWeb(HookManager.hookAgentWeb(new AgentWeb(this), this));
        }

        private IEventHandler mIEventHandler;


        public void setIndicatorColorWithHeight(int indicatorColorWithHeight) {
            mIndicatorColorWithHeight = indicatorColorWithHeight;
        }
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
//                throw new IllegalStateException(" please call ready before go  to finish all webview settings");  //i want to do this , but i cannot;
                ready();
            }
            return mAgentWeb.go(url);
        }


    }

    public static class ConfigIndicatorBuilder {

        private AgentBuilder mAgentBuilder;

        private ConfigIndicatorBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public IndicatorBuilder useDefaultIndicator() {
            this.mAgentBuilder.isNeedProgress = true;
            mAgentBuilder.enableProgress();
            return new IndicatorBuilder(mAgentBuilder);
        }

        public CommonAgentBuilder customProgress(BaseIndicatorView view) {
            this.mAgentBuilder.v = view;

            mAgentBuilder.isNeedProgress = false;
            return new CommonAgentBuilder(mAgentBuilder);
        }

        public CommonAgentBuilder closeProgressBar() {
            mAgentBuilder.closeProgress();
            return new CommonAgentBuilder(mAgentBuilder);
        }


    }

    public static class CommonAgentBuilder {
        private AgentBuilder mAgentBuilder;


        private CommonAgentBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;

        }

        public CommonAgentBuilder setWebViewClient(@Nullable WebViewClient webViewClient) {
            this.mAgentBuilder.mWebViewClient = webViewClient;
            return this;
        }


        public CommonAgentBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mAgentBuilder.mWebChromeClient = webChromeClient;
            return this;
        }

        public CommonAgentBuilder setEventHandler(@Nullable IEventHandler iEventHandler) {
            this.mAgentBuilder.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonAgentBuilder setAgentWebSettings(@Nullable AgentWebSettings agentWebSettings) {
            this.mAgentBuilder.mAgentWebSettings = agentWebSettings;
            return this;
        }


        public CommonAgentBuilder(@Nullable IndicatorController indicatorController) {
            this.mAgentBuilder.mIndicatorController = indicatorController;
        }


        public CommonAgentBuilder addJavascriptInterface(String name, Object o) {
            mAgentBuilder.addJavaObject(name, o);
            return this;
        }

        public CommonAgentBuilder setWebCreator(@Nullable WebCreator webCreator) {
            this.mAgentBuilder.mWebCreator = webCreator;
            return this;
        }

        public CommonAgentBuilder setReceivedTitleCallback(@Nullable ChromeClientCallbackManager.ReceivedTitleCallback receivedTitleCallback) {
            this.mAgentBuilder.mChromeClientCallbackManager.setReceivedTitleCallback(receivedTitleCallback);
            return this;
        }

        public CommonAgentBuilder setSecutityType(@Nullable SecurityType secutityType) {
            this.mAgentBuilder.mSecurityType = secutityType;
            return this;
        }

        public CommonAgentBuilder setWebView(@Nullable WebView webView) {
            this.mAgentBuilder.mWebView = webView;
            return this;
        }

        public CommonAgentBuilder setWebLayout(@NonNull IWebLayout webLayout) {
            this.mAgentBuilder.mWebLayout = webLayout;
            return this;
        }

        public CommonAgentBuilder additionalHttpHeader(String k, String v) {
            this.mAgentBuilder.addHeader(k, v);
            return this;
        }

        public CommonAgentBuilder closeWebViewClientHelper() {
            mAgentBuilder.webclientHelper = false;
            return this;
        }

        public CommonAgentBuilder addDownLoadResultListener(DownLoadResultListener downLoadResultListener) {

            if (this.mAgentBuilder.mDownLoadResultListeners == null) {
                this.mAgentBuilder.mDownLoadResultListeners = new ArrayList<>();
            }
            this.mAgentBuilder.mDownLoadResultListeners.add(downLoadResultListener);
            return this;
        }

        public PreAgentWeb createAgentWeb() {
            return mAgentBuilder.buildAgentWeb();
        }

    }

    public static enum SecurityType {
        default_check, strict;
    }

    public static class IndicatorBuilder {

        private AgentBuilder mAgentBuilder = null;

        private IndicatorBuilder(AgentBuilder builder) {
            this.mAgentBuilder = builder;
        }

        public CommonAgentBuilder setIndicatorColor(int color) {
            mAgentBuilder.setIndicatorColor(color);
            return new CommonAgentBuilder(mAgentBuilder);
        }

        public CommonAgentBuilder defaultProgressBarColor() {
            mAgentBuilder.setIndicatorColor(-1);
            return new CommonAgentBuilder(mAgentBuilder);
        }

        public CommonAgentBuilder setIndicatorColorWithHeight(@ColorInt int color, int height_dp) {
            mAgentBuilder.setIndicatorColor(color);
            mAgentBuilder.setIndicatorColorWithHeight(height_dp);
            return new CommonAgentBuilder(mAgentBuilder);
        }


    }


    /*********************为Fragment构建AgentWeb***********************/

    public static final class AgentBuilderFragment {
        private Activity mActivity;
        private Fragment mFragment;

        private ViewGroup mViewGroup;
        private boolean isNeedDefaultProgress;
        private int index = -1;
        private BaseIndicatorView v;
        private IndicatorController mIndicatorController = null;
        /*默认进度条是打开的*/
        private boolean enableProgress = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private AgentWebSettings mAgentWebSettings;
        private WebCreator mWebCreator;
        private HttpHeaders mHttpHeaders = null;
        private IEventHandler mIEventHandler;
        private int height_dp = -1;
        private ArrayMap<String, Object> mJavaObject;
        private ChromeClientCallbackManager mChromeClientCallbackManager = new ChromeClientCallbackManager();
        private SecurityType mSecurityType = SecurityType.default_check;
        private WebView mWebView;
        private WebViewClientCallbackManager mWebViewClientCallbackManager = new WebViewClientCallbackManager();
        private boolean webClientHelper = true;
        private List<DownLoadResultListener> mDownLoadResultListeners = null;
        private IWebLayout mWebLayout = null;


        public AgentBuilderFragment(@NonNull Activity activity, @NonNull Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
        }

        public IndicatorBuilderForFragment setAgentWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilderForFragment(this);
        }

        private PreAgentWeb buildAgentWeb() {
            if (this.mViewGroup == null)
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

    public static class IndicatorBuilderForFragment {
        AgentBuilderFragment agentBuilderFragment = null;

        public IndicatorBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.agentBuilderFragment = agentBuilderFragment;
        }

        public CommonBuilderForFragment useDefaultIndicator(int color) {
            this.agentBuilderFragment.enableProgress = true;
            this.agentBuilderFragment.mIndicatorColor = color;
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment useDefaultIndicator() {
            this.agentBuilderFragment.enableProgress = true;
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment closeDefaultIndicator() {
            this.agentBuilderFragment.enableProgress = false;
            this.agentBuilderFragment.mIndicatorColor = -1;
            this.agentBuilderFragment.height_dp = -1;
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment setCustomIndicator(@NonNull BaseIndicatorView v) {
            if (v != null) {
                this.agentBuilderFragment.enableProgress = true;
                this.agentBuilderFragment.v = v;
                this.agentBuilderFragment.isNeedDefaultProgress = false;
            } else {
                this.agentBuilderFragment.enableProgress = true;
                this.agentBuilderFragment.isNeedDefaultProgress = true;
            }

            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment setIndicatorColorWithHeight(@ColorInt int color, int height_dp) {
            this.agentBuilderFragment.mIndicatorColor = color;
            this.agentBuilderFragment.height_dp = height_dp;
            return new CommonBuilderForFragment(this.agentBuilderFragment);
        }

    }


    public static class CommonBuilderForFragment {
        private AgentBuilderFragment mAgentBuilderFragment;

        public CommonBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.mAgentBuilderFragment = agentBuilderFragment;
        }

        public CommonBuilderForFragment setEventHanadler(@Nullable IEventHandler iEventHandler) {
            mAgentBuilderFragment.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonBuilderForFragment closeWebViewClientHelper() {
            mAgentBuilderFragment.webClientHelper = false;
            return this;
        }

        public CommonBuilderForFragment setWebCreator(@Nullable WebCreator webCreator) {
            this.mAgentBuilderFragment.mWebCreator = webCreator;
            return this;
        }

        public CommonBuilderForFragment setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mAgentBuilderFragment.mWebChromeClient = webChromeClient;
            return this;

        }

        public CommonBuilderForFragment setWebViewClient(@Nullable WebViewClient webChromeClient) {
            this.mAgentBuilderFragment.mWebViewClient = webChromeClient;
            return this;
        }

        public CommonBuilderForFragment setAgentWebWebSettings(@Nullable AgentWebSettings agentWebSettings) {
            this.mAgentBuilderFragment.mAgentWebSettings = agentWebSettings;
            return this;
        }

        public PreAgentWeb createAgentWeb() {
            return this.mAgentBuilderFragment.buildAgentWeb();
        }

        public CommonBuilderForFragment setReceivedTitleCallback(@Nullable ChromeClientCallbackManager.ReceivedTitleCallback receivedTitleCallback) {
            this.mAgentBuilderFragment.mChromeClientCallbackManager.setReceivedTitleCallback(receivedTitleCallback);
            return this;
        }

        public CommonBuilderForFragment addJavascriptInterface(@NonNull String name, @NonNull Object o) {
            this.mAgentBuilderFragment.addJavaObject(name, o);
            return this;
        }

        public CommonBuilderForFragment setSecurityType(SecurityType type) {
            this.mAgentBuilderFragment.mSecurityType = type;
            return this;
        }

        public CommonBuilderForFragment setWebView(@Nullable WebView webView) {
            this.mAgentBuilderFragment.mWebView = webView;
            return this;
        }

        public CommonBuilderForFragment setWebLayout(@Nullable IWebLayout iWebLayout) {
            this.mAgentBuilderFragment.mWebLayout = iWebLayout;
            return this;
        }

        public CommonBuilderForFragment additionalHttpHeader(String k, String v) {
            this.mAgentBuilderFragment.addHeader(k, v);

            return this;
        }


        public CommonBuilderForFragment addDownLoadResultListener(DownLoadResultListener downLoadResultListener) {

            if (this.mAgentBuilderFragment.mDownLoadResultListeners == null) {
                this.mAgentBuilderFragment.mDownLoadResultListeners = new ArrayList<>();
            }
            this.mAgentBuilderFragment.mDownLoadResultListeners.add(downLoadResultListener);
            return this;
        }
    }


}
