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

package com.just.agentwebX5;

import android.view.View;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;


/**
 * @author cenxiaozhong
 * @date 2017/6/10
 * @since 2.0.0
 */
public interface IVideo {


    void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback);


    void onHideCustomView();


    boolean isVideoState();

}
