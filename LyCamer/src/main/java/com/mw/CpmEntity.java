package com.mw;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class CpmEntity {
	
	public String Img;
	public String Alias;
	public int Age;
	public String Gender;
	public String Area;
	
	public String DomainAddr;
	public String UId;
	public String Psw;
	
	public String Token;	
	public String rst;
	public JSONObject JsonRealDataObj;
	public JSONObject JsonAlertDataObj;	
	public JSONObject JsonRoleDataObj;
	public JSONObject JsonActDataObj;
	
	public ArrayList<T_Data> Arr_T_Data;
	public ArrayList<T_Data> Arr_H_Data;
	
	public CpmEntity(String _Img, String _Alias, int _Age, String _Gender, String _Area, String _DomainAddr, String _UId, String _Psw)
	{
		Arr_T_Data=new ArrayList<T_Data>();
		Arr_H_Data=new ArrayList<T_Data>();
		
		JsonRealDataObj=null;
		JsonAlertDataObj=null;
		JsonRoleDataObj=null;
		JsonActDataObj=null;
		Token=null;
		rst="1003";
		Img=_Img;
		Alias=_Alias;
		Age=_Age;
		Gender=_Gender;
		Area=_Area;
		DomainAddr=_DomainAddr;
		UId=_UId;
		Psw=_Psw;
	}
	/*-----------rst-----------
		0000:成功; 0
		1001:用户不存在; 1
		1002:密码错误; 2
		1003:失败；3
		9999:系统忙4
	 */
	public String Login()
	{
		JSONObject JsonObj=CpmProtrol.appLogin(DomainAddr, UId, Psw);
		if(JsonObj!=null)
		{
			try {
				rst=JsonObj.getString("rst");
				if(rst.contains("0000")){
					//Log.e("BSD", "----JsonObj->" + JsonObj.toString());
					Token=JsonObj.getString("token");
					Log.e("Token-->>", Token);
					RealData();
					RoleDataData();					
					AlertData();
			
					}
				else
					Token=null;
				
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rst;		
	}
	
	public void refeshGraph(int HisDataIndex)
	{
	
		
	
	}
	
	
	public String RealData()
	{
		
		JsonRealDataObj=CpmProtrol.appRealData(DomainAddr, Token);
		if(JsonRealDataObj!=null)
		{
			try {
				rst=JsonRealDataObj.getString("rst");
				if(rst.contains("0000"))
				{
					//Log.e("BSD", "-- RealData Json Str -->>-----" + JsonRealDataObj.toString());
					Arr_T_Data.clear();
					Arr_H_Data.clear();	
   					JSONArray ArrJson = JsonRealDataObj.getJSONArray("Record");
   					int ArrLen = ArrJson.length(); 
   					if(ArrLen>1)
   					{
   						JSONObject oj = ArrJson.getJSONObject(1); 
   						JSONArray ArrRealData = oj.getJSONArray("DATA");   						
   						ArrLen=ArrRealData.length(); 
   						SimpleDateFormat  DateFormat =  new  SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");   
   						
   						if(ArrLen>0)
   						{
   							JSONArray ArrHisData=ArrRealData.getJSONObject(0).getJSONArray("Item");
   							ArrLen=ArrHisData.length();
   							JSONObject objItem = null;
   							for(int k=0;k<ArrLen;k++)
   							{
   								objItem=ArrHisData.getJSONObject(k);	
   								Arr_T_Data.add(new T_Data(DateFormat.parse(objItem.getString("Date")),Float.parseFloat(objItem.getString("Value"))));	
   							}    							
   						}
   						if(ArrLen>1)
   						{
   							JSONArray ArrHisData=ArrRealData.getJSONObject(1).getJSONArray("Item");
   							ArrLen=ArrHisData.length();
   							JSONObject objItem = null;
   							for(int k=0;k<ArrLen;k++)
   							{
   								objItem=ArrHisData.getJSONObject(k);	
   								Arr_H_Data.add(new T_Data(DateFormat.parse(objItem.getString("Date")),Float.parseFloat(objItem.getString("Value"))));
   							} 
   						}
   						Log.e("BSD", "更新历史温度------Size->" + String.valueOf(Arr_T_Data.size()));
   					}
				}
				else
				{
					Token=null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rst;		
	}
	public String AlertData()
	{		
		JsonAlertDataObj=CpmProtrol.appAlertData(DomainAddr, Token);
		if(JsonAlertDataObj!=null)
		{
			try {
				rst=JsonAlertDataObj.getString("rst");
				if(rst.contains("0000"))
				{
					//JSONArray ArrJson = JsonRealDataObj.getJSONArray("Record");
					//int ArrLen = ArrJson.length(); 
					//Log.e("BSD", "--JsonAlertDataObj-length-->>-----" + String.valueOf(ArrLen));
					//if(ArrLen>0)
					//{
						//JSONObject oj = ArrJson.getJSONObject(0); 
						//Log.e("BSD", "--->" + oj.getString("value"));
					//}
				}
				else
				{
					Token=null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rst;		
	}
	public String RoleDataData()
	{
		
		JsonRoleDataObj=CpmProtrol.appRoleData(DomainAddr, Token);
		
		if(JsonAlertDataObj!=null)
		{
			try {
				rst=JsonRoleDataObj.getString("rst");
				if(rst.contains("0000"))
				{
					Log.e("BSD", "--RoleData-Json Data->>-----" + JsonRoleDataObj.toString());
					//JSONArray ArrJson = JsonRealDataObj.getJSONArray("Record");
					//int ArrLen = ArrJson.length(); 
					//Log.e("BSD", "--JsonAlertDataObj-length-->>-----" + String.valueOf(ArrLen));
					//if(ArrLen>0)
					//{
						//JSONObject oj = ArrJson.getJSONObject(0); 
						//Log.e("BSD", "--->" + oj.getString("value"));
					//}
				}
				else
				{
					Token=null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rst;		
	}	

	public boolean ActExec(String Id,String ActId,String ActFlag)
	{
		if(Token==null)
			return false;
		JSONObject JsonActExecObj=CpmProtrol.appActExec(DomainAddr, Token,Id,ActId,ActFlag);
		if(JsonActExecObj!=null)
		{
			try {
				rst=JsonActExecObj.getString("rst");
				if(rst.contains("0000"))
					return true;
				else
					Token=null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

public String ActDataData(String Id)
{
	if(Token==null)
		return "";
	JsonActDataObj=CpmProtrol.appActData(DomainAddr, Token,Id);
	if(JsonActDataObj!=null)
	{
		try {
			rst=JsonActDataObj.getString("rst");
			if(rst.contains("0000"))
			{
				Log.e("BSD", "--ActData-Json Data->>-----" + JsonActDataObj.toString());
				//JSONArray ArrJson = JsonRealDataObj.getJSONArray("Record");
				//int ArrLen = ArrJson.length(); 
				//Log.e("BSD", "--JsonAlertDataObj-length-->>-----" + String.valueOf(ArrLen));
				//if(ArrLen>0)
				//{
					//JSONObject oj = ArrJson.getJSONObject(0); 
					//Log.e("BSD", "--->" + oj.getString("value"));
				//}
			}
			else
			{
				Token=null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	return rst;		
}


}
