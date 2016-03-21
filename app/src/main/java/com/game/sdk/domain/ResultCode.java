package com.game.sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.util.Logger;

public class ResultCode implements JsonParseInterface {
	public int code;
	public String data;
	public String orderid;
	public String username;
	public String password;
	public String sign;
	public long logintime;
	public String msg;
	public String ptbkey;
	public int yxb;
	public int ptb;


	public String url;// 充值时 第三方返回来的url

	@Override
	public String getShotName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject buildJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			password = json.isNull("c") ? "" : json.getString("c");
			sign = json.isNull("d") ? "" : json.getString("d");
			if("".equals(json.getString("e"))){
				logintime = 0;
			}else{
				logintime = json.isNull("e") ? 0 : json.getLong("e");
			}
			msg = json.isNull("f") ? "" : json.getString("f");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loginoutJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			
			msg = json.isNull("b") ? "" : json.getString("b");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void parseYXBJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			ptb = json.isNull("b") ? 0 : json.getInt("b");
			yxb = json.isNull("c") ? 0 : json.getInt("c");
			msg = json.isNull("d") ? "" : json.getString("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 现在支付返回参数
	 * 
	 * @param json
	 */
	public void parseNowPayJson(JSONObject json){
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void regJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			password = json.isNull("c") ? "" : json.getString("c");
			msg = json.isNull("g") ? "" : json.getString("g");
			Logger.msg("kadaj=====sss=="+msg);
			sign = json.isNull("d") ? "" : json.getString("d");
			ptbkey =json.isNull("f") ? "" :json.getString("f");
			if("".equals(json.getString("e"))){
				logintime = 0;
			}else{
				logintime = json.isNull("e") ? 0 : json.getLong("e");
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void oneregJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void parseTTBJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			orderid = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseAlipayJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
			//msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseTTBTwoJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			data = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 财付通返回参数
	 * 
	 * @param json
	 */
	public void parseCFTJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			url = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");
			orderid = json.isNull("d") ? "" : json.getString("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 易联返回参数
	 * 
	 * @param json
	 */
	public void parseECOJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("code") ? 0 : json.getInt("code");
			data = json.isNull("data") ? "" : json.getString("data");
			msg = json.isNull("msg") ? "" : json.getString("msg");
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 订单状态查询返回参数
	 * 
	 * @param json
	 */
	public void parseOrderidJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
