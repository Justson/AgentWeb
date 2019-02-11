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
 * @date 2019/2/8
 * @since 1.0.0
 */

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 静态缓存当前正在下载的任务 Url
 * i -> Url
 * i+1 -> DownloadTask
 */
public class ExecuteTasksMap extends ReentrantReadWriteLock {
	private LinkedList<Object> mTasks = null;
	private static volatile ExecuteTasksMap sInstance = null;

	private ExecuteTasksMap() {
		super(false);
		mTasks = new LinkedList();
	}

	static ExecuteTasksMap getInstance() {
		if (null == sInstance) {
			synchronized (ExecuteTasksMap.class) {
				if (null == sInstance) {
					sInstance = new ExecuteTasksMap();
				}
			}
		}
		return sInstance;
	}

	void removeTask(String url) {
		writeLock().lock();
		try {
			int position = -1;
			if ((position = mTasks.indexOf(url)) == -1) {
				return;
			}
			mTasks.remove(position);
			mTasks.remove(position - 1);
		} finally {
			writeLock().unlock();
		}
	}

	void addTask(String url, DownloadTask task) {
		writeLock().lock();
		try {
			mTasks.add(url);
			mTasks.add(task);
		} finally {
			writeLock().unlock();
		}
	}

	// 加锁读
	boolean contains(String url) {
		readLock().lock();
		try {
			return mTasks.contains(url);
		} finally {
			readLock().unlock();
		}
	}
}
