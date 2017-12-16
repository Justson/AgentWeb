package com.just.agentweb;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultUIController extends AgentWebUIController {

    private AlertDialog mAlertDialog;

    protected AlertDialog confirmDialog;
    private JsPromptResult pJsResult = null;
    private JsResult cJsResult = null;
    private AlertDialog promptDialog = null;
    private Activity mActivity;
    private WebParentLayout mWebParentLayout;
    private AlertDialog askOpenOtherAppDialog = null;

    @Override
    public void onJsAlert(WebView view, String url, String message) {
        AgentWebUtils.toastShowShort(view.getContext().getApplicationContext(), message);
    }


    @Override
    public void onAskOpenOtherApp(WebView view, String url, String message, String confirm, String title, final Handler.Callback callback) {

        LogUtils.i(TAG, "onAskOpenOtherApp");
        if (askOpenOtherAppDialog == null) {
            askOpenOtherAppDialog = new AlertDialog
                    .Builder(mActivity)//
                    .setMessage(message)//
                    .setTitle(title)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.handleMessage(Message.obtain(null, -1));
                        }
                    })//
                    .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.handleMessage(Message.obtain(null, 1));
                        }
                    })
                    .create();
        }
        askOpenOtherAppDialog.show();
    }

    @Override
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
        onJsConfirmInternal(message, jsResult);
    }

    @Override
    public void showChooser(WebView view, String url, final String[] ways, final Handler.Callback callback) {
        showChooserInternal(ways, callback);
    }

    @Override
    public void onForceDownloadAlert(String url, DefaultMsgConfig.DownLoadMsgConfig message, final Handler.Callback callback) {

        onForceDownloadAlertInternal(message, callback);

    }

    private void onForceDownloadAlertInternal(DefaultMsgConfig.DownLoadMsgConfig message, final Handler.Callback callback) {
        Activity mActivity;
        if ((mActivity = this.mActivity) == null || mActivity.isFinishing())
            return;

        AlertDialog mAlertDialog = null;
        mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setTitle(message.getTips())//
                .setMessage(message.getHoneycomblow())//
                .setNegativeButton(message.getDownLoad(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                        if (callback != null)
                            callback.handleMessage(Message.obtain());
                    }
                })//
                .setPositiveButton(message.getCancel(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null)
                            dialog.dismiss();
                    }
                }).create();

        mAlertDialog.show();
    }

    private void showChooserInternal(String[] ways, final Handler.Callback callback) {
        mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setSingleChoiceItems(ways, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LogUtils.i(TAG, "which:" + which);
                        if (callback != null) {
                            Message mMessage = Message.obtain();
                            mMessage.what = which;
                            callback.handleMessage(mMessage);
                        }

                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.handleMessage(Message.obtain(null, -1));
                        }
                    }
                }).create();
        mAlertDialog.show();
    }

    private void onJsConfirmInternal(String message, JsResult jsResult) {
        LogUtils.i(TAG, "activity:" + mActivity.hashCode() + "  ");
        Activity mActivity = this.mActivity;
        if (mActivity == null || mActivity.isFinishing()) {
            toCancelJsresult(jsResult);
            return;
        }

        if (confirmDialog == null) {
            confirmDialog = new AlertDialog.Builder(mActivity)//
                    .setMessage(message)//
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            toCancelJsresult(cJsResult);
                        }
                    })//
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            if (cJsResult != null) {
                                cJsResult.confirm();
                            }

                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            toCancelJsresult(cJsResult);
                        }
                    })
                    .create();

        }
        confirmDialog.setMessage(message);
        this.cJsResult = jsResult;
        confirmDialog.show();
    }


    private void onJsPromptInternal(String message, String defaultValue, JsPromptResult jsPromptResult) {
        Activity mActivity = this.mActivity;
        if (mActivity == null || mActivity.isFinishing()) {
            jsPromptResult.cancel();
            return;
        }
        if (promptDialog == null) {

            final EditText et = new EditText(mActivity);
            et.setText(defaultValue);
            promptDialog = new AlertDialog.Builder(mActivity)//
                    .setView(et)//
                    .setTitle(message)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);
                            toCancelJsresult(pJsResult);
                        }
                    })//
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);

                            if (pJsResult != null)
                                pJsResult.confirm(et.getText().toString());

                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            toCancelJsresult(pJsResult);
                        }
                    })
                    .create();
        }
        this.pJsResult = jsPromptResult;
        promptDialog.show();
    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
        onJsPromptInternal(message, defaultValue, jsPromptResult);
    }

    @Override
    public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {

        if(mWebParentLayout!=null){
            mWebParentLayout.showPageMainFrameError();
        }
    }

    @Override
    public void onShowMainFrame() {
            if(mWebParentLayout!=null){
                mWebParentLayout.hidePageMainFrameError();
            }
    }

    @Override
    public void showMessage(String message, String from) {
        AgentWebUtils.toastShowShort(mActivity.getApplicationContext(), message);
    }

    private void toCancelJsresult(JsResult result) {
        if (result != null)
            result.cancel();
    }


    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
        this.mActivity = activity;
        this.mWebParentLayout = webParentLayout;

    }
}
