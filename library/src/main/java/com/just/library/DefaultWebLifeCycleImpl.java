package com.just.library;

/**
 * Created by cenxiaozhong on 2017/5/30.
 */

public class DefaultWebLifeCycleImpl implements WebLifeCycle {


    public static DefaultWebLifeCycleImpl create(AgentWeb agentWeb){
        return new DefaultWebLifeCycleImpl(agentWeb);
    }

    private AgentWeb mAgentWeb;
    public DefaultWebLifeCycleImpl(AgentWeb agentWeb){
        this.mAgentWeb=agentWeb;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        mAgentWeb.getWebCreator().get().getSettings().setJavaScriptEnabled(true);

    }

    @Override
    public void onStop() {
        mAgentWeb.getWebCreator().get().getSettings().setJavaScriptEnabled(false);

    }

    @Override
    public void onDestroy() {

    }
}
