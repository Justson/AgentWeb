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
import java.util.concurrent.CountDownLatch;

/**
 * @author cenxiaozhong
 * @date 2019/2/9
 * @since 1.0.0
 */
public class SyncDownloader extends Downloader implements Callable<File> {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private volatile boolean mEnqueue;

    SyncDownloader(DownloadTask downloadTask) {
        super();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new UnsupportedOperationException("Sync download must call it in the non main-Thread  ");
        }
        mDownloadTask = downloadTask;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        synchronized (this) {
            notify();
        }
    }

    @Override
    protected void destroyTask() {
    }

    @Override
    public DownloadTask cancelDownload() {
        super.cancelDownload();
        return null;
    }

    @Override
    public File call() throws Exception {
        synchronized (this) {
            final CountDownLatch syncLatch = new CountDownLatch(1);
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    mEnqueue = download(mDownloadTask);
                    syncLatch.countDown();
                }
            });
            syncLatch.await();
            if (!mEnqueue) {
                throw new RuntimeException("download task already exist!");
            }
            wait();
        }
        if (null != mThrowable) {
            throw (RuntimeException) mThrowable;
        }
        return mDownloadTask.mFile;
    }


}
