package com.mw;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LoadingPage extends Activity {

	TextView _txtLoadingText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_loading);
		
		_txtLoadingText = (TextView) this.findViewById(R.id.txtLoadingText);
        Intent in = getIntent();
        int TimeLoop = in.getIntExtra("TimeLoop",1000); 
        
		timer.schedule(task, 100, TimeLoop);
	}
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	   //if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		  if (keyCode == KeyEvent.KEYCODE_BACK)
		      return true; 
		  else
			  return false;
	  }
	Handler handler = new Handler() {
		   @Override
		   public void handleMessage(Message msg) {
			    switch(msg.what)
			    {
			    case 1:
			    	if(UserInfos.LoadingText.length()==0 && !UserInfos.LoadingShowFlag)
			    		finish();
			    	else
			    		_txtLoadingText.setText(UserInfos.LoadingText);
			    	if(!UserInfos.LoadingShowFlag)
			    		UserInfos.LoadingText="";			    	
			    	break;
			    default:
			    	super.handleMessage(msg);
			    	break;
			    }
		   }
	};
	
	  @Override
		protected void onDestroy() {
		  timer.cancel();
			super.onDestroy();
	  }
	
	
	private final Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
	    @Override
	    public void run() {
	     // TODO Auto-generated method stub
	     Message message = new Message();
	     message.what = 1;
	     handler.sendMessage(message);
	     //Log.e("BSD", "------run-------");
	     
	    }
	 };
	
	

}
