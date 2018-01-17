package com.just.agentweb;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebView;


/**
 *source code  https://github.com/Justson/AgentWeb
 */

public class JSAccessEntraceImpl extends BaseJSAccessEntrace {

    private WebView mWebView;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public static JSAccessEntraceImpl getInstance(WebView webView) {
        return new JSAccessEntraceImpl(webView);
    }

    private JSAccessEntraceImpl(WebView webView) {
        super(webView);
        this.mWebView = webView;
    }



    private void callSafeCallJs(final String s, final ValueCallback valueCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callJS(s, valueCallback);
            }
        });
    }

    @Override
    public void callJS(String params, final ValueCallback<String> callback) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSafeCallJs(params, callback);
            return;
        }

        super.callJS(params,callback);

    }


}
