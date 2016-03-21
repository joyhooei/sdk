package com.game.sdk.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.game.sdk.YTAppService;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TelLoginView extends BaseView {
	
	public static Activity acontext;
	private static OnLoginListener loginListener;// 登录回调监听
	private UserInfo userinfo;
	private EditText et_username,et_sendcode;
	private LinearLayout ll_usermsg_back;
	private Button btn_sendcode,btn_login;
	private SharedPreferences sp;
	TimeCount time;
	public static String username, pwd;
	public static boolean isSelfRegister = false; // 用来标记是否是自定义注册 调整到登录页面的
	/**
	 * 构造方法 用来示例view对象
	 * 
	 * @param ctx
	 */
	public TelLoginView(Activity ctx,OnLoginListener logininlist) {
		acontext = ctx;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		loginListener = logininlist;
		time = new TimeCount(60000, 1000);//构造CountDownTimer对象
		initData();
		content_view = inflater.inflate(MResource.getIdByName(ctx,
					Constants.Resouce.LAYOUT, "sdk_tel_login"), null);
		et_username = (EditText) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "et_username"));
		et_sendcode = (EditText) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "et_sendcode"));
		ll_usermsg_back = (LinearLayout) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "ll_usermsg_back"));
		btn_sendcode = (Button) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "btn_sendcode"));
		btn_login =(Button) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "btn_login"));
		
		btn_login.setOnClickListener(this);
		btn_sendcode.setOnClickListener(this);
	
		sp = acontext.getSharedPreferences(Constants.CONFIG,
				Context.MODE_PRIVATE);
		
	}
	/**
	 * 初始化相关数据
	 */
	private void initData() {
		userinfo = new UserInfo();
		TelephonyManager tm = (TelephonyManager) acontext
				.getSystemService(Context.TELEPHONY_SERVICE);
		userinfo.imeil = tm.getDeviceId();
		userinfo.deviceinfo = tm.getLine1Number() + "||android"
				+ Build.VERSION.RELEASE;

		userinfo.agent = YTAppService.agentid;
	}
	
	public void setOnClick(OnClickListener onclick) {
		
		ll_usermsg_back.setOnClickListener(onclick);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == MResource.getIdByName(acontext, Constants.Resouce.ID,
				"btn_login")) {
			String username = et_username.getText().toString().trim();
			
			String sendcode = et_sendcode.getText().toString().trim();
			//Pattern pat = Pattern.compile("[\u4e00-\u9fa5]"); 
		
			Pattern p=Pattern.compile("[1][3458]\\d{9}");
    		Matcher pipeMatcher = p.matcher(username);
    		 if (!pipeMatcher.matches())
             {
                 
            	  Toast.makeText(acontext, "手机号码输入不正确，请重新输入！",
    						Toast.LENGTH_SHORT).show();
            	  
            	  return;
              }
			if (TextUtils.isEmpty(username)) {
				Toast.makeText(acontext, "请输入手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(sendcode)) {
				Toast.makeText(acontext, "请输入验证码", Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			

			if (null == userinfo) {
				initData();
			}
			
			userinfo.issend = 0;
			userinfo.sendcode=sendcode;
			userinfo.username = username;
			//userinfo.password = password;
			DialogUtil.showDialog(acontext, "正在登陆...");
			new RegisterAsyTask().execute();
		}
		if (v.getId() == MResource.getIdByName(acontext, Constants.Resouce.ID,
				"btn_sendcode")) {
			
			String usertelephone = et_username.getText().toString().trim();
			userinfo.username = usertelephone;
			userinfo.issend = 1;
			new AsyncTask<Void, Void, ResultCode>(){

				@Override
				protected ResultCode doInBackground(Void... params) {
					// TODO Auto-generated method stub
				
					JSONObject json = userinfo.buildJson();
					ResultCode result = GetDataImpl.getInstance(acontext).loginSendCode(
							json.toString());
					return result;
				}
				protected void onPostExecute(ResultCode result) {
					if (result.code ==1) {
						time.start();
					}
					Logger.msg("msg===="+result.msg);
					String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
					Toast.makeText(acontext, msg, Toast.LENGTH_SHORT)
							.show();
					
				};
				
			}.execute();
		}

	}

	/**
	 * ִ用户注册
	 * 
	 * @author Administrator
	 */
	private class RegisterAsyTask extends AsyncTask<Void, Void, ResultCode> {
		@Override
		protected ResultCode doInBackground(Void... params) {
			JSONObject json = userinfo.buildJson();
			ResultCode result = GetDataImpl.getInstance(acontext).loginSendCode(
					json.toString());
			return result;
		}

		@Override
		protected void onPostExecute(ResultCode result) {
			super.onPostExecute(result);
			// pd_register.dismiss();
			try {
				DialogUtil.dismissDialog();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null != result) {
				if (result.code == 1) {
					// 注册成功
					isSelfRegister = true;
					username = result.username;
					pwd = result.password;
					YTAppService.ptbkey=result.ptbkey;
					
					LogincallBack loginCallback = new LogincallBack();
					if (!UserLoginInfodao.getInstance(acontext)
							.findUserLoginInfoByName(result.username)) {
							UserLoginInfodao.getInstance(acontext).saveUserLoginInfo(
									result.username, result.password);
					} else {
						UserLoginInfodao.getInstance(acontext)
									.deleteUserLoginByName(result.username);
						UserLoginInfodao.getInstance(acontext).saveUserLoginInfo(
									result.username, result.password);
					}
					Editor editor = sp.edit();
					editor.putBoolean(Constants.ISFIRST_INSTALL, false);
					editor.commit();
					
					YTAppService.isLogin = true;
					YTAppService.userinfo = userinfo;

					loginCallback.logintime = result.logintime;
					loginCallback.sign = result.sign;
					loginCallback.username = result.username;
					//Logger.msg("logintime:"+result.logintime+"--sign:"+result.sign+"--username:"+username);
					loginListener.loginSuccess(loginCallback);// 回调登录成功
					
					acontext.finish();
					return;
				}else{
					String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
					Toast.makeText(acontext, msg, Toast.LENGTH_SHORT)
							.show();
				}
				//Toast.makeText(acontext, result.msg, Toast.LENGTH_SHORT).show();
			} else {
				
				String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
				Toast.makeText(acontext, msg, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public View getContentView() {
		return content_view;
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		
		}	
		@Override
		public void onFinish() {//计时完毕时触发
			btn_sendcode.setText("重新发送");
			btn_sendcode.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			btn_sendcode.setClickable(false);
			btn_sendcode.setText(millisUntilFinished /500+"秒");
		}
	}
}
