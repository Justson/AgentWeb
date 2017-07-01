package com.just.library;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class DefaultWebCreator implements WebCreator {

    private Activity mActivity;
    private ViewGroup mViewGroup;
    private boolean isNeedDefaultProgress;
    private int index;
    private BaseIndicatorView progressView;
    private ViewGroup.LayoutParams mLayoutParams = null;
    private int color = -1;
    private int height_dp;
    private boolean isCreated=false;
    private IWebLayout mIWebLayout;
    private BaseProgressSpec mBaseProgressSpec;


    protected DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, int color, int height_dp, WebView webView,IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = true;
        this.index = index;
        this.color = color;
        this.mLayoutParams = lp;
        this.height_dp = height_dp;
        this.mWebView = webView;
        this.mIWebLayout=webLayout;
    }

   protected DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, @Nullable WebView webView,IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = false;
        this.index = index;
        this.mLayoutParams = lp;
        this.mWebView = webView;
        this.mIWebLayout=webLayout;
    }

   protected DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, BaseIndicatorView progressView, WebView webView,IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = false;
        this.index = index;
        this.mLayoutParams = lp;
        this.progressView = progressView;
        this.mWebView = webView;
        this.mIWebLayout=webLayout;
    }

    private WebView mWebView = null;
    private FrameLayout mFrameLayout = null;
    private View targetProgress;

    public WebView getWebView() {
        return mWebView;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public FrameLayout getFrameLayout() {
        return mFrameLayout;
    }

    public void setFrameLayout(FrameLayout frameLayout) {
        mFrameLayout = frameLayout;
    }

    public View getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(View targetProgress) {
        this.targetProgress = targetProgress;
    }

    @Override
    public DefaultWebCreator create() {


        if(isCreated){
            return this;
        }
        isCreated=true;
        ViewGroup mViewGroup = this.mViewGroup;
        if (mViewGroup == null) {
            mViewGroup = this.mFrameLayout= (FrameLayout)createGroupWithWeb();
            mActivity.setContentView(mViewGroup);
        } else {
            if (index == -1)
                mViewGroup.addView( this.mFrameLayout= (FrameLayout)createGroupWithWeb(), mLayoutParams);
            else
                mViewGroup.addView(this.mFrameLayout= (FrameLayout)createGroupWithWeb(), index, mLayoutParams);
        }
        return this;
    }

    @Override
    public WebView get() {
        return mWebView;
    }

    @Override
    public ViewGroup getGroup() {
        return mFrameLayout;
    }



    private ViewGroup createGroupWithWeb() {
        Activity mActivity = this.mActivity;
        FrameLayout mFrameLayout = new FrameLayout(mActivity);
        mFrameLayout.setBackgroundColor(Color.WHITE);
        View target=mIWebLayout==null?(this.mWebView= (WebView) web()):webLayout();
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        mFrameLayout.addView(target, mLayoutParams);

        LogUtils.i("Info", "    webView:" + (this.mWebView instanceof AgentWebView));
        if (isNeedDefaultProgress) {
            FrameLayout.LayoutParams lp = null;
            WebProgress mWebProgress = new WebProgress(mActivity);
            if (height_dp > 0)
                lp = new FrameLayout.LayoutParams(-2, AgentWebUtils.dp2px(mActivity, height_dp));
            else
                lp = mWebProgress.offerLayoutParams();
            if (color != -1)
                mWebProgress.setColor(color);
            lp.gravity = Gravity.TOP;
            mFrameLayout.addView((View) (this.mBaseProgressSpec = mWebProgress), lp);
        } else if (!isNeedDefaultProgress && progressView != null) {
            mFrameLayout.addView((View) (this.mBaseProgressSpec = (BaseProgressSpec) progressView), progressView.offerLayoutParams());
        }
        return mFrameLayout;

    }



    private View webLayout(){
        WebView mWebView = null;
        if((mWebView=mIWebLayout.getWeb())==null){
            mWebView=web();
            mIWebLayout.getLayout().addView(mWebView,-1,-1);
            LogUtils.i("Info","add webview");

        }else{
            AgentWebConfig.WEBVIEW_TYPE=AgentWebConfig.WEBVIEW_CUSTOM_TYPE;
        }
        this.mWebView=mWebView;
        return mIWebLayout.getLayout();

    }

    private WebView web(){

        WebView mWebView = null;
        if (this.mWebView != null) {
            mWebView = this.mWebView;
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_CUSTOM_TYPE;
        } else if (AgentWebConfig.isKikatOrBelowKikat) {
            mWebView = new AgentWebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE;
        } else {
            mWebView = new WebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_DEFAULT_TYPE;
        }

        return mWebView;
    }

    @Override
    public BaseProgressSpec offer() {
        return mBaseProgressSpec;
    }
}
