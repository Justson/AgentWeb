package com.just.agentweb.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.just.agentweb.Action;
import com.just.agentweb.ActionActivity;
import com.just.agentweb.AgentWebDownloader;
import com.just.agentweb.AgentWebPermissions;
import com.just.agentweb.AgentWebUIController;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.DefaultMsgConfig;
import com.just.agentweb.DownloadListener;
import com.just.agentweb.DownloadingService;
import com.just.agentweb.LogUtils;
import com.just.agentweb.PermissionInterceptor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DefaultDownloadImpl extends DownloadListener.DownloadListenerAdapter implements android.webkit.DownloadListener {

    private Context mContext;
    private volatile static int NoticationID = 1;
    private DownloadListener mDownloadListener;
    private WeakReference<Activity> mActivityWeakReference = null;
    private static final String TAG = DefaultDownloadImpl.class.getSimpleName();
    private PermissionInterceptor mPermissionListener = null;
    private String url;
    private String contentDisposition;
    private long contentLength;
    private String mimetype;
    private WeakReference<AgentWebUIController> mAgentWebUIController;
    private Builder mBuilder;
    private String userAgent;
    private Builder mCloneBuilder = null;

    DefaultDownloadImpl(Builder builder) {
        if (!builder.isCloneObject) {
            this.bind(builder);
            this.mBuilder = builder;
        } else {
            this.mCloneBuilder = mCloneBuilder;
        }
    }

    private void bind(Builder builder) {
        this.mActivityWeakReference = new WeakReference<Activity>(builder.mActivity);
        this.mContext = builder.mActivity.getApplicationContext();
        this.mDownloadListener = builder.mDownloadListener;
        this.mPermissionListener = builder.mPermissionInterceptor;
        this.mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(builder.mWebView));
    }


    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength);

    }


    private void onDownloadStartInternal(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        if (mActivityWeakReference.get() == null || mActivityWeakReference.get().isFinishing()) {
            return;
        }
        if (this.mPermissionListener != null) {
            if (this.mPermissionListener.intercept(url, AgentWebPermissions.STORAGE, "download")) {
                return;
            }
        }

        LogUtils.i(TAG, "mimetype:" + mimetype);
        this.url = url;
        this.contentDisposition = contentDisposition;
        this.contentLength = contentLength;
        this.mimetype = mimetype;
        this.userAgent = userAgent;
        Builder mCloneBuilder = null;
        try {
            mCloneBuilder = (Builder) this.mBuilder.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        mCloneBuilder
                .setUrl(url)
                .setMimetype(this.mimetype)
                .setContentDisposition(this.contentDisposition)
                .setContentLength(this.contentLength)
                .setUserAgent(this.userAgent);
        this.mCloneBuilder = mCloneBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> mList = null;
            if ((mList = checkNeedPermission()).isEmpty()) {
                preDownload();
            } else {
                Action mAction = Action.createPermissionsAction(mList.toArray(new String[]{}));
                ActionActivity.setPermissionListener(getPermissionListener());
                ActionActivity.start(mActivityWeakReference.get(), mAction);
            }

        } else {
            preDownload();
        }
    }

    private ActionActivity.PermissionListener getPermissionListener() {
        return new ActionActivity.PermissionListener() {
            @Override
            public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
                if (checkNeedPermission().isEmpty()) {
                    preDownload();
                } else {
                    LogUtils.e(TAG, "储存权限获取失败~");
                }

            }
        };
    }

    private List<String> checkNeedPermission() {

        List<String> deniedPermissions = new ArrayList<>();

        if (!AgentWebUtils.hasPermission(mActivityWeakReference.get(), AgentWebPermissions.STORAGE)) {
            deniedPermissions.addAll(Arrays.asList(AgentWebPermissions.STORAGE));
        }
        return deniedPermissions;
    }

    private void preDownload() {

        //true 表示用户取消了该下载事件。
        if (mDownloadListener != null && mDownloadListener.start(this.url, this.userAgent, this.contentDisposition, this.mimetype, contentLength, this.mCloneBuilder)) {
            return;
        }
        File mFile = getFile(contentDisposition, url);
        //File 创建文件失败
        if (mFile == null) {
            LogUtils.i(TAG, "新建文件失败");
            return;
        }
        if (mFile.exists() && mFile.length() >= contentLength) {

            //true 表示用户处理了下载完成后续的通知用户事件
            if (mDownloadListener != null && mDownloadListener.result(mFile.getAbsolutePath(), url, null)) {
                return;
            }

            Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mContext, mFile);
            try {
//                mContext.getPackageManager().resolveActivity(mIntent)
                if (mIntent != null) {
                    if (!(mContext instanceof Activity))
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(mIntent);
                }
                return;
            } catch (Throwable throwable) {
                if (LogUtils.isDebug())
                    throwable.printStackTrace();
            }

        }


        //该链接是否正在下载
        if (ExecuteTasksMap.getInstance().contains(url) || ExecuteTasksMap.getInstance().contains(mFile.getAbsolutePath())) {
            if (mAgentWebUIController.get() != null) {
                mAgentWebUIController.get().showMessage(
                        this.mCloneBuilder.mDownloadMsgConfig.getTaskHasBeenExist(), TAG.concat("|preDownload"));
            }
            return;
        }


        if (AgentWebUtils.checkNetworkType(mContext) > 1) { //移动数据

            showDialog(url, contentLength, mFile);
            return;
        }
        performDownload( mFile);
    }

    private void forceDown(final String url, final long contentLength, final File file) {

        this.mCloneBuilder.isForceDownload = true;
        performDownload(url, contentLength, file);


    }

    private void showDialog(final String url, final long contentLength, final File file) {

        Activity mActivity;
        if ((mActivity = mActivityWeakReference.get()) == null || mActivity.isFinishing())
            return;

        AgentWebUIController mAgentWebUIController;
        if ((mAgentWebUIController = this.mAgentWebUIController.get()) != null) {
            mAgentWebUIController.onForceDownloadAlert(url, this.mBuilder.mDownloadMsgConfig, createCallback(url, contentLength, file));
        }

    }

    private Handler.Callback createCallback(final String url, final long contentLength, final File file) {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                forceDown(url, contentLength, file);
                return true;
            }
        };
    }

    private void performDownload(File file) {

        try {

            ExecuteTasksMap.getInstance().addTask(url, file.getAbsolutePath());
            if (mAgentWebUIController.get() != null) {
                mAgentWebUIController.get()
                        .showMessage(this.mCloneBuilder.mDownloadMsgConfig.getPreLoading() + ":" + file.getName(), TAG.concat("|performDownload"));
            }

            DownloadTask mDownloadTask = new DownloadTask(NoticationID++,
                    this,
                    mContext, file,
                    this.mCloneBuilder);
            new Downloader().download(mDownloadTask);

            this.url = null;
            this.contentDisposition = null;
            this.contentLength = -1;
            this.mimetype = null;
            this.userAgent = null;

        } catch (Throwable ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }

    }


    private File getFile(String contentDisposition, String url) {

        try {
            String fileName = getFileName(contentDisposition);
            if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(url)) {
                Uri mUri = Uri.parse(url);
                fileName = mUri.getPath().substring(mUri.getPath().lastIndexOf('/') + 1);
            }
            if (!TextUtils.isEmpty(fileName) && fileName.length() > 64) {
                fileName = fileName.substring(fileName.length() - 64, fileName.length());
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = AgentWebUtils.md5(url);
            }
            if (fileName.contains("\"")) {
                fileName = fileName.replace("\"", "");
            }
            return AgentWebUtils.createFileByName(mContext, fileName, !this.mCloneBuilder.isParallelDownload);
        } catch (Throwable e) {
            if (LogUtils.isDebug())
                e.printStackTrace();
        }

        return null;
    }

    private String getFileName(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) {
            return "";
        }
        Matcher m = Pattern.compile(".*filename=(.*)").matcher(contentDisposition.toLowerCase());
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }


    @Override
    public void progress(String url, long downloaded, long length, long useTime, DownloadingService downloadingService) {
        if (mDownloadListener != null) {
            synchronized (mDownloadListener) {
                mDownloadListener.progress(url, downloaded, length, useTime, downloadingService);
            }
        }
    }

    @Override
    public boolean result(String path, String url, Throwable e) {
        ExecuteTasksMap.getInstance().removeTask(path);
        return mDownloadListener != null && mDownloadListener.result(path, url, e);
    }


    //静态缓存当前正在下载的任务url
    public static class ExecuteTasksMap extends ReentrantLock {

        private LinkedList<String> mTasks = null;

        private ExecuteTasksMap() {
            super(false);
            mTasks = new LinkedList();
        }

        private static ExecuteTasksMap sInstance = null;


        static ExecuteTasksMap getInstance() {


            if (sInstance == null) {
                synchronized (ExecuteTasksMap.class) {
                    if (sInstance == null)
                        sInstance = new ExecuteTasksMap();
                }
            }
            return sInstance;
        }

        void removeTask(String path) {

            int index = mTasks.indexOf(path);
            if (index == -1)
                return;
            try {
                lock();
                int position = -1;
                if ((position = mTasks.indexOf(path)) == -1)
                    return;
                mTasks.remove(position);
                mTasks.remove(position - 1);
            } finally {
                unlock();
            }

        }

        void addTask(String url, String path) {
            try {
                lock();
                mTasks.add(url);
                mTasks.add(path);
            } finally {
                unlock();
            }

        }

        //加锁读
        boolean contains(String url) {

            try {
                lock();
                return mTasks.contains(url);
            } finally {
                unlock();
            }

        }
    }

    public static Builder newBuilder(Activity activity) {
        return new Builder().setActivity(activity);
    }


    public static class Builder extends AgentWebDownloader.ExtraService implements Cloneable {
        private transient Activity mActivity;
        private boolean isForceDownload = false;
        private boolean enableIndicator = true;
        private transient DownloadListener mDownloadListener;
        private transient PermissionInterceptor mPermissionInterceptor;
        private boolean isParallelDownload = true;
        private transient WebView mWebView;
        protected int icon = -1;
        private DefaultDownloadImpl mDefaultDownload;
        protected DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig;

        protected String url;
        protected String userAgent;
        protected String contentDisposition;
        protected String mimetype;
        protected long contentLength;

        private boolean isCloneObject = false;


        public String getUrl() {
            return url;
        }

        @Override
        protected Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getUserAgent() {
            return userAgent;
        }

        @Override
        protected Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public String getContentDisposition() {
            return contentDisposition;
        }

        @Override
        protected Builder setContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        public String getMimetype() {
            return mimetype;
        }

        @Override
        protected Builder setMimetype(String mimetype) {
            this.mimetype = mimetype;
            return this;
        }

        public long getContentLength() {
            return contentLength;
        }

        @Override
        protected Builder setContentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }


        Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public DefaultMsgConfig.DownloadMsgConfig getDownloadMsgConfig() {
            return mDownloadMsgConfig;
        }

        public Builder setForceDownload(boolean force) {
            isForceDownload = force;
            return this;
        }

        public Builder setDownloadMsgConfig(@NonNull DefaultMsgConfig.DownloadMsgConfig downloadMsgConfig) {
            if (downloadMsgConfig != null) {
                mDownloadMsgConfig = downloadMsgConfig;
            }
            return this;
        }

        public Builder setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
            return this;
        }

        Builder setDownloadListener(DownloadListener downloadListeners) {
            this.mDownloadListener = downloadListeners;
            return this;
        }


        Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setParallelDownload(boolean parallelDownload) {
            isParallelDownload = parallelDownload;
            return this;
        }

        public Builder setOpenBreakPointDownload(boolean openBreakPointDownload) {
            isOpenBreakPointDownload = openBreakPointDownload;
            return this;
        }

        Builder setWebView(WebView webView) {
            this.mWebView = webView;
            return this;
        }

        public void build() {
            if (mDefaultDownload != null) {
                mDefaultDownload.bind(this);
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Builder mBuilder = (Builder) super.clone();
            mBuilder.isCloneObject = true;
//            mBuilder.mActivity = null;
//            mBuilder.mDownloadListener = null;
//            mBuilder.mPermissionInterceptor = null;
//            mBuilder.mWebView = null;
            return mBuilder;
        }

        DefaultDownloadImpl create() {
            return this.mDefaultDownload = new DefaultDownloadImpl(this);
        }

        public void toReDownload() {

            if (mDefaultDownload != null) {
                mDefaultDownload.onDownloadStart(getUrl(), getUserAgent(), getContentDisposition(), getMimetype(), getContentLength());
            }
        }

    }


}
