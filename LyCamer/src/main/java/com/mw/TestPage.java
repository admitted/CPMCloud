package com.mw;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class TestPage extends Activity {

	Button _BtnTest;
	HealthView _HealthView;
	HorizontalScrollView _HorizontalScrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_test_page);
		
		/*
		_BtnTest = (Button) this.findViewById(R.id.MyTestButton);
		_HealthView = (HealthView) this.findViewById(R.id.MyheadView1);	
		_HorizontalScrollView=(HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);	
		_BtnTest.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				_HealthView.refreshData();
				//_HorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);//滚动到底部
			}
		});
		*/
		

        
		
	}

}
