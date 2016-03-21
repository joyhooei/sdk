package com.game.sdk.view;

import com.game.sdk.YTAppService;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.MResource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class kefuView extends BaseView {
	public static Context context;

	private ImageView iv_ingame;
	private TextView tv_back,tv_service_qq,tv_service_tel;
	
	public kefuView(Context ctx) {
		context=ctx;
		
		this.inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.content_view=inflater.inflate(MResource.getIdByName(ctx, Constants.Resouce.LAYOUT,"ttw_kefu"),null);
		tv_back = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_back"));
		iv_ingame = (ImageView) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "iv_ingame"));
		iv_ingame.setVisibility(View.GONE);
		tv_service_qq = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_service_qq"));
		tv_service_tel = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_service_tel"));
		
		tv_service_qq.setText(YTAppService.service_qq);
		tv_service_tel.setText(YTAppService.service_tel);
	}

	public void setBackOnclik(OnClickListener onclick) {
		tv_back.setOnClickListener(onclick);
		iv_ingame.setOnClickListener(onclick);
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
