package com.game.sdk.util;

/**
 * author janecer 2014年7月21日下午4:52:09
 */
public class Constants {

	/**
	 * 平台用户协议条款
	 */
	public final static String URL_USER_AGREMENT = "http://www.iyuewan.cn/sdk/xieyi.html";

	/**
	 * 充值说明
	 */
	public final static String URL_USER_EXPLAIN = "http://www.iyuewan.cn/sdk/pay_des.html";

	/**
	 * 用户注册地址
	 */
	public final static String URL_USER_REGISTER = "http://www.iyuewan.cn/sdk/registernew.php";
	/**
	 *  用户 发送验证码注册
	 * */
	public final static String URL_USER_SENDCODE ="http://www.iyuewan.cn/sdk/registerSend.php";
	/**
	 * 
	 * 用户发送验证码 登陆
	 * */
	public final static String URL_USER_LOGIN_SENDCODE ="http://www.iyuewan.cn/sdk/loginMsg.php";
	/**
	 * 用户登录地址
	 */
	
	public final static String URL_USER_LOGIN = "http://www.iyuewan.cn/sdk/login.php";
	 /**
     * 公告信息
     * */
    public static final String URL_Float_PRO="http://www.iyuewan.cn/cysdkfloat/userproclamation.php";
	/**
	 * 用户一键注册地址
	 */
	public final static String URL_USER_ONKEY2REGISTER = "http://www.iyuewan.cn/sdk/oneRegister.php";

	/**
	 * 退出
	 */
	public final static String URL_USER_LOGIN_OUT = "http://www.iyuewan.cn/sdk/logout.php";
	  /**
     *  获取 公告信息
     * */
    public static final String URL_PROCLAMATION="http://www.iyuewan.cn/sdk/getproclamation.php";
    
    /**
     * 获取用户查看公告信息
     * */
    public static final String URL_USERPROCLAMATION="http://www.iyuewan.cn/sdk/getuserproinfo.php";
	/**
	 * 获取支付聚道
	 */
	public final static String URL_GET_CHARGERCHANNEL = "http://www.iyuewan.cn/sdk/getPayWay.php";

	/**
	 * 将相关数据带给支付宝时，先将相关数据发送到我方服务端
	 */
	public final static String URL_CHARGER_ZIFUBAO = "http://www.iyuewan.cn/sdk/alipay/alipay.php";
	/**
	 * 将相关数据带给现在支付时，先将相关数据发送到我方服务端
	 **/
	public final static String URL_CHARGER_NOWPAY="http://www.iyuewan.cn/sdk/nowpay/nowpay.php";
	/**
	 *现在支付回调路径 
	 **/
	public final static String URL_NOWPAY_URL="http://www.iyuewan.cn/sdk/nowpay/api/notify.php";
	/**
	 * 支付宝充值回调路径
	 */
	public final static String URL_NOTIFY_URL = "http://www.iyuewan.cn/sdk/alipay/notify_url.php";

	/**
	 * 获取平台币信息
	 */
	public final static String URL_USER_PAYTTB = "http://www.iyuewan.cn/sdk/gamepay/getGameGold.php";
	/**
	 * 游戏币充值接口
	 */
	public final static String URL_USER_GAMEPAY = "http://www.iyuewan.cn/sdk/gamepay/gamePay.php";
	/**
	 * 平台充值接口
	 */
	public final static String URL_USER_CHAGETTB = "http://www.iyuewan.cn/sdk/ttbpay/ttbnew.php";

	/**
	 * 查询正在处理订单的状态的地址
	 */
	public final static String URL_STATE_ORDER_SERCH = "http://www.iyuewan.cn/sdk/ypay/nobankcard/search_orderid.php";

	/**
	 * 根据手机imeil码获取账号信息
	 */
	public final static String URL_IMSI_USERINFO = "http://www.iyuewan.cn/sdk/searchUserBuImeil.php";

	/**
	 * 获取客服qq与客户电话
	 */
	public final static String URL_GETSERVICE_TELANDQQ = "http://www.iyuewan.cn/sdk/getKefu.php";

	/**
	 * 财付通充值url地址
	 */
	public final static String URL_CHARGER_CAIFUTONG = "http://www.iyuewan.cn/sdk/tenpay/tenpay.php";

	/**
	 * 一键支付地址
	 */
	public final static String URL_USR_ONEKEYPAY = "http://www.iyuewan.cn/sdk/ypay/yeepay.php";
	
	/**
	 * 易联支付地址
	 */
	public final static String URL_USR_ECOPAY = "http://www.iyuewan.cn/sdk/payeco/payeco.php";
	
	/**
	 * 易联支付消息通知
	 */
	public final static String URL_PAY_NOTIFY = "http://www.iyuewan.cn/sdk/payeco/Return_url.php";
	

	/**
	 * 移动，联通，电信充值卡充值时的地址
	 */
	public final static String URL_USR_MOBILECARDPAY = "http://www.iyuewan.cn/sdk/cypay/nobankcard/nobankcard.php";

	/**
	 * 查询订单记录
	 */
	public final static String URL_ORDER_SEARCH = "http://www.iyuewan.cn/sdk/payRecords_page.php";
	
	/**
	 * 游戏详细信息
	 */
    public static final String URL_GAMEDTAIL_MSG="http://www.iyuewan.cn/sdk/getGameDetail.php";
    
    /**
     * 根据infoid获取礼包码
     */
    public static final String URL_GET_GAMEGIFT="http://www.iyuewan.cn/sdk/getGiftCode.php";
    
    /**
     * 游戏礼包条目地址
     */
    public static final String URL_GAMEGIFT_ITEM="http://www.iyuewan.cn/sdk/getGiftList.php";
    
    /**
     * 用户中心
     */
    public static final String URL_Float_USER="http://www.iyuewan.cn/cysdkfloat/user.php";
    
    /**
     * 礼包中心
     */
    public static final String URL_Float_Gift="http://www.iyuewan.cn/cysdkfloat/gift.php";
    
    /**
     * 客服中心
     */
    public static final String URL_Float_Kefu="http://www.iyuewan.cn/cysdkfloat/kefu.php";
    
    /**
     * 论坛中心
     */
    public static final String URL_Float_BBS="http://bbs.xingyoust.com/forum.php";
    
    /**
     * 密码找回
     */
    public static final String URL_Forgetpwd="http://www.iyuewan.cn/cysdkfloat/forgetpwd.php";
    
    

	/**
	 * 财付通充值回调地址
	 */
	public final static String URL_CHARGER_CAIFUTONGBACK = "";

	public final class Resouce {
		public final static String LAYOUT = "layout";
		public final static String ID = "id";
		public final static String DRAWABLE="drawable";
		public final static String STYLE="style";
	}

	/** sharepref里面的相关配置地址 **/
	public static final String CONFIG = "config";
	public static final String LOGIN_USER_USERNAME = "login_user_username";
	public static final String LOGIN_USER_PWD = "login_user_pwd";
	public static final String ISFIRST_INSTALL = "isfirst_install_config";// 用来判断
																			// 这个xssdk是否是第一次安装
																			// 启动

}
