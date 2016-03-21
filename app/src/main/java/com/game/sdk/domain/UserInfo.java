package com.game.sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.YTAppService;

public class UserInfo implements JsonParseInterface {
	public String username;//
	public String password;//
	public String newpassword;//
	public int isrpwd = 0;// 0已经修改过密码，1表示没有修改过密码

	public int device = 2;// 1为pc端，2为Android端 3为ios
	public String imeil;//
	public String deviceinfo;//
	public String agent;//
	public String sendcode="";
	public int  issend=0;

	@Override
	public String getShotName() {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject buildJson() {
		JSONObject jsonStr = new JSONObject();
		try {
			jsonStr = new JSONObject();
			jsonStr.put("a", username);
			jsonStr.put("b", password);
			jsonStr.put("c", device);
			jsonStr.put("d", YTAppService.gameid);
			jsonStr.put("e", imeil);
			jsonStr.put("f", agent);
			jsonStr.put("g", YTAppService.appid);
			jsonStr.put("h", deviceinfo);
			jsonStr.put("i", issend);
			jsonStr.put("j", sendcode);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonStr;
	}

	/**
	 * @return
	 */
	public JSONObject deviceInfoToJson() {
		JSONObject jsonStr = new JSONObject();
		try {
			jsonStr = new JSONObject();
			jsonStr.put("a", YTAppService.appid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonStr;
	}

	/**
	 * 退出
	 * 
	 * @return
	 */
	public JSONObject outInfoToJson() {
		JSONObject jsonStr = new JSONObject();
		try {
			jsonStr = new JSONObject();
			jsonStr.put("a", username);
			jsonStr.put("b", device);
			jsonStr.put("c", YTAppService.gameid);
			jsonStr.put("d", imeil);
			jsonStr.put("e", agent);
			jsonStr.put("f", YTAppService.appid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonStr;
	}

	@Override
	public void parseJson(JSONObject json) {
		// TODO Auto-generated method stub

	}
}
