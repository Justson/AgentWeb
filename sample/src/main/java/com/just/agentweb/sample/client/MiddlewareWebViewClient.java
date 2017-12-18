package com.just.agentweb.sample.client;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.just.agentweb.MiddleWareWebClientBase;

/**
 * Created by cenxiaozhong on 2017/12/16.
 */

public class MiddlewareWebViewClient extends MiddleWareWebClientBase {

    public MiddlewareWebViewClient() {
    }

    private static int count = 1;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.i("Info", "MiddlewareWebViewClient -- >  shouldOverrideUrlLoading:" + request.getUrl().toString() + "  c:" + (count++));
        return super.shouldOverrideUrlLoading(view, request);

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("Info", "MiddlewareWebViewClient -- >  shouldOverrideUrlLoading:" + url + "  c:" + (count++));
        return super.shouldOverrideUrlLoading(view, url);

    }
}
