package com.just.library;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */


/**
 *
 * FrameLayout--嵌套WebView,ProgressBar
 *
 *
 */
public class AgentWeb {

    private Activity mActivity;
    private ViewGroup mViewGroup;
    private WebCreator mWebCreator;
    private WebSettings mWebSettings;
    private AgentWeb mAgentWeb = null;
    private ProgressController mProgressController;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private boolean enableProgress;
    private Fragment mFragment;
    private IEventHandler mIEventHandler;
    private int TAG = 0;

    private AgentWeb(AgentBuilder agentBuilder) {
        this.mActivity = agentBuilder.mActivity;
        this.mViewGroup = agentBuilder.mViewGroup;
        this.enableProgress = agentBuilder.enableProgress;
        mWebCreator = agentBuilder.mWebCreator == null ? configWebCreator(agentBuilder.v, agentBuilder.index, agentBuilder.mLayoutParams, agentBuilder.mIndicatorColor) : agentBuilder.mWebCreator;
        mProgressController = agentBuilder.mProgressController;
        this.mWebChromeClient = agentBuilder.mWebChromeClient;
        this.mWebViewClient = agentBuilder.mWebViewClient;
        mAgentWeb = this;
        this.mWebSettings = agentBuilder.mWebSettings;
        this.mIEventHandler = agentBuilder.mIEventHandler;
        TAG = 0;
    }


    public AgentWeb(AgentBuilderFragment agentBuilderFragment) {
        TAG = 1;
        this.mActivity = agentBuilderFragment.mActivity;
        this.mFragment = agentBuilderFragment.mFragment;
        this.mViewGroup = agentBuilderFragment.mViewGroup;
        this.mIEventHandler = agentBuilderFragment.mIEventHandler;
        this.enableProgress = agentBuilderFragment.enableProgress;
        mWebCreator = agentBuilderFragment.mWebCreator == null ? configWebCreator(agentBuilderFragment.v, agentBuilderFragment.index, agentBuilderFragment.mLayoutParams, agentBuilderFragment.mIndicatorColor) : agentBuilderFragment.mWebCreator;
        mProgressController = agentBuilderFragment.mProgressController;
        this.mWebChromeClient = agentBuilderFragment.mWebChromeClient;
        this.mWebViewClient = agentBuilderFragment.mWebViewClient;
        mAgentWeb = this;
        this.mWebSettings = agentBuilderFragment.mWebSettings;
    }

    private WebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int mIndicatorColor) {

        if (progressView != null && enableProgress) {
            return new DefaultWebCreator(mActivity, mViewGroup, lp, index, progressView);
        } else {
            return enableProgress ?
                    new DefaultWebCreator(mActivity, mViewGroup, lp, index, mIndicatorColor)
                    : new DefaultWebCreator(mActivity, mViewGroup, lp, index);
        }
    }

    public static AgentBuilder with(Activity activity) {
        return new AgentBuilder(activity);
    }

    public static AgentBuilderFragment with(Activity activity, Fragment fragment) {

        return new AgentBuilderFragment(activity, fragment);
    }


    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.get());
        }
        return mIEventHandler.onKeyDown(keyCode, keyEvent);
    }

    public static AgentWeb withCreatorWeb(WebCreator creatorWeb) {
        return new AgentBuilder(creatorWeb).buildAgentWeb();
    }

    public ViewGroup defaultWeb() {
        return mWebCreator.create().getGroup();
    }

    public AgentWeb createWebViewWithSettings() {
        WebSettings mWebSettings = this.mWebSettings;
        if (mWebSettings == null) {
            mWebSettings = WebDefaultSettings.getInstance();
        }
        mWebSettings.toSetting(mWebCreator.create().get(), getClient(), getChromeClient());
        return this;
    }

    private WebChromeClient getChromeClient() {
        ProgressController mProgressController = (this.mProgressController == null) ? ProgressHandler.getInstance().inJectProgressView(mWebCreator.offer()) : this.mProgressController;
        if (mWebChromeClient != null) {
            return enableProgress ? new WebChromeClientProgressWrapper(mProgressController, mWebChromeClient) : mWebChromeClient;
        } else {
            return new ChromeClientProgress(mProgressController);
        }

    }

    private WebViewClient getClient() {
        if (mWebViewClient != null) {
            return mWebViewClient;
        } else {
            return new DefaultWebClient();
        }
    }

    public AgentWeb loadUrl(String url) {
        if (TextUtils.isEmpty(url) || (!url.startsWith("http")))
            throw new UrlCommonException("url is null or '' or not startsWith http , please check url format");
        mWebCreator.get().loadUrl(url);
        return this;
    }

    public ViewGroup getViewGroup() {
        return mWebCreator.getGroup();
    }

    public void destroy() {
        AgentWebUtils.clearWebView(mWebCreator.get());
    }

    /*********************************************************为Activity构建AgentWeb***********************************************************************/


    public static class AgentBuilder {

        private Activity mActivity;
        private ViewGroup mViewGroup;
        private boolean isNeedProgress;
        private int index = -1;
        private BaseIndicatorView v;
        private ProgressController mProgressController = null;
        /*默认进度条是打开的*/
        private boolean enableProgress = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private WebSettings mWebSettings;
        private WebCreator mWebCreator;



        public AgentBuilder setWebSettings(WebSettings webSettings) {
            this.mWebSettings = webSettings;
            return this;
        }

        public int getIndicatorColor() {
            return mIndicatorColor;
        }

        public void setIndicatorColor(int indicatorColor) {
            mIndicatorColor = indicatorColor;
        }

        private AgentBuilder(Activity activity) {
            this.mActivity = activity;
        }

        public AgentBuilder enableProgress() {
            this.enableProgress = true;
            return this;
        }

        public AgentBuilder closeProgress() {
            this.enableProgress = false;
            return this;
        }


        private AgentBuilder(WebCreator webCreator) {
            this.mWebCreator = webCreator;
        }

        public AgentBuilder(ProgressController progressController) {
            this.mProgressController = progressController;
        }


        public AgentBuilder setViewGroup(ViewGroup viewGroup, ViewGroup.LayoutParams lp) {
            this.mViewGroup = viewGroup;
            mLayoutParams = lp;
            return this;
        }

        public AgentBuilder setViewGroupDefault() {
            this.mViewGroup = null;
            this.mLayoutParams = null;
            return this;
        }

        public IndicatorBuilder useDefaultProgressBar() {
            this.isNeedProgress = true;
            enableProgress = true;
            return new IndicatorBuilder(this);
        }

        public AgentBuilder customProgress(BaseIndicatorView view) {
            this.v = view;

            isNeedProgress = false;
            return this;
        }

        /*如果index==-1 默认为最后*/
        public AgentBuilder setViewIndex(int index) {
            this.index = index;
            return this;
        }


        public AgentBuilder setWebViewClient(WebViewClient webViewClient) {
            this.mWebViewClient = webViewClient;
            return this;
        }


        public AgentBuilder setWebChromeClient(WebChromeClient webChromeClient) {
            this.mWebChromeClient = webChromeClient;
            return this;
        }

        public AgentWeb buildAgentWeb() {
            return HookManager.hookAgentWeb(new AgentWeb(this), this);
        }

        private IEventHandler mIEventHandler;

        public AgentBuilder setEventHandler(IEventHandler iEventHandler) {
            this.mIEventHandler = iEventHandler;
            return this;
        }

    }

    public static class IndicatorBuilder {

        private AgentBuilder mAgentBuilder = null;

        private IndicatorBuilder(AgentBuilder builder) {
            this.mAgentBuilder = builder;
        }

        public AgentBuilder setIndicatorColor(int color) {
            mAgentBuilder.setIndicatorColor(color);
            return mAgentBuilder;
        }
    }


    /*********************************为Fragment构建AgentWeb**************************************************************/

    public static final class AgentBuilderFragment {
        private Activity mActivity;
        private Fragment mFragment;

        private ViewGroup mViewGroup;
        private boolean isNeedDefaultProgress;
        private int index = -1;
        private BaseIndicatorView v;
        private ProgressController mProgressController = null;
        /*默认进度条是打开的*/
        private boolean enableProgress = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private WebSettings mWebSettings;
        private WebCreator mWebCreator;

        private IEventHandler mIEventHandler;


        public AgentBuilderFragment(Activity activity, Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
        }

        public IndicatorBuilderForFragment configViewGroup(ViewGroup v, ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilderForFragment(this);
        }

        public AgentWeb buildAgentWeb() {
            if (this.mViewGroup == null)
                throw new NullPointerException("ViewGroup is null,please check you params");
            return HookManager.hookAgentWeb(new AgentWeb(this), this);
        }
    }

    public static class IndicatorBuilderForFragment {
        AgentBuilderFragment agentBuilderFragment = null;

        public IndicatorBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.agentBuilderFragment = agentBuilderFragment;
        }

        public ChromeBuilderForFragment useDefaultIndicator(int color) {
            this.agentBuilderFragment.enableProgress = true;
            this.agentBuilderFragment.mIndicatorColor = color;
            return new ChromeBuilderForFragment(agentBuilderFragment);
        }

        public ChromeBuilderForFragment useDefaultIndicator() {
            this.agentBuilderFragment.enableProgress = true;
            return new ChromeBuilderForFragment(agentBuilderFragment);
        }

        public ChromeBuilderForFragment closeDefaultIndicator() {
            this.agentBuilderFragment.enableProgress = false;
            return new ChromeBuilderForFragment(agentBuilderFragment);
        }

        public ChromeBuilderForFragment setCustomIndicator(BaseIndicatorView v) {
            if (v != null) {
                this.agentBuilderFragment.enableProgress = true;
                this.agentBuilderFragment.v = v;
                this.agentBuilderFragment.isNeedDefaultProgress = false;
            } else {
                this.agentBuilderFragment.enableProgress = true;
                this.agentBuilderFragment.isNeedDefaultProgress = true;
            }

            return new ChromeBuilderForFragment(agentBuilderFragment);
        }

        public AgentWeb finishBuild() {
            return this.agentBuilderFragment.buildAgentWeb();
        }
    }

    public static class ChromeBuilderForFragment {

        private AgentBuilderFragment mAgentBuilderFragment;

        public ChromeBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.mAgentBuilderFragment = agentBuilderFragment;
        }

        public WebViewClientBuilderForFragment setChromeClient(WebChromeClient webChromeClient) {
            this.mAgentBuilderFragment.mWebChromeClient = webChromeClient;
            return new WebViewClientBuilderForFragment(mAgentBuilderFragment);
        }

        public WebViewClientBuilderForFragment setDefaultChromeClient() {
            this.mAgentBuilderFragment.mWebChromeClient = null;
            return new WebViewClientBuilderForFragment(mAgentBuilderFragment);

        }

        public AgentWeb buildAgentWeb() {
            return this.mAgentBuilderFragment.buildAgentWeb();
        }
    }

    public static class WebViewClientBuilderForFragment {

        private AgentBuilderFragment mAgentBuilderFragment;

        public WebViewClientBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.mAgentBuilderFragment = agentBuilderFragment;

        }

        public SettingBuilderForFragment setWebViewClient(WebViewClient webChromeClient) {
            this.mAgentBuilderFragment.mWebViewClient = webChromeClient;
            return new SettingBuilderForFragment(mAgentBuilderFragment);
        }

        public SettingBuilderForFragment setDefaultWebClient() {
            this.mAgentBuilderFragment.mWebViewClient = null;
            return new SettingBuilderForFragment(mAgentBuilderFragment);
        }

        public AgentWeb finishBuild() {
            return this.mAgentBuilderFragment.buildAgentWeb();
        }
    }

    public static class SettingBuilderForFragment {
        private AgentBuilderFragment agentBuilderFragment;

        public SettingBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.agentBuilderFragment = agentBuilderFragment;
        }

        public CommonBuilderForFragment setWebSettings(WebSettings webSettings) {
            this.agentBuilderFragment.mWebSettings = webSettings;
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment setDefaultWebSettings() {
            this.agentBuilderFragment.mWebSettings = null;
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public AgentWeb finishBuild() {
            return this.agentBuilderFragment.buildAgentWeb();
        }
    }

    public static class CommonBuilderForFragment {
        private AgentBuilderFragment mAgentBuilderFragment;

        public CommonBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
            this.mAgentBuilderFragment = agentBuilderFragment;
        }

        public CommonBuilderForFragment setEventHanadler(IEventHandler iEventHandler) {
            mAgentBuilderFragment.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonBuilderForFragment setWebCreator(WebCreator webCreator) {
            this.mAgentBuilderFragment.mWebCreator = webCreator;
            return this;
        }

        public AgentWeb create() {
            return this.mAgentBuilderFragment.buildAgentWeb();
        }
    }


}
