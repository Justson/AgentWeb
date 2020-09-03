package com.just.agentweb.sample.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.just.agentweb.sample.R;
import com.just.agentweb.sample.fragment.EasyWebFragment;

/**
 * Created by cenxiaozhong on 2017/7/22.
 */

public class ContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        Fragment mFragment=null;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_framelayout,mFragment= EasyWebFragment.getInstance(new Bundle()),EasyWebFragment.class.getName())
                .show(mFragment)
                .commit();
    }
}
