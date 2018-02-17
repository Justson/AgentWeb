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
 * @date 2018/2/4
 */
public interface DownloadingService {
    /**
     * 当前任务是否已经终止
     * @return
     */
    boolean isShutdown();

    /**
     * 终止当前下载的任务
     * @return ExtraService#performReDownload 重新提交下载任务
     */
    AgentWebDownloader.ExtraService shutdownNow();

}
