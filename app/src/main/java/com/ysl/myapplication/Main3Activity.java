package com.ysl.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.tu.loadingdialog.LoadingDialog;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ysl.myapplication.Constant.struil_hefeng;
import static com.ysl.myapplication.Constant.strurl;

public class Main3Activity extends AppCompatActivity {
    private LinearLayout ll1,ll2,ll3;

    private TextView tv_cond,tv_tmp,tv_pcpn;
    String tmp,cond_txt,pcpn,date;
    private com.alibaba.fastjson.JSONObject jsonObject;
    private ImageView refresh;
    ActivityManager exitAM = ActivityManager.getInstance();

    private Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
//                case 0x666:
//                    tv_ah.setText(ah+"%");
//                    tv_at.setText(at+"℃");
//                    tv_sh.setText(sh+"%");
//                    tv_st.setText(st+"℃");
//                    tv_ra.setText("466W/㎡");
                case 0x888:
                    tv_cond.setText(cond_txt);
                    tv_tmp.setText(tmp+"℃");
                    tv_pcpn.setText(pcpn+"mm");
//                case 0x999:
//                    Toast.makeText(Main3Activity.this,"信息已更新",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        init();
        exitAM.addActivity(this);
    }

    private void init() {
        getRequestWithOkhttp();
        ll1 = (LinearLayout)findViewById(R.id.LL1);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setSelected(true);
                ll2.setSelected(false);
                ll3.setSelected(false);
                Intent intent1 = new Intent();
                intent1.setClass(Main3Activity.this,MainActivity.class);
                startActivity(intent1);
//                finish();
            }
        });
        ll2 = (LinearLayout)findViewById(R.id.LL2);
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setSelected(false);
                ll2.setSelected(true);
                ll3.setSelected(false);
                Intent intent2 = new Intent();
                intent2.setClass(Main3Activity.this,Main2Activity.class);
                startActivity(intent2);
//                finish();
            }
        });
        ll3 = (LinearLayout)findViewById(R.id.LL3);
        ll3.setSelected(true);
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setSelected(false);
                ll2.setSelected(false);
                ll3.setSelected(true);

            }
        });
        refresh = (ImageView)findViewById(R.id.iv_refresh3);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequestWithOkhttp();
                LoadingDialog.Builder builder1=new LoadingDialog.Builder(Main3Activity.this)
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
        tv_cond = (TextView)findViewById(R.id.tv_cond_txt);
        tv_tmp = (TextView)findViewById(R.id.tv_tmp);
        tv_pcpn = (TextView)findViewById(R.id.tv_pcpn);
    }

    private void getRequestWithOkhttp() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url(strurl)
//                            .build();
                    Request request = new Request.Builder()
                            .url(struil_hefeng)
                            .build();
                    Response response = client.newCall(request).execute();
//                    Response response1 = client.newCall(request1).execute();
                    String responseData = response.body().string();
//                    String responseData1 = response1.body().string();
                    handleResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleResponse(final String responseData) {

        jsonObject = JSON.parseObject(responseData);
        JSONObject object1 = jsonObject.getJSONArray("HeWeather6").getJSONObject(0);
        JSONArray forecast = object1.getJSONArray("daily_forecast");
        JSONObject tomorrow = forecast.getJSONObject(1);
        tmp = tomorrow.getString("tmp_max");
        cond_txt = tomorrow.getString("cond_txt_d");
        pcpn = tomorrow.getString("pcpn");
        date = tomorrow.getString("date");
        System.out.println("预报的时间"+date);

        Myrunnable myrunnable = new Myrunnable();
        myrunnable.start();
    }

    private class Myrunnable extends Thread{

        @Override
        public void run() {
            myhandler.sendEmptyMessage(0x888);
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
