package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的一些UI
 */
public abstract class AgentWebUIController {

    private static boolean hasDesignLib = false;

    static {
        try {
            Class.forName("android.support.design.widget.Snackbar");
            hasDesignLib = true;
        } catch (Throwable ignore) {
            hasDesignLib = false;
        }
    }

    private AgentWebUIController mAgentWebUIControllerDelegate;

    public AgentWebUIController create() {
        return hasDesignLib ? new DefaultDesignUIController() : new DefaultUIController();
    }

    protected void onJsAlert(WebView view, String url, String message) {

        getDelegate().onJsAlert(view, url, message);
    }


    protected void onJsConfirm(WebView view, String url, String message) {
        getDelegate().onJsConfirm(view, url, message);
    }


    public void onJsPrompt(WebView view, String url, String message, String defaultValue) {
        getDelegate().onJsPrompt(view, url, message, defaultValue);
    }

    public AgentWebUIController getDelegate() {
        AgentWebUIController mAgentWebUIController = this.mAgentWebUIControllerDelegate;
        if (mAgentWebUIController == null) {
            this.mAgentWebUIControllerDelegate = mAgentWebUIController = create();
        }
        return mAgentWebUIController;
    }
}
