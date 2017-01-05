package com.mw;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public final class helper {

	static {}

	//设置窗口控件透明度
	public static void SetWinAlpha(Activity act, float _alpha)
	{		
		Window window = act.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		wl.alpha=_alpha;//设置透明度,0.0为完全透明，1.0为完全不透明
		window.setAttributes(wl);
	}
	 //状态栏隐藏(全屏),在Activity.setCurrentView();
	  public static void HideStatusBar(Activity act)
	  {
	      //隐藏标题
		  act.requestWindowFeature(Window.FEATURE_NO_TITLE );
	      //定义全屏参数
	      int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
	      //获得窗口对象
	      Window myWindow = act.getWindow();
	      //设置Flag标识
	      myWindow.setFlags( flag, flag );
	  }
		public static int dip2px(Context context, float dipValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}

		public static int px2dip(Context context, float pxValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}
	  

}
