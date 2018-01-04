package com.just.agentweb;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static com.just.agentweb.ActionActivity.KEY_ACTION;
import static com.just.agentweb.ActionActivity.KEY_FILE_CHOOSER_INTENT;
import static com.just.agentweb.ActionActivity.KEY_FROM_INTENTION;
import static com.just.agentweb.ActionActivity.KEY_URI;
import static com.just.agentweb.ActionActivity.start;

/**
 * Created by cenxiaozhong on 2017/5/22.
 * source code  https://github.com/Justson/AgentWeb
 */

public class FileUpLoadChooserImpl implements IFileUploadChooser {

    private Activity mActivity;
    private ValueCallback<Uri> mUriValueCallback;
    private ValueCallback<Uri[]> mUriValueCallbacks;
    public static final int REQUEST_CODE = 0x254;
    private boolean isAboveL = false;
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
    private int FROM_INTENTION_CODE = 21;
    private WeakReference<AgentWebUIController> mAgentWebUIController = null;
    private String acceptType = "*/*";
    public static int MAX_WAIT_PHOTO_MS = 8 * 1000;

    public FileUpLoadChooserImpl(Builder builder) {

        this.mActivity = builder.mActivity;
        this.mUriValueCallback = builder.mUriValueCallback;
        this.mUriValueCallbacks = builder.mUriValueCallbacks;
        this.isAboveL = builder.isL;
        this.jsChannel = builder.jsChannel;
        this.mFileChooserParams = builder.mFileChooserParams;
        this.mJsChannelCallback = builder.mJsChannelCallback;
        this.mFileUploadMsgConfig = builder.mFileUploadMsgConfig;
        this.mWebView = builder.mWebView;
        this.mPermissionInterceptor = builder.mPermissionInterceptor;
        this.acceptType = builder.acceptType;
        mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(this.mWebView));
    }


    @Override
    public void openFileChooser() {
        if (!AgentWebUtils.isUIThread()) {
            AgentWebUtils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    openFileChooser();
                }
            });
            return;
        }

        openFileChooserInternal();
    }

    private void fileChooser() {

        List<String> permission = null;
        if (AgentWebUtils.getDeniedPermissions(mActivity, AgentWebPermissions.STORAGE).isEmpty()) {
            touchOffFileChooserAction();
        } else {
            Action mAction = Action.createPermissionsAction(AgentWebPermissions.STORAGE);
            mAction.setFromIntention(FROM_INTENTION_CODE >> 2);
            ActionActivity.setPermissionListener(mPermissionListener);
            ActionActivity.start(mActivity, mAction);
        }


    }

    private void touchOffFileChooserAction() {
        Action mAction = new Action();
        mAction.setAction(Action.ACTION_FILE);
        ActionActivity.setFileDataListener(getFileDataListener());
        mActivity.startActivity(new Intent(mActivity, ActionActivity.class).putExtra(KEY_ACTION, mAction)
                .putExtra(KEY_FILE_CHOOSER_INTENT, getFilechooserIntent()));
    }

    private Intent getFilechooserIntent() {
        Intent mIntent = null;
        if (isAboveL && mFileChooserParams != null && (mIntent = mFileChooserParams.createIntent()) != null) {
            if (mFileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
                mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            return mIntent;
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        if (TextUtils.isEmpty(this.acceptType)) {
            i.setType("*/*");
        } else {
            i.setType(this.acceptType);
        }
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return mIntent = Intent.createChooser(i, "");
    }

    private ActionActivity.FileDataListener getFileDataListener() {
        return new ActionActivity.FileDataListener() {
            @Override
            public void onFileDataResult(int requestCode, int resultCode, Intent data) {

                LogUtils.i(TAG, "request:" + requestCode + "  resultCode:" + resultCode);
                fetchFilePathFromIntent(requestCode, resultCode, data);
            }
        };
    }


    private void openFileChooserInternal() {


        // 是否直接打开文件选择器
        if (this.isAboveL && this.mFileChooserParams != null && this.mFileChooserParams.getAcceptTypes() != null) {
            boolean needCamera = false;
            String[] types = this.mFileChooserParams.getAcceptTypes();
            for (String typeTmp : types) {

                LogUtils.i(TAG, "typeTmp:" + typeTmp);
                if (TextUtils.isEmpty(typeTmp)) {
                    continue;
                }
                if (typeTmp.contains("*/") || typeTmp.contains("image/")) {
                    needCamera = true;
                    break;
                }
            }
            if (!needCamera) {
                touchOffFileChooserAction();
                return;
            }
        }
        if (!TextUtils.isEmpty(this.acceptType) && !this.acceptType.startsWith("*") && !this.acceptType.startsWith("image")) {
            touchOffFileChooserAction();
            return;
        }

        LogUtils.i(TAG, "controller:" + this.mAgentWebUIController.get() + "   acceptType:" + acceptType);
        if (this.mAgentWebUIController.get() != null) {
            this.mAgentWebUIController
                    .get()
                    .showChooser(this.mWebView, mWebView.getUrl(), mFileUploadMsgConfig.getMedias(), getCallBack());
            LogUtils.i(TAG, "open");
        }

    }


    public Handler.Callback getCallBack() {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        cameraState = true;
                        onCameraAction();

                        break;
                    case 1:
                        cameraState = false;
                        fileChooser();
                        break;
                    default:
                        cancel();
                        break;
                }
                return true;
            }
        };
    }


    private void onCameraAction() {

        if (mActivity == null)
            return;

        if (mPermissionInterceptor != null) {
            if (mPermissionInterceptor.intercept(FileUpLoadChooserImpl.this.mWebView.getUrl(), AgentWebPermissions.CAMERA, "camera")) {
                cancel();
                return;
            }

        }

        Action mAction = new Action();
        List<String> deniedPermissions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(deniedPermissions = checkNeedPermission()).isEmpty()) {
            mAction.setAction(Action.ACTION_PERMISSION);
            mAction.setPermissions(deniedPermissions.toArray(new String[]{}));
            mAction.setFromIntention(FROM_INTENTION_CODE >> 3);
            ActionActivity.setPermissionListener(this.mPermissionListener);
            start(mActivity, mAction);
        } else {
            openCameraAction();
        }

    }

    private List<String> checkNeedPermission() {

        List<String> deniedPermissions = new ArrayList<>();

        if (!AgentWebUtils.hasPermission(mActivity, AgentWebPermissions.CAMERA)) {
            deniedPermissions.add(AgentWebPermissions.CAMERA[0]);
        }
        if (!AgentWebUtils.hasPermission(mActivity, AgentWebPermissions.STORAGE)) {
            deniedPermissions.addAll(Arrays.asList(AgentWebPermissions.STORAGE));
        }
        return deniedPermissions;
    }

    private void openCameraAction() {
        Action mAction = new Action();
        mAction.setAction(Action.ACTION_CAMERA);
        ActionActivity.setFileDataListener(this.getFileDataListener());
        ActionActivity.start(mActivity, mAction);
    }

    private ActionActivity.PermissionListener mPermissionListener = new ActionActivity.PermissionListener() {

        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {

            boolean tag = true;
            tag = AgentWebUtils.hasPermission(mActivity, Arrays.asList(permissions)) ? true : false;
            permissionResult(tag, extras.getInt(KEY_FROM_INTENTION));

        }
    };

    private void permissionResult(boolean grant, int from_intention) {
        if (from_intention == FROM_INTENTION_CODE >> 2) {
            if (grant) {
                touchOffFileChooserAction();
            } else {
                cancel();
                LogUtils.i(TAG, "permission denied");
            }
        } else if (from_intention == FROM_INTENTION_CODE >> 3) {
            if (grant)
                openCameraAction();
            else {
                cancel();
                LogUtils.i(TAG, "permission denied");
            }
        }


    }

    @Override
    public void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data) {

        LogUtils.i(TAG, "request:" + requestCode + "  result:" + resultCode + "  data:" + data);
        if (REQUEST_CODE != requestCode) {
            return;
        }

        //用户已经取消
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            cancel();
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            cancel();
            return;
        }

        //通过JS获取文件
        if (jsChannel) {
            convertFileAndCallBack(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
            return;
        }

        //5.0以上系统通过input标签获取文件
        if (isAboveL) {
            handleAboveL(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data), cameraState);
            return;
        }


        //4.4以下系统通过input标签获取文件
        if (mUriValueCallback == null) {
            cancel();
            return;
        }

        if (cameraState) {
            mUriValueCallback.onReceiveValue((Uri) data.getParcelableExtra(KEY_URI));
        } else {
            handleBelowLData(data);
        }

        /*if (isAboveL)
            handleAboveL(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
        else if (jsChannel)
            convertFileAndCallBack(cameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
        else {
            if (cameraState && mUriValueCallback != null)
                mUriValueCallback.onReceiveValue((Uri) data.getParcelableExtra(KEY_URI));
            else
                handleBelowLData(data);
        }*/


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


    private void handleBelowLData(Intent data) {


        if (data == null) {
            if (mUriValueCallback != null)
                mUriValueCallback.onReceiveValue(Uri.EMPTY);
            return;
        }
        Uri mUri = data.getData();
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
        ClipData mClipData = data.getClipData();
        if (mClipData != null && mClipData.getItemCount() > 0) {
            datas = new Uri[mClipData.getItemCount()];
            for (int i = 0; i < mClipData.getItemCount(); i++) {

                ClipData.Item mItem = mClipData.getItemAt(i);
                datas[i] = mItem.getUri();

            }
        }
        return datas;


    }

    private void convertFileAndCallBack(final Uri[] uris) {

        /*try {
            String[] paths = AgentWebUtils.uriToPath(mActivity, uris);
            for (int i = 0; i < paths.length; i++) {
                File mFile = new File(paths[i]);
                LogUtils.i(TAG, "abs:" + mFile.getAbsolutePath() + " file length:" + mFile.length() + " uri:" + uris[i] + "  parse:" + Uri.fromFile(mFile) + " write:" + mFile.canWrite() + "   read:" + mFile.canRead() + "   isDirectory:" + mFile.isDirectory());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/

        String[] paths = null;
        if (uris == null || uris.length == 0 || (paths = AgentWebUtils.uriToPath(mActivity, uris)) == null || paths.length == 0) {
            mJsChannelCallback.call(null);
            return;
        }

        int sum = 0;
        for (String path : paths) {
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            File mFile = new File(path);
            if (!mFile.exists()) {
                continue;
            }
            sum += mFile.length();
        }

        if (sum > AgentWebConfig.MAX_FILE_LENGTH) {
            if (mAgentWebUIController.get() != null) {
                mAgentWebUIController.get().showMessage(String.format(mFileUploadMsgConfig.getMaxFileLengthLimit(), (AgentWebConfig.MAX_FILE_LENGTH / 1024 / 1024) + ""), TAG.concat("|convertFileAndCallBack"));
            }
            mJsChannelCallback.call(null);
            return;
        }

        new CovertFileThread(this.mJsChannelCallback, paths).start();

    }

    /**
     * 经过多次的测试，在小米 MIUI ， 华为 ，多部分为 Android 6.0 左右系统相机获取到的文件
     * length为0 ，导致前端 ，获取到的文件， 作预览的时候不正常 ，等待5S左右文件又正常了 ， 所以这里做了阻塞等待处理，
     *
     * @param datas
     * @param isCamera
     */
    private void handleAboveL(final Uri[] datas, boolean isCamera) {
        if (mUriValueCallbacks == null) {
            return;
        }
        if (!isCamera) {
            mUriValueCallbacks.onReceiveValue(datas == null ? new Uri[]{} : datas);
            return;
        }

        if (mAgentWebUIController.get() == null) {
            mUriValueCallbacks.onReceiveValue(null);
            return;
        }
        String[] paths = AgentWebUtils.uriToPath(mActivity, datas);
        if (paths == null || paths.length == 0) {
            mUriValueCallbacks.onReceiveValue(null);
            return;
        }
        final String path = paths[0];
        mAgentWebUIController.get().onLoading(mFileUploadMsgConfig.getLoading());
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new WaitPhotoRunnable(path, new AboveLCallback(mUriValueCallbacks, datas, mAgentWebUIController)));

    }

    private static final class AboveLCallback implements Handler.Callback {
        private ValueCallback<Uri[]> mValueCallback;
        private Uri[] mUris;
        private WeakReference<AgentWebUIController> controller;

        private AboveLCallback(ValueCallback<Uri[]> valueCallbacks, Uri[] uris, WeakReference<AgentWebUIController> controller) {
            this.mValueCallback = valueCallbacks;
            this.mUris = uris;
            this.controller = controller;
        }

        @Override
        public boolean handleMessage(final Message msg) {

            AgentWebUtils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    AboveLCallback.this.safeHandleMessage(msg);
                }
            });
            return false;
        }

        private void safeHandleMessage(Message msg) {
            if (mValueCallback != null) {
                mValueCallback.onReceiveValue(mUris);
            }
            if (controller != null && controller.get() != null) {
                controller.get().cancelLoading();
            }
        }
    }

    private static final class WaitPhotoRunnable implements Runnable {
        private String path;
        private Handler.Callback mCallback;

        private WaitPhotoRunnable(String path, Handler.Callback callback) {
            this.path = path;
            this.mCallback = callback;
        }

        @Override
        public void run() {


            if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                if (mCallback != null) {
                    mCallback.handleMessage(Message.obtain(null, -1));
                }
                return;
            }
            int ms = 0;

            while (ms <= MAX_WAIT_PHOTO_MS) {

                ms += 300;
                SystemClock.sleep(300);
                File mFile = new File(path);
                if (mFile.length() > 0) {

                    if (mCallback != null) {
                        mCallback.handleMessage(Message.obtain(null, 1));
                        mCallback = null;
                    }
                    break;
                }

            }

            if (ms > MAX_WAIT_PHOTO_MS) {
                LogUtils.i(TAG, "WaitPhotoRunnable finish!");
                if (mCallback != null) {
                    mCallback.handleMessage(Message.obtain(null, -1));
                }
            }
            mCallback = null;
            path = null;

        }
    }


    static class CovertFileThread extends Thread {

        private WeakReference<JsChannelCallback> mJsChannelCallback;
        private String[] paths;

        private CovertFileThread(JsChannelCallback jsChannelCallback, String[] paths) {
            this.mJsChannelCallback = new WeakReference<JsChannelCallback>(jsChannelCallback);
            this.paths = paths;
        }

        @Override
        public void run() {


            try {
                Queue<FileParcel> mQueue = AgentWebUtils.convertFile(paths);
                String result = AgentWebUtils.convertFileParcelObjectsToJson(mQueue);
                LogUtils.i(TAG, "result:" + result);
                if (mJsChannelCallback != null && mJsChannelCallback.get() != null)
                    mJsChannelCallback.get().call(result);

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
        String acceptType = "*/*";

        public Builder setAcceptType(String acceptType) {
            this.acceptType = acceptType;
            return this;
        }

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
