package com.just.agentweb;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class WebParentLayout extends FrameLayout implements Provider<AgentWebUIController> {
    private AgentWebUIController mAgentWebUIController = null;
    private String TAG = this.getClass().getSimpleName();
    @LayoutRes
    private int errorLayoutRes;
    @IdRes
    private int clickId = -1;
    private View errorView;
    private WebView mWebView;

    public WebParentLayout(@NonNull Context context) {
        this(context, null);
        LogUtils.i(TAG,"WebParentLayout");
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("WebParentLayout context must be activity or activity sub class .");
        }
        this.errorLayoutRes=R.layout.agentweb_error_page;
        LogUtils.i(TAG,"errorLayoutRes:"+errorLayoutRes);
    }

    void bindController(AgentWebUIController agentWebUIController) {
        LogUtils.i(TAG, "bindController:" + agentWebUIController);
        this.mAgentWebUIController = agentWebUIController;
        this.mAgentWebUIController.bindWebParent(this, (Activity) getContext());
    }

    void showPageMainFrameError() {

        View container = this.findViewById(R.id.mainframe_error_container_id);
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        } else {
            createErrorLayout();
        }
    }

    private void createErrorLayout() {
        ViewStub mViewStub = (ViewStub) this.findViewById(R.id.mainframe_error_viewsub_id);
        final FrameLayout mFrameLayout = new FrameLayout(getContext());
        mFrameLayout.setId(R.id.mainframe_error_container_id);
        if (this.errorView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
            LogUtils.i(TAG,"errorLayoutRes:"+errorLayoutRes);
            mLayoutInflater.inflate(errorLayoutRes, mFrameLayout, true);
            final int index = this.indexOfChild(mViewStub);
            this.removeViewInLayout(mViewStub);
            final ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                this.addView(mFrameLayout, index, layoutParams);
            } else {
                this.addView(mFrameLayout, index);
            }
        } else {
            mFrameLayout.addView(errorView);
        }

        mFrameLayout.setVisibility(View.VISIBLE);
        if (clickId != -1) {
            mFrameLayout.findViewById(clickId)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getWebView() != null) {
                                getWebView().reload();
                            }
                        }
                    });
        } else {

            mFrameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getWebView() != null) {
                        getWebView().reload();
                    }

                }
            });
        }
    }

    void hidePageMainFrameError() {
        View mView = null;
        if ((mView = this.findViewById(R.id.mainframe_error_container_id)) != null) {
            mView.setVisibility(View.GONE);
        }
    }

    void setErrorView(@NonNull View errorView) {
        this.errorView = errorView;
    }

    void setErrorLayoutRes(@LayoutRes int resLayout, @IdRes int id) {
        this.clickId = id;
        if (this.clickId <= 0) {
            this.clickId = -1;
        }
        this.errorLayoutRes = resLayout;
        if(this.errorLayoutRes<=0){
            this.errorLayoutRes=R.layout.agentweb_error_page;
        }
    }

    @Override
    public AgentWebUIController provide() {
        return this.mAgentWebUIController;
    }


    void bindWebView(WebView view) {
        if (this.mWebView == null) {
            this.mWebView = view;
        }
    }

    public WebView getWebView() {
        return this.mWebView;
    }


}
