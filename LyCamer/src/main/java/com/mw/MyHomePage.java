package com.mw;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ly.MainActivity;
import com.ly.MyCamera;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyHomePage extends Activity {

	ListView lv;
	BaseAdapter _Ba;
	String[] _ArrTrip;
	
	ImageButton _BtnBack;
	ImageButton _BtnAlarm;
	ImageButton _BtnControl;
	ImageButton _BtnEditUserList;
	ImageView _BtnChangeUser;
	TextView _tvTempuer;
	TextView _tvHumidity;	
	LinearLayout _LLhealth;
	LinearLayout _LLLoading;	
	HealthView _iVHealth;
	boolean LoadingIng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 //helper.HideStatusBar(this);
		super.onCreate(savedInstanceState);		
       // setContentView(R.layout.transparent);
		setContentView(R.layout.act_my_home_page1);
		LoadingIng=false;
		_BtnBack = (ImageButton) this.findViewById(R.id.btnCloseMyHomePage);
		_BtnAlarm = (ImageButton) this.findViewById(R.id.BtnAlarmInfo);
		_BtnControl = (ImageButton) this.findViewById(R.id.BtnControlInfo);
		_BtnEditUserList = (ImageButton) this.findViewById(R.id.BtnEditUserList);
		_BtnChangeUser = (ImageView) this.findViewById(R.id.BtnChangeCpmUser);
		
		//_BtnChangeUser.setImageDrawable(getResources().getDrawable(R.drawable.headimg));

		_BtnBack.setOnClickListener(new BtnFuncListener());
		_BtnAlarm.setOnClickListener(new BtnFuncListener());
		_BtnControl.setOnClickListener(new BtnFuncListener());
		_BtnEditUserList.setOnClickListener(new BtnFuncListener());	
		_BtnChangeUser.setOnClickListener(new BtnFuncListener());			
		_ArrTrip=new String[]{"暂无"};
	
		lv = (ListView) this.findViewById(R.id.lvHealth2);
		lv.setCacheColorHint(0);
		_Ba = new BaseAdapter() {
			public int getCount() {								
				return _ArrTrip.length;
			}
			public Object getItem(int position) {
				//Log.d("PlayDemo", "getItem -> position" + String.valueOf(position));
				return position;
			}
			public long getItemId(int position) {
				//Log.d("PlayDemo", "getItemId -> position" + String.valueOf(position));
				return position;
			}
			public View getView(int arg0, View arg1, ViewGroup arg2) {						
				//Log.d("PlayDemo", "getView -> arg0" + String.valueOf(arg0));
				LinearLayout ll = new LinearLayout(MyHomePage.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向								
				ll.setPadding(5, 5, 5, 5);// 设置四周留白
				/*
				ImageView ii = new ImageView(MyHomePage.this);			
				ii.setImageDrawable(getResources().getDrawable(R.drawable.gi));										
				ii.setScaleType(ImageView.ScaleType.FIT_XY);
				ii.setLayoutParams(new Gallery.LayoutParams(60, 60));
				ll.addView(ii);// 添加到LinearLayout中
				*/
				TextView tv = new TextView(MyHomePage.this);
				tv.setText("  " +  _ArrTrip[arg0]);
				tv.setTextSize(15);// 设置字体大小
				tv.setTextColor(MyHomePage.this.getResources().getColor(R.color.white));// 设置字体颜色
				tv.setPadding(5, 5, 5, 5);// 设置四周留白
				tv.setGravity(Gravity.LEFT);
				ll.addView(tv);// 添加到LinearLayout中
				//Log.e("PlayDemo", "addView");
				return ll;
			}
		};
		lv.setAdapter(_Ba);// 为ListView设置内容适配器
		lv.setOnItemClickListener(new OnLvitemClickEvent());
		
		DisplayMetrics dm2 = getResources().getDisplayMetrics();
		_tvTempuer= (TextView) this.findViewById(R.id.tvRealTempuer);
		_tvHumidity= (TextView) this.findViewById(R.id.tvRealHumidity);		
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  // , 1是可选写的
		//Log.e("BSD", String.valueOf(dm2.heightPixels));
		lp1.setMargins(helper.dip2px(this, 10.0f), dm2.heightPixels*2/5, helper.dip2px(this, 10.0f), 0);
		_tvTempuer.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 300);  
		_iVHealth=(HealthView) this.findViewById(R.id.iVHealth);
		lp.setMargins(0, 0, 0, 0);
		lp.height=dm2.heightPixels/3;
		_iVHealth.setLayoutParams(lp);
		lv.setLayoutParams(lp);
		//LinearLayout _LLhealth=(LinearLayout) this.findViewById(R.id.LLhealth);
		_LLLoading=(LinearLayout) this.findViewById(R.id.LLLoading);
		_LLLoading.setVisibility(4);
		//lp.setMargins(helper.dip2px(this, 10.0f), dm2.heightPixels/4, helper.dip2px(this, 10.0f), 0);
		//_LLhealth.setLayoutParams(lp);
		final ScrollView view = (ScrollView) findViewById(R.id.MainSorollView);
		view.setOnTouchListener(new OnTouchListener(){
	        public boolean onTouch(View v, MotionEvent event){
	            switch (event.getAction()){
	            case MotionEvent.ACTION_DOWN:
	                break;
	            case MotionEvent.ACTION_MOVE:
	                if (v.getScrollY() <= 0){
	                	if(!LoadingIng)
	                	{
	                		LoadingIng=true;
		                    Log.e("BSD", "top");
		                    _LLLoading.setVisibility(0);
		                    UserInfos.RefreshCurCpm();
	                	}
	                }
	                /*
	                else if (view.getChildAt(0).getMeasuredHeight() <= v.getHeight() + v.getScrollY())
	                {
	                    Log.e("BSD", "bottom");
	                    //Log.d("scroll view", "view.getMeasuredHeight() = " + view.getMeasuredHeight() + ", v.getHeight() = " + v.getHeight() + ", v.getScrollY() = " + v.getScrollY()+ ", view.getChildAt(0).getMeasuredHeight() = " + view.getChildAt(0).getMeasuredHeight());
	                }*/
	                break;
	            default:
	                break;
	            }
	            return false;
		 }
		});
		
		helper.SetWinAlpha(this, 0.9f);
		

		timer.schedule(task, 10, 2000);
	}

    /**
     * 转换图片成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
    private Bitmap toRoundBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float roundPx;
            float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
            if (width <= height) {
                    roundPx = width / 2;
                    top = 0;
                    bottom = width;
                    left = 0;
                    right = width;
                    height = width;
                    dst_left = 0;
                    dst_top = 0;
                    dst_right = width;
                    dst_bottom = width;
            } else {
                    roundPx = height / 2;
                    float clip = (width - height) / 2;
                    left = clip;
                    right = width - clip;
                    top = 0;
                    bottom = height;
                    width = height;
                    dst_left = 0;
                    dst_top = 0;
                    dst_right = height;
                    dst_bottom = height;
            }
             
            Bitmap output = Bitmap.createBitmap(width,
                            height, Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
             
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
            final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);
             
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);
            return output;
    }

	private final Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
	    @Override
	    public void run() {
	     // TODO Auto-generated method stub
	     Message message = new Message();
	     message.what = 1;
	     handler.sendMessage(message);
	    }
	 };

	Handler handler = new Handler() {
	   @Override
	   public void handleMessage(Message msg) {
		    switch(msg.what)
		    {
		    case 1:
        	
               // Log.e("BSD", "top");
                _LLLoading.setVisibility(4);
            	LoadingIng=false;
            	
		    	//Log.e("BSD", "------handleMessage-------");
		    	CpmEntity _CurCpm=UserInfos.GetCurCpm();
		    	if(_CurCpm!=null)
		    	{
		    		String _PhotoFileName = _CurCpm.Img;
		    		if(_PhotoFileName.length()>10)
		    		{
		    			File hfile = new File(_PhotoFileName);
		    			if(hfile.exists())
		    			{
		    				_BtnChangeUser.setImageBitmap(BitmapFactory.decodeFile(hfile.getPath()));
		    			}
		    			else
		    			{
		    				_BtnChangeUser.setImageDrawable(getResources().getDrawable(R.drawable.headimg));
		    			}
		    				//_BtnChangeUser.setImageBitmap(BitmapFactory.decodeFile(hfile.getPath()));
		    		}
		    		else
		    		{
		    			_BtnChangeUser.setImageDrawable(getResources().getDrawable(R.drawable.headimg));
		    		}
		    		
		    		
		    		if(_CurCpm.JsonRealDataObj!=null)
		    		{
		    			try {
		    				 String rst=_CurCpm.JsonRealDataObj.getString("rst");
		    				if(rst.contains("0000"))
		    				{
		    					JSONArray ArrJson = _CurCpm.JsonRealDataObj.getJSONArray("Record");
		    					int ArrLen = ArrJson.length(); 
		    					//Log.e("BSD", "--JsonRealDataObj-length-->>-----" + String.valueOf(ArrLen));
		    					if(ArrLen>0)
		    					{
		    						JSONObject oj = ArrJson.getJSONObject(0); 
		    						//Log.e("BSD", "--JsonRealDataObj-JSONObject-->>-----" + oj==null?"null": "not null");
		    						
		    						JSONArray ArrRealData = oj.getJSONArray("DATA");
		    						
		    						ArrLen=ArrRealData.length(); 
		    						//Log.e("BSD", "--JsonRealDataObj-DATA JSONObject Len-->>-----" + String.valueOf(ArrLen));
		    						if(ArrLen>0)
		    						{
		    							JSONObject ojTempuer = ArrRealData.getJSONObject(0).getJSONArray("Item").getJSONObject(0);
			    						ArrLen=ArrRealData.length(); 
			    						//Log.e("BSD", "--JsonRealDataObj-Item JSONObject Len-->>-----" + String.valueOf(ArrLen));
		    							
			    						_tvHumidity.setText(ArrRealData.getJSONObject(0).getString("attrname") + ojTempuer.getString("Value") + "%");
		    							if(ArrLen>1)
		    							{
			    							JSONObject ojHumidity = ArrRealData.getJSONObject(1).getJSONArray("Item").getJSONObject(0); 
			    							_tvTempuer.setText(String.valueOf((int)Float.parseFloat( ojHumidity.getString("Value"))) + "°");
		    							}
		    							else
		    							{
		    								_tvTempuer.setText("");
		    							}
		    							
		    						}
		    						else
		    						{
		    							_tvTempuer.setText("");
		    							_tvHumidity.setText("");
		    						}
		    						//Log.e("BSD", "更新温湿度-------");
		    					}
		    					_iVHealth.refreshData();
		    				}
		    			} catch (JSONException e) {
		    				e.printStackTrace();
		    			}
		    			catch (Exception e) {
		    				e.printStackTrace();
		    			}
		    		}
		    		
		    	}
		    	break;
		    	default:
		    		super.handleMessage(msg);
		    		break;
		    }

	   }
	};
	
	private class BtnFuncListener implements OnClickListener {
		public void onClick(View CurView) {

				
			Intent intent = new Intent();
			if(CurView == _BtnBack)
				MyHomePage.this.setResult(0, intent);
			else if(CurView == _BtnAlarm)
			{
				if(UserInfos.DataReadly(2))
					MyHomePage.this.setResult(3, intent);
				else
				{
					Toast.makeText(MyHomePage.this, "数据读取中..请稍候重试..", 1000).show();
					return;
				}				
			}
			else if(CurView == _BtnControl)
			{				
				if(UserInfos.DataReadly(1))
					MyHomePage.this.setResult(2, intent);
				else
				{
					Toast.makeText(MyHomePage.this, "数据读取中..请稍候重试..", 1000).show();
					return;
				}	
			}
			else if(CurView == _BtnEditUserList)
				MyHomePage.this.setResult(4, intent);			
			else if(CurView == _BtnChangeUser)
			{
				UserInfos.ChangeCpm();
				_iVHealth.refreshDrawableState();
				return;				
			}
			else{}
			finish();
		}
	}
	class OnLvitemClickEvent implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			//Toast.makeText(MyHomePage.this, "暂无关于->." + _ArrTrip[arg2], 500).show();
		}
	}
	

}
