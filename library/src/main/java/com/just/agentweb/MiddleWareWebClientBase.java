package com.just.agentweb;

import android.webkit.WebViewClient;

/**
 * Created by cenxiaozhong on 2017/12/15.
 */

public class MiddleWareWebClientBase extends WrapperWebViewClient {
    private MiddleWareWebClientBase mMiddleWrareWebClientBase;
    private String TAG = this.getClass().getSimpleName();

     MiddleWareWebClientBase(MiddleWareWebClientBase client) {
        super(client);
        this.mMiddleWrareWebClientBase = client;
    }

    MiddleWareWebClientBase(WebViewClient client) {
        super(client);
    }
    public MiddleWareWebClientBase(){
         super(null);
    }

    MiddleWareWebClientBase next() {
        LogUtils.i(TAG, "next");
        return this.mMiddleWrareWebClientBase;
    }


    @Override
     final void setWebViewClient(WebViewClient webViewClient) {
        super.setWebViewClient(webViewClient);

    }
     MiddleWareWebClientBase enq(MiddleWareWebClientBase middleWrareWebClientBase){
        setWebViewClient(middleWrareWebClientBase);
        this.mMiddleWrareWebClientBase = middleWrareWebClientBase;
        return middleWrareWebClientBase;
    }


}
