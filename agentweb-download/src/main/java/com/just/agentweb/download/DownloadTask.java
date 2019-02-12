/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb.download;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DownloadTask extends Extra implements Serializable, Cloneable {

    static final String TAG = DownloadTask.class.getSimpleName();
    int mId = Rumtime.getInstance().generateGlobalId();
    long mTotalsLength;
    Context mContext;
    File mFile;
    DownloadListener mDownloadListener;
    public static final int STATUS_NEW = 1000;
    public static final int STATUS_PENDDING = 1001;
    public static final int STATUS_DOWNLOADING = 1002;
    public static final int STATUS_COMPLETED = 1003;

    @IntDef({STATUS_NEW, STATUS_PENDDING, STATUS_DOWNLOADING, STATUS_COMPLETED})
    @interface DownloadTaskStatus {
    }

    private AtomicInteger status = new AtomicInteger(STATUS_NEW);

    public DownloadTask() {
        super();
    }

    public int getStatus() {
        return status.get();
    }

    void setStatus(@DownloadTaskStatus int status) {
        this.status.set(status);
    }

    public int getId() {
        return this.mId;
    }

    public Context getContext() {
        return mContext;
    }

    public DownloadTask setContext(Context context) {
        mContext = context.getApplicationContext();
        return this;
    }

    public File getFile() {
        return mFile;
    }

    public Uri getFileUri() {
        return Uri.fromFile(this.mFile);
    }

    public DownloadTask setFile(File file) {
        mFile = file;
        return this;
    }

    protected void destroy() {
        this.mId = -1;
        this.mUrl = null;
        this.mContext = null;
        this.mFile = null;
        this.mIsParallelDownload = false;
        mIsForceDownload = false;
        mEnableIndicator = true;
        mIcon = R.drawable.ic_file_download_black_24dp;
        mIsParallelDownload = true;
        mIsBreakPointDownload = true;
        mUserAgent = "";
        mContentDisposition = "";
        mMimetype = "";
        mContentLength = -1L;
        if (mHeaders != null) {
            mHeaders.clear();
            mHeaders = null;
        }
        status.set(STATUS_NEW);
    }

    public DownloadListener getDownloadListener() {
        return mDownloadListener;
    }

    public DownloadTask setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        return this;
    }

    public long getLength() {
        return mTotalsLength;
    }

    @Override
    protected DownloadTask clone() {
        try {
            return (DownloadTask) super.clone();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return new DownloadTask();
        }
    }

    public void setLength(long length) {
        mTotalsLength = length;
    }
}
