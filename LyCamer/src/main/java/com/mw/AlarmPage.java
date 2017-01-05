package com.mw;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mw.ControlPage.OnLvitemClickEvent;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AlarmPage extends Activity {
   Button _BtnClose;
   ArrayList<String> _ArrTrip;
   ListView lv;
   BaseAdapter _Ba;
   TextView _BtnTotalAlarmCount;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_alarmpage);
				
		_BtnClose = (Button) this.findViewById(R.id.BtnClose);
		_BtnTotalAlarmCount = (TextView) this.findViewById(R.id.txtAlarmCount);
		_BtnClose.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				finish();
			}
		});

		_ArrTrip=new ArrayList<String>();
		
		
		
    	CpmEntity _CurCpm=UserInfos.GetCurCpm();
    	if(_CurCpm!=null)
    	{
    		if(_CurCpm.JsonAlertDataObj!=null)
    		{
    			try {
    				 String rst=_CurCpm.JsonAlertDataObj.getString("rst");
    				if(rst.contains("0000"))
    				{
    					JSONArray ArrJson = _CurCpm.JsonAlertDataObj.getJSONArray("Record");
    					int ArrLen = ArrJson.length(); 
    					//Log.e("BSD", "--JsonRoleDataDataObj-length-->>-----" + String.valueOf(ArrLen));
    					for(int i=0;i<ArrLen;i++)
    					{
    						JSONObject oj = ArrJson.getJSONObject(i);     						
    						_ArrTrip.add(oj.getString("ctime") + "《" + oj.getString("name") + "》" + oj.getString("desc"));
    					}
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    		
    	}
    	
    	_BtnTotalAlarmCount.setText(String.format("告警信息 （%d 条未处理）", _ArrTrip.size()));
    	
		lv = (ListView) this.findViewById(R.id.ListAlarmInfo1);
		lv.setCacheColorHint(0);
		_Ba = new BaseAdapter() {
			public int getCount() {								
				return _ArrTrip.size();
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
				LinearLayout ll = new LinearLayout(AlarmPage.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向								
				ll.setPadding(5, 5, 5, 5);// 设置四周留白
				TextView tv = new TextView(AlarmPage.this);
				
				tv.setText(String.valueOf(arg0 + 1) + "、" + _ArrTrip.get(arg0));
				
				
				tv.setTextSize(15);// 设置字体大小
				tv.setTextColor(AlarmPage.this.getResources().getColor(R.color.black));// 设置字体颜色
				tv.setPadding(5, 5, 5, 5);// 设置四周留白
				tv.setGravity(Gravity.LEFT);
				ll.addView(tv);// 添加到LinearLayout中
				//Log.e("PlayDemo", "addView");
				return ll;
			}
		};
		lv.setAdapter(_Ba);// 为ListView设置内容适配器
		//lv.setOnItemClickListener(new OnLvitemClickEvent());
		//lv.getBackground().setAlpha(100);
		helper.SetWinAlpha(this, 0.7f);
	}


}
