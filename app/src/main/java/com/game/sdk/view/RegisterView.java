package com.game.sdk.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sdk.YTAppService;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.ui.LoginActivity.onRegisterBack;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ThreadPoolManager;
import com.game.sdk.view.LoginView.UserLoginAsyTask;

/**
 * author janecer 2014年7月21日下午5:03:45
 */
public class RegisterView extends BaseView {
	public static Activity acontext;
	private UserInfo userinfo;
	private String uname = "";
	private SharedPreferences sp;
	private EditText et_username, et_pwd,et_code;
	private Button btn_game_in ,btn_send_code; // 注册，发送验证码
	
	TimeCount time;
	public static boolean isSelfRegister = false; // 用来标记是否是自定义注册 调整到登录页面的
	public static String username, pwd;
	private static OnLoginListener loginListener;// 登录回调监听
	private LinearLayout ll_back_user_login ,ll_user_oneregister_goin,ll_user_register_goin;
	/**
	 * 构造方法 用来示例view对象
	 * 
	 * @param ctx
	 */
	public RegisterView(Activity ctx,OnLoginListener logininlist) {
		time = new TimeCount(60000, 1000);//构造CountDownTimer对象
		acontext = ctx;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		loginListener = logininlist;
		
		content_view = inflater.inflate(MResource.getIdByName(ctx,
					Constants.Resouce.LAYOUT, "sdk_tel_register"), null);
	
		initData();
		
		et_code = (EditText) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "et_code"));
		et_username = (EditText) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "et_username"));
		et_pwd = (EditText) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "et_pwd"));
		
	
		//用户协议
		//tv_user_aggrement = (TextView) content_view.findViewById(MResource
		//		.getIdByName(ctx, Constants.Resouce.ID, "tv_user_aggrement"));
		btn_game_in = (Button) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "btn_game_in"));
		
		btn_send_code =(Button)content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "btn_send_code"));
		ll_back_user_login=(LinearLayout)content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "ll_back_user_login"));
		ll_user_oneregister_goin =(LinearLayout)content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "ll_user_oneregister_goin"));
		ll_user_register_goin =(LinearLayout)content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "ll_user_register_goin"));
		
		btn_game_in.setOnClickListener(this);
		btn_send_code.setOnClickListener(this);
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
		ll_back_user_login.setOnClickListener(onclick);
		ll_user_oneregister_goin.setOnClickListener(onclick);
		ll_user_register_goin.setOnClickListener(onclick);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(acontext, Constants.Resouce.ID,
				"btn_game_in")) {
			String username = et_username.getText().toString().trim();
			String password = et_pwd.getText().toString().trim();
			String sendcode = et_code.getText().toString().trim();
			Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
			Pattern p=Pattern.compile("[1][3458]\\d{9}");
    		Matcher pipeMatcher = p.matcher(username);
    		 if (!pipeMatcher.matches())
             {
                 
            	  Toast.makeText(acontext, "手机号码输入不正确，请重新输入！",
    						Toast.LENGTH_SHORT).show();
            	  
            	  return;
              }
			if (TextUtils.isEmpty(sendcode)) {
				Toast.makeText(acontext, "请输入验证号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(username)) {
				Toast.makeText(acontext, "请输入手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(acontext, "请输入密码", Toast.LENGTH_SHORT).show();
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
			
			userinfo.issend = 0;
			userinfo.sendcode=sendcode;
			userinfo.username = username;
			userinfo.password = password;
			DialogUtil.showDialog(acontext, "正在注册帐号...");
			new RegisterAsyTask().execute();
		}
		if (v.getId() == MResource.getIdByName(acontext, Constants.Resouce.ID,
				"btn_send_code")) {
			
			String usertelephone = et_username.getText().toString().trim();
			userinfo.username = usertelephone;
			userinfo.issend = 1;
			new AsyncTask<Void, Void, ResultCode>(){

				@Override
				protected ResultCode doInBackground(Void... params) {
					// TODO Auto-generated method stub
				
					JSONObject json = userinfo.buildJson();
					ResultCode result = GetDataImpl.getInstance(acontext).regSendCode(
							json.toString());
					return result;
				}
				protected void onPostExecute(ResultCode result) {
					if (result.code ==1) {
						time.start();
					}
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
			ResultCode result = GetDataImpl.getInstance(acontext).regSendCode(
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
				else{
					
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
			btn_send_code.setText("重新发送");
			btn_send_code.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			btn_send_code.setClickable(false);
			btn_send_code.setText(millisUntilFinished /500+"秒");
		}
	}
	/**
	 * 登录
	 */
//	public void login(String username, String password, final boolean flag) {
//		// if (null == userInfo) {
//		// initData();
//		// }
//		userinfo.username = username;
//		userinfo.password = password;
//
//		if (!flag) {
//			DialogUtil.showDialog(acontext, "正在登录...");
//			new UserLoginAsyTask(flag).execute();
//		} else {
//			new AsyncTask<Void, Void, Void>() {
//				@Override
//				protected Void doInBackground(Void... params) {
//					try {
//						Thread.sleep(2500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					return null;
//				}
//
//				@Override
//				protected void onPostExecute(Void result) {
//					if (rl_login.getVisibility() == View.GONE) {
//						// tv_tip.setText("正在登录...");
//						new UserLoginAsyTask(flag).execute();// 睡眠2秒执行登录
//					}
//					super.onPostExecute(result);
//				}
//			}.execute();
//
//		}
//
//	}
//
//	/**
//	 * 用户登录
//	 * 
//	 * @author Administrator
//	 */
//	public class UserLoginAsyTask extends AsyncTask<Void, Void, ResultCode> {
//		private boolean flag;
//
//		public UserLoginAsyTask(boolean mark) {
//			flag = mark;
//		}
//
//		@Override
//		protected ResultCode doInBackground(Void... params) {
//			JSONObject json = userinfo.buildJson();
//			ResultCode result = GetDataImpl.getInstance(acontext).login(
//					json.toString());
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(final ResultCode result) {
//			if ((!flag && !DialogUtil.isShowing())) {
//				if (null != result && result.code == 1) {
//					ThreadPoolManager.getInstance().addTask(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								userinfo.username = result.username;
//								TTWAppService.userinfo = userinfo;
//								JSONObject json = userinfo.outInfoToJson();
//								GetDataImpl.getInstance(acontext).loginOut(
//										json.toString());
//								Logger.msg("用户取消登录...");
//							} catch (NullPointerException e) {
//								e.printStackTrace();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//				return;
//			}
//			super.onPostExecute(result);
//			try {
//				DialogUtil.dismissDialog();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			LogincallBack loginCallback = new LogincallBack();
//			if (null != result && result.code == 1) {
//				if (!UserLoginInfodao.getInstance(acontext)
//						.findUserLoginInfoByName(result.username)) {
//					UserLoginInfodao.getInstance(acontext).saveUserLoginInfo(
//							result.username, result.password);
//				} else {
//					UserLoginInfodao.getInstance(acontext)
//							.deleteUserLoginByName(result.username);
//					UserLoginInfodao.getInstance(acontext).saveUserLoginInfo(
//							result.username, result.password);
//				}
//				TTWAppService.userinfo = userinfo;
//				TTWAppService.isLogin = true;
//
//				loginCallback.logintime = result.logintime;
//				loginCallback.sign = result.sign;
//				loginCallback.username = result.username;
//
//				loginListener.loginSuccess(loginCallback);// 回调登录成功
//				// Toast.makeText(acontext,"登录成功",Toast.LENGTH_SHORT).show();
//
//				/**
//				 * 登录成功获取
//				 */
//				Intent app_intent = new Intent(acontext, TTWAppService.class);
//				app_intent.putExtra("login_success", "login_success");
//				acontext.startService(app_intent);
//
//				acontext.finish();// 销毁登录页面
//			} else {
//				//showlogin();
//				int code = result != null ? result.code : 0;
//				String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
//				LoginErrorMsg errorMsg = new LoginErrorMsg(code, msg);
//				loginListener.loginError(errorMsg);// 登录失败回调
//				//Toast.makeText(acontext, msg, Toast.LENGTH_SHORT).show();
//			}
//
//		}
//	}
}
