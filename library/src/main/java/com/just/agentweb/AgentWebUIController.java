package com.just.agentweb;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的界面
 */
public abstract class AgentWebUIController {

    public static boolean hasDesignLib = false;
    private Activity mActivity;
    private WebParentLayout mWebParentLayout;
    private volatile boolean isBindWebParent = false;
    protected AgentWebUIController mAgentWebUIControllerDelegate;
    protected String TAG = this.getClass().getSimpleName();

    static {
        try {
            Class.forName("android.support.design.widget.Snackbar");
            Class.forName("android.support.design.widget.BottomSheetDialog");
            hasDesignLib = true;
        } catch (Throwable ignore) {
            hasDesignLib = false;
        }
    }


    protected AgentWebUIController create() {
        return hasDesignLib ? new DefaultDesignUIController() : new DefaultUIController();
    }

    protected AgentWebUIController getDelegate() {
        AgentWebUIController mAgentWebUIController = this.mAgentWebUIControllerDelegate;
        if (mAgentWebUIController == null) {
            this.mAgentWebUIControllerDelegate = mAgentWebUIController = create();
        }
        return mAgentWebUIController;
    }

    final synchronized void bindWebParent(WebParentLayout webParentLayout, Activity activity) {
        if (!isBindWebParent) {
            isBindWebParent = true;
            this.mWebParentLayout = webParentLayout;
            this.mActivity = activity;
            bindSupportWebParent(webParentLayout, activity);
        }
    }

    protected void toDismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void toShowDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    protected abstract void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity);

    public abstract void onJsAlert(WebView view, String url, String message);

    public abstract void onAskOpenOtherApp(WebView view, String url, String message,String confirm,String title,Handler.Callback callback);

    public abstract void onJsConfirm(WebView view, String url, String message, JsResult jsResult);

    public abstract void showChooser(WebView view, String url, String[] ways, Handler.Callback callback);

    public abstract void onForceDownloadAlert(String url, DefaultMsgConfig.DownLoadMsgConfig message, Handler.Callback callback);

    public abstract void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult);

    public abstract void onMainFrameError(WebView view, int errorCode, String description, String failingUrl);

    public abstract void onShowMainFrame();
    /**
     * @param message 消息
     * @param intent 说明message的来源
     */
    public abstract void showMessage(String message, String intent);


}
