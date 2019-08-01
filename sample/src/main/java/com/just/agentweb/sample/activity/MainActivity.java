package com.just.agentweb.sample.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.sample.R;
import com.just.agentweb.sample.common.GuideItemEntity;

import static com.just.agentweb.sample.sonic.SonicJavaScriptInterface.PARAM_CLICK_TIME;

/**
 * source code  https://github.com/Justson/AgentWeb
 */
public class MainActivity extends AppCompatActivity {


    private ListView mListView;

    private Toolbar mToolbar;
    private TextView mTitleTextView;


    public static final int FLAG_GUIDE_DICTIONARY_USE_IN_ACTIVITY = 0x01;
    public static final int FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT = FLAG_GUIDE_DICTIONARY_USE_IN_ACTIVITY << 1;
    public static final int FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD = FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT << 1;
    public static final int FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM = FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD << 1;
    public static final int FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION = FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM << 1;
    public static final int FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN = FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION << 1;
    public static final int FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR = FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN << 1;
    public static final int FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS = FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR << 1;
    public static final int FLAG_GUIDE_DICTIONARY_LINKS = FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS << 1;
    public static final int FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT = FLAG_GUIDE_DICTIONARY_LINKS << 1;
    public static final int FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE = FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT << 1;
    public static final int FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_ACT = FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE << 1;
    public static final int FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_FRAG = FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_ACT << 1;
    public static final int FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH = FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_FRAG << 1;
    public static final int FLAG_GUIDE_DICTIONARY_MAP = FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH << 1;
    public static final int FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE = FLAG_GUIDE_DICTIONARY_MAP << 1;
    public static final int FLAG_GUIDE_DICTIONARY_LINKAGE_WITH_TOOLBAR = FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE << 1;
    public static final int FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW = FLAG_GUIDE_DICTIONARY_LINKAGE_WITH_TOOLBAR << 1;
    public static final int FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE = FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW << 1;
    public static final int FLAG_GUIDE_DICTIONARY_COMMON_FILE_DOWNLOAD = FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE << 1;
    public static final GuideItemEntity[] datas = new GuideItemEntity[]{
            new GuideItemEntity("Activity 使用 AgentWeb", FLAG_GUIDE_DICTIONARY_USE_IN_ACTIVITY),
            new GuideItemEntity("Fragment 使用 AgentWeb ", FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT),
            new GuideItemEntity("H5文件下载", FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD),
            new GuideItemEntity("input标签文件上传", FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM),
            new GuideItemEntity("Js 通信文件上传,兼用Android 4.4Kitkat", FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE),
            new GuideItemEntity("Js 通信", FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION),
            new GuideItemEntity("Video 视频全屏播放", FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN),
            new GuideItemEntity("自定义进度条", FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR),
            new GuideItemEntity("自定义设置", FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS),
            new GuideItemEntity("电话 ， 信息 ， 邮件", FLAG_GUIDE_DICTIONARY_LINKS),
            new GuideItemEntity("自定义 WebView", FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW),
            new GuideItemEntity("下拉回弹效果", FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT),
            new GuideItemEntity("Jsbridge 例子", FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE),
            new GuideItemEntity("继承 BaseAgentWebActivity", FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_ACT),
            new GuideItemEntity("继承 BaseAgentWebFragment", FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_FRAG),
            new GuideItemEntity("SmartRefresh 下拉刷新", FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH),
            new GuideItemEntity("地图", FLAG_GUIDE_DICTIONARY_MAP),
            new GuideItemEntity("VasSonic 首屏秒开", FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE),
            new GuideItemEntity("与ToolBar联动", FLAG_GUIDE_DICTIONARY_LINKAGE_WITH_TOOLBAR),
            new GuideItemEntity("原生文件下载", FLAG_GUIDE_DICTIONARY_COMMON_FILE_DOWNLOAD),
    };


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
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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


        if (AgentWebConfig.DEBUG) {
            Log.i("Info", "Debug 模式");
        } else {
            Log.i("Info", "release 模式");
        }

        AgentWebConfig.debug();
    }


    private void doClick(int position) {

        int index = datas[position].getGuideDictionary();
        switch (index) {

            /* Activity agentWeb */
            case FLAG_GUIDE_DICTIONARY_USE_IN_ACTIVITY:

                startActivity(new Intent(this, WebActivity.class));
                break;
            case FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT));
                break;
            case FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD));
                break;
            case FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM));
                break;
            case FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE));
                break;
            case FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION));
                break;
            case FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN));
                break;

            case FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR));
                break;
            case FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS));
                break;
            case FLAG_GUIDE_DICTIONARY_LINKS:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_LINKS));
                break;
            case FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW));
                break;
            case FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT));
                break;
            case FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE));
                break;
            case FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_ACT:
                startActivity(new Intent(this, EasyWebActivity.class));
                break;

            case FLAG_GUIDE_DICTIONARY_EXTENDS_BASE_FRAG:
                startActivity(new Intent(this, ContainerActivity.class));
                break;

            case FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH));
                break;
            case FLAG_GUIDE_DICTIONARY_MAP:
                startActivity(new Intent(this, CommonActivity.class)
                        .putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_MAP));
                break;
            case FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE:
                startActivity(new Intent(this,
                        CommonActivity.class).putExtra(CommonActivity.TYPE_KEY, FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE)
                        .putExtra(PARAM_CLICK_TIME, System.currentTimeMillis()));
                break;
            case FLAG_GUIDE_DICTIONARY_LINKAGE_WITH_TOOLBAR:
                startActivity(new Intent(this, AutoHidenToolbarActivity.class));
                break;
            case FLAG_GUIDE_DICTIONARY_COMMON_FILE_DOWNLOAD:
                startActivity(new Intent(this, NativeDownloadActivity.class));
                break;
            default:
                break;

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

            mViewHolder.mTextView.setText(datas[position].getGuideTitle());
            return convertView;
        }


    }

    class ViewHolder {
        TextView mTextView;
    }


}
