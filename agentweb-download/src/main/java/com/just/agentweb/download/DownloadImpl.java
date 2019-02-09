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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cenxiaozhong
 * @date 2019/2/9
 * @since 1.0.0
 */
public class DownloadImpl {

	private static final DownloadImpl sInstance = new DownloadImpl();
	private ConcurrentHashMap<String, DownloadTask> mTasks = new ConcurrentHashMap<>();

	public static DownloadImpl getInstance() {
		return sInstance;
	}

	public ResourceRequest with(Context context) {
		return ResourceRequest.with(context);
	}


	public void enqueue(DownloadTask downloadTask) {
		new Downloader().download(downloadTask);
	}

	public File call(DownloadTask downloadTask) {
		Callable<File> callable = new SyncDownloader(downloadTask);
		try {
			return callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public File callEx(DownloadTask downloadTask) throws Exception {
		Callable<File> callable = new SyncDownloader(downloadTask);
		return callable.call();
	}

	public DownloadTask cancel(String url) {
		return CancelDownloadInformer.getInformer().cancelAction(url);
	}

	public List<DownloadTask> cancelAll() {
		return CancelDownloadInformer.getInformer().cancelActions();
	}

	public DownloadTask pause(String url) {
		DownloadTask downloadTask = cancel(url);
		if (downloadTask != null) {
			mTasks.put(downloadTask.getUrl(), downloadTask);
		}
		return downloadTask;
	}

	public boolean resume(String url) {
		DownloadTask downloadTask = mTasks.get(url);
		if (downloadTask != null) {
			enqueue(downloadTask);
			return true;
		}
		return false;
	}

	public boolean exist(String url) {
		return ExecuteTasksMap.getInstance().contains(url);
	}

}
