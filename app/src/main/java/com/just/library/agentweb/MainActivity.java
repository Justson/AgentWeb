package com.just.library.agentweb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AgentWeb.with(this)//
        .enableProgress()//
        .setViewGroup((ViewGroup) this.findViewById(R.id.container),new LinearLayout.LayoutParams(-1,-1))//
        .buildAgentWeb()//
        .createWebViewWithSettings()//
        .loadUrl("http://www.mi.com");


    }
}
