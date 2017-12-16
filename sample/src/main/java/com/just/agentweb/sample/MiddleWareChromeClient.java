package com.just.agentweb.sample;

import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;

import com.just.agentweb.MiddleWareWebChromeBase;

/**
 * Created by cenxiaozhong on 2017/12/16.
 */

public class MiddleWareChromeClient extends MiddleWareWebChromeBase {
    public MiddleWareChromeClient() {

    }


    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.i("Info","onJsAlert:"+url);
        return super.onJsAlert(view, url, message, result);
    }
}
