package com.ly;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;




final class CallCgiThread implements Runnable {

	String _Url;
	Handler __Handler;
	public CallCgiThread(String Url, Handler _Handler)
	{		
		_Url=Url;
		__Handler=_Handler;
	}
	
    public void run() 
    {
        Message _msg = new Message();	
        if(_Url.contains("Moblielogin.cgi"))
        	_msg.what=0;
        else  if(_Url.contains("GetGroupDevice.cgi"))
        	_msg.what=1;
        else  if(_Url.contains("GetDevice.cgi"))
        	_msg.what=2;
        else
        	_msg.what=3;
        
        Bundle _Bundle = new Bundle();
        Log.d("PlayDemo", "Excute Thread Call CGI->." + _Url);
        HttpGet request=null;
		try{
			request = new HttpGet(_Url);	
			HttpClient httpClient = new DefaultHttpClient();
			request.setHeader("User-Agent", "Android");
			HttpResponse response = httpClient.execute(request);						
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				String str = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);	
				str=str.replace("\r\n", "");
				_Bundle.putInt("Code", HttpStatus.SC_OK);
		    	_Bundle.putString("Content", str);
			}
		}
		catch(Exception EX){
	        _Bundle.putInt("Code", -5);
	        _Bundle.putString("Content", EX.getMessage());
	        EX.printStackTrace();
		}
		finally{request.abort();}
        if(__Handler!=null)
        {
	        _msg.setData(_Bundle);
	        __Handler.sendMessage(_msg);
        }
    }

}

public class Comm {

}
