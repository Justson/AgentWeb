package com.just.library;

/**
 * Created by cenxiaozhong on 2017/8/10.
 */

import android.app.Activity;
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class WebPools {


    private final Queue<WebView> mWebViews;

    private Object lock = new Object();
    private static WebPools mWebPools = null;

    private static final AtomicReference<WebPools> mAtomicReference = new AtomicReference<>();

    private WebPools() {
        mWebViews = new LinkedBlockingQueue<>();
    }


    public static WebPools getInstance() {

        for (; ; ) {
            if (mAtomicReference.get() != null)
                return mAtomicReference.get();
            if (mAtomicReference.compareAndSet(null, new WebPools()))
                return mAtomicReference.get();

        }
    }


    public static void recycleWebView(WebView webView) {

        if (webView.getContext() instanceof MutableContextWrapper) {
            MutableContextWrapper mContext = (MutableContextWrapper) webView.getContext();
            mContext.setBaseContext(null);
        }
        mWebPools.enqueue(webView);

    }

    private void enqueue(WebView webView) {
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
