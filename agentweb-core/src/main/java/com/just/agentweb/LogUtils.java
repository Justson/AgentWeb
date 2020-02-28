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

import android.util.Log;

/**
 * @author cenxiaozhong
 * @date 2017/5/28
 * @since 1.0.0
 */
class LogUtils {

    private static final String PREFIX = "agentweb-";

    static boolean isDebug() {
        return AgentWebConfig.DEBUG;
    }

    static void i(String tag, String message) {

        if (isDebug()) {
            Log.i(PREFIX.concat(tag), message);
        }
    }

    static void v(String tag, String message) {

        if (isDebug()) {
            Log.v(PREFIX.concat(tag), message);
        }

    }

    static void safeCheckCrash(String tag, String msg, Throwable tr) {
        if (isDebug()) {
            throw new RuntimeException(PREFIX.concat(tag) + " " + msg, tr);
        } else {
            Log.e(PREFIX.concat(tag), msg, tr);
        }
    }

    static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    static void e(String tag, String message) {

        if (isDebug()) {
            Log.e(PREFIX.concat(tag), message);
        }
    }
}
