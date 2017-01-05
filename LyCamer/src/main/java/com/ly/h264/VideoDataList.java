package com.ly.h264;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

@SuppressWarnings("unchecked")
public class VideoDataList {

	private List videoList;
	private boolean _AlwaysReadKey;
	public VideoDataList()
	{
		_AlwaysReadKey=false;
		videoList = new ArrayList();
	}
	
	public VideoData getVideoData() {
		synchronized (this) {
			if (videoList.isEmpty()) {
				return null;
			}

			return (VideoData) (videoList.remove(0));
		}
	}

	public void addVideoData(VideoData videoData) {
		synchronized (this) {
			//Log.d("Dvr", "videoList.size()" + String.valueOf(videoList.size())); 
			videoList.add(videoData);
			/*
			if(videoList.size()<15)//正常缓存
			{
					if(videoData.m_FrameKey)
					{
						_AlwaysReadKey=true;
						videoList.add(videoData);
					}
					else
					{
						if(_AlwaysReadKey)
						{
							_AlwaysReadKey=true;
							videoList.add(videoData);
						}
						else
						{
							_AlwaysReadKey=false;
						}
					}
			}
			else if(videoList.size()<30)//播放有慢
			{
				//I帧才可以添加
				if(videoData.m_FrameKey)
				{
					_AlwaysReadKey=true;
					videoList.add(videoData);
				}
				else
				{
					_AlwaysReadKey=false;
				}
			}
			else
			{
				_AlwaysReadKey=false;
			}//缓存已满
			
			*/
		}

	}

	public void clearVideoData() {
		synchronized (this) {
			videoList.clear();
		}
	}

	public int getVideoDataListSize() {
		synchronized (this) {
			return videoList.size();
		}
	}
}
