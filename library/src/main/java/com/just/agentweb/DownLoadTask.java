package com.just.agentweb;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DownLoadTask implements Serializable {


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
    private boolean enableIndicator=true;
    /**
     *  Context
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

    private WeakReference<DownloadResultListener>mReference=null;
    private DefaultMsgConfig.DownLoadMsgConfig mDownLoadMsgConfig;


    public DownLoadTask(int id, String url, DownloadResultListener downloadResultListeners, boolean isForce, boolean enableIndicator, Context context, File file, long length, DefaultMsgConfig.DownLoadMsgConfig downLoadMsgConfig, int drawableRes) {
        this.id = id;
        this.url = url;
        this.isForce = isForce;
        this.enableIndicator = enableIndicator;
        mContext = context;
        mFile = file;
        this.length = length;
        this.drawableRes = drawableRes;
        mReference=new WeakReference<DownloadResultListener>(downloadResultListeners);
        this.mDownLoadMsgConfig=downLoadMsgConfig;
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

    public void setUrl(String url) {
        this.url = url;
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

    public void setEnableIndicator(boolean enableIndicator) {
        this.enableIndicator = enableIndicator;
    }

    public WeakReference<DownloadResultListener> getReference() {
        return mReference;
    }

    public void setReference(WeakReference<DownloadResultListener> reference) {
        mReference = reference;
    }

    public DefaultMsgConfig.DownLoadMsgConfig getDownLoadMsgConfig() {
        return mDownLoadMsgConfig;
    }

    public void setDownLoadMsgConfig(DefaultMsgConfig.DownLoadMsgConfig downLoadMsgConfig) {
        mDownLoadMsgConfig = downLoadMsgConfig;
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

    public DownloadResultListener getDownLoadResultListener() {
        return mReference.get();
    }



    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }
}
