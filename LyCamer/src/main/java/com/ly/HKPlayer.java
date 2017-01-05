package com.ly;

import org.MediaPlayer.PlayM4.Player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_NETCFG_V30;
import com.hikvision.netsdk.RealPlayCallBack;
import com.mw.R;

public class HKPlayer extends Activity implements Callback
{
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
	
	private Player 			m_oPlayerSDK			= null;
	private HCNetSDK		m_oHCNetSDK				= null;
	
	private SurfaceView 	m_osurfaceView			= null;
	String strIP;
	int	nPort;
	String strUser;
	String strPsd;
	// call NET_DVR_Login_v30 to login on, port 8000 as default
	
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
	
	private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
	private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
	private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime	
	private byte			m_byGetFlag				= 1;				// 1-get net cfg, 0-set net cfg 
	private int				m_iPort					= -1;				// play port
	private	NET_DVR_NETCFG_V30 NetCfg = new NET_DVR_NETCFG_V30();		//netcfg struct
	
	private final String 	TAG						= "HKLog";
	private int MChannel = 1;
	
	private TextView _txtIpcName;
	Button _BtnBack;
	
	ImageView _BtnPtzUp;
	ImageView _BtnPtzLeft;	
	ImageView _BtnPtzRight;
	ImageView _BtnPtzDown;		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //HideStatusBar();
        setContentView(R.layout.act_hkplayer1);
        
        
        Intent in = getIntent();
        /*
		in.putExtra("ID", _Arr_ID.get(arg2));
		in.putExtra("name", _Arr_name.get(arg2));
		in.putExtra("status", _Arr_status.get(arg2));
		in.putExtra("channel", _Arr_channel.get(arg2));
		in.putExtra("type", _Arr_type.get(arg2));
		in.putExtra("ip", _Arr_ip.get(arg2));
		in.putExtra("port", _Arr_port.get(arg2));
		in.putExtra("uid", _Arr_uid.get(arg2));
		in.putExtra("pwd", _Arr_pwd.get(arg2));	
        */
        
        strIP = in.getStringExtra("ip");    
        nPort = Integer.valueOf(in.getStringExtra("port")); 
        strUser = in.getStringExtra("uid");
        strPsd = in.getStringExtra("pwd");
        MChannel = Integer.valueOf(in.getStringExtra("channel"));  
        // GUI init
    	m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);   
    	_txtIpcName = (TextView) findViewById(R.id.txtIpcName);     
    	_txtIpcName.setText(in.getStringExtra("name"));
    	
 		_BtnPtzUp = (ImageView) this.findViewById(R.id.BtnPtzUp);				
		_BtnPtzUp.getBackground().setAlpha(150);
		_BtnPtzUp.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				//m_oHCNetSDK.NET_DVR_PTZControl(m_iPlayID, arg1, arg2);
			}
		}); 
		_BtnPtzLeft = (ImageView) this.findViewById(R.id.BtnPtzLeft);				
		_BtnPtzLeft.getBackground().setAlpha(150);
		_BtnPtzLeft.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {

			}
		}); 
		_BtnPtzRight = (ImageView) this.findViewById(R.id.BtnPtzRight);				
		_BtnPtzRight.getBackground().setAlpha(150);
		_BtnPtzRight.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {

			}
		}); 
 		_BtnPtzDown = (ImageView) this.findViewById(R.id.BtnPtzDown);				
 		_BtnPtzDown.getBackground().setAlpha(150);
 		_BtnPtzDown.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {

			}
		}); 
		_BtnBack = (Button) this.findViewById(R.id.BtnCloseHKPlayPage);		
		_BtnBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				finish();
			}
		});
    	
    	m_osurfaceView.getHolder().addCallback(this);
    	
    	//_TvReadViewTi =(TextView) findViewById(R.id.TvReadViewTi);  
    	//_TvReadViewTi.setText(in.getStringExtra("D_Name"));
    	new Thread(new DisplayThread()).start();
    	Toast.makeText(HKPlayer.this, in.getStringExtra("name") + "  视频导入中,请稍候...", 6000).show();		 
    }
    
    
  //显示视频的线程
    class DisplayThread implements Runnable{
    	      
    	public void run() {
			try{
				Thread.sleep(500);
		        if (!initeSdk())
		        {
		        	return;
		        }
		        Thread.sleep(1000);
				loginFisrt();
			}catch(Exception e){				
			}

    	}    	
    }
    
    //@Override    
    public void surfaceCreated(SurfaceHolder holder) {  
    	Log.i(TAG, "surface is created" + m_iPort); 
        Surface surface = holder.getSurface();
        if (null != m_oPlayerSDK && true == surface.isValid()) {
        	if (false == m_oPlayerSDK.setVideoWindow(m_iPort, 0, surface)) {	
        		Log.e(TAG, "Player setVideoWindow failed!");
        	}	
    	}        
    }       
        
    //@Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {   

    }  
        
    //@Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
    	Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (null != m_oPlayerSDK && true == holder.getSurface().isValid()) {
        	if (false == m_oPlayerSDK.setVideoWindow(m_iPort, 0, null)) {	
        		Log.e(TAG, "Player setVideoWindow failed!");
        	}
        }
    } 
    
	@Override  
	protected void onSaveInstanceState(Bundle outState) {    
		outState.putInt("m_iPort", m_iPort);  
		super.onSaveInstanceState(outState);  
		Log.i(TAG, "onSaveInstanceState"); 
	}  
    
	@Override  
	protected void onRestoreInstanceState(Bundle savedInstanceState) {  
		m_iPort = savedInstanceState.getInt("m_iPort");  
		m_oPlayerSDK = Player.getInstance();
		super.onRestoreInstanceState(savedInstanceState);  
		Log.i(TAG, "onRestoreInstanceState" ); 
	}  

    private boolean initeSdk()
	{
		// get an instance and init net sdk
		m_oHCNetSDK = new HCNetSDK();
    	if (null == m_oHCNetSDK)
    	{
    		Log.e(TAG, "m_oHCNetSDK new is failed!");
    		return false;
    	}
    	
    	if (!m_oHCNetSDK.NET_DVR_Init())
    	{
    		Log.e(TAG, "HCNetSDK init is failed!");
    		return false;
    	}
    	m_oPlayerSDK = Player.getInstance();
    	if (m_oPlayerSDK == null)
    	{
    		Log.e(TAG,"PlayCtrl getInstance failed!");
    		return false;
    	}
    	return true;
	}

	
	public void Preview() 
	{
		try
		{
			if(m_iLogID < 0)
			{
				Log.e(TAG,"please login on device first");
				return ;
			}
			if(m_iPlayID < 0)
			{	
				if(m_iPlaybackID >= 0)
				{
					Log.i(TAG, "Please stop palyback first");
					return;
				}
				RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
				if (fRealDataCallBack == null)
				{
				    Log.e(TAG, "fRealDataCallBack object is failed!");
		            return;
				}
				
				int iFirstChannelNo = m_oNetDvrDeviceInfoV30.byStartChan;// get start channel no
				
				Log.i(TAG, "iFirstChannelNo:" +iFirstChannelNo);
				
				NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
				ClientInfo.lChannel=MChannel;
		        //ClientInfo.lChannel =  iFirstChannelNo; 	// start channel no + preview channel
		        ClientInfo.lLinkMode = (1<<31);  			// bit 31 -- 0,main stream;1,sub stream
		        										// bit 0~30 -- link type,0-TCP;1-UDP;2-multicast;3-RTP 
		        ClientInfo.sMultiCastIP = null;
		        
				// net sdk start preview
		        m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID, ClientInfo, fRealDataCallBack, true);
				if (m_iPlayID < 0)
				{
				 	Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
				 	return;
				}
				
				Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
			}
			else
			{
				stopPlay();
			}				
		} 
		catch (Exception err)
		{
			Log.e(TAG, "error: " + err.toString());
		}
	}	 
	// configuration listener

	private void stopPlay()
	{
		if ( m_iPlayID < 0)
		{
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}
		
		//  net sdk stop preview
		if (!m_oHCNetSDK.NET_DVR_StopRealPlay(m_iPlayID))
		{
			Log.e(TAG, "StopRealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
			return;
		}
		
		// player stop play
		if (!m_oPlayerSDK.stop(m_iPort)) 
        {
            Log.e(TAG, "stop is failed!");
            return;
        }	
		
		if(!m_oPlayerSDK.closeStream(m_iPort))
		{
            Log.e(TAG, "closeStream is failed!");
            return;
        }
		if(!m_oPlayerSDK.freePort(m_iPort))
		{
            Log.e(TAG, "freePort is failed!");
            return;
        }
		m_iPort = -1;
		// set id invalid
		m_iPlayID = -1;		
	}
	private int loginDevice()
	{
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		if (null == m_oNetDvrDeviceInfoV30)
		{
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}
		int iLogID = m_oHCNetSDK.NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
		if (iLogID < 0)
		{
			Log.e(TAG, "NET_DVR_Login is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
			return -1;
		}
		
		Log.i(TAG, "NET_DVR_Login is Successful!");
		
		return iLogID;
	}

	public void loginFisrt()
	{
		try
		{
			if(m_iLogID < 0)
			{
				// login on the device
				m_iLogID = loginDevice();
				if (m_iLogID < 0)
				{
					Log.e(TAG, "This device logins failed!");
					return;
				}
				// get instance of exception callback and set
				ExceptionCallBack oexceptionCbf = getExceptiongCbf();
				if (oexceptionCbf == null)
				{
				    Log.e(TAG, "ExceptionCallBack object is failed!");
				    return ;
				}
				
				if (!m_oHCNetSDK.NET_DVR_SetExceptionCallBack(oexceptionCbf))
			    {
			        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
			        return;
			    }
				Log.i(TAG, "Login sucess *******************************************************");
				Preview();
			}
			else
			{
				// whether we have logout
				if (!m_oHCNetSDK.NET_DVR_Logout_V30(m_iLogID))
				{
					Log.e(TAG, " NET_DVR_Logout is failed!");
					return;
				}
				m_iLogID = -1;
			}		
		} 
		catch (Exception err)
		{
			Log.e(TAG, "error: " + err.toString());
		}
	}
	
	private ExceptionCallBack getExceptiongCbf()
	{
	    ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
            	;// you can add process here
            }
        };
        return oExceptionCbf;
	}
	
	private RealPlayCallBack getRealPlayerCbf()
	{
	    RealPlayCallBack cbf = new RealPlayCallBack()
        {
             public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
             {
            	// player channel 1
            	processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME); 
             }
        };
        return cbf;
	}
	
	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
	{
		int i = 0;
	  ///  Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + "iDataType:" + iDataType + "iDataSize:" + iDataSize);
	    try
        {
	    	switch (iDataType)
	    	{
	    		case HCNetSDK.NET_DVR_SYSHEAD:    		
	    			if(m_iPort >= 0)
	    			{
	    				break;
	    			}	    			
	    			m_iPort = m_oPlayerSDK.getPort();	
	    			if(m_iPort == -1)
	    			{
	    				Log.e(TAG, "getPort is failed!");
	    				break;
	    			}
	    			if (iDataSize > 0)
	    			{
	    				if (!m_oPlayerSDK.setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
	    				{
	    					Log.e(TAG, "setStreamOpenMode failed");
	    					break;
	    				}
	    				if(!m_oPlayerSDK.setSecretKey(m_iPort, 1, "ge_security_3477".getBytes(), 128))
	    				{
	    					Log.e(TAG, "setSecretKey failed");
	    					break;
	    				}
	    				if (!m_oPlayerSDK.openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
	    				{
	    					Log.e(TAG, "openStream failed");
	    					break;
	    				}

	    				if (!m_oPlayerSDK.play(m_iPort, m_osurfaceView.getHolder().getSurface())) 
	    				{
	    					Log.e(TAG, "play failed");
	    					break;
	    				}
	    			}
	    			break;	
	    		case HCNetSDK.NET_DVR_STREAMDATA:
	    		case HCNetSDK.NET_DVR_STD_AUDIODATA:
	    		case HCNetSDK.NET_DVR_STD_VIDEODATA:	    		
	    			if (iDataSize > 0 && m_iPort != -1)
	    			{
	    				for(i = 0; i < 400; i++)
	    				{
	    					if (m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize))
		    				{
		    					break;
		    				} 
	    					Thread.sleep(10);
	    				}
	    				if(i == 400)
	    				{
	    					Log.e(TAG, "inputData failed");
	    				}
	    			}
	    			break;
	    		default:
	    			break;
	    	}
        }
        catch (Exception e)
        {
            Log.e(TAG, "processRealData Exception!err:" + e.toString());
        }
	}
		
    public void Cleanup()
    {
        m_oPlayerSDK.freePort(m_iPort);
	    m_oHCNetSDK.NET_DVR_Cleanup();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
         switch (keyCode)
         {
         case KeyEvent.KEYCODE_BACK:
        	 	
        	  stopPlay();
        	  Cleanup();
              //android.os.Process.killProcess(android.os.Process.myPid());
              break;
         default:
              break;
         }
     
         return true;
    }
}
