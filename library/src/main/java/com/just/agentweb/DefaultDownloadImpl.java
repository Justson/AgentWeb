package com.just.agentweb;

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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DefaultDownloadImpl extends DownloadListener.DownloadListenerAdapter implements android.webkit.DownloadListener {

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;
    private volatile static int NoticationID = 1;
    private DownloadListener mDownloadListener;
    private WeakReference<Activity> mActivityWeakReference = null;
    private DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig = null;
    private static final String TAG = DefaultDownloadImpl.class.getSimpleName();
    private PermissionInterceptor mPermissionListener = null;
    private String url;
    private String contentDisposition;
    private long contentLength;
    private String mimetype;
    private AtomicBoolean isParallelDownload = new AtomicBoolean(false);
    private int icon = -1;
    private WeakReference<AgentWebUIController> mAgentWebUIController;
    private Builder mBuilder;

    DefaultDownloadImpl(Builder builder) {
        this.bind(builder);
        this.mBuilder = builder;
    }

    private void bind(Builder builder) {
        mActivityWeakReference = new WeakReference<Activity>(builder.mActivity);
        this.mContext = builder.mActivity.getApplicationContext();
        this.isForce = builder.isForce;
        this.enableIndicator = builder.enableIndicator;
        this.mDownloadListener = builder.mDownloadListener;
        this.mDownloadMsgConfig = builder.mDownloadMsgConfig;
        this.mPermissionListener = builder.mPermissionInterceptor;
        isParallelDownload.set(builder.isParallelDownload);
        icon = builder.icon;
        this.mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(builder.mWebView));
    }


    public boolean isParallelDownload() {
        return isParallelDownload.get();
    }


    public void setParallelDownload(boolean isOpen) {
        isParallelDownload.set(isOpen);
    }


    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength);

    }

    private String userAgent;

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
        if (mDownloadListener != null && mDownloadListener.start(this.url, this.userAgent, this.contentDisposition, this.mimetype, contentLength, this.mBuilder)) {
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
                        mDownloadMsgConfig.getTaskHasBeenExist(), TAG.concat("|preDownload"));
            }
            return;
        }


        if (AgentWebUtils.checkNetworkType(mContext) > 1) { //移动数据

            showDialog(url, contentLength, mFile);
            return;
        }
        performDownload(url, contentLength, mFile);
    }

    private void forceDown(final String url, final long contentLength, final File file) {

        isForce = true;
        performDownload(url, contentLength, file);


    }

    private void showDialog(final String url, final long contentLength, final File file) {

        Activity mActivity;
        if ((mActivity = mActivityWeakReference.get()) == null || mActivity.isFinishing())
            return;

        AgentWebUIController mAgentWebUIController;
        if ((mAgentWebUIController = this.mAgentWebUIController.get()) != null) {
            mAgentWebUIController.onForceDownloadAlert(url, mDownloadMsgConfig, createCallback(url, contentLength, file));
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

    private void performDownload(String url, long contentLength, File file) {

        ExecuteTasksMap.getInstance().addTask(url, file.getAbsolutePath());
        if (mAgentWebUIController.get() != null) {
            mAgentWebUIController.get()
                    .showMessage(mDownloadMsgConfig.getPreLoading() + ":" + file.getName(), TAG.concat("|performDownload"));
        }
        //并行下载.
        if (isParallelDownload.get()) {
            new DownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownloadMsgConfig, icon == -1 ? R.drawable.ic_file_download_black_24dp : icon)).executeOnExecutor(ExecutorProvider.getInstance().provide(), (Void[]) null);
        } else {
            //默认串行下载.
            new DownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownloadMsgConfig, icon == -1 ? R.drawable.ic_file_download_black_24dp : icon)).execute();
        }

        this.url = null;
        this.contentDisposition = null;
        this.contentLength = -1;
        this.mimetype = null;
        this.userAgent = null;


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
            return AgentWebUtils.createFileByName(mContext, fileName, false);
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
    public boolean result(String path, String url, Throwable e) {
        ExecuteTasksMap.getInstance().removeTask(path);
        return mDownloadListener != null && mDownloadListener.result(path, url, e);
    }


    static class ExecutorProvider implements Provider<Executor> {


        private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private final int CORE_POOL_SIZE = (int) (Math.max(2, Math.min(CPU_COUNT - 1, 4)) * 1.5);
        private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        private final int KEEP_ALIVE_SECONDS = 15;

        private final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);
            private SecurityManager securityManager = System.getSecurityManager();
            private ThreadGroup group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();

            public Thread newThread(Runnable r) {
                Thread mThread = new Thread(group, r, "pool-agentweb-thread-" + mCount.getAndIncrement());
                if (mThread.isDaemon()) {
                    mThread.setDaemon(false);
                }
                mThread.setPriority(Thread.MIN_PRIORITY);
                LogUtils.i(TAG, "Thread Name:" + mThread.getName());
                LogUtils.i(TAG, "live:" + mThreadPoolExecutor.getActiveCount() + "    getCorePoolSize:" + mThreadPoolExecutor.getCorePoolSize() + "  getPoolSize:" + mThreadPoolExecutor.getPoolSize());
                return mThread;
            }
        };

        private static final BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<Runnable>(128);
        private ThreadPoolExecutor mThreadPoolExecutor;

        private ExecutorProvider() {
            internalInit();
        }

        private void internalInit() {
            if (mThreadPoolExecutor != null && !mThreadPoolExecutor.isShutdown()) {
                mThreadPoolExecutor.shutdownNow();
            }
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            mThreadPoolExecutor.allowCoreThreadTimeOut(true);
        }


        public static ExecutorProvider getInstance() {
            return InnerHolder.M_EXECUTOR_PROVIDER;
        }

        static class InnerHolder {
            private static final ExecutorProvider M_EXECUTOR_PROVIDER = new ExecutorProvider();
        }

        @Override
        public Executor provide() {
            return mThreadPoolExecutor;
        }

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


    public static class Builder extends Extra {
        private Activity mActivity;
        private boolean isForce;
        private boolean enableIndicator;
        private DownloadListener mDownloadListener;
        private DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig;
        private PermissionInterceptor mPermissionInterceptor;
        private int icon = -1;
        private boolean isParallelDownload = false;
        private WebView mWebView;
        private DefaultDownloadImpl mDefaultDownload;

        Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setForceDownload(boolean force) {
            isForce = force;
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

        public Builder setDownloadMsgConfig(DefaultMsgConfig.DownloadMsgConfig downloadMsgConfig) {
            mDownloadMsgConfig = downloadMsgConfig;
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

        Builder setWebView(WebView webView) {
            this.mWebView = webView;
            return this;
        }

        public void build() {
            if (mDefaultDownload != null) {
                mDefaultDownload.bind(this);
            }
        }

        DefaultDownloadImpl create() {
            return this.mDefaultDownload = new DefaultDownloadImpl(this);
        }
    }


}
