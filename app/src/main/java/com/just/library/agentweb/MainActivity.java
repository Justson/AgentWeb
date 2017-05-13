package com.just.library.agentweb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.just.library.AgentWeb;

public class MainActivity extends AppCompatActivity {

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*AgentWeb.with(this)//
        .enableProgress()//
        .setViewGroup((ViewGroup) this.findViewById(R.id.container),new LinearLayout.LayoutParams(-1,-1))//
        .buildAgentWeb()//
        .ready()//
        .loadUrl("http://www.mi.com");*/


        //
//
//
//
        mAgentWeb = AgentWeb.with(this)//
                .createContentViewTag()//
                .useDefaultIndicator()//
                .setIndicatorColor(-1)
                .addJavascriptInterface("hello", new HelloJs())//
                .createAgentWeb()//
                .ready()
                .go("https://www.wandoujia.com");

        /*AgentWeb.with(this)//
        .setViewGroup(null,null)//
        .useDefaultIndicator()//
        .defaultProgressBarColor()//
        .createAgentWeb()//
        .ready()//
        .go()*/

        /*AgentWeb.with(this,null)//
        .configRootView(null,null)//
        .setCustomIndicator(null)//
        .createAgentWeb()//
        .ready()//
        .go("http://www.jd.com");*/


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.destroy();
    }
}
