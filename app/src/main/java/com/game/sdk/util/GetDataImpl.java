package com.game.sdk.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.game.sdk.YTAppService;
import com.game.sdk.domain.ChannelMessage;
import com.game.sdk.domain.GameDetail;
import com.game.sdk.domain.GameGiftItem;
import com.game.sdk.domain.Proclamation;
import com.game.sdk.domain.ResultCode;

/**
 * author janecer 2014年4月10日下午5:29:57
 */
public class GetDataImpl {

	private static final String TAG = "GetDataImpl";
	private static GetDataImpl getdataImpl;
	private static Context ctx;

	private GetDataImpl(Context ctxs) {
		this.ctx = ctxs;
		
	}

	public static GetDataImpl getInstance(Context ctxs) {
		if (null == getdataImpl) {
			getdataImpl = new GetDataImpl(ctxs);
		}
		if (ctx==null) {
			ctx=ctxs;
		}
		return getdataImpl;
	}

	public void test() {
		InputStream request = doRequest("http://192.168.0.159/web/test7.php",
				"ssss//!~@ssssssss12312淡定");
		String bb = parseIs2Str(request);
		String aa = Encrypt.decode(bb);
		// bb = Encrypt.decode(bb);
		Logger.msg("service test:" + bb);
		
	}
	
	 /**
	 * 根据图片地址 去服务端下载图片
	 * @param path
	 * @return
	 */
	 public InputStream getImgFromNet(String url){
		   return doRequesttwo(url, null);
	 }

	/**
	 * 登录
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode login(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_LOGIN, jasonStr);
		ResultCode result = new ResultCode();
		
		try {
			String str = unzip(request);
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 一键注册
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode UserOneKeyRegister(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_ONKEY2REGISTER,
				jasonStr);
		ResultCode result = new ResultCode();
		
		try {
			
			String str = unzip(request);
			
			if(null != str){
				
				JSONObject json = new JSONObject(str);
				result.oneregJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 注册
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode register(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_REGISTER, jasonStr);
		ResultCode result = new ResultCode();

		try {
			
			String str = unzip(request);
			
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 	 短信 注册
	 * */
	public ResultCode regSendCode(String jasonStr){
		InputStream request = executeRequest(Constants.URL_USER_SENDCODE, jasonStr);
		ResultCode result = new ResultCode();
		
		try {
			
			String str = unzip(request);
			
			Logger.msg("kadaj+++==="+str);
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result; 
	}
	 /**
	 * 获取游戏币
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode getYXB(String jasonStr) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			
			InputStream request = doRequest(Constants.URL_USER_PAYTTB,
						jasonStr);
			String str = unzip(request);
			
			if(null != str){
				json = new JSONObject(str);
			}

			result = new ResultCode();
			result.parseYXBJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * 游戏币支付
	 * @param jasonStr
	 * @return
	 */
	public ResultCode changeGame(String type, int amount, String imeil,
			String appid, String agent, String username, String roleid,
			String serverid, String gameid, String productname,
			String productdes, String attach, String sign) {

		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", imeil);
			json.put("d", appid);
			json.put("e", agent);
			json.put("f", username);
			json.put("g", roleid);
			json.put("h", serverid);
			json.put("j", gameid);
			json.put("k", productname);
			json.put("l", productdes);
			json.put("m", attach);
			json.put("z", sign);
			//Logger.msg("json :" + json.toString());
			InputStream request = doRequest(Constants.URL_USER_GAMEPAY,
					json.toString());
			String str=unzip(request);
			
			json = new JSONObject(str);

			result = new ResultCode();
			result.parseTTBJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	public void Getuserpro(String jasonStr){
		ResultCode result =null;
		
		try {
			JSONObject json = new JSONObject();
			
			InputStream request = doRequest(Constants.URL_USERPROCLAMATION, 
					jasonStr);
			String str= unzip(request);
			
			if (null !=str) {
				json = new JSONObject(str);
				int code = json.isNull("a") ? 0 : json.getInt("a");
				if (code == 1) {
					YTAppService.isReadPro=true;
					
				}
			}
			
		} catch (JSONException  e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
public  Void GetProclamation(String jasonStr){
		
		ResultCode result =null;
		
		try {
			JSONObject json = new JSONObject();
			InputStream request = doRequest(Constants.URL_PROCLAMATION, 
					jasonStr);
			String str= unzip(request);
			
			if (null !=str) {
				json= new JSONObject(str);
				int code = json.isNull("a") ? 0 : json.getInt("a");
				
				if (code == 1) {
					
					String projson=json.isNull("b") ? "" :json.getString("b");
					
				   JSONObject	jsons= new JSONObject(projson);
				    Proclamation procla= new Proclamation();
				   
				    procla.proid = jsons.isNull("a") ? 0 : jsons
							.getInt("a");
					
				    procla.title = jsons.isNull("b") ? "" : jsons
							.getString("b");
					
				    procla.content= jsons.isNull("c") ? "" :jsons
							.getString("c");
				    YTAppService.proclation = procla;
					
				}
			}
			
		} catch (JSONException  e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return  null;
	}
	 /**
	 * 现在支付 
	 **/
	public ResultCode nowpayserver(String type, int amount, String username,
			String roleid, String serverid, String gameid, String orderid,
			String imeil, String appid, String agent, String productname,
			String productdesc, String fcallback, String attach,String preSignStr) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", orderid);
			json.put("h", imeil);
			json.put("j", appid);
			json.put("k", agent);
			json.put("l", productname);
			//json.put("m", productdesc);
			json.put("n", attach);
			json.put("fcallbackurl", fcallback);
			json.put("o", preSignStr);
			//Logger.msg("json :" + json.toString());
			InputStream request = doRequest(Constants.URL_CHARGER_NOWPAY,
					json.toString());
			
			String str = unzip(request);
			
			if(null != str){
				
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseNowPayJson(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * 	 短信 验证码登陆
	 * */
	public ResultCode loginSendCode(String jasonStr){
		InputStream request = executeRequest(Constants.URL_USER_LOGIN_SENDCODE, jasonStr);
		ResultCode result = new ResultCode();
		
		try {
			
			String str = unzip(request);
			
			Logger.msg("kadaj+++==="+str);
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result; 
	}
	
	public static String PHPSESSID = null;  
	   public InputStream executeRequest(String url,String str){
		   String ret="none";
		   DefaultHttpClient  client=new DefaultHttpClient();
		   if(null==client){
			   return null;
		   }
		   HttpPost post=new HttpPost(url);
		   post.setHeader("content-type", "text/html");
		   if (null!=PHPSESSID) {
			  
			   post.setHeader("Cookie","PHPSESSID="+PHPSESSID);
		   }
		  Logger.msg( "request url and data:"+url+"   data:"+str);
		  
		   if(str!=null){
			   str = Encrypt.encode(str);
			   HttpEntity entity=new ByteArrayEntity(compress(str.getBytes()));
			   post.setEntity(entity);
		   }
		   
		   int count=0;
		   //等待3秒在请求2次
		   while(count<2){
			  try {
			     HttpResponse response= client.execute(post);
			     
			     if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			    	
			    	//HttpEntity entity=response.getEntity();
			    	//ret=EntityUtils.toString(entity);
			    	CookieStore mCookieStore= client.getCookieStore();
			    	 List<Cookie> cookies = mCookieStore.getCookies(); 
			    	 for (int i = 0; i < cookies.size(); i++) {  
		                    //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值  
		                    if ("PHPSESSID".equals(cookies.get(i).getName())) {  
		                        PHPSESSID = cookies.get(i).getValue();  
		                        break;  
		                    }  
		  
		                }  
			    	 
			    	 return response.getEntity().getContent();		  
			     }
		      } catch (ClientProtocolException e) {
			    e.printStackTrace();
		      } catch (IOException e) {
			    e.printStackTrace();
		      }
		     count++;
		     try {
				Thread.currentThread().sleep(3000);
			    } catch (InterruptedException e) {
				  e.printStackTrace();
			    }
		   }
		   return null;
	   }

	/**
	 * 获取平台币
	 * 
	 * @param jasonStr
	 * @return
	 */
	public int getTTB(String jasonStr) {
		int ttb = 0;
		InputStream request = doRequest(Constants.URL_USER_PAYTTB, jasonStr);
		// ResultCode result = new ResultCode();

		try {
			// Logger.msg("test = :"+parseIs2Str(request));
			String str = unzip(request);
			if(null != str){
				JSONObject json = new JSONObject(str);

				ttb = json.isNull("b") ? 0 : json.getInt("b");
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ttb;
	}
	
	/**
	 * 获取平台币
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode getTTBTwo(String jasonStr) {
		
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			
			InputStream request = doRequest(Constants.URL_USER_PAYTTB,
						jasonStr);
			String str = unzip(request);
			if(null != str){
				json = new JSONObject(str);
			}

			result = new ResultCode();
			result.parseTTBTwoJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * 支付宝充值
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode alipay2server(String type, int amount, String username,
			String roleid, String serverid, String gameid, String orderid,
			String imeil, String appid, String agent, String productname,
			String productdesc, String fcallback, String attach) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", orderid);
			json.put("h", imeil);
			json.put("j", appid);
			json.put("k", agent);
			json.put("l", productname);
			//json.put("m", productdesc);
			json.put("n", attach);
			json.put("fcallbackurl", fcallback);
			
			InputStream request = doRequest(Constants.URL_CHARGER_ZIFUBAO,
					json.toString());
			
			String str = unzip(request);
			
			if(null != str){
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseAlipayJson(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 财付通支付
	 * 
	 * @param type
	 * @param amount
	 * @param username
	 * @param roleid
	 * @param serverid
	 * @param gameid
	 * @param imeil
	 * @param appid
	 * @param agent
	 * @param productname
	 * @param productdesc
	 * @param fcallbackurl
	 * @param attach
	 * @return
	 */
	public ResultCode caifutongCharge(String type, int amount, String username,
			String roleid, String serverid, String gameid, String imeil,
			String appid, String agent, String productname, String productdesc,
			String fcallbackurl, String attach) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", imeil);
			json.put("h", appid);
			json.put("j", agent);
			json.put("k", productname);
			json.put("l", productdesc);
			json.put("fcallbackurl", fcallbackurl);
			json.put("m", attach);
			InputStream request = doRequest(Constants.URL_CHARGER_CAIFUTONG,
					json.toString());
			json = new JSONObject(unzip(request));

			result = new ResultCode();
			result.parseCFTJson(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 易宝快捷支付
	 * 
	 * @param username
	 * @param appid
	 * @param gameid
	 * @param agent
	 * @param amount
	 * @param productname
	 * @param imeil
	 * @param serverid
	 * @param type
	 * @param fcallbackurl
	 * @param attach
	 * @param roleid
	 * @param userua
	 * @param productdesc
	 * @return
	 */
	public ResultCode onkeyCharge(String username, String appid, String gameid,
			String agent, int amount, String productname, String imeil,
			String serverid, String type, String fcallbackurl, String attach,
			String roleid, String userua, String productdesc) {
		ResultCode result = null;
		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", appid);
			json.put("f", serverid);
			json.put("g", gameid);
			json.put("h", agent);
			json.put("j", imeil);
			json.put("k", productname);
			json.put("l", productdesc);
			json.put("m", userua);
			json.put("fcallbackurl", fcallbackurl);
			json.put("n", attach);

			InputStream request = doRequesttwo(Constants.URL_USR_ONEKEYPAY,
					json.toString());
			String str=parseIs2Str(request);
//			json = new JSONObject(str);
//			
//			result = new ResultCode();
//			result.parseCFTJson(json);
			
			if(null != str){
				
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseCFTJson(json);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	/**
	 * 易联支付
	 * 
	 * @param username
	 * @param appid
	 * @param gameid
	 * @param agent
	 * @param amount
	 * @param productname
	 * @param imeil
	 * @param serverid
	 * @param type
	 * @param fcallbackurl
	 * @param attach
	 * @param roleid
	 * @param userua
	 * @param productdesc
	 * @return
	 */
	public ResultCode onEcoCharge(String username, String appid, String gameid,
			String agent, int amount, String productname, String imeil,
			String serverid, String fcallbackurl, String attach,
			String roleid, String userua, String productdesc) {
		//String rjson = "";
		ResultCode result = null;
		try {
			JSONObject json = new JSONObject();
			json.put("a", amount);
			json.put("b", username);
			json.put("c", roleid);
			json.put("d", appid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", agent);
			json.put("h", productdesc);
			json.put("j", imeil);
			json.put("n", attach);

			InputStream request = doRequest(Constants.URL_USR_ECOPAY,
					json.toString());
			String str = parseIs3Str(request);
			if(null != str){
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseECOJson(json);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 易联支付回调信息
	 * 
	 * @param username
	 */
	public String onEcoNotify(JSONObject json) {
		//String rjson = "";
		String result = null;
		try {

			InputStream request = doRequest(Constants.URL_PAY_NOTIFY,
					json.toString());
			
			result = parseIs3Str(request);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	

	/**
	 * 手机充值卡充值
	 * 
	 * @param appid
	 * @param gameid
	 * @param agent
	 * @param type
	 * @param imeil
	 * @param amount
	 * @param productname
	 * @param producttype
	 * @param productdesc
	 * @param cardno
	 * @param cardamt
	 * @param cardpwd
	 * @param serverid
	 * @param fcallbackurl
	 * @param attach
	 * @param roleid
	 * @param username
	 * @return
	 */
	public ResultCode mobileCardCharge(String appid, String gameid,
			String agent, String type, String imeil, int amount,
			String productname, String producttype, String productdesc,
			String cardno, String cardamt, String cardpwd, String serverid,
			String fcallbackurl, String attach, String roleid, String username) {
		ResultCode result = null;
		try {
			JSONObject json = new JSONObject();
			json.put("a", amount);
			json.put("b", productname);
			json.put("c", producttype);
			json.put("d", productdesc);
			json.put("e", cardamt);
			json.put("f", cardno);
			json.put("g", cardpwd);
			json.put("h", type);
			json.put("j", username);
			json.put("k", agent);
			json.put("l", serverid);
			json.put("m", gameid);
			json.put("n", imeil);
			json.put("o", roleid);
			json.put("p", appid);
			json.put("fcallbackurl", fcallbackurl);
			json.put("q", attach);

			InputStream request = doRequest(Constants.URL_USR_MOBILECARDPAY,
					json.toString());
			// InputStream request2 = request;
			// Logger.msg("moblie="+parseIs2Str(request2));
			json = new JSONObject(unzip(request));

			result = new ResultCode();
			result.parseTTBJson(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询订单情况
	 * 
	 * @param appid
	 * @param orderid
	 * @return
	 */
	public String searchOrderid(String appid, String orderid) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", appid);
			json.put("b", orderid);

			InputStream request = doRequest(Constants.URL_STATE_ORDER_SERCH,
					json.toString());

			return unzip(request);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 平台币支付
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode changeTTB(String type, int amount, String imeil,
			String appid, String agent, String username, String roleid,
			String serverid, String gameid, String productname,
			String productdes, String attach, String sign) {

		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", imeil);
			json.put("d", appid);
			json.put("e", agent);
			json.put("f", username);
			json.put("g", roleid);
			json.put("h", serverid);
			json.put("j", gameid);
			json.put("k", productname);
			json.put("l", productdes);
			json.put("m", attach);
			json.put("z", sign);
			
			InputStream request = doRequest(Constants.URL_USER_CHAGETTB,
					json.toString());
			json = new JSONObject(unzip(request));

			result = new ResultCode();
			result.parseTTBJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * imeil查询用户信息
	 * 
	 * @param appid
	 * @param imeil
	 * @return
	 */
	public String searchLoginUserinfoByImel(String appid, String imeil) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", appid);
			json.put("b", imeil);
			
			InputStream request = doRequest(Constants.URL_IMSI_USERINFO,
					json.toString());
			//Logger.msg("searchLoginUserinfoByImel = :"+parseIs2Str(request));
			String str=unzip(request);
			
			return str;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 查询订单信息
	 * 
	 * @param username
	 * @param type
	 * @param appid
	 * @return
	 */
	public String searchOrderByUsername(String username, String type,
			String appid, int page) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", username);
			json.put("c", type);
			json.put("b", appid);
			json.put("d", page);
			InputStream request = doRequest(Constants.URL_ORDER_SEARCH,
					json.toString());
			return unzip(request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取客服qq与电话号码
	 */
	public void getTelAndQQ() {
		try {
			Logger.msg("获取qq与tel");
			JSONObject json = new JSONObject();
			json.put("a", YTAppService.appid);

			InputStream request = doRequest(Constants.URL_GETSERVICE_TELANDQQ,
					json.toString());
			String str = unzip(request);
			if(null != str){
				JSONObject jo = new JSONObject(str);
				String tel_qq_str = jo.getString("data");
				jo = new JSONObject(tel_qq_str);
				YTAppService.service_tel = jo.isNull("a") ? "4007169039" : jo
						.getString("a");
				YTAppService.service_qq = jo.isNull("b") ? "1715790647" : jo
						.getString("b");
				YTAppService.ttbrate = jo.isNull("c") ? 10 : jo
						.getInt("c");
				YTAppService.notice = jo.isNull("d") ? "" : jo
						.getString("d");
				YTAppService.isgift = jo.isNull("e") ? 0 :jo.getInt("e");
			}
		} catch (Exception e) {
			YTAppService.service_tel = "4007169039";
			YTAppService.service_qq = "1715790647";
			e.printStackTrace();
		}
	}
		/**
	    * 根据infoid获取礼包码
	    * @param infoid
	    * @return
	    */
	   public String getCodeByinfoid(String appid,String infoid){
		  
			try {
				JSONObject json = new JSONObject();
				json.put("a", YTAppService.appid);
				json.put("b", infoid);
				//json.put("c", gamename);
				
				InputStream is=doRequest(Constants.URL_GET_GAMEGIFT,json.toString());
				
				JSONObject jo = new JSONObject(unzip(is));
				//Logger.msg("dddw"+jo.getString("msg").toString());
				String data_str = jo.getString("data");
				
				return data_str;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		   
		   return null;
	   }
	   
	   /**
	    * 获取游戏礼包条目信息
	    * @return
	    */
	   public  List<GameGiftItem> getGameGiftItems(){
			try {
			   JSONObject json = new JSONObject();
			   json.put("a", YTAppService.appid);
			   List<GameGiftItem> gfs;
			   InputStream is=doRequest(Constants.URL_GAMEGIFT_ITEM,json.toString());
			   JSONObject jo = new JSONObject(unzip(is));
			   String data_str = jo.getString("data");
			  // jo = new JSONObject(data_str);
				
				if(null!=data_str){
					   gfs=new ArrayList<GameGiftItem>();
					   GameGiftItem[] gs=(GameGiftItem[])JsonUtil.parseJson2ArrayNoShotName(GameGiftItem.class,data_str);
					      if(null!=gs){
					    	  gfs=Arrays.asList(gs);
					    	  return gfs;
					      }
				   }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		   return null;
	   }
	   
	   /**
	    * 根据gameid获取游戏的详细信息
	    * @param gameid
	    * @return
	    */
	   public GameDetail getGameDetail(String appid,String gameid){
		   if(TextUtils.isEmpty(gameid)){
			   return null;
		   }
		   
		try {
			JSONObject json = new JSONObject();
			json.put("a", appid);
			json.put("b", gameid);
			InputStream is=doRequest(Constants.URL_GAMEDTAIL_MSG,json.toString());
			JSONObject jo = new JSONObject(unzip(is));
			String data_str = jo.getString("data");

		      return (GameDetail)JsonUtil.parseJson2ObjectNoShotName(GameDetail.class,data_str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		   return null;
	   }

	/**
	 * 用户退出登录
	 */
	public ResultCode loginOut(String jasonStr) {
		Logger.msg("loginOut = :"+"退出");
		InputStream request = doRequest(Constants.URL_USER_LOGIN_OUT, jasonStr);
		ResultCode result = new ResultCode();

		try {
			// Logger.msg("loginOut = :"+unzip(request));
			String str = unzip(request);
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.loginoutJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**************************************************************************************************************************/
	/**
	 * 负责发送请求与请求后获取数据(加密压缩发送)
	 * 
	 * @param url
	 *            请求地址
	 * @param str
	 *            请求携带的参数
	 * @return inputstream服务端返回的输入流
	 */
	public InputStream doRequest(String url, String str) {
		HttpClient client = NetworkImpl.getHttpClient(ctx);
		if (null == client) {
			return null;
		}
		HttpPost post = new HttpPost(url);

		post.setHeader("content-type", "text/html");
		//Logger.msg("request url and data:" + url + "   data:" + str);
		if (str != null) {
			str = Encrypt.encode(str);

			HttpEntity entity = new ByteArrayEntity(compress(str.getBytes()));
			post.setEntity(entity);

		}
		int count = 0;
		// 等待3秒在请求2次
		while (count < 2) {
			try {
				HttpResponse response = client.execute(post);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					
					return response.getEntity().getContent();
				}
			} catch (ClientProtocolException e) {
				Logger.msg("网络连接异常");
				e.printStackTrace();
			} catch (IOException e) {
				Logger.msg("网络连接异常");
				e.printStackTrace();
			}
			count++;
			try {
				// 请求失败，在请求一次
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				Logger.msg("网络连接异常");
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	    * 负责发送请求与请求后获取数据(无加密压缩发送)
	    * @param url 请求地址
	    * @param str 请求携带的参数
	    * @return inputstream服务端返回的输入流
	    */
	   public InputStream doRequesttwo(String url,String str){
		   HttpClient client=NetworkImpl.getHttpClient(ctx);
		   if(null==client){
			   return null;
		   }
		   
		   HttpPost post=new HttpPost(url);
		   post.setHeader("content-type", "text/html");
		   //Logger.msg("request url and data:"+url+"   data:"+str);
		   if(str!=null){
		     HttpEntity entity=new ByteArrayEntity(str.getBytes());
		     post.setEntity(entity);
		   }
		   int count=0;
		   //等待3秒在请求2次
		   while(count<2){
			  try {
			     HttpResponse response= client.execute(post);
			     if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
	               return response.getEntity().getContent();			  
			     }
		      } catch (ClientProtocolException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				} catch (IOException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				}
		     count++;
		     try {
				Thread.currentThread().sleep(3000);
			    } catch (InterruptedException e) {
			    	Logger.msg("网络连接异常");
			    	e.printStackTrace();
			    }
		   }
		   return null;
	   }

	/**
	 * 解析服务端返回过来的输入流
	 * 
	 * @param is
	 * @return
	 */
	public String parseIs2Str(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			String str = new String(bos.toByteArray());
			//Logger.msg("service response data:" + str);
			return str;
		} catch (ClientProtocolException e) {
			Logger.msg("网络连接异常");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.msg("网络连接异常");
			e.printStackTrace();
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 解析服务端返回过来的输入流(无解压)
	 * 
	 * @param is
	 * @return
	 */
	public String parseIs3Str(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			String str = new String(bos.toByteArray());
			String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll("");
	        }
			//Logger.msg("service response data:" + dest);
			//Logger.msg("service response data:--------" + Encrypt.decode2(dest));
			return Encrypt.decode2(dest);
		} catch (IOException e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} catch (NullPointerException e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} catch (Exception e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					Logger.msg("数据获取异常");
					e.printStackTrace();
					//return null;
				}
			}
		}
		return null;
	}

	/**
	 * 数据压缩
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] compress(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 压缩
			compress(bais, baos);

			byte[] output = baos.toByteArray();

			baos.flush();
			baos.close();
			bais.close();

			return output;
		} catch (Exception e) {
			Logger.msg("数据压缩异常！");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 数据压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static void compress(InputStream is, OutputStream os)
			throws Exception {
		GZIPOutputStream gos = new GZIPOutputStream(os);
		int count;
		byte data[] = new byte[1024];
		while ((count = is.read(data, 0, data.length)) != -1) {
			gos.write(data, 0, count);
		}
		// gos.flush();
		gos.finish();
		gos.close();
	}

	/**
	 * 数据解压
	 */
	public static String unzip(InputStream in) {
		// Open the compressed stream

		GZIPInputStream gin;
		try {
			if (in == null) {
				Logger.msg("没有输入流========");
				return null;
			}
			gin = new GZIPInputStream(new BufferedInputStream(in));
			Log.i("kd", "文件解压中1。。。");
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// Transfer bytes from the compressed stream to the output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = gin.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			//Logger.msg("文件解压中2。。。");
			// Close the file and stream
			gin.close();
			out.close();
			String str_result = new String(out.toByteArray());
			//Logger.msg("service back data:" + str_result);
			//Logger.msg("service Encrypt data:" + Encrypt.decode(str_result));
			return Encrypt.decode(str_result);
		} catch (IOException e) {
			Logger.msg("解压文件异常:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
