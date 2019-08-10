package com.just.agentweb.sample.sonic;

import android.content.Context;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.MiddlewareWebClientBase;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

/**
 * Created by cenxiaozhong on 2017/12/17.
 */

public class SonicImpl {

    private SonicSession sonicSession;
    private Context mContext;
    private String url;
    private SonicSessionClientImpl sonicSessionClient;
    public SonicImpl(String url , Context context){
        this.url=url;
        this.mContext=context;

    }
    
    /**
     */
    public void onCreateSession() {

        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
        sessionConfigBuilder.setSupportLocalServer(true);
        SonicEngine.createInstance(new DefaultSonicRuntimeImpl(mContext.getApplicationContext()), new SonicConfig.Builder().build());
        // create sonic session and run sonic flow
        sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // throw new UnknownError("create session fail!");
//            Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
        }
    }

    public SonicSessionClientImpl getSonicSessionClient(){
        return this.sonicSessionClient;
    }

    /**
     * 不使用中间件，使用普通的 WebViewClient 也是可以的。
     * @return MiddlewareWebClientBase
     */
    public MiddlewareWebClientBase createSonicClientMiddleWare(){
        return new SonicWebViewClient(sonicSession);
    }

    public void bindAgentWeb(AgentWeb agentWeb){
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(agentWeb);
            sonicSessionClient.clientReady();
        } else { // default mode
            agentWeb.getUrlLoader().loadUrl(url);
        }
    }

    public void destrory(){
        if(sonicSession!=null){
            sonicSession.destroy();
        }
    }


}
