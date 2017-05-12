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

    public AgentWeb ready() {
        WebSettings mWebSettings = this.mWebSettings;
        if (mWebSettings == null) {
            this.mWebSettings=mWebSettings = WebDefaultSettings.getInstance();
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

    public AgentWeb go(String url) {

        return loadUrl(url);
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




        public ConfigIndicatorBuilder setViewGroup(ViewGroup viewGroup, ViewGroup.LayoutParams lp) {
            this.mViewGroup = viewGroup;
            mLayoutParams = lp;
            return new ConfigIndicatorBuilder(this);
        }

        public ConfigIndicatorBuilder setViewGroup(ViewGroup viewGroup, ViewGroup.LayoutParams lp,int position) {
            this.mViewGroup = viewGroup;
            mLayoutParams = lp;
            this.index=position;
            return new ConfigIndicatorBuilder(this);
        }
        public ConfigIndicatorBuilder createContentViewTag(){

            this.mViewGroup = null;
            this.mLayoutParams = null;
            return new ConfigIndicatorBuilder(this);
        }

//



      /*  *//*如果index==-1 默认为最后*//*
        public AgentBuilder setViewIndex(int index) {
            this.index = index;
            return this;
        }*/




        private AgentWeb buildAgentWeb() {
            return HookManager.hookAgentWeb(new AgentWeb(this), this);
        }

        private IEventHandler mIEventHandler;



    }

    public static class ConfigIndicatorBuilder{

        private AgentBuilder mAgentBuilder;
        private ConfigIndicatorBuilder(AgentBuilder agentBuilder){
            this.mAgentBuilder=agentBuilder;
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
        public CommonAgentBuilder closeProgressBar(){
            mAgentBuilder.closeProgress();
            return new CommonAgentBuilder(mAgentBuilder);
        }

    }

    public  static class CommonAgentBuilder{
        private AgentBuilder mAgentBuilder;
        private CommonAgentBuilder(AgentBuilder agentBuilder){
            this.mAgentBuilder=agentBuilder;

        }
        public CommonAgentBuilder setWebViewClient(WebViewClient webViewClient) {
            this.mAgentBuilder.mWebViewClient = webViewClient;
            return this;
        }


        public CommonAgentBuilder setWebChromeClient(WebChromeClient webChromeClient) {
            this.mAgentBuilder.mWebChromeClient = webChromeClient;
            return this;
        }

        public CommonAgentBuilder setEventHandler(IEventHandler iEventHandler) {
            this.mAgentBuilder.mIEventHandler = iEventHandler;
            return this;
        }
        public CommonAgentBuilder setWebSettings(WebSettings webSettings) {
            this.mAgentBuilder.mWebSettings = webSettings;
            return this;
        }


        public CommonAgentBuilder(ProgressController progressController) {
            this.mAgentBuilder.mProgressController = progressController;
        }

        public AgentWeb createAgentWeb(){
            return mAgentBuilder.buildAgentWeb();
        }
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

        public IndicatorBuilderForFragment configRootView(ViewGroup v, ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilderForFragment(this);
        }

        private AgentWeb buildAgentWeb() {
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
            return new CommonBuilderForFragment(agentBuilderFragment);
        }

        public CommonBuilderForFragment setCustomIndicator(BaseIndicatorView v) {
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
        public CommonBuilderForFragment setChromeClient() {
            this.mAgentBuilderFragment.mWebChromeClient = null;
            return this;

        }
        public CommonBuilderForFragment setWebViewClient(WebViewClient webChromeClient) {
            this.mAgentBuilderFragment.mWebViewClient = webChromeClient;
            return this;
        }

        public CommonBuilderForFragment setWebSettings(WebSettings webSettings) {
            this.mAgentBuilderFragment.mWebSettings = webSettings;
            return this;
        }

        public AgentWeb createAgentWeb() {
            return this.mAgentBuilderFragment.buildAgentWeb();
        }
    }


}
