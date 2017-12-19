package com.just.agentweb.sample.client;

import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;

import com.just.agentweb.MiddleWareWebChromeBase;

/**
 * Created by cenxiaozhong on 2017/12/16.
 * After agentweb 3.0.0  ， allow  dev to custom self WebChromeClient's MiddleWare  .
 */
public class MiddleWareChromeClient extends MiddleWareWebChromeBase {
    public MiddleWareChromeClient() {
    }
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.i("Info","onJsAlert:"+url);
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        Log.i("Info","onProgressChanged:");
    }
}
