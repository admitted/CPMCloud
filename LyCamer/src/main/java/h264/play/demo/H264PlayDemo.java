package h264.play.demo;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xm.eye.preview.H264View;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.mw.R;


public class H264PlayDemo extends Activity {

	static {
		System.loadLibrary("H264Android");
	}

	public native int InitDecoder(int width, int height);
	public native int UninitDecoder();
	public native int DecoderNal(byte[] in, int insize, byte[] out);

public class SurfaseOntuch implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {						
			// TODO Auto-generated method stub
			
			Log.d("ipcam", String.valueOf(event.getAction()));
			//if(v == btnPtzUp){			
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					int TempWidth=v.getWidth() / 3;
					int TempHeight=v.getHeight() /3;
					int tempX=(int)event.getX();
					int tempY=(int)event.getY();

					if(tempY <= TempHeight)
					{
						CommandMessage cmdMsg = new CommandMessage();
					    cmdMsg.msgType = CMD_PTZ_CTRL_REQUEST;
					    cmdMsg.msgParam1 = 1;
					    commandList.addCommand(cmdMsg);	
					    					   					   
					}
					else if(tempY > TempHeight && tempY <= TempHeight * 2)
					{
						if(tempX<= TempWidth)
						{
							CommandMessage cmdMsg = new CommandMessage();
						    cmdMsg.msgType = CMD_PTZ_CTRL_REQUEST;
						    cmdMsg.msgParam1 = 3;
						    commandList.addCommand(cmdMsg);	
						    					   			
							
							
						}else if(tempX > TempWidth && tempX <= TempWidth * 2)
						{
							
							
						}
						else
						{
							CommandMessage cmdMsg = new CommandMessage();
						    cmdMsg.msgType = CMD_PTZ_CTRL_REQUEST;
						    cmdMsg.msgParam1 = 6;
						    commandList.addCommand(cmdMsg);	
						    					   			
							
						}
					}
					else
					{
						CommandMessage cmdMsg = new CommandMessage();
					    cmdMsg.msgType = CMD_PTZ_CTRL_REQUEST;
					    cmdMsg.msgParam1 = 2;
					    commandList.addCommand(cmdMsg);
					}
					CommandMessage cmdMsg1 = new CommandMessage();
				    cmdMsg1.msgType = CMD_PTZ_CTRL_REQUEST;
				    cmdMsg1.msgParam1 = 0;
				    commandList.addCommand(cmdMsg1);	
		
					break;
				case MotionEvent.ACTION_UP:
					
					//ptzCtrList.addControlCmd(0);
					//sendPtzControlMessage(0);
					break;
					default:
						break;
				}								


			return false;			
		}

	}

/** Called when the activity is first created. */
	
	private boolean bCommandThreadRuning = false;
	private boolean bMediaThreadRuning = false;
	private boolean bSendCommandThreadRuning = false;
	private boolean bDisplayThreadRuning = false;
	
	private InputStream cmdInputStream = null;
	private OutputStream cmdOutputStream = null;
	
	private InputStream mediaInputStream = null;
	private OutputStream mediaOutputStream = null;
	
	private boolean bStartMediaRequest = false;
	
	private Socket cmdSocket = null;
	private Socket mediaSocket = null;
	
	//private byte[] videoBuffer = null;
	private VideoData videoData = null;
	private int videoRecvSize = 0;
	private byte[] _VideoID;
	private static final int MESSAGE_VERSION = 0x10;
	private static final int MSG_HEADER_LENGTH = 8;
	
	//命令定义
	private static final int CMD_REGISTER_REQUEST = 0x0101; 
	private static final int CMD_REGISTER_RESPONSE = 0x8101;
	private static final int ALARM_MOTION_DETECT = 0x8560;
	private static final int ALARM_GPIO = 0x8561;
	
	private static final int STREAM_REQUEST = 0x0201;
	private static final int STREAM_RESPONSE = 0x8201;
	private static final int STREAM_NOTIFY = 0x0202;
	
	private static final int SEND_VIDEO_DATA = 0x0301;
	private static final int SEND_AUDIO_DATA = 0x0302;
	private static final int CMD_HEART_BEAT_REQUEST = 0x0103;
	private static final int CMD_HEART_BEAT_RESPONSE = 0x8103;
	
	private static final int CMD_PTZ_CTRL_REQUEST = 0x0401;	
	private static final int CMD_PTZ_CTRL_RESPONSE = 0x8401;	
	
	private CommandList commandList = new CommandList();
	private VideoDataList videoDataList = new VideoDataList();
	
	  
	  private String  _strIpAddr;
	  private Integer _httpPort;
	  private String  _strUser;
	  private String  _strPwd;
	  private String  _StrName;
	  private boolean _RelayFlag;
	  public SurfaceHolder _holder;
	  private boolean _FullScreen = false;
	  
	  private int videoWidth = 640;
	  private int videoHeight = 480;
	
	  
	  Rect Srect = new Rect(0, 0, videoWidth, videoHeight);
	  Rect Rrect = new Rect(0, 0, 320, 240);
	  Rect Surfacerect = new Rect(0, 0, 320, 240);
	  
	  private int _FrameNo =0;
	  private int _TempNo =0;
	  private int _FramePlay=61;
	  private boolean _IsJumpFrame=false;
	  Paint p;
	  //private H264View _H264View;

  
	  private byte [] mPixel = null;
	  ByteBuffer buffer = null;
	  Bitmap VideoBit = null;
	
	
	private final Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
	    @Override
	    public void run() {
	     // TODO Auto-generated method stub
	     Message message = new Message();
	     message.what = 1;
	     handler.sendMessage(message);
	    }
	 };

	Handler handler = new Handler() {
	   @Override
	   public void handleMessage(Message msg) {
	    // TODO Auto-generated method stub
	    //要做的事情
	    super.handleMessage(msg);
	    
	    CommandMessage cmdMsg = new CommandMessage();
	    cmdMsg.msgType = CMD_HEART_BEAT_REQUEST;
	    commandList.addCommand(cmdMsg);

	   }
	};
	
	/**
   * 状态栏隐藏(全屏),在Activity.setCurrentView();
   */
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
	

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      HideStatusBar();
      p = new Paint(); //创建画笔
      p.setAlpha(0);
      //p.setColor(Color.YELLOW);	
     // p.setTextSize(25);     
		p.setColor(Color.YELLOW);
		p.setStyle(Paint.Style.STROKE);
		p.setTextSize(32);
		p.setStrokeWidth(1);
      
      setContentView(new MyView(this));   
      
      //_H264View=new H264View();
      
      //setContentView(com.ly.R.layout.video1);

      Intent in = getIntent();
      _strIpAddr = in.getStringExtra("D_HostAddr");     
      _httpPort = in.getIntExtra("D_Web_Port", 81);
      _strUser = in.getStringExtra("D_User");
      _strPwd = in.getStringExtra("D_Psw");	
      _StrName = in.getStringExtra("D_Name"); 
      _RelayFlag = in.getIntExtra("D_Relay", 0) == 1;
      _VideoID=new byte[2];
      //启动播放线程
      bDisplayThreadRuning = true;
     new Thread(new DisplayThread()).start();
      //发送命令消息线程
      bSendCommandThreadRuning = true;
     new Thread(new SendCommandThread()).start();        
      //接收命令线程
      bCommandThreadRuning = true;
     new Thread(new CommandThread()).start();
      //接收数据线程
      bMediaThreadRuning = true;
      new Thread(new MediaThread()).start();     

      
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
       switch (keyCode)
       {
       case KeyEvent.KEYCODE_BACK:
      	 	this.finish();
            //android.os.Process.killProcess(android.os.Process.myPid());
            break;
       default:
            break;
       }
   
       return true;
  }
  
//显示视频的线程
  class DisplayThread implements Runnable{

  	public void run() {
  		Canvas c = null;
  		while(bDisplayThreadRuning){
  			try{
  				VideoData videoData = videoDataList.getVideoData();
  				if(videoData == null){
  					Thread.sleep(10);
  					continue;						
  				}
  			
  				//解码视频
  				if(mPixel.length>307200)
  					mPixel[307200] = 0;
  				int nRet = DecoderNal(videoData.videoBuf, videoData.videoLength, mPixel);
		   			if(nRet > 0){
		   				if(mPixel[307200]==0)
		   					Srect = new Rect(0, 0, 320, 240);
		   				else
		   					_IsJumpFrame=true;
		   		  	    VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565);
		   		  	    buffer.rewind();
		   				VideoBit.copyPixelsFromBuffer(buffer);
		                try
		                {
		                    synchronized (_holder)
		                    {
		                        c = _holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
		                        c.drawColor(Color.BLACK);  
		                        if(_FullScreen)
		                        	c.drawBitmap(VideoBit, Srect, Surfacerect, null);
		                        else
		                        	c.drawBitmap(VideoBit, Srect, Rrect, null);
		                        
		                        c.drawText(String.format("%s (%d x %d) %s", _StrName, Srect.right, Srect.bottom, _RelayFlag?"Relay":""), 30, 30, p);
		                    }
		                }
		                catch (Exception e) {
		                    e.printStackTrace();
		                }
		                finally
		                {
		                    if(c!= null)
		                    {
		                        _holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
		                    }
		                }			
		   			}   		
		   			
  			}catch(Exception e){  				
  			}  			
  		}
  		UninitDecoder();
  	}
  	
  }
  
  
  //视频数据结构
  class VideoData{
  	public int videoLength = 0;
  	public byte[] videoBuf = null;
  	
  	VideoData(int videoLength){
  		videoBuf = new byte[videoLength + 1];
  	}
  }
  
  //视频缓冲
  class VideoDataList{
  	
  	private List videoList = new ArrayList();
  	
  	public VideoData getVideoData(){
  		synchronized(this){
  			if(videoList.isEmpty()){
  				return null;
  			}
  			
  			return (VideoData)(videoList.remove(0));
  		}
  	}
  	
  	public void addVideoData(VideoData videoData){
  		synchronized(this){
  			videoList.add(videoData);
  		}
  		
  	}
  	
  	public void clearVideoData(){
  		synchronized(this){
  			videoList.clear();
  		}
  	}
  	
  	public int getVideoDataListSize(){
  		synchronized(this){
  			return videoList.size();
  		}
  	}
  }
  
  
  
  //消息类
  class CommandMessage{
  	public int msgType = 0;
  	public int msgParam1 = 0;
  	public int msgParam2 = 0;
  }
  
  //消息列表
  class CommandList{
  	private List cmdList = new ArrayList();
  	
  	public CommandMessage getCommand(){
  		
  		synchronized(this){
  			if(cmdList.isEmpty()){
      			return null;
      		}
      		
      		return (CommandMessage)(cmdList.remove(0));
  		}    		
  	}
  	
  	public void addCommand(CommandMessage cmdMsg){
  		synchronized(this){
  			cmdList.add(cmdMsg);
  		}
  	}
  	
  	public void ClearCommand(){
  		synchronized(this){
  			cmdList.clear();
  		}
  	}
  }
  
  //发送命令消息
  private boolean sendCommandMessage(CommandMessage cmdMsg){
  	
  	boolean bResult = false;
  	
  	switch(cmdMsg.msgType){
  	case CMD_HEART_BEAT_REQUEST:
  		//Log.d("PlayDemo", "CMD_HEART_BEAT_REQUEST");
  		bResult = sendHeartBeatMessage();
  		break;
  	case CMD_PTZ_CTRL_REQUEST:
  		bResult = sendPtzControlMessage(cmdMsg.msgParam1);
  		break;
  	case CMD_PTZ_CTRL_RESPONSE:
  		bResult = sendPtzControlMessage(cmdMsg.msgParam1);
  		break;    		
  	default:
  		Log.d("PlayDemo", "sendCommandMessage other msg");
  		break;
  	}
  	
  	return bResult;
  }
  
  //发送心跳
  private boolean sendHeartBeatMessage(){
  	byte[] sendBuf = new byte[16];
  	
  	packetMessageHeader(sendBuf, CMD_HEART_BEAT_REQUEST, 0);  
				
  	
  	try{
  		cmdOutputStream.write(sendBuf, 0, 8);
  		
  	}catch(Exception e){
  		Log.d("PlayDemo", "sendHeartBeatMessage Exception");
  		return false;
  	}
  	
  	return true;
  }
  
  //发送命令消息线程
  class SendCommandThread implements Runnable{

		public void run() {

			InitDecoder(videoWidth, videoHeight);
	  	  mPixel = new byte[videoWidth*videoHeight*2];
	  	  buffer = ByteBuffer.wrap( mPixel );
	  	  VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565);
	
			while(bSendCommandThreadRuning){
				try{
					
					//取得命令消息
					CommandMessage cmdMsg = commandList.getCommand();
					if(null == cmdMsg){
						Thread.sleep(10);
						continue;
					}
					Thread.sleep(400);
					//发送消息
					if(!sendCommandMessage(cmdMsg)){
						closeCommandSocket();
						Log.d("PlayDemo", "sendCommandMessage failed");
						commandList.ClearCommand();
						return;
					}					
					
				}catch(Exception e){
					Log.d("PlayDemo", "SendCommandThread Exception");
					return;
				}
				
			}
		}
  	
  }
 
  
  
  //处理命令消息
  private void processCmdMessage(int msgType, int msgLen, byte[] dataBuf){
  	switch(msgType){
  	case CMD_REGISTER_RESPONSE: //注册回应
  		Log.d("PlayDemo", "CMD_REGISTER_RESPONSE");
  		sendVideoRequestMessage();
  		break;
  	case STREAM_RESPONSE: 
  		bStartMediaRequest = true;
  		
  		
  		_VideoID[0]=dataBuf[2];
  		_VideoID[1]=dataBuf[3];
  		//Log.d("PlayDemo", "_VideoID->" + String.valueOf(_VideoID[0]) + "---" + String.valueOf(_VideoID[1]));

  		timer.schedule(task, 1000, 10000);
  		break;
  	case ALARM_MOTION_DETECT: //移动侦测报警
  		Log.d("PlayDemo", "ALARM_MOTION_DETECT");
  		break;
  	case ALARM_GPIO: //GPIO报警
  		Log.d("PlayDemo", "ALARM_GPIO");
  		break;
  	case CMD_HEART_BEAT_RESPONSE: //心跳
  		//Log.d("PlayDemo", "CMD_HEART_BEAT_RESPONSE");
  		break;
  	case CMD_PTZ_CTRL_RESPONSE:
  		Log.d("PlayDemo", "CMD_PTZ_CTRL_RESPONSE");
  		break;	
  	default:
  		Log.d("PlayDemo", "Unknow command");
  		break;
  	}
  	
  }
  
  static class AVHead {
  	
  	//得到包序号
  	public static int getPacketIndex(byte[] avHeadBuf){  	    		
  		return (avHeadBuf[6] & 0xff);
  	}
  	
  	//得到帧类型
  	public static int getFrameType(byte[] avHeadBuf){
  		return (avHeadBuf[4] & 0xff);
  	}
  	
  	//得到帧长度
  	public static int getFrameLength(byte[] avHeadBuf){
  		int frameLength = 0;
  		
  		frameLength = avHeadBuf[15] & 0xff;
  		frameLength = frameLength << 8;
  		frameLength += avHeadBuf[14] & 0xff;
  		frameLength = frameLength << 8;
  		frameLength += avHeadBuf[13] & 0xff;
  		frameLength = frameLength << 8;
  		frameLength += avHeadBuf[12] & 0xff;
  		
  		return frameLength;
  	}
  	
  	//得到当前包的长度
  	public static int getPacketLength(byte[] avHeadBuf){
  		int packetLength = 0;
  		
  		packetLength = avHeadBuf[23] & 0xff;
  		packetLength = packetLength << 8;
  		packetLength += avHeadBuf[22] & 0xff;
  		
  		return packetLength;
  	}
  	
  }
      
  
  //处理音频数据
  private void processAudioData(int msgLen, byte[] dataBuf){
  	
  }
  
  //数组的拷贝
  private boolean ArrayCopy(byte[] src, int srcStart, int srcLength, byte[] dest, int destStart){
  	if(srcLength > src.length){
  		return false;
  	}
  	
  	if(srcLength > dest.length - destStart){
  		Log.d("PlayDemo", "length error222");
  		return false;
  	}
  	
  	int i;
  	for(i=0; i < srcLength; i++){
  		dest[destStart + i] = src[srcStart + i];
  	}
  	
  	return true;
  }
  
  //处理视频数据
  private void processVideoData(int msgLen, byte[] dataBuf){
  	
  	int packetIndex = AVHead.getPacketIndex(dataBuf);
  	int frameLength = AVHead.getFrameLength(dataBuf);
  	int frameType = AVHead.getFrameType(dataBuf);
  	//Log.e("Dvr", String.valueOf(frameType));
  	int packetLength = AVHead.getPacketLength(dataBuf);

  	if(Srect.bottom ==640 &&  frameType == 6)
  		return;
	  	_TempNo = dataBuf[8] + dataBuf[9]*256;
	  	if(_FrameNo !=_TempNo)
	  	{
	  		_FrameNo = _TempNo;	
	  	  	if(_IsJumpFrame)
	  	  	{		  
		  		if(dataBuf[4]==5)
		  			_FramePlay = 0;
		  		else		
			  		_FramePlay++;
	  	  	}
	  	  	else
	  	  	{
	  	  		_FramePlay = 0;
	  	  	}	  	  		
	  	}
		if(_FramePlay>16 || (_FramePlay % 2 != 0))
			return;
  	
  	//Log.d("PlayDemo", String.valueOf(_FrameNo));
  	
  	/*String str;  _FrameNo
  	str = "packetIndex:" + packetIndex + " frameLength:"+frameLength+ " frameType:"+frameType+" packetLength:"+packetLength;
  	Log.d("PlayDemo", str);*/
  	
  	if(packetIndex == 0){
  		if(videoData != null){
  			//写入视频缓冲队列中
  			//Log.d("PlayDemo", "write to video buffer");
  			videoRecvSize = 0;
  			//if(Srect.bottom !=320 &&  frameType != 1)
  			videoDataList.addVideoData(videoData);
  		}
  		
  		videoData = null;
  		//申请内存
  		videoData = new VideoData(frameLength);
  		videoData.videoLength = frameLength;
  		
  		//拷贝视频流到视频缓存中
  		ArrayCopy(dataBuf, 24, packetLength, videoData.videoBuf, videoRecvSize);
  		videoRecvSize += packetLength;    		
  	}else{
  		//拷贝视频流到视频缓存中
  		ArrayCopy(dataBuf, 24, packetLength, videoData.videoBuf, videoRecvSize);
  		videoRecvSize += packetLength;  
  	}
  }
  
  //处理数据消息
  private void processMediaMessage(int msgType,int msgLen, byte[] dataBuf){
  	switch(msgType){
  	case SEND_AUDIO_DATA: //音频
  		Log.d("PlayDemo", "SEND_AUDIO_DATA");
  		processAudioData(msgLen, dataBuf);
  		break;
  	case SEND_VIDEO_DATA: //视频
  		//Log.d("PlayDemo", "SEND_VIDEO_DATA");
  		processVideoData(msgLen, dataBuf);
  		break;
  	default: 
  		Log.d("PlayDemo", "media other msg");
  		break;
  	}
  }
  
  //取得消息类型
  private int getMsgType(byte[] headBuf){
  	
  	int msgType;
  	
  	msgType = headBuf[3] & 0xff;
  	msgType = msgType << 8;
  	msgType += headBuf[2] & 0xff;    	
  	
  	return msgType;
  }
  
  //取得消息长度
  private int getMsgLen(byte[] headBuf){
  	int msgLen;
  	
  	msgLen = headBuf[7] & 0xff;
  	msgLen = msgLen << 8;
  	msgLen += headBuf[6] & 0xff;    	
  	
  	return msgLen;
  }
  
  //接收消息头
  private boolean recvHeader(InputStream is, byte[] recvBuf){
  	
  	byte version = 0;
  	byte headLen = 0;
  	
  	int recvSize = 0;
  	
  	if(recvBuf.length < MSG_HEADER_LENGTH){
  		Log.d("PlayDemo", "recvBuf.length < MSG_HEADER_LENGTH");
  		return false;
  	}
  	
  	try{
  		while(recvSize < MSG_HEADER_LENGTH){
  			//int nRed = is.read();
  			int nRed = is.read(recvBuf, recvSize, MSG_HEADER_LENGTH - recvSize);
  			if(nRed == -1){
  				Log.d("PlayDemo", "recvHeader nRed == -1 recvSize:" + recvSize);    				
  				return false;
  			}
  			recvSize += nRed;
  			//recvBuf[recvSize++] = (byte)nRed;
  		}  	
  		
  	}catch(Exception e){
  		e.printStackTrace();
  		Log.d("PlayDemo", "recvHeader Exception");
  		return false;
  	}   
  	
  	version = recvBuf[0];
  	headLen = recvBuf[1];
		//if(version != MESSAGE_VERSION || headLen != MESSAGE_VERSION){
  	if(version != MESSAGE_VERSION ){
			Log.d("PlayDemo", "version:" + version + " headLen:"+ headLen);
			return false;
		}
		
		/*int i;
		for(i=0; i<MSG_HEADER_LENGTH; i++){
			Log.d("PlayDemo", "headBuf["+i+"]:"+ (recvBuf[i] & 0xff));
		}*/
		
		return true;
	}
  
  //接收数据部分
  private boolean recvData(InputStream is, byte[] recvBuf, int recvlen){
  	
  	int recvSize = 0;
  	
  	if(recvBuf.length < recvlen){  		
  		return false;
  	}
  	
  	try{
  		while(recvSize < recvlen){
  			//int nRed = is.read();
  			int nRed = is.read(recvBuf, recvSize, recvlen - recvSize);  			  
  			if(nRed == -1){
  				Log.d("PlayDemo", "recvData nRed == -1 recvSize:"+ recvSize);    				
  				return false;
  			}
  			recvSize += nRed;  			
  			//recvBuf[recvSize++] = (byte)nRed;
  		}
  		
  	}catch(Exception e){
  		e.printStackTrace();
  		Log.d("PlayDemo", "recvData Exception");
  		return false;
  	}   
  	
  	return true;
  }
  
  private boolean packetMessageHeader(byte[] packBuf, int msgType, int msgLen){
  	if(packBuf.length < MSG_HEADER_LENGTH){
  		return false;
  	}
  	
  	//version
  	packBuf[0] = MESSAGE_VERSION;
  	
  	//headlen
  	packBuf[1] = MESSAGE_VERSION;
  	
  	//msgType
  	packBuf[2] = (byte)(msgType & 0xff);
  	packBuf[3] = (byte)((msgType >> 8) & 0xff);
  	
  	//reserved
  	packBuf[4] = 0;
  	packBuf[5] = 0;
  	
  	//msglen
  	packBuf[6] = (byte)(msgLen & 0xff);
  	packBuf[7] = (byte)((msgLen >> 8) & 0xff);
  	
  	return true;
  }
  //发送云台控制消息
  private boolean sendPtzControlMessage(int PtzCmd){
  	byte[] sendBuf = new byte[12];
  	
  	packetMessageHeader(sendBuf, CMD_PTZ_CTRL_REQUEST, 4);
  	
  	sendBuf[8] = (byte)PtzCmd;
		sendBuf[9] = 1;
		sendBuf[10] = 1;
		sendBuf[11] = 0;
		//sendBuf[12] = 0;
				
		Log.d("PlayDemo1", "sendPtzControlMessage");
  	try{
  		cmdOutputStream.write(sendBuf, 0, 12);
  		
  	}catch(Exception e){
  		Log.d("PlayDemo1", "sendPtzControlMessage Exception");
  		return false;
  	}
  	
  	return true;
  }
  //发送视频请求消息
  private boolean sendVideoRequestMessage(){
	  	byte[] sendBuf = new byte[64];
	  	
	  	packetMessageHeader(sendBuf, STREAM_REQUEST, 16);
	  	
	  	sendBuf[8] = 0;
		sendBuf[9] = 0;
		sendBuf[10] = 1;
		//sendBuf[11] = 0;
		//sendBuf[12] = 0;
		
	
		sendBuf[18] = 18;
		sendBuf[19] = 0;
		

	    sendBuf[20] = (byte)229;
	    sendBuf[21] = 0;
	    sendBuf[22] = (byte)168;
	    sendBuf[23] = (byte)192;
				
  	
  	try{
  		cmdOutputStream.write(sendBuf, 0, 24);
  		
  	}catch(Exception e){
  		Log.d("PlayDemo", "sendVideoRequestMessage Exception");
  		return false;
  	}
  	
  	return true;
  }
  
  //发送视频通知
  private boolean sendVideoNotify(){
  	byte[] sendBuf = new byte[32];
  	
  	packetMessageHeader(sendBuf, STREAM_NOTIFY, 4);  
	
  	sendBuf[8]=_VideoID[0];
  	sendBuf[9]=_VideoID[1];
  	try{
  		mediaOutputStream.write(sendBuf, 0, 12);
  		
  	}catch(Exception e){
  		Log.d("PlayDemo", "sendVideoNotify Exception");
  		return false;
  	}
  	
  	return true;
  	
  }
  
  //发送注册消息
  private boolean sendRegisterMessage(){
  	
  	byte[] sendBuf = new byte[64];
  	
  	packetMessageHeader(sendBuf, CMD_REGISTER_REQUEST, 24);
  	
  	sendBuf[8] = 1;
		sendBuf[9] = 0;
		sendBuf[10] = 0;
		sendBuf[11] = 0;
		sendBuf[12] = 1;
		sendBuf[13] = 2;
		sendBuf[14] = 0;
		sendBuf[15] = 0;
		
		byte[] b = _strUser.getBytes();
		
		for(int i=0;i<b.length && i < 8; i++)
			sendBuf[16 + i] = b[i];
		
		if(_strPwd.length()!=0)
		{
			 byte[] c = _strPwd.getBytes();
			 for(int j=0;j<b.length && j < 8; j++)
				 sendBuf[24 + j] = c[j];
		}
		/*sendBuf[16] = b[0];
		sendBuf[17] = b[1];
		sendBuf[18] = b[2];
		sendBuf[19] = b[3];
		sendBuf[20] = b[4];*/
				
  	
  	try{
  		cmdOutputStream.write(sendBuf, 0, 32);
  		
  	}catch(Exception e){
  		Log.d("PlayDemo", "sendRegisterMessage Exception");
  		return false;
  	}
  	
  	return true;
  }
  
  //命令消息接收线程
  class CommandThread implements Runnable{
  	
  	private byte[] tmpBuf = new byte[1024];
  	

	
		public void run() {
			
			Log.d("PlayDemo", "CommandThread Runing...");
			
			try{
				//建立命令连接
				Log.d("PlayDemo", "_strIpAddr " + _strIpAddr);
				Log.d("PlayDemo", "_httpPort " + String.valueOf(_httpPort));
				cmdSocket = new Socket(_strIpAddr, _httpPort);				
				cmdInputStream = cmdSocket.getInputStream();
				cmdOutputStream = cmdSocket.getOutputStream();
				
				Log.d("PlayDemo", "sendRegisterMessage begin...");
				//发送注册消息
				if(!sendRegisterMessage()){
					closeCommandSocket();
					Log.d("PlayDemo", "sendRegisterMessage failed");
					return ;
				}
				
				Log.d("PlayDemo", "sendRegisterMessage end...");
				
			}catch(Exception e){
				Log.d("PlayDemo", "new cmd socket exception");
				return ;
			}
			
			
			// TODO Auto-generated method stub
			while(bCommandThreadRuning){			
				int msgType = 0;
				int msgLen = 0;
				
				try{
					
					//Log.d("PlayDemo", "recvHeader begin...");
					//接收数据头
					if(!recvHeader(cmdInputStream, tmpBuf)){
						closeCommandSocket();
						Log.d("PlayDemo", "command recvHeader failed");
						return;
					}
					
					///Log.d("PlayDemo", "recvHeader end...");
					
					msgType = getMsgType(tmpBuf);
					msgLen = getMsgLen(tmpBuf);	
					
					//Log.d("PlayDemo", "msgType:"+ msgType + " msgLen:"+msgLen);
					
					
					//Log.d("PlayDemo", "recvData begin...");
					//接收数据
					if(!recvData(cmdInputStream, tmpBuf, msgLen)){
						closeCommandSocket();
						Log.d("PlayDemo", "command recv Data failed");
						return;
					}
					//Log.d("PlayDemo", "recvData end...");
					
					//处理命令消息
					processCmdMessage(msgType, msgLen, tmpBuf);
					
				}catch(Exception e){
					e.printStackTrace();
					closeCommandSocket();
					Log.d("PlayDemo", "command socket Exception");
					return;
				}
				
			}
		}		
  	
  }
  
  private void closeCommandSocket(){
		try{
			cmdInputStream.close();
			cmdOutputStream.close();
			cmdSocket.close();
			
			timer.cancel();
		}catch(Exception e){
			
		}
		
	}
  
  private void closeMediaSocket(){
		try{
			mediaInputStream.close();
			mediaOutputStream.close();
			mediaSocket.close();
		}catch(Exception e){
			
		}		
	}    
  
  
  @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	  
		super.onDestroy();
		this.bCommandThreadRuning = false;
		this.bMediaThreadRuning = false;
		this.bSendCommandThreadRuning = false;
		this.bDisplayThreadRuning = false;
		
		closeCommandSocket();
		closeMediaSocket();
	
		
	}
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case Menu.FIRST + 3:
          _FullScreen = !_FullScreen;
          break;
      case Menu.FIRST + 5:   
    	  
    	  /*
          if (menuDialog == null) {
              menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
          } else {
        	  menuDialog.dismiss();
              menuDialog.show();
          }
          */
          break;
      }
      return  true;
  }
  @Override
  public boolean onMenuOpened(int featureId, Menu menu) {	  
	  /*
      if (menuDialog == null) {
          menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
      } else {
          menuDialog.show();
      }
      return false;// 返回为true 则显示系统menu
      */
	  return true;
  }
  @Override
  public void onOptionsMenuClosed(Menu menu) {
      //Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
      //Toast.makeText(this,"选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单",Toast.LENGTH_LONG).show();

      // 如果返回false，此方法就把用户点击menu的动作给消费了，onCreateOptionsMenu方法将不会被调用

      return true;

  }
/*
  private SimpleAdapter getMenuAdapter(String[] menuNameArray,
          int[] imageResourceArray) {
      ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
      for (int i = 0; i < menuNameArray.length; i++) {
          HashMap<String, Object> map = new HashMap<String, Object>();
          map.put("itemImage", imageResourceArray[i]);
          map.put("itemText", menuNameArray[i]);
          data.add(map);
      }
      SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
              R.layout.item_menu, new String[] { "itemImage", "itemText" },
              new int[] { R.id.item_image, R.id.item_text });
      return simperAdapter;
  }
*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/* * add()方法的四个参数，依次是：         *        
		 * 1、组别，如果不分组的话就写Menu.NONE,         *      
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单         *     
		 * 3、顺序，那个菜单现在在前面由这个参数的大小决定         *    
		  * 4、文本，菜单的显示文本         */
	        // setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以
	        // android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的

	        menu.add(Menu.NONE, Menu.FIRST + 3, 5, "全屏").setIcon(R.drawable.menu_fullscreen);	    
	        //menu.add(Menu.NONE, Menu.FIRST + 5, 6, "退出").setIcon(R.drawable.menu_quit);

	        
	        /*
	        menuView = View.inflate(this, R.layout.gridview_menu, null);
	        // 创建AlertDialog
	        menuDialog = new AlertDialog.Builder(this).create();
	        menuDialog.setView(menuView);
	        
	        menuDialog.setOnKeyListener(new OnKeyListener(){

				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
	                    dialog.dismiss();
					return false;
				}
	        });
	     
	       
	        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
	        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
	        // 监听menu选项 **
	        menuGrid.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) new OnItemClickListener());
	        */	        
		//getMenuInflater().inflate(R.menu.activity_my_camera, menu);
		return true;
	}
	/*
	public class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			switch (arg2) {
	        case ITEM_SEARCH:// 搜索
	            break;
	        case ITEM_FILE_MANAGER:// 文件管理
	            break;
	        case ITEM_DOWN_MANAGER:// 下载管理
	            break;
	        case ITEM_FULLSCREEN:// 全屏
	        	_FullScreen = !_FullScreen;
	            break;
	        case ITEM_MORE:// 翻页
	            if (isMore) {
	                menuGrid.setAdapter(getMenuAdapter(menu_name_array2,
	                        menu_image_array2));
	                isMore = false;
	            } else {// 首页
	                menuGrid.setAdapter(getMenuAdapter(menu_name_array,
	                        menu_image_array));
	                isMore = true;
	            }
	            menuGrid.invalidate();// 更新menu
	            menuGrid.setSelection(ITEM_MORE);
	            break;
	        }	
		}

	}
	
	private boolean isMore = false;// menu菜单翻页控制
    AlertDialog menuDialog;// menu菜单Dialog
    GridView menuGrid;
    View menuView;
    
    private final int ITEM_SEARCH = 0;// 搜索
    private final int ITEM_FILE_MANAGER = 1;// 文件管理
    private final int ITEM_DOWN_MANAGER = 2;// 下载管理
    private final int ITEM_FULLSCREEN = 3;// 全屏
    private final int ITEM_MORE = 11;// 菜单

    
    //** 菜单图片 **
    int[] menu_image_array = { R.drawable.menu_search,
            R.drawable.menu_filemanager, R.drawable.menu_downmanager,
            R.drawable.menu_fullscreen, R.drawable.menu_inputurl,
            R.drawable.menu_bookmark, R.drawable.menu_bookmark_sync_import,
            R.drawable.menu_sharepage, R.drawable.menu_quit,
            R.drawable.menu_nightmode, R.drawable.menu_refresh,
            R.drawable.menu_more };
    //** 菜单文字 **
    String[] menu_name_array = { "搜索", "文件管理", "下载管理", "全屏", "网址", "书签",
            "加入书签", "分享页面", "退出", "夜间模式", "刷新", "更多" };
   //** 菜单图片2 **
    int[] menu_image_array2 = { R.drawable.menu_auto_landscape,
            R.drawable.menu_penselectmodel, R.drawable.menu_page_attr,
            R.drawable.menu_novel_mode, R.drawable.menu_page_updown,
            R.drawable.menu_checkupdate, R.drawable.menu_checknet,
            R.drawable.menu_refreshtimer, R.drawable.menu_syssettings,
            R.drawable.menu_help, R.drawable.menu_about, R.drawable.menu_return };
    //** 菜单文字2 **
    String[] menu_name_array2 = { "自动横屏", "笔选模式", "阅读模式", "浏览模式", "快捷翻页",
            "检查更新", "检查网络", "定时刷新", "设置", "帮助", "关于", "返回" };
	
    */
    
	class MediaThread implements Runnable{

		private byte[] tmpBuf = new byte[1048];

		public void run() {	
			
			while(true){
				
				if(!bMediaThreadRuning){
					return;
				}
				
				try{
					
					if(!bStartMediaRequest){
						Thread.sleep(100);
						continue;
					}
					
					mediaSocket = new Socket(_strIpAddr, _httpPort);
					mediaInputStream = mediaSocket.getInputStream();
					mediaOutputStream = mediaSocket.getOutputStream();
					
					//Log.d("PlayDemo", "sendVideoNotify begin...");
					if(!sendVideoNotify()){
						Log.d("PlayDemo", "sendVideoNotify failed");
						closeMediaSocket();
						return;
					}
					
					break;
				}catch(Exception e){
					
				}

			}
	
			// TODO Auto-generated method stub
			while(bMediaThreadRuning){
				try{
					
					int msgType = 0;
					int msgLen = 0;					
			
						
					//Log.d("PlayDemo", "media recvHeader begin...");
					//接收数据头
					if(!recvHeader(mediaInputStream, tmpBuf)){
						closeMediaSocket();
						Log.d("PlayDemo", "media recvHeader failed");
						return;
					}
					
					//Log.d("PlayDemo", "media recvHeader end...");
						
					msgType = getMsgType(tmpBuf);
					msgLen = getMsgLen(tmpBuf);	
					
					//Log.d("PlayDemo", "media" + "msgType:"+ msgType + " msgLen:"+msgLen);
					
					
					//Log.d("PlayDemo", "media recvData begin...");
					//接收数据
					if(!recvData(mediaInputStream, tmpBuf, msgLen)){
						closeMediaSocket();
						Log.d("PlayDemo", "media recv Data failed");
						return;
					}
					
					//Log.d("PlayDemo", "media recvData end...");
				  	  //处理媒体消息
					processMediaMessage(msgType, msgLen, tmpBuf);
						
				}catch(Exception e){
					e.printStackTrace();
					closeMediaSocket();
					Log.d("PlayDemo", "media socket Exception");
					return;
				}
				
			}
			
		}
  	
  }
	
	 //视图内部类
    class MyView extends SurfaceView implements SurfaceHolder.Callback
    {
    	
        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            _holder = this.getHolder();
            _holder.addCallback(this);
            this.setOnTouchListener(new SurfaseOntuch());
        }
 
        
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            // TODO Auto-generated method stub
        	Surfacerect.right = width;
        	Surfacerect.bottom = height;
			  
        	if(width *240 /320 >= height)
        	{
        		Rrect.top = 0 ;
        		Rrect.bottom=height;
	        	Rrect.right=height * 320 / 240;// + (width - height * 320 /240) /2 ;	        	
	        	Rrect.left = (width - Rrect.right) /2 ;//(width - height * 320 /240) /2 ;
	        	Rrect.right=Rrect.right+Rrect.left;
        	}
        	else
        	{
  	        	Rrect.left = 0;      		
	        	Rrect.right=width;
	        	Rrect.bottom=width * 240/320;   	        	
	        	Rrect.top = (height -  Rrect.bottom) / 2 ;
	        	Rrect.bottom=Rrect.bottom+Rrect.top;
        	}
        	Canvas c = null;
        	try
            {
                synchronized (_holder)
                {
                	
                    c = _holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                    c.drawColor(Color.BLACK);                                         
                    c.drawText("视频导入中,请稍候...", Rrect.right / 2 - 80, Rrect.bottom / 2 -10, p);
                    //c.drawText("这是第"+(count++)+"秒", 100, 310, p);
                    //Thread.sleep(1000);//睡眠时间为1秒
                }
            }
            catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            finally
            {
                if(c!= null)
                {
                    _holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。

                }
            }
        	
        	//Log.d("PlayDemo","width->" + String.valueOf(width));            
        }
       
        public void surfaceCreated(SurfaceHolder holder) {    
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    	protected void onRestoreInstanceState(Bundle savedInstanceState) {  
    	}  
        
    }
     
	
	
}