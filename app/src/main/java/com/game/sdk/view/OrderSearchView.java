package com.game.sdk.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sdk.YTAppService;
import com.game.sdk.domain.Order;
import com.game.sdk.domain.ResultCode;
import com.game.sdk.ui.MyNoFocusListView;
import com.game.sdk.util.Constants;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.GetDataImpl;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;

public class OrderSearchView extends BaseView{
	
	private ViewPager viewPager;
	private MviewPagerAdapter cfAdapter;
	private List<View> listViews; // Tab页面列表
	private Activity ctx;
	
	private View v_slider,ttw_order_item_success,ttw_order_item_wait,ttw_order_item_fail;
	private MyNoFocusListView my_lv_success,my_lv_wait,my_lv_fail;
	private InputMethodManager im;
	
	private static final String TAG = "OrderSearchView";
	private static String TYPE_WAIT = 0 + "";// 查询待支付订单信息
	private static String TYPE_SUCCESS = 1 + "";// 查询订单成功信息
	private static String TYPE_FAIL = 2 + "";// 查询失败订单信息
	private static final int ORDER_COUNT = 8;// 一致性查询数据的长度

	private String current_type;// 当前显示的状态
	private TextView tv_charge_title,tv_back,tv_success, tv_wait, tv_fail,head_view;
	private ImageView iv_ingame;
	private String pay_state = "支付成功";// 三个选项 未支付，支付成功，支付失败

	private List<Order> orders;
	private OrderItemAdapter success_adapter,wait_adapter,fail_adapter;
	private LinearLayout ll_null;
	private int page = 1;
	private boolean isLast = false;// 表示是否加载到了最后一页
	private boolean isloadding=false;//表示是否在正在加载状态
	private  String productname;// 充值游戏名称
	
	private int visibleLastIndex = 0;   //最后的可视项索引    
	private int visibleItemCount;       // 当前窗口可见项总数    

	public OrderSearchView(FragmentActivity activity){
		im=(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		ctx=activity;
		this.productname=activity.getIntent().getStringExtra("productname");
		
		this.inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.content_view=inflater.inflate(MResource.getIdByName(ctx, Constants.Resouce.LAYOUT,"ttw_order_account"),null);
		
		initUI();
		
	}
	
	private void initUI() {
		tv_charge_title = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_charge_title"));
		tv_charge_title.setText("充值记录");
		tv_back = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, Constants.Resouce.ID, "tv_back"));
		iv_ingame = (ImageView) content_view.findViewById(MResource
				.getIdByName(ctx, Constants.Resouce.ID, "iv_ingame"));
		iv_ingame.setVisibility(View.GONE);
		
		v_slider = content_view.findViewById(MResource.getIdByName(ctx,Constants.Resouce.ID, "v_slider"));
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v_slider.getLayoutParams();
		params.width=DimensionUtil.getWidth(ctx)/3;
		v_slider.setLayoutParams(params);
		
		viewPager = (ViewPager) content_view
				.findViewById(MResource.getIdByName(ctx, Constants.Resouce.ID,
						"viewPager"));
		
		tv_success = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, "id", "tv_success"));
		tv_wait = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, "id", "tv_wait"));
		tv_fail = (TextView) content_view.findViewById(MResource.getIdByName(
				ctx, "id", "tv_fail"));

		ttw_order_item_success = inflater.inflate(MResource.getIdByName(
				ctx, "layout", "ttw_order_item_sucess"),null);
		ttw_order_item_wait =inflater.inflate(MResource.getIdByName(
				ctx, "layout", "ttw_order_item_wait"),null);
		ttw_order_item_fail = inflater.inflate(MResource.getIdByName(
				ctx, "layout", "ttw_order_item_fail"),null);
		
		my_lv_success=(MyNoFocusListView) ttw_order_item_success.findViewById(MResource.getIdByName(ctx, "id", "lv_order_success"));
		my_lv_wait=(MyNoFocusListView) ttw_order_item_wait.findViewById(MResource.getIdByName(ctx, "id", "lv_order_wait"));
		my_lv_fail=(MyNoFocusListView) ttw_order_item_fail.findViewById(MResource.getIdByName(ctx, "id", "lv_order_fail"));
		
		//添加页脚
		head_view = new TextView(ctx);
		head_view.setLayoutParams(new AbsListView.LayoutParams(-1, -1));
		head_view.setText("");
		head_view.setTextSize(18);
		head_view.setGravity(Gravity.CENTER_HORIZONTAL);
		my_lv_wait.addFooterView(head_view);
		my_lv_success.addFooterView(head_view);
		my_lv_fail.addFooterView(head_view);
		
		searchOrders(TYPE_SUCCESS,my_lv_success,ttw_order_item_success);
		
		//左右滚动页码
		listViews = new ArrayList<View>();
		listViews.add(ttw_order_item_success);
		listViews.add(ttw_order_item_wait);
		listViews.add(ttw_order_item_fail);
		
		tv_success.setOnClickListener(this);
		tv_wait.setOnClickListener(this);
		tv_fail.setOnClickListener(this);
		
		content_view.findViewById(MResource.getIdByName(ctx, "id", "tv_back"))
				.setOnClickListener(this);
		content_view.findViewById(
				MResource.getIdByName(ctx, "id", "iv_ingame"))
				.setOnClickListener(this);// 进入游戏

		cfAdapter = new MviewPagerAdapter(listViews);
		viewPager.setAdapter(cfAdapter);
		//设置监听，主要是设置点点的背景    
        viewPager.setOnPageChangeListener(new ChargeOnPageChangeListenner());  
	}
	
	public void setBackOnlist(OnClickListener onclick) {
		tv_back.setOnClickListener(onclick);
		iv_ingame.setOnClickListener(onclick);
	}
	

	@Override
	public void onClick(View v) {
		if (v.getId() == tv_success.getId()) {// 查询成功订单
			if (current_type.equals(TYPE_SUCCESS)) {
				return;
			}
			viewPager.setCurrentItem(0);
			return;
		}
		if (v.getId() == tv_wait.getId()) {// 查询正在处理的订单
			if (current_type.equals(TYPE_WAIT)) {
				return;
			}
			viewPager.setCurrentItem(1);
			return;
		}
		if (v.getId() == tv_fail.getId()) {// 查询失败的订单
			if (current_type.equals(TYPE_FAIL)) {
				return;
			}
			viewPager.setCurrentItem(2);
			return;
		}
	}
	
	public void clearColor2White() {
		tv_success.setTextColor(Color.BLACK);
		tv_fail.setTextColor(Color.BLACK);
		tv_wait.setTextColor(Color.BLACK);
	}
	
	@Override
	public View getContentView() {
		return content_view;
	}
	
	/**
	 * 查询订单信息
	 */
	public void searchOrders(final String type,final MyNoFocusListView lv_orders,View v) {
		ll_null = (LinearLayout) v.findViewById(MResource.getIdByName(
				ctx, "id", "ll_null"));	
		if (!DialogUtil.isShowing() && page == 1) {
			DialogUtil.showDialog(ctx, "正在努力获取订单信息...");
		}
		new AsyncTask<String, Void, ResultCode>() {
			@Override
			protected ResultCode doInBackground(String... params) {
				try {
					isloadding=true;
					String json_str = GetDataImpl.getInstance(ctx)
							.searchOrderByUsername(
									YTAppService.userinfo.username,
									params[0], YTAppService.appid,
									page);
					Logger.msg(json_str);
					ResultCode rc = new ResultCode();
					JSONObject jo = new JSONObject(json_str);
					//rc.parseCFTJson(jo);		
					try {
						rc.code = jo.isNull("code")?0:jo.getInt("code"); 
						rc.msg = jo.isNull("msg")?"":jo.getString("msg");
						String jos_str = jo.isNull("data")?"":jo.getString("data");
						
						Order order = null;
						if (orders == null) {
							orders = new ArrayList<Order>();
						} else {
							if (!current_type.equals(type)) {
								orders.clear();
							}
						}
						if(!"".equals(jos_str)){
							JSONArray jos = new JSONArray(jos_str);
							if (jos.length() < ORDER_COUNT) {
								isLast = true;
							} 
	
							for (int i = 0; i < jos.length(); i++) {
								JSONObject j_data = jos.getJSONObject(i);
								order = new Order();
								order.amount = j_data.getString("b");
								order.create_time = j_data.getString("d");
								order.orderid = j_data.getString("a");
								order.paytype = j_data.getString("c");
								orders.add(order);
							}
						}else{
							isLast = true;
						}
						Logger.msg("查询到orders的个数:" + orders.size());
					} catch (JSONException e) {
						// 转换异常
						// 说明获取数据为空
						orders = new ArrayList<Order>();
						e.printStackTrace();
					}
					page++;
					return rc;
				} catch (Exception e) {// 捕获异常
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(ResultCode result) {
				current_type = type;
				try {
					DialogUtil.dismissDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onPostExecute(result);
				String msg = result == null ? "服务端异常，请联系客服！" : result.msg;
				if (result != null && result.code == 1) {
					if (orders.size() == 0) {
						ll_null.setVisibility(View.VISIBLE);
						lv_orders.setVisibility(View.GONE);
					} else {
						ll_null.setVisibility(View.GONE);
						lv_orders.setVisibility(View.VISIBLE);
					}
					head_view.setText(isLast?"数据加载完成":"数据加载中。。");
//					
					if(TYPE_SUCCESS.equals(type)){
						if (success_adapter == null) {
							success_adapter = new OrderItemAdapter();
							lv_orders.setAdapter(success_adapter);
							//滑动监听
							lv_orders.setOnScrollListener(new listOnScrollListener(type,lv_orders,ttw_order_item_success,success_adapter));
						} else {
							success_adapter.notifyDataSetChanged();
						};
					}else if(TYPE_WAIT.equals(type)){
						if (wait_adapter == null) {
							wait_adapter = new OrderItemAdapter();
							lv_orders.setAdapter(wait_adapter);
							lv_orders.setOnScrollListener(new listOnScrollListener(type,lv_orders,ttw_order_item_wait,wait_adapter));
						} else {
							wait_adapter.notifyDataSetChanged();
						};
					}else if(TYPE_FAIL.equals(type)){
						if (fail_adapter == null) {
							fail_adapter = new OrderItemAdapter();
							lv_orders.setAdapter(fail_adapter);
							lv_orders.setOnScrollListener(new listOnScrollListener(type,lv_orders,ttw_order_item_fail,fail_adapter));
						} else {
							fail_adapter.notifyDataSetChanged();
						};
					}
				}
				isloadding=false;
			}
		}.execute(type);
	}
	/**
	 * 显示订单详信息列表
	 * 
	 * @author janecer
	 */
	private class OrderItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orders.size();
		}

		@Override
		public Object getItem(int position) {
			return orders.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Order order = orders.get(position);
			if (null == convertView) {
				View view = inflater.inflate(
								MResource.getIdByName(ctx,
										"layout", "ttw_order_item"),
								null);
				ViewHolder vh = new ViewHolder();
				vh.tv_text = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_text"));
				vh.tv_money = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_money"));
				vh.tv_paytype = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_paytype"));
				vh.tv_order = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_order"));
				vh.tv_detail = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_detail"));
				vh.tv_time = (TextView) view.findViewById(MResource
						.getIdByName(ctx, "id", "tv_date"));
				convertView = view;
				convertView.setTag(vh);
			}
			ViewHolder vh = (ViewHolder) convertView.getTag();
			//vh.tv_text.setText("支付: ");
			vh.tv_money.setText(order.amount + "元 ");
			vh.tv_detail.setText(pay_state);
			vh.tv_order.setText(order.orderid);
			vh.tv_paytype.setText(order.paytype);
			vh.tv_time.setText(order.create_time);
			//convertView.setTag(vh);
			return convertView;
		}
		
		class ViewHolder {
			TextView tv_money, tv_paytype, tv_order, tv_detail, tv_time,tv_text;
		}

	}
	/**
	 * 从新计算listview的高度
	 * 
	 * @param listView
	 */
	public void setListViewHeight(ListView listView) {

		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
									// 子view只有布局需要是Linearlayout，只有它才有mearure方法
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ 40;
		listView.setLayoutParams(params);
	}
	
	
	private class MviewPagerAdapter extends PagerAdapter{ 
		public List<View> mListViews;

        public MviewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override  
        public int getCount() {  
            return 3;  
        }  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0==arg1;  
        }  
       
		@Override
		public void destroyItem(View container, int position, Object arg2) {
			// TODO Auto-generated method stub
			
			((ViewPager) container).removeView(mListViews.get(position));
		}
		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			
			//Log.d("instantiateItem", ""+arg0+" "+arg1);
            try { 
                if(mListViews.get(position).getParent()==null)
                    ((ViewPager) container).addView(mListViews.get(position), 0);  
                else{
                    // 很难理解新添加进来的view会自动绑定一个父类，由于一个儿子view不能与两个父类相关，所以得解绑
                    //不这样做否则会产生 viewpager java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
                	// 还有一种方法是viewPager.setOffscreenPageLimit(3); 这种方法不用判断parent 是不是已经存在，但多余的listview不能被destroy
                    ((ViewGroup)mListViews.get(position).getParent()).removeView(mListViews.get(position));

                    ((ViewPager) container).addView(mListViews.get(position), 0); 
                }
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
               // Log.d("parent=", ""+mListViews.get(arg1).getParent()); 
                e.printStackTrace();  
            }  
            return mListViews.get(position);
		}
		
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}  
    }  
	
	private class ChargeOnPageChangeListenner implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) { 
//			//initTextColor();
			switch (position) {
			case 0:// 
				isLast = false;
				page = 1;
				pay_state = "支付成功！";
				clearColor2White();
				tv_success.setTextColor(Color.RED);
				searchOrders(TYPE_SUCCESS,my_lv_success,ttw_order_item_success);
				break;
			case 1:// 
				isLast = false;
				page = 1;
				pay_state = "未支付！";
				clearColor2White();
				tv_wait.setTextColor(Color.RED);
				searchOrders(TYPE_WAIT,my_lv_wait,ttw_order_item_wait);
				break;
			case 2:// 
				isLast = false;
				page = 1;
				pay_state = "支付失败！";
				clearColor2White();
				tv_fail.setTextColor(Color.RED);
				searchOrders(TYPE_FAIL,my_lv_fail,ttw_order_item_fail);
				break;
			}
			;
		}

		@Override
		public void onPageScrolled(int position, float positonoffset,
				int positionOffsetPixels) {
			Logger.msg("position:" + position + "  positionoffset:"
					+ positonoffset + "  positionoffsetPixels:"
					+ positionOffsetPixels);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v_slider.getLayoutParams();
			params.leftMargin = (int) (position * v_slider.getWidth()  + params.width
					* positonoffset);
			v_slider.setLayoutParams(params);
		}

		@Override
		public void onPageScrollStateChanged(int position) {
			im.hideSoftInputFromWindow(
					content_view.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private class listOnScrollListener implements OnScrollListener{
		private String type;
		private MyNoFocusListView list;
		private View lview;
		private OrderItemAdapter adapter;
		
		public listOnScrollListener(String type,MyNoFocusListView list,View v,OrderItemAdapter adapter) {
			this.type = type;
			this.list = list;
			this.lview = v;
			this.adapter = adapter;
		}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			int itemsLastIndex = adapter.getCount() - 1;    //数据集最后一项的索引    

			int lastIndex = itemsLastIndex+1;             //加上底部的loadMoreView项    
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {    
				if (!isLast&&!isloadding) {
					searchOrders(type,list,lview);
				} else {
					if(isLast){
						Toast.makeText(ctx,
								"获取不到更多订单信息了！", Toast.LENGTH_SHORT)
								.show();
				   }
				}
			}    
			return ;
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount1, int totalItemCount) {
			// TODO Auto-generated method stub
			visibleItemCount = visibleItemCount1;    
			visibleLastIndex = firstVisibleItem + visibleItemCount - 1; 
			return ;
		}
		
	}
	
}
