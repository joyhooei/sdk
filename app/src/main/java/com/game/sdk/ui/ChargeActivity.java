package com.game.sdk.ui;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.domain.OnChargerListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.util.ActivityTaskManager;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.view.ChargeExplainView;
import com.game.sdk.view.ChargeView;
import com.game.sdk.view.OrderSearchView;
import com.game.sdk.view.kefuView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * author janecer 2014年7月23日下午2:08:56
 */
public class ChargeActivity extends BaseActivity {
	public static OnChargerListener Chargerlistener;
	public static OnPaymentListener paymentListener;// 充值接口监听
	private InputMethodManager im;
	private ChargeMenuSelectAdapter adapter;
	private List<String> menus = new ArrayList<String>();
	// private boolean isPortrait;//是否是竖版
	// 声明PopupWindow对象的引用
	private PopupWindow popupWindow;
	private ChargeView charge ;
	
	private int charge_money;// 需要充值的金额
	private String serverid;// 充值的服务器id；
	private String productname;// 充值游戏名称
	private String productdesc;// 产品描述
	private String fcallbackurl;// 充值回调地址，由游戏方传递
	private String roleid;// 角色id；
	private String attach;// 游戏方传递的拓展参数

	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == MResource.getIdByName(ChargeActivity.this,
					Constants.Resouce.ID, "tv_back")) {
				
				if(!isTop()){
					popViewFromStack();
				}else{
					popViewFromStack();
					if(!ChargeView.ischarge){
						PaymentErrorMsg msg_e = new PaymentErrorMsg();
						msg_e.code = 2;
						msg_e.msg = "支付返回";
						msg_e.money = 0;
						paymentListener.paymentError(msg_e);
					}
				}
				return;
			}

			if (v.getId() == MResource.getIdByName(ChargeActivity.this,
					Constants.Resouce.ID, "iv_ingame")) {
				// TODO Auto-generated method stub
				// isPortrait=(ChargeActivity.this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT);
				if (menus.size() <= 0) {
					menus.add("充值记录");
					menus.add("联系客服");
					menus.add("充值说明");
					// menus.add("账户密码");
				}
				showPopupWindow(v);
				return;
			}
			if (v.getId() == MResource.getIdByName(ChargeActivity.this, 
					Constants.Resouce.ID, "btn_goto_pay")) {
				String paytype=charge.checkpay;
				if (paytype.equals("checkpay")) {
					Toast.makeText(ChargeActivity.this, "请选择支付方式！",
							Toast.LENGTH_SHORT).show();
					return ;
				}
				// 支付宝支付
				if (paytype.equals("alipay")) {
					Intent pay_int = new Intent(ChargeActivity.this, AlipayActivity.class);
					
					pay_int.putExtra("roleid", roleid);
					pay_int.putExtra("money", charge_money);
					pay_int.putExtra("serverid", serverid);
					pay_int.putExtra("productname", productname);
					pay_int.putExtra("productdesc", productdesc);
					pay_int.putExtra("fcallbackurl", "");
					pay_int.putExtra("attach", attach);
					pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(pay_int);
				}
				if (paytype.equals("ptb")) {
					Intent pay_int = new Intent(ChargeActivity.this, PtbActivity.class);
					
					pay_int.putExtra("roleid", roleid);
					pay_int.putExtra("money", charge_money);
					pay_int.putExtra("serverid", serverid);
					pay_int.putExtra("productname", productname);
					pay_int.putExtra("productdesc", productdesc);
					pay_int.putExtra("fcallbackurl", "");
					pay_int.putExtra("attach", attach);
					pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(pay_int);
				}
				if (paytype.equals("gamepay")) {
					Intent pay_int = new Intent(ChargeActivity.this, GameCurrencyActivity.class);
					
					pay_int.putExtra("roleid", roleid);
					pay_int.putExtra("money", charge_money);
					pay_int.putExtra("serverid", serverid);
					pay_int.putExtra("productname", productname);
					pay_int.putExtra("productdesc", productdesc);
					pay_int.putExtra("fcallbackurl", "");
					pay_int.putExtra("attach", attach);
					pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(pay_int);
				}
				if(paytype.equals("nowpay")){
					
					Intent pay_int = new Intent(ChargeActivity.this, NowPayActivity.class);
					
					pay_int.putExtra("roleid", roleid);
					pay_int.putExtra("money", charge_money);
					pay_int.putExtra("serverid", serverid);
					pay_int.putExtra("productname", productname);
					pay_int.putExtra("productdesc", productdesc);
					pay_int.putExtra("fcallbackurl", "");
					pay_int.putExtra("attach", attach);
					pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(pay_int);
				}
		
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		roleid = intent.getStringExtra("roleid");
		serverid = intent.getStringExtra("serverid");
		charge_money = intent.getIntExtra("money", 0);
		productname = intent.getStringExtra("productname");
		productdesc = intent.getStringExtra("productdesc");
		fcallbackurl = intent.getStringExtra("fcallbackurl");
		attach = intent.getStringExtra("attach");
		ActivityTaskManager.getInstance().putActivity("ChargeActivity", ChargeActivity.this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		charge = new ChargeView(this, Chargerlistener);
		charge.setBackOnlist(onclick);
		pushView2Stack(charge.getContentView());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void showPopupWindow(View parent) {
		
		if (null == adapter) {
			adapter = new ChargeMenuSelectAdapter();
		}
		;

		View view = getLayoutInflater().inflate(
				MResource.getIdByName(this, "layout", "ttw_menu_list"), null);
		ListView lv_menu = (ListView) view.findViewById(MResource.getIdByName(
				this, "id", "lv_menu"));
		popupWindow = new PopupWindow(view, DimensionUtil.dip2px(this, 100),
				LinearLayout.LayoutParams.WRAP_CONTENT, true);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int xPos = -popupWindow.getWidth() + DimensionUtil.dip2px(this, 20 / 2);

		popupWindow.showAsDropDown(parent, xPos, 0);

		lv_menu.setCacheColorHint(0x00000000);
		lv_menu.setAdapter(adapter);
		lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long row) {
				String name = menus.get(position);
				popupWindow.dismiss();
				if ("充值记录".equals(name)) {
					// 账户 订单查询
					OrderSearchView order = new OrderSearchView(
							ChargeActivity.this);
					pushView2Stack(order.getContentView());

					order.setBackOnlist(onclick);
					return;
				}
				if ("联系客服".equals(name)) {
					kefuView kefu = new kefuView(
							ChargeActivity.this);
					pushView2Stack(kefu.getContentView());
					kefu.setBackOnclik(onclick);
					return;
				}
				if ("充值说明".equals(name)) {
					ChargeExplainView explain = new ChargeExplainView(
							ChargeActivity.this);
					pushView2Stack(explain.getContentView());

					explain.setBackOnclik(onclick);
					return;
				}
				// et_money.setText(money);
			}
		});
		// popupWindow = new PopupWindow(view,
		// xPos,LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// popupWindow.setContentView(view);
	}

	/**
	 * popupwindow显示设配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class ChargeMenuSelectAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menus.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return menus.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (null == convertView) {
				View view = getLayoutInflater().inflate(
						MResource.getIdByName(ChargeActivity.this, "layout",
								"ttw_menu_list_item"), null);

				convertView = view;
			}
			TextView tv = (TextView) convertView.findViewById(MResource
					.getIdByName(ChargeActivity.this, "id", "tv_menuname"));

			tv.setText(menus.get(position));
			return convertView;
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if(!ChargeView.ischarge){
//			PaymentErrorMsg msg_e = new PaymentErrorMsg();
//			msg_e.code = 2;
//			msg_e.msg = "支付未成功";
//			msg_e.money = 0;
//			paymentListener.paymentError(msg_e);
//		}
		return super.onKeyUp(keyCode, event);
	}
	

}
