package com.game.sdk.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * author janecer 2014年7月21日下午5:04:09
 */
public abstract class BaseView implements OnClickListener {

	protected View content_view;
	protected LayoutInflater inflater;

	/**
	 * 获得view视图
	 * 
	 * @return
	 */
	public abstract View getContentView();

}
