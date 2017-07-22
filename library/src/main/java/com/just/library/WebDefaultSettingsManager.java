package com.just.library;


import android.os.Build;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b><br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述:source CODE  https://github.com/Justson/AgentWeb</b><br>
 */

public class WebDefaultSettingsManager implements AgentWebSettings, WebListenerManager {

    private android.webkit.WebSettings mWebSettings;

    public static WebDefaultSettingsManager getInstance() {
        return new WebDefaultSettingsManager();
    }

    protected WebDefaultSettingsManager() {

    }

    @Override
    public AgentWebSettings toSetting(WebView webView) {
        settings(webView);
        return this;
    }

    private void settings(WebView webView) {


        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mWebSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

//        mWebSettings.setRenderPriority(android.webkit.AgentWebSettings.RenderPriority.HIGH);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        mWebSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >=19)
            mWebSettings.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        else
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);

        //
        String dir = AgentWebConfig.getCachePath(webView.getContext());

        LogUtils.i("Info", "dir:" + dir + "   appcache:" + AgentWebConfig.getCachePath(webView.getContext()));
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);
        //适配5.0不允许http和https混合使用情况
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);


    }

    @Override
    public android.webkit.WebSettings getWebSettings() {
        return mWebSettings;
    }

    @Override
    public WebListenerManager setWebChromeClient(WebView webview, WebChromeClient webChromeClient) {
        webview.setWebChromeClient(webChromeClient);

        return this;
    }

    @Override
    public WebListenerManager setWebViewClient(WebView webView, WebViewClient webViewClient) {
        webView.setWebViewClient(webViewClient);
        return this;
    }

    @Override
    public WebListenerManager setDownLoader(WebView webView, DownloadListener downloadListener) {
        webView.setDownloadListener(downloadListener);
        return this;
    }


}
