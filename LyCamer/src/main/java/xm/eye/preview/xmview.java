package xm.eye.preview;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.mw.R.drawable;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class xmview extends View {
	private Paint mPaint;
	private int width;
	private int height;
	private byte[] mPixel;
	private ByteBuffer buffer;
	private Bitmap VideoBit;
	private int mTrans;
	private boolean bFirst;
	private boolean bFindPPS;
	private byte[] NalBuf;
	private int nalLen;
	private int NalBufUsed;
	private int SockBufUsed;

	private Rect Srect;
	private Rect Rrect;
	private int WndWidth;;	
	private int WndHeight;;	
	private H264View _H264View;
	
	public String _DeviceName;
	private boolean Showloading;
	private boolean _Drawing;
	
	Bitmap _ImgUp;
	Bitmap _ImgDown;	
	Bitmap _ImgLeft;		
	Bitmap _ImgRight;		
	Bitmap _ImgDa;		
	Bitmap _ImgXiao;		
	int _ShownBtnType;
	int _ShowBtnTime;
	//初始化变量
	private void IniVarl()
	{
		_DeviceName="";
		mPaint = new Paint();
		mPaint.setAlpha(0);
		mPaint.setColor(Color.YELLOW);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextSize(32);
		mPaint.setStrokeWidth(1);
		Showloading=true;
		_Drawing=true;

		width = 928; // 此处设定不同的分辨率
		height = 576;
		WndWidth=352;
		WndHeight=288;
		Srect = new Rect(0, 0, 352, 288);
		Rrect = new Rect(0, 0, 352, 288);		
		
		mPixel = new byte[width * height * 2];
		buffer = ByteBuffer.wrap(mPixel);
		VideoBit = Bitmap.createBitmap(width, height, Config.RGB_565);
		mTrans = 0x0F0F0F0F;
		bFirst = true;
		bFindPPS = true;
		NalBuf = new byte[32 * 1024 * 20]; // 40k
		nalLen=0;
		NalBufUsed = 0;
		SockBufUsed = 0;

		_H264View=new H264View();

		setFocusable(true);


		for (int i = 0; i < mPixel.length; i++) 
			mPixel[i] = (byte) 0x00;

		_H264View.InitDecoder(width, height, 15);
		Log.d("Dvr", "InitDecoder Suess..");
		_ShowBtnTime=10;
		_ShownBtnType=0;
		_ImgUp=GetImg(drawable.ptzup);
		_ImgDown=GetImg(drawable.ptzdown);
		_ImgLeft=GetImg(drawable.ptzleft);
		_ImgRight=GetImg(drawable.ptzright);
		_ImgDa=GetImg(drawable.ptzeda);
		_ImgXiao=GetImg(drawable.ptzxiao);


	}
	
	public void ShowBtnImg(int ShownBtnType)
	{
		_ShownBtnType=ShownBtnType;
		_ShowBtnTime=0;
	}
	
	Bitmap GetImg(int _ImgId)
	{
		InputStream is = getResources().openRawResource(_ImgId);  
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}
	
	public xmview(Context context) {
		super(context);		
		IniVarl();
	}

	public xmview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		IniVarl();
	}

	public xmview(Context context, AttributeSet attrs) {
		super(context, attrs);
		IniVarl();
	}

	
	
	public void OfferData(byte[] data, int len)
	{
		SockBufUsed = 0;
		inputData(data, len);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		if(_Drawing)
		{
			WndHeight=this.getHeight();
			int ScreenWidth=this.getWidth();
			WndWidth=WndHeight * 352 / 288;	
			
			buffer.rewind();
			VideoBit.copyPixelsFromBuffer(buffer);	
			Rrect = new Rect((this.getWidth()-WndWidth) / 2, 0, (this.getWidth()-WndWidth) / 2 + WndWidth, WndHeight);
			canvas.drawBitmap(VideoBit, Srect, Rrect, null);

			if(_ShowBtnTime < 10)
			{
				++_ShowBtnTime;
				switch(_ShownBtnType)
				{
				case 0:
					canvas.drawBitmap(_ImgUp,ScreenWidth / 3  + 30, 0, null);
					canvas.drawBitmap(_ImgLeft, 30, WndHeight / 3, null);
					canvas.drawBitmap(_ImgRight, ScreenWidth * 2 / 3 + 30, WndHeight / 3, null);				
					canvas.drawBitmap(_ImgXiao, 40, WndHeight * 2 / 3  + 10, null);
					canvas.drawBitmap(_ImgDown, ScreenWidth / 3 + 30, WndHeight *  2 / 3, null);						
					canvas.drawBitmap(_ImgDa,  ScreenWidth * 2 / 3 + 40, WndHeight * 2 / 3  + 10, null);
					break;
				case 1:
					canvas.drawBitmap(_ImgUp,ScreenWidth / 3 + 30, 0, null);
					break;
				case 2:
					canvas.drawBitmap(_ImgDown, ScreenWidth / 3 + 30, WndHeight *  2 / 3, null);						
					break;
				case 3:
					canvas.drawBitmap(_ImgLeft, 30, WndHeight / 3, null);
					break;
				case 4:
					canvas.drawBitmap(_ImgRight, ScreenWidth * 2 / 3 + 30, WndHeight / 3, null);				
					break;
				case 5:	
					canvas.drawBitmap(_ImgXiao, 10, WndHeight * 2 / 3  + 40, null);
					break;
				case 6:			
					canvas.drawBitmap(_ImgDa,  ScreenWidth * 2 / 3 + 40, WndHeight * 2 / 3  + 10, null);
					break;
				}
			}
		//	canvas.drawBitmap(R., matrix, paint)
			if(Showloading)		 
				 canvas.drawText("数据导入中,请稍候...",  WndWidth / 2 - 80, WndHeight / 2, mPaint);
			else
				canvas.drawText(String.format("%s (%d x %d)", _DeviceName, Srect.right, Srect.bottom), 30, 30, mPaint);
		}
	}

	int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf,
			int SockBufUsed, int SockRemain) {
		int i = 0;
		byte Temp;
		for (i = 0; i < SockRemain; i++) {
			Temp = SockBuf[i + SockBufUsed];
			NalBuf[i + NalBufUsed] = Temp;
			mTrans <<= 8;
			mTrans |= Temp;
			if (mTrans == 1) // 找到一个开始字
			{
				i++;
				break;
			}
		}
		return i;
	}


	/*
	 * 解码并显示视频
	 */
	public void inputData(byte[] buf, int size) {
		int iTemp = 0;
		int bytesRead = 0;
		bytesRead = size;
		while (bytesRead - SockBufUsed > 0) {
			nalLen = MergeBuffer(NalBuf, NalBufUsed, buf, SockBufUsed, bytesRead - SockBufUsed);
			NalBufUsed += nalLen;
			SockBufUsed += nalLen;
			while (mTrans == 1) {
				mTrans = 0xFFFFFFFF;
				if (bFirst == true) // the first start flag
				{
					bFirst = false;
				} else // a complete NAL data, include 0x00000001 trail.
				{
					if (bFindPPS == true) // true
					{
						if ((NalBuf[4] & 0x1F) == 7) {
							bFindPPS = false;
						} else {
							NalBuf[0] = 0;
							NalBuf[1] = 0;
							NalBuf[2] = 0;
							NalBuf[3] = 1;
							NalBufUsed = 4;
							break;
						}
					}
					synchronized (this) {
						iTemp = _H264View.DecoderNal(NalBuf, NalBufUsed - 4, mPixel);
						if (iTemp > 0) {
							Showloading=false;
							postInvalidate(); // 使用postInvalidate可以直接在线程中更新界面 //
						}
					}
				}
				NalBuf[0] = 0;
				NalBuf[1] = 0;
				NalBuf[2] = 0;
				NalBuf[3] = 1;
				NalBufUsed = 4;
			}
		}
	}

	public void onDestroy()
	{
		//this.onDestroy();
		_Drawing=false;
			synchronized(this)
			{
				_H264View.UninitDecoder();
				Log.d("Dvr", "UninitDecoder Suess..");
			}
	  }

	
}
