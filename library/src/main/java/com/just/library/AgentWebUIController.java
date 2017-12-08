package com.just.library;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的一些界面
 */
public abstract class AgentWebUIController {

    public static boolean hasDesignLib = false;
    protected Activity mActivity;
    protected WebParentLayout mWebParentLayout;
    private volatile boolean isBindWebParent = false;
    protected AgentWebUIController mAgentWebUIControllerDelegate;
    protected String TAG = this.getClass().getSimpleName();

    static {
        try {
            Class.forName("android.support.design.widget.Snackbar");
            hasDesignLib = true;
        } catch (Throwable ignore) {
            hasDesignLib = false;
        }
    }


    protected AgentWebUIController create() {
        return hasDesignLib ? new DefaultDesignUIController() : new DefaultUIController();
    }

    public abstract void onJsAlert(WebView view, String url, String message);


    public abstract void onJsConfirm(WebView view, String url, String message);


    public abstract void onJsPrompt(WebView view, String url, String message, String defaultValue);

    protected AgentWebUIController getDelegate() {
        AgentWebUIController mAgentWebUIController = this.mAgentWebUIControllerDelegate;
        if (mAgentWebUIController == null) {
            this.mAgentWebUIControllerDelegate = mAgentWebUIController = create();
        }
        return mAgentWebUIController;
    }

    synchronized void bindWebParent(WebParentLayout webParentLayout, Activity activity) {

        if (!isBindWebParent) {
            isBindWebParent = true;
            this.mWebParentLayout = webParentLayout;
            this.mActivity = activity;
            getDelegate().bindWebParent(webParentLayout, activity);
        }

    }


}
