package com.game.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.game.sdk.YTAppService;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.util.ActivityTaskManager;
import com.game.sdk.util.Constants;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.MResource;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.ipaynow.plugin.utils.PreSignMessageUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NowPayActivity extends Activity {
	
	private String orderid;
	private int charge_money;// 需要充值的金额
	private String serverid;// 充值的服务器id；
	private String productname;// 充值游戏名称
	private String productdesc;// 产品描述
	private String fcallbackurl;// 充值回调地址，由游戏方传递
	private String roleid;// 角色id；
	private String attach;// 游戏方传递的拓展参数
	private Context ctx;
	private PreSignMessageUtil preSign=new PreSignMessageUtil();
	private static String preSignStr=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(MResource.getIdByName(this, Constants.Resouce.LAYOUT, "ttw_alipay_pay"));
		this.ctx = getApplicationContext();
		
		Intent intent = getIntent();
		roleid = intent.getStringExtra("roleid");
		serverid = intent.getStringExtra("serverid");
		charge_money = intent.getIntExtra("money", 0);
		productname = intent.getStringExtra("productname");
		productdesc = intent.getStringExtra("productdesc");
		fcallbackurl = intent.getStringExtra("fcallbackurl");
		attach = intent.getStringExtra("attach");
		pay();
		
	}
	public void pay() {
		prePayMessage();
		orderid=getOutTradeNo();
		preSign.mhtOrderNo=orderid;


	    goToPay("13");
		
	}
	private void goToPay(String flag) {
		// TODO Auto-generated method stub
		// 网咯连接
		ConnectivityManager manager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		 if (info!=null&&info.isConnected()) {
			 
			
		     //支付类型： 微信支付
		     preSign.payChannelType=flag;
		     //生成代签名
    		 preSignStr=preSign.generatePreSignMessage();
    		
    		 
		     new AsyncTask<Void, Void, ResultCode>(){

				@Override
				protected ResultCode doInBackground(Void... params) {
					// TODO Auto-generated method stub
					try {
						return GetDataImpl.getInstance(ctx).nowpayserver("weixin",
								charge_money, YTAppService.userinfo.username,
								roleid, serverid, YTAppService.gameid, orderid,
								YTAppService.dm.imeil, YTAppService.appid,
								YTAppService.agentid, productname, productdesc,
								fcallbackurl, attach,preSignStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
					
				}
		    	@Override
		    	protected void onPostExecute(ResultCode result) {
		    		// TODO Auto-generated method stub
		    		super.onPostExecute(result);
		    		
		    		
		    		
		    		if (null != result && result.code == 1) {
		    		 String mhtSignature=preSignStr+"&mhtSignature="+result.msg+"&mhtSignType=MD5";
		    		 
		    		 try {
		    			
						IpaynowPlugin.pay(NowPayActivity.this,mhtSignature);
						
						
						

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		} else {
						String msg = result == null ? "服务端异常请稍候重试！" : result.msg;
						Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
						ActivityTaskManager.getInstance().removeActivity("ChargeActivity");
					}
		    		 
	         
		    	}
		     }.execute();
		     
		   
	         }
		 }
	
	private void prePayMessage(){
		 preSign.appId="1452852539222499";

		 preSign.mhtCharset="UTF-8";
		 preSign.mhtCurrencyType="156";
		 // 充值金额
		 preSign.mhtOrderAmt=Integer.toString(charge_money*100);

		 preSign.mhtOrderDetail=productdesc;
		
		 preSign.mhtOrderName=productname;

		 preSign.mhtOrderStartTime=new SimpleDateFormat("yyyyMMddHHmmss",Locale.CHINA).format(new Date());
		
		 preSign.mhtOrderTimeOut="3600";
		 preSign.mhtOrderType="01";
		preSign.mhtReserved=attach;
		
		 preSign.notifyUrl=Constants.URL_NOWPAY_URL;
		

   

		}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		 String respCode = data.getExtras().getString("respCode");
	      String errorCode = data.getExtras().getString("errorCode");
	      String respMsg = data.getExtras().getString("respMsg");
	     
			if (respCode.equals("00")) {
				PaymentCallbackInfo pci = new PaymentCallbackInfo();
				pci.money = charge_money;
				pci.msg = "支付成功";
				ChargeActivity.paymentListener.paymentSuccess(pci);
			}
			if(respCode.equals("02")){
				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code=02;
				msg_e.msg = "支付取消";
				msg_e.money = charge_money;
				ChargeActivity.paymentListener.paymentError(msg_e);
			}
			if (respCode.equals("01")) {
				
				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code = 03;
				msg_e.msg = "错误编码： "+errorCode+"    错误信息：=="+respMsg;
				msg_e.money = charge_money;
				
				ChargeActivity.paymentListener.paymentError(msg_e);
			}
			
			NowPayActivity.this.finish();// 不管充值是否成功 直接退出游戏界面
			ActivityTaskManager.getInstance().removeActivity("ChargeActivity");
	}
	
	
	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	private String getOutTradeNo() {
		
		
		Random random = new Random();
		int s = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
		return "" + System.currentTimeMillis() + s;
	}
	
	
	
	
}
