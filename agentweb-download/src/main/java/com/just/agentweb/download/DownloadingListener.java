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

/**
 * @author cenxiaozhong
 * @date 2018/2/11
 */
public interface DownloadingListener {

    /**
     * @param downloadingService 开发者可以通过 DownloadingService#shutdownNow 终止下载
     */
    void onBindService(String url, DownloadingService downloadingService);

    /**
     * @param url        下载链接
     * @param downloaded 已经下载的长度
     * @param length     文件的总大小
     * @param usedTime   耗时,单位ms
     *                   <p>
     *                   注意该方法回调在子线程 ，线程名 AsyncTask #XX or AgentWeb # XX
     */
    void onProgress(String url, long downloaded, long length, long usedTime);


    /**
     *
     * @param url
     * @param downloadingService 释放 downloadingService
     */
    void onUnbindService(String url, DownloadingService downloadingService);
}
