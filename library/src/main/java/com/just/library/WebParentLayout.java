package com.just.library;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class WebParentLayout extends FrameLayout implements Provider<AgentWebUIController> {
    private AgentWebUIController mAgentWebUIController = null;
    private String TAG=this.getClass().getSimpleName();

    private WebView mWebView;
    public WebParentLayout(@NonNull Context context) {
        this(context, null);
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("WebParentLayout context must be activity or activity sub class .");
        }
    }

    void bindController(AgentWebUIController agentWebUIController) {
        LogUtils.i(TAG,"bindController:"+agentWebUIController);
        this.mAgentWebUIController = agentWebUIController;
        this.mAgentWebUIController.bindWebParent(this, (Activity) getContext());
    }

    public void showPagerMainFrameError(@LayoutRes int layoutRes){

    }

    @Override
    public AgentWebUIController provide() {
        return this.mAgentWebUIController;
    }


    void bindWebView(WebView view){
        if(this.mWebView==null){
            this.mWebView =view;
        }
    }
    public WebView getWebView(){
        return this.mWebView;
    }
}
