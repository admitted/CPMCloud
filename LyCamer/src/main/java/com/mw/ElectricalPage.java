package com.mw;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ly.HKPlayer;
import com.mw.ControlPage.OnLvitemClickEvent;

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
import android.view.ViewGroup.LayoutParams;
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
import android.widget.ToggleButton;

public class ElectricalPage extends Activity {

	Button _BtnBack;
	ImageView _ivDeviceImg;
	String _ID;
	String _STAUTS;
	ListView lv;
	BaseAdapter _Ba;
	TextView _txtTitle;
	
	ArrayList<String> _Arr_actstatus;
	ArrayList<String> _Arr_name;
	ArrayList<String> _Arr_actid;
	Handler _handler;
	Bundle _Bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_doact_page);
		_BtnBack = (Button) this.findViewById(R.id.BtnCloseElectricalPage);
		_ivDeviceImg = (ImageView) this.findViewById(R.id.ivDeviceImg);		
		_txtTitle = (TextView) this.findViewById(R.id.tvElectricalPageTitle);		
		_BtnBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				finish();
			}
		});
		//helper.SetWinAlpha(this, 0.9f);
	
		
        Intent in = getIntent();
        _ID = in.getStringExtra("ID"); 
        _STAUTS = in.getStringExtra("status");
        _txtTitle.setText(in.getStringExtra("name"));
        
        
		_Arr_actstatus=new ArrayList<String>();
		_Arr_name=new ArrayList<String>();	
		_Arr_actid=new ArrayList<String>();	
        
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
    					//Log.e("BSD", "--JsonRoleDataDataObj-length-->>-----" + String.valueOf(ArrLen));
    					for(int i=0;i<ArrLen;i++)
    					{
    						JSONObject oj = ArrJson.getJSONObject(i);
    						if(oj.getString("name").contains("布防") || oj.getString("name").contains("撤防"))
    						{
    							continue;
    						}
    						else
    						{
	    						_Arr_actstatus.add(oj.getString("actstatus"));
	    						_Arr_name.add(oj.getString("name"));
	    						_Arr_actid.add(oj.getString("actid"));
    						}
    					}
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		
    	}
		
    	//_ivDeviceImg
		int indentify= getResources().getIdentifier(String.format("status%s%s",_STAUTS,_ID.substring(0, 6)), "drawable", "com.mw");
		if(indentify>0)
			_ivDeviceImg.setImageDrawable(getResources().getDrawable(indentify));
		//Log.e("BSD", "-->>>>-------------");

		lv = (ListView) this.findViewById(R.id.LvActDataList);	
		lv.setCacheColorHint(0);
		_Ba = new BaseAdapter() {
			public int getCount() {								
				return _Arr_name.size();
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
				LinearLayout ll = new LinearLayout(ElectricalPage.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向								
				ll.setPadding(5, 5, 5, 5);// 设置四周留白
				//ll.setBackgroundColor(ElectricalPage.this.getResources().getColor(R.color.transparent_background3));
				/*
				ImageView ii = new ImageView(MyHomePage.this);			
				ii.setImageDrawable(getResources().getDrawable(R.drawable.gi));										
				ii.setScaleType(ImageView.ScaleType.FIT_XY);
				ii.setLayoutParams(new Gallery.LayoutParams(60, 60));
				ll.addView(ii);// 添加到LinearLayout中
				*/
			
				ToggleButton _ToggleButton = new ToggleButton(ElectricalPage.this);
				_ToggleButton.setChecked(_Arr_actstatus.get(arg0)=="1");
				_ToggleButton.setLayoutParams(new Gallery.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 100));
				_ToggleButton.setText(_Arr_name.get(arg0));
				final int intdex1=arg0;
				_ToggleButton.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0) {
						Log.e("BSD", "arg0->" + String.valueOf(intdex1));
						((ToggleButton)arg0).setText(_Arr_actstatus.get(intdex1)=="1"?"关闭中...":"打开中...");
						
						UserInfos.LoadingText=(_Arr_actstatus.get(intdex1)=="1" ? "关闭 《" :"打开 《") + _Arr_name.get(intdex1)  + "》 处理中...";
						UserInfos.LoadingShowFlag=true;
						Intent intent = new Intent();
						intent.setClass(ElectricalPage.this, LoadingPage.class);
						intent.putExtra("TimeLoop", 500);
						startActivityForResult(intent,10);
						
								ArrayList<String> _ListParam=new ArrayList<String>();					
								_ListParam.add("ActExec");
								_ListParam.add(_ID);
								_ListParam.add(_Arr_actid.get(intdex1));
								_ListParam.add(_Arr_actstatus.get(intdex1)=="1" ? "2" :"1");					
								_ListParam.add(String.valueOf(intdex1));
								
								(new Thread(new AsynCallCgi( _handler, _ListParam, 1))).start();			
						
						
					}
					
				});
				ll.addView(_ToggleButton);
				/*
				TextView tv = new TextView(ElectricalPage.this);
				//tv.setText(_Arr_name.get(arg0) + "("+ (_Arr_actstatus.get(arg0)=="1" ? "打开" :"关闭") +")");
				tv.setText(_Arr_name.get(arg0)	);
				tv.setTextSize(20);// 设置字体大小
				tv.setTextColor(ElectricalPage.this.getResources().getColor(R.color.black));// 设置字体颜色
				tv.setPadding(5, 5, 5, 5);// 设置四周留白
				tv.setGravity(Gravity.LEFT);
				ll.addView(tv);// 添加到LinearLayout中
			*/
				//Log.e("PlayDemo", "addView");
				return ll;
			}
		};
		lv.setAdapter(_Ba);// 为ListView设置内容适配器
		//lv.setOnItemClickListener(new OnLvitemClickEvent());
		//lv.getBackground().setAlpha(100);
		//helper.SetWinAlpha(this, 0.7f);
		
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

								
				            	int arg2 = Integer.valueOf(_Bundle.getString("arg2"));
				            	if(_Arr_actstatus.get(arg2)=="1")
				            		_Arr_actstatus.set(arg2, "2");
				            	else
				            		_Arr_actstatus.set(arg2, "1");
				            	_Ba.notifyDataSetChanged();
				            	
				            	UserInfos.CloseLoadingFrm((_Arr_actstatus.get(arg2)!="1" ? "关闭 《" :"打开 《") + _Arr_name.get(arg2)  + "》 成功.");
				            	
				            	//Toast.makeText(ElectricalPage.this, "控制设备成功!", 1000).show();
				            }
				            else
				            {
				            	UserInfos.CloseLoadingFrm("操作失败!");
				            	//Toast.makeText(ElectricalPage.this, "控制设备失败!", 1000).show();	
				            }
					    }
			   }
		};
		Log.e("BSD", "-->>>>------6------");
		
	}
	class OnLvitemClickEvent implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			   //从CGI得到支持的动作

		


			
		}
	}

}
