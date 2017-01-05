package com.ly;

import com.mw.R;

import xm.eye.preview.xmplayer;
import h264.play.demo.H264PlayDemo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyCamera extends Activity {

	class OnLvitemClickEvent implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if(_ArrType[arg2].contains("0"))
			{
				Intent intent = new Intent();
				intent.setClass(MyCamera.this, MyCamera.class);
				intent.putExtra("User", _User);
				intent.putExtra("Password", _Psw);
				intent.putExtra("G_ID", Integer.valueOf(_ArrID[arg2]));
				intent.putExtra("_ServerAddr", _ServerAddr);
				startActivity(intent);
			}
			else
			{
				if(_ArrOnline[arg2].contains("0"))
				{
					Toast.makeText(MyCamera.this, "该设备不在线.", 2000).show();
				}
				else
				{
					CallCgi(_ServerAddr + "GetDeviceInfo.cgi?User=" + _User + "&Password=" + _Psw + "&D_ID=" + _ArrID[arg2] + "&");
				}
			}
		}

	}


	ListView lv;
	BaseAdapter _Ba;
	String[] _ArrType;
	String[] _ArrID;
	String[] _ArrName;
	String[] _ArrOnline;
	String _User;
	String _Psw;
	int _G_ID;
	Handler _handler;
	String _ServerAddr;
	
	private void CallCgi(String _Url)
	{
		(new Thread(new CallCgiThread(_Url, _handler))).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_camera);
	  	  _ArrType=null;
		  _ArrID=null;
		  _ArrName=null;
		  _ArrOnline=null;		
	      Intent in = getIntent();
	      _User = in.getStringExtra("User");	
	      _Psw = in.getStringExtra("Password");	
	      _G_ID = in.getIntExtra("G_ID", 0);
	      _ServerAddr = in.getStringExtra("_ServerAddr");
		
		lv = (ListView) this.findViewById(R.id.ListView01);
		// 为ListView设置适配器
		_handler = new Handler()
		{
			   @Override
			   public void handleMessage(Message msg)
			   {
				    super.handleMessage(msg);			    
				    Bundle _Bundle = msg.getData();
				    int _Code=_Bundle.getInt("Code");
		            String _Content = _Bundle.getString("Content");
		            if(_Code==200)
		            {
						if(_Content !="")
						{
							if(msg.what==1)
							{
								Log.d("PlayDemo", "_Content -> " + _Content);
								String[] _ArrResStr = _Content.split("%");
								_ArrType = new String[_ArrResStr.length];
								_ArrID =  new String[_ArrResStr.length];
								_ArrName =  new String[_ArrResStr.length];
								_ArrOnline =  new String[_ArrResStr.length];
								for(int i=0;i < _ArrResStr.length; i++)
								{
									String[] _ArrEntity=_ArrResStr[i].split(",");
									_ArrType[i] = _ArrEntity[0];
									_ArrID[i] = _ArrEntity[1];
									_ArrName[i] = _ArrEntity[2];
									_ArrOnline[i] = _ArrEntity[3];
								}					
								_Ba = new BaseAdapter() {
									public int getCount() {								
										//Log.d("PlayDemo", "getCount -> " + String.valueOf(_ArrType.length));
										return _ArrType.length;
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
										//Log.d("PlayDemo", "setAdapter");
										LinearLayout ll = new LinearLayout(MyCamera.this);
										ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向								
										ll.setPadding(5, 5, 5, 5);// 设置四周留白
										
										// 初始化ImageView
										ImageView ii = new ImageView(MyCamera.this);
							
										if(_ArrType[arg0].contains("0"))
										{										
											ii.setImageDrawable(getResources().getDrawable(R.drawable.gi));										
										}
										else
										{										
											if(_ArrOnline[arg0].contains("0"))
												ii.setImageDrawable(getResources().getDrawable(R.drawable.ul));
											else
												ii.setImageDrawable(getResources().getDrawable(R.drawable.ol));
										}
										
										ii.setScaleType(ImageView.ScaleType.FIT_XY);
										ii.setLayoutParams(new Gallery.LayoutParams(60, 60));
										ll.addView(ii);// 添加到LinearLayout中
										// 初始化TextView
										TextView tv = new TextView(MyCamera.this);
							
										tv.setText(_ArrName[arg0]);
										tv.setTextSize(20);// 设置字体大小
										tv.setTextColor(MyCamera.this.getResources().getColor(R.color.black));// 设置字体颜色
										tv.setPadding(5, 5, 5, 5);// 设置四周留白
										tv.setGravity(Gravity.LEFT);
										ll.addView(tv);// 添加到LinearLayout中
										Log.e("PlayDemo", "addView");
						
										return ll;
									}
								};
								lv.setAdapter(_Ba);// 为ListView设置内容适配器
								lv.setOnItemClickListener(new OnLvitemClickEvent());
								
							}
							else
							{								
								String[] _ArrEntity=_Content.split(",");
								Intent in = new Intent();
								if(_ArrEntity[4].contains("hk"))
								{					
									in.setClass(MyCamera.this, HKPlayer.class);
								}
								else
								{
									if(_ArrEntity[7].contains("1"))
									{
										in.putExtra("D_Channel", Integer.valueOf(_ArrEntity[8]));
										in.setClass(MyCamera.this, xmplayer.class);
									}
									else
									{
										in.setClass(MyCamera.this, H264PlayDemo.class);										
									}									
								}
								in.putExtra("D_ID", Integer.valueOf(_ArrEntity[0]));
								in.putExtra("D_Name", _ArrEntity[1]);
								in.putExtra("D_HostAddr", _ArrEntity[2]);
								in.putExtra("D_Web_Port", Integer.valueOf(_ArrEntity[3]));
								if(_ArrEntity[4].contains("hk"))
									in.putExtra("D_User", "admin");
								else
									in.putExtra("D_User", _ArrEntity[4]);
								in.putExtra("D_Psw", _ArrEntity[5]);
								in.putExtra("D_Relay", Integer.valueOf(_ArrEntity[6]));								
								startActivity(in);
							}
						}
						else
						{
							//if(msg.what!=1)
						}
		            }
		            else
		            {
		            	Toast.makeText(MyCamera.this, "网络错误.", 2000).show();		            	
		            }
			   }
		};
		CallCgi(_ServerAddr + "GetGroupDevice.cgi?User=" + _User + "&Password=" + _Psw + "&G_ID=" + _G_ID + "&");
	}

}
