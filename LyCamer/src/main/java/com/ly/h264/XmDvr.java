package com.ly.h264;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//雄迈DVR数据通信类
public class XmDvr implements Runnable{

	
	String s_ip;
	String s_User;
	int i_port;
	boolean bRuning;
	Socket o_Socket;
	InputStream o_InputStream;
	OutputStream o_OutputStream;
	byte[] _recvBuf;
	byte[] _sendBuf;
	public int _SessID;
	public int _CH;
	int _Sequ;
	Timer _timer;
	Handler m_Handler;
	public XmDvr(String _Ip, int _Port,	String _User,int _Channel, Handler _Handler)
	{
		m_Handler=_Handler;
		s_ip=_Ip;
		i_port=_Port;
		s_User=_User;
		_CH=_Channel;
		bRuning = false;
		o_Socket=null;
		o_InputStream=null;
		o_OutputStream=null;
		_recvBuf=null;
		_sendBuf=null;
		_SessID=0;
		 _Sequ=0;
		 _timer = new Timer();
		 //_timer=null;
	}
	
	private TimerTask _keepAliveTask = new TimerTask() {
	    @Override
	    public void run() {    	
	    	if(bRuning)
	    	{
	    	    if(o_OutputStream!=null)
	    	    {	    	    	
	    			String[] _CmdParam=new String[1];
					_CmdParam[0]=DvrPacker.HexLeftPad(_SessID, 8);
		    		if(!SendCmd(DvrPacker.KEEPALIVE_REQ, _CmdParam))
		    			Disponse();
	    		}
	    	}
	    }
	 };
	
	//连接设备
	public void run() {
		bRuning=true;
		Log.d("Dvr", "Soket Thread Start ...");
		try {
			o_Socket = new Socket(s_ip, i_port);				
			o_InputStream = o_Socket.getInputStream();
			o_OutputStream =o_Socket.getOutputStream();
			_recvBuf=new byte[16384 + DvrPacker.MsgHeadLen];
			_sendBuf=new byte[16384 + DvrPacker.MsgHeadLen];
			
			Log.d("Dvr", "Soket Connect Suess ...");
		}  catch (UnknownHostException e) {
			Log.d("Dvr", "Soket UnknownHostException ...");
			
		} catch (IOException e) {//e.printStackTrace();
			Log.d("Dvr", "Soket IOException  ...");
		}
		String[] _LoginParam=new String[1];
		_LoginParam[0]=s_User;
		SendCmd(DvrPacker.LOGIN_REQ, _LoginParam);
		while(bRuning)
		{			
			if(o_InputStream==null) break;
			Sleep(50);
			//判断是否有数据
			try {
				if(o_InputStream.available()<=32)
					continue;
			} catch (IOException e) {
				Log.d("Dvr", "o_InputStream.available() -> IOException ...");
			}
			
			if(!recvData(o_InputStream, _recvBuf))
			{
				Log.d("Dvr", "Socket recvData Fail ...");
				break;
			}
			else
			{
				//System.currentTimeMillis()
				//Log.d("Dvr",DvrPacker.UnPack(_recvBuf));	
				_SessID=DvrPacker.SessionID(_recvBuf);
				switch(DvrPacker.MessageID(_recvBuf))
				{
					case DvrPacker.LOGIN_RSP:
						 Message _msg = new Message();
						 _msg.what=0;
						 m_Handler.sendMessage(_msg);
						_timer.schedule(_keepAliveTask, 10000, 10000);
						break;
					case DvrPacker.LOGOUT_RSP:	
						break;
					case DvrPacker.FORCELOGOUT_RSP:
						break;
					case DvrPacker.KEEPALIVE_RSP:
						break;
					case DvrPacker.SYSINFO_RSP:
						break;
					case DvrPacker.MONITOR_RSP:
		    			String[] _CmdParam=new String[1];
						_CmdParam[0]=DvrPacker.HexLeftPad(_SessID, 8);
			    		SendCmd(DvrPacker.KEEPALIVE_REQ, _CmdParam);			    			
						break;
					case DvrPacker.MONITOR_DATA:
						break;
					case DvrPacker.MONITOR_CLAIM_RSP:
						break;
					case DvrPacker.PTZ_RSP:	
						break;
					default:
						Log.d("Dvr","Unknown Command ...");	
							break;
				}
				
				//break;
			}
		}
	
		try {
			if(null!=o_InputStream)
			{
				o_InputStream.close();
				o_InputStream=null;
			}
			if(null!=o_OutputStream)
			{
				o_OutputStream.close();
				o_OutputStream=null;
			}
			if(null!=o_Socket)
				o_Socket.close();
		} catch (IOException e) {
			Log.d("Dvr", "o_Socket Close IOException...");//e.printStackTrace();
		}
		Disponse();
		Log.d("Dvr", "Soket Thread Exit ...");
	}

	//休眠线程
	void Sleep(int WaitTime)
	{
		try {
			if(WaitTime==0)
				Thread.sleep(0);
			else if(WaitTime<=100)
				Thread.sleep(WaitTime);
			else
				for(int i=0;bRuning &&i<= WaitTime /100 ;i++)
					Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.d("Dvr",  " Thread Sleep Fail ...");
		}
	}
	
	boolean recvLenData(InputStream is, byte[] recvBuf, int _offset, int Datalen)
	{
		int recvSize = 0;
		int nRed=-1;
		for(int i=0;bRuning &&i<5 && recvSize <Datalen ;i++)
		{
			try {
				nRed = is.read(recvBuf, _offset + recvSize, Datalen - recvSize);
			} catch (IOException e) {
				Log.d("Dvr", "recvLenData IOException");    		
			}
  			if(nRed <=0){ 	Sleep(50); }//Log.d("Dvr", "recvHead Data Over ");    	
  			else{recvSize += nRed;}
		}
		return recvSize==Datalen ; 
	}
	
	//读取数据体
	boolean recvData(InputStream is, byte[] recvBuf){
		if(recvLenData(is, recvBuf, 0,DvrPacker.MsgHeadLen ))
			return recvLenData(is, recvBuf, DvrPacker.MsgHeadLen,DvrPacker.DataLength(recvBuf) );
		else
			return false;
	}
	
	public boolean SendCmd(short _MsgID, String[] _Params)
	{
		DvrPacker.PackCmdBuf(_sendBuf,_SessID,_Sequ++, _MsgID, _Params);	
		return SendMsgBuf(o_OutputStream,_sendBuf);
	}
	
	boolean SendMsgBuf(OutputStream os, byte[] recvBuf)
	{
		//Log.d("Dvr", "DataLength" + String.valueOf(DvrPacker.DataLength(recvBuf)));
		try {
			os.write(recvBuf,0,DvrPacker.DataLength(recvBuf) + DvrPacker.MsgHeadLen);
		} catch (IOException e) {
			Log.d("Dvr", "SendData IOException");
			return false;
		}
		return true;
	}
	
	//释放
	public void Disponse()
	{
		bRuning=false;
		if(_timer!=null)
		{
			_timer.cancel();
			_timer=null;
		}
	}

	public void PtzControl(String Command, int Preset, int Step) {
		String[] _CmdParam=new String[5];
		_CmdParam[0]=Command;
		_CmdParam[1]=String.valueOf(_CH);
		_CmdParam[2]=String.valueOf(Preset);
		_CmdParam[3]=String.valueOf(Step);
		_CmdParam[4]="0x" + Integer.toHexString(_SessID);
		SendCmd(DvrPacker.PTZ_REQ, _CmdParam);				
	}
}
