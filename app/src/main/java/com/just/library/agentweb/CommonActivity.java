package com.just.library.agentweb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        Bundle mBundle=null;
        ft.add(R.id.container_framelayout,mAgentWebFragment=AgentWebFragment.getInstance(mBundle=new Bundle()),AgentWebFragment.class.getName());
        switch (key){

            /*Fragment 使用AgenWebt*/
            case 0:
                mBundle.putString(AgentWebFragment.URL_KEY,"http://www.jd.com");
                break;
            case 1:
                mBundle.putString(AgentWebFragment.URL_KEY,"https://h5.m.jd.com/active/download/download.html?channel=jd-msy1");
                break;
            case 2:
                mBundle.putString(AgentWebFragment.URL_KEY,"file:///android_asset/upload_file/uploadfile.html");
                break;
            case 3:
                mBundle.putString(AgentWebFragment.URL_KEY,"file:///android_asset/upload_file/jsuploadfile.html");
                break;


        }
        ft.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mAgentWebFragment.onActivityResult(requestCode,resultCode,data);
        Log.i("Info","activity result");
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
