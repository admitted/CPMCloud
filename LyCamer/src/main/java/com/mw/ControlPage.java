package com.mw;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ly.HKPlayer;
import com.ly.MainActivity;
import com.ly.MyCamera;


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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ControlPage extends Activity {

	Button _BtnBack;
	ListView lv;
	BaseAdapter _Ba;
	
    ArrayList<Integer>  mImageIds;
    ArrayList<String> mArrTitle;
    ArrayList<DevInfos> mArrDevInfos;	
	int CurDevIndex;
	Handler _handler;
	Bundle _Bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_control_page);
		
		_BtnBack = (Button) this.findViewById(R.id.BtnCloseControlPage);
		_BtnBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				finish();
			}
		});
		
	    mImageIds=new ArrayList<Integer>();
	    mArrTitle=new ArrayList<String>();
	    mArrDevInfos=new  ArrayList<DevInfos>();
	    CurDevIndex=-1;
		
    	CpmEntity _CurCpm=UserInfos.GetCurCpm();
    	if(_CurCpm!=null)
    	{
    		if(_CurCpm.JsonRoleDataObj!=null)
    		{
    			try {
    				 String rst=_CurCpm.JsonRoleDataObj.getString("rst");
    				if(rst.contains("0000"))
    				{
    					JSONArray ArrJson = _CurCpm.JsonRoleDataObj.getJSONArray("Record");
    					int ArrLen = ArrJson.length(); 
    					//Log.e("BSD", "--JsonRoleDataDataObj-length-->>-----" + String.valueOf(ArrLen));
    					for(int i=0;i<ArrLen;i++)
    					{
    						JSONObject oj = ArrJson.getJSONObject(i);    					    
    					    switch(i)
    					    {
    					    case 0:
    					    	mImageIds.add(R.drawable.imgframe4);
    					    	break;
    					    case 1:
    					    	mImageIds.add(R.drawable.imgframe3);
    					    	break;
    					    case 2:
    					    	mImageIds.add(R.drawable.imgframe2);
    					    	break;
    					    case 3:
    					    	mImageIds.add(R.drawable.imgframe5);
    					    	break;
					    	default:
					    		mImageIds.add(R.drawable.imgframe1);
					    		break;
    					    }
    					    
    					    mArrTitle.add(oj.getString("NAME"));
    					    mArrDevInfos.add(new DevInfos());
    						
    						JSONArray ArrDataJson = oj.getJSONArray("Data");
    						int ArrDataLen = ArrDataJson.length(); 
    						for(int j=0;j<ArrDataLen;j++)
    						{
    							DevInfos CurDev=mArrDevInfos.get(i);
    							JSONObject oj1 = ArrDataJson.getJSONObject(j); 
    							CurDev._Arr_ID.add(oj1.getString("ID"));
    							CurDev._Arr_name.add(oj1.getString("NAME"));
    							CurDev._Arr_status.add(oj1.getString("STATUS"));     						
    							CurDev._Arr_channel.add(oj1.getString("CHANNEL"));
    							CurDev._Arr_type.add("");
        						if(oj1.getString("MEMO").length()>0)
        						{        							
    			    				Pattern pattern = Pattern.compile("([^=]+)=([^,]+),");
    			    				Matcher matcher = pattern.matcher(oj1.getString("MEMO"));    			    				
    			    				while(matcher.find())
    			    				{  
    			    					if(matcher.group(1).contains("outip"))
    			    						CurDev._Arr_ip.add(matcher.group(2));
    			    					else if(matcher.group(1).contains("outport"))
    			    						CurDev._Arr_port.add(matcher.group(2));
    			    					else if(matcher.group(1).contains("login_id"))
    			    						CurDev._Arr_uid.add(matcher.group(2));
    			    					else if(matcher.group(1).contains("login_pwd"))
    			    						CurDev._Arr_pwd.add(matcher.group(2)); 
    			    					else{}
    			    					//Log.e("BSD", "pro -> " + matcher.group(1) + "  Value -> " + matcher.group(2));
    			    				}        							
        							/*
        							CurDev._Arr_ip.add("60.12.218.237");
        							CurDev._Arr_port.add("8002");
        							CurDev._Arr_uid.add("admin");
        							CurDev._Arr_pwd.add("12345"); 
        							*/
        						}
        						else
        						{
        							CurDev._Arr_ip.add("");
        							CurDev._Arr_port.add("");
        							CurDev._Arr_uid.add("");
        							CurDev._Arr_pwd.add(""); 
        						}
    						}
    					}
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    		
    	}
		
		
		lv = (ListView) this.findViewById(R.id.LvElectrical1);
		lv.setCacheColorHint(0);
		_Ba = new BaseAdapter() {
			public int getCount() {
				if(CurDevIndex<0)
					return 0;
				else
				return mArrDevInfos.get(CurDevIndex)._Arr_ID.size();
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
				LinearLayout ll = new LinearLayout(ControlPage.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
				ll.setPadding(5, 5, 5, 5);// 设置四周留白
				int indentify=0;
				/* 设备有5个状态
					撤防状态0   b_icon
					离线状态1   g_icon
					异常状态2   r_icon
					关状态4     c_icon
					开状态5     0_icon
					把设备图标存到 5个目录下  根据设备状态找目录， 在目录根据id 前6位找图标*/
				indentify= getResources().getIdentifier(String.format("status%s%s",mArrDevInfos.get(CurDevIndex)._Arr_status.get(arg0),
						mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg0).substring(0, 6)), "drawable", "com.mw");
				if(indentify>0)
				{
					ImageView ii3 = new ImageView(ControlPage.this);				
					ii3.setImageDrawable(getResources().getDrawable(indentify));
					ii3.setScaleType(ImageView.ScaleType.FIT_XY);
					ii3.setLayoutParams(new Gallery.LayoutParams(75, 70));
					ll.addView(ii3);
				}
				ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape1));	
				TextView tv = new TextView(ControlPage.this);				
				tv.setText((( indentify>0 || mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg0).length()<=6)?"":"     ")  + mArrDevInfos.get(CurDevIndex)._Arr_name.get(arg0));							
				//tv.setTextSize(25);// 设置字体大小
				tv.setTextColor(ControlPage.this.getResources().getColor(R.color.white));// 设置字体颜色
				tv.setTextSize(23);// 设置字体大小
				tv.setPadding(5, 5, 5, 5);// 设置四周留白
				tv.setGravity(Gravity.LEFT);
				ll.addView(tv);// 添加到LinearLayout中
				if(mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg0).length()>6)
				{
					ImageView ii2 = new ImageView(ControlPage.this);			
					ii2.setImageDrawable(getResources().getDrawable(R.drawable.controlbar_forward_enable));										
					ii2.setScaleType(ImageView.ScaleType.FIT_XY);
					ii2.setLayoutParams(new Gallery.LayoutParams(60, 60));
					ii2.setPadding(0, 15, 0, 0);// 设置四周留白
					ll.addView(ii2);// 添加到LinearLayout中
				}				
				//Log.e("PlayDemo", "addView");
				return ll;
			}
		};
		lv.setAdapter(_Ba);// 为ListView设置内容适配器
		lv.setOnItemClickListener(new OnLvitemClickEvent());
		//lv.getBackground().setAlpha(100);		
		//helper.SetWinAlpha(this, 0.7f);
	     
        ImageAdapter adapter = new ImageAdapter(this, mImageIds,mArrTitle);  
        adapter.createReflectedImages();  
  
        GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.Gallery02);  
        galleryFlow.setAdapter(adapter);  
        galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener()
        {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				CurDevIndex=arg2;
				_Ba.notifyDataSetChanged();
				//Log.e("BSD", "onItemSelected" + String.valueOf(arg2));				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				//Log.e("BSD", "onNothingSelected" + String.valueOf(arg0.getId()));	
			}                
        });
		
		
		_handler = new Handler()
		{
			   @Override
			   public void handleMessage(Message msg)
			   {
					    super.handleMessage(msg);
					    if(msg.what==1)
					    {
						    _Bundle = msg.getData();
						    int _Code=_Bundle.getInt("Code");
				            						    
				            if(_Code==1)
				            {
				            	UserInfos.CloseLoadingFrm("读取完成!");
				            }
				            else
				            {
				            	UserInfos.CloseLoadingFrm("获取设备指令失败!");
				            	//Toast.makeText(ControlPage.this, "获取设备指令失败!", 2000).show();	
				            }
					    }
			   }
		};
		
	}
	
	 @Override 
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	        // TODO Auto-generated method stub 
	        super.onActivityResult(requestCode, resultCode, data); 
	        //Toast.makeText(MainActivity.this, String.valueOf(resultCode), 2000).show();	
	        Intent intent=null;
	        switch(requestCode)
	        {
	        case 10://“我的家页面 返回主页面
	        	//this.finish();
	        	CpmEntity _CurCpm=UserInfos.GetCurCpm();
	        	if(_CurCpm!=null)
	        	{
	        		if(_CurCpm.JsonActDataObj!=null)
	        		{
	        			try {
	        				 String rst=_CurCpm.JsonActDataObj.getString("rst");
	        				if(rst.contains("0000"))
	        				{
	        					JSONArray ArrJson = _CurCpm.JsonActDataObj.getJSONArray("Record");
	        					int ArrLen = ArrJson.length(); 
	        					if(ArrLen<=2)
	        					{
	        						Toast.makeText(ControlPage.this, "该设备为采集数据类型设备，不支持远程控制.", 2000).show();	
	        						return;
	        					}
	        				}
	        			} catch (JSONException e) {
	        				e.printStackTrace();
	        			} catch (Exception e) {
	        				e.printStackTrace();
	        			}
	        		}
	        	}
	        	
				Intent in = new Intent();
				in.setClass(ControlPage.this, ElectricalPage.class);
				in.putExtra("ID", _Bundle.getString("ID"));
				in.putExtra("name", _Bundle.getString("name"));
				in.putExtra("status", _Bundle.getString("status"));
				startActivity(in);
	        	break;
	        }
	 }
	
	class OnLvitemClickEvent implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if(mArrDevInfos.get(CurDevIndex)._Arr_ip.get(arg2).length()>5)//摄像头
			{
				Intent in = new Intent();
				in.setClass(ControlPage.this, HKPlayer.class);

				in.putExtra("ID", mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg2));
				in.putExtra("name", mArrDevInfos.get(CurDevIndex)._Arr_name.get(arg2));
				in.putExtra("status", mArrDevInfos.get(CurDevIndex)._Arr_status.get(arg2));
				in.putExtra("channel", mArrDevInfos.get(CurDevIndex)._Arr_channel.get(arg2));
				in.putExtra("type", mArrDevInfos.get(CurDevIndex)._Arr_type.get(arg2));
				in.putExtra("ip", mArrDevInfos.get(CurDevIndex)._Arr_ip.get(arg2));
				in.putExtra("port", mArrDevInfos.get(CurDevIndex)._Arr_port.get(arg2));
				in.putExtra("uid", mArrDevInfos.get(CurDevIndex)._Arr_uid.get(arg2));
				in.putExtra("pwd", mArrDevInfos.get(CurDevIndex)._Arr_pwd.get(arg2));	
				
				startActivityForResult(in,1);
			}
			else
			{
			   //从CGI得到支持的动作

				if(mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg2).length()>6)
				{
					UserInfos.LoadingText="读取" +mArrDevInfos.get(CurDevIndex)._Arr_name.get(arg2) + "数据中...";
					UserInfos.LoadingShowFlag=true;
					Intent intent = new Intent();
					intent.putExtra("TimeLoop", 1000);
					intent.setClass(ControlPage.this, LoadingPage.class);
					startActivityForResult(intent,10);
					
					ArrayList<String> _ListParam=new ArrayList<String>();					
					_ListParam.add("ActData");
					_ListParam.add(mArrDevInfos.get(CurDevIndex)._Arr_ID.get(arg2));
					_ListParam.add(mArrDevInfos.get(CurDevIndex)._Arr_name.get(arg2));
					_ListParam.add(mArrDevInfos.get(CurDevIndex)._Arr_status.get(arg2));					
					(new Thread(new AsynCallCgi( _handler, _ListParam, 1))).start();	
				}

			}
		}
	}


}
