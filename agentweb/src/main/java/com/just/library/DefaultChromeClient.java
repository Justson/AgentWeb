package com.just.library;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class DefaultChromeClient extends ChromeClientProgress {


    private Activity mActivity;
    private AlertDialog promptDialog = null;
    private AlertDialog confirmDialog=null;
    private JsResult pJsResult=null;
    private JsResult cJsResult=null;
    private ChromeClientCallbackManager mChromeClientCallbackManager;
    public DefaultChromeClient(Activity activity, IndicatorController indicatorController,ChromeClientCallbackManager chromeClientCallbackManager) {
        super(indicatorController);
        this.mActivity = activity;
        this.mChromeClientCallbackManager=chromeClientCallbackManager;
    }


    @Override
    public void onReceivedTitle(WebView view, String title) {
        ChromeClientCallbackManager.ReceivedTitleCallback mCallback=null;
        if(mChromeClientCallbackManager!=null&&(mCallback=mChromeClientCallbackManager.getReceivedTitleCallback())!=null)
            mCallback.onReceivedTitle(view,title);

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

        //
        AgentWebUtils.show(view,
                message,
                Snackbar.LENGTH_SHORT,
                Color.WHITE,
                mActivity.getResources().getColor(R.color.black),
                null,
                -1,
                null);
        result.confirm();

        return true;
    }


    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

            showJsPrompt(message,result,defaultValue);
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        showJsConfirm(message,result);
        return true;
    }



    private void toDismissDialog(Dialog dialog) {
        if (dialog != null)
            dialog.dismiss();

    }


    private void showJsConfirm(String message,final JsResult result){

        if(confirmDialog==null)
            confirmDialog = new AlertDialog.Builder(mActivity)//
                    .setMessage(message)//
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            toCancelOrConfirmJsresult(cJsResult);
                        }
                    })//
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            toCancelOrConfirmJsresult(cJsResult);

                        }
                    }).create();
        this.cJsResult=result;
        confirmDialog.show();

    }

    private void toCancelOrConfirmJsresult(JsResult result) {
        if (result != null)
            result.confirm();
    }

    private void showJsPrompt(String message, final JsResult js,String defaultstr) {

        if (promptDialog == null) {

            final EditText et = new EditText(mActivity);
            et.setText(defaultstr);
            promptDialog = new AlertDialog.Builder(mActivity)//
                    .setView(et)//
                    .setTitle(message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);
                            toCancelOrConfirmJsresult(pJsResult);
                        }
                    })//
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);

                            toCancelOrConfirmJsresult(pJsResult);

                        }
                    }).create();
        }
        this.pJsResult=js;
        promptDialog.show();


    }
}
