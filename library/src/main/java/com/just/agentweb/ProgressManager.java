package com.just.agentweb;

/**
 *Created by cenxiaozhong .
 * source code  https://github.com/Justson/AgentWeb
 */

public interface ProgressManager<T extends BaseProgressSpec> {


    T offer();
}
