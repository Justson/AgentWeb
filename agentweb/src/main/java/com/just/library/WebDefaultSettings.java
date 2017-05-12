package com.just.library;


import android.webkit.WebChromeClient;
import android.webkit.WebView;
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

public class WebDefaultSettings implements WebSettings {

    public static WebDefaultSettings getInstance() {
        return InnerSettingsHolder.getHolder();
    }

    @Override
    public void toSetting(WebView webView, WebViewClient webViewClient, WebChromeClient webChromeClient) {
        android.webkit.WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setTextZoom(100);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setDomStorageEnabled(true);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
    }

    static class InnerSettingsHolder {
        private static final WebDefaultSettings target = new WebDefaultSettings();

        private static WebDefaultSettings getHolder() {
            return target;
        }
    }
}
