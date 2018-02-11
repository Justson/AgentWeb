package com.just.agentweb.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.just.agentweb.Action;
import com.just.agentweb.ActionActivity;
import com.just.agentweb.AgentWebPermissions;
import com.just.agentweb.AgentWebUIController;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.LogUtils;
import com.just.agentweb.PermissionInterceptor;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DefaultDownloadImpl extends DownloadListenerAdapter implements android.webkit.DownloadListener {

    private Context mContext;
    private volatile static AtomicInteger NOTICATION_ID = new AtomicInteger(1);
    private DownloadListener mDownloadListener;
    private WeakReference<Activity> mActivityWeakReference = null;
    private static final String TAG = DefaultDownloadImpl.class.getSimpleName();
    private PermissionInterceptor mPermissionListener = null;
    private String url;
    private String contentDisposition;
    private long contentLength;
    private String mimetype;
    private WeakReference<AgentWebUIController> mAgentWebUIController;
    private ExtraServiceImpl mExtraServiceImpl;
    private String userAgent;
    private ExtraServiceImpl mCloneExtraServiceImpl = null;
    private DownloadingListener mDownloadingListener;

    DefaultDownloadImpl(ExtraServiceImpl extraServiceImpl) {
        if (!extraServiceImpl.isCloneObject) {
            this.bind(extraServiceImpl);
            this.mExtraServiceImpl = extraServiceImpl;
        } else {
            this.mCloneExtraServiceImpl = extraServiceImpl;
        }
    }

    private void bind(ExtraServiceImpl extraServiceImpl) {
        this.mActivityWeakReference = new WeakReference<Activity>(extraServiceImpl.mActivity);
        this.mContext = extraServiceImpl.mActivity.getApplicationContext();
        this.mDownloadListener = extraServiceImpl.mDownloadListener;
        this.mDownloadingListener = extraServiceImpl.downloadingListener;
        this.mPermissionListener = extraServiceImpl.mPermissionInterceptor;
        this.mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(extraServiceImpl.mWebView));
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength, null);

    }


    private synchronized void onDownloadStartInternal(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, ExtraServiceImpl extraServiceImpl) {

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
        ExtraServiceImpl mCloneExtraServiceImpl = null;
        if (extraServiceImpl == null) {
            try {
                mCloneExtraServiceImpl = (ExtraServiceImpl) this.mExtraServiceImpl.clone();
            } catch (CloneNotSupportedException ignore) {
                if (LogUtils.isDebug()) {
                    ignore.printStackTrace();
                }
                LogUtils.i(TAG, " clone object failure !!! ");
                return;
            }
        } else {
            mCloneExtraServiceImpl = extraServiceImpl;
        }
        mCloneExtraServiceImpl
                .setUrl(url)
                .setMimetype(this.mimetype)
                .setContentDisposition(this.contentDisposition)
                .setContentLength(this.contentLength)
                .setUserAgent(this.userAgent);
        this.mCloneExtraServiceImpl = mCloneExtraServiceImpl;

        LogUtils.i(TAG, " clone a extraServiceImpl : " + this.mCloneExtraServiceImpl.mWebView + "  aty:" + this.mCloneExtraServiceImpl.mActivity + "  :" + this.mCloneExtraServiceImpl.getMimetype());

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
        if (mDownloadListener != null
                && mDownloadListener
                .start(this.url,
                        this.userAgent,
                        this.contentDisposition,
                        this.mimetype,
                        contentLength,
                        this.mCloneExtraServiceImpl)) {
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
                        mActivityWeakReference.get().getString(R.string.agentweb_download_task_has_been_exist), TAG.concat("|preDownload"));
            }
            return;
        }


        if (!this.mCloneExtraServiceImpl.isForceDownload() && AgentWebUtils.checkNetworkType(mContext) > 1) { //移动数据

            showDialog(mFile);
            return;
        }
        performDownload(mFile);
    }

    private void forceDownload(final File file) {

        this.mCloneExtraServiceImpl.setForceDownload(true);
        performDownload(file);


    }

    private void showDialog(final File file) {

        Activity mActivity;
        if ((mActivity = mActivityWeakReference.get()) == null || mActivity.isFinishing())
            return;

        AgentWebUIController mAgentWebUIController;
        if ((mAgentWebUIController = this.mAgentWebUIController.get()) != null) {
            mAgentWebUIController.onForceDownloadAlert(url, createCallback(file));
        }

    }

    private Handler.Callback createCallback(final File file) {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                forceDownload(file);
                return true;
            }
        };
    }

    private void performDownload(File file) {

        try {

            ExecuteTasksMap.getInstance().addTask(url, file.getAbsolutePath());
            if (mAgentWebUIController.get() != null) {
                mAgentWebUIController.get()
                        .showMessage(mActivityWeakReference.get().getString(R.string.agentweb_coming_soon_download) + ":" + file.getName(), TAG.concat("|performDownload"));
            }

            DownloadTask mDownloadTask = new DownloadTask(NOTICATION_ID.incrementAndGet(),
                    this,
                    mContext, file,
                    this.mCloneExtraServiceImpl);
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
            return AgentWebUtils.createFileByName(mContext, fileName, !this.mCloneExtraServiceImpl.isOpenBreakPointDownload());
        } catch (Throwable e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
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
    public void progress(String url, long downloaded, long length, long useTime) {
        if (mDownloadingListener != null) {
            synchronized (mDownloadingListener) {
                if (mDownloadListener != null) {
                    mDownloadingListener.progress(url, downloaded, length, useTime);
                }
            }
        }
    }

    @Override
    public void onBindService(String url, DownloadingService downloadingService) {
        if (mDownloadingListener != null) {
            synchronized (mDownloadingListener) {
                mDownloadingListener.onBindService(url, downloadingService);
            }
        }

    }

    @Override
    public void onUnbindService(String url, DownloadingService downloadingService) {
        if (mDownloadingListener != null) {
            synchronized (mDownloadingListener) {
                mDownloadingListener.onUnbindService(url, downloadingService);
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

    public static ExtraServiceImpl newExtra(Activity activity) {
        return new ExtraServiceImpl().setActivity(activity);
    }

    public static DefaultDownloadImpl create(Activity activity,
                                             WebView webView,
                                             DownloadListener downloadListener,
                                             DownloadingListener downloadingListener,
                                             PermissionInterceptor permissionInterceptor) {
        return new ExtraServiceImpl()
                .setActivity(activity)
                .setWebView(webView)
                .setDownloadListener(downloadListener)//
                .setPermissionInterceptor(permissionInterceptor)
                .setDownloadingListener(downloadingListener)
                .create();
    }

    public static class ExtraServiceImpl extends AgentWebDownloader.ExtraService implements Cloneable, Serializable {
        private transient Activity mActivity;
        private boolean isForceDownload = false;
        private boolean enableIndicator = true;
        private transient DownloadListener mDownloadListener;
        private transient PermissionInterceptor mPermissionInterceptor;
        private boolean isParallelDownload = true;
        private transient WebView mWebView;
        protected int icon = R.drawable.ic_file_download_black_24dp;
        private DefaultDownloadImpl mDefaultDownload;
        protected String url;
        protected String userAgent;
        protected String contentDisposition;
        protected String mimetype;
        protected long contentLength;
        private boolean isCloneObject = false;
        private DownloadingListener downloadingListener;

        public ExtraServiceImpl setDownloadingListener(DownloadingListener downloadingListener) {
            this.downloadingListener = downloadingListener;
            return this;
        }

        public boolean isForceDownload() {
            return isForceDownload;
        }

        //        public static final int PENDDING = 1001;
//        public static final int DOWNLOADING = 1002;
//        public static final int FINISH = 1003;
//        public static final int ERROR = 1004;
//        private AtomicInteger state = new AtomicInteger(PENDDING);

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        protected ExtraServiceImpl setUrl(String url) {
            this.url = url;
            return this;
        }

        @Override
        public String getUserAgent() {
            return userAgent;
        }

        @Override
        protected ExtraServiceImpl setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }


        @Override
        public String getContentDisposition() {
            return contentDisposition;
        }

        @Override
        protected ExtraServiceImpl setContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        @Override
        @DrawableRes
        public int getIcon() {
            return icon;
        }

        @Override
        public String getMimetype() {
            return mimetype;
        }

        @Override
        protected ExtraServiceImpl setMimetype(String mimetype) {
            this.mimetype = mimetype;
            return this;
        }

        @Override
        public long getContentLength() {
            return contentLength;
        }

        @Override
        protected ExtraServiceImpl setContentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }


        ExtraServiceImpl setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }


        @Override
        public ExtraServiceImpl setForceDownload(boolean force) {
            isForceDownload = force;
            return this;
        }

        @Override
        public ExtraServiceImpl setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
            return this;
        }

        ExtraServiceImpl setDownloadListener(DownloadListener downloadListeners) {
            this.mDownloadListener = downloadListeners;
            return this;
        }

        ExtraServiceImpl setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        @Override
        public ExtraServiceImpl setIcon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public ExtraServiceImpl setParallelDownload(boolean parallelDownload) {
            isParallelDownload = parallelDownload;
            return this;
        }

        @Override
        public ExtraServiceImpl setOpenBreakPointDownload(boolean openBreakPointDownload) {
            isOpenBreakPointDownload = openBreakPointDownload;
            return this;
        }

        ExtraServiceImpl setWebView(WebView webView) {
            this.mWebView = webView;
            return this;
        }


        @Override
        protected Object clone() throws CloneNotSupportedException {
            ExtraServiceImpl mExtraServiceImpl = (ExtraServiceImpl) super.clone();
            mExtraServiceImpl.isCloneObject = true;
            mExtraServiceImpl.mActivity = null;
            mExtraServiceImpl.mDownloadListener = null;
            mExtraServiceImpl.mPermissionInterceptor = null;
            mExtraServiceImpl.mWebView = null;
            return mExtraServiceImpl;
        }

        DefaultDownloadImpl create() {
            return this.mDefaultDownload = new DefaultDownloadImpl(this);
        }

        @Override
        public synchronized void performReDownload() {

            LogUtils.i(TAG, "performReDownload:" + mDefaultDownload);
            if (this.mDefaultDownload != null) {
                this.mDefaultDownload
                        .onDownloadStartInternal(
                                getUrl(),
                                getUserAgent(),
                                getContentDisposition(),
                                getMimetype(),
                                getContentLength(), this);
            }
        }

    }


}
