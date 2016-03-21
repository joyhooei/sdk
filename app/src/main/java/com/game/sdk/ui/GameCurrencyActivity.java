package com.game.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.YTAppService;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.util.ActivityTaskManager;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.Md5Util;
import com.game.sdk.view.ChargeView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class GameCurrencyActivity extends Activity implements OnClickListener{
	private TextView tv_refresh, tv_ttb_rest, tv_ttb_tip,tv_ptb_username,et_money,tv_pay,tv_back,tv_charge_title;
	private Context ctx;
	private int charge_money;// 需要充值的金额
	private String serverid;// 充值的服务器id；
	private String productname;// 充值游戏名称
	private String productdesc;// 产品描述
	private String fcallbackurl;// 充值回调地址，由游戏方传递
	private String roleid;// 角色id；
	private String attach;// 游戏方传递的拓展参数
	private long lasttime;//防止用户点击刷新
	private String sign ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(MResource.getIdByName(this,
				Constants.Resouce.LAYOUT, "sdk_game_pay"));
		this.ctx=getApplicationContext();
		Intent intent = getIntent();
		roleid = intent.getStringExtra("roleid");
		serverid = intent.getStringExtra("serverid");
		charge_money = intent.getIntExtra("money", 0);
		productname = intent.getStringExtra("productname");
		productdesc = intent.getStringExtra("productdesc");
		fcallbackurl = intent.getStringExtra("fcallbackurl");
		attach = intent.getStringExtra("attach");
		init();
		super.onCreate(savedInstanceState);
	}
   private void init(){
		
		et_money = (TextView) findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_ptb_money"));
		tv_pay = (TextView)findViewById(MResource.getIdByName(ctx,
				Constants.Resouce.ID, "tv_pay"));
		tv_refresh = (TextView) findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_refresh"));
		tv_ttb_rest = (TextView) findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "tv_ttb_rest"));
		tv_ttb_tip = (TextView) findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_ttb_tip"));
		tv_ptb_username=(TextView)findViewById(MResource.getIdByName(ctx, 
				Constants.Resouce.ID, "tv_ptb_username"));
		tv_charge_title=(TextView) findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "tv_charge_title"));
		tv_charge_title.setOnClickListener(this);
		tv_back=(TextView) findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "tv_back"));
		tv_back.setOnClickListener(this);
		
		tv_ptb_username.setText(YTAppService.userinfo.username);
		tv_ttb_rest.setText(String.valueOf(YTAppService.yxb));
		et_money.setText(charge_money+"元"+" ("+charge_money * YTAppService.ttbrate + "游戏币)");
		tv_ttb_tip.setText("(1人民币=" + YTAppService.ttbrate + "游戏币)");

		//init_btn_charge_color(charge_money * SDKService.ptbrate+"");
		et_money.setOnFocusChangeListener(fc_et_money);
		//et_money.addTextChangedListener(tw_et_money);
		et_money.setOnClickListener(this);
		tv_pay.setOnClickListener(this);
		tv_refresh.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (null != tv_back && v.getId() == tv_back.getId()) {
					
					ChargeView.ischarge = false;
					PaymentErrorMsg pci = new PaymentErrorMsg();
					pci.money = charge_money;
					pci.msg = "用户取消充值！";
					pci.code=-1;
					ChargeActivity.paymentListener.paymentError(pci);
					
					this.finish();
					ActivityTaskManager.getInstance().removeActivity("ChargeActivity");
					return;
				}
				if (null != tv_charge_title && v.getId() == tv_charge_title.getId()) {
					ChargeView.ischarge = false;
					PaymentErrorMsg pci = new PaymentErrorMsg();
					pci.money = charge_money;
					pci.code=-1;
					pci.msg = "用户取消充值！";
					ChargeActivity.paymentListener.paymentError(pci);
					
					this.finish();
					ActivityTaskManager.getInstance().removeActivity("ChargeActivity");
					return;
				}
				if (null != tv_pay && v.getId() == tv_pay.getId()) {
					charge_money = charge_money * YTAppService.ttbrate;
					
					if (charge_money > YTAppService.yxb) {
						Toast.makeText(ctx, "貌似游戏币不够！快去充值吧，骚年", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					DialogUtil.showDialog(GameCurrencyActivity.this, "正在充值中...");
					try {
						String parment=getNewOrderInfo();
						
						sign = Md5Util.md5(parment);
						
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new AsyncTask<Void, Void, ResultCode>() {
						@Override
						protected ResultCode doInBackground(Void... params) {
							// TODO Auto-generated method stub+
							ResultCode result = GetDataImpl.getInstance(ctx)
									.changeGame("yxb", charge_money,
											YTAppService.dm.imeil,
											YTAppService.appid, YTAppService.agentid,
											YTAppService.userinfo.username, roleid,
											serverid, YTAppService.gameid,
											productname, productdesc, attach,
											sign);
							return result;
						}

						@Override
						protected void onPostExecute(ResultCode result) {
							// TODO Auto-generated method stub
							try {
								DialogUtil.dismissDialog();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// 充值成功
							// 支付成功
							ChargeView.ischarge = true;
							PaymentCallbackInfo pci = new PaymentCallbackInfo();
							pci.money = charge_money;
							pci.msg = "充值成功";
							ChargeActivity.paymentListener.paymentSuccess(pci);
							showDialog((null != result && result.code == 1) ? "充值成功！谢谢您的支持！"
									: "充值失败！请联系客服\n QQ:" + YTAppService.service_qq
											+ " \n Tel:" + YTAppService.service_tel);
							super.onPostExecute(result);
							/**
							 * 支付成功后刷新币
							 */
							Intent app_intent = new Intent(ctx, YTAppService.class);
							app_intent.putExtra("login_success", "login_success");
							ctx.startService(app_intent);
							GameCurrencyActivity.this.finish();
							ActivityTaskManager.getInstance().removeActivity("ChargeActivity");

						}
						
					}.execute();

					return;
				}				
				if (null != tv_refresh && v.getId() == tv_refresh.getId()) {
					refreshTTB();
					return;
				}
		}
	
	private OnFocusChangeListener fc_et_money = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
		}
	};

	private void init_btn_charge_color(String s) {
		if (TextUtils.isEmpty(s) || "".equals(s) || !s.matches("[0-9]+")) {
			tv_pay.setBackgroundColor(ctx.getResources().getColor(
					MResource.getIdByName(ctx, "color", "btn_charge_gray")));
		} else {
			int money = Integer.parseInt(s);
			charge_money = money;
			tv_pay.setBackgroundColor(ctx.getResources().getColor(
					MResource
							.getIdByName(ctx, "color",
									money == 0 ? "btn_charge_gray"
											: "btn_charge_green")));
		}
	}
	
	private Dialog dialog;
	/**
	 * 游戏币充值时显示的对话框
	 */
	private void showDialog(String msg) {
		dialog = new Dialog(GameCurrencyActivity.this, MResource.getIdByName(ctx,
				"style", "customDialog"));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		// View
		// view=getLayoutInflater().inflate(R.layout.tiantianwan_order_fail,null);
		View view = LayoutInflater.from(this).inflate(
				MResource.getIdByName(ctx, "layout",
						"sdk_tell_service"), null);
		view.findViewById(MResource.getIdByName(ctx, "id", "tv_tip"))
				.setVisibility(View.GONE);
		((TextView) view.findViewById(MResource.getIdByName(ctx,
				"id", "tv_qq"))).setText(msg);
		((TextView) view.findViewById(MResource.getIdByName(ctx,
				"id", "tv_tel"))).setVisibility(View.GONE);

		((TextView) view.findViewById(MResource.getIdByName(ctx,
				"id", "tv_orderid_title"))).setVisibility(View.GONE);
		((TextView) view.findViewById(MResource.getIdByName(ctx,
				"id", "tv_orderid_title1"))).setVisibility(View.GONE);
		view.findViewById(
				MResource.getIdByName(ctx, "id", "btn_sure"))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						dialog = null;
						GameCurrencyActivity.this.finish();
						ActivityTaskManager.getInstance().removeActivity("ChargeActivity");
					}
				});
		dialog.setContentView(view);
		dialog.show();
	};
	private String getNewOrderInfo() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("type=ptb&yxb=");// 合作者身份id 等待服务端传递
		sb.append(charge_money);
		
		sb.append("&imeil=");// 订单号，等待服务端传递
		sb.append(URLEncoder.encode(YTAppService.dm.imeil, "UTF-8"));
		
		sb.append("&appid=");
		sb.append(YTAppService.appid);
		
		sb.append("&agent=");
		sb.append(URLEncoder.encode(YTAppService.agentid, "UTF-8"));
		
		sb.append("&username=");
		sb.append(URLEncoder.encode(YTAppService.userinfo.username, "UTF-8"));
		
		
		
		sb.append("&roleid=");
		sb.append(URLEncoder.encode(roleid, "UTF-8"));
		
		
		
		sb.append("&serverid=");
		sb.append(URLEncoder.encode(serverid, "UTF-8"));
		
		 sb.append("&gameid=");
		 sb.append(YTAppService.gameid);
		 
		 
		sb.append("&productname=");
		sb.append(URLEncoder.encode(productname, "UTF-8"));
		
		
		sb.append("&productdesc=");
		sb.append(URLEncoder.encode(productdesc, "UTF-8"));
		
		
		sb.append("&attach=");
		sb.append(URLEncoder.encode(attach, "UTF-8"));
		
		sb.append("&memkey=");
		sb.append(URLEncoder.encode(YTAppService.ptbkey, "UTF-8"));
		
		return new String(sb);
	}
	private int count=1;
	/**
	 * 刷新易通币
	 */
	private void refreshTTB() {
		if(System.currentTimeMillis()-lasttime<=3000){
			if(count==1){
			    Toast.makeText(ctx, "您点击频率太快，请休息几秒钟", Toast.LENGTH_SHORT).show();
			}
			count++;
			return;
		}
		//刷新平台币
		new AsyncTask<Void, Void, ResultCode>() {
			@Override
			protected ResultCode doInBackground(Void... params) {
				JSONObject json = new JSONObject();
				try {
					json.put("a", YTAppService.appid);
					json.put("b", YTAppService.userinfo.username);
					json.put("c", YTAppService.gameid);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return GetDataImpl.getInstance(ctx).getYXB(json.toString());
			}
			protected void onPostExecute(ResultCode result) {
				if (null == result) {
					return;
				}
				String msg="";
				if (result.code == 1) {
					try {
						YTAppService.yxb = result.yxb;
						msg="余额刷新成功！";
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (result.code == -2) {
					// 没有平台币
					YTAppService.yxb = 0;
					msg="余额刷新成功！";
				}else{
					msg="余额刷新失败！";
				}
				Toast.makeText(ctx, msg,Toast.LENGTH_SHORT).show();
				tv_ttb_rest.setText(String.valueOf(YTAppService.yxb));// 设置平台币数目					
				lasttime=System.currentTimeMillis();
				count=1;
			};
		}.execute();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (dialog != null && dialog.isShowing())
		{
		     dialog.dismiss();
		}
		super.onDestroy();
		
	}
}
