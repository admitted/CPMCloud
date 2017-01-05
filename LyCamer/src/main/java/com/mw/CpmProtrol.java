package com.mw;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public final class CpmProtrol {

	private static boolean DebugFlag;
	static {DebugFlag=true;}
	  
	//MD5加密，32位  
    private static String MD5(String str)  
    {  
        MessageDigest md5 = null;  
        try  
        {  
            md5 = MessageDigest.getInstance("MD5"); 
        }catch(Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++)  
        {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++)  
        {  
            int val = ((int)md5Bytes[i])&0xff;  
            if(val < 16)  
            {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString().toUpperCase(); 
    }
    
	  //Post HtttpRequest阻塞方法
	  private static JSONObject SendHttpPost(String _Url,List<NameValuePair> PostParams)
	  {		  
		 // DebugFlag=false;
		  /*
		  if(DebugFlag)
		  {
			    JSONTokener jsonParser1 =null;
			   if(_Url.contains("appLogin"))
			   {
				   jsonParser1= new JSONTokener("{ \"rst\": \"0000\", \"url\": \"appLogin.cgi\", \"token\": \"591B2C74FDDBA0916C5D490CA2D0B2E0\" }");
			   }
			   else if(_Url.contains("appRealData"))
			   {
				   jsonParser1= new JSONTokener("{ \"rst\": \"0000\", \"url\": \"appRealData.cgi\", \"Record\": [ { \"TYPE\": \"RealData\", \"DATA\": [ { \"ID\": \"00710200020002\", \"devname\": \"温湿度\", \"attrname\": \"湿度\", \"unit\": \"%\", \"Item\": [ { \"Date\": \"2014-04-15 15:51:17\", \"Value\": \"43.50\" } ] }, { \"ID\": \"00710200020001\", \"devname\": \"温湿度\", \"attrname\": \"温度\", \"unit\": \"℃\", \"Item\": [ { \"Date\": \"2014-04-15 15:51:08\", \"Value\": \"22.40\" } ] } ] }, { \"TYPE\": \"HisData\", \"DATA\": [ { \"ID\": \"00710200020001\", \"devname\": \"温湿度\", \"attrname\": \"温度\", \"unit\": \"℃\", \"Item\": [ { \"Date\": \"2014-04-01 00:00:00\", \"Value\": \"21.2\" }, { \"Date\": \"2014-04-02 00:00:00\", \"Value\": \"21.2\" }, { \"Date\": \"2014-04-03 00:00:00\", \"Value\": \"21.14\" }, { \"Date\": \"2014-04-04 00:00:00\", \"Value\": \"21.1\" }, { \"Date\": \"2014-04-05 00:00:00\", \"Value\": \"21.1\" }, { \"Date\": \"2014-04-06 00:00:00\", \"Value\": \"21.1\" }, { \"Date\": \"2014-04-07 00:00:00\", \"Value\": \"21.1\" }, { \"Date\": \"2014-04-08 00:00:00\", \"Value\": \"20.39\" }, { \"Date\": \"2014-04-09 00:00:00\", \"Value\": \"20.48\" }, { \"Date\": \"2014-04-10 00:00:00\", \"Value\": \"21.39\" }, { \"Date\": \"2014-04-11 00:00:00\", \"Value\": \"21.39\" }, { \"Date\": \"2014-04-12 00:00:00\", \"Value\": \"21.61\" }, { \"Date\": \"2014-04-13 00:00:00\", \"Value\": \"20.84\" }, { \"Date\": \"2014-04-14 00:00:00\", \"Value\": \"21.05\" } ] }, { \"ID\": \"00710200020002\", \"devname\": \"温湿度\", \"attrname\": \"湿度\", \"unit\": \"%\", \"Item\": [ { \"Date\": \"2014-04-01 00:00:00\", \"Value\": \"58.55\" }, { \"Date\": \"2014-04-02 00:00:00\", \"Value\": \"58.33\" }, { \"Date\": \"2014-04-03 00:00:00\", \"Value\": \"58.79\" }, { \"Date\": \"2014-04-04 00:00:00\", \"Value\": \"59.37\" }, { \"Date\": \"2014-04-05 00:00:00\", \"Value\": \"59.94\" }, { \"Date\": \"2014-04-06 00:00:00\", \"Value\": \"60.84\" }, { \"Date\": \"2014-04-07 00:00:00\", \"Value\": \"60.93\" }, { \"Date\": \"2014-04-08 00:00:00\", \"Value\": \"60.99\" }, { \"Date\": \"2014-04-09 00:00:00\", \"Value\": \"57.28\" }, { \"Date\": \"2014-04-10 00:00:00\", \"Value\": \"53.11\" }, { \"Date\": \"2014-04-11 00:00:00\", \"Value\": \"54.36\" }, { \"Date\": \"2014-04-12 00:00:00\", \"Value\": \"63.49\" }, { \"Date\": \"2014-04-13 00:00:00\", \"Value\": \"61.42\" }, { \"Date\": \"2014-04-14 00:00:00\", \"Value\": \"53.93\" } ] } ] }, { \"TYPE\": \"SugData\", \"DATA\": [ ] } ] }");  
			   }
			   else if(_Url.contains("appRoleData"))
			   {
				   jsonParser1= new JSONTokener("{ \"rst\": \"0000\", \"url\": \"appRoleData.cgi\", \"Record\": [ { \"ID\": \"600101\", \"NAME\": \"智能客厅\", \"Icon\": \"\", \"Data\": [ { \"ID\": \"0021210001\", \"NAME\": \"灯\", \"STATUS\": \"4\", \"ICON\": \"\", \"CHANNEL\": \"\", \"MEMO\": \"\" } ] }, { \"ID\": \"600102\", \"NAME\": \"智能厨房\", \"Icon\": \"\", \"Data\": [ ] }, { \"ID\": \"600103\", \"NAME\": \"智能卧室\", \"Icon\": \"\", \"Data\": [ { \"ID\": \"0021410001\", \"NAME\": \"窗帘\", \"STATUS\": \"3\", \"ICON\": \"\", \"CHANNEL\": \"\", \"MEMO\": \"\" } ] }, { \"ID\": \"900101\", \"NAME\": \"摄像头\", \"Icon\": \"\", \"Data\": [ { \"ID\": \"0041020001\", \"NAME\": \"机位1\", \"STATUS\": \"3\", \"ICON\": \"\", \"CHANNEL\": \"1\", \"MEMO\": \"pan_tilt=0,outip=183.129.254.76,outport=8000,inip=192.168.0.185,inport=8000,login_id=admin,login_pwd=12345,code_stream=1,\" } ] } ] }");
			   }
			   else if(_Url.contains("appAlertData"))
			   {
				   jsonParser1= new JSONTokener("{ \"rst\": \"0000\", \"url\": \"appAlertData.cgi\", \"Record\": [ { \"ID\": \"0081020001\", \"name\": \"温湿度\", \"attid\": \"\", \"attname\": \"\", \"ctype\": \"1\", \"ctime\": \"2014-04-12 17:46:53\", \"desc\": \"设备离线\" }, { \"ID\": \"0031020001\", \"name\": \"医生建议\", \"attid\": \"\", \"attname\": \"\", \"ctype\": \"1\", \"ctime\": \"2014-04-15 03:22:52\", \"desc\": \"设备离线\" } ] }");
			   }
			      
			    try {
			    	if(jsonParser1!=null)
					return (JSONObject) jsonParser1.nextValue();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			  
		  }
		  */
		  Log.e("BSD", "-----_Url--->>-----" + _Url);
			JSONObject JsonObj=null;
	        HttpPost request=null;
			try{
				//Log.e("BSD", _Url);
				request = new HttpPost(_Url);  
				request.setEntity(new UrlEncodedFormEntity(PostParams, HTTP.UTF_8));  
				HttpClient httpClient = new DefaultHttpClient();
				request.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C)");
				HttpResponse response = httpClient.execute(request);
			    if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
			    {
			    	String Resp = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			    	if(Resp.contains("\"rst\":"))
			    	{
			    	Log.e("BSD", Resp);
				    JSONTokener jsonParser = new JSONTokener(Resp);  
				    JsonObj = (JSONObject) jsonParser.nextValue();
				    Log.e("BSD",  JsonObj.getString("url") + "---Call--_Url--Suess->>-----");
			    	}
			    }
			}catch (JSONException e) {e.printStackTrace(); } 
			catch (ParseException e) {e.printStackTrace(); } 
			catch (IOException e) {e.printStackTrace(); } 
			catch(Exception e){e.printStackTrace(); } 
			finally{request.abort();}
		   return JsonObj;
	  }
	  //CPM InterFace -------------------------------------------------------------------
	  //登入接口
	  public static JSONObject appLogin(String _DomainAddr,String UId, String Psw)
	  {		  
		   //Log.e("BSD", "-----UId--->>-----" + UId);
		  // Log.e("BSD", "-----MD5--->>-----" + MD5(UId + Psw));
		  // Log.e("BSD", "-----Url--->>-----" + "http://" + _DomainAddr + "/cgi-bin/appLogin.cgi");
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("UId", UId)); 
			params.add(new BasicNameValuePair("MD5", MD5(UId + Psw))); 
			
		    return SendHttpPost("http://" + _DomainAddr + "/cgi-bin/appLogin.cgi", params);
	  }
	  //登出接口
	  public static JSONObject appLogout(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appLogout.cgi", params);
	  }
	  //实时数据
	  public static JSONObject appRealData(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appRealData.cgi", params);
	  }
	  //历史数据
	  public static JSONObject appHisData(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appHisData.cgi", params);
	  }
	  //建议信息
	  public static JSONObject appSugData(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token));
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appSugData.cgi", params);
	  }
	  //防区信息
	  public static JSONObject appRoleData(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appRoleData.cgi", params);
	  }
	  //设备动作信息
	  public static JSONObject appActData(String DomainAddr, String Token,String Id)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); //Parameter: Id ???
			params.add(new BasicNameValuePair("Id", Id)); //Parameter: Id ???
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appActData.cgi", params);
	  }
	  //设备动作信息
	  public static JSONObject appActExec(String DomainAddr, String Token,String Id,String ActId,String ActFlag)
	  {
		  //Log.e("BSD", "-----Id--->>-----" + Id);
		  //Log.e("BSD", "-----ActId--->>-----" + ActId);
		  //Log.e("BSD", "-----ActFlag--->>-----" + ActFlag);
		  
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			params.add(new BasicNameValuePair("ID", Id)); 
			params.add(new BasicNameValuePair("ActId", ActId)); 
			params.add(new BasicNameValuePair("Object", ActFlag)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appActExec.cgi", params);
	  }	  
	  //告警信息
	  public static JSONObject appAlertData(String DomainAddr, String Token)
	  {
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("Token", Token)); 
			return SendHttpPost("http://" +DomainAddr + "/cgi-bin/appAlertData.cgi", params);
	  }
	
}
