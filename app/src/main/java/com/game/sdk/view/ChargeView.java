package com.game.sdk.view;

import java.util.HashMap;
import java.util.Map;


import com.game.sdk.YTAppService;

import com.game.sdk.domain.ChannelMessage;
import com.game.sdk.domain.OnChargerListener;

import com.game.sdk.util.Constants;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.NetworkImpl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChargeView extends BaseView {
	
	private InputMethodManager im;
	private Activity activityt;
	private Context ctx;
	private GridView gv_pay_sort;
	private TextView tv_username,tv_money,tv_back;
	private ImageView iv_ingame;
	private Button btn_goto_pay;
	private GV_Adapter gv_adapter;
	private int channelmoney;
	public String checkpay="checkpay";
	public static boolean ischarge = false; // 用来标记是否是自定义注册 调整到登录页面的
	
	
	public ChargeView (FragmentActivity activity, OnChargerListener oncharger){
		
		im = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		this.activityt = activity;
		ctx = activity.getApplicationContext();
		channelmoney=activity.getIntent().getIntExtra("money", 0);
		this.inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.content_view = inflater.inflate(MResource.getIdByName(ctx,
				Constants.Resouce.LAYOUT, "sdk_charge_two"), null);

		gv_pay_sort=(GridView) content_view
				.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID,
						"gv_pay_sort"));
		
		tv_back = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_back"));
		iv_ingame = (ImageView) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "iv_ingame"));
		btn_goto_pay=(Button)content_view.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "btn_goto_pay"));
		tv_money=(TextView)content_view.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "tv_money"));
		tv_username=(TextView)content_view.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "tv_username"));
		tv_username.setText(YTAppService.userinfo.username);
		tv_money.setText(channelmoney+"元");
		gv_adapter=new GV_Adapter();
		gv_pay_sort.setAdapter(gv_adapter);
		gv_pay_sort.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//view.findViewById(R.id.img_check_pay).setVisibility(View.VISIBLE);
				checkpay=YTAppService.channels.get(position).type;
				
				
				gv_adapter.notifyDataSetChanged();
			}
		
		
		});
		
		
		
		
	}
	private void onitemclick(final int position, boolean flag) {
		
		if(!NetworkImpl.isNetWorkConneted(ctx)){
			Toast.makeText(ctx, "网络连接错误，请检查网络连接状态！", Toast.LENGTH_SHORT).show();
			return;
		}
		ChannelMessage channels=YTAppService.channels.get(position);
	}
	

	public void setBackOnlist(OnClickListener onclick) {
		tv_back.setOnClickListener(onclick);
		iv_ingame.setOnClickListener(onclick);
		btn_goto_pay.setOnClickListener(onclick);
	}
	private class GV_Adapter extends BaseAdapter{

		private LayoutInflater inflater;
		
		public GV_Adapter(){
			inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return YTAppService.channels==null?0:YTAppService.channels.size();
		}

		@Override
		public Object getItem(int position) {
			
			return YTAppService.channels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null==convertView){
				convertView=inflater.inflate(MResource.getIdByName(ctx, Constants.Resouce.LAYOUT, "sdk_charge_item"),null);
				View_hold_sort vh=new View_hold_sort();
				
				vh.img_alipay=(ImageView)convertView.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "img_alipay"));
				vh.img_check_pay=(ImageView)convertView.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "img_check_pay"));
				vh.tv_pay_type=(TextView) convertView.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID, "tv_pay_type"));
				
				convertView.setTag(vh);
				
			}
			final View_hold_sort vh=(View_hold_sort) convertView.getTag();
			ChannelMessage channel = YTAppService.channels.get(position);
			Resources resources = ctx.getResources();   
//			Drawable btnDrawable = resources.getDrawable(R.drawable.layout_bg); 
			vh.tv_pay_type.setText(channel.channelDes);
			vh.img_check_pay.setVisibility(View.GONE);
			
			if (channel.type.equals("ptb")) {
				if (checkpay.equals("ptb")) {
					vh.img_check_pay.setVisibility(View.VISIBLE);
				}
				vh.img_alipay.setBackgroundDrawable(resources.getDrawable(MResource.getIdByName(ctx, Constants.Resouce.DRAWABLE, "sdk_ptb")));
			}else if (channel.type.equals("alipay")) {
				if (checkpay.equals("alipay")) {
					vh.img_check_pay.setVisibility(View.VISIBLE);
				}
				vh.img_alipay.setBackgroundDrawable(resources.getDrawable(MResource.getIdByName(ctx, Constants.Resouce.DRAWABLE, "sdk_alipay")));
			}else if(channel.type.equals("tclpay")){
				if (checkpay.equals("tclpay")) {
					vh.img_check_pay.setVisibility(View.VISIBLE);
				}
				vh.img_alipay.setBackgroundDrawable(resources.getDrawable(MResource.getIdByName(ctx, Constants.Resouce.DRAWABLE, "sdk_tclpay")));
			}else if(channel.type.equals("nowpay")){
				if (checkpay.equals("nowpay")) {
					vh.img_check_pay.setVisibility(View.VISIBLE);
				}
				vh.img_alipay.setBackgroundDrawable(resources.getDrawable(MResource.getIdByName(ctx, Constants.Resouce.DRAWABLE, "sdk_wxpay")));
			}else if(channel.type.equals("gamepay")){
				if (checkpay.equals("gamepay")) {
					vh.img_check_pay.setVisibility(View.VISIBLE);
				}
				vh.img_alipay.setBackgroundDrawable(resources.getDrawable(MResource.getIdByName(ctx, Constants.Resouce.DRAWABLE, "gamepay")));
			}
			
			;
		
			return convertView;
		}
		
	} 

    static class View_hold_sort{
    	private ImageView img_alipay,img_check_pay;
    	private TextView  tv_pay_type;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		
		
		return content_view;
	}

}
