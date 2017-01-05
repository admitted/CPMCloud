package xm.eye.preview;


import com.mw.R;
import com.ly.h264.VideoData;
import com.ly.h264.VideoDataList;
import com.ly.h264.XmDvr;
import com.ly.h264.XmDvrVideoReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class xmplayer extends Activity {
	  private void HideStatusBar()
	  {
	      //隐藏标题
	      requestWindowFeature( Window.FEATURE_NO_TITLE );
	      //定义全屏参数
	      int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
	      //获得窗口对象
	      Window myWindow = this.getWindow();
	      //设置Flag标识
	      myWindow.setFlags( flag, flag );
	  }
	  
	  private String  _strIpAddr;
	  private Integer _httpPort;
	  private String  _strUser;
	  private String  _strPwd;
	  private String  _StrName;
	  private boolean _RelayFlag;
	  //private int _DeviceType;
	  private int _Channel;
	  
	  private Handler _handler;
	  private XmDvr _XmDvr;	  
	  private XmDvrVideoReader _XmDvrVideoReader;	  
	  private VideoDataList videoDataList;
	  private boolean bDisplayThreadRuning;
	  
	  
	  private xmview _xmview;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
		 HideStatusBar();
		 setContentView(R.layout.xmplayer_activity);
		 //_xmview = new xmview(xmplayer.this);
		 _xmview=(xmview)findViewById(R.id.view);

		 Intent in = getIntent();
		 _strIpAddr = in.getStringExtra("D_HostAddr");     
		 _httpPort = in.getIntExtra("D_Web_Port", 81);
		 _strUser = in.getStringExtra("D_User");
		 _strPwd = in.getStringExtra("D_Psw");	
		 _StrName = in.getStringExtra("D_Name"); 
		 _xmview._DeviceName =_StrName;
		 _RelayFlag = in.getIntExtra("D_Relay", 0) == 1;		  
		 //_DeviceType =in.getIntExtra("D_Type",0); 
		 _Channel = in.getIntExtra("D_Channel",0); 
		
		bDisplayThreadRuning = true;
		_XmDvrVideoReader=null;
		videoDataList= new VideoDataList();
		_handler = new Handler()
		{
			   @Override
			   public void handleMessage(Message msg)
			   {
				    super.handleMessage(msg);		
				    if(msg.what==0)
				    {
					  	_XmDvrVideoReader=new XmDvrVideoReader(_strIpAddr, _httpPort, _XmDvr._SessID, _Channel, _XmDvr,videoDataList);
					  	new Thread(_XmDvrVideoReader).start(); 
					     //启动播放线程
					    new Thread(new DisplayThread()).start();
				    }
			   }
		};
	     _XmDvr=new XmDvr(_strIpAddr,_httpPort, _strUser,  _Channel,_handler);
	     new Thread(_XmDvr).start();    
	     
	     _xmview.setOnTouchListener(new ViewOntuch());
	     
	}
	
	public class ViewOntuch implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {						
			// TODO 云台控制
		   //Log.d("ipcam", String.valueOf(event.getAction()));
			//if(v == btnPtzUp){			
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					int TempWidth=v.getWidth() / 3;
					int TempHeight=v.getHeight() /3;
					int tempX=(int)event.getX();
					int tempY=(int)event.getY();

					if(tempY <= TempHeight)
					{
						if(tempX > TempWidth && tempX <= TempWidth * 2)
						{	
							_xmview.ShowBtnImg(1);
							_XmDvr.PtzControl("DirectionUp", 65535, 5);
							_XmDvr.PtzControl("DirectionUp", 65535, 5);
							_XmDvr.PtzControl("DirectionUp", -1, 5);
						}
					}
					else if(tempY > TempHeight && tempY <= TempHeight * 2)
					{
							if(tempX<= TempWidth){
								_xmview.ShowBtnImg(3);
								_XmDvr.PtzControl("DirectionLeft", 65535, 5);
								_XmDvr.PtzControl("DirectionLeft", 65535, 5);
								_XmDvr.PtzControl("DirectionLeft", -1, 5);
							}
							else if(tempX > TempWidth && tempX <= TempWidth * 2)
							{	
								_xmview.ShowBtnImg(0);
							}
							else{
								_xmview.ShowBtnImg(4);
								_XmDvr.PtzControl("DirectionRight", 65535, 5);
								_XmDvr.PtzControl("DirectionRight", 65535, 5);
								_XmDvr.PtzControl("DirectionRight", -1, 5);
							}
					}
					else
					{

						if(tempX<= TempWidth){
							_xmview.ShowBtnImg(5);
							_XmDvr.PtzControl("ZoomWide", 65535, 5);
							_XmDvr.PtzControl("ZoomWide", 65535, 5);
							_XmDvr.PtzControl("ZoomWide", 65535, 5);
							_XmDvr.PtzControl("ZoomWide", -1, 5);
						}
						else if(tempX > TempWidth && tempX <= TempWidth * 2)
						{	
							_xmview.ShowBtnImg(2);
							_XmDvr.PtzControl("DirectionDown", 65535, 5);
							_XmDvr.PtzControl("DirectionDown", 65535, 5);
							_XmDvr.PtzControl("DirectionDown", -1, 5);
						}
						else{
							_xmview.ShowBtnImg(6);
							_XmDvr.PtzControl("ZoomTile", 65535, 5);
							_XmDvr.PtzControl("ZoomTile", 65535, 5);
							_XmDvr.PtzControl("ZoomTile", 65535, 5);
							_XmDvr.PtzControl("ZoomTile", -1, 5);
						}
						
					}
					break;
				case MotionEvent.ACTION_UP:
					//_XmDvr.PtzControl("DirectionUp", 65535, 5);
					//ptzCtrList.addControlCmd(0);
					//sendPtzControlMessage(0);
					break;
					default:
						break;
				}								
			return false;			
		}

	}
	
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		   { 
			   //onDestroy();
				this.bDisplayThreadRuning = false;
			  _xmview.onDestroy();
				_XmDvr.Disponse();
				  if(_XmDvrVideoReader!=null)
					  _XmDvrVideoReader.Disponse();			
			   this.finish();
	            return true; 
		   } 
		   else
		   {
	           return false; 
		   }
	  }
	
	  @Override
		protected void onDestroy() {
			 super.onDestroy();
	}
	//显示线程
	 private class DisplayThread implements Runnable
	 {
	  	public void run()
	  	{
	  		while(bDisplayThreadRuning)
	  		{
				VideoData videoData = videoDataList.getVideoData();
				if (videoData == null) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				else
				{
					_xmview.OfferData(videoData.m_videoBuf, videoData.m_FrameLength);
				}
	  		}
	  	}
	 }
	
}
