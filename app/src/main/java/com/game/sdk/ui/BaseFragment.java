package com.game.sdk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * author janecer 2014年7月24日下午3:59:02
 */
public class BaseFragment extends Fragment {

	protected Context ctx;
	protected EditText et_money;
	protected TextView tv_pay;
	protected View contentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
