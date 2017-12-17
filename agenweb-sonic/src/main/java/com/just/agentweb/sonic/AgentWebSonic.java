package com.just.agentweb.sonic;

import android.app.Activity;

import com.just.agentweb.AgentWeb;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

/**
 * Created by cenxiaozhong on 2017/12/17.
 */

public class AgentWebSonic {

    private SonicSession sonicSession;
    private Activity mActivity;
    private AgentWeb mAgentWeb;
    private String url;
    public AgentWebSonic(){

    }
    SonicSessionClientImpl sonicSessionClient;
    /**
     * AgentWeb Create 之前回调
     */
    public void preCreate(String url) {


        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
        sessionConfigBuilder.setSupportLocalServer(true);

        // create sonic session and run sonic flow
        sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // throw new UnknownError("create session fail!");
//            Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
        }

    }

    public void go(){
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(mAgentWeb);
            sonicSessionClient.clientReady();
        } else { // default mode
            mAgentWeb.getLoader().loadUrl(url);
        }
    }


}
