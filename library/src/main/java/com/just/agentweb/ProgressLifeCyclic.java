package com.just.agentweb;

/**
 * Created by cenxiaozhong on 2017/5/26.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface ProgressLifeCyclic {

    void showProgressBar();

    void setProgressBar(int newProgress);

    void finish();
}
