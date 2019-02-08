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

import com.just.agentweb.LogUtils;

/**
 * @author cenxiaozhong
 * @date 2019/2/8
 * @since 1.0.0
 */
public class AgentWebDownloader extends Downloader implements IAgentWebDownloader {
	private static final String TAG = AgentWebDownloader.class.getSimpleName();

	@Override
	public synchronized boolean isShutdown() {
		LogUtils.i(TAG, "" + mIsShutdown.get() + "  " + mIsCanceled.get() + "  :" + (getStatus() == Status.FINISHED));
		return mIsShutdown.get() || mIsCanceled.get() || (getStatus() == Status.FINISHED);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		DownloadTask downloadTask = this.mDownloadTask;
		if (null != downloadTask.getDownloadListener()) {
			downloadTask.getDownloadListener().onBindService(downloadTask.getUrl(), this);
		}
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);
		DownloadTask downloadTask = this.mDownloadTask;
		if (null != downloadTask.getDownloadListener()) {
			downloadTask.getDownloadListener().onUnbindService(downloadTask.getUrl(), this);
		}
	}

	@Override
	public synchronized IAgentWebDownloader.ExtraService shutdownNow() {
		if (getStatus() == Status.FINISHED) {
			LogUtils.e(TAG, "  Termination failed , becauce the downloader already dead !!! ");
			return null;
		}
		try {
			ExtraService mExtraService = (ExtraService) mDownloadTask;
			return mExtraService;
		} finally {
			mIsShutdown.set(true);
		}
	}
}
