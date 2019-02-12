package com.just.agentweb.download;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.just.agentweb.AgentWebUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static Pattern DISPOSITION_PATTERN = Pattern.compile(".*filename=(.*)");

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

    public File createFile(Context context, Extra extra) {
        String fileName = "";
        try {
            fileName = getFileNameByContentDisposition(extra.getContentDisposition());
            if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(extra.getUrl())) {
                Uri mUri = Uri.parse(extra.getUrl());
                fileName = mUri.getPath().substring(mUri.getPath().lastIndexOf('/') + 1);
            }
            if (!TextUtils.isEmpty(fileName) && fileName.length() > 64) {
                fileName = fileName.substring(fileName.length() - 64, fileName.length());
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = AgentWebUtils.md5(extra.getUrl());
            }
            if (fileName.contains("\"")) {
                fileName = fileName.replace("\"", "");
            }
            return createFileByName(context, fileName, !extra.isBreakPointDownload());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public File createFileByName(Context context, String name, boolean cover) throws IOException {
        String path = getDir(context).getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File mFile = new File(path, name);
        if (mFile.exists()) {
            if (cover) {
                mFile.delete();
                mFile.createNewFile();
            }
        } else {
            mFile.createNewFile();
        }

        return mFile;
    }

    private String getFileNameByContentDisposition(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) {
            return "";
        }
        Matcher m = DISPOSITION_PATTERN.matcher(contentDisposition.toLowerCase());
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
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
