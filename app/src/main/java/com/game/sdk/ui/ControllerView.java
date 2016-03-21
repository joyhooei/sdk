package com.game.sdk.ui;

import com.game.sdk.util.DimensionUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class ControllerView extends FrameLayout {

	/**
	 * 状态栏高度
	 **/
	public static int tool_bar_high = 0;
    public static boolean float_bar_isLeft=true;//浮标是在左边还是右边 false在右边，true在左边  用来判断菜单栏显示在浮标的左边还是右边
	/** 跟随监听 **/
	private LayoutChangeListener layoutListener;

	private Context mContext;
	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	private int startX = 0;
	private int startY = 0;
	WindowManager wm;
	public float x;
	public float y;

	/** 屏幕宽 **/
	private int window_width;

	/**按下去的时间*/
	public static long downTime;
	/** View宽 **/
//	private int width;

	/** 是否在左边 **/
//	private boolean isLeft = true;

	private boolean isShow = false;

	public ControllerView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		wm = (WindowManager) paramContext.getSystemService("window");
		init();
	}

	public ControllerView(Context context) {
		super(context);
		this.mContext = context;
		wm = (WindowManager)context.getSystemService("window");
		init();
	}

	private void init() {
//		setLeft(true);
		getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
//				width = ControllerView.this.getWidth();
				DisplayMetrics displayMetrics = new DisplayMetrics();
				wm.getDefaultDisplay().getMetrics(displayMetrics);
				window_width = displayMetrics.widthPixels;
				return true;
			}
		});
	}

	public void addToWindow(int paramInt1, int paramInt2) {
		params.width =  DimensionUtil.dip2px(getContext(), 65);
		params.height = DimensionUtil.dip2px(getContext(), 65);
		
//		params.width =  80;
//		params.height = 80;
		
		params.type = 2002;
		params.flags = 40;
		// params.gravity = 51;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.format = 1;
		params.x = paramInt1;
		params.y = paramInt2;
		wm.addView(this, this.params);
	}

	public void removeControllerView() {
		wm.removeView(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (!isShow) {
			this.x = event.getRawX();
			this.y = (event.getRawY() - tool_bar_high);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) event.getX();
				startY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				updatePosition(false);
				break;
			case MotionEvent.ACTION_UP:
				//记录最后一次退出的位置
				int width=DimensionUtil.getWidth(mContext);
				x=x<=(width-x)?0:width;
				float_bar_isLeft=x==0?true:false;
				updatePosition(true);
				SharedPreferences saveXY = mContext.getSharedPreferences("savaXY", Context.MODE_PRIVATE);
				Editor editor = saveXY.edit();
				if(this.x != 0 && this.y != 0){
					editor.putInt("x", params.x);
					editor.putInt("y", params.y);
					editor.commit();
				}
				this.startY = 0;
				this.startX = 0;
				break;
			}
		}
		downTime = event.getEventTime() - event.getDownTime() ;
		return super.onInterceptTouchEvent(event);
	}

	public void setLayoutChangeListener(
			LayoutChangeListener paramLayoutChangeListener) {
		this.layoutListener = paramLayoutChangeListener;
	}

	public void updatePosition(boolean up) {
	
		params.x = (int) (this.x - this.startX);
		// }
		params.y = (int) (this.y - this.startY);
		this.wm.updateViewLayout(this, this.params);
		if (this.layoutListener != null)
			this.layoutListener.layout(this, this.params.x, this.params.y);
	}

	// public int getParamsY() {
	// return params.y;
	// }
	public android.view.WindowManager.LayoutParams getWMParams() {
		return params;
	}
	public void setWMParams(WindowManager.LayoutParams layoutParams) {
		params = layoutParams;
		wm.updateViewLayout(this, params);
	}

	public static abstract interface LayoutChangeListener {
		public abstract void layout(ControllerView paramVG_ControllerView,
				int paramInt1, int paramInt2);
	}

	public void initStatusBaHeight(Context context) {
		if (context instanceof Activity) {
			int flag = ((Activity) context).getWindow().getAttributes().flags;

			if ((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
				tool_bar_high = 0;
			} else {
				try {
					Class<?> class1 = Class.forName("com.android.internal.R$dimen");
					Object obj = class1.newInstance();
					int j = Integer.parseInt(class1.getField("status_bar_height").get(obj).toString());
					tool_bar_high = context.getResources().getDimensionPixelSize(j);
				} catch (Exception e) {
				}
			}
		}

	}

//	public boolean isLeft() {
//		return isLeft;
//	}
//
//	public void setLeft(boolean isLeft) {
//		this.isLeft = isLeft;
//	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isShow() {
		return isShow;
	}

	public int getWindow_width() {
		return window_width;
	}
}