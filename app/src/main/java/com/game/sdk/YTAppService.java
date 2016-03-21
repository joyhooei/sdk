package com.game.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.game.sdk.domain.ChannelMessage;
import com.game.sdk.domain.DesDeclaration;
import com.game.sdk.domain.DeviceMsg;
import com.game.sdk.domain.Proclamation;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.ThreadPoolManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * author janecer 2014年7月22日上午9:46:00 sdk系统核心类
 */
public class YTAppService extends Service {

	public static UserInfo userinfo;
	public static String gameid;
	public static String appid;
	public static String agentid;
	public static String service_tel;
	public static String service_qq;
	public static String ptbkey;
	public static int ttbrate;
	public static String notice;
	public static int ttb;
	public static int yxb;
	public static Proclamation  proclation;
	public static boolean isReadPro=false;
	public static DeviceMsg dm;// 设备信息
	public static List<ChannelMessage> channels;
	// public static List<Activity> activitys = new ArrayList<Activity>();
	public static boolean isLogin = false;// 判断用户是否登录
	public static int isgift = 1;// 判断用户是否登录
	private static Context actx;
	
	public static byte[] keyValue;
	public static byte[] iv;
	public static char[] k;
	
	static DesDeclaration desDeclaration;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void startService(Context ctx) {
		if (!isServiceRunning(ctx)) {
			actx = ctx;
			Intent intent_service = new Intent(ctx,YTAppService.class);
			intent_service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startService(intent_service);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String succ = "";
		try {
			succ = intent.getStringExtra("login_success");
		}catch (Exception e) {
			succ = "";
			//e.printStackTrace();
		}
		if ("login_success".equals(succ)) {
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					try {
						
						JSONObject json = new JSONObject();
						json.put("a", appid);
						json.put("b", userinfo.username);
						json.put("c", gameid);
						
						ResultCode result =null;
						result = GetDataImpl.getInstance(YTAppService.this)
								.getYXB(json.toString());
						
						ttb = result.ptb;
						yxb = result.yxb;
						
						
						Logger.msg("用户取消登录...");
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		if (null == channels) {
			
	
			ChannelMessage channel = null;
			channels = new ArrayList<ChannelMessage>();
			for(int i = 1;i < 5 ;i++){
				
				if(i == 1)
					channel = new ChannelMessage(i, "支付宝支付", "支付宝支付","alipay");
				if(i == 2)
					channel = new ChannelMessage(i, "微信支付", "微信支付","nowpay");
				if (i == 3)
					channel = new ChannelMessage(i, "爱心支付", "爱心支付","ptb");
				if (i == 4)
					channel = new ChannelMessage(i, "游戏币支付", "游戏币","gamepay");
				channels.add(channel);
				channel = null;
			}
			
		}
		handCommand();
		return START_STICKY;
	}
	
	private void handCommand() {
		Notification notification = new Notification(0, "",
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, "", "", contentIntent);
		startForeground(0, notification);
	}

	/**
	 * 用来判断当前服务类是否在运行
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runServices = am
				.getRunningServices(Integer.MAX_VALUE);
		if (null == runServices || 0 == runServices.size()) {
			return false;
		}
		for (int i = 0; i < runServices.size(); i++) {
			if (runServices.get(i).service.getClassName()
					.equals(YTAppService.class.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
