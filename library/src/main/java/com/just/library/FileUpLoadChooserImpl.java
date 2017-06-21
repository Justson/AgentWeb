package com.just.library;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.Queue;

/**
 * Created by cenxiaozhong on 2017/5/22.
 */

public class FileUpLoadChooserImpl implements IFileUploadChooser {

    private Activity mActivity;
    private ValueCallback<Uri> mUriValueCallback;
    private ValueCallback<Uri[]> mUriValueCallbacks;
    private Fragment mFragment;
    //1表示fragment 0 表示activity
    private int tag = 0;
    private static final int REQUEST_CODE = 0x254;
    private boolean isL = false;

    private WebChromeClient.FileChooserParams mFileChooserParams;
    private JsChannelCallback mJsChannelCallback;
    private boolean jsChannel = false;

    public FileUpLoadChooserImpl(Activity activity, ValueCallback<Uri> callback) {
        this.mActivity = activity;
        this.mUriValueCallback = callback;
        isL = false;
        jsChannel = false;
    }

    public FileUpLoadChooserImpl(WebView webView, Activity activity, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        jsChannel = false;
        this.mActivity = activity;
        this.mUriValueCallbacks = valueCallback;
        this.mFileChooserParams = fileChooserParams;
        isL = true;
    }

    public FileUpLoadChooserImpl(Activity activity, JsChannelCallback jsChannelCallback) {
        if (jsChannelCallback == null)
            throw new NullPointerException("jsChannelCallback can not null");
        jsChannel = true;
        this.mJsChannelCallback = jsChannelCallback;
        this.mActivity = activity;

    }

    @Override
    public void openFileChooser() {
        if (isL && mFileChooserParams != null)
            mActivity.startActivityForResult(mFileChooserParams.createIntent(), REQUEST_CODE);
        else
            this.openRealFileChooser();
    }


    @Override
    public void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "request:" + requestCode + "  result:" + resultCode + "  data:" + data);
        if (REQUEST_CODE != requestCode)
            return;

        if (resultCode == Activity.RESULT_CANCELED) {


            if (jsChannel) {
                mJsChannelCallback.call(null);
                return;
            }
            if (mUriValueCallback != null)
                mUriValueCallback.onReceiveValue(null);
            if (mUriValueCallbacks != null)
                mUriValueCallbacks.onReceiveValue(null);
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            if (isL)
                handleAboveL(handleData(data));
            else if (jsChannel)
                convertFileAndCallBack(handleData(data));
            else
                handleDataBelow(data);

        }


    }

    private void convertFileAndCallBack(final Uri[] uris) {

        String[] paths = null;
        if (uris == null || uris.length == 0 || (paths = AgentWebUtils.uriToPath(mActivity, uris)) == null || paths.length == 0) {
            mJsChannelCallback.call(null);
            return;
        }

//        Log.i("Info", "length:" + paths.length);
        new CovertFileThread(this.mJsChannelCallback, paths).start();

    }


    private void handleDataBelow(Intent data) {
        Uri mUri = data == null ? null : data.getData();

        LogUtils.i("Info","handleDataBelow  -- >uri:"+mUri+"  mUriValueCallback:"+mUriValueCallback);
        if (mUriValueCallback != null)
            mUriValueCallback.onReceiveValue(mUri);

    }

    private Uri[] handleData(Intent data) {

        Uri[] datas = null;
        if (data == null) {
            return datas;
        }
        String target = data.getDataString();
        if (!TextUtils.isEmpty(target)) {
            return datas = new Uri[]{Uri.parse(target)};
        }
        ClipData mClipData = null;
        if (mClipData != null && mClipData.getItemCount() > 0) {
            datas = new Uri[mClipData.getItemCount()];
            for (int i = 0; i < mClipData.getItemCount(); i++) {

                ClipData.Item mItem = mClipData.getItemAt(i);
                datas[i] = mItem.getUri();

            }
        }
        return datas;


    }

    private void handleAboveL(Uri[] datas) {
        if (mUriValueCallbacks == null)
            return;
        mUriValueCallbacks.onReceiveValue(datas == null ? new Uri[]{} : datas);
    }


    private void openRealFileChooser() {

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        mActivity.startActivityForResult(Intent.createChooser(i,
                "File Chooser"), REQUEST_CODE);
    }

    static class CovertFileThread extends Thread {

        private JsChannelCallback mJsChannelCallback;
        private String[] paths;

        private CovertFileThread(JsChannelCallback jsChannelCallback, String[] paths) {
            this.mJsChannelCallback = jsChannelCallback;
            this.paths = paths;
        }

        @Override
        public void run() {


            try {
                Queue<FileParcel> mQueue = AgentWebUtils.convertFile(paths);
                String result = AgentWebUtils.FileParcetoJson(mQueue);
                LogUtils.i("Info", "result:" + result);
                if (mJsChannelCallback != null)
                    mJsChannelCallback.call(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    interface JsChannelCallback {

        void call(String value);
    }
}
