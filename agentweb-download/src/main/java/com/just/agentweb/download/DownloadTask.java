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

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DownloadTask extends Extra implements Serializable {

	private int mId;

	/**
	 * Context
	 */
	private Context mContext;
	/**
	 * 下载的文件
	 */
	private File mFile;
	/**
	 * 表示当前任务是否被销毁了。
	 */
	private AtomicBoolean mIsDestroyed = new AtomicBoolean(false);
	private SimpleDownloadListener mSimpleDownloadListener;

	public void setSimpleDownloadListener(SimpleDownloadListener simpleDownloadListener) {
		mSimpleDownloadListener = simpleDownloadListener;
	}

	private String TAG = this.getClass().getSimpleName();
	private long mTotalsLength;


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
	}

	public SimpleDownloadListener getSimpleDownloadListener() {
		return null;
	}

	public SimpleDownloadListener getDownloadListener() {
		return null;
	}

	public long getLength() {
		return mTotalsLength;
	}

	public void setLength(long length) {
		mTotalsLength = length;
	}
}
