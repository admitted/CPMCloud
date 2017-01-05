package com.ly.h264;

public class VideoEntity {
  	public int _RevLen;
  	public int _TotalLen;
  	public byte[] videoBuf ;
	public int _MaxFrameLen;
  	public boolean IsFrameKey;
  	
	public VideoEntity()
  	{
  		_RevLen=0;
  		_TotalLen=-1;
  		_MaxFrameLen=16777216;
  		videoBuf = new byte[_MaxFrameLen];
  		IsFrameKey=false;
  	}
  	
	public boolean IsOK()
	{
		return _RevLen==_TotalLen;		
	}
	
  	public void Reset()
  	{
  		_RevLen=0;
  		_TotalLen=-1; 
  		IsFrameKey=false;
  	}
  	
  	
}
