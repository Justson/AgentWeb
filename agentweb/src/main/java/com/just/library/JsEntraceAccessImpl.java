package com.just.library;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;


/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class JsEntraceAccessImpl implements JsEntraceAccess {

    private WebView mWebView;

    public static JsEntraceAccessImpl getInstance(WebView webView) {
        return new JsEntraceAccessImpl(webView);
    }

    private JsEntraceAccessImpl(WebView webView) {
        this.mWebView = webView;
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void callSafeCallJs(final String s, final ValueCallback valueCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callJs(s, valueCallback);
            }
        });
    }

    @Override
    public void callJs(String str, final ValueCallback<String> callback) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSafeCallJs(str, callback);
            return;
        }
        Log.i("Info"," isEnd"+str.endsWith(")")+"   callback:"+callback+"  str:"+str);
        mWebView.evaluateJavascript(str, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (callback != null)
                    callback.onReceiveValue(value);
            }
        });
    }

    private void callSalfeLoadJs(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadJs(str);
            }
        });
    }

    @Override
    public void loadJs(String str) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSalfeLoadJs(str);
            return;
        }
        mWebView.loadUrl(str);
    }
}
