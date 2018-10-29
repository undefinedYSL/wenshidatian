package com.ysl.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

	private EditText etUserName;
	private EditText etPassword;
	private String UserName;
	private String Password;
	private SharedPreferences sp;
	private CheckBox checkBox;
	private ImageView nameClear;
	private ImageView passwordClear;

	//http通信,实验室沿用
	private String service_LoginMessage;

	public String getService_LoginMessage(){
		return service_LoginMessage;
	}

	public void setService_LoginMessage( String service_LoginMessage){
		this.service_LoginMessage = service_LoginMessage;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		init();

		sp = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		if (sp.getBoolean("ISCHECKED",true)){
			checkBox.setChecked(true);
			etUserName.setText(sp.getString("USERNAME",""));
			etPassword.setText(sp.getString("PASSWORD",""));

		}
		checkBox.setOnCheckedChangeListener(new MyListerner1());
	}

	private void init() {


		etUserName = (EditText)findViewById(R.id.et_username);
		etPassword = (EditText)findViewById(R.id.et_password);
		nameClear = (ImageView)findViewById( R.id.iv_usernameclear);
		passwordClear = (ImageView)findViewById(R.id.iv_passwordclear);
//        btnLogin = (Button)findViewById(R.id.btn_login);
		checkBox = (CheckBox)findViewById(R.id.cb_checkbox);

		EditTextClearTools.addClearListener(etUserName,nameClear);
		EditTextClearTools.addClearListener(etPassword,passwordClear);
	}

	public void click(View view) {
		SharedPreferences.Editor editor = sp.edit();

		try {
			UserName = etUserName.getText().toString().trim();
			Password = etPassword.getText().toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (checkBox.isChecked()) {
			editor.putString("USERNAME", UserName);
			editor.putString("PASSWORD", Password);
			editor.commit();
		}
		final String sendMessage = "login" + "@" + UserName + "@" + Password;
		if (TextUtils.isEmpty(UserName) || TextUtils.isEmpty(Password)) {
			Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
		}else {
			new Thread() {
				public void run() {
					final String result = send_ToService.send_Message(sendMessage);
					if (result != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setService_LoginMessage(result);
								checklogin();
								LoginActivity.this.finish();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.putExtra("str", "NO");
								intent.setClass(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								LoginActivity.this.finish();

							}
						});
					}
				}
			}.start();
		}
		//离线登录
//            else if (UserName.equals("admin")&&Password.equals("admin")){
//            Toast.makeText(Main2Activity.this,"登陆成功",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setClass(Main2Activity.this,Main3Activity.class);
//            startActivity(intent);
//            }
	}

	private void checklogin() {
		String str = getService_LoginMessage();
		String[] strs = str.split("@");
		String enter = strs[1];
		if ("OK".equals(enter)){
			Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("str","OK");
			intent.setClass(LoginActivity.this,MainActivity.class);
			startActivity(intent);
		}else {
			Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
		}
	}

	private class MyListerner1 implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (checkBox.isChecked()){
				sp.edit().putBoolean("ISCHECKED",true).commit();
			}else {
				sp.edit().putBoolean("ISCHECKED",false).commit();
			}
		}
	}
}
