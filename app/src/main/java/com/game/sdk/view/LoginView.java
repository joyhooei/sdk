package com.game.sdk.view;

import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.game.sdk.YTAppService;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.floatwindow.FloatWebActivity;

import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.NetworkImpl;
import com.game.sdk.util.ThreadPoolManager;

/**
 * author janecer 2014年7月21日下午2:41:07
 */
public class LoginView extends BaseView {

	private RelativeLayout ll_quick_login;// 显示快捷登录布局
	private TextView tv_cut_login;
	private TextView tv_quick_username;
	
	//private TableLayout tl_btn;
	// private TableLayout tl_btn_savelogin;
	//private Button btn_rpwd;
	
	private PopupWindow pw_select_user;
	private PwAdapter pw_adapter;
	private List<UserInfo> userLoginInfos;
	
	private UserInfo userinfo_select;// 用户选择用户的时候 赋予的用户信息

	private RelativeLayout rl_login;// 显示登录界面
	private EditText et_username, et_pwd;// 输入用户名和密码
	private ImageView iv_userselect,iv_loadingtu,img_show_pwd;// 用户名输入框里面 选择登录的用户信息
	
	private Button btn_login,btn_forget;
	private TextView tv_telregister,tv_tellogin;// 自定义注册，一键注册
	private LinearLayout ll_tellogin_back,ll_telregister_back;
	
	private boolean is_cut_login = false;
	private boolean isPortrait;// 是否是竖版
	// private boolean isBack=false;//用来判别是否在登录的时候 用户按下了返回键
	private boolean isShowQuikLogin;// false表示直接显示登录界面，true表示如果有帐号 就直接快捷登录

	private static OnLoginListener loginListener;// 登录回调监听
	public static Activity acontext;
	private SharedPreferences sp;
	private UserInfo userInfo;
	private boolean showpwd=false;
	private Dialog notice_dialog;//公告对话框
	public LoginView(Activity ctx, OnLoginListener logininlist) {
		acontext = ctx;
		Intent intent = ctx.getIntent();
		isShowQuikLogin = intent.getBooleanExtra("isShowQuikLogin", true);
		
		loginListener = logininlist;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content_view = inflater.inflate(MResource.getIdByName(ctx,
				Constants.Resouce.LAYOUT, "ttw_login"), null);

		isPortrait = (acontext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);

		initUI();
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		// 自动登录控件
		ll_quick_login = (RelativeLayout) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "ll_quick_login"));
		ll_quick_login.setVisibility(View.GONE);
		tv_cut_login = (TextView) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "tv_cut_login"));
		tv_quick_username = (TextView) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID,
						"tv_quick_username"));
		
		img_show_pwd = (ImageView) content_view.findViewById(MResource.getIdByName(
				acontext, "id", "img_show_pwd"));
		iv_loadingtu = (ImageView) content_view.findViewById(MResource.getIdByName(
				acontext, "id", "iv_loadingtu"));
		ll_tellogin_back=(LinearLayout)content_view.findViewById(MResource.getIdByName(
				acontext, Constants.Resouce.ID, "ll_tellogin_back"));
		ll_telregister_back=(LinearLayout)content_view.findViewById(MResource.getIdByName(
				acontext, Constants.Resouce.ID, "ll_telregister_back"));
		
		// 手动登录控件
		rl_login = (RelativeLayout) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "rl_login"));
		
		et_username = (EditText) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "et_username"));
		et_pwd = (EditText) content_view.findViewById(MResource.getIdByName(
				acontext, Constants.Resouce.ID, "et_pwd"));
		iv_userselect = (ImageView) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "iv_userselect"));
		btn_login = (Button) content_view.findViewById(MResource.getIdByName(
				acontext, Constants.Resouce.ID, "btn_login"));
		tv_telregister = (TextView) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "tv_telregister"));
		tv_tellogin = (TextView) content_view.findViewById(MResource
				.getIdByName(acontext, Constants.Resouce.ID, "tv_tellogin"));
		btn_forget = (Button) content_view.findViewById(MResource.getIdByName(
				acontext, Constants.Resouce.ID, "btn_forget"));
		img_show_pwd.setOnClickListener(this);
		btn_forget.setOnClickListener(this);
		iv_userselect.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		tv_cut_login.setOnClickListener(this);
		
		if (null != YTAppService.proclation) {
			 notice_dialog = new Dialog(acontext, MResource.getIdByName(acontext,
						Constants.Resouce.STYLE, "customDialog"));
				 View gift_view=inflater.inflate( MResource.getIdByName(acontext,
							Constants.Resouce.LAYOUT, "sdk_notice"),null);
				// LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				 
				 ImageView img_del =(ImageView)gift_view.findViewById(MResource.getIdByName(
						 acontext, Constants.Resouce.ID, "img_del"));
				 TextView tv_notic_title =(TextView)gift_view.findViewById(MResource.getIdByName(
						 acontext, Constants.Resouce.ID, "tv_notic_title"));
				 TextView tv_notice_content =(TextView)gift_view.findViewById(MResource.getIdByName(
						 acontext, Constants.Resouce.ID, "tv_notice_content"));
				 Button btn_goin =(Button)gift_view.findViewById(MResource.getIdByName(
						 acontext, Constants.Resouce.ID, "btn_goin"));
				 tv_notic_title.setText(YTAppService.proclation.title);
				 tv_notice_content.setText(YTAppService.proclation.content);
				 img_del.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						notice_dialog.dismiss();
						isLoadUserLogin();
					}
				});
				 btn_goin.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						notice_dialog.dismiss();
						isLoadUserLogin();
					}
				});
				 notice_dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialogbu消失
				 notice_dialog.setContentView(gift_view);
				 notice_dialog.show();
		}
		else{
			
			isLoadUserLogin();
		}
	}

	/**
	 * 初始化相关信息
	 */
	private void initData() {
		
		userInfo = new UserInfo();
		TelephonyManager tm = (TelephonyManager) acontext
				.getSystemService(Context.TELEPHONY_SERVICE);
		userInfo.imeil = tm.getDeviceId();
		userInfo.deviceinfo = tm.getLine1Number() + "||android"
				+ Build.VERSION.RELEASE;

		userInfo.agent = YTAppService.agentid;

		String username = et_username.getText().toString().trim();
		String pwd = et_pwd.getText().toString().trim();
		userInfo.username = TextUtils.isEmpty(username) ? null : username;
		userInfo.password = TextUtils.isEmpty(pwd) ? null : pwd;
	}

	@Override
	public View getContentView() {
		return content_view;
	}

	public void setRegisterOnClick(OnClickListener onclick) {
		ll_telregister_back.setOnClickListener(onclick);
		ll_tellogin_back.setOnClickListener(onclick);
		tv_telregister.setOnClickListener(onclick);
		tv_tellogin.setOnClickListener(onclick);
	}

	@Override
	public void onClick(View v) {
		if (!NetworkImpl.isNetWorkConneted(acontext)) {
			Toast.makeText(acontext, "网络连接错误，请检查当前网络状态！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (null != img_show_pwd && v.getId() == img_show_pwd.getId()) {
			if (showpwd) {
				et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);    
				showpwd =false;
			}else{
				et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				showpwd=true;
			}
			
			return;
		}
		if (null != btn_login && v.getId() == btn_login.getId()) {
			String username = et_username.getText().toString().trim();
			String password = et_pwd.getText().toString().trim();
			
			if (TextUtils.isEmpty(username)) {
				Toast.makeText(acontext, "请输入账号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(acontext, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");

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
			if (!NetworkImpl.isNetWorkConneted(acontext)) {
				Toast.makeText(acontext, "网络连接错误，请检查当前网络状态！",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (null == userInfo) {
				initData();
			}
			is_cut_login = false;
			login(username, password, false);
			return;
		}
		if (null != tv_cut_login && v.getId() == tv_cut_login.getId()) {
			is_cut_login = true;
			showlogin();
			return;
		}
		if (null != iv_userselect && v.getId() == iv_userselect.getId()) {
			userselect(v);
			return;
		}
		if (null != btn_forget && v.getId() == btn_forget.getId()) {
			web("忘记密码", Constants.URL_Forgetpwd);
			return;
		}
		

	}

	/**
	 * 登录
	 */
	public void login(String username, String password, final boolean flag) {
		// if (null == userInfo) {
		// initData();
		// }
		userInfo.username = username;
		userInfo.password = password;

		if (!flag) {
			DialogUtil.showDialog(acontext, "正在登录...");
			new UserLoginAsyTask(flag).execute();
		} else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (rl_login.getVisibility() == View.GONE) {
						// tv_tip.setText("正在登录...");
						new UserLoginAsyTask(flag).execute();// 睡眠2秒执行登录
					}
					super.onPostExecute(result);
				}
			}.execute();

		}

	}

	/**
	 * 用户登录
	 * 
	 * @author Administrator
	 */
	public class UserLoginAsyTask extends AsyncTask<Void, Void, ResultCode> {
		private boolean flag;

		public UserLoginAsyTask(boolean mark) {
			flag = mark;
		}

		@Override
		protected ResultCode doInBackground(Void... params) {
			JSONObject json = userInfo.buildJson();
			ResultCode result = GetDataImpl.getInstance(acontext).login(
					json.toString());
			return result;
		}

		@Override
		protected void onPostExecute(final ResultCode result) {
			if ((!flag && !DialogUtil.isShowing()) || is_cut_login) {
				if (null != result && result.code == 1) {
					ThreadPoolManager.getInstance().addTask(new Runnable() {
						@Override
						public void run() {
							try {
								userInfo.username = result.username;
								YTAppService.userinfo = userInfo;
								JSONObject json = userInfo.outInfoToJson();
								GetDataImpl.getInstance(acontext).loginOut(
										json.toString());
								Logger.msg("用户取消登录...");
							} catch (NullPointerException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
				return;
			}
			super.onPostExecute(result);
			try {
				DialogUtil.dismissDialog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LogincallBack loginCallback = new LogincallBack();
			if (null != result && result.code == 1) {
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
				YTAppService.userinfo = userInfo;
				YTAppService.isLogin = true;
				YTAppService.ptbkey=result.ptbkey;

				loginCallback.logintime = result.logintime;
				loginCallback.sign = result.sign;
				loginCallback.username = result.username;

				loginListener.loginSuccess(loginCallback);// 回调登录成功
				// Toast.makeText(acontext,"登录成功",Toast.LENGTH_SHORT).show();

				/**
				 * 登录成功获取
				 */
				Intent app_intent = new Intent(acontext, YTAppService.class);
				app_intent.putExtra("login_success", "login_success");
				acontext.startService(app_intent);

				acontext.finish();// 销毁登录页面
			} else {
				showlogin();
				int code = result != null ? result.code : 0;
				String msg = result != null ? result.msg : "服务器内部错误，请您联系客服";
				Logger.msg("kadaj=="+msg);
				LoginErrorMsg errorMsg = new LoginErrorMsg(code, msg);
				loginListener.loginError(errorMsg);// 登录失败回调
				
				Toast.makeText(acontext, msg, Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * 检查是否需要加载 用户以前登录的信息
	 */
	private void isLoadUserLogin() {
		
		sp = acontext.getSharedPreferences(Constants.CONFIG,
				Context.MODE_PRIVATE);
		if (sp.getBoolean(Constants.ISFIRST_INSTALL, true)) {
			
			// 用来判别 用户是否是第一次安装sdk 如果是第一次安装sdk 启动登录界面后 将根据手机
			DialogUtil.showDialog(acontext, "正在查询您是否有帐号！");
			new AsyncTask<Void, Void, ResultCode>() {
				@Override
				protected ResultCode doInBackground(Void... params) {
					ResultCode rc = new ResultCode();
					try {
						String str = GetDataImpl.getInstance(acontext)
								.searchLoginUserinfoByImel(YTAppService.appid,
										YTAppService.dm.imeil);
						if(str == null || "".equals(str)){
							rc.code = 1 ;
							rc.msg = "";
						}else{
							JSONObject jo = new JSONObject(str);
							//Logger.msg(str + "--" + jo.getString("msg").toString());
							JSONArray jos = jo.getJSONArray("data");
							//Logger.msg("login count users：" + jos.length());
							for (int i = 0; i < jos.length(); i++) {
								JSONObject jo_item = jos.getJSONObject(i);
								//Logger.msg("username___:" + jo_item.getString("a"));
								UserLoginInfodao.getInstance(acontext)
										.saveUserLoginInfo(jo_item.getString("a"),
												jo_item.getString("b"));
							}
							rc.code = jo.isNull("code") ? 0 : jo.getInt("code");
							rc.msg = jo.isNull("msg") ? "" : jo.getString("msg");
						}
						return rc;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (NullPointerException e) {
						Log.i("login", "Null---"+e.getMessage());
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(ResultCode result) {
					try {
						DialogUtil.dismissDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
					super.onPostExecute(result);
					if (null != result && result.code == 1) {
						Editor editor = sp.edit();
						editor.putBoolean(Constants.ISFIRST_INSTALL, false);
						editor.commit();

						getSqliteUser();
					} else {
						Toast.makeText(acontext, result.msg,
								Toast.LENGTH_SHORT).show();
					}
					
				}
			}.execute();
		} else {

			getSqliteUser();
		}
	}

	/**
	 * 显示自动登录对话框及自动登录倒计时
	 */
	private void autoLogin() {
		String username = et_username.getText().toString().trim();
		String password = et_pwd.getText().toString().trim();
		showquick();
		login(username, password, true);
	}

	/**
	 * 显示登录界面
	 */
	private void showlogin() {
		rl_login.setVisibility(View.VISIBLE);
		// iv_loading.clearAnimation();
		ll_quick_login.setVisibility(View.GONE);
	}

	/**
	 * 显示快捷登录界面
	 */
	private void showquick() {
		rl_login.setVisibility(View.GONE);
		iv_loadingtu.startAnimation(DialogUtil.rotaAnimation());
		ll_quick_login.setVisibility(View.VISIBLE);
		tv_cut_login.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取上次登录的用户信息
	 */
	private void getSqliteUser() {
		new AsyncTask<Void, Void, UserInfo>() {
			@Override
			protected UserInfo doInBackground(Void... params) {
				// 获取上次登录成功的用户信息显示在用户名框与密码框中
				try {
					UserInfo userlogini = UserLoginInfodao
							.getInstance(acontext).getUserInfoLast();
					return userlogini;
				} catch (Exception e) {
					String code = e.getMessage();
					Logger.msg(""+code);
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(UserInfo result) {
				if (null != result && !TextUtils.isEmpty(result.username)
						&& !TextUtils.isEmpty(result.password)) {
					Logger.msg("showquick()执行了吗");
					
					if(RegisterView.isSelfRegister){
						et_username.setText(RegisterView.username);
						et_pwd.setText(RegisterView.pwd);
						tv_quick_username.setText(RegisterView.username + ",");
					}else{
						et_username.setText(result.username);
						et_pwd.setText(result.password);
						tv_quick_username.setText(result.username + ",");
					}
					
					
					if (isShowQuikLogin) {
						showquick();
						autoLogin();
					} else {
						showlogin();
					}
				} else {
					Logger.msg("showlogin()执行了吗");
					showlogin();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	private void userselect(View v) {
		if (pw_select_user != null && pw_select_user.isShowing()) {
			pw_select_user.dismiss();
		} else {
			if (null != userLoginInfos) {
				userLoginInfos.clear();
			}
			;
			userLoginInfos = UserLoginInfodao.getInstance(acontext)
					.getUserLoginInfo();
			if (null == userLoginInfos) {
				return;
			}
			if (null == pw_adapter) {
				pw_adapter = new PwAdapter();
			}
			;
			Logger.msg("isPortrait:"+isPortrait);
			int pwidth = isPortrait ? (int) (DimensionUtil.getWidth(acontext) - DimensionUtil
					.dip2px(acontext, 76)) : (int) (DimensionUtil
					.getHeight(acontext) - DimensionUtil.dip2px(acontext, -200));

			if (pw_select_user == null) {
				// View
				// view=getLayoutInflater().inflate(R.layout.tiantianwan_pw_list,null);
				View view = inflater.inflate(MResource.getIdByName(acontext,
						"layout", "ttw_pw_list"), null);
				// ListView lv_pw=(ListView) view.findViewById(R.id.lv_pw);
				ListView lv_pw = (ListView) view.findViewById(MResource
						.getIdByName(acontext, "id", "lv_pw"));
				// LinearLayout.LayoutParams lp=new
				// LinearLayout.LayoutParams(200,-2 );
				// lv_pw.setLayoutParams(lp);
				lv_pw.setCacheColorHint(0x00000000);
				lv_pw.setAdapter(pw_adapter);
				lv_pw.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterview,
							View view, int position, long row) {
						pw_select_user.dismiss();
						userinfo_select = userLoginInfos.get(position);
						et_username.setText(userinfo_select.username);
						et_pwd.setText(userinfo_select.password);
						//tl_btn.setVisibility(View.VISIBLE);
						// tl_btn_savelogin.setVisibility(View.GONE);
						et_username.setEnabled(true);
						//btn_rpwd.setVisibility(View.GONE);

						userInfo.username = userinfo_select.username;
						userInfo.password = userinfo_select.password;
						if (userinfo_select.isrpwd == 0) {
							// 已经修改了密码
							// et_pwd.setInputType(InputType.TYPE_CLASS_TEXT |
							// InputType.TYPE_TEXT_VARIATION_PASSWORD);
							//btn_rpwd.setVisibility(View.GONE);
							// isPwselect = false;
						} else {
							// 表示没有修改
							// et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							//btn_rpwd.setVisibility(View.VISIBLE);
							// isPwselect = true;
						}
					}
				});
				pw_select_user = new PopupWindow(view, pwidth,
						LinearLayout.LayoutParams.WRAP_CONTENT, true);
				pw_select_user.setBackgroundDrawable(new ColorDrawable(
						0x00000000));
				pw_select_user.setContentView(view);
			} else {
				pw_adapter.notifyDataSetChanged();
			}
			pw_select_user.showAsDropDown(v,
					-pwidth + (int) DimensionUtil.dip2px(acontext, 40), 0);
		}
	}
	
	public void web(String name,String url){
		Intent intent_view = new Intent(acontext,
				FloatWebActivity.class);
		intent_view.putExtra("url", url);
		intent_view.putExtra("title", name);
		intent_view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		acontext.startActivity(intent_view);
		//getActivity().finish();
	}

	/**
	 * popupwindow显示已经登录用户的设配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class PwAdapter extends BaseAdapter {

		private ImageView iv_delete;

		@Override
		public int getCount() {
			return userLoginInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return userLoginInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (null == convertView) {
				View view = inflater.inflate(MResource.getIdByName(acontext,
						"layout", "ttw_pw_list_item"), null);

				convertView = view;
			}
			TextView tv = (TextView) convertView.findViewById(MResource
					.getIdByName(acontext, "id", "tv_username"));
			iv_delete = (ImageView) convertView.findViewById(MResource
					.getIdByName(acontext, "id", "ib_delete"));
			iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 如果删除的用户名与输入框中的用户名一致将删除输入框中的用户名与密码
					if (et_username.getText().toString().trim()
							.equals(userLoginInfos.get(position).username)) {
						et_username.setText("");
						et_pwd.setText("");
					}
					;
					UserLoginInfodao.getInstance(acontext)
							.deleteUserLoginByName(
									userLoginInfos.get(position).username);
					userLoginInfos.remove(position);
					if (null != pw_adapter) {
						pw_adapter.notifyDataSetChanged();
					}
				}
			});
			tv.setText(userLoginInfos.get(position).username);
			return convertView;
		}
	}
	

}
