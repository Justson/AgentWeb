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

package com.just.agentweb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 * @author cenxiaozhong
 * @date 2017/5/12
 * @since 1.0.0
 */
public abstract class BaseIndicatorView extends FrameLayout implements BaseIndicatorSpec,LayoutParamsOffer{
    public BaseIndicatorView(Context context) {
        super(context);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void reset() {

    }

    @Override
    public void setProgress(int newProgress) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

}
