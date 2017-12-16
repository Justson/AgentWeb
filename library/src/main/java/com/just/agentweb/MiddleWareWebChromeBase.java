package com.just.agentweb;

import android.webkit.WebChromeClient;

/**
 * Created by cenxiaozhong on 2017/12/16.
 */

public class MiddleWareWebChromeBase extends WebChromeClientWrapper {

    private MiddleWareWebChromeBase mMiddleWareWebChromeBase;

    public MiddleWareWebChromeBase(WebChromeClient webChromeClient) {
        super(webChromeClient);
    }

    public MiddleWareWebChromeBase(){
        super(null);
    }
    @Override
    final void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    public MiddleWareWebChromeBase enq(MiddleWareWebChromeBase middleWareWebChromeBase) {
        setWebChromeClient(middleWareWebChromeBase);
        this.mMiddleWareWebChromeBase = middleWareWebChromeBase;
        return this.mMiddleWareWebChromeBase;
    }


    public MiddleWareWebChromeBase next() {
        return this.mMiddleWareWebChromeBase;
    }

}
