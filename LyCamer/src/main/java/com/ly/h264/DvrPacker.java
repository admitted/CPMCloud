package com.ly.h264;

import android.util.Log;

public class DvrPacker {
	
	/**********************IPC命令码定义****************************/
	public static final int MESSAGE_VERSION = 0x10;
	public static final int MSG_HEADER_LENGTH = 8;
	
	public static final int CMD_REGISTER_REQUEST = 0x0101;
	public static final int CMD_REGISTER_RESPONSE = 0x8101;
	public static final int ALARM_MOTION_DETECT = 0x8560;
	public static final int ALARM_GPIO = 0x8561;

	public static final int STREAM_REQUEST = 0x0201;
	public static final int STREAM_RESPONSE = 0x8201;
	public static final int STREAM_NOTIFY = 0x0202;

	public static final int SEND_VIDEO_DATA = 0x0301;
	public static final int SEND_AUDIO_DATA = 0x0302;
	public static final int CMD_HEART_BEAT_REQUEST = 0x0103;
	public static final int CMD_HEART_BEAT_RESPONSE = 0x8103;
	
	
	/**********************DVR命令码定义****************************/
	public static final short LOGIN_REQ=1000;//Or 1000登 录请求
	public static final short LOGIN_RSP=1001;//登录响应
	public static final short LOGOUT_REQ=1001;//登出请求
	public static final short LOGOUT_RSP=1002;//登出响应
	public static final short FORCELOGOUT_REQ=1003;//强制登出请求
	public static final short FORCELOGOUT_RSP=1004;//强制登出响应
	public static final short KEEPALIVE_REQ=1005;//保活请求
	public static final short KEEPALIVE_RSP=1006;//保活响应

	public static final short SYSINFO_REQ=1020;//获取系统信息请求
	public static final short SYSINFO_RSP=1021;//获取系统信息请求响应
	
	
	public static final short MONITOR_REQ=1410;//实时监视请求
	public static final short MONITOR_RSP=1411;//实时监视请求响应
	public static final short MONITOR_DATA=1412;//实时监视数据
	public static final short MONITOR_CLAIM=1413;//监视认领请求
	public static final short MONITOR_CLAIM_RSP=1414;//监视认领请求响应
	
	public static final short PTZ_REQ=1400;//云台控制请求
	public static final short PTZ_RSP=1401;//云台控制响应
	/**********************命令码定义****************************/
	
	public static int MsgHeadLen=20;
	
	
	public static void IntToByte(int _Value, byte[] Buf, int Offset)
	{
		
		Buf[Offset + 3] = (byte) (_Value >> 24);  
		Buf[Offset + 2] = (byte) (_Value >> 16); 
		Buf[Offset + 1] = (byte) (_Value >> 8); 
		Buf[Offset ] = (byte) (_Value >> 0);    
		/*
		for(int i = 0;i < 4;i++)
			Buf[Offset + 3 - i] = (byte)(_Value >> (24 - i * 8)); 
	 	*/
	}
	
	public static int ByteToInt(byte[] Buf, int Offset)
	{
		
  		int _Var = 0;
  		_Var=Buf[Offset+3] & 0xff;
  		_Var = _Var << 8;
  		_Var+=Buf[Offset+2] & 0xff;
  		_Var = _Var << 8;
  		_Var+=Buf[Offset+1] & 0xff;
  		_Var = _Var << 8;
  		_Var+=Buf[Offset] & 0xff;
  		return _Var;
  		
		// return (int) ((((Buf[Offset + 3] & 0xff) << 24) | ((Buf[Offset + 2] & 0xff) << 16) | ((Buf[Offset + 1] & 0xff) << 8) | ((Buf[Offset] & 0xff) << 0)));
	}
	
	public static short ByteToShort(byte[] Buf, int Offset)
	{
  		/*return (short)(Buf[Offset+1] << 8 +Buf[Offset]);*/
		return (short) (((Buf[Offset + 1] << 8) | Buf[Offset] & 0xff));   

	}
	
	public static void ShortToByte(short _Value, byte[] Buf, int Offset)
	{
		Buf[Offset] = (byte) (_Value >> 0);  
		Buf[Offset + 1] = (byte) (_Value >> 8);    
	}
	
	//会话ID
	public static  int SessionID( byte[] MsgBuf)
	{		
		return ByteToInt(MsgBuf,4);
	}
	
	//包序号
	public static  int Sequence( byte[] MsgBuf)
	{		
		return ByteToInt(MsgBuf,8);		
	}	
	
	//消息码
	public static  short MessageID( byte[] MsgBuf)
	{		
		return ByteToShort(MsgBuf,14);		
	}
	
	//数据区长度，字节为单位，最大不超过16K
	public static  int DataLength( byte[] MsgBuf)
	{		
		return ByteToInt(MsgBuf,16);		
	}
	//数据区长度，字节为单位，最大不超过1K + 32
	public static  int HwDataLength( byte[] MsgBuf)
	{		
		return ByteToInt(MsgBuf,6);		
	}
	public static String UnPack(byte[] MsgBuf)
	{
		int _len=DataLength(MsgBuf);
		//Log.d("Dvr", "recvHead UnPack->" + String.valueOf(_len));		
		if(_len==0)
			return "";
		else
			return new String(MsgBuf,MsgHeadLen, _len);
	}
	public static String HexLeftPad(int _Val, int Count)
	{
		 String _Temp = Integer.toHexString(_Val);
		 for(int i=Count - _Temp.length();i>0;i--)
			 _Temp="0" + _Temp;
		 return "0x" + _Temp;
	}
	
	public static String MakeMsgComand(short MsgID, String[] MsgParams)
	{
		StringBuilder _MsgBody=new StringBuilder();
		switch(MsgID)
		{	
		case LOGIN_REQ:	
			_MsgBody.append("{ \"EncryptType\" : \"MD5\", \"LoginType\" : \"DVRIP-Web\", \"PassWord\" : \"tlJwpbo6\", \"UserName\" : \"" + MsgParams[0] + "\" }");
			break;
		case LOGIN_RSP:
			break;
		/*case LOGOUT_REQ:			
			break;*/
		case LOGOUT_RSP:			
			break;
		case FORCELOGOUT_REQ:			
			break;
		case FORCELOGOUT_RSP:			
			break;
		case KEEPALIVE_REQ:	//Integer.parseInt(String, 16);
			_MsgBody.append("{ \"Name\" : \"KeepAlive\", \"SessionID\" : \"" + MsgParams[0] + "\" }");
			break;
		case KEEPALIVE_RSP:			
			break;
		case SYSINFO_REQ:			
			break;
		case SYSINFO_RSP:			
			break;
		case MONITOR_REQ:	
			_MsgBody.append("{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Start\", \"Parameter\" : { \"Channel\" : " + MsgParams[0] + ", \"CombinMode\" : \"NONE\", \"StreamType\" : \"Extra1\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \"" + MsgParams[1] + "\" }");
			//_MsgBody.append("{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Start\", \"Parameter\" : { \"Channel\" : " + MsgParams[0] + ", \"CombinMode\" : \"NONE\", \"StreamType\" : \"Main\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \"" + MsgParams[1] + "\" }");
			break;
		case MONITOR_RSP:					
			break;
		case MONITOR_DATA:			
			break;
		case MONITOR_CLAIM:
			_MsgBody.append("{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Claim\", \"Parameter\" : { \"Channel\" : " + MsgParams[0] + ", \"CombinMode\" : \"NONE\", \"StreamType\" : \"Extra1\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \"" + MsgParams[1] + "\" }");
			//_MsgBody.append("{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Claim\", \"Parameter\" : { \"Channel\" : " + MsgParams[0] + ", \"CombinMode\" : \"NONE\", \"StreamType\" : \"Main\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \"" + MsgParams[1] + "\" }");
			break;
		case MONITOR_CLAIM_RSP:			
			break;
		case PTZ_REQ:		
			_MsgBody.append("{ \"Name\" : \"OPPTZControl\", \"OPPTZControl\" : { \"Command\" : \"" + MsgParams[0] + "\", \"Parameter\" : { \"AUX\" : { \"Number\" : 0, \"Status\" : \"On\" }, \"Channel\" : " + MsgParams[1] + ", \"MenuOpts\" : \"Enter\", \"Pattern\" : \"Start\", \"Preset\" : " + MsgParams[2] + ", \"Step\" : " + MsgParams[3] + ", \"Tour\" : 0 } }, \"SessionID\" : \"" + MsgParams[4] + "\" }");
			Log.d("Dvr", _MsgBody.toString());    
			break;
		case PTZ_RSP:			
			break;
		}
		//Log.d("Dvr",  _MsgBody.toString());
		return _MsgBody.toString();
	}
	
	public static void PackHwMsg(byte[] MsgBuf,short SessID, short MsgID, String[] MsgParams)
	{
		MsgBuf[0]=0x10;//版本号
		MsgBuf[1]=0x10;//消息头长度
		//消息命令
		ShortToByte(MsgID, MsgBuf,2);
		//会话ID
		ShortToByte(SessID, MsgBuf,4);
		//消息体长度
		ShortToByte((short)0, MsgBuf,6);
		
		
	}
	
	public static void PackCmdBuf(byte[] MsgBuf,int SessID, int Sequ, short MsgID, String[] MsgParams)
	{
		PackMsg(MsgBuf,SessID,Sequ,MsgID,MakeMsgComand(MsgID,MsgParams));		
	}
	
	public static void PackMsg(byte[] MsgBuf,int SessID, int Sequ, short MsgID, String MsgBody)
	{
		MsgBuf[0]=(byte)255;//Head Flag
		MsgBuf[1]=0;//	VERSION
		IntToByte(SessID, MsgBuf,4);
		IntToByte(Sequ, MsgBuf,8);
		ShortToByte(MsgID, MsgBuf,14);
		IntToByte(MsgBody.length(), MsgBuf,16);
		MsgBody.getBytes(0, MsgBody.length(), MsgBuf, MsgHeadLen);
	}
	
	//休眠线程
	public static void Sleep(int WaitTime)
	{
		try {
			if(WaitTime==0)
				Thread.sleep(0);
			else if(WaitTime<=100)
				Thread.sleep(WaitTime);
			else
				for(int i=0;i<= WaitTime /100 ;i++)
					Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.d("Dvr",  " Thread Sleep Fail ...");
		}
	}
}


