package com.game.sdk.floatwindow;


import com.game.sdk.YTAppService;
import com.game.sdk.domain.CloseWindowJavaScriptInterface;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.Md5Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatWebActivity extends Activity implements
		OnClickListener {
	private static final String TAG = "FloatWebActivity";
	private WebView wv;
	private TextView tv_back,tv_charge_title;
	private ImageView iv_cancel;
	private String url,title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"sdk_float_web"));

//		findViewById(MResource.getIdByName(getApplication(), "id", "iv_ingame"))
//				.setOnClickListener(this);// 进入游戏
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");

		wv = (WebView) findViewById(MResource.getIdByName(getApplication(),
				"id", "wv_content"));
		tv_back = (TextView) findViewById(MResource.getIdByName(
				getApplication(), "id", "tv_back"));
		iv_cancel = (ImageView) findViewById(MResource.getIdByName(
				getApplication(), "id", "iv_cancel"));
		
		tv_charge_title = (TextView) findViewById(MResource.getIdByName(getApplication(),
				"id", "tv_charge_title"));
		tv_charge_title.setText(title);
		tv_back.setOnClickListener(this);
		iv_cancel.setOnClickListener(this);

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.getSettings().setAppCacheEnabled(false);
		wv.getSettings().setDomStorageEnabled(true);
		CloseWindowJavaScriptInterface closejs = new CloseWindowJavaScriptInterface();
		closejs.ctx = this;
		wv.addJavascriptInterface(closejs, "ttw_w");

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!DialogUtil.isShowing()) {
					DialogUtil.showDialog(FloatWebActivity.this,
							"正在加载...");
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
		//String time = String.valueOf(System.currentTimeMillis());
		//String timeStr = time.substring(0, time.length()-3);
		
		//String sign = "username="+TTWAppService.userinfo.username+"&appkey="+appkey+"&logintime="+timeStr;
		//sign = Md5Util.md5(sign);
		
		//url = url+"?gameid="+1+"&username="+TTWAppService.userinfo.username
			//	+"&logintime="+timeStr+"&sign="+sign;
		
		wv.loadUrl(url);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == tv_back.getId()) {
			if(!wv.canGoBack()){
				if(title.equals("忘记密码")){
					FloatViewImpl.getInstance(this);
					FloatViewImpl.removeFloat();
					this.finish();
				}else{
					FloatViewImpl.getInstance(this);
					FloatViewImpl.ShowFloat();
					this.finish();
				}
			}else{
				wv.goBack();// 返回前一个页面
			}
		}
		if (v.getId() == iv_cancel.getId()) {
			if(title.equals("忘记密码")){
				FloatViewImpl.getInstance(this);
				FloatViewImpl.removeFloat();
				this.finish();
			}else{
				FloatViewImpl.getInstance(this);
				FloatViewImpl.ShowFloat();
				this.finish();
			}
		}

	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
				wv.goBack();// 返回前一个页面
			   return true;
		}
		if(title.equals("忘记密码")){
			FloatViewImpl.getInstance(this);
			FloatViewImpl.removeFloat();
			return super.onKeyUp(keyCode, event);
		}else{
			FloatViewImpl.getInstance(this);
			FloatViewImpl.ShowFloat();
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.finish();
	}
	

}
