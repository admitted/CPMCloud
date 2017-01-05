package com.mw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import com.ly.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public final class UserInfos{
	
	public static ArrayList<CpmEntity> LisUser;
	public static int SelectedIndex;
	public static Timer timer;
	private static TimerTask task;
	public static boolean LoadingShowFlag;
	public static String LoadingText;
	
	static {
		LoadingShowFlag=false;
		LoadingText="数据处理中...";
		SelectedIndex=0;
		LisUser = new ArrayList<CpmEntity>();
		//LisUser.add(new CpmEntity("head1","演示用户",32,"男","宁波","smarthome.591ip.net","user","111111"));
		//LisUser.add(new CpmEntity("head2","爸爸",55,"男","福建","smarthome.591ip.net","user","111111"));
		//LisUser.add(new CpmEntity("head3","妈妈",53,"男","香港","smarthome.591ip.net","user","111111"));
		//LisUser.add(new CpmEntity("head4","老婆",30,"男","莆田","smarthome.591ip.net","user","111111"));
		//LisUser.add(new CpmEntity("head5","儿子",6,"男","宁波","smarthome.591ip.net","user","111111"));
		
		timer = new Timer(true);
		task = new TimerTask(){
		      public void run() {
		  		for(CpmEntity temp:LisUser)
		  		{
		  			Log.e("BSD", "-----TimerTask.run()--->>-----");
					//if(temp.Token==null)
					//{
						
						temp.Login();
						//if(temp.Login().contains("0000"))
						//{
							
							
						//}
					//}
		  		}
		    	  
		    	  
		    	  
		      //Message message = new Message(); 
		      //message.what = 1;      
		      //handler.sendMessage(message);    
		   }  
		};
		timer.schedule(task, 500,60000); //延时1000ms后执行，1000ms执行一次
	}
	
	public static boolean DataReadly(int _Type)
	{
		boolean IsReadLy=false;
    	CpmEntity _CurCpm=UserInfos.GetCurCpm();
    	if(_CurCpm!=null)
    	{
    		JSONObject _CurJsonObj=null;
    		switch(_Type)
    		{
    		case 0:
    			_CurJsonObj = _CurCpm.JsonRealDataObj;
    			break;
    		case 1:
    			_CurJsonObj = _CurCpm.JsonRoleDataObj;
    			break;
    		case 2:
    			_CurJsonObj = _CurCpm.JsonAlertDataObj;
    			break;
    		}
    		if(_CurJsonObj!=null)
    		{
    			try {
    				 String rst=_CurJsonObj.getString("rst");
    				if(rst.contains("0000"))
    					IsReadLy=true;
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}    		
    	}
		return IsReadLy;
	}
	
    public static void CloseLoadingFrm(String Text)
    {
    	LoadingText=Text;
     	LoadingShowFlag=false;
    }
    
	public static boolean GetActData(String Id)
	{
		CpmEntity __CE=GetCurCpm();
		if(__CE!=null)
			return __CE.ActDataData(Id).contains("0000");
		return false;
	}
	
	public static boolean ActExec(String Id,String ActId,String ActFlag)
	{
		CpmEntity __CE=GetCurCpm();
		if(__CE!=null)
			return __CE.ActExec(Id,ActId,ActFlag);
		return false;
	}
	
	private static CpmEntity GetCpmEntityByAlias(String _Alias)
	{
		for(CpmEntity temp:LisUser)
			if(temp.Alias.contains(_Alias))
				return temp;
		return null;
	}
	
	//实现查询所有用户信息迭代接口
	/*
	public static Iterable<CpmEntity> GetCpms()
	{
			return new Iterable<CpmEntity>()
			{ 
					public Iterator<CpmEntity> iterator()
					{
							return new Iterator<CpmEntity>()
							{
									private int Cur_Index=LisUser.size()-1;
									public boolean hasNext()
									{
										if(Cur_Index>-1)
											return true;
										else
											return false;
									}
									public CpmEntity next()
									{
										return LisUser.get(Cur_Index--);
									}
									public void remove(){}
						};
				}
			};
	}
	*/
	//插入用户信息
	public static void UpdateCpm(String _Img, String _Alias, int _Age, String _Gender, String _Area, String _DomainAddr, String _UId, String _Psw)
	{
		CpmEntity temp=GetCpmEntityByAlias(_Alias);
		if(temp==null)
			LisUser.add(new CpmEntity(_Img,_Alias,_Age,_Gender,_Area,_DomainAddr,_UId,_Psw));
		else
		{
			temp.Img=_Img;
			temp.Alias=_Alias;
			temp.Age=_Age;
			temp.Gender=_Gender;
			temp.Area=_Area;
			temp.DomainAddr=_DomainAddr;
			temp.UId=_UId;
			temp.Psw=_Psw;
		}
		if(LisUser.size()==1)
			SelectedIndex=0;
	}
	static Timer timer1 =null;
	public static void RefreshCurCpm()
	{
		final Timer timer1 = new Timer(true);
		TimerTask task1 = new TimerTask(){
		      public void run() {
		    	  timer1.cancel();
		  		CpmEntity __CE=GetCurCpm();
				if(__CE!=null)
					__CE.Login();
		      }
		};
		timer1.schedule(task1, 0,60000); //延时1000ms后执行，1000ms执行一次		
	}
	public static void ChangeCpm()
	{
		if(LisUser.size()>0)
			SelectedIndex = ++SelectedIndex % LisUser.size();
		else
			return;
		
		final Timer timer1 = new Timer(true);
		TimerTask task1 = new TimerTask(){
		      public void run() {
		    	  timer1.cancel();
		  		CpmEntity __CE=GetCurCpm();
				if(__CE!=null)
					__CE.Login();
		      }
		};
		timer1.schedule(task1, 0,60000); //延时1000ms后执行，1000ms执行一次		
	}
	public static void RemoveCpm(String _Alias)
	{
		int _index=-1;
		for(CpmEntity temp:LisUser)
		{
			_index++;
			if(temp.Alias==_Alias)
			{
				LisUser.remove(_index);
				break;
			}
		}
	}
	public static CpmEntity GetCurCpm()
	{
		if(SelectedIndex>=0)
		{
			if(LisUser.size() > SelectedIndex)
			{
				return LisUser.get(SelectedIndex);
			}
		}
		return null;
	}
	
	
	
	public static void RemoveCpmByIndex(int _Index)
	{
		if(SelectedIndex < _Index)
			SelectedIndex=-1;
		
		if(LisUser.size() > _Index)
			LisUser.remove( _Index);
		if(LisUser.size()>0)
			SelectedIndex=0;
	}
	//初始化用户信息
	public static void InitFromJsonStr(String _JsonStr)
	{
		// "{\"cpms\":[]}";
		if(_JsonStr.length()>0)
		{
			try {
	
				    JSONTokener jsonParser = new JSONTokener(_JsonStr);  
				    JSONObject JsonObj = (JSONObject) jsonParser.nextValue();
					JSONArray ArrJson = JsonObj.getJSONArray("cpms");
					int ArrLen = ArrJson.length(); 
					Log.e("BSD", "-----ArrJson.length()--->>-----" + String.valueOf(ArrLen));
					for(int i=0;i<ArrLen;i++)
					{
						JSONObject _cpm = ArrJson.getJSONObject(i); 						
						LisUser.add(new CpmEntity(_cpm.getString("Img"),_cpm.getString("Alias"),_cpm.getInt("Age"),_cpm.getString("Gender"),_cpm.getString("Area"),
								_cpm.getString("DomainAddr"),_cpm.getString("UId"),_cpm.getString("Psw")));_cpm.getString("Img");
					}
					Log.e("BSD", "ArrJson size:" + String.valueOf(LisUser.size()));	
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		if(UserInfos.LisUser.size()==0)
			LisUser.add(new CpmEntity("","Jacy",32,"男","宁波","smarthome.591ip.net:3333","user","111111"));
		if(UserInfos.LisUser.size()>0)
			UserInfos.SelectedIndex=0;
	}
	//产生用户信息的Json 字符串
	public static String GetJsonStr()
	{
		JSONStringer jsonText = new JSONStringer(); 	
		try {
			jsonText.object();
			jsonText.key("cpms");  
			jsonText.array(); 
		
	
			for(CpmEntity tmp:LisUser){
				jsonText.object();
				jsonText.key("Img");
				jsonText.value(tmp.Img); 
				jsonText.key("Alias");
				jsonText.value(tmp.Alias); 
				jsonText.key("Age"); 
				jsonText.value(tmp.Age); 
				jsonText.key("Gender"); 
				jsonText.value(tmp.Gender); 				
				jsonText.key("Area");
				jsonText.value(tmp.Area); 
				jsonText.key("DomainAddr");
				jsonText.value(tmp.DomainAddr); 
				jsonText.key("UId");
				jsonText.value(tmp.UId); 
				jsonText.key("Psw");
				jsonText.value(tmp.Psw); 
				jsonText.endObject();  			
			}
		
			jsonText.endArray();  
			jsonText.endObject(); 
			InitFromJsonStr(jsonText.toString());
			return jsonText.toString();
		} catch (JSONException e) {
			e.printStackTrace();	
			return "{\"cpms\":[]}";
		}	

	}

	
}
