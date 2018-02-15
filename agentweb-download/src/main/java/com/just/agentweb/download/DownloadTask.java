package com.just.agentweb.download;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.just.agentweb.LogUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DownloadTask extends AgentWebDownloader.Extra implements Serializable {


    private int id;

    /**
     * Context
     */
    private Context mContext;
    /**
     * 下载的文件
     */
    private File mFile;

    private WeakReference<DownloadListenerAdapter> mDownloadWR = null;
    /**
     * 表示当前任务是否被销毁了。
     */
    private AtomicBoolean isDestroy = new AtomicBoolean(false);

    private WeakReference<DefaultDownloadImpl.ExtraServiceImpl> mExtraServiceImpl = null;

    private String TAG = this.getClass().getSimpleName();

    private DefaultDownloadImpl.ExtraServiceImpl mCloneExtraService;

    public DownloadTask(int id,
                        DownloadListenerAdapter downloadListeners,
                        Context context, File file,
                        DefaultDownloadImpl.ExtraServiceImpl extraServiceImpl) {
        super();

        this.id = id;
        this.mContext = context;
        this.mFile = file;
        this.mDownloadWR = new WeakReference<DownloadListenerAdapter>(downloadListeners);
        this.isParallelDownload = extraServiceImpl.isParallelDownload();
        try {
            this.mCloneExtraService = extraServiceImpl.clone();
            this.mExtraServiceImpl = new WeakReference<DefaultDownloadImpl.ExtraServiceImpl>(extraServiceImpl);
        } catch (CloneNotSupportedException e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
            this.mCloneExtraService = extraServiceImpl;
        }
    }

    public DefaultDownloadImpl.ExtraServiceImpl getExtraServiceImpl() {
        return mExtraServiceImpl.get();
    }
    @Override
    public boolean isParallelDownload() {
        return mCloneExtraService.isParallelDownload();
    }

    public int getId() {
        return id;
    }


    /**
     * 下载的地址
     */
    @Override
    public String getUrl() {
        return mCloneExtraService.getUrl();
    }


    /**
     * 是否强制下载不管网络类型
     */
    public boolean isForce() {
        return mCloneExtraService.isForceDownload();
    }

    /**
     * 如否需要需要指示器
     */
    @Override
    public boolean isEnableIndicator() {
        return mCloneExtraService.isEnableIndicator();
    }


    public WeakReference<DownloadListenerAdapter> getDownloadWR() {
        return mDownloadWR;
    }


    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    /**
     * 文件的总大小
     */
    public long getLength() {
        return mCloneExtraService.getContentLength();
    }

    /**
     * 通知的icon
     */
    @DrawableRes
    public int getDrawableRes() {
        return mCloneExtraService.getIcon() == -1 ? R.drawable.ic_file_download_black_24dp : mCloneExtraService.getIcon();
    }

    public DownloadListenerAdapter getDownloadListener() {
        return mDownloadWR.get();
    }
    @Override
    public int getBlockMaxTime() {
        return this.mCloneExtraService.getBlockMaxTime();
    }

    @Override
    public int getConnectTimeOut() {
        return this.mCloneExtraService.getConnectTimeOut();
    }

    @Override
    public long getDownloadTimeOut() {
        return this.mCloneExtraService.getDownloadTimeOut();
    }

    public boolean isDestroy() {
        return null == this.isDestroy || this.isDestroy.get();
    }


    public void destroy() {
        this.isDestroy.set(true);
        this.id = -1;
        this.url = null;
        this.mContext = null;
        this.mFile = null;
        this.mDownloadWR = null;
        this.isParallelDownload = false;
        if (this.mExtraServiceImpl.get() != null) {
            this.mExtraServiceImpl.clear();
        }
        this.mExtraServiceImpl = null;
        this.isDestroy = null;
        this.mCloneExtraService = null;
    }
}