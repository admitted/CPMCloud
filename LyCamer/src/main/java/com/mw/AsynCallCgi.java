package com.mw;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class AsynCallCgi implements Runnable { 


	 Handler __Handler;
	 ArrayList<String> __ListParam;
	 int __What;
	public AsynCallCgi(Handler _Handler,ArrayList<String> _ListParam,int _What)
	{		
		__Handler=_Handler;
		__ListParam=_ListParam;
		__What=_What;
		//Log.e("BSD", "URL -> " + Url);
		//Log.e("BSD", "CookieStr -> " + CookieStr);
	}
	
	
	
	public void run() {

		Message _msg = new Message();
		_msg.what=__What;
        Bundle _Bundle = new Bundle();
        //Log.d("BSD", "Excute Thread Post Data To CGI->." + _Url);

        //
        if(__ListParam.get(0).contains("ActData"))
        {
        	_Bundle.putInt("Code",UserInfos.GetActData(__ListParam.get(1)) ? 1 : 0);
         	_Bundle.putString("ID",__ListParam.get(1));
         	_Bundle.putString("name",__ListParam.get(2));
         	_Bundle.putString("status",__ListParam.get(3));
        }
        else if(__ListParam.get(0).contains("ConnectTest"))
        {
        	String rst="1003";
        	JSONObject _JsonObj=CpmProtrol.appLogin(__ListParam.get(1),__ListParam.get(2),__ListParam.get(3));
        	if(_JsonObj!=null)
        	{
        		try 
        		{
        			rst=_JsonObj.getString("rst");
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
        	_Bundle.putString("Code",rst);
        	//_Bundle.putString("arg2",__ListParam.get(4));
        	}
        }        
        else if(__ListParam.get(0).contains("ActExec"))
        {
        	_Bundle.putInt("Code",UserInfos.ActExec(__ListParam.get(1),__ListParam.get(2),__ListParam.get(3)) ? 1 : 0);
        	_Bundle.putString("arg2",__ListParam.get(4));
        }
        else
        {
        	_Bundle.putInt("Code",0);
        	_Bundle.putString("ID","");
        }
        if(__Handler!=null)
        {
	        _msg.setData(_Bundle);
	        __Handler.sendMessage(_msg);
        }
		
	}
	
	
}