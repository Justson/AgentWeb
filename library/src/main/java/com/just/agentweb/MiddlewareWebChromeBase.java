package com.just.agentweb;

import android.webkit.WebChromeClient;

/**
 * Created by cenxiaozhong on 2017/12/16.
 *  https://github.com/Justson/AgentWeb
 */

public class MiddlewareWebChromeBase extends WebChromeClientWrapper {

    private MiddlewareWebChromeBase mMiddlewareWebChromeBase;

    public MiddlewareWebChromeBase(WebChromeClient webChromeClient) {
        super(webChromeClient);
    }

    public MiddlewareWebChromeBase(){
        super(null);
    }
    @Override
    final void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    public MiddlewareWebChromeBase enq(MiddlewareWebChromeBase middlewareWebChromeBase) {
        setWebChromeClient(middlewareWebChromeBase);
        this.mMiddlewareWebChromeBase = middlewareWebChromeBase;
        return this.mMiddlewareWebChromeBase;
    }


    public MiddlewareWebChromeBase next() {
        return this.mMiddlewareWebChromeBase;
    }

}
