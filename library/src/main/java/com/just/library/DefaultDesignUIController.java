package com.just.library;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultDesignUIController extends AgentWebUIController {


    protected AlertDialog confirmDialog;
    private JsPromptResult pJsResult = null;
    private JsResult cJsResult = null;
    private AlertDialog promptDialog = null;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    public void onJsAlert(WebView view, String url, String message) {

        onJsAlertInternal(view, message);

    }

    private void onJsAlertInternal(WebView view, String message) {
        Activity mActivity = this.mActivity;
        if (mActivity == null || mActivity.isFinishing()) {
            return;
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
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {

        onJsConfirmInternal(message, jsResult);

    }

    private static final int RECYCLERVIEW_ID = 0x1001;

    @Override
    public void showChooser(WebView view, String url, String[] ways, Handler.Callback callback) {
        showChooserInternal(view, url, ways, callback);
    }

    private void showChooserInternal(WebView view, String url, final String[] ways, Handler.Callback callback) {


        LogUtils.i(TAG, "url:" + url + "  ways:" + ways[0]);
        RecyclerView mRecyclerView;
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(mActivity);
            mRecyclerView = new RecyclerView(mActivity);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            mRecyclerView.setId(RECYCLERVIEW_ID);
            mBottomSheetDialog.setContentView(mRecyclerView);
        }
        mRecyclerView = (RecyclerView) mBottomSheetDialog.getDelegate().findViewById(RECYCLERVIEW_ID);
        mRecyclerView.setAdapter(getAdapter(ways, callback));
        mBottomSheetDialog.show();


    }

    private RecyclerView.Adapter getAdapter(final String[] ways, final Handler.Callback callback) {
        return new RecyclerView.Adapter<BottomSheetHolder>() {
            @Override
            public BottomSheetHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new BottomSheetHolder(mLayoutInflater.inflate( android.R.layout.simple_list_item_1, viewGroup,false));
            }

            @Override
            public void onBindViewHolder(BottomSheetHolder bottomSheetHolder, final int i) {
                TypedValue outValue = new TypedValue();
                mActivity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                bottomSheetHolder.mTextView.setBackgroundResource(outValue.resourceId);
                bottomSheetHolder.mTextView.setText(ways[i]);

                bottomSheetHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
                            mBottomSheetDialog.dismiss();
                        }
                        Message mMessage = Message.obtain();
                        mMessage.arg1 = i;
                        callback.handleMessage(mMessage);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return ways.length;
            }
        };
    }

    private static class BottomSheetHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public BottomSheetHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
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
                    }).create();
        }
        confirmDialog.setMessage(message);
        this.cJsResult = jsResult;
        confirmDialog.show();
    }

    private void toCancelJsresult(JsResult result) {
        if (result != null)
            result.cancel();
    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {

        onJsPromptInternal(message, defaultValue, jsPromptResult);
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
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);
                            toCancelJsresult(pJsResult);
                        }
                    })//
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);

                            if (pJsResult != null)
                                pJsResult.confirm(et.getText().toString());

                        }
                    }).create();
        }
        this.pJsResult = jsPromptResult;
        promptDialog.show();
    }

    private Activity mActivity = null;
    private WebParentLayout mWebParentLayout;
    private LayoutInflater mLayoutInflater;
    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
        this.mActivity = activity;
        this.mWebParentLayout = webParentLayout;
        mLayoutInflater=LayoutInflater.from(mActivity);
    }

    private void toDismissDialog(AlertDialog confirmDialog) {

        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }

    }


}
