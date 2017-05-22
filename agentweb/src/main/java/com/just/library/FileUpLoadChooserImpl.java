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

    public FileUpLoadChooserImpl(Activity activity, ValueCallback<Uri> callback) {
        this.mActivity = activity;
        this.mUriValueCallback = callback;
        isL=false;
    }

    public FileUpLoadChooserImpl(WebView webView, Activity activity, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        this.mActivity = activity;
        this.mUriValueCallbacks = valueCallback;
        this.mFileChooserParams = fileChooserParams;
        isL = true;
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
        if (REQUEST_CODE != requestCode || resultCode != mActivity.RESULT_OK)
            return;

        if (isL)
            handleDataOverL(data);
        else
            handleDataBelow(data);

    }

    private void handleDataBelow(Intent data) {
        Uri mUri=data.getData();

        if(mUriValueCallback!=null)
            mUriValueCallback.onReceiveValue(mUri);

    }

    private void handleDataOverL(Intent data) {

        Uri[] datas = null;

        ClipData mClipData = data.getClipData();
        if (mClipData != null && mClipData.getItemCount() > 0) {
            datas = new Uri[mClipData.getItemCount()];
            for (int i = 0; i < mClipData.getItemCount(); i++) {

                ClipData.Item mItem = mClipData.getItemAt(i);
                datas[i] = mItem.getUri();

            }
        }
        String str = data.getDataString();
        if (!TextUtils.isEmpty(str))
            datas = new Uri[]{Uri.parse(str)};

        Log.i("Info", "str:" + datas.length + "   d:" + datas[0]);
        if (mUriValueCallbacks!=null&&datas != null && datas.length > 0)
            mUriValueCallbacks.onReceiveValue(datas);

        mUriValueCallbacks = null;


    }


    public void openRealFileChooser() {

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(i,
                "File Chooser"), REQUEST_CODE);
    }
}
