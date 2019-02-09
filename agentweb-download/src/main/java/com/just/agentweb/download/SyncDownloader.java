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

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author cenxiaozhong
 * @date 2019/2/9
 * @since 1.0.0
 */
public class SyncDownloader extends Downloader implements Callable<File> {

	private static final Handler HANDLER = new Handler(Looper.getMainLooper());

	SyncDownloader(DownloadTask downloadTask) {
		super();
		mDownloadTask = downloadTask;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);
		notify();
	}

	@Override
	public File call() throws Exception {
		File file = mDownloadTask.mFile;
		synchronized (this) {
			HANDLER.post(new Runnable() {
				@Override
				public void run() {
					download(mDownloadTask);
				}
			});
			wait();
		}
		if (null != mThrowable) {
			throw (RuntimeException) mThrowable;
		}
		return file;
	}


}
