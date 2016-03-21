package com.game.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.game.sdk.floatwindow.FloatWebActivity;
import com.game.sdk.ui.ControllerView;
import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;


public class ControllerViewImpl implements OnClickListener {

	protected static final String TAG = "ControllerViewImpl";
	/**
	 * 悬浮view
	 **/
	private ControllerView controllerView;
	private Map<String, Bitmap> table = new HashMap<String, Bitmap>();

	private static ControllerViewImpl instance = null;
	private boolean isExist = false;
	private int density = 0;

	private Drawable drawable_left = null;
	private Drawable drawable_right = null;

	private Context mContext;
	private SharedPreferences sharedPreferences;
	private ImageView imageView;
	private RotateAnimation rotate;
	boolean isOne = true;
	private PopupWindow pw_menu_item;

	
	private Handler mHandler = new Handler();
//	{
//		public void handleMessage(android.os.Message msg) {
//			addMenuView(controllerView_Menu.getContext());
//			mHandler.postDelayed(runnable,5*1000);};
//	};
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//controllerView_Menu.setVisibility(View.GONE);
			controllerView.setShow(true);
		}
	};
	
	private OnClickListener iconOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			Logger.msg("浮标被点击了..");
			
			if(ControllerView.downTime > 200 )
				return;
			if (!controllerView.isShow()) {

				// 关闭动画效果
//				imageView.clearAnimation();
                controllerView.setShow(true);
				Logger.msg("浮标处于显示状态");
				//imageButton.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "tiantianwan_float_select"));
				showFloat_menu(v);
				//显示浮标菜单
			} else {
//				ControllerView.downTime = 0;
//				controllerView.setShow(false);
				imageView.startAnimation(rotate);
				controllerView.setShow(false);
				Logger.msg("浮标处于隐藏状态");
				//imageButton.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "tiantianwan_float_normal"));
				dismiss_Float_menu();
				//异常浮标菜单
			}
		}
	};
	private View view;
	private ImageButton imageButton;

	/**
	 * 显示菜单
	 */
	public void showFloat_menu(View anchor){
		if(pw_menu_item==null){
			view = LayoutInflater.from(mContext).inflate(MResource.getIdByName(mContext, "layout", "sdk_floatmenu"),null);
			view.findViewById(MResource.getIdByName(mContext, "id","rl_gift")).setOnClickListener(this);//礼包
			view.findViewById(MResource.getIdByName(mContext, "id","rl_reading")).setOnClickListener(this);//攻阅
			view.findViewById(MResource.getIdByName(mContext, "id","rl_useraccount")).setOnClickListener(this);//账户
			view.findViewById(MResource.getIdByName(mContext, "id","rl_service")).setOnClickListener(this);//客服
			
			pw_menu_item=new PopupWindow(view,DimensionUtil.dip2px(mContext,180),DimensionUtil.dip2px(mContext, 60));
			pw_menu_item.setBackgroundDrawable(new ColorDrawable(0x00000000));
		}
		if(ControllerView.float_bar_isLeft){
			//显示左边的箭头
			view.setBackgroundResource(MResource.getIdByName(mContext, "drawable","ttw_float_left_bg"));
			pw_menu_item.showAsDropDown(anchor,anchor.getWidth(),-anchor.getHeight());
		}else{
			//显示右边的箭头
			view.setBackgroundResource(MResource.getIdByName(mContext, "drawable","ttw_float_right_bg"));
			pw_menu_item.showAsDropDown(anchor,-view.getWidth(),-anchor.getHeight());
		}
	}
	
	/**
	 * 隐藏菜单栏
	 */
	public void dismiss_Float_menu(){
		if(null!=pw_menu_item&&pw_menu_item.isShowing()){
			pw_menu_item.dismiss();
		}
	}
	
	/**
	 * 点击相应的菜单做出相应的反应
	 */
	@Override
	public void onClick(View v) {
        if(v.getId()==MResource.getIdByName(mContext, "id","rl_gift")){//礼包
        	Logger.msg("礼包");
        	//startUriIntent();
        	web("http://www.cysdk.com/webgift/web_gift.html");
        }if(v.getId()==MResource.getIdByName(mContext, "id","rl_reading")){//攻阅
        	Logger.msg("攻阅");
        	//startUriIntent("");
        	web("http://www.hao123.com");
        }if(v.getId()==MResource.getIdByName(mContext, "id","rl_useraccount")){//帐户
        	Logger.msg("帐户");
        	//startUriIntent("http://www.cysdk.com/webgift/gift_password.html");
        	web("http://www.cysdk.com/webgift/gift_password.html");
        }if(v.getId()==MResource.getIdByName(mContext, "id","rl_service")){//客服
        	Logger.msg("客服");
        }
        
        dismiss_Float_menu();//隐藏菜单选项
        controllerView.setShow(false);
        imageView.startAnimation(rotate);
	}
	
	public void web(String url){
		Intent intent_view = new Intent(mContext,
				FloatWebActivity.class);
		intent_view.putExtra("url", url);
		intent_view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// bank_intent.putExtra(BANK_PAY_URL,"http://www.251wan.com/sdk/ypay/yeepay.html");
		mContext.startActivity(intent_view);
		//getActivity().finish();
	}
	
	/**
	 * 根据uri地址打开一个浏览器
	 * @param uri_path
	 */
	private void startUriIntent(String uri_path){
		Intent intent_view=new Intent();
		intent_view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent_view.setAction(Intent.ACTION_VIEW);
		intent_view.setData(Uri.parse(uri_path));
		mContext.startActivity(intent_view);
	}
	
	
	private ControllerViewImpl(Context context) {
		init(context.getApplicationContext());
	}

	public synchronized static ControllerViewImpl getInstance(Context context) {
		if (instance == null) {
			instance = new ControllerViewImpl(context);
		}
		return instance;
	}

	// 动画效果设置
	public void initAnimation() {
		rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		LinearInterpolator lir = new LinearInterpolator();
		rotate.setFillEnabled(false);
		rotate.setDuration(800);
		rotate.setRepeatMode(Animation.RESTART);
		rotate.setRepeatCount(-1);
		rotate.setInterpolator(lir);
	}

	protected void init(Context context) {
		this.mContext = context;
		controllerView = new ControllerView(context);
		imageButton = new ImageButton(context);
		imageButton.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "sdk_float_normal"));
		controllerView.addView(imageButton);

		imageView = new ImageView(context);
		imageView.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "flow_gif"));
		// 初始化动画
		initAnimation();
		imageView.startAnimation(rotate);
		controllerView.addView(imageView);
		imageButton.setOnClickListener(iconOnClick);
	}

	protected void setControllerView(int visibility) {
		controllerView.setVisibility(visibility);
	}

	protected void removeControllerView() {
		if (isExist) {
			controllerView.removeControllerView();
			isExist = false;
			instance = null;
		}
		if (controllerView.isShow()) {
			mHandler.removeCallbacks(runnable);
			controllerView.setShow(false);
		}
		
	}

	protected void addToWindow(Context context, int paramX, int paramY) {	
		//读取上次退出时的位置坐标，如果没有数据，默认为参数坐标
		SharedPreferences saveXY = context.getSharedPreferences("savaXY", Context.MODE_PRIVATE);
		int iconX = saveXY.getInt("x", paramX);
		int iconY = saveXY.getInt("y", paramY);
		if (!isExist) {
			isExist = true;
			leftMenu(context, paramX, paramY);
			controllerView.addToWindow(iconX, iconY);		
		}
		controllerView.setVisibility(View.VISIBLE);
		controllerView.initStatusBaHeight(context);
	}

	public void leftMenu(Context context, int paramX, int paramY) {
	}

	private Bitmap getBitmap(Context ctx, String path) {
		synchronized (table) {
			if (table.containsKey(path)) {
				return table.get(path);
			}
			InputStream in = null;
			try {
				in = ctx.getAssets().open(path);
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				table.put(path, bitmap);
				return bitmap;
			} catch (IOException e) {
				return Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Drawable getDrawable(Context ctx, String path) {
		Bitmap bitmap = getBitmap(ctx, path);
		return getDrawable(ctx, bitmap);
	}

	private Drawable getDrawable(Context ctx, Bitmap bitmap) {

		if (density == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) ctx
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			density = metrics.densityDpi;
		}

		BitmapDrawable d = new BitmapDrawable(bitmap);
		d.setTargetDensity((int) (density * (density * 1.0f / 240)));
		return d;
	}

	public NinePatchDrawable getNinePatchDrawable(Context ctx, String path) {
		Bitmap bm;

		synchronized (table) {
			try {
				if (table.containsKey(path)) {
					bm = table.get(path);
				} else {
					bm = BitmapFactory.decodeStream(ctx.getAssets().open(path));
					table.put(path, bm);

				}

				byte[] chunk = bm.getNinePatchChunk();
				boolean isChunk = NinePatch.isNinePatchChunk(chunk);
				if (!isChunk) {
					return null;
				}
				Rect rect = new Rect();
				NinePatchChunk npc = NinePatchChunk.deserialize(chunk);
				NinePatchDrawable d = new NinePatchDrawable(bm, chunk,
						npc.mPaddings, null);
				d.getPadding(rect);
				return d;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	static class NinePatchChunk {

		public static final int NO_COLOR = 0x00000001;
		public static final int TRANSPARENT_COLOR = 0x00000000;

		public Rect mPaddings = new Rect();

		public int mDivX[];
		public int mDivY[];
		public int mColor[];

		private static void readIntArray(int[] data, ByteBuffer buffer) {
			for (int i = 0, n = data.length; i < n; ++i) {
				data[i] = buffer.getInt();
			}
		}

		private static void checkDivCount(int length) {
			if (length == 0 || (length & 0x01) != 0) {
				throw new RuntimeException("invalid nine-patch: " + length);
			}
		}

		public static NinePatchChunk deserialize(byte[] data) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(
					ByteOrder.nativeOrder());

			byte wasSerialized = byteBuffer.get();
			if (wasSerialized == 0)
				return null;

			NinePatchChunk chunk = new NinePatchChunk();
			chunk.mDivX = new int[byteBuffer.get()];
			chunk.mDivY = new int[byteBuffer.get()];
			chunk.mColor = new int[byteBuffer.get()];

			checkDivCount(chunk.mDivX.length);
			checkDivCount(chunk.mDivY.length);

			// skip 8 bytes
			byteBuffer.getInt();
			byteBuffer.getInt();

			chunk.mPaddings.left = byteBuffer.getInt();
			chunk.mPaddings.right = byteBuffer.getInt();
			chunk.mPaddings.top = byteBuffer.getInt();
			chunk.mPaddings.bottom = byteBuffer.getInt();

			// skip 4 bytes
			byteBuffer.getInt();

			readIntArray(chunk.mDivX, byteBuffer);
			readIntArray(chunk.mDivY, byteBuffer);
			readIntArray(chunk.mColor, byteBuffer);
			return chunk;
		}
	}

	public class MyCustomView extends View {

		public MyCustomView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			// mMovie = Movie.decodeStream(context.getAssets().open(fileName))
		}

	}
	//
}
