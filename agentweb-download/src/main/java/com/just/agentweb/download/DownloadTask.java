package com.just.agentweb.download;

import android.content.Context;

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
     * 下载的地址
     */
    private String url;
    /**
     * 是否强制下载不管网络类型
     */
    private boolean isForce;

    /**
     * 如否需要需要指示器
     */
    private boolean enableIndicator = true;
    /**
     * Context
     */
    private Context mContext;
    /**
     * 下载的文件
     */
    private File mFile;
    /**
     * 文件的总大小
     */
    private long length;
    /**
     * 通知的icon
     */
    private int drawableRes;

    private WeakReference<DownloadListenerAdapter> mDownloadWR = null;
    /**
     * 表示当前任务是否被销毁了。
     */
    private AtomicBoolean isDestroy = new AtomicBoolean(false);


    private volatile boolean isParallelDownload = false;


    private WeakReference<DefaultDownloadImpl.ExtraServiceImpl> mExtraServiceImpl = null;


    private String TAG = this.getClass().getSimpleName();


    public DownloadTask(int id,
                        DownloadListenerAdapter downloadListeners,
                        Context context, File file,
                        DefaultDownloadImpl.ExtraServiceImpl extraServiceImpl) {
        super();
        this.id = id;
        this.url = extraServiceImpl.getUrl();
        this.isForce = extraServiceImpl.isForceDownload();
        this.enableIndicator = extraServiceImpl.isEnableIndicator();
        this.mContext = context;
        this.mFile = file;
        this.length = extraServiceImpl.getContentLength();
        this.drawableRes = extraServiceImpl.getIcon() == -1 ? R.drawable.ic_file_download_black_24dp : extraServiceImpl.getIcon();
        mDownloadWR = new WeakReference<DownloadListenerAdapter>(downloadListeners);
        this.isParallelDownload = extraServiceImpl.isParallelDownload();
        this.mExtraServiceImpl = new WeakReference<DefaultDownloadImpl.ExtraServiceImpl>(extraServiceImpl);
    }

    public DefaultDownloadImpl.ExtraServiceImpl getExtraServiceImpl() {
        return mExtraServiceImpl.get();
    }

    public boolean isParallelDownload() {
        return isParallelDownload;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }


    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public boolean isEnableIndicator() {
        return enableIndicator;
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

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public DownloadListenerAdapter getDownloadListener() {
        return mDownloadWR.get();
    }


    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }


    public void destroy() {
        this.isDestroy.set(true);
        this.id = -1;
        this.url = null;
        this.isForce = false;
        this.enableIndicator = false;
        this.mContext = null;
        this.mFile = null;
        this.length = -1;
        this.drawableRes = -1;
        this.mDownloadWR = null;
        this.isParallelDownload = false;
        this.mExtraServiceImpl = null;
        this.isDestroy = null;
    }

    public int getBlockMaxTime() {
        return mExtraServiceImpl.get().getBlockMaxTime();
    }

    @Override
    public int getConnectTimeOut() {
        return mExtraServiceImpl.get().getConnectTimeOut();
    }

    @Override
    public long getDownloadTimeOut() {
        return mExtraServiceImpl.get().getDownloadTimeOut();
    }

    public boolean isDestroy() {
        return null == this.isDestroy || isDestroy.get();
    }
}
