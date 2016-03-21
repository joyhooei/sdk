package com.game.sdk.floatwindow;


import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.game.sdk.YTAppService;
import com.game.sdk.YTSDKManager;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.util.Constants;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.Md5Util;


public class FloatViewImpl {

	protected static final String TAG = "FloatActivity";
	/**
	 * 悬浮view
	 **/
	private static FloatViewImpl instance = null;
     
	//定义浮动窗口布局  
	private static RelativeLayout mFloatLayout; 
	private LinearLayout item_lay,float_item_user_lay,float_item_gift_lay,float_item_server_lay,float_item_bbs_lay;
	private static WindowManager.LayoutParams wmParams;  
	//创建浮动窗口设置布局参数的对象  
	private static WindowManager mWindowManager;  
	      
	private ImageView mFloatView,float_item_id,float_img_pro,float_img_mypro;  
	private RelativeLayout float_item_server_pro;   
	private boolean isShow = false;
	private LayoutInflater inflater;

	private boolean isExist = false;
	private int density = 0;
	private static boolean fristLogin=true;
	private Context mContext;
	boolean isOne = true;
	private final int MOBILE_QUERY = 1;
	
	private FloatViewImpl(Context context) {
		init(context.getApplicationContext());
	}

	public synchronized static FloatViewImpl getInstance(Context context) {
		if (instance == null) {
			instance = new FloatViewImpl(context);
		}
		return instance;
	}
	protected void init(Context context) {
		this.mContext = context;
		//controllerView = new ControllerView(context);
		// 获取用户是否有未查看的公告
				if (YTAppService.userinfo != null  && fristLogin) {
					
					
					new AsyncTask<Void, Void, Void>(){

						@Override
						protected Void doInBackground(Void... params) {
							// TODO Auto-generated method stub
							try {
								JSONObject json = new JSONObject();
								json.put("a", YTAppService.appid);
								json.put("b", YTAppService.agentid);
								json.put("c", YTAppService.gameid);
								json.put("d", YTAppService.userinfo.username);
								 GetDataImpl.getInstance(mContext)
										.Getuserpro(json.toString());
								
							} catch (NullPointerException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}
						protected void onPostExecute(Void result) {
							fristLogin=false;
							createFloatView();
							
						};
						
					}.execute();
				}else{
					
					createFloatView();
				}
	}
	private Handler hendler= new Handler(){
		public void handleMessage(Message msg) {
			
			switch(msg.what)
            {
            case MOBILE_QUERY:                   
            	wmParams.alpha=90;  
            	mWindowManager.updateViewLayout(mFloatLayout, wmParams); //当10秒到达后，作相应的操作。
                
                break;

            }	
			

		};
	};
	private void createFloatView()  
    {  
        wmParams = new WindowManager.LayoutParams();  
        //获取的是WindowManagerImpl.CompatModeWrapper  
        mWindowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);  
        Log.i(TAG, "mWindowManager--->" + mWindowManager);  
        //设置window type  
        wmParams.type = LayoutParams.TYPE_PHONE;   
        //设置图片格式，效果为背景透明  
        wmParams.format = PixelFormat.RGBA_8888;   
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
        //调整悬浮窗显示的停靠位置为左侧置顶  
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        wmParams.x = 0;  
        wmParams.y = 0;  
  
        //设置悬浮窗口长宽数据    
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
  
        LayoutInflater inflater = LayoutInflater.from(mContext);  
        this.inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		this.content_view = inflater.inflate(MResource.getIdByName(mContext,
//				Constants.Resouce.LAYOUT, "ttw_charge"), null);
        //获取浮动窗口视图所在布局  
        mFloatLayout = (RelativeLayout)inflater.inflate(MResource.getIdByName(mContext,
				Constants.Resouce.LAYOUT, "float_layout"), null);
        //添加mFloatLayout  
        mWindowManager.addView(mFloatLayout, wmParams);  
        init();
    }  
    
    private void init() {
    	 //浮动窗口按钮  
        mFloatView = (ImageView)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "iv_float"));
        
        item_lay = (LinearLayout)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "item_lay")); 
        float_item_id = (ImageView)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "float_item_id"));
        float_item_user_lay = (LinearLayout)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "float_item_user_lay")); 
        float_item_gift_lay = (LinearLayout)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "float_item_gift_lay"));
        float_item_server_lay = (LinearLayout)mFloatLayout.findViewById(MResource
				.getIdByName(mContext, "id", "float_item_server_lay"));
        float_item_bbs_lay = (LinearLayout)mFloatLayout.findViewById(MResource
      	    .getIdByName(mContext, "id", "float_item_bbs_lay"));
        float_img_pro=(ImageView)mFloatLayout.findViewById(MResource
        		.getIdByName(mContext, "id", "float_img_pro"));
        float_item_server_pro=(RelativeLayout)mFloatLayout.findViewById(MResource
        		.getIdByName(mContext, "id", "float_item_server_pro"));
        float_img_mypro=(ImageView)mFloatLayout.findViewById(MResource
        		.getIdByName(mContext, "id", "float_img_mypro"));
          
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
       
        //设置监听浮动窗口的触摸移动  
        mFloatView.setOnTouchListener(new OnTouchListener()   
        {  
              
            @Override  
            public boolean onTouch(View v, MotionEvent event)   
            {  
                // TODO Auto-generated method stub  
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标  
            	 wmParams.alpha=10;
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()/2; 
                Log.i(TAG, "RawX" + event.getRawX());  
                Log.i(TAG, "X" + event.getX());  
                Log.i(TAG, "Width:" + mFloatView.getMeasuredWidth()); 
                //减25为状态栏的高度  
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()/2 - 25;  
                Log.i(TAG, "RawY" + event.getRawY());  
                Log.i(TAG, "Y" + event.getY());  
                 //刷新  
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);  
                switch(event.getAction())
                {
                case MotionEvent.ACTION_DOWN:
                 	
                    resetTime();
                    break;
                case MotionEvent.ACTION_UP:
                	 
                    break;
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听  
            }  
        });   
          
        mFloatView.setOnClickListener(onclick); 
        
        float_item_id.setOnClickListener(onclick);
        //礼包
        float_item_gift_lay.setOnClickListener(onclick);
        //攻略
        float_item_server_lay.setOnClickListener(onclick);
        //论坛
         float_item_bbs_lay.setOnClickListener(onclick);
        //用户
        float_item_user_lay.setOnClickListener(onclick);
        //公告
        float_item_server_pro.setOnClickListener(onclick);
        if (YTAppService.isReadPro) {
			float_img_pro.setVisibility(View.VISIBLE);
			float_img_mypro.setVisibility(View.VISIBLE);
		}
	}
    //  传送msg
    private void resetTime(){
    	  hendler.removeMessages(MOBILE_QUERY);        
          Message msg = hendler.obtainMessage(MOBILE_QUERY);
          hendler.sendMessageDelayed(msg, 8000);   
    }
    private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == mFloatView.getId()) {
				item_lay.setVisibility(View.VISIBLE);
				return;
			}
			if (v.getId() == float_item_id.getId()) {
				item_lay.setVisibility(View.GONE);
				
				if (YTAppService.isReadPro) {
					
					float_img_mypro.setVisibility(View.VISIBLE);
				}
				return;
			}
			if (v.getId() == float_item_user_lay.getId()) {
				hidFloat();
            	web("用户中心",url(Constants.URL_Float_USER));
				return;
			}
			if (v.getId() == float_item_gift_lay.getId()) {
				hidFloat();
            	web("礼包中心",url(Constants.URL_Float_Gift));
				return;
			}
			if (v.getId() == float_item_server_lay.getId()) {
				hidFloat();
				web("客服中心",Constants.URL_Float_Kefu);
				return;
			}
			if (v.getId() == float_item_bbs_lay.getId()) {
				hidFloat();
            	web("论坛",url(Constants.URL_Float_BBS));
				return;
			}
			if (v.getId() == float_item_server_pro.getId()) {
				float_img_pro.setVisibility(View.GONE);
				hidFloat();
            	web("公告",url(Constants.URL_Float_PRO));
            	YTAppService.isReadPro=false;
            	return;
				
			}
		}
	};
	
	//移除悬浮窗口  
    public static void hidFloat()   
    {  
        // TODO Auto-generated method stub  
        //移除悬浮窗口  
    	mFloatLayout.setVisibility(View.GONE);
        //mWindowManager.removeView(mFloatLayout);  
    }
  //移除悬浮窗口  
    public static void removeFloat()   
    {  
        // TODO Auto-generated method stub  
    	mFloatLayout.removeAllViews();
		instance = null;
    }
    //显示悬浮窗口  
    public static void ShowFloat()   
    {  
        // TODO Auto-generated method stub  
        //移除悬浮窗口  
    	mFloatLayout.setVisibility(View.VISIBLE);
        //mWindowManager.addView(mFloatLayout, wmParams);  
    }
    
    public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isShow() {
		return isShow;
	}
	
	public String url(String url){
		String appkey = "ywyouxi@!sdk";
		String time = String.valueOf(System.currentTimeMillis());
		String timeStr = time.substring(0, time.length()-3);
		
		String sign = "username="+YTAppService.userinfo.username+"&appkey="+appkey+"&logintime="+timeStr;
		sign = Md5Util.md5(sign);
		
		
		url = url+"?gameid="+YTAppService.gameid+"&username="+YTAppService.userinfo.username+"&appid="+YTAppService.appid+"&agent="+YTAppService.agentid
				+"&logintime="+timeStr+"&sign="+sign;
		
		
		
		return url;
	}
	
	
	
	public void web(String name,String url){
		Intent intent_view = new Intent(mContext,
				FloatWebActivity.class);
		intent_view.putExtra("url", url);
		intent_view.putExtra("title", name);
		intent_view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent_view);
		//getActivity().finish();
	}
	//
}
