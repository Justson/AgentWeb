package com.just.library.agentweb;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.just.library.IWebLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by cenxiaozhong on 2017/8/14.
 */

public class SmartRefreshWebLayout implements IWebLayout {

    private final SmartRefreshLayout mSmartRefreshLayout;
    private final WebView mWebView;

    public SmartRefreshWebLayout(Activity activity){

        View mView=activity.getLayoutInflater().inflate(R.layout.fragment_srl_web,null);
        View smarkView = mView.findViewById(R.id.smarkLayout);
        mSmartRefreshLayout = (SmartRefreshLayout) smarkView;
        mWebView = (WebView) mSmartRefreshLayout.findViewById(R.id.webView);

    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return mSmartRefreshLayout;
    }

    @Nullable
    @Override
    public WebView getWeb() {
        return mWebView;
    }
}
