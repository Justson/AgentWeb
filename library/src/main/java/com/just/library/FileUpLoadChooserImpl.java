package com.just.library;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
    private AlertDialog mAlertDialog;
    private static final String TAG = FileUpLoadChooserImpl.class.getSimpleName();
    private DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig mFileUploadMsgConfig;
    private Uri mUri;

    private boolean cameraState = false;

    FileUpLoadChooserImpl(Activity activity, ValueCallback<Uri> callback, DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig fileUploadMsgConfig) {
        this.mActivity = activity;
        this.mUriValueCallback = callback;
        isL = false;
        this.mFileUploadMsgConfig = fileUploadMsgConfig;
        jsChannel = false;
    }

    FileUpLoadChooserImpl(WebView webView, Activity activity, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams, DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig fileUploadMsgConfig) {

        jsChannel = false;
        this.mActivity = activity;
        this.mUriValueCallbacks = valueCallback;
        this.mFileChooserParams = fileChooserParams;
        isL = true;
        this.mFileUploadMsgConfig = fileUploadMsgConfig;
    }

    FileUpLoadChooserImpl(Activity activity, JsChannelCallback jsChannelCallback, DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig fileUploadMsgConfig) {
        if (jsChannelCallback == null)
            throw new NullPointerException("jsChannelCallback can not null");
        jsChannel = true;
        this.mJsChannelCallback = jsChannelCallback;
        this.mActivity = activity;
        this.mFileUploadMsgConfig = fileUploadMsgConfig;

    }

    @Override
    public void openFileChooser() {
        //
        openFileChooserInternal();

    }

    private void realOpenFileChooser() {
        if (isL && mFileChooserParams != null)
            mActivity.startActivityForResult(mFileChooserParams.createIntent(), REQUEST_CODE);
        else
            this.openRealFileChooser();
    }

    private void openFileChooserInternal() {

        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(mActivity)//
                    .setSingleChoiceItems(mFileUploadMsgConfig.getMedias(), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                            LogUtils.i(TAG, "which:" + which);
                            if (which == 1) {
                                cameraState = false;
                                realOpenFileChooser();
                            } else {
                                cameraState = true;
                                realOpenCamera();
                            }
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            cancel();
                        }
                    }).create();
        mAlertDialog.show();


    }

    private void realOpenCamera() {

        if (mActivity == null)
            return;
        Intent intent = new Intent();
        // 指定开启系统相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mUri = Uri.fromFile(AgentWebUtils.getImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        mActivity.startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data) {

        LogUtils.i(TAG, "request:" + requestCode + "  result:" + resultCode + "  data:" + data);
        if (REQUEST_CODE != requestCode)
            return;

        if (resultCode == Activity.RESULT_CANCELED) {
            cancel();
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            if (isL)
                handleAboveL(cameraState ? new Uri[]{mUri} : handleData(data));
            else if (jsChannel)
                convertFileAndCallBack(cameraState ? new Uri[]{mUri} : handleData(data));
            else {
                if (cameraState && mUriValueCallback != null)
                    mUriValueCallback.onReceiveValue(mUri);
                else
                    handleDataBelow(data);
            }

        }


    }

    private void cancel() {
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

        LogUtils.i("Info", "handleDataBelow  -- >uri:" + mUri + "  mUriValueCallback:" + mUriValueCallback);
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
