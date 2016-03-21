package com.game.sdk.ui;


import android.view.View;
import android.view.View.OnClickListener;



import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.util.Constants;

import com.game.sdk.util.MResource;



import com.game.sdk.view.LoginView;
import com.game.sdk.view.OneRegisterUser;
import com.game.sdk.view.RegisterView;
import com.game.sdk.view.TelLoginView;


/**
 * author janecer 2014年7月22日上午9:44:22
 */
public class LoginActivity extends BaseActivity {
	
	public static OnLoginListener loginlistener;

	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "tv_telregister"  )  || v.getId() == MResource.getIdByName(LoginActivity.this,
							Constants.Resouce.ID, "ll_telregister_back"  )  ) {
				final RegisterView register = new RegisterView(
						LoginActivity.this,loginlistener);
				pushView2Stack(register.getContentView());
				register.setOnClick(onclick);
				return ;
			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					
					Constants.Resouce.ID, "tv_tellogin") || v.getId() == MResource.getIdByName(LoginActivity.this,
							Constants.Resouce.ID, "ll_tellogin_back"  )) {
				
				TelLoginView telregister = new TelLoginView(
						LoginActivity.this,loginlistener);
				pushView2Stack(telregister.getContentView());
				telregister.setOnClick(onclick);
				return ;
			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "ll_usermsg_back"  )) {
			
				popViewFromStack();
				
			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "rl_oneregister_back_login"  )) {
				
				popViewFromStack();
				popViewFromStack();
				
			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "ll_user_oneregister_goin"  )) {
				final OneRegisterUser oneregister = new OneRegisterUser(
						LoginActivity.this,loginlistener,true);
				oneregister.OneKeyRegister(new onRegisterBack() {

					@Override
					public void toRegister() {
						// TODO Auto-generated method stub
						pushView2Stack(oneregister.getContentView());
					}

				});

				oneregister.setOnClick(onclick);
				
				return ;
				
			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "ll_user_register_goin"  )) {
				OneRegisterUser userregister = new OneRegisterUser(
						LoginActivity.this,loginlistener,false);
				pushView2Stack(userregister.getContentView());
				userregister.setOnClick(onclick);
				return ;
				
			}
			
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "ll_back_user_login")) {
				popViewFromStack();
				return;
			}
			//用户协议 
//			if (v.getId() == MResource.getIdByName(LoginActivity.this,
//					Constants.Resouce.ID, "tv_user_aggrement")) {
//				UserAgrementView agrement = new UserAgrementView(
//						LoginActivity.this);
//				pushView2Stack(agrement.getContentView());
//
//				agrement.setBackOnclik(onclick);
//				return;
//			}
			if (v.getId() == MResource.getIdByName(LoginActivity.this,
					Constants.Resouce.ID, "tv_back")) {
				popViewFromStack();
				
				
				return;
			}

		}
	};
	
	
			

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoginView login = new LoginView(this, loginlistener);
		login.setRegisterOnClick(onclick);
		pushView2Stack(login.getContentView());

	};

	@Override
	public void onBackPressed() {
		if(!isTop()){
			popViewFromStack();
		}else{
			popViewFromStack();
			LoginErrorMsg errorMsg = new LoginErrorMsg(2, "取消登录");
			loginlistener.loginError(errorMsg);// 登录失败回调
		}
	}
	
	public interface onRegisterBack {
		public void toRegister();
	}
}
