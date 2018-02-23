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
import android.support.annotation.DrawableRes;

import com.just.agentweb.LogUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DownloadTask extends AgentWebDownloader.Extra implements Serializable {


	private int mId;

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
	private AtomicBoolean mIsDestroyed = new AtomicBoolean(false);

	private WeakReference<DefaultDownloadImpl.ExtraServiceImpl> mExtraServiceImpl = null;

	private String TAG = this.getClass().getSimpleName();

	private DefaultDownloadImpl.ExtraServiceImpl mCloneExtraService;

	public DownloadTask(int id,
	                    DownloadListenerAdapter downloadListeners,
	                    Context context, File file,
	                    DefaultDownloadImpl.ExtraServiceImpl extraServiceImpl) {
		super();

		this.mId = id;
		this.mContext = context;
		this.mFile = file;
		this.mDownloadWR = new WeakReference<DownloadListenerAdapter>(downloadListeners);
		this.mIsParallelDownload = extraServiceImpl.isParallelDownload();
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
		return this.mId;
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
		return null == this.mIsDestroyed || this.mIsDestroyed.get();
	}

	@Override
	public boolean isAutoOpen() {
		return this.mCloneExtraService.isAutoOpen();
	}

	public void destroy() {
		this.mIsDestroyed.set(true);
		this.mId = -1;
		this.mUrl = null;
		this.mContext = null;
		this.mFile = null;
		this.mDownloadWR = null;
		this.mIsParallelDownload = false;
		if (null != this.mExtraServiceImpl.get()) {
			this.mExtraServiceImpl.clear();
		}
		this.mExtraServiceImpl = null;
		this.mIsDestroyed = null;
		this.mCloneExtraService = null;
	}
}
