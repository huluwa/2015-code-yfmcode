package com.iptv.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class AppUpdateThread extends Thread{

	private Handler handler;
	private String url;
	private HttpUtils hu;
	public AppUpdateThread(Handler handler,String url){
		this.handler=handler;
		this.url=url;
		hu=new HttpUtils();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			Message msg=handler.obtainMessage();
			Log.i("tvinfo", url);
			Appinfo info=hu.getremoteapp(url);
			msg.what=1;
			msg.obj=info;
			handler.sendMessage(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
	}

	public void close(){
		hu.close();
	}
	
}