package com.game.sdk.ui;

import java.util.Stack;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * author janecer 2014年7月22日上午9:53:03
 */
public class BaseActivity extends FragmentActivity {

	protected Stack<View> mStackView = new Stack<View>();

	/**
	 * 将view放进任务栈顶，并在activity中显示view
	 * 
	 * @param view
	 */
	protected void pushView2Stack(View view) {
		if (mStackView.size() > 0) {
			View old_view = mStackView.peek();
			old_view.clearFocus();
			old_view = null;
		}
		mStackView.push(view);
		this.setContentView(view);
		view.requestFocus();
	};

	/**
	 * 将栈顶中的view弹出，并activity显示上一个view
	 */
	protected void popViewFromStack() {
		View view = null;
		if (mStackView.size() > 1) {
			View view_old = mStackView.pop();
			view_old.clearFocus();
			view_old = null;

			view = mStackView.peek();
			setContentView(view);
			view.requestFocus();
		} else {
			this.finish();
		}
	}
	
	/**
	 * 是否最顶层视图
	 * @return
	 */
	public Boolean isTop(){
		
		if (mStackView.size() > 1) {
			return false;
		}else{
			return true;
		}
	}
}
