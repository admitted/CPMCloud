package com.ly.h264;

public class AVHead {
	
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
