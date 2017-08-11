package com.just.library;

/**
 * Created by cenxiaozhong on 2017/8/10.
 */

import android.app.Activity;
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 */
public class WebPools {


    private final Queue<WebView> mWebViews;

    private Object lock = new Object();
    private static final WebPools mWebPools = new WebPools();

    private WebPools() {
        mWebViews=new LinkedBlockingDeque<WebView>();
    }

    public static WebView acquireWebView(Activity activity) {

        return mWebPools.acquireWebViewInternal(activity);

    }

    public static void recyclerWebView(WebView webView){

        MutableContextWrapper mContext= (MutableContextWrapper) webView.getContext();
        mContext.setBaseContext(null);

        mWebPools.enqueue(webView);

    }

    private void enqueue(WebView webView){
        mWebViews.offer(webView);
    }
    public WebView acquireWebViewInternal(Activity activity) {

        WebView mWebView = mWebViews.poll();
        if (mWebView == null) {
            synchronized (lock) {
                return new WebView(new MutableContextWrapper(activity));
            }
        } else {
            MutableContextWrapper mMutableContextWrapper = (MutableContextWrapper) mWebView.getContext();
            mMutableContextWrapper.setBaseContext(activity);
            return mWebView;
        }
    }



}
