package com.game.sdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * @author janecer
 * @version 解决横向滑动ViewPager时，稍微上下偏移ViewPager就会失去焦点的方法
 */
public class MyNoFocusListView extends ListView {
	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	public MyNoFocusListView(Context context) {
		super(context);
	}

	public MyNoFocusListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
	}

	public MyNoFocusListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev)
				&& mGestureDetector.onTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY){
			if (distanceY != 0 && distanceX != 0){
			}
			if (Math.abs(distanceY) >= Math.abs(distanceX)) {
				return true;
			}
			return false;
		}
	}
}
