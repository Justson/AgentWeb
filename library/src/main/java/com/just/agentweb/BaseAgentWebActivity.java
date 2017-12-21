package com.just.agentweb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by cenxiaozhong on 2017/7/22.
 * <p>
 * source code  https://github.com/Justson/AgentWeb
 */

public abstract class BaseAgentWebActivity extends AppCompatActivity {

    protected AgentWeb mAgentWeb;
    private AgentWebUIControllerImplBase mAgentWebUIController;
    private ErrorLayoutEntity mErrorLayoutEntity;
    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        buildAgentWeb();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        buildAgentWeb();
    }

    protected void buildAgentWeb() {
        ErrorLayoutEntity mErrorLayoutEntity = getErrorLayoutEntity();
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(getAgentWebParent(), new ViewGroup.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .setIndicatorColorWithHeight(getIndicatorColor(), getIndicatorHeight())
                .setReceivedTitleCallback(getReceivedTitleCallback())
                .setWebChromeClient(getWebChromeClient())
                .setWebViewClient(getWebViewClient())
                .setWebView(getWebView())
                .setPermissionInterceptor(getPermissionInterceptor())
                .setWebLayout(getWebLayout())
                .setAgentWebUIController(getAgentWebUIController())
                .interceptUnkownScheme()
                .setOpenOtherAppWays(getOpenOtherAppWay())
                .useMiddleWareWebChrome(getMiddleWareWebChrome())
                .useMiddleWareWebClient(getMiddleWareWebClient())
                .addDownLoadResultListener(getDownLoadResultListener())
                .setAgentWebSettings(getAgentWebSettings())
                .setMainFrameErrorView(mErrorLayoutEntity.layoutRes, mErrorLayoutEntity.reloadId)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()
                .go(getUrl());
    }


    protected @NonNull
    ErrorLayoutEntity getErrorLayoutEntity() {
        if (this.mErrorLayoutEntity == null) {
            this.mErrorLayoutEntity = new ErrorLayoutEntity();
        }
        return mErrorLayoutEntity;
    }

    protected AgentWeb getAgentWeb() {
        return this.mAgentWeb;
    }


    protected static class ErrorLayoutEntity {
        private int layoutRes = R.layout.agentweb_error_page;
        private int reloadId;

        public void setLayoutRes(int layoutRes) {
            this.layoutRes = layoutRes;
            if (layoutRes <= 0) {
                layoutRes = -1;
            }
        }

        public void setReloadId(int reloadId) {
            this.reloadId = reloadId;
            if (reloadId <= 0) {
                reloadId = -1;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAgentWeb != null)
            mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }


    protected @Nullable
    DownLoadResultListener getDownLoadResultListener() {
        return null;
    }

    private @Nullable
    ChromeClientCallbackManager.ReceivedTitleCallback getReceivedTitleCallback() {
        return new ChromeClientCallbackManager.ReceivedTitleCallback() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(view, title);
            }
        };
    }

    protected void setTitle(WebView view, String title) {

    }

    protected
    @Nullable
    String getUrl() {
        return null;
    }

    public @Nullable
    AgentWebSettings getAgentWebSettings() {
        return WebDefaultSettingsManager.getInstance();
    }

    protected abstract @NonNull
    ViewGroup getAgentWebParent();

    protected @Nullable
    WebChromeClient getWebChromeClient() {
        return null;
    }

    protected @ColorInt
    int getIndicatorColor() {
        return -1;
    }

    protected int getIndicatorHeight() {
        return -1;
    }

    protected @Nullable
    WebViewClient getWebViewClient() {
        return null;
    }


    protected @Nullable
    WebView getWebView() {
        return null;
    }

    protected @Nullable
    IWebLayout getWebLayout() {
        return null;
    }

    protected @Nullable
    PermissionInterceptor getPermissionInterceptor() {
        return null;
    }

    public @Nullable
    AgentWebUIControllerImplBase getAgentWebUIController() {
        return null;
    }

    public @Nullable
    DefaultWebClient.OpenOtherAppWays getOpenOtherAppWay() {
        return null;
    }
    protected @NonNull MiddleWareWebChromeBase getMiddleWareWebChrome() {
        return this.mMiddleWareWebChrome = new MiddleWareWebChromeBase();
    }

    protected @NonNull MiddleWareWebClientBase getMiddleWareWebClient() {
        return this.mMiddleWareWebClient = new MiddleWareWebClientBase();
    }
}
