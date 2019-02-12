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
import android.text.TextUtils;

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
    private static Context mContext;

    public static DownloadImpl getInstance() {
        return sInstance;
    }

    public ResourceRequest with(Context context) {
        if (null != context) {
            mContext = context.getApplicationContext();
        }
        return ResourceRequest.with(mContext);
    }

    public ResourceRequest with(String url) {
        if (null == mContext) {
            throw new NullPointerException("Context can't be null . ");
        }
        return ResourceRequest.with(mContext).url(url);
    }

    public ResourceRequest with(Context context, String url) {
        if (null != context) {
            mContext = context.getApplicationContext();
        }
        return ResourceRequest.with(mContext).url(url);
    }

    private void safe(DownloadTask downloadTask) {
        if (null == downloadTask.getContext()) {
            throw new NullPointerException("context can't be null .");
        }
        if (TextUtils.isEmpty(downloadTask.getUrl())) {
            throw new NullPointerException("url can't be empty .");
        }
    }

    public boolean enqueue(DownloadTask downloadTask) {
        safe(downloadTask);
        return new Downloader().download(downloadTask);
    }

    public File call(DownloadTask downloadTask) {
        safe(downloadTask);
        Callable<File> callable = new SyncDownloader(downloadTask);
        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File callEx(DownloadTask downloadTask) throws Exception {
        safe(downloadTask);
        Callable<File> callable = new SyncDownloader(downloadTask);
        return callable.call();
    }

    public DownloadTask cancel(String url) {
        return ExecuteTasksMap.getInstance().cancelTask(url);
    }

    public List<DownloadTask> cancelAll() {
        return ExecuteTasksMap.getInstance().cancelTasks();
    }


    public DownloadTask pause(String url) {
        DownloadTask downloadTask = ExecuteTasksMap.getInstance().pauseTask(url);
        if (downloadTask != null) {
            mTasks.put(downloadTask.getUrl(), downloadTask);
        }
        return downloadTask;
    }

    public List<DownloadTask> pauseAll(String url) {
        List<DownloadTask> downloadTasks = ExecuteTasksMap.getInstance().pauseTasks();
        return downloadTasks;
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
        return ExecuteTasksMap.getInstance().exist(url);
    }

}
