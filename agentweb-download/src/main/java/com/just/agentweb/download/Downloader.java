package com.just.agentweb.download;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.just.agentweb.AgentWebDownloader;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.DownloadListener;
import com.just.agentweb.LogUtils;
import com.just.agentweb.Provider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class Downloader extends AsyncTask<Void, Integer, Integer> implements AgentWebDownloader<DownloadTask>, Observer {

    /**
     * 下载参数
     */
    private volatile DownloadTask mDownloadTask;
    /**
     * 已经下载的大小
     */
    private long loaded = 0l;
    /**
     * 总大小
     */
    private long totals = -1l;
    /**
     *
     */
    private long tmp = 0;
    private long mUsedTime = 0l;
    /**
     * 上一次更新通知的时间
     */
    private long mLastTime = 0l;
    /**
     * 下载开始时间
     */
    private long mBeginTime = 0l;
    /**
     * 当前下载速度
     */
    private long mSpeed = 0;
    /**
     * 下载错误，回调给用户的错误
     */
    private Exception e;
    /**
     * 下载最大时长
     */
    private long downloadTimeOut = 30000000l;
    /**
     * 连接超时
     */
    private long connectTimeOut = 10000l;
    /**
     * 通知
     */
    private AgentWebNotification mAgentWebNotification;

    private static final int ERROR_LOAD = 406;

    private static final String TAG = Downloader.class.getSimpleName();
    /**
     * true 表示用户已经取消下载
     */
    private AtomicBoolean isCancel = new AtomicBoolean(false);
    /**
     * true  表示终止下载
     */
    private AtomicBoolean isShutdown = new AtomicBoolean(false);
    /**
     * Observable 缓存当前Downloader，如果用户滑动通知取消下载，通知所有 Downloader 找到
     * 相应的 Downloader 取消下载。
     */
    private static Observable mObservable = new Observable() {
        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    };


    Downloader() {

    }

    private void checkNullTask(DownloadTask downloadTask) {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(mDownloadTask.getDownloadListener()!=null){
            mDownloadTask.getDownloadListener().onBindService(mDownloadTask.getUrl(),this);
        }

        mObservable.addObserver(this);
        buildNotify(new Intent(), mDownloadTask.getId(),
                mDownloadTask.getContext().getString(R.string.agentweb_coming_soon_download));
    }

    private boolean checkDownloadCondition() {

        if (mDownloadTask.getLength() - mDownloadTask.getFile().length() > AgentWebUtils.getAvailableStorage()) {
            LogUtils.i(TAG, " 空间不足");
            return false;
        }
        return true;
    }

    private boolean checkNet() {
        if (!mDownloadTask.isForce()) {
            return AgentWebUtils.checkWifi(mDownloadTask.getContext());
        } else {
            return AgentWebUtils.checkNetwork(mDownloadTask.getContext());
        }
    }


    @Override
    protected Integer doInBackground(Void... params) {
        int result = ERROR_LOAD;
        try {
            this.mBeginTime = System.currentTimeMillis();
            if (!checkDownloadCondition())
                return DownloadMsg.STORAGE_ERROR.code;
            if (!checkNet())
                return DownloadMsg.NETWORK_ERROR_CONNECTION.code;
            result = doDownload();

        } catch (Exception e) {

            this.e = e;//发布
            LogUtils.i(TAG, "doInBackground   Exception:" + e.getMessage());
            // e.printStackTrace();

        }

        return result;
    }

    private int doDownload() throws IOException {

        HttpURLConnection mHttpURLConnection = createUrlConnection(mDownloadTask.getUrl());

        if (mDownloadTask.getFile().length() > 0) {
            mHttpURLConnection.addRequestProperty("Range", "bytes=" + (tmp = mDownloadTask.getFile().length()) + "-");
        }
        try {
            mHttpURLConnection.connect();
            boolean isSeek = false;
            int resCode = mHttpURLConnection.getResponseCode();
            if (resCode != 200 && resCode != 206) {
                return DownloadMsg.ERROR_NETWORK_STATUS.code;
            } else {
                isSeek = (resCode == 206);
            }

            LogUtils.i(TAG, "response code:" + mHttpURLConnection.getResponseCode());
            return doDownload(mHttpURLConnection.getInputStream(), new LoadingRandomAccessFile(mDownloadTask.getFile()), isSeek);
        } finally {
            if (mHttpURLConnection != null)
                mHttpURLConnection.disconnect();
        }

    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {


        HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        mHttpURLConnection.setRequestProperty("Accept", "application/*");
        mHttpURLConnection.setConnectTimeout(5000 * 2);
        return mHttpURLConnection;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {

        try {
            long currentTime = System.currentTimeMillis();
            this.mUsedTime = currentTime - this.mBeginTime;

            if (mUsedTime == 0) {
                this.mSpeed = 0;
            } else {
                this.mSpeed = loaded * 1000 / this.mUsedTime;
            }

            if (currentTime - this.mLastTime < 800) {
                return;
            }
            this.mLastTime = currentTime;
            if (mAgentWebNotification != null) {
                if (!mAgentWebNotification.hasDeleteContent()) {
                    mAgentWebNotification.setDelecte(buildCancelContent(mDownloadTask.getContext().getApplicationContext(), mDownloadTask.getId()));
                }

                int mProgress = (int) ((tmp + loaded) / Float.valueOf(totals) * 100);
                mAgentWebNotification.setContentText(
                        mDownloadTask.getContext()
                                .getString(R.string.agentweb_current_downloading_progress, (mProgress + "%"))
                );
                mAgentWebNotification.setProgress(100, mProgress, false);
            }
            if (mDownloadTask.getDownloadListener() != null) {
                mDownloadTask
                        .getDownloadListener()
                        .progress(mDownloadTask.getUrl(), (tmp + loaded), totals, mUsedTime);
            }
        } catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPostExecute(Integer integer) {

        try {
            LogUtils.i(TAG, "onPostExecute:" + integer);
            mObservable.deleteObserver(this);

            if(mDownloadTask.getDownloadListener()!=null){
                mDownloadTask.getDownloadListener().onUnbindService(mDownloadTask.getUrl(),this);
            }
            if (mDownloadTask.getDownloadListener() != null) {
                mDownloadTask
                        .getDownloadListener()
                        .progress(mDownloadTask.getUrl(), (tmp + loaded), totals, mUsedTime);

            }

            boolean t = doCallback(integer);
            if (integer > 200) {

                if (mAgentWebNotification != null)
                    mAgentWebNotification.cancel(mDownloadTask.getId());
                return;
            }
            if (mDownloadTask.isEnableIndicator()) {
                if (mAgentWebNotification != null)
                    mAgentWebNotification.cancel(mDownloadTask.getId());

                if (t) {
                    return;
                }
                Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mDownloadTask.getContext(), mDownloadTask.getFile());
                try {
                    if (mIntent != null) {
                        if (!(mDownloadTask.getContext() instanceof Activity))
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent rightPendIntent = PendingIntent.getActivity(mDownloadTask.getContext(),
                                mDownloadTask.getId() << 4, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAgentWebNotification.setProgressFinish(mDownloadTask.getContext().getString(R.string.agentweb_click_open), rightPendIntent);
                    }
                    return;
                } catch (Throwable throwable) {
                    if (LogUtils.isDebug())
                        throwable.printStackTrace();
                }
            }
        } catch (Exception e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
        } finally {

            if (!isShutdown.get()) {
                return;
            }
            if (mDownloadTask != null) {
                mDownloadTask.destroy();
            }

        }


    }

    private boolean doCallback(Integer code) {
        DownloadListener mDownloadListener = null;
        if ((mDownloadListener = mDownloadTask.getDownloadListener()) == null) {
            LogUtils.e(TAG, "DownloadListener has been death");
            DefaultDownloadImpl.ExecuteTasksMap.getInstance().removeTask(mDownloadTask.getFile().getPath());
            return false;
        }
        return mDownloadListener.result(mDownloadTask.getFile().getAbsolutePath(),
                mDownloadTask.getUrl(), code <= 200 ? null
                        : this.e == null
                        ? new RuntimeException("download fail ， cause:" + DownloadMsg.getMsgByCode(code)) : this.e);

    }


    private void buildNotify(Intent intent, int id, String progressHint) {

        Context mContext = mDownloadTask.getContext().getApplicationContext();
        if (mContext != null && mDownloadTask.isEnableIndicator()) {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mContext,
                    0x33 * id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = mDownloadTask.getDrawableRes();
            String ticker = mContext.getString(R.string.agentweb_trickter);
            mAgentWebNotification = new AgentWebNotification(mContext, id);

            String title = TextUtils.isEmpty(mDownloadTask.getFile().getName()) ? mContext.getString(R.string.agentweb_file_download) : mDownloadTask.getFile().getName();

            if (title.length() > 20) {
                title = "..." + title.substring(title.length() - 20, title.length());
            }
            mAgentWebNotification.notify_progress(rightPendIntent, smallIcon, ticker, title, progressHint, false, false, false, buildCancelContent(mContext, id));
            mAgentWebNotification.sent();
        }
    }


    private PendingIntent buildCancelContent(Context context, int id) {

        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction("com.agentweb.cancelled");
        intentCancel.putExtra("type", "type");
        intentCancel.putExtra("TAG", mDownloadTask.getUrl());
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, id << 3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        LogUtils.i(TAG, "id<<3:" + (id << 3));
        return pendingIntentCancel;
    }


    private int doDownload(InputStream in, RandomAccessFile out, boolean isSeek) throws IOException {

        this.mBeginTime = System.currentTimeMillis();
        byte[] buffer = new byte[1024 * 10];
        BufferedInputStream bis = new BufferedInputStream(in, 1024 * 10);
        try {

            if (isSeek) {
                LogUtils.i(TAG, "seek -- >" + isSeek + "  length:" + out.length());
                out.seek(out.length());
            } else {
                LogUtils.i(TAG, "seek -- >" + false + "  , length : 0");
                out.seek(0l);
            }
            int bytes = 0;
            long previousBlockTime = -1;

            while (!isCancel.get() && !isShutdown.get()) {
                int n = bis.read(buffer, 0, 1024 * 10);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                bytes += n;

                if (!checkNet()) {
                    LogUtils.i(TAG, "network");
                    return DownloadMsg.NETWORK_ERROR_CONNECTION.code;
                }

                if (mSpeed != 0) {
                    previousBlockTime = -1;
                } else if (previousBlockTime == -1) {
                    previousBlockTime = System.currentTimeMillis();
                } else if ((System.currentTimeMillis() - previousBlockTime) > downloadTimeOut) {
                    LogUtils.i(TAG, "timeout");
                    return DownloadMsg.TIME_OUT.code;
                }
            }
            LogUtils.i(TAG, "isCancel:" + isCancel.get());
            if (isCancel.get()) {
                return DownloadMsg.USER_CANCEL.code;
            }
            if (isShutdown.get()) {
                return DownloadMsg.SHUTDOWN.code;
            }
            return DownloadMsg.SUCCESSFULL.code;
        } finally {
            AgentWebUtils.closeIO(out);
            AgentWebUtils.closeIO(bis);
        }

    }

    private final void toCancel() {
        isCancel.set(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        //LogUtils.i(TAG, "update Object    ... ");
        String url = "";
        if (arg instanceof String && !TextUtils.isEmpty(url = (String) arg) && url.equals(mDownloadTask.getUrl())) {
            toCancel();
        }


    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get() || isCancel.get();
    }

    @Override
    public synchronized AgentWebDownloader.ExtraService shutdownNow() {

        if (getStatus() == Status.FINISHED) {
            return null;
        }
        toCancel();
        isShutdown.set(true);
        return mDownloadTask.getExtraServiceImpl();
    }

    @Override
    public void download(DownloadTask downloadTask) {
        downloadInternal(downloadTask);
    }

    private final void downloadInternal(DownloadTask downloadTask) {
        this.mDownloadTask = downloadTask;
        this.totals = mDownloadTask.getLength();
        checkNullTask(downloadTask);
        downloadTimeOut = mDownloadTask.getDownloadTimeOut();
        connectTimeOut = mDownloadTask.getConnectTimeOut();
        if (downloadTask.isParallelDownload()) {
            this.executeOnExecutor(ExecutorProvider.getInstance().provide(), (Void[]) null);
        } else {
            this.execute();
        }
    }

    private final class LoadingRandomAccessFile extends RandomAccessFile {

        public LoadingRandomAccessFile(File file) throws FileNotFoundException {
            super(file, "rw");
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {

            super.write(buffer, offset, count);
            loaded += count;
            publishProgress(0);

        }
    }


    enum DownloadMsg {
        NETWORK_ERROR_CONNECTION(400),
        ERROR_NETWORK_STATUS(401),
        STORAGE_ERROR(402),
        SHUTDOWN(405),
        TIME_OUT(403),
        USER_CANCEL(404),
        SUCCESSFULL(200);
        int code;

        DownloadMsg(int e) {
            this.code = e;
        }


        public static String getMsgByCode(int code) {
            LogUtils.i(TAG, "  code:" + code);
            switch (code) {


                case 400:
                    return "Network connection error";
                case 401:
                    return "Connection status code result, non-200 or non 206";
                case 402:
                    return "Insufficient memory space";
                case 403:
                    return "Download time is overtime";
                case 404:
                    return "The user canceled the download";
                case 405:
                    return "Shutdown";
                case 200:
                    return "Download successful";
                default:
                    return "Unknown exception";

            }
        }


    }


    public static class NotificationBroadcastReceiver extends BroadcastReceiver {


        public NotificationBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.agentweb.cancelled")) {

                try {
                    String url = intent.getStringExtra("TAG");
                    Class<?> mClazz = mObservable.getClass();
                    Method mMethod = mClazz.getMethod("setChanged", (Class<?>[]) null);
                    mMethod.setAccessible(true);
                    mMethod.invoke(mObservable, (Object[]) null);
                    mObservable.notifyObservers(url);
                    LogUtils.i(TAG, "size:" + mObservable.countObservers());
                } catch (Throwable ignore) {
//                    ignore.printStackTrace();
                }

            }
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


}
