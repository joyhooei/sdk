package com.game.sdk.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;

import com.game.sdk.YTAppService;

public class Util {

	private static final String TAG = "Util";

	/**
	 * @return
	 */
	public static String getOrderId() {
		String orderId = "" + System.currentTimeMillis();
		return orderId;
	}

	// public static String md5Encode(String s) {
	// if (s == null) {
	// return "";
	// }
	// try {
	// MessageDigest md5 = MessageDigest.getInstance("MD5");
	// byte[] digest = md5.digest(s.getBytes("utf-8"));
	// byte[] encode = Base64.encodeBase64(digest);
	// return new String(encode, "utf-8");
	// } catch (Exception e) {
	// Log.i(TAG, "md5 encode exception");
	// }
	// return s;
	// }

	/**
	 * 随机获取16位
	 * 
	 * @return
	 */
	public static String getRandom16() {
		String s = "";
		Random random = new Random();
		s += random.nextInt(9) + 1;
		for (int i = 0; i < 16 - 1; i++) {
			s += random.nextInt(10);
		}
		BigInteger bigInteger = new BigInteger(s);
		System.out.println(bigInteger);
		return bigInteger.toString();
	}

	/**
	 * 获取ua信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String getUserUa(Context context) {
		WebView webview = new WebView(context);
		webview.layout(0, 0, 0, 0);
		String str = webview.getSettings().getUserAgentString();
		Logger.msg(str);
		return str;
	}

	/**
	 * 从清单文件中获取gameid与appid
	 * 
	 * @param context
	 * @return
	 */
	public static void getGameAndAppId(Context context) {

		PackageManager pm = context.getPackageManager();

		try {
			// 先通过反射获取
//			reflectGameAndAppId(context);
//			if (null != TTWAppService.gameid && null != TTWAppService.agentid
//					&& null != TTWAppService.appid) {
//				return;
//			}
			// 在从清单文件中获取
			String channel =getChannel(context);
			//Logger.msg("test----"+channel);
			// 在从清单文件中获取
			ApplicationInfo appinfo = pm.getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appinfo.metaData;
			if (null != bundle) {
				YTAppService.appid = bundle.getInt("YW_APPID") + "";
				YTAppService.gameid = bundle.getInt("YW_GAMEID") + "";
				YTAppService.agentid = bundle.getString("YW_AGENT");
				if(null != channel && !"".equals(channel)){
					YTAppService.agentid = channel;
				}
				//Logger.msg("CHUYOU_____>appid：" + TTWAppService.appid
						//+ "    gameid:" + TTWAppService.gameid);
				//Logger.msg("agentid:" + TTWAppService.agentid);
				return;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过反射获取gameid,appid,agent
	 * 
	 * @param context
	 */
	public static void reflectGameAndAppId(Context context) {
		try {
			Class<?> c = Class.forName("com.tiantianwan.game.sdk.TTWconfig");
			Method method_gameid = c.getDeclaredMethod("getGameId",
					Context.class);
			method_gameid.setAccessible(true);
			Method method_appid = c
					.getDeclaredMethod("getAppId", Context.class);
			method_appid.setAccessible(true);
			Method method_agent = c
					.getDeclaredMethod("getAgent", Context.class);
			method_agent.setAccessible(true);

			Object obj = c.newInstance();
			YTAppService.gameid = (String) method_gameid.invoke(obj, context);
			YTAppService.appid = (String) method_appid.invoke(obj, context);
			YTAppService.agentid = (String) method_agent.invoke(obj, context);
			Logger.msg("反射获取的tiantianwan_____>appid：" + YTAppService.appid
					+ "    gameid:" + YTAppService.gameid);
			Logger.msg("agentid:" + YTAppService.agentid);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	//渠道获取
		public static String getChannel(Context context) {  
			// 从配置文件中 找到文件
			ApplicationInfo appinfo = context.getApplicationInfo();         
			String sourceDir = appinfo.sourceDir;  
			
			String ret = "";         
			ZipFile zipfile = null;         
			try {             
				zipfile = new ZipFile(sourceDir);             
				Enumeration<?> entries = zipfile.entries();            
				while (entries.hasMoreElements()) {                 
					ZipEntry entry = ((ZipEntry) entries.nextElement()); 
					
					String entryName = entry.getName();     
				
					if (entryName.startsWith("META-INF/gamechannel")) { 
							ret = entryName;                     
							break;                 
						}             
					}         
				} catch (IOException e) {             
					e.printStackTrace();         
				} finally {
					if (zipfile != null) { 
						try {                   
								zipfile.close();              
							} 
						catch (IOException e) { 
								e.printStackTrace();                
							}            
						}        
					}         
				String[] split = ret.split("_");       
				if (split != null && split.length >= 2) { 
					 
					  return  split[1];          
					} 
				else {    
					return "";        
					}    
//				if (split != null && split.length >= 2) { 
//					return ret.substring(split[0].length() + 1);         
//				} 
//				else {    
//					return "";        
//				}    
		}
}
