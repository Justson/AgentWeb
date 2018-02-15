package com.just.agentweb;

import android.webkit.WebChromeClient;

/**
 * Created by cenxiaozhong on 2017/12/16.
 * https://github.com/Justson/AgentWeb
 */

public class MiddlewareWebChromeBase extends WebChromeClientWrapper {

    private MiddlewareWebChromeBase mMiddlewareWebChromeBase;

    protected MiddlewareWebChromeBase(WebChromeClient webChromeClient) {
        super(webChromeClient);
    }

    protected MiddlewareWebChromeBase() {
        super(null);
    }

    @Override
    final void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    final MiddlewareWebChromeBase enq(MiddlewareWebChromeBase middlewareWebChromeBase) {
        setWebChromeClient(middlewareWebChromeBase);
        this.mMiddlewareWebChromeBase = middlewareWebChromeBase;
        return this.mMiddlewareWebChromeBase;
    }


    final MiddlewareWebChromeBase next() {
        return this.mMiddlewareWebChromeBase;
    }

}
