package com.mw;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HealthView  extends View{

	public HealthView(Context context) {
		super(context);		
		


		IniVarl();
	}
	
	public HealthView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		IniVarl();
	}

	public HealthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		IniVarl();
	}
	Paint mPaint;
	Paint bPaint;
	Paint mPaint2;
	
	ArrayList<T_Data> ArrData;
	ArrayList<T_Data> ArrData2;
	float SpaceLen=60.00f;
	float TopLen=120.00f;
	float PosLen=80.00f;
	
	float MaxValue=-100.00f;
	float MinValue=100.00f;;
	float ScaleLen=0.00f;
	float TotalHeigh=1.00f;
	float MaxValue2=-100.00f;
	float MinValue2=100.00f;;
	float ScaleLen2=0.00f;
	float TotalHeigh2=1.00f;
	Bitmap tblCell;
	
	//1为温度,2为湿度
	public void refreshData()
	{
			ArrData.clear();
			ArrData2.clear();
			


			
			CpmEntity Ce =UserInfos.GetCurCpm();
			if(Ce!=null)
			{							
					ArrData=(ArrayList<T_Data>)Ce.Arr_T_Data.clone();
					ArrData2=(ArrayList<T_Data>)Ce.Arr_H_Data.clone();
					for(T_Data temp:ArrData)
					{
						if(MaxValue<temp.J_Value)
							MaxValue=temp.J_Value;	
						if(MinValue>temp.J_Value)
							MinValue=temp.J_Value;			
					}
					TotalHeigh = MaxValue - MinValue;
					if(TotalHeigh==0)
						ScaleLen=0;
					else
					{			
						ScaleLen=(this.getHeight() -  TopLen - SpaceLen) / TotalHeigh;
					}		
					for(T_Data temp:ArrData2)
					{
						if(MaxValue2<temp.J_Value)
							MaxValue2=temp.J_Value;	
						if(MinValue2>temp.J_Value)
							MinValue2=temp.J_Value;			
					}
					TotalHeigh2 = MaxValue2 - MinValue2;
					
					if(TotalHeigh2==0)
						ScaleLen2=0;
					else
					{			
						ScaleLen2=(this.getHeight() -  TopLen - SpaceLen) / TotalHeigh2;
					}				
			}
			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600);
			lp1.width=(int)(SpaceLen * 2 + (ArrData.size() - 1) * PosLen);
			//lp1.height=700;
			setLayoutParams(lp1);
			
		this.invalidate();		
	}
	
	//初始化变量
	private void IniVarl(){
		mPaint = new Paint(); //创建画笔
		bPaint=new Paint();
		mPaint2 = new Paint(); //创建画笔
		mPaint.setAlpha(1);
		bPaint.setAlpha(200);
		bPaint.setColor(Color.WHITE);
	      //mPaint.setColor(Color.YELLOW);	
	     // mPaint.setTextSize(25);     
		mPaint.setColor(Color.WHITE);
		mPaint2.setColor(Color.BLUE);
		mPaint2.setTextSize(35);
		
		//mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextSize(45);
		bPaint.setTextSize(35);
		bPaint.setStrokeWidth(3);
		mPaint.setStrokeWidth(3);
		mPaint2.setStrokeWidth(3);
		ArrData=new ArrayList<T_Data>();
		ArrData2=new ArrayList<T_Data>();
		//this.getWidth();
		
		WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
		int mwidth = wm.getDefaultDisplay().getWidth();
		PosLen=(mwidth - 50) / 5;
		//Log.e("BSD", "PosLen->" + String.valueOf(PosLen));
		//this.invalidate();
		tblCell=BitmapFactory.decodeResource(getResources(),R.drawable.tblcell); 
	}
	private void DrawBaseGraph(Canvas c)
	{	
		//c.drawColor(Color.BLUE);
		if(ArrData.size()>0)
		{
			 int txtSize=(int)(mPaint.getTextSize());
			//c.drawText(String.format("%s - 高%d", "温度表(℃)", this.getHeight()), SpaceLen,  TopLen - txtSize / 2 , mPaint);
			int Index=0;
            float N_X=0;
            float N_Y=0;
            float P_X=0;
            float P_Y=0;
            SimpleDateFormat  formatter =  new  SimpleDateFormat ("MM/dd");  
            Calendar c1 = Calendar.getInstance();
            String Week="";
			for(T_Data temp:ArrData)
			{
				P_X=SpaceLen+ Index *  PosLen;
				P_Y=TopLen + (TotalHeigh - (temp.J_Value - MinValue)) * ScaleLen;
				//c.drawLine(P_X, TopLen, P_X, TotalHeigh * ScaleLen , bPaint);				
				c.drawCircle(P_X, P_Y , 5, mPaint);
				
				
				c1.setTime(temp.J_Date);
				switch(c1.get(Calendar.DAY_OF_WEEK))
				{
				case 1:
					Week="周日";
					break;
				case 2:
					Week="周一";
					break;
				case 3:
					Week="周二";
					break;
				case 4:
					Week="周三";
					break;
				case 5:
					Week="周四";
					break;
				case 6:
					Week="周五";
					break;
				case 7:
					Week="周六";
					break;
				}
				c.drawText(Week, P_X - 40, 46 , mPaint);
				c.drawBitmap(tblCell, P_X - 40, TopLen, mPaint);
				//Matrix matrix = new Matrix();
				//c.drawBitmap(tblCell, matrix, mPaint);
				c.drawText(formatter.format(temp.J_Date), P_X - 38, TopLen - 36 , bPaint);				
				c.drawText(String.valueOf(temp.J_Value) + "℃", P_X - 20, P_Y - 10 , bPaint);
				if(N_X!=0)
					c.drawLine(N_X, N_Y, P_X, P_Y, mPaint);
				N_X=P_X;
				N_Y=P_Y;
				Index++;
			}
		}
		if(ArrData2.size()>0)
		{
			int Index=0;
            float N_X=0;
            float N_Y=0;
            float P_X=0;
            float P_Y=0;
			for(T_Data temp:ArrData2)
			{
				P_X=SpaceLen+ Index *  PosLen;
				P_Y=TopLen + (TotalHeigh2 - (temp.J_Value - MinValue2)) * ScaleLen2;
				//c.drawLine(P_X, TopLen, P_X, TotalHeigh * ScaleLen , bPaint);				
				c.drawCircle(P_X, P_Y , 5, mPaint2);
				c.drawText(String.valueOf(temp.J_Value) + "%", P_X - 25, P_Y - 10 , mPaint2);
				if(N_X!=0)
					c.drawLine(N_X, N_Y, P_X, P_Y, mPaint2);
				N_X=P_X;
				N_Y=P_Y;
				Index++;
			}
		}
			

		
		/*
		int Sp_Len=10;
        int X_Len=this.getWidth() - Sp_Len * 2 ;
        int Y_Len=this.getHeight() - Sp_Len * 3;
        int txtSize=(int)(mPaint.getTextSize());
        c.drawColor(Color.WHITE);
        //c.drawCircle(Sp_Len,  Sp_Len, 5, p);
       //c.drawText(String.format("%s", "温度表(℃)"), Sp_Len,  Sp_Len - txtSize / 2 , mPaint);
       //c.drawLine(Sp_Len, Sp_Len,  Sp_Len, Sp_Len + Y_Len, mPaint);
       //c.drawLine(Sp_Len, Sp_Len + Y_Len, Sp_Len + X_Len , Sp_Len + Y_Len , mPaint);
       
       //c.drawText(str, 50, 150, mPaint);
      //int X_Pos = Sp_Len + X_Len;
       SimpleDateFormat  formatter =  new  SimpleDateFormat ("MM/dd");    
		long SysCurMillis=System.currentTimeMillis();
		//Date curDate   =  new   Date(SysCurMillis - 24*360000);//获取当前时间 		
		 
		CpmEntity _CE=UserInfos.GetCurCpm();
       for(int i=6;i>=0;i--)
       {   
    	   c.drawText(String.format("健康趋势 - %s",(_CE==null?"":_CE.Alias)), Sp_Len,  Sp_Len*3, mPaint);
    	   c.drawLine(Sp_Len, Sp_Len * 5,  Sp_Len + X_Len,  Sp_Len * 5, mPaint);
    	   c.drawText(formatter.format(new  Date(SysCurMillis -24*3600000*i)),  Sp_Len + X_Len / 7 * i + 5,  Sp_Len * 8 , mPaint);
    	   //c.drawText(String.format("%s", i),  Sp_Len + X_Len / 24 * i  - 10 + X_Pos,  Sp_Len * 2 + Y_Len - txtSize  , mPaint);
    	   c.drawLine(Sp_Len, Sp_Len * 5,  Sp_Len + X_Len,  Sp_Len * 5, mPaint);
    	   c.drawText(String.format("暂无数据"), Sp_Len,  Sp_Len*15, mPaint);
       }	                       
      // c.drawText(String.format("%s", "湿度表(%)"), Sp_Len + X_Pos,  Sp_Len - txtSize / 2  , mPaint);
       //c.drawLine(Sp_Len + X_Pos, Sp_Len,  Sp_Len + X_Pos, Sp_Len + Y_Len, mPaint);
       //c.drawLine(Sp_Len + X_Pos, Sp_Len + Y_Len, Sp_Len + X_Len + X_Pos , Sp_Len + Y_Len , mPaint);
        
        */
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawLine(1, 1, this.getWidth(), 1, mPaint);
		if(this.getHeight()>150)
			DrawBaseGraph(canvas);
		super.onDraw(canvas);
		//canvas.drawText(String.format("暂无健康趋势数据"), 30, 30, mPaint);
	
	}
}
