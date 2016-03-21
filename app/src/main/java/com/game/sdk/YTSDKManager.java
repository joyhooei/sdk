package com.game.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.game.sdk.domain.DesDeclaration;
import com.game.sdk.domain.DeviceMsg;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.floatwindow.FloatViewImpl;
import com.game.sdk.ui.ChargeActivity;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.util.CrashHandler;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.NetworkImpl;
import com.game.sdk.util.ThreadPoolManager;
import com.game.sdk.util.Util;

/**
 * author janecer 2014年7月22日上午9:45:18
 */
public class YTSDKManager {
	private static YTSDKManager instance;
	private static Context acontext;
	
	static {
	       System.loadLibrary("ytsdk");
	}

	private YTSDKManager(Context context) {
		YTAppService.startService(context);
		Logger.msg("inde===");
		YTAppService.desDeclaration = new DesDeclaration();  
		YTAppService.keyValue = YTAppService.desDeclaration.getKeyValue();  
		YTAppService.iv = YTAppService.desDeclaration.getIv(); 
		YTAppService.k = YTAppService.desDeclaration.getK(); 
        	
		init();
	}

	/**
	 * 初始化相关数据
	 */
	private void init() {
		
		Intent intent_service = new Intent(acontext, YTAppService.class);
		acontext.startService(intent_service);
		
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(acontext,""); 
        //设备管理器
		TelephonyManager tm = (TelephonyManager) acontext
				.getSystemService(Context.TELEPHONY_SERVICE);
		final DeviceMsg dm = new DeviceMsg();
		dm.imeil = tm.getDeviceId();
		dm.deviceinfo = tm.getLine1Number() + "||android"
				+ Build.VERSION.RELEASE;
		dm.userua = Util.getUserUa(acontext);
		YTAppService.dm = dm;
		Util.getGameAndAppId(acontext);

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				// dm.userip=NetworkImpl.getNetIp();
				GetDataImpl.getInstance(acontext).getTelAndQQ();
				try {
					JSONObject json = new JSONObject();
				
					json.put("a", YTAppService.appid);
					json.put("b", YTAppService.agentid);
					json.put("c", YTAppService.gameid);
					
					GetDataImpl.getInstance(acontext)
							.GetProclamation(json.toString());
				
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	//单例模式
		public static synchronized YTSDKManager getInstance(Context context) {
			Logger.msg("实例化");
			if(Looper.myLooper() != Looper.getMainLooper()){
				Logger.msg("实例化失败,未在主线程调用");
			}
			if (null == instance) {
				acontext = context;
				instance = new YTSDKManager(context);
			}
			
			if (null == acontext) {
				acontext = context;
			}
			
			return instance;
		}


	/**
	 * 显示登录
	 */
	public void showLogin(Context context, boolean isShowQuikLogin,
			OnLoginListener loginlist) {
		LoginActivity.loginlistener = loginlist;
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Toast.makeText(acontext, "网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
			Intent login_int = new Intent(context, LoginActivity.class);
			login_int.putExtra("isShowQuikLogin", false);
			login_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(login_int);
			return;
		}
		Intent login_int = new Intent(context, LoginActivity.class);
		login_int.putExtra("isShowQuikLogin", isShowQuikLogin);
		login_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(login_int);
		return;
	}

	/**
	 * 显示充值
	 * 
	 * @param context
	 * @param roleid
	 *            角色id
	 * @param money
	 *            充值金额
	 * @param serverid
	 *            服务器id
	 * @param productname
	 *            游戏名称 例如【诛仙-3阶成品天琊】
	 * @param productdesc
	 *            产品描述
	 * @param fcallbackurl
	 *            回调地址，此处可不填，由后台配置
	 * @param attach
	 *            拓展参数【若有自定义参数传递】
	 * @param paymentListener
	 *            充值回调监听
	 **/
	public void showPay(Context context, String roleid, String money,
			String serverid, String productname, String productdesc, String fcallbackurl,String attach,
			OnPaymentListener paymentListener) {
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Toast.makeText(acontext, "网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!YTAppService.isLogin) {
			Toast.makeText(acontext, "请先登录！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Toast.makeText(acontext, "网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (null == money || "".equals(money) || !money.matches("[0-9]+")) {
			Toast.makeText(acontext, "请输入金额,金额为数字", Toast.LENGTH_SHORT).show();
			return;
		}
		int moneys = Integer.parseInt(money);
	
		Intent pay_int = new Intent(context, ChargeActivity.class);
		ChargeActivity.paymentListener = paymentListener;
		pay_int.putExtra("roleid", roleid);
		pay_int.putExtra("money", moneys);
		pay_int.putExtra("serverid", serverid);
		pay_int.putExtra("productname", productname);
		pay_int.putExtra("productdesc", productdesc);
		pay_int.putExtra("fcallbackurl", "");
		pay_int.putExtra("attach", attach);
		pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(pay_int);
	}
	
	/**
	 * 显示充值
	 * 
	 * @param context
	 * @param roleid
	 *            角色id
	 * @param money
	 *            充值金额
	 * @param serverid
	 *            服务器id
	 * @param productname
	 *            游戏名称 例如【诛仙-3阶成品天琊】
	 * @param productdesc
	 *            产品描述
	 * @param attach
	 *            拓展参数【若有自定义参数传递】
	 * @param paymentListener
	 *            充值回调监听
	 **/
	public void showPay(Context context, String roleid, String money,
			String serverid, String productname, String productdesc,String attach,
			OnPaymentListener paymentListener) {
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Toast.makeText(acontext, "网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!YTAppService.isLogin) {
			Toast.makeText(acontext, "请先登录！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Toast.makeText(acontext, "网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (null == money || "".equals(money) || !money.matches("[0-9]+")) {
			Toast.makeText(acontext, "请输入金额,金额为数字", Toast.LENGTH_SHORT).show();
			return;
		}
		int moneys = Integer.parseInt(money);
	
		Intent pay_int = new Intent(context, ChargeActivity.class);
		ChargeActivity.paymentListener = paymentListener;
		pay_int.putExtra("roleid", roleid);
		pay_int.putExtra("money", moneys);
		pay_int.putExtra("serverid", serverid);
		pay_int.putExtra("productname", productname);
		pay_int.putExtra("productdesc", productdesc);
		pay_int.putExtra("fcallbackurl", "");
		pay_int.putExtra("attach", attach);
		pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(pay_int);
	}
	
	
	/**
	 * 显示浮标
	 * @param context
	 * @param x
	 * @param y
	 */
	public void showFloatView(){
		if(!YTAppService.isLogin){
			return;
		}
		Logger.msg("浮点启动");
		FloatViewImpl.getInstance(acontext);
	}
	
	/**
	 * 隐藏浮标
	 */
	public void removeFloatView() {
		FloatViewImpl.getInstance(acontext);
		FloatViewImpl.removeFloat();
	}
	
	
	/**
	 * 资源回收
	 */
	public void recycle(){
		//用户登录出去
		Logger.msg("回收资源");
		if(YTAppService.userinfo != null){
			new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... params) {
					JSONObject json = YTAppService.userinfo.outInfoToJson();
					GetDataImpl.getInstance(acontext).loginOut(json.toString());
					//Logger.msg("用户登录出");
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					YTAppService.isLogin=false;
					removeFloatView();
					Intent intent=new Intent(acontext,YTAppService.class);
					acontext.stopService(intent);
					return ;
				}
				
			}.execute();
		}
		
		 //移除浮标
		removeFloatView();
		
		Intent intent=new Intent(acontext,YTAppService.class);
		acontext.stopService(intent);
       
	}
}
