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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cenxiaozhong
 * @date 2019/2/8
 * @since 1.0.0
 */
public class Config {
	static AtomicInteger THREAD_GLOBAL_COUNTER = new AtomicInteger(1);
	/**
	 * 通知ID，默认从1开始
	 */
	static AtomicInteger NOTICATION_ID = new AtomicInteger(1);
}
