package com.ysl.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
        iv_display1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = MainActivity.this.getPackageManager();
                Intent intent3=new Intent();
                try {
                    intent3 =packageManager.getLaunchIntentForPackage("com.videogo");
                    //intent =packageManager.getLaunchIntentForPackage("com.mcu.iVMS");
                }catch (Exception e)
                {
                    Log.i(TAG, e.toString());
                }
                startActivity(intent3);
            }

        });
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
