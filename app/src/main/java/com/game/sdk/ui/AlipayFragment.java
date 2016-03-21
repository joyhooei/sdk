package com.game.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.game.sdk.YTAppService;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Keys;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.NetworkImpl;
import com.game.sdk.util.Rsa;
import com.game.sdk.util.ThreadPoolManager;
import com.game.sdk.view.ChargeView;

import android.R.anim;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * author janecer 2014年7月23日下午3:35:31
 */
public class AlipayFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "ZifubaoFragment";
	private static final int RQF_PAY = 1000;
	private TextView tv_desc;
	private Activity mcActivity;
	private String orderid;
	private int charge_money;// 需要充值的金额
	private String serverid;// 充值的服务器id；
	private String productname;// 充值游戏名称
	private String productdesc;// 产品描述
	private String fcallbackurl;// 充值回调地址，由游戏方传递
	private String roleid;// 角色id；
	private String attach;// 游戏方传递的拓展参数

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RQF_PAY:// 支付宝支付回调
				// ChargerActivity.paymentListener.paymentSuccess(callbackInfo);
				String result = (String) msg.obj;
				if (null != result) {
					String[] result_obj = result.split(";");
					int resultStatus;// 返回状态码
					String memo;// 提示信息
					resultStatus = Integer.parseInt(result_obj[0].substring(
							result_obj[0].indexOf("{") + 1,
							result_obj[0].lastIndexOf("}")));
					memo = result_obj[1].substring(
							result_obj[1].indexOf("{") + 1,
							result_obj[1].lastIndexOf("}"));
					if (resultStatus == 9000) {
						// 支付成功
						ChargeView.ischarge = true;
						PaymentCallbackInfo pci = new PaymentCallbackInfo();
						pci.money = charge_money;
						pci.msg = memo;
						ChargeActivity.paymentListener.paymentSuccess(pci);
					} else {
						ChargeView.ischarge = false;
						PaymentErrorMsg msg_e = new PaymentErrorMsg();
						msg_e.code = resultStatus;
						msg_e.msg = memo;
						msg_e.money = charge_money;
						ChargeActivity.paymentListener.paymentError(msg_e);
					}
				} else {
					// 如果msg为null 是支付宝那边返回数据为null
					ChargeView.ischarge = false;
					PaymentErrorMsg msg_e = new PaymentErrorMsg();
					msg_e.code = 88888888;
					msg_e.msg = "无法判别充值是否成功！具体请查看后台数据";
					msg_e.money = charge_money;
					ChargeActivity.paymentListener.paymentError(msg_e);
				}
				mcActivity.finish();// 不管充值是否成功 直接退出游戏界面
				Logger.msg("result:" + result);
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = getActivity().getApplicationContext();
		mcActivity = getActivity();
		Intent intent = getActivity().getIntent();
		roleid = intent.getStringExtra("roleid");
		serverid = intent.getStringExtra("serverid");
		charge_money = intent.getIntExtra("money", 0);
		productname = intent.getStringExtra("productname");
		productdesc = intent.getStringExtra("productdesc");
		fcallbackurl = intent.getStringExtra("fcallbackurl");
		attach = intent.getStringExtra("attach");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(MResource.getIdByName(getActivity(),
				Constants.Resouce.LAYOUT, "ttw_alipay_pay"), null);
		et_money = (EditText) contentView.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "et_money"));
		tv_pay = (TextView) contentView.findViewById(MResource.getIdByName(ctx,
				Constants.Resouce.ID, "tv_pay"));
		tv_desc = (TextView) contentView.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_desc"));
		tv_desc.setText("去支付宝付款");

		et_money.setText("" + charge_money);
		et_money.setOnFocusChangeListener(fc_et_money);
		et_money.addTextChangedListener(tw_et_money);
		et_money.setOnClickListener(this);
		init_btn_charge_color(et_money.getText().toString().trim());
		tv_pay.setOnClickListener(this);
		return contentView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == et_money.getId()) {
			return;
		}
		if (v.getId() == tv_pay.getId()) {// 确认支付按钮
			Logger.msg("确定支付");
			
			pay();
			return;
		}
	}

	private OnFocusChangeListener fc_et_money = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
		}
	};
	private TextWatcher tw_et_money = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			init_btn_charge_color(s.toString());
		}
	};

	private void init_btn_charge_color(String s) {
		if (TextUtils.isEmpty(s) || "".equals(s)) {
			tv_pay.setBackgroundColor(ctx.getResources().getColor(
					MResource.getIdByName(ctx, "color", "btn_charge_gray")));
		} else {
			int money = Integer.parseInt(s);
			tv_pay.setBackgroundColor(ctx.getResources().getColor(
					MResource
							.getIdByName(ctx, "color",
									money == 0 ? "btn_charge_gray"
											: "btn_charge_green")));
		}
	}

	/**
	 * 先将相关数据发送到服务端，再将相关数据带给支付宝进行处理
	 */
	public void pay() {
		DialogUtil.showDialog(getActivity(), "正在努力的加载...");
		new AsyncTask<Void, Void, ResultCode>() {
			@Override
			protected ResultCode doInBackground(Void... params) {
				orderid = getOutTradeNo();
				try {
					return GetDataImpl.getInstance(ctx).alipay2server("zfb",
							charge_money, YTAppService.userinfo.username,
							roleid, serverid, YTAppService.gameid, orderid,
							YTAppService.dm.imeil, YTAppService.appid,
							YTAppService.agentid, productname, productdesc,
							fcallbackurl, attach);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(ResultCode result) {
				super.onPostExecute(result);
				try {
					DialogUtil.dismissDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null != result && result.code == 1) {
					// 调用支付宝相关api，显示支付宝充值界面
					payTask();
				} else {
					String msg = result == null ? "服务端异常请稍候重试！" : result.msg;
					Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	/**
	 * 确定支付，将相关信息发送到支付宝服务端
	 */
	private void payTask() {
		try {
			String info = getNewOrderInfo();
			String sign = Rsa.sign(info, Keys.PRIVATE);

			sign = URLEncoder.encode(sign);

			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			//Log.i(TAG, "info = " + info);

			final String orderInfo = info;

			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(getActivity());
					// 调用支付接口
					String result = alipay.pay(orderInfo);

					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(ctx, "支付失败！" + ex, Toast.LENGTH_SHORT).show();
		}
	}

	private String getNewOrderInfo() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");// 合作者身份id 等待服务端传递
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");// 订单号，等待服务端传递
		sb.append(orderid);
		sb.append("\"&subject=\"");
		sb.append(Uri.decode(productname));
		sb.append("\"&body=\"");
		sb.append(Uri.decode(productdesc));
		sb.append("\"&total_fee=\"");
		sb.append(charge_money);
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode(Constants.URL_NOTIFY_URL, "UTF-8"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		// sb.append("\"&return_url=\"");
		// sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");// 卖家支付宝账号，等待服务端传递
		sb.append(Keys.DEFAULT_SELLER);
		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		return new String(sb);
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	private String getOutTradeNo() {
		// ''.time().rand(100000,999999);
		long timestamp = System.currentTimeMillis();
		Random random = new Random();
		int s = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
		return "" + System.currentTimeMillis() + s;
	}
}
