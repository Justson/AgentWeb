package com.just.agentweb.download;

import android.content.Context;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ringle-android
 * @date 19-2-12
 * @since 1.0.0
 */
public class Rumtime {

    private static final Rumtime sInstance = new Rumtime();
    private final DownloadTask sDefaultDownloadTask = new DownloadTask();
    private AtomicInteger mIDGenerator;
    private AtomicInteger mThreadGlobalCounter;
    private File mDownloadDir = null;

    private Rumtime() {
        sDefaultDownloadTask.setBreakPointDownload(true)
                .setIcon(R.drawable.ic_file_download_black_24dp)
                .setConnectTimeOut(6000)
                .setBlockMaxTime(10 * 60 * 1000)
                .setDownloadTimeOut(Long.MAX_VALUE)
                .setParallelDownload(true)
                .setEnableIndicator(true)
                .setAutoOpen(false)
                .setForceDownload(true);
        mIDGenerator = new AtomicInteger(1);
        mThreadGlobalCounter = new AtomicInteger(1);
    }

    public static Rumtime getInstance() {
        return sInstance;
    }

    public DownloadTask getDefaultDownloadTask() {
        return sDefaultDownloadTask.clone();
    }

    public int generateGlobalId() {
        return mIDGenerator.getAndIncrement();
    }

    public int generateGlobalThreadId() {
        return mThreadGlobalCounter.getAndIncrement();
    }

    public File getDir(Context context) {
        if (mDownloadDir == null) {
            File file = context.getCacheDir();
            file = new File(file.getAbsolutePath(), "download");
            if (!file.exists()) {
                file.mkdirs();
            }
            mDownloadDir = file;
        }
        return mDownloadDir;
    }
}
