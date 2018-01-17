package com.just.agentweb.sample.sonic;

import android.annotation.TargetApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.just.agentweb.MiddlewareWebClientBase;
import com.tencent.sonic.sdk.SonicSession;

/**
 * Created by cenxiaozhong on 2017/12/17.
 */

public class SonicWebViewClient extends MiddlewareWebClientBase {

    private SonicSession sonicSession;

    public SonicWebViewClient(SonicSession sonicSession) {
        this.sonicSession=sonicSession;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (sonicSession != null) {
            sonicSession.getSessionClient().pageFinish(url);
        }
    }

    @TargetApi(21)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (sonicSession != null) {
            return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
        }
        return null;
    }
}
