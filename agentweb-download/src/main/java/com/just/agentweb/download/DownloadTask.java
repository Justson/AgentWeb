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

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.just.agentweb.download.Config.NOTICATION_ID;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DownloadTask extends Extra implements Serializable, Cloneable {

	int mId = NOTICATION_ID.getAndIncrement();
	static final String TAG = DownloadTask.class.getSimpleName();
	long mTotalsLength;
	/**
	 * Context
	 */
	Context mContext;
	/**
	 * 下载的文件
	 */
	File mFile;
	/**
	 * 表示当前任务是否被销毁了。
	 */
	AtomicBoolean mIsDestroyed = new AtomicBoolean(false);
	DownloadListener mDownloadListener;

	public DownloadTask() {
		super();
	}


	public int getId() {
		return this.mId;
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

	public Uri getFileUri() {
		return Uri.fromFile(this.mFile);
	}

	public void setFile(File file) {
		mFile = file;
	}


	public boolean isDestroy() {
		return null == this.mIsDestroyed || this.mIsDestroyed.get();
	}


	public void destroy() {
		this.mIsDestroyed.set(true);
		this.mId = -1;
		this.mUrl = null;
		this.mContext = null;
		this.mFile = null;
		this.mIsParallelDownload = false;
		mIsForceDownload = false;
		mEnableIndicator = true;
		mIcon = R.drawable.ic_file_download_black_24dp;
		mIsParallelDownload = true;
		mIsOpenBreakPointDownload = true;
		mUserAgent = "";
		mContentDisposition = "";
		mMimetype = "";
		mContentLength = -1L;
		if (mHeaders != null) {
			mHeaders.clear();
			mHeaders = null;
		}
	}

	public DownloadListener getDownloadListener() {
		return mDownloadListener;
	}

	public void setDownloadListener(DownloadListener downloadListener) {
		mDownloadListener = downloadListener;
	}

	public long getLength() {
		return mTotalsLength;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setLength(long length) {
		mTotalsLength = length;
	}
}
