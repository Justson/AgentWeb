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

import java.util.concurrent.ConcurrentHashMap;


/**
 * CancelDownloadInformer 缓存当前所有 Downloader，
 * 如果用户滑动通知取消下载，通知相应 Downloader 取消下载。
 *
 * @author cenxiaozhong
 * @date 2018/2/12
 */
public final class CancelDownloadInformer {
	private ConcurrentHashMap<String, CancelDownloadRecipient> mRecipients = null;

	private CancelDownloadInformer() {
		mRecipients = new ConcurrentHashMap<>();
	}

	static CancelDownloadInformer getInformer() {
		return InformerHolder.INSTANCE;
	}

	void cancelAction(String url) {
		CancelDownloadRecipient mCancelDownloadRecipient = mRecipients.get(url);
		if (null != mCancelDownloadRecipient) {
			mCancelDownloadRecipient.cancelDownload();
		}
	}

	void addRecipient(String url, CancelDownloadRecipient recipient) {
		if (null != url && null != recipient) {
			mRecipients.put(url, recipient);
		}
	}

	void removeRecipient(String url) {
		if (null != url) {
			this.mRecipients.remove(url);
		}
	}

	private static class InformerHolder {
		private static final CancelDownloadInformer INSTANCE = new CancelDownloadInformer();
	}
}
