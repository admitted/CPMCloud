package com.ly;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Shine
 *【文件读，写】
 */
public class FileOperate 
{  
 /**
  * 【读取文件】
  * @param context
  * @param fileName
  * @return
  */
 public String ReadText(Context context,String fileName)
 {
       FileInputStream fIn = null;
       InputStreamReader isr = null;       
       char[] inputBuffer = new char[255];
       String data = null;       
       try
       {
        fIn =context.openFileInput(fileName);      
           isr = new InputStreamReader(fIn);
           isr.read(inputBuffer);
           data = new String(inputBuffer);
           //Toast.makeText(context, "read Succeed",Toast.LENGTH_SHORT).show();
       }
      catch (Exception e) 
          {      
       e.printStackTrace();
       //Toast.makeText(context, " not read",Toast.LENGTH_SHORT).show();
          }
       finally
       {
              try {
                     isr.close();
                     fIn.close();
                   } 
              catch (IOException e) 
              {
                    e.printStackTrace();
              }
           }
       return data;
   }
 
 /**
  * 【写入文件】
  * @param context
  * @param fileName
  * @param data
  */
 public void WriteText(Context context, String fileName,String data)
 {
       FileOutputStream fOut = null;
       OutputStreamWriter osw = null;
       
       try{
        fOut =context.openFileOutput(fileName, 1);
           osw = new OutputStreamWriter(fOut);
           osw.write(data);
           osw.flush();
           //Toast.makeText(context, " saved",Toast.LENGTH_SHORT).show();
           }
           catch (Exception e) 
           {      
           e.printStackTrace();
           //Toast.makeText(context, " not saved",Toast.LENGTH_SHORT).show();
           }
           finally 
           {
              try {
                     osw.close();
                     fOut.close();
                   } 
              catch (IOException e)
               {
                    e.printStackTrace();
                 }
           }
  }
}

