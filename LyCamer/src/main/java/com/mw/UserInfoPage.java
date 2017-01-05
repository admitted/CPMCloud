package com.mw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.ly.MainActivity;
import com.ly.MyCamera;
import com.mw.R.color;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoPage extends Activity {


	BaseAdapter mydapter;
	Button _BtnClose;
	ImageButton _BtnAddUser;
	long firClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_userpage);
		firClick=0;
		 GridView gridview = (GridView) findViewById(R.id.gvUserInfos);  
		 mydapter=new BaseAdapter() {
			    public View getView(int position, View convertView, ViewGroup parent) {
					LinearLayout ll = new LinearLayout(UserInfoPage.this);
					ll.setOrientation(LinearLayout.VERTICAL); // 设置朝向
					ll.setPadding(5, 5, 5, 5);// 设置四周留白
			    	
					CircularImage ii = new CircularImage(UserInfoPage.this);
					ii.setTag(new indexTag(position));
					//ll.setBackgroundColor(color.transparent_background);//transparent_background
				
		
					String _PhotoFileName = UserInfos.LisUser.get(position).Img;
					Log.e("BSD", _PhotoFileName);
					if(_PhotoFileName.length()>10)
					{
						File hfile = new File(_PhotoFileName);
						
						Log.e("BSD", "length->" + String.valueOf(hfile.length()));
						if(hfile.exists())
							ii.setImageBitmap(BitmapFactory.decodeFile(hfile.getPath()));
					}
					else
					{
						ii.setImageDrawable(getResources().getDrawable(R.drawable.headimg));
					}
					
					
					ii.setScaleType(ImageView.ScaleType.FIT_XY);
					ii.setLayoutParams(new Gallery.LayoutParams(100, 100));
					ii.setOnTouchListener(new OnTouchListener(){
							public boolean onTouch(View v2, MotionEvent event) {
								switch(event.getAction())
								{
								case MotionEvent.ACTION_DOWN:
									firClick= System.currentTimeMillis();
									break;
								case MotionEvent.ACTION_UP:
									if(System.currentTimeMillis() - firClick < 1500)//单击
									{
										Intent intent = new Intent();
										intent.putExtra("_Index", ((indexTag)v2.getTag()).mIndex);
										intent.setClass(UserInfoPage.this, SettingPage.class);
										startActivityForResult(intent,0);	
									}
									else//长按
									{
										 dialog(((indexTag)v2.getTag()).mIndex);
									}
									break;
								}
								return true;
							}
					});
					
					ll.addView(ii);// 添加到LinearLayout中
					TextView tv = new TextView(UserInfoPage.this);
					tv.setText(UserInfos.LisUser.get(position).Alias);
					tv.setTextSize(20);// 设置字体大小
					tv.setTextColor(UserInfoPage.this.getResources().getColor(R.color.white));// 设置字体颜色
					tv.setPadding(5, 5, 5, 5);// 设置四周留白
					tv.setGravity(Gravity.LEFT);
					ll.addView(tv);// 添加到LinearLayout中					
			    
			        return ll;
			    }
			    public long getItemId(int position) {
			        return position;
			    }
			    public Object getItem(int position) {
			        return null;
			    }
			    public int getCount() {
			        return UserInfos.LisUser.size();
			    }
			};
			 gridview.setAdapter(mydapter);
			   /*registerForContextMenu(gridview);
		//GridView监听事件
		 gridview.setOnItemClickListener(new OnItemClickListener() {
		 public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			  Log.e("BSD", ">>>>selected…index." + String.valueOf(arg2));
		    mydapter.notifyDataSetChanged();
		    
		 }
		 });*/
		 
		 
			_BtnClose = (Button) this.findViewById(R.id.BtnCloseUserInfoPage);
			_BtnClose.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0) {
					finish();
				}
			});
			_BtnAddUser = (ImageButton) this.findViewById(R.id.BtnAddUser);

			_BtnAddUser.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.putExtra("_Index", -1);
					intent.setClass(UserInfoPage.this, SettingPage.class);
					startActivityForResult(intent,0);
					//startActivity(intent);
				}
			});
			
			helper.SetWinAlpha(this, 0.7f);

	  }  
	
	  protected void dialog(int itemIndex) { 
		  final int _itemIndex=itemIndex;
          AlertDialog.Builder builder = new Builder(UserInfoPage.this); 
          builder.setMessage("确定删除吗?"); 
          builder.setTitle("提示"); 
          builder.setPositiveButton("确认", 
          new android.content.DialogInterface.OnClickListener() { 
              public void onClick(DialogInterface dialog, int which) { 
                  dialog.dismiss(); 
                  UserInfos.RemoveCpmByIndex(_itemIndex);
              } 
          }); 
          builder.setNegativeButton("取消", 
          new android.content.DialogInterface.OnClickListener() { 
              public void onClick(DialogInterface dialog, int which) { 
                  dialog.dismiss(); 
              } 
          }); 
          builder.create().show(); 
      }
	
	 @Override 
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	        // TODO Auto-generated method stub 
	        super.onActivityResult(requestCode, resultCode, data); 
	        mydapter.notifyDataSetChanged();
	 }
	
	 class indexTag
	 {
		 public int mIndex;
		 public indexTag(int itemIndex){mIndex=itemIndex;}
	 }
	 
	@Override
	public boolean onContextItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case 1:

			//startActivity(intent);
	    	break;
	    case 2:
	    	//UserInfos.RemoveCpmByIndex(((indexTag)arg0.getTag()).mIndex));

	    	break;
	    }
	    mydapter.notifyDataSetChanged();
	    return super.onContextItemSelected(item);
	}

	  /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act_userpage, menu);
		return true;
	}*/

}
