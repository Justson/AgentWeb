/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class DefaultWebCreator implements WebCreator {

    private Activity mActivity;
    private ViewGroup mViewGroup;
    private boolean mIsNeedDefaultProgress;
    private int mIndex;
    private BaseIndicatorView mProgressView;
    private ViewGroup.LayoutParams mLayoutParams = null;
    private int mColor = -1;
    /**
     * 单位dp
     */
    private int mHeight;
    private boolean mIsCreated = false;
    private IWebLayout mIWebLayout;
    private BaseIndicatorSpec mBaseIndicatorSpec;
    private WebView mWebView = null;
    private FrameLayout mFrameLayout = null;
    private View mTargetProgress;
    private static final String TAG = DefaultWebCreator.class.getSimpleName();

	/**
	 * 使用默认的进度条
	 * @param activity
	 * @param viewGroup
	 * @param lp
	 * @param index
	 * @param color
	 * @param mHeight
	 * @param webView
	 * @param webLayout
	 */
    protected DefaultWebCreator(@NonNull Activity activity,
                                @Nullable ViewGroup viewGroup,
                                ViewGroup.LayoutParams lp,
                                int index,
                                int color,
                                int mHeight,
                                WebView webView,
                                IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = true;
        this.mIndex = index;
        this.mColor = color;
        this.mLayoutParams = lp;
        this.mHeight = mHeight;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

	/**
	 * 关闭进度条
	 * @param activity
	 * @param viewGroup
	 * @param lp
	 * @param index
	 * @param webView
	 * @param webLayout
	 */
    protected DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, @Nullable WebView webView, IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = false;
        this.mIndex = index;
        this.mLayoutParams = lp;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

    /**
     * 自定义Indicator
     * @param activity
     * @param viewGroup
     * @param lp
     * @param index
     * @param progressView
     * @param webView
     * @param webLayout
     */
    protected DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, BaseIndicatorView progressView, WebView webView, IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = false;
        this.mIndex = index;
        this.mLayoutParams = lp;
        this.mProgressView = progressView;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }


    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public FrameLayout getFrameLayout() {
        return mFrameLayout;
    }


    public View getTargetProgress() {
        return mTargetProgress;
    }

    public void setTargetProgress(View targetProgress) {
        this.mTargetProgress = targetProgress;
    }

    @Override
    public DefaultWebCreator create() {


        if (mIsCreated) {
            return this;
        }
        mIsCreated = true;
        ViewGroup mViewGroup = this.mViewGroup;
        if (mViewGroup == null) {
            mViewGroup = this.mFrameLayout = (FrameLayout) createLayout();
            mActivity.setContentView(mViewGroup);
        } else {
            if (mIndex == -1) {
                mViewGroup.addView(this.mFrameLayout = (FrameLayout) createLayout(), mLayoutParams);
            } else {
                mViewGroup.addView(this.mFrameLayout = (FrameLayout) createLayout(), mIndex, mLayoutParams);
            }
        }
        return this;
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public FrameLayout getWebParentLayout() {
        return mFrameLayout;
    }


    private ViewGroup createLayout() {
        Activity mActivity = this.mActivity;
        WebParentLayout mFrameLayout = new WebParentLayout(mActivity);
        mFrameLayout.setId(R.id.web_parent_layout_id);
        mFrameLayout.setBackgroundColor(Color.WHITE);
        View target = mIWebLayout == null ? (this.mWebView = (WebView) createWebView()) : webLayout();
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        mFrameLayout.addView(target, mLayoutParams);
        mFrameLayout.bindWebView(this.mWebView);
        LogUtils.i(TAG, "  instanceof  AgentWebView:" + (this.mWebView instanceof AgentWebView));
        if (this.mWebView instanceof AgentWebView) {
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE;
        }
        ViewStub mViewStub = new ViewStub(mActivity);
        mViewStub.setId(R.id.mainframe_error_viewsub_id);
        mFrameLayout.addView(mViewStub, new FrameLayout.LayoutParams(-1, -1));
        if (mIsNeedDefaultProgress) {
            FrameLayout.LayoutParams lp = null;
            WebIndicator mWebIndicator = new WebIndicator(mActivity);
            if (mHeight > 0) {
                lp = new FrameLayout.LayoutParams(-2, AgentWebUtils.dp2px(mActivity, mHeight));
            } else {
                lp = mWebIndicator.offerLayoutParams();
            }
            if (mColor != -1) {
                mWebIndicator.setColor(mColor);
            }
            lp.gravity = Gravity.TOP;
            mFrameLayout.addView((View) (this.mBaseIndicatorSpec = mWebIndicator), lp);
            mWebIndicator.setVisibility(View.GONE);
        } else if (!mIsNeedDefaultProgress && mProgressView != null) {
            mFrameLayout.addView((View) (this.mBaseIndicatorSpec = (BaseIndicatorSpec) mProgressView), mProgressView.offerLayoutParams());
            mProgressView.setVisibility(View.GONE);
        }
        return mFrameLayout;

    }


    private View webLayout() {
        WebView mWebView = null;
        if ((mWebView = mIWebLayout.getWebView()) == null) {
            mWebView = createWebView();
            mIWebLayout.getLayout().addView(mWebView, -1, -1);
            LogUtils.i(TAG, "add webview");

        } else {
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_CUSTOM_TYPE;
        }
        this.mWebView = mWebView;
        return mIWebLayout.getLayout();

    }

    private WebView createWebView() {

        WebView mWebView = null;
        if (this.mWebView != null) {
            mWebView = this.mWebView;
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_CUSTOM_TYPE;
        } else if (AgentWebConfig.IS_KITKAT_OR_BELOW_KITKAT) {
            mWebView = new AgentWebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE;
        } else {
            mWebView = new WebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_DEFAULT_TYPE;
        }

        return mWebView;
    }

    @Override
    public BaseIndicatorSpec offer() {
        return mBaseIndicatorSpec;
    }
}
