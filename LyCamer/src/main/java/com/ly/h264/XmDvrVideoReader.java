package com.ly.h264;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.util.Log;

//雄迈DVR数据通信类
public class XmDvrVideoReader implements Runnable{

	
	String s_ip;
	int i_port;
	boolean bRuning;
	Socket o_Socket;
	InputStream o_InputStream;
	OutputStream o_OutputStream;
	byte[] _recvBuf;
	byte[] _sendBuf;
	private byte[] ArrGiveUp;
	int i_SessID;
	int i_Sequ;
	int i_CH;
	XmDvr o_XmDvr;
	//视频数据缓存
	//public VideoBuffer _VideoBuffer;
	
	int _InsertTotalLen;
	int _CurFrameDataLen;
	int _CurFrameHeadLen;
	byte[] _FrameHeader;
	int _RevFrameHeaderLen;
	int _CurWidth;
	int _CurHeight;
	
	VideoDataList m_VideoDataList;
	public XmDvrVideoReader(String _Ip, int _Port, int _SessID, int _CH, XmDvr _XmDvr, VideoDataList _VideoDataList)
	{		
		m_VideoDataList=_VideoDataList;
		s_ip=_Ip;
		i_port=_Port;
		bRuning = false;
		o_Socket=null;
		o_InputStream=null;
		o_OutputStream=null;
		_recvBuf=null;
		_sendBuf=null;
		ArrGiveUp=new byte[1024*50];
		_recvBuf=new byte[16384 + DvrPacker.MsgHeadLen];
		_sendBuf=new byte[16384 + DvrPacker.MsgHeadLen];
		i_Sequ=0;
		i_SessID=_SessID;
		i_CH=_CH;
		//_VideoBuffer=null;
		//_VideoBuffer=new VideoBuffer();
		
		_InsertTotalLen=0;
		_CurFrameDataLen=0;
		_CurFrameHeadLen=0;
		_FrameHeader=new byte[16];
		_RevFrameHeaderLen=0;
		
	    //_CurWidth = 704;
		//_CurHeight=576;
		_CurWidth = 352;
		_CurHeight=288;
		o_XmDvr=_XmDvr;
	}
	//连接设备
	public void run() {
		bRuning=true;
		
		
		Log.d("Dvr", "Video Soket Thread Start ...");
		try {
			o_Socket = new Socket(s_ip, i_port);
			o_InputStream = o_Socket.getInputStream();
			o_OutputStream =o_Socket.getOutputStream();
			Log.d("Dvr", "Video Soket Connect Suess ...");
		}  catch (UnknownHostException e) {
			Log.d("Dvr", "Video Soket UnknownHostException ...");
			
		} catch (IOException e) {//e.printStackTrace();
			Log.d("Dvr", "Video Soket IOException  ...");
		}
		String[] _CmdParam=new String[2];
		_CmdParam[0]=String.valueOf(i_CH);
		_CmdParam[1]="0x" + Integer.toHexString(i_SessID);
		Log.d("Dvr", "SendCmd MONITOR_CLAIM Begin ...");
		if(o_OutputStream!=null?SendCmd(DvrPacker.MONITOR_CLAIM, _CmdParam):false)
		{
			Log.d("Dvr", "SendCmd MONITOR_CLAIM End ...");
				while(bRuning)
				{			
						if(o_InputStream==null) break;
							Sleep(30);
						try {
							//Log.d("Dvr", "Video Socket available ->" + String.valueOf(o_InputStream.available()));
							if(o_InputStream.available()<=32)//判断是否有数据								
								continue;
						} catch (IOException e) {
							Log.d("Dvr", "Video o_InputStream.available() -> IOException ...");
						}
						if(!recvData(o_InputStream, _recvBuf))
						{
							break;
						}
						else
						{
							//Log.d("Dvr", "Video Socket recvData Suess ...");//收到认领响应，开始读数据												
							//i_SessID=DvrPacker.SessionID(_recvBuf);
							//break;
						}
				}
		}
		
		try {
			if(null!=o_InputStream)
				o_InputStream.close();
			if(null!=o_OutputStream)
				o_OutputStream.close();
			if(null!=o_Socket)
				o_Socket.close();
		} catch (IOException e) {
			Log.d("Dvr", "o_Socket Close IOException...");//e.printStackTrace();
		}		
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
	
	public byte DataType()
	{
		//FC- >I帧，FD- >P帧，FA- >音频，FE- >图片帧头部，F9- >信息帧头部
		return _FrameHeader[3];		
	}
	//图像宽度
	public int Width()
	{
		//if(_FrameHeader[3] == 0xFC)
			//_CurWidth=_FrameHeader[ 6] * 8;		
		return _CurWidth;
	}
	//图像高度
	public int Height()
	{
		//if(_FrameHeader[3] == 0xFC)
			//_CurHeight=_FrameHeader[7] * 8;		
		return _CurHeight;
	}
	
	public int LeaveLen()
	{
		int len=0;
		//FC- >I帧，FD- >P帧，FA- >音频，FE- >图片帧头部，F9- >信息帧头部
		switch(_FrameHeader[3] & 0xFF)
		{
		case 0xFC:
		case 0xFE:
			len=12;
			break;
		default:
			len=4;
			break;	
		}
		return len;
		
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
				Log.d("Dvr", "Video recvLenData IOException");    		
			}
  			if(nRed <=0){ 	Sleep(50); }//Log.d("Dvr", "recvHead Data Over ");    	
  			else{recvSize += nRed;}
		}
		//Log.d("Dvr", "recvLenData  recvSize==Datalen->" + String.valueOf(recvSize==Datalen ));    
		return recvSize==Datalen ; 
	}
	//读取数据体
	int recvDataByMaxLen(InputStream is, byte[] recvBuf, int _offset, int Datalen, int _MaxLen)
	{
		int recvSize = 0;
		int nRed=-1;
		Datalen=Datalen<_MaxLen ? Datalen : _MaxLen;
		for(int i=0;bRuning &&i<5 && recvSize <Datalen ;i++)
		{
			try {
				nRed = is.read(recvBuf, _offset + recvSize, Datalen - recvSize);
			} catch (IOException e) {
				Log.d("Dvr", "Video recvLenData IOException");    		
			}
  			if(nRed <=0){ 	Sleep(50); }
  			else{recvSize += nRed;}
		}
		return recvSize ; 
	}
	
	boolean recvFame(InputStream is, int PackLen)
	{
		//Log.v("Dvr", "Cur Packet Data Lengh -->" +  String.valueOf(PackLen));
		
		int _CurRevSize=0;
		while(PackLen>0)
		{
			while(PackLen>0)//读帧消息头4个字节
			{
				if(_RevFrameHeaderLen<4)
				{
					_CurRevSize=recvDataByMaxLen(is,_FrameHeader,_RevFrameHeaderLen, 4-_RevFrameHeaderLen, PackLen);
					//Log.d("Dvr", "Red Packet Lengh -->" +  String.valueOf(_CurRevSize));	
					if(_CurRevSize>0)
					{
						_RevFrameHeaderLen+=_CurRevSize;
						PackLen=PackLen-_CurRevSize;
						if(_RevFrameHeaderLen==4)
						{						
							//还要判断是否为帧头
							if(_FrameHeader[0] != 0 || _FrameHeader[1] != 0 || _FrameHeader[2] != 1)	
							{
								Log.e("Dvr", "Rev Frame Error Type .");														
								return false;
							}
							else
							{								
								//Log.d("Dvr", "Rev Frame Header Type -->0x" +  Integer.toHexString(_FrameHeader[3] & 0xFF));	
								_CurFrameHeadLen=GetHeadLen();									
								break;
							}
						}
					}
				}
				else
				{
					break;//已经读过
				}
			}

			while(PackLen>0)//读帧消息头剩余部分
			{
				if(_RevFrameHeaderLen<_CurFrameHeadLen)
				{
					_CurRevSize=recvDataByMaxLen(is,_FrameHeader,_RevFrameHeaderLen, _CurFrameHeadLen-_RevFrameHeaderLen, PackLen);
					//Log.d("Dvr", "Red Packet Lengh -->" +  String.valueOf(_CurRevSize));	
					if(_CurRevSize>0)
					{
						_RevFrameHeaderLen+=_CurRevSize;
						PackLen=PackLen-_CurRevSize;
						if(_RevFrameHeaderLen==_CurFrameHeadLen)
						{							
							//Log.v("Dvr", "Read Frame Head Data End, Lengh -->" +  String.valueOf(_CurFrameHeadLen));
							_CurFrameDataLen=GetFrameDataLengh();
							//Log.d("Dvr", "Frame Data Lengh -->" +  String.valueOf(_CurFrameDataLen));	
							break;
						}
					}
				}
				else
				{
					break;
				}
			}
			
	
			while(PackLen>0)//读帧数据
			{
					if(_InsertTotalLen<_CurFrameDataLen)
					{
							//判断帧类型
							//FC- >I帧，FD- >P帧，FA- >音频，FE- >图片帧头部，F9- >信息帧头部
						/*
							if((_FrameHeader[3] & 0xFF) == 0xFC)
							{								
									_CurRevSize=_VideoBuffer.RevData(is, true, Width(), Height(), _CurFrameDataLen -_InsertTotalLen, PackLen,_CurFrameDataLen);
							}
							else if((_FrameHeader[3] & 0xFF) == 0xFD)
							{
									_CurRevSize=_VideoBuffer.RevData(is, false, Width(), Height(), _CurFrameDataLen - _InsertTotalLen, PackLen,_CurFrameDataLen);
							}
							else
							{
									Log.i("Dvr","Rev Other Frame Data ...Lengh->" + String.valueOf(PackLen));	
									_CurRevSize=recvDataByMaxLen(is,ArrGiveUp,0, _CurFrameDataLen-_InsertTotalLen, PackLen);
							}
							*/
							//Log.d("Dvr", "Red Packet Lengh -->" +  String.valueOf(_CurRevSize));	
							_CurRevSize=recvDataByMaxLen(is,ArrGiveUp,_InsertTotalLen, _CurFrameDataLen-_InsertTotalLen, PackLen);
							if(_CurRevSize>0)
							{
								_InsertTotalLen+=_CurRevSize;
								PackLen=PackLen-_CurRevSize;
								if(_CurFrameDataLen==_InsertTotalLen)
								{								 
									m_VideoDataList.addVideoData(new VideoData(_InsertTotalLen, (_FrameHeader[3] == 0xFC) ,ArrGiveUp.clone()));
									//Log.v("Dvr", "Rev Full Frame Data. Lengh -->" +  String.valueOf(_CurRevSize));//读满一帧
									_RevFrameHeaderLen=0;
									_InsertTotalLen=0;
									break;
								}
							}
					}
					else
					{
						break;
					}
			}		
		}
		return true;
	}
	
	
	public int GetFrameDataLengh()
	{
		int len=0;
		//FC- >I帧，FD- >P帧，FA- >音频，FE- >图片帧头部，F9- >信息帧头部
		switch(_FrameHeader[3] & 0xFF)
		{
		case 0xFC:
			len=DvrPacker.ByteToInt(_FrameHeader, 12);
			break;
		case 0xFD:
			len=DvrPacker.ByteToInt(_FrameHeader, 4);
			break;
		case 0xFA:
			len=(int)DvrPacker.ByteToShort(_FrameHeader, 6);
			break;
		case 0xFE:
			len=DvrPacker.ByteToInt(_FrameHeader, 12);
			break;
		case 0xF9:
			len=(int)DvrPacker.ByteToShort(_FrameHeader, 6);
			break;	
		}
		return len;
	}
	
	int GetHeadLen()
	{
		return ((_FrameHeader[3] & 0xFF) == 0xFC || (_FrameHeader[3] & 0xFF) == 0xFE) ? 16 : 8;
	}
	
	
	boolean recvData(InputStream is, byte[] recvBuf){
		
		boolean _Result=false;
		if(recvLenData(is, recvBuf, 0,DvrPacker.MsgHeadLen ))//读消息头
		{
			//判断消息类型
			switch(DvrPacker.MessageID(_recvBuf))
			{
				case DvrPacker.MONITOR_DATA://码流帧消息		
					_Result=recvFame(is,DvrPacker.DataLength(recvBuf));
					break;
				case DvrPacker.MONITOR_CLAIM_RSP:						
						_Result=recvLenData(is, recvBuf, DvrPacker.MsgHeadLen,DvrPacker.DataLength(recvBuf));
						//Log.d("Dvr",DvrPacker.UnPack(_recvBuf));	
						if(_Result)
						{
							String[] _CmdParam=new String[2];
							_CmdParam[0]=String.valueOf(i_CH);
							_CmdParam[1]="0x" + Integer.toHexString(i_SessID);
							_Result=o_XmDvr.SendCmd(DvrPacker.MONITOR_REQ, _CmdParam);
							if(_Result)
								Log.d("Dvr","Send MONITOR_REQ Command Suess ...");	
							else
								Log.d("Dvr","Send MONITOR_REQ Command Fail ...");	
						}
					break;
				default:
						Log.d("Dvr","Rev Unknown Data ...MessageID->" + String.valueOf(DvrPacker.MessageID(_recvBuf) + "  DataLength->" + String.valueOf(DvrPacker.DataLength(recvBuf))));	
						_Result=recvLenData(is, recvBuf, DvrPacker.MsgHeadLen,DvrPacker.DataLength(recvBuf) );
				break;
			}		
		}
		return _Result;
	}
	
	public boolean SendCmd(short _MsgID, String[] _Params)
	{
		DvrPacker.PackCmdBuf(_sendBuf,i_SessID,i_Sequ++, _MsgID, _Params);	
		return SendMsgBuf(o_OutputStream,_sendBuf);
	}
	
	boolean SendMsgBuf(OutputStream os, byte[] recvBuf)
	{
		//Log.d("Dvr", "DataLength" + String.valueOf(DvrPacker.DataLength(recvBuf)));
		try {
			os.write(recvBuf,0,DvrPacker.DataLength(recvBuf) + DvrPacker.MsgHeadLen);
		} catch (IOException e) {
			Log.d("Dvr", "Video SendData IOException");
			return false;
		}
		return true;
	}
	
	//释放
	public void Disponse()
	{
		bRuning=false;	
	}
}
