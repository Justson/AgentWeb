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
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @author cenxiaozhong
 * @date 2017/12/8
 * @since 3.0.0
 */
public class DefaultDesignUIController extends DefaultUIController {


    private BottomSheetDialog mBottomSheetDialog;
    private static final int RECYCLERVIEW_ID = 0x1001;
    private Activity mActivity = null;
    private WebParentLayout mWebParentLayout;
    private LayoutInflater mLayoutInflater;

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
            if (LogUtils.isDebug()){
                throwable.printStackTrace();
            }
        }
    }


    @Override
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
        super.onJsConfirm(view, url, message, jsResult);
    }


    @Override
    public void onSelectItemsPrompt(WebView view, String url, String[] ways, Handler.Callback callback) {
        showChooserInternal(view, url, ways, callback);
    }

    @Override
    public void onForceDownloadAlert(String url, final Handler.Callback callback) {
        super.onForceDownloadAlert(url, callback);
    }

    private void showChooserInternal(WebView view, String url, final String[] ways, final Handler.Callback callback) {


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
        mBottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (callback != null) {
                    callback.handleMessage(Message.obtain(null, -1));
                }
            }
        });
        mBottomSheetDialog.show();


    }

    private RecyclerView.Adapter getAdapter(final String[] ways, final Handler.Callback callback) {
        return new RecyclerView.Adapter<BottomSheetHolder>() {
            @Override
            public BottomSheetHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new BottomSheetHolder(mLayoutInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false));
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
                        mMessage.what = i;
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

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
        super.onJsPrompt(view, url, message, defaultValue, jsPromptResult);
    }


    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
        super.bindSupportWebParent(webParentLayout, activity);
        this.mActivity = activity;
        this.mWebParentLayout = webParentLayout;
        mLayoutInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public void onShowMessage(String message, String from) {
        if (!TextUtils.isEmpty(from) && from.contains("performDownload")) {
            return;
        }
        onJsAlertInternal(mWebParentLayout.getWebView(), message);
    }
}
