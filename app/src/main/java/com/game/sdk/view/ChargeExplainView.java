package com.game.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.MResource;

public class ChargeExplainView extends BaseView {
	public static Context acontext;

	private ImageView iv_ingame;
	private TextView tv_charge_title, tv_back;
	private WebView wv_content;

	public ChargeExplainView(Context ctx) {
		acontext = ctx;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content_view = inflater.inflate(MResource.getIdByName(ctx,
				Constants.Resouce.LAYOUT, "ttw_user_agreement"), null);

		tv_charge_title = (TextView) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "tv_charge_title"));
		tv_back = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_back"));
		iv_ingame = (ImageView) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "iv_ingame"));
		wv_content = (WebView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "wv_content"));

		tv_charge_title.setText("充值说明");
		iv_ingame.setVisibility(View.GONE);

		wv_content.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!DialogUtil.isShowing()) {
					DialogUtil.showDialog(acontext, "正在加载...");
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				try {
					DialogUtil.dismissDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		wv_content.loadUrl(Constants.URL_USER_EXPLAIN);
	}

	public void setBackOnclik(OnClickListener onclick) {
		tv_back.setOnClickListener(onclick);
	}

	@Override
	public View getContentView() {
		return content_view;
	}

	@Override
	public void onClick(View v) {

	}
}
