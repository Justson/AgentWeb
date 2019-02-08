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

import android.net.Uri;

/**
 * @author cenxiaozhong
 * @date 2018/6/21
 * @update 4.0.0
 * @since 1.0.0
 */
public interface DownloadListener {


	/**
	 * @param url                下载链接
	 * @param userAgent          mUserAgent
	 * @param contentDisposition mContentDisposition
	 * @param mimetype           资源的媒体类型
	 * @param contentLength      文件长度
	 * @param extra              下载配置 ，可以通过 Extra 修改下载icon ， 关闭进度条 ， 或者是否强制下载。
	 * @return true              处理了该下载事件 ， false 交给 AgentWeb 下载
	 */
	boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra);

	/**
	 * @param url        下载链接
	 * @param downloaded 已经下载的长度
	 * @param length     文件的总大小
	 * @param usedTime   耗时,单位ms
	 *                   <p>
	 *                   注意该方法回调在子线程 ，线程名 pool-agentweb-thread-xx
	 */
	void onProgress(String url, long downloaded, long length, long usedTime);


	/**
	 * @param throwable 如果异常，返回给异常
	 * @param path      文件的绝对路径
	 * @param url       下载的地址
	 * @return true     处理了下载完成后续的事件 ，false 默认交给Downloader 处理
	 */
	boolean onResult(Throwable throwable, Uri path, String url, Extra extra);


}
