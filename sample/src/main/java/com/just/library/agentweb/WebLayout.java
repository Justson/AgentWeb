package com.just.library.agentweb;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.just.library.IWebLayout;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

/**
 * Created by cenxiaozhong on 2017/7/1.
 */

public class WebLayout implements IWebLayout {

    private Activity mActivity;
    private final TwinklingRefreshLayout mTwinklingRefreshLayout;
    private WebView mWebView = null;

    public WebLayout(Activity activity) {
        this.mActivity = activity;
        mTwinklingRefreshLayout = (TwinklingRefreshLayout) LayoutInflater.from(activity).inflate(R.layout.fragment_twk_web, null);
        mTwinklingRefreshLayout.setPureScrollModeOn();
        mTwinklingRefreshLayout.setHeaderView(new Header(mActivity));
        mWebView = (WebView) mTwinklingRefreshLayout.findViewById(R.id.webView);
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return mTwinklingRefreshLayout;
    }

    @Nullable
    @Override
    public WebView getWeb() {
        return mWebView;
    }


   static class Header implements IHeaderView {


        View mView = null;

        Header(Activity activity) {
            mView = new View(activity);
            mView.setBackgroundColor(Color.TRANSPARENT);
        }

        @Override
        public View getView() {

            return mView;
        }

        @Override
        public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {

        }

        @Override
        public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {

        }

        @Override
        public void startAnim(float maxHeadHeight, float headHeight) {

        }

        @Override
        public void onFinish(OnAnimEndListener animEndListener) {

        }

        @Override
        public void reset() {

        }
    }
}
