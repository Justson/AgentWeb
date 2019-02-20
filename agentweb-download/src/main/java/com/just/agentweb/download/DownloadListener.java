/*
 * Copyright (C)  Justson(https://github.com/Justson/Downloader)
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
import android.support.annotation.MainThread;


/**
 * @author cenxiaozhong
 * @date 2018/2/11
 */
public class DownloadListener {

	@MainThread
	public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
		return false;
	}

	public void onProgress(String url, long downloaded, long length, long usedTime) {
	}

	@MainThread
	public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
		return false;
	}
}
