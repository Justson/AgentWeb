package com.just.library;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultDesignUIController extends AgentWebUIController {


    protected AlertDialog confirmDialog;;

    @Override
    public void onJsAlert(WebView view, String url, String message) {

        Activity mActivity = this.mActivity;
        if (mActivity == null || mActivity.isFinishing()) {
            return ;
        }
        try {
            AgentWebUtils.show(view,
                    message,
                    Snackbar.LENGTH_SHORT,
                    Color.WHITE,
                    mActivity.getResources().getColor(R.color.black),
                    null,
                    -1,
                    null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (LogUtils.isDebug())
                LogUtils.i(TAG, throwable.getMessage());
        }

    }


    @Override
    public void onJsConfirm(WebView view, String url, String message) {
        Activity mActivity = this.mActivity;
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }

        if (confirmDialog == null) {
            confirmDialog = new AlertDialog.Builder(mActivity)//
                    .setMessage(message)//
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                        }
                    })//
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);

                        }
                    }).create();
        }
        confirmDialog.setMessage(message);
        confirmDialog.show();


    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue) {

    }

    private void toDismissDialog(AlertDialog confirmDialog) {

    }
}
