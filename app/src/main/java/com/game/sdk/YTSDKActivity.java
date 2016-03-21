package com.game.sdk;

import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.util.Constants;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.Md5Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class YTSDKActivity extends Activity implements OnClickListener {

	public YTSDKManager ttwsdkmanager;
	private EditText et_money;
	private TextView tv_msg;
	private Button btn_login, btn_charger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ttwsdkmanager = YTSDKManager.getInstance(this);

		setContentView(MResource.getIdByName(getApplication(),
				Constants.Resouce.LAYOUT, "ttw_sdk"));
		btn_login = (Button) findViewById(MResource.getIdByName(
				getApplication(), "id", "btn_login"));
		btn_charger = (Button) findViewById(MResource.getIdByName(
				getApplication(), "id", "btn_charger"));
		tv_msg = (TextView) findViewById(MResource.getIdByName(
				getApplication(), "id", "tv_msg"));

		et_money = (EditText) findViewById(MResource.getIdByName(
				getApplication(), "id", "et_money"));

		btn_login.setOnClickListener(this);
		btn_charger.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//登录事件
		if (null != btn_login && btn_login.getId() == v.getId()) {
			ttwsdkmanager.showLogin(this, true, new OnLoginListener() {
				@Override
				public void loginSuccess(LogincallBack logincallback) {
					// TODO Auto-generated method stub
					Toast.makeText(
							getApplication(),
							"sign:" + logincallback.sign + " logintime:"
									+ logincallback.logintime + " username:"
									+ logincallback.username, Toast.LENGTH_LONG)
							.show();
					try {
						Logger.msg(Md5Util
								.md5("username="
										+ logincallback.username
										+ "&appkey=145ad122f6be8b1cae0c81bba53f88bc&logintime="
										+ logincallback.logintime));
						Logger.msg(""
								+ (Md5Util
										.md5("username="
												+ logincallback.username
												+ "&appkey=145ad122f6be8b1cae0c81bba53f88bc&logintime="
												+ logincallback.logintime) == logincallback.sign));
					} catch (Exception e) {
						e.printStackTrace();
					}
					ttwsdkmanager.showFloatView();
				}

				@Override
				public void loginError(LoginErrorMsg errorMsg) {
					// TODO Auto-generated method stub

				}
			});
			return;
		}
		//充值事件
		if (null != btn_charger && btn_charger.getId() == v.getId()) {
			String money_str = et_money.getText().toString().trim();
			String money = "1";
			if (!TextUtils.isEmpty(money_str) && !"".equals(money_str)) {
				money = money_str;
			}
			ttwsdkmanager.showPay(this, "roleid", money, "1", "魔神", "金币","", "描述", new OnPaymentListener() {
						@Override
						public void paymentSuccess(
								PaymentCallbackInfo callbackInfo) {
							// TODO Auto-generated method stub
							Toast.makeText(
									getApplication(),
									"充值金额数：" + callbackInfo.money + " 消息提示："
											+ callbackInfo.msg,
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void paymentError(PaymentErrorMsg errorMsg) {
							// TODO Auto-generated method stub
							Toast.makeText(
									getApplication(),
									"充值失败：code:" + errorMsg.code
											+ "  ErrorMsg:" + errorMsg.msg
											+ "  预充值的金额：" + errorMsg.money,
									Toast.LENGTH_LONG).show();
						}
					});

			return;
		}

	}
	
	@Override
	protected void onDestroy() {
		ttwsdkmanager.recycle();//游戏退出必须调用
		super.onDestroy();
	};
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		ttwsdkmanager.removeFloatView();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		ttwsdkmanager.showFloatView();
		super.onResume();
	}
	
}
