package com.just.agentweb.sample.client;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.just.agentweb.MiddlewareWebClientBase;

/**
 * Created by cenxiaozhong on 2017/12/16.
 *
 *
 * 方法的执行顺序，例如下面用了7个中间件一个 WebViewClient
 *
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 1
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 2
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 3
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 4
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 5
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 6
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 7
 *  DefaultWebClient                                  // 8
 *  .setWebViewClient(mWebViewClient)                 // 9
 *
 *
 * 典型的洋葱模型
 * 对象内部的方法执行顺序: 1->2->3->4->5->6->7->8->9->8->7->6->5->4->3->2->1
 *
 *
 * 中断中间件的执行， 删除super.methodName(...) 这行即可
 *
 */

public class MiddlewareWebViewClient extends MiddlewareWebClientBase {

    public MiddlewareWebViewClient() {
    }

    private static int count = 1;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.i("Info", "MiddlewareWebViewClient -- >  shouldOverrideUrlLoading:" + request.getUrl().toString() + "  c:" + (count++));
        return super.shouldOverrideUrlLoading(view, request);

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("Info", "MiddlewareWebViewClient -- >  shouldOverrideUrlLoading:" + url + "  c:" + (count++));
        return super.shouldOverrideUrlLoading(view, url);

    }
}
