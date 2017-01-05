package com.ly.h264;

/**
 * <p>
 *  视屏流数据定义
 * </p>
 * @author Jack Zhou
 * @version $Id: VideoData.java,v 0.1 2012-3-20 上午11:03:06 Jack Exp $
 */
/*
public class VideoData {
	public int videoLength = 0;//长度
	public byte[] videoBuf = null;//视屏流缓存

	public VideoData(int videoLength) {
		videoBuf = new byte[videoLength + 1];
	}
}
*/
public class VideoData {
	public boolean m_FrameKey;
	public int m_FrameLength;//长度
	public byte[] m_videoBuf;//视屏流缓存

	public VideoData(int FrameLength, boolean FrameKey, byte[] videoBuf) {
		m_FrameKey = FrameKey;
		m_FrameLength=FrameLength;
		m_videoBuf=videoBuf;
	}
}