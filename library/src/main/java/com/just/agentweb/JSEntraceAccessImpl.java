package com.just.agentweb;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebView;


/**
 *source code  https://github.com/Justson/AgentWeb
 */

public class JSEntraceAccessImpl extends BaseJSEntraceAccess {

    private WebView mWebView;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public static JSEntraceAccessImpl getInstance(WebView webView) {
        return new JSEntraceAccessImpl(webView);
    }

    private JSEntraceAccessImpl(WebView webView) {
        super(webView);
        this.mWebView = webView;
    }



    private void callSafeCallJs(final String s, final ValueCallback valueCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callJs(s, valueCallback);
            }
        });
    }

    @Override
    public void callJs(String params, final ValueCallback<String> callback) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSafeCallJs(params, callback);
            return;
        }

        super.callJs(params,callback);

    }


}
