package com.just.library;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.Queue;

import static com.just.library.ActionActivity.KEY_ACTION;
import static com.just.library.ActionActivity.KEY_URI;

/**
 * Created by cenxiaozhong on 2017/5/22.
 */

public class FileUpLoadChooserImpl implements IFileUploadChooser {

    private Activity mActivity;
    private ValueCallback<Uri> mUriValueCallback;
    private ValueCallback<Uri[]> mUriValueCallbacks;
    public static final int REQUEST_CODE = 0x254;
    private boolean isL = false;
    private WebChromeClient.FileChooserParams mFileChooserParams;
    private JsChannelCallback mJsChannelCallback;
    private boolean jsChannel = false;
    private AlertDialog mAlertDialog;
    private static final String TAG = FileUpLoadChooserImpl.class.getSimpleName();
    private DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig mFileUploadMsgConfig;
    private Uri mUri;
    private WebView mWebView;
    private boolean cameraState = false;
    private PermissionInterceptor mPermissionInterceptor;

    public FileUpLoadChooserImpl(Builder builder) {

        this.mActivity = builder.mActivity;
        this.mUriValueCallback = builder.mUriValueCallback;
        this.mUriValueCallbacks = builder.mUriValueCallbacks;
        this.isL = builder.isL;
        this.mFileChooserParams = builder.mFileChooserParams;
        this.mJsChannelCallback = builder.mJsChannelCallback;
        this.mFileUploadMsgConfig = builder.mFileUploadMsgConfig;
        this.mWebView = builder.mWebView;

    }


    @Override
    public void openFileChooser() {
        openFileChooserInternal();
    }

    private void fileChooser() {
        ActionActivity.Action mAction = new ActionActivity.Action();
        mAction.setAction(ActionActivity.Action.ACTION_FILE);
        ActionActivity.setFileDataListener(mFileDataListener);
        mActivity.startActivity(new Intent(mActivity, ActionActivity.class).putExtra(KEY_ACTION, mAction));

    }

    private final ActionActivity.FileDataListener mFileDataListener = new ActionActivity.FileDataListener() {
        @Override
        public void onFileDataResult(int requestCode, int resultCode, Intent data) {
            fetchFilePathFromIntent(requestCode, resultCode, data);
        }
    };

    private void checkPermission() {


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
                                fileChooser();
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


        ActionActivity.Action mAction=new ActionActivity.Action();
        mAction.setAction(ActionActivity.Action.ACTION_CAMERA);
        ActionActivity.setFileDataListener(this.mFileDataListener);
        mActivity.startActivity(new Intent(mActivity,ActionActivity.class).putExtra(KEY_ACTION,mAction));

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
                handleAboveL(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
            else if (jsChannel)
                convertFileAndCallBack(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
            else {
                if (cameraState && mUriValueCallback != null)
                    mUriValueCallback.onReceiveValue((Uri)data.getParcelableExtra(KEY_URI));
                else
                    handleBelowLData(data);
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

        new CovertFileThread(this.mJsChannelCallback, paths).start();

    }


    private void handleBelowLData(Intent data) {
        Uri mUri = data == null ? null : data.getData();

        LogUtils.i(TAG, "handleBelowLData  -- >uri:" + mUri + "  mUriValueCallback:" + mUriValueCallback);
        if (mUriValueCallback != null)
            mUriValueCallback.onReceiveValue(mUri);

    }

    private Uri[] processData(Intent data) {

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

    public static final class Builder {

        private Activity mActivity;
        private ValueCallback<Uri> mUriValueCallback;
        private ValueCallback<Uri[]> mUriValueCallbacks;
        private boolean isL = false;
        private WebChromeClient.FileChooserParams mFileChooserParams;
        private JsChannelCallback mJsChannelCallback;
        private boolean jsChannel = false;
        private DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig mFileUploadMsgConfig;
        private WebView mWebView;
        private PermissionInterceptor mPermissionInterceptor;

        public Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setUriValueCallback(ValueCallback<Uri> uriValueCallback) {
            mUriValueCallback = uriValueCallback;
            isL = false;
            jsChannel = false;
            mUriValueCallbacks = null;
            mJsChannelCallback = null;
            return this;
        }

        public Builder setUriValueCallbacks(ValueCallback<Uri[]> uriValueCallbacks) {
            mUriValueCallbacks = uriValueCallbacks;
            isL = true;
            mUriValueCallback = null;
            mJsChannelCallback = null;
            jsChannel = false;
            return this;
        }


        public Builder setFileChooserParams(WebChromeClient.FileChooserParams fileChooserParams) {
            mFileChooserParams = fileChooserParams;
            return this;
        }

        public Builder setJsChannelCallback(JsChannelCallback jsChannelCallback) {
            mJsChannelCallback = jsChannelCallback;
            jsChannel = true;
            mUriValueCallback = null;
            mUriValueCallbacks = null;
            return this;
        }


        public Builder setFileUploadMsgConfig(DefaultMsgConfig.ChromeClientMsgCfg.FileUploadMsgConfig fileUploadMsgConfig) {
            mFileUploadMsgConfig = fileUploadMsgConfig;
            return this;
        }


        public Builder setWebView(WebView webView) {
            mWebView = webView;
            return this;
        }


        public FileUpLoadChooserImpl build() {
            return new FileUpLoadChooserImpl(this);
        }
    }

}
