package com.just.library;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 *     source code  https://github.com/Justson/AgentWeb
 */

public class DefaultWebClient extends WrapperWebViewClient {

    private WebViewClientCallbackManager mWebViewClientCallbackManager;
    DefaultWebClient(WebViewClient client,WebViewClientCallbackManager manager) {
        super(client);
        this.mWebViewClientCallbackManager=manager;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.i("Info", "shouldOverrideUrlLoading");
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i("Info", "onPageStarted");
        if(AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE&&mWebViewClientCallbackManager.getPageLifeCycleCallback()!=null){
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageStarted(view,url,favicon);
        }
        super.onPageStarted(view, url, favicon);

    }



    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.i("Info", "onReceivedError");
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.i("Info", "onReceivedError");

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE&&mWebViewClientCallbackManager.getPageLifeCycleCallback()!=null){
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageFinished(view,url);
        }
        super.onPageFinished(view, url);

        Log.i("Info", "onPageFinished");
    }



    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        Log.i("Info", "shouldOverrideKeyEvent");
        return super.shouldOverrideKeyEvent(view, event);
    }


}
