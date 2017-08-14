package com.just.library.agentweb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.just.library.AgentWebConfig;

import static com.just.library.agentweb.CommonActivity.TYPE_KEY;

/**
 * source code  https://github.com/Justson/AgentWeb
 */
public class MainActivity extends AppCompatActivity {


    private ListView mListView;

    private Toolbar mToolbar;
    private TextView mTitleTextView;


    public static final String[] datas = new String[]{"Activity 使用 AgentWeb", "Fragment 使用 AgentWeb ", "文件下载", "input标签文件上传", "Js 通信文件上传,兼用Android 4.4Kitkat", "Js 通信","Video 视屏全屏播放", "自定义进度条", "自定义设置","电话 ， 信息 ， 邮件","自定义 WebView","下拉回弹效果","支持 Jsbridge","继承 BaseAgentWebActivity","继承 BaseAgentWebFragment","SmartRefresh 下拉刷新"};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
        mTitleTextView.setText("AgentWeb 使用指南");
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.this.finish();
            }
        });

        mListView = (ListView) this.findViewById(R.id.listView);
        mListView.setAdapter(new MainAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                doClick(position);
            }
        });



        if(AgentWebConfig.DEBUG){
            Log.i("Info","Debug 模式");
        }else{
            Log.i("Info","release 模式");
        }

    }

    private void doClick(int position) {


        switch (position) {

            /*Activity agentWeb*/
            case 0:

                startActivity(new Intent(this, WebActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 0));
                break;
            case 2:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 1));
                break;
            case 3:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 2));
                break;
            case 4:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 3));
                break;
            case 5:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 4));
                break;
            case 6:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 5));
                break;

            case 7:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 6));
                break;

            case 8:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 7));
                break;

            case 9:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 8));
                break;
            case 10:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 9));
                break;
            case 11:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 10));
                break;
            case 12:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 11));
                break;
            case 13:
                startActivity(new Intent(this, EasyWebActivity.class));
                break;

            case 14:
                startActivity(new Intent(this, ContainerActivity.class));
                break;

            case 15:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 12));

        }


    }


    public class MainAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                View mView = MainActivity.this.getLayoutInflater().inflate(R.layout.listview_main, parent, false);
                mViewHolder.mTextView = (TextView) mView.findViewById(R.id.content);
                mView.setTag(mViewHolder);
                convertView = mView;
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            mViewHolder.mTextView.setText(datas[position]);
            return convertView;
        }


    }

    class ViewHolder {
        TextView mTextView;
    }


}
