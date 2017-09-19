package com.just.library;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.DownloadListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class DefaultDownLoaderImpl implements DownloadListener, DownLoadResultListener {

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;
    private volatile static int NoticationID = 1;
    private List<DownLoadResultListener> mDownLoadResultListeners;
    private WeakReference<Activity> mActivityWeakReference = null;
    private DefaultMsgConfig.DownLoadMsgConfig mDownLoadMsgConfig = null;
    private static final String TAG = DefaultDownLoaderImpl.class.getSimpleName();
    private PermissionInterceptor mPermissionListener = null;
    private String url;
    private String contentDisposition;
    private long contentLength;
    private AtomicBoolean isParallelDownload = new AtomicBoolean(false);
    private int icon = -1;


    DefaultDownLoaderImpl(Builder builder) {
        mActivityWeakReference = new WeakReference<Activity>(builder.mActivity);
        this.mContext = builder.mActivity.getApplicationContext();
        this.isForce = builder.isForce;
        this.enableIndicator = builder.enableIndicator;
        this.mDownLoadResultListeners = builder.mDownLoadResultListeners;
        this.mDownLoadMsgConfig = builder.mDownLoadMsgConfig;
        this.mPermissionListener = builder.mPermissionInterceptor;
        isParallelDownload.set(builder.isParallelDownload);
        icon = builder.icon;

    }


    public boolean isParallelDownload() {
        return isParallelDownload.get();
    }


    public void setParallelDownload(boolean isOpen) {
        isParallelDownload.set(isOpen);
        /*if(isParallelDownload.get()){
            ExecutorProvider.getInstance().open();
        }else{
            ExecutorProvider.getInstance().close();
        }*/

    }


    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        onDownloadStartInternal(url, contentDisposition, mimetype, contentLength);

    }

    private void onDownloadStartInternal(String url, String contentDisposition, String mimetype, long contentLength) {

        if (mActivityWeakReference.get() == null || mActivityWeakReference.get().isFinishing()) {
            return;
        }
        LogUtils.i(TAG, "mime:" + mimetype);
        if (this.mPermissionListener != null) {
            if (this.mPermissionListener.intercept(url, AgentWebPermissions.STORAGE, "download")) {
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> mList = null;
            if ((mList = checkNeedPermission()).isEmpty()) {
                preDownload(url, contentDisposition, contentLength);
            } else {
                ActionActivity.Action mAction = new ActionActivity.Action();
                mAction.setPermissions(AgentWebPermissions.STORAGE);
                mAction.setAction(ActionActivity.Action.ACTION_PERMISSION);
                ActionActivity.setPermissionListener(getPermissionListener());
                this.url = url;
                this.contentDisposition = contentDisposition;
                this.contentLength = contentLength;
                ActionActivity.start(mActivityWeakReference.get(), mAction);

            }

        } else {

            preDownload(url, contentDisposition, contentLength);
        }
    }

    private ActionActivity.PermissionListener getPermissionListener() {
        return new ActionActivity.PermissionListener() {
            @Override
            public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
                if (checkNeedPermission().isEmpty()) {
                    preDownload(DefaultDownLoaderImpl.this.url, DefaultDownLoaderImpl.this.contentDisposition, DefaultDownLoaderImpl.this.contentLength);
                    url = null;
                    contentDisposition = null;
                    contentLength = -1;
                } else {
                    LogUtils.e(TAG, "储存权限获取失败~");
                }

            }
        };
    }

    private List<String> checkNeedPermission() {

        List<String> deniedPermissions = new ArrayList<>();

        for (int i = 0; i < AgentWebPermissions.STORAGE.length; i++) {

            if (ContextCompat.checkSelfPermission(mActivityWeakReference.get(), AgentWebPermissions.STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(AgentWebPermissions.STORAGE[i]);
            }
        }
        return deniedPermissions;
    }

    private void preDownload(String url, String contentDisposition, long contentLength) {
        File mFile = getFile(contentDisposition, url);
        if (mFile == null)
            return;
        if (mFile.exists() && mFile.length() >= contentLength) {

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


        if (ExecuteTasksMap.getInstance().contains(url)) {
            AgentWebUtils.toastShowShort(mContext, mDownLoadMsgConfig.getTaskHasBeenExist());
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

        AlertDialog mAlertDialog = null;
        mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setTitle(mDownLoadMsgConfig.getTips())//
                .setMessage(mDownLoadMsgConfig.getHoneycomblow())//
                .setNegativeButton(mDownLoadMsgConfig.getDownLoad(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                        forceDown(url, contentLength, file);
                    }
                })//
                .setPositiveButton(mDownLoadMsgConfig.getCancel(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null)
                            dialog.dismiss();
                    }
                }).create();

        mAlertDialog.show();

    }

    private void performDownload(String url, long contentLength, File file) {

        ExecuteTasksMap.getInstance().addTask(url, file.getAbsolutePath());
        //并行下载.
        if (isParallelDownload.get()) {
            new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownLoadMsgConfig, icon == -1 ? R.mipmap.download : icon)).executeOnExecutor(ExecutorProvider.getInstance().provide(), (Void[]) null);
        } else {
            //默认串行下载.
            new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownLoadMsgConfig, icon == -1 ? R.mipmap.download : icon)).execute();
        }


    }


    private File getFile(String contentDisposition, String url) {

        try {
            String filename = "";
            if (!TextUtils.isEmpty(contentDisposition) && contentDisposition.contains("filename") && !contentDisposition.endsWith("filename")) {

                int position = contentDisposition.indexOf("filename");
                filename = contentDisposition.substring(position);
                if (filename.contains("=")) {
                    filename = filename.replace("filename=", "");
                } else {
                    filename = filename.replace("filename", "");
                }
            }
            if (TextUtils.isEmpty(filename) && !TextUtils.isEmpty(url) && !url.endsWith("/")) {

                int p = url.lastIndexOf("/");
                if (p != -1)
                    filename = url.substring(p + 1);
                if (filename.contains("?")) {
                    int index = filename.indexOf("?");
                    filename = filename.substring(0, index);

                }
            }

            if (TextUtils.isEmpty(filename)) {
                filename = AgentWebUtils.md5(url);
            }
            LogUtils.i(TAG, "name:" + filename);
            if (filename.length() > 64) {
                filename = filename.substring(filename.length() - 64, filename.length());
            }
            LogUtils.i(TAG, "filename:" + filename);
            return AgentWebUtils.createFileByName(mContext, filename, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void success(String path) {


        ExecuteTasksMap.getInstance().removeTask(path);

        if (AgentWebUtils.isEmptyCollection(mDownLoadResultListeners)) {
            return;
        }
        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            mDownLoadResultListener.success(path);
        }
    }


    @Override
    public void error(String path, String resUrl, String cause, Throwable e) {

        ExecuteTasksMap.getInstance().removeTask(path);

        if (AgentWebUtils.isEmptyCollection(mDownLoadResultListeners)) {
            AgentWebUtils.toastShowShort(mContext, mDownLoadMsgConfig.getDownLoadFail());
            return;
        }

        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            mDownLoadResultListener.error(path, resUrl, cause, e);
        }
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


    public static class Builder {
        private Activity mActivity;
        private boolean isForce;
        private boolean enableIndicator;
        private List<DownLoadResultListener> mDownLoadResultListeners;
        private DefaultMsgConfig.DownLoadMsgConfig mDownLoadMsgConfig;
        private PermissionInterceptor mPermissionInterceptor;
        private int icon = -1;
        private boolean isParallelDownload = false;

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setForce(boolean force) {
            isForce = force;
            return this;
        }

        public Builder setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
            return this;
        }

        public Builder setDownLoadResultListeners(List<DownLoadResultListener> downLoadResultListeners) {
            this.mDownLoadResultListeners = downLoadResultListeners;
            return this;
        }

        public Builder setDownLoadMsgConfig(DefaultMsgConfig.DownLoadMsgConfig downLoadMsgConfig) {
            mDownLoadMsgConfig = downLoadMsgConfig;
            return this;
        }

        public Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
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

        public DefaultDownLoaderImpl create() {
            return new DefaultDownLoaderImpl(this);
        }
    }
}
