package com.just.agentweb.sample;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.just.agentweb.MiddleWareWebClientBase;

/**
 * Created by cenxiaozhong on 2017/12/16.
 */

public class SonicWebViewClient extends MiddleWareWebClientBase {
    public SonicWebViewClient() {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        Log.i("Info","SonicWebViewClient -- >  shouldOverrideUrlLoading:"+request.getUrl().toString()+ " this:"+this);
        boolean tag= super.shouldOverrideUrlLoading(view, request);
        return tag;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("Info","SonicWebViewClient -- >  shouldOverrideUrlLoading:"+url);
        boolean tag= super.shouldOverrideUrlLoading(view, url);
        return tag;
    }
}
