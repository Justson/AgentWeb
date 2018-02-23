package com.just.agentweb.sample.activity;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.NestedScrollAgentWebView;
import com.just.agentweb.sample.R;

public class AutoHidenToolbarActivity extends AppCompatActivity {

    private AgentWeb mAgentWeb;
    private CoordinatorLayout main;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_hiden_toolbar);

        initView();

        setSupportActionBar(toolbar);

        NestedScrollAgentWebView webView = new NestedScrollAgentWebView(this);

        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(-1, -1);
        lp.setBehavior(new AppBarLayout.ScrollingViewBehavior());

        mAgentWeb = AgentWeb.with(this)
                .setWebView(webView) //传入支持内嵌滑动的webview
                .setAgentWebParent(main, lp)//lp记得设置behavior属性
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com/");

    }

    private void initView() {
        main = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
    }
}
