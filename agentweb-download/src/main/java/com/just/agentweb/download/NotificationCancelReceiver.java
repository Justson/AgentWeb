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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.just.agentweb.LogUtils;

/**
 * @author cenxiaozhong
 * @date 2018/2/12
 */
public class NotificationCancelReceiver extends BroadcastReceiver {

	public static final String ACTION = "com.agentweb.cancelled";

	public NotificationCancelReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION)) {
			try {
				String url = intent.getStringExtra("TAG");
				CancelDownloadInformer.getInformer().cancelAction(url);
			} catch (Throwable ignore) {
				if (LogUtils.isDebug()) {
					ignore.printStackTrace();
				}
			}

		}
	}
}