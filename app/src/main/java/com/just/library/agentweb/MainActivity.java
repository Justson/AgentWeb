package com.just.library.agentweb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.just.library.AgentWeb;

public class MainActivity extends AppCompatActivity {

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


        AgentWeb.with(this)//
        .createContentViewTag()//
        .useDefaultIndicator()//
        .setIndicatorColor(-1)
        .createAgentWeb()//
        .ready()
        .go("http://www.jd.com");


        AgentWeb.with(this,null)//
        .configViewGroup(null,null)//
        .setCustomIndicator(null)//
        .createAgentWeb()//
        .ready()//
        .go("http://www.jd.com");


    }
}
