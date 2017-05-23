package com.just.library.agentweb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong on 2017/5/23.
 */

public class CommonActivity extends AppCompatActivity {


    private FrameLayout mFrameLayout;
    public static final String TYPE_KEY="type_key";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        mFrameLayout = (FrameLayout) this.findViewById(R.id.container_framelayout);


        int key=getIntent().getIntExtra(TYPE_KEY,-1);
        mFragmentManager = this.getSupportFragmentManager();
        openFragment(key);
    }


    private AgentWebFragment mAgentWebFragment;
    private void openFragment(int key){

        FragmentTransaction ft=mFragmentManager.beginTransaction();

        switch (key){

            /*Fragment 使用AgenWebt*/
            case 0:
                ft.add(R.id.container_framelayout,mAgentWebFragment=AgentWebFragment.getInstance(new Bundle()),AgentWebFragment.class.getName());
                break;


        }
        ft.commit();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AgentWebFragment mAgentWebFragment=this.mAgentWebFragment;
        if(mAgentWebFragment!=null) {
            FragmentKeyDown mFragmentKeyDown = mAgentWebFragment;
            if(mFragmentKeyDown.onFragmentKeyDown(keyCode,event))
                return true;
            else
                return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }
}
