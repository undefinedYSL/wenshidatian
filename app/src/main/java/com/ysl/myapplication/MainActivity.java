package com.ysl.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.tu.loadingdialog.LoadingDialog;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.Glide;
import com.ezvizuikit.open.EZUIPlayer;
import com.ysl.myapplication.BubbleTips.BubblePopupWindow;
import com.ysl.myapplication.TouchListener.OnDoubleClickListener;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ysl.myapplication.Constant.strurl1;
import static com.ysl.myapplication.Constant.strurl3;


public class MainActivity extends AppCompatActivity {


    private LinearLayout ll1_1,ll2_1,ll3_1;
    private TextView tv_at,tv_ah,tv_st,tv_sh,tv_ra;
    private ImageView iv_display1;
    private String at,st,ah,sh,ra;
    private String imgurl,timeurl,imgurl1;
    private com.alibaba.fastjson.JSONObject jsonObject;
    private JSONObject jsonObject1;
    private ImageView refresh;
    ActivityManager exitAM = ActivityManager.getInstance();
    private static final String TAG = null;

    //海康直播用数据
    private static final String TAG1 = "MainActivity";
    private String playUrl = "ezopen://open.ys7.com/C50474868/1.live";
    private String appkey = "a9f5de51775d47eb8d0c489af966fe76";
    private String accesstoken = "";
    private String mGlobalAreaDomain = "";
    private boolean isGlobal = false;
    private String strurl = "https://open.ys7.com/api/lapp/token/get";

    //fastjson解析用
    private com.alibaba.fastjson.JSONObject jsonObject2;
    private String code = "";
    private String data = "";

    //气泡提示用
    private LayoutInflater inflater;
    private BubblePopupWindow tipsWindow;

    private Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x666:
                    tv_ah.setText(ah+"%");
                    tv_at.setText(at+"℃");
                    tv_sh.setText(sh+"%");
                    tv_st.setText(st+"℃");
                    tv_ra.setText("466W/㎡");
                    GlideApp
                            .with(MainActivity.this)
                            .load(imgurl1)
                            .placeholder(R.drawable.loading_bg)
                            .into(iv_display1);
//                case 0x888:
//                    tv_cond.setText(cond_txt);
//                    tv_tmp.setText(tmp+"℃");
//                    tv_pcpn.setText(pcpn+"mm");
//                case 0x999:
//                    Toast.makeText(MainActivity.this,"信息已更新",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        exitAM.addActivity(this);

        if (TextUtils.isEmpty(appkey)
                || TextUtils.isEmpty(accesstoken)
                || TextUtils.isEmpty(playUrl)){
            Toast.makeText(this,"appkey,accesstoken or playUrl is null",Toast.LENGTH_LONG).show();
            getBroadcastWithOkhttp();
//			finish();
//			return;
        }

        inflater = LayoutInflater.from(this);
        tipsWindow = new BubblePopupWindow(MainActivity.this);
    }

    private void init() {
        getRequestWithOkhttp();
        ll1_1 = (LinearLayout)findViewById(R.id.LL1);
        ll1_1.setSelected(true);
        ll1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1_1.setSelected(true);
                ll2_1.setSelected(false);
                ll3_1.setSelected(false);

            }
        });
        ll2_1 = (LinearLayout)findViewById(R.id.LL2);
        ll2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1_1.setSelected(false);
                ll2_1.setSelected(true);
                ll3_1.setSelected(false);
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this,Main2Activity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
//                finish();
            }
        });
        ll3_1 = (LinearLayout)findViewById(R.id.LL3);
        ll3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1_1.setSelected(false);
                ll2_1.setSelected(false);
                ll3_1.setSelected(true);
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this,Main3Activity.class);
//                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
//                finish();
            }
        });
        tv_ah = (TextView)findViewById(R.id.tv_ah);
        tv_at = (TextView)findViewById(R.id.tv_at);
        tv_ra = (TextView)findViewById(R.id.tv_ra);
        tv_sh = (TextView)findViewById(R.id.tv_sh);
        tv_st = (TextView)findViewById(R.id.tv_st);
//        tv_cond = (TextView)findViewById(R.id.tv_cond_txt);
//        tv_tmp = (TextView)findViewById(R.id.tv_tmp);
//        tv_pcpn = (TextView)findViewById(R.id.tv_pcpn);
        refresh = (ImageView)findViewById(R.id.iv_refresh1);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequestWithOkhttp();
                LoadingDialog.Builder builder1=new LoadingDialog.Builder(MainActivity.this)
                        .setMessage("加载中...")
                        .setCancelable(false);
                final LoadingDialog dialog1=builder1.create();
                dialog1.show();
                myhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
//                        myhandler.sendEmptyMessage(0x999);
                    }
                },1500);

            }
        });
        iv_display1 = (ImageView)findViewById(R.id.iv_display1);
        GlideApp
                .with(MainActivity.this)
                .load(imgurl1)
                .placeholder(R.drawable.loading_bg)
                .into(iv_display1);
        /**
         * 单击事件的监听
         */
//        iv_display1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
////                PackageManager packageManager = MainActivity.this.getPackageManager();
////                Intent intent3=new Intent();
////                try {
////                    intent3 =packageManager.getLaunchIntentForPackage("com.videogo");
////                    //intent =packageManager.getLaunchIntentForPackage("com.mcu.iVMS");
////                }catch (Exception e)
////                {
////                    Log.i(TAG, e.toString());
////                }
////                startActivity(intent3);
//            }
//
//        });
        iv_display1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onSingleClick(View v) {
                Log.d("iv_display1","这个也不执行么");
                //气泡初始化
                View bubbleView = inflater.inflate(R.layout.layout_popup_window,null);
                tipsWindow.setBubbleView(bubbleView);
                tipsWindow.show(v, Gravity.BOTTOM);
            }

            @Override
            public void onDoubleClick(View v) {
                if (TextUtils.isEmpty(appkey)){
                    Toast.makeText(MainActivity.this,"appkey can not be null",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(accesstoken)){
                    Toast.makeText(MainActivity.this,"accesstoken can not be null",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(playUrl)){
                    Toast.makeText(MainActivity.this,"url can not be null",Toast.LENGTH_LONG).show();
                    return;
                }
                saveDefaultParams();

                EZUIPlayer.EZUIKitPlayMode mode = null;
                mode = EZUIPlayer.getUrlPlayType(playUrl);
                if (mode == EZUIPlayer.EZUIKitPlayMode.EZUIKIT_PLAYMODE_LIVE){
                    //直播预览
                    if (isGlobal){
                        //启动播放页面
                        PlayActivity1.startPlayActivityGlobal(MainActivity.this, appkey, accesstoken, playUrl,mGlobalAreaDomain);
                        //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                        return;
                    }
                    //启动播放页面
                    PlayActivity1.startPlayActivity(MainActivity.this, appkey, accesstoken, playUrl);
                }else{
                    Toast.makeText(MainActivity.this,"播放模式未知，默认进入直播预览模式",Toast.LENGTH_LONG).show();
                    //直播预览
                    if (isGlobal){
                        //启动播放页面
                        PlayActivity1.startPlayActivityGlobal(MainActivity.this, appkey, accesstoken, playUrl,mGlobalAreaDomain);
                        //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                        return;
                    }
                    //启动播放页面
                    PlayActivity1.startPlayActivity(MainActivity.this, appkey, accesstoken, playUrl);
                }
            }
        }));
    }


    private void getRequestWithOkhttp() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(strurl1)
                            .build();
                    Request request1 = new Request.Builder()
                            .url(strurl3)
                            .build();
                    Response response = client.newCall(request).execute();
                    Response response1 = client.newCall(request1).execute();
                    String responseData = response.body().string();
                    String responseData1 = response1.body().string();
                    handleResponse(responseData,responseData1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleResponse(final String responseData,final String responseData1) {
        String[] s1;
        String[] s2;
        s1 = responseData.split("\\[");
        s2 = s1[1].split("\\]");

        //监测站
        jsonObject = JSON.parseObject(s2[0]);
        at = jsonObject.getString("AirTemprature_1");
        st = jsonObject.getString("SoilTemprature_1");
        ah = jsonObject.getString("AirHumidity_1");
        sh = jsonObject.getString("SoilHumidity_1");
        ra = jsonObject.getString("Radiation_1");
        System.out.println("空气温度"+at);

        //监测站图片
        String[] s3;
        String[] s4;
        s3 = responseData1.split("\\[");
        s4 = s3[1].split("\\]");

        jsonObject1 = JSON.parseObject(s4[0]);
        imgurl = jsonObject1.getString("Url");
        timeurl = jsonObject1.getString("Time");
        imgurl1 = imgurl.replace("amp;","");
        System.out.println("测试1"+imgurl+"..."+imgurl1);

        //和风天气
//        jsonObject1 = JSON.parseObject(responseData1);
//        JSONObject object1 = jsonObject1.getJSONArray("HeWeather6").getJSONObject(0);
//        JSONObject now = object1.getJSONObject("now");
//        tmp = now.getString("tmp");
//        cond_txt = now.getString("cond_txt");
//        pcpn = now.getString("pcpn");
//        System.out.println("温度"+tmp);


        Myrunnable myrunnable = new Myrunnable();
        myrunnable.start();
    }

    private class Myrunnable extends Thread{

        @Override
        public void run() {
            myhandler.sendEmptyMessage(0x666);
//            myhandler.sendEmptyMessage(0x888);
        }
    }

    private void saveDefaultParams() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PlayActivity1.APPKEY,appkey);
        editor.putString(PlayActivity1.AccessToekn,accesstoken);
        editor.putString(PlayActivity1.PLAY_URL,playUrl);
        if (!isGlobal){
            editor.putString(PlayActivity1.Global_AreanDomain,"");
        }else{
            editor.putString(PlayActivity1.Global_AreanDomain,mGlobalAreaDomain);
        }
        editor.commit();
    }

    private void getBroadcastWithOkhttp() {
        System.out.println("这句话都没执行么");
        final String sendMessage = "appKey=a9f5de51775d47eb8d0c489af966fe76&appSecret=a94fd52745de907228e0bd8c7f6ddb5f";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),sendMessage);
                    //发送请求
                    Request request = new Request.Builder()
                            .url(strurl)
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    handleResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleResponse(String responseData) {
        jsonObject = JSON.parseObject(responseData);
        JSONObject jsonObject3 = jsonObject.getJSONObject("data");
        data = jsonObject.getString("data");
        code = jsonObject.getString("code");
        accesstoken = jsonObject3.getString("accessToken");
        Log.d("MainActivity","data="+data);
        Log.d("MainActivity","code="+code);
        Log.d("MainActivity","token="+accesstoken);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            dialog();
            return true;
        }else {
            return super.onKeyDown(keyCode,event);
        }
    }

    private void dialog() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("大田信息监测终端").setMessage("确认退出应用程序？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exitAM.exit();
//                        System.exit(0);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
