package com.ly.h264;

import java.util.ArrayList;
import java.util.List;

public class VideoBuffer {

	private int ArrCount;
	private int ArrMaxCount;
	private VideoEntity[] ArrFrame ;
	private int _RedFrame;
	private int _WrtFrame;
	private boolean _AlwaysReadKey;
	
	public VideoBuffer()
	{
		ArrCount=10;
		ArrMaxCount=30;
		ArrFrame = new  VideoEntity[ArrMaxCount];
		_RedFrame=0;
		_WrtFrame=0;		
		_AlwaysReadKey=false;
	}
	
	public Boolean RevData()
	{
		int _IsOkCount=0;
		boolean _RevSuess=true;
		synchronized(this)
		{
			for(int i=0;i<ArrMaxCount;i++)
				_IsOkCount +=ArrFrame[i].IsOK()?1:0;
			if(_IsOkCount<ArrCount)
			{
				//添加 
			}
			else if(_IsOkCount<ArrMaxCount)
			{
				//I帧才可以添加
			}
			else{_RevSuess=false;}
		}
		return _RevSuess;
	}
	
	public VideoEntity GetFrame()
	{
  		synchronized(this)
  		{
  			for(int i=0;i<ArrMaxCount;i++)
  			{
  	  			if(ArrFrame[_RedFrame].IsOK())
  	  			{
  	  				int CurIndex=_RedFrame;
  	  				_RedFrame=++_RedFrame % ArrMaxCount;
  	  				if(!_AlwaysReadKey)
  	  				{
  	  					if(ArrFrame[CurIndex].IsFrameKey)
  	  					{
  	  						_AlwaysReadKey=true;
  	  						return ArrFrame[CurIndex]; 
  	  					}
  	  					else
  	  					{
  	  						ArrFrame[CurIndex].Reset();
  	  					}
  	  				}
  	  				
  	  			} 
  			}
      	}
		return null;
	}
	
	public void Reset()
	{
		synchronized(this)
		{
			for(int i=0;i<ArrMaxCount;i++)
				ArrFrame[i].Reset();
		}
	}
	
}
