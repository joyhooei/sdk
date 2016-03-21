package com.game.sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.util.MResource;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EcoResultActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"ttw_order_dealing"));
		String mPayResult =  getIntent().getExtras().getString("result");
		LinearLayout ll = (LinearLayout) findViewById(MResource.getIdByName(getApplication(), "id", "ll_result_content"));
		t: try {
			JSONObject json = new JSONObject(mPayResult);
			
			if (json.has("respCode")) {
				
				String respCode = json.getString("respCode");
				if ("W101".equals(respCode)) { //W101订单未支付，用户主动退出插件
					finish();
					break t;
				}
				
				if (!"0000".equals(respCode)) { //非0000，订单支付响应异常
					String respDesc = json.getString("respDesc");
					ll.addView(getRow("错误码", respCode));
					ll.addView(getRow("错误消息", respDesc));
					break t;
				}
			}
			
			if(json.has("OrderId")){
				ll.addView(getRow("订单号", json.getString("OrderId")));
			}
			if(json.has("Amount")){
				ll.addView(getRow("金额", json.getString("Amount")+"元"));
			}
			if(json.has("PayTime")){
				ll.addView(getRow("交易时间", json.getString("PayTime")));
			}
			if(json.has("Status")){
				String status = "";
				if ("01".equals(json.getString("Status"))) {
					status = "未支付";
				}
				if ("02".equals(json.getString("Status"))) {
					status = "已支付";
				}
				if ("03".equals(json.getString("Status"))) {
					status = "已退款(全额撤销/冲正)";
				}
				if ("04".equals(json.getString("Status"))) {
					status = "已过期";
				}
				if ("05".equals(json.getString("Status"))) {
					status = "已作废";
				}
				if ("06".equals(json.getString("Status"))) {
					status = "支付中";
				}
				if ("07".equals(json.getString("Status"))) {
					status = "退款中";
				}
				if ("08".equals(json.getString("Status"))) {
					status = "已被商户撤销";
				}
				if ("09".equals(json.getString("Status"))) {
					status = "已被持卡人撤销";
				}
				if ("10".equals(json.getString("Status"))) {
					status = "调账-支付成功";
				}
				if ("11".equals(json.getString("Status"))) {
					status = "调账-退款成功";
				}
				if ("12".equals(json.getString("Status"))) {
					status = "已退货";
				}
				ll.addView(getRow("交易状态", status));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/*TextView payResult_payState_edit = (TextView) findViewById(R.id.payResult_payState_edit);
		payResult_payState_edit.setText(mPayResult);*/
	}
	
	public void back(View view){
		finish();
	}
	
    public View getRow(String title,String content){
	   LinearLayout ll = new LinearLayout(this);
	   LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(-1,-2);
	   param.topMargin = 25;
	   ll.setLayoutParams(param);
	   ll.setOrientation(LinearLayout.HORIZONTAL);
	   
	   TextView  title_v = new TextView(this);
	   LinearLayout.LayoutParams param_t = new  LinearLayout.LayoutParams(0,-2,1);
	   title_v.setText(title+"：");
	   title_v.setTextSize(16.0f);
	   title_v.setLayoutParams(param_t);
//	   title_v.setTextColor(Color.BLACK);
	   
	   LinearLayout.LayoutParams param_c = new  LinearLayout.LayoutParams(0,-2,2);
	   TextView content_v = new TextView(this);
	   content_v.setLayoutParams(param_c);
	   content_v.setText(content);
	   content_v.setTextSize(18.0f);
	   content_v.setTextColor(Color.BLACK);
	   
	   ll.addView(title_v);
	   ll.addView(content_v);
	   return ll;
   }
}