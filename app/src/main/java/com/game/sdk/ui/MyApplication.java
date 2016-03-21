package com.game.sdk.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import com.game.sdk.util.Logger;

import android.app.Application;
import android.os.Environment;
import android.widget.TextView;

/**
 * author janecer
 * 2014-3-24下午6:27:19
 */
public class MyApplication extends Application implements
		UncaughtExceptionHandler {

	private static final String TAG = "MyApplication";
	public static TextView tv_download_count;
	public static String apkdownload_path="";
    @Override
    public void onCreate() {
    	super.onCreate();
    	apkdownload_path=Environment.getExternalStorageDirectory().getPath()+File.separator+this.getPackageName();
        File file=new File(apkdownload_path);
        if(!file.exists()){
        	boolean flag=file.mkdirs();
        }
        apkdownload_path+=File.separator;
    }
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
         
	}

	/**
	 * 如果有处理 返回false，如果没有处理返回true则由系统进行处理
	 * @param ex
	 * @return 
	 */
	public boolean handMessage(Throwable ex){
		
		return false;
	}
}
