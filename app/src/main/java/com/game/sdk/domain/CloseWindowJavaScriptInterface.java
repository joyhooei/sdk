package com.game.sdk.domain;

import com.game.sdk.YTAppService;
import com.game.sdk.util.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.text.ClipboardManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * author janecer 2014年4月15日下午3:27:56
 */
public class CloseWindowJavaScriptInterface {

	private static final String TAG = "CloseWindowJavaScriptInterface";
	public Activity ctx;

	@JavascriptInterface
	public void goToGame() {
		Logger.msg("______________goToGame");
		ctx.finish();
		// TTWAppService.;
	}
	

	public void goToGift(String code) {
		
		Logger.msg("______________code"+ code);
		Logger.msg("kadaj++++"+android.os.Build.VERSION.SDK_INT);
		if (android.os.Build.VERSION.SDK_INT>11) {
			android.content.ClipboardManager mClipboard= (android.content.ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("simple text",code);
			mClipboard.setPrimaryClip(clip);
			Toast.makeText(ctx, "复制成功，请尽快使用", Toast.LENGTH_LONG).show();
		}else{
		    ClipboardManager Mclipboard=(ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
		
	    	Mclipboard.setText(code);
		
		    Toast.makeText(ctx, "复制成功，请尽快使用", Toast.LENGTH_LONG).show();
		}
		// TTWAppService.;
	}
}
