package com.ly;

import org.json.JSONException;

import h264.play.demo.H264PlayDemo;
import xm.eye.preview.xmplayer;

import com.ly.h264.DvrPacker;
import com.ly.h264.XmDvr;
import com.mw.AlarmPage;
import com.mw.ControlPage;
import com.mw.CpmEntity;
import com.mw.ElectricalPage;
import com.mw.LoadingPage;
import com.mw.MyHomePage;
import com.mw.MyHomePage1;
import com.mw.R;
import com.mw.SettingPage;
import com.mw.TestPage;
import com.mw.UserInfoPage;
import com.mw.UserInfos;
import com.mw.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity{

	EditText uName;
	EditText uPassword ;
	Button _BtnLogin;
	ToggleButton _TB;
	Handler _handler;
	View menuView;
	AlertDialog _AlertDialog;
	SharedPreferences _SPf;
	String _ServerAddr;
	
	Button _BtnMyHome;
	Button _BtnLive;
	Button _BtnArea;
	Button _BtnSetting;
	ImageView _HeadImg;
	
	private RelativeLayout _RelativeLayout = null;
	private AnimationDrawable anim = null;
	
	private void CtorUI()
	{
		_HeadImg= (ImageView) findViewById(R.id.ivlogo);		
		_BtnMyHome = (Button) findViewById(R.id.BtnMyHome);		
		_BtnLive = (Button) findViewById(R.id.BtnLive);				
		_BtnArea = (Button) findViewById(R.id.BtnArea);		
		_BtnSetting = (Button) findViewById(R.id.BtnSetting);	
		
		_BtnMyHome.getBackground().setAlpha(100);
		_BtnLive.getBackground().setAlpha(100);
		_BtnArea.getBackground().setAlpha(100);
		_BtnSetting.getBackground().setAlpha(100);	
		
		_BtnMyHome.setOnClickListener(new BtnFuncListener());
		_BtnLive.setOnClickListener(new BtnFuncListener());
		_BtnArea.setOnClickListener(new BtnFuncListener());
		_BtnSetting.setOnClickListener(new BtnFuncListener());	
		
		//_RelativeLayout = (RelativeLayout) findViewById(R.id.bgrl1);
		//_RelativeLayout.getViewTreeObserver().addOnPreDrawListener(onpdl);
	}
	/*
	OnPreDrawListener onpdl = new OnPreDrawListener() {
		public boolean onPreDraw() {
			Object ob = _RelativeLayout.getBackground();
			anim = (AnimationDrawable) ob;
			anim.start();
			return true;
		}
	};*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//设置没有title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		//helper.HideStatusBar(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CtorUI();
		
		_TB = (ToggleButton)findViewById(R.id.ToggleButton01);
        uName = (EditText) findViewById(R.id.EditText01);
		uPassword = (EditText) findViewById(R.id.EditText02);
		/*LinearLayout ll= (LinearLayout) findViewById(R.id.llogin);
		ll.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
				if(imm.isActive())
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
		});*/

		_SPf = getSharedPreferences("LyIpc",MODE_PRIVATE);
		
		String mTempString = _SPf.getString("SaveData","0");
		UserInfos.InitFromJsonStr(_SPf.getString("JsonStr",""));		
		
		Log.d("PlayDemo", mTempString);
		_TB.setChecked(mTempString=="1");
		Log.d("PlayDemo", String.valueOf(_TB.isChecked()));
		
		if(_TB.isChecked())
		{
			mTempString = _SPf.getString("User","demo");
			uName.setText(mTempString);
			mTempString = _SPf.getString("Psw","demo");
			uPassword.setText(mTempString);
		}
		else
		{
			if(_SPf.getString("User","").length()!=0)
			{
				mTempString = _SPf.getString("User","");
				uName.setText(mTempString.length()!=0?mTempString:mTempString);
				uPassword.setText("");
			}
		}
		_TB.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				_SPf.edit().putString("SaveData", _TB.isChecked()?"1":"0").commit();	
				_SPf.edit().putString("User",uName.getText().toString()).commit();
				_SPf.edit().putString("Psw",uPassword.getText().toString()).commit();
			}
		});
				
		menuView = View.inflate(this, R.layout.setting, null);
		_AlertDialog = null;		
		_handler = new Handler()
		{
			   @Override
			   public void handleMessage(Message msg)
			   {
				    super.handleMessage(msg);						    
				    Bundle _Bundle = msg.getData();
				    int _Code=_Bundle.getInt("Code");
		            String _Content = _Bundle.getString("Content");
		            _BtnLogin.setEnabled(true);
		            if(_Code==200)
		            {
						if(Integer.valueOf(_Content) > -1)
						{
							_ServerAddr="http://" + _SPf.getString("Server","ipc.chinashilong.net") + ":" + _SPf.getString("Port","80") + "/";
							_SPf.edit().putString("SaveData", _TB.isChecked()?"1":"0").commit();
							_SPf.edit().putString("User",uName.getText().toString()).commit();
							_SPf.edit().putString("Psw",uPassword.getText().toString()).commit();
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, MyCamera.class);
							intent.putExtra("User", uName.getText().toString());
							intent.putExtra("Password", uPassword.getText().toString());
							intent.putExtra("G_ID", Integer.valueOf(_Content));
							intent.putExtra("_ServerAddr", _ServerAddr);
							startActivity(intent);
						}
						else
						{
							switch(Integer.valueOf(_Content))
							{
							case 0:
								break;
							case -1:
								Toast.makeText(MainActivity.this, "验证信息格式错误.", 2000).show();	
								break;
							case -2:
								Toast.makeText(MainActivity.this, "用户名不正确.", 2000).show();	
								break;
							case -3:
								Toast.makeText(MainActivity.this, "密码不正确。.", 2000).show();	
								break;
							case -4:
								Toast.makeText(MainActivity.this, "验证过期.", 2000).show();	
								break;
							default:
								Toast.makeText(MainActivity.this, "未知错误.", 2000).show();	
								break;
							}	
						}
		            }
		            else
		            {
		            	Toast.makeText(MainActivity.this, "网络错误.", 2000).show();				            	
		            }
			   }
		};
		
		
	}
	
	private void ShowFunBtn(int _ShowFlag)
	{
		_BtnMyHome.setVisibility(_ShowFlag);
		_BtnLive.setVisibility(_ShowFlag);
		_BtnArea.setVisibility(_ShowFlag);
		_BtnSetting.setVisibility(_ShowFlag);
		_HeadImg.setVisibility(_ShowFlag);		
	}
	
	private class BtnFuncListener implements OnClickListener {
		public void onClick(View CurView) {			
			if(CurView == _BtnMyHome)
			{
				ShowFunBtn(4);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MyHomePage.class);
				//startActivity(intent);
				startActivityForResult(intent,0);
			}
			else if(CurView == _BtnLive)
			{
			/*
				//CpmEntity _CpmEntity=new CpmEntity("head","别名",0,"男","所在地区","smarthome.591ip.net","user","111111");
				for(CpmEntity tmp:UserInfos.LisUser){
					
				}


					Toast.makeText(MainActivity.this, UserInfos.GetJsonStr(), 2000).show();
	*/
				
				//Toast.makeText(MainActivity.this, "暂无生活栏目.", 2000).show();					
			}
			else if(CurView == _BtnArea)
			{
				/*
				ShowFunBtn(4);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, TestPage.class);
				//startActivity(intent);
				startActivityForResult(intent,4);				
				//Toast.makeText(MainActivity.this, "暂无社区栏目.", 2000).show();				
				 * 
				 */
			}
			else if(CurView == _BtnSetting)
			{
			
				//ShowFunBtn(4);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, TestPage.class);
				startActivity(intent);
				//startActivityForResult(intent,4);*/
			}
			else{}
			/*
			   if(uPassword.getText().toString().length()==0){
				Toast.makeText(MainActivity.this, "请您输入密码.", 2000).show();	
				return;
			}
			_BtnLogin.setEnabled(false);			
			_ServerAddr="http://" + _SPf.getString("Server","cam.chinashilong.net") + ":" + _SPf.getString("Port","80") + "/";
			CallCgi(_ServerAddr + "Moblielogin.cgi?User=" + uName.getText().toString() + "&Password=" + uPassword.getText().toString() + "&");
			 */
		}
	}
	
	 @Override 
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	        // TODO Auto-generated method stub 
	        super.onActivityResult(requestCode, resultCode, data); 
	        //Toast.makeText(MainActivity.this, String.valueOf(resultCode), 2000).show();	
	        Intent intent=null;
	        switch(requestCode)
	        {
	        case 0://“我的家页面 返回主页面	        	
	        	switch(resultCode)
	        	{
		        case 0://直接返回
		        	ShowFunBtn(0);
		        	break;
		        case 1://点击设置
		        	ShowFunBtn(0);
		        	break;
		        case 2://点击控制
					intent = new Intent();
					intent.setClass(MainActivity.this, ControlPage.class);
					startActivityForResult(intent,5);
		        	break;
		        case 3://点击告警
					 intent = new Intent();
					intent.setClass(MainActivity.this, AlarmPage.class);
					startActivityForResult(intent,6);
		        	break;
		        case 4://小贴士
					 intent = new Intent();
					intent.setClass(MainActivity.this, UserInfoPage.class);
					startActivityForResult(intent,7);
		        	break;
	        	}
	        	break;
	        case 4://“设置页面” 返回主页面
	        	ShowFunBtn(0);
	        	break;
	        case 5://控制页面返回
	        case 6://告警页面返回
	        case 7://用户配置页面返回
				intent = new Intent();
				intent.setClass(MainActivity.this, MyHomePage.class);
				startActivityForResult(intent,0);
	        	break;
	        default:
	        	ShowFunBtn(0);
	        	break;
	        }
			//ControlPage ElectricalPage
	  }
	
	private void CallCgi(String _Url)
	{		
		(new Thread(new CallCgiThread(_Url, _handler))).start();
	}
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	      switch (item.getItemId()) {
	      case Menu.FIRST + 2:
	          if (_AlertDialog == null) 
	          {
		      		_AlertDialog = new AlertDialog.Builder(this).create();
		    		_AlertDialog.setView(menuView);
		    		
		    		_AlertDialog.setOnKeyListener(new OnKeyListener()
		    		{
		    			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
		    			{
		    				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
		                        dialog.dismiss();	    				
		    				return false;
		    			}
		            });	
		    		Button _BtnSaveData=(Button) menuView.findViewById(R.id.BtnSaveData);
		    		((EditText) menuView.findViewById(R.id.EtServer)).setText(_SPf.getString("Server","cam.chinashilong.net"));
		    		((EditText) menuView.findViewById(R.id.EtPort)).setText(_SPf.getString("Port","80"));
		    		_BtnSaveData.setOnClickListener(new OnClickListener()
		    		{
						public void onClick(View v){
							_SPf.edit().putString("Server",((EditText) menuView.findViewById(R.id.EtServer)).getText().toString()).commit();
							_SPf.edit().putString("Port",((EditText) menuView.findViewById(R.id.EtPort)).getText().toString()).commit();
							
							_AlertDialog.dismiss();
						}		    			
		    		});
		    		_AlertDialog.show();
	          } else
	          {
	        	  _AlertDialog.dismiss();
	        	  _AlertDialog.show();
	          }
	          break;
	      }
	      return  true;
	  }
	  
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
	   { 
              dialog(); 
              return true; 
	   } 
	   else
	   {
             return false; 
	   }
	  }

	  protected void dialog() { 
          AlertDialog.Builder builder = new Builder(MainActivity.this); 
          builder.setMessage("确定要退出吗?"); 
          builder.setTitle("提示"); 
          builder.setPositiveButton("确认", 
          new android.content.DialogInterface.OnClickListener() { 
              public void onClick(DialogInterface dialog, int which) { 
                  dialog.dismiss(); 
          		_SPf.edit().putString("SaveData", _TB.isChecked()?"1":"0").commit();	
        		_SPf.edit().putString("User",uName.getText().toString()).commit();
        		_SPf.edit().putString("Psw",uPassword.getText().toString()).commit();        
        		_SPf.edit().putString("JsonStr",UserInfos.GetJsonStr()).commit();    
                  finish();                  
                 android.os.Process.killProcess(android.os.Process.myPid()); 
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
	  /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 2, 5, "设置").setIcon(
        R.drawable.menu_syssettings);
		return true;
	}*/

	
}
