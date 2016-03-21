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
import com.game.sdk.ui.LoginActivity.onRegisterBack;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.MResource;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OneRegisterUser extends BaseView {
	public static Activity acontext;
	private static OnLoginListener loginListener;// 登录回调监听
	private boolean isoneregister;
	private EditText et_username, et_pwd;
	private String uname = "";
	private SharedPreferences sp;
	private UserInfo userinfo;
	public static boolean isSelfRegister = false; // 用来标记是否是自定义注册 调整到登录页面的
	public static String username, pwd;
	private Button btn_game_in;
	private RelativeLayout rl_oneregister_back_login;
	/**
	 * 构造方法 用来示例view对象
	 * 
	 * @param ctx
	 */
	public OneRegisterUser(Activity ctx,OnLoginListener logininlist,boolean isoneregister) {
		this.isoneregister=isoneregister;
		acontext = ctx;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		loginListener = logininlist;
		if (isoneregister) {
			content_view = inflater.inflate(MResource.getIdByName(ctx,
					Constants.Resouce.LAYOUT, "sdk_oneregister"), null);
		}
		else{
			content_view = inflater.inflate(MResource.getIdByName(ctx,
					Constants.Resouce.LAYOUT, "sdk_user_register"), null);
		}
		initData();
		rl_oneregister_back_login=(RelativeLayout)content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "rl_oneregister_back_login"));
		btn_game_in = (Button) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "btn_game_in"));
		et_username = (EditText) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "et_username"));
		et_pwd = (EditText) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "et_pwd"));
		btn_game_in.setOnClickListener(this);
		et_username.setText(uname);
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
		rl_oneregister_back_login.setOnClickListener(onclick);
		
	}

	/**
	 * 一键注册
	 */
	public void OneKeyRegister(onRegisterBack onregister) {
		if (null == userinfo) {
			initData();
		}
		et_username.setCursorVisible(false);      //设置输入框中的光标不可见  
		et_username.setFocusable(false);           //无焦点  
		et_username.setFocusableInTouchMode(false);  
		new UserOneKeyRegisterTask(onregister).execute();
	}
	/**
	 * 一键注册
	 */
	private class UserOneKeyRegisterTask extends
			AsyncTask<Void, Void, ResultCode> {
		public onRegisterBack onRegisterBack;

		public UserOneKeyRegisterTask(onRegisterBack onRegisterBack) {
			this.onRegisterBack = onRegisterBack;
		}

		@Override
		protected ResultCode doInBackground(Void... params) {
			JSONObject json = userinfo.deviceInfoToJson();
			ResultCode result = GetDataImpl.getInstance(acontext)
					.UserOneKeyRegister(json.toString());
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
			onRegisterBack.toRegister();
			if (null != result) {
				if (result.code == 1) {
					
					et_username.setText(result.username);
					userinfo.username = result.username;
					uname = result.username;
					
					
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == MResource.getIdByName(acontext, Constants.Resouce.ID,
				"btn_game_in")) {
			String username = et_username.getText().toString().trim();
			String password = et_pwd.getText().toString().trim();
			
			Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
			Pattern p=Pattern.compile(".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]");
    		Matcher pipeMatcher = p.matcher(username);
    		 if (!pipeMatcher.matches())
             {
                 
            	  Toast.makeText(acontext, "用户必须包含英文跟数字",
    						Toast.LENGTH_SHORT).show();
            	  
            	  return;
              }

			if (TextUtils.isEmpty(username)) {
				Toast.makeText(acontext, "请输入账号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(acontext, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (username.length() < 6 || username.length() > 16
					|| pat.matcher(username).find()) {
				Toast.makeText(acontext, "账号只能由6至16位英文或数字组成",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (password.length() < 6 || password.length() > 16
					|| pat.matcher(password).find()) {
				Toast.makeText(acontext, "密码只能由6至16位16位英文或数字组成",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (null == userinfo) {
				initData();
			}
			userinfo.username = username;
			userinfo.password = password;
			DialogUtil.showDialog(acontext, "正在注册帐号...");
			new RegisterAsyTask().execute();
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
			ResultCode result = GetDataImpl.getInstance(acontext).register(
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
				}
				//Toast.makeText(acontext, result.msg, Toast.LENGTH_SHORT).show();
			} else {
				String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
				Toast.makeText(acontext, "服务器内部错误，请您联系客服", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return content_view;
	}

}
