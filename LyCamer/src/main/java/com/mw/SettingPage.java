package com.mw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ly.MainActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingPage extends Activity {

	Button _BtnBack;
	int _Index;
	
	EditText _txtUserAlias;
	RadioButton _rgMan;
	RadioButton _rgWoman;	
	Spinner _txtNumAge;
	EditText _txtDomainAddr;	
	EditText _txtEbUId;
	EditText _txtEbPsw;	
	Button _BtnSaveUserInfo;
	String _Gender;
	ImageView _ibHeadImg;
	
	Handler _handler;
	Bundle _Bundle;
	
	/*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 3023;  
    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 3021;  
    /*拍照的照片存储位置*/
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");  
    private File mCurrentPhotoFile;//照相机拍照得到的图片
    private String _PhotoFileName;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_setting_page);
		_Gender="男";
		
		 Intent in = getIntent();
		_Index = in.getIntExtra("_Index", -1);
		_PhotoFileName="";
		_txtUserAlias = (EditText) this.findViewById(R.id.txtUserAlias);
		_rgMan = (RadioButton) this.findViewById(R.id.rgMan);
		_rgWoman = (RadioButton) this.findViewById(R.id.rgWoman);
		
		_rgMan.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1)
			{
				if(arg1)
					_Gender="男";
				else
					_Gender="女";
			}
		});
		
		_txtNumAge = (Spinner) this.findViewById(R.id.txtNumAge);
		_txtDomainAddr = (EditText) this.findViewById(R.id.txtDomainAddr);
		_txtEbUId = (EditText) this.findViewById(R.id.txtEbUId);	
		_txtEbPsw = (EditText) this.findViewById(R.id.txtEbPsw);
		
		_BtnBack = (Button) this.findViewById(R.id.BtnCloseSettingPage);
		_BtnSaveUserInfo = (Button) this.findViewById(R.id.BtnSaveUserInfo);
		_ibHeadImg = (ImageView) this.findViewById(R.id.ibHeadImg);
		
		if(_Index>=0)
		{
			CpmEntity _Ce=UserInfos.LisUser.get(_Index);
			_txtUserAlias.setText(_Ce.Alias);
			if(_Ce.Gender.contains("男"))
				_rgMan.setChecked(true);
			else
				_rgWoman.setChecked(true);			
			_txtNumAge.setSelection(_Ce.Age);
			
			
			_txtDomainAddr.setText(_Ce.DomainAddr);
			_txtEbUId.setText(_Ce.UId);
			_txtEbPsw.setText(_Ce.Psw);
			_PhotoFileName=_Ce.Img;
			
			
			
			//_PhotoFileName
			//_ibHeadImg.setImageBitmap(Bitmap.);
			if(_PhotoFileName.length()>10)
			{
				File hfile = new File(_PhotoFileName);
				if(hfile.exists())
					_ibHeadImg.setImageBitmap(BitmapFactory.decodeFile(hfile.getPath()));
			}
		}
		_ibHeadImg.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				doPickPhotoAction();
			}
		});			
		_BtnSaveUserInfo.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				
				if(_txtUserAlias.getText().toString().length()==0){
					Toast.makeText(SettingPage.this, "请输入用户名.", 1000).show();	
					return;
				}
				if(_txtDomainAddr.getText().toString().length()==0){
					Toast.makeText(SettingPage.this, "请输入设备地址.", 1000).show();	
					return;
				}
				if(_txtEbUId.getText().toString().length()==0){
					Toast.makeText(SettingPage.this, "请输入设备帐号.", 1000).show();	
					return;
				}
				if(_txtEbPsw.getText().toString().length()==0){
					Toast.makeText(SettingPage.this, "请输入设备密码.", 1000).show();	
					return;
				}
				
				UserInfos.LoadingText="连接测试中...";
				UserInfos.LoadingShowFlag=true;
				Intent intent = new Intent();
				intent.putExtra("TimeLoop", 2000);
				intent.setClass(SettingPage.this, LoadingPage.class);
				startActivityForResult(intent,10);
				
				ArrayList<String> _ListParam=new ArrayList<String>();					
				_ListParam.add("ConnectTest");
				_ListParam.add(_txtDomainAddr.getText().toString());
				_ListParam.add(_txtEbUId.getText().toString());
				_ListParam.add(_txtEbPsw.getText().toString());
				(new Thread(new AsynCallCgi( _handler, _ListParam, 1))).start();	
			
			}
		});		
		_BtnBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) {
				finish();
			}
		});
		helper.SetWinAlpha(this, 0.7f);
		
		_handler = new Handler()
		{
			   @Override
			   public void handleMessage(Message msg)
			   {
					    super.handleMessage(msg);
					    if(msg.what==1)
					    {
						    _Bundle = msg.getData();
						    String _Code=_Bundle.getString("Code");
				            if(_Code.contains("0000"))
				            {
				            	UserInfos.CloseLoadingFrm("连接成功!");
				            }
				            else if(_Code.contains("1001"))
				            {
						    	UserInfos.CloseLoadingFrm("设备帐号不存在!");
				            }
				            else if(_Code.contains("1002"))
				            {
						    	UserInfos.CloseLoadingFrm("设备密码错误!");
				            }
				            else if(_Code.contains("1003"))
				            {
						    	UserInfos.CloseLoadingFrm("连接失败!");
				            }
				            else if(_Code.contains("9999"))
				            {
						    	UserInfos.CloseLoadingFrm("系统忙!");
				            }				            
				            else
				            {
				            	UserInfos.CloseLoadingFrm("网络错误!");
				            	//Toast.makeText(ControlPage.this, "获取设备指令失败!", 2000).show();	
				            }
					    }
			   }
		};
		
	}


	
	private void doPickPhotoAction() {  
        Context context = SettingPage.this;  
      
        // Wrap our context to inflate list items using correct theme  
        final Context dialogContext = new ContextThemeWrapper(context,  
                android.R.style.Theme_Light);  
        String cancel="返回";  
        String[] choices;  
        choices = new String[1];  
        choices[0] = "拍照"; //拍照  
        //choices[1] ="从相册中选择";//从相册中选择  
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,  
                android.R.layout.simple_list_item_1, choices);  
      
        final AlertDialog.Builder builder = new AlertDialog.Builder(  
                dialogContext);  
        builder.setTitle("选择头像");  
        builder.setSingleChoiceItems(adapter, -1,  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        switch (which) {  
                        case 0:{  
                            String status=Environment.getExternalStorageState();  
                            if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡  
                                doTakePhoto();// 用户点击了从照相机获取  
                            }  
                            else{  
                                //showToast("没有SD卡"); 
                            	//Toast.makeText(this, "没有SD卡",  Toast.LENGTH_LONG).show();  
                            }  
                            break;  
                              
                        }  
                        case 1:  
                            doPickPhotoFromGallery();// 从相册中去获取  
                            break;  
                        }  
                    }  
                });  
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
              
        });  
        builder.create().show();  
    }  

  
/** 
* 拍照获取图片 
*  
*/  
protected void doTakePhoto() {  
    try {  
        // Launch camera to take photo for selected contact  
        PHOTO_DIR.mkdirs();// 创建照片的存储目录  
      String _TFileName=getPhotoFileName(".jpg");
        mCurrentPhotoFile = new File(PHOTO_DIR, _TFileName);// 给新照的照片文件命名  
        final Intent intent = getTakePickIntent(mCurrentPhotoFile, _TFileName);  
        startActivityForResult(intent, CAMERA_WITH_DATA);  
    } catch (ActivityNotFoundException e) {  
        Toast.makeText(this, "图片没有找到",  Toast.LENGTH_LONG).show();  
    }  
}  
  
public static Intent getTakePickIntent(File f, String _ImgFileName) {  
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
    //intent.putExtra("FlieName", _ImgFileName);  
    return intent;  
}  
  
/** 
* 用当前时间给取得的图片命名 
*  
*/  
private String getPhotoFileName(String Exn) {  
    Date date = new Date(System.currentTimeMillis());  
    SimpleDateFormat dateFormat = new SimpleDateFormat( "'IMG'_yyyy-MM-dd HH:mm:ss");  
    return dateFormat.format(date) + Exn; 
}
  
// 请求Gallery程序  
protected void doPickPhotoFromGallery() {  
    try {  
        // Launch picker to choose photo for selected contact  
        final Intent intent = getPhotoPickIntent();  
        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
    } catch (ActivityNotFoundException e) {  
        Toast.makeText(this, "没有选择图片",  Toast.LENGTH_LONG).show();  
    }  
}  
  
// 封装请求Gallery的intent  
public static Intent getPhotoPickIntent() {  
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
    intent.setType("image/*");  
    intent.putExtra("crop", "true");  
    intent.putExtra("aspectX", 1);  
    intent.putExtra("aspectY", 1);  
    intent.putExtra("outputX", 80);  
    intent.putExtra("outputY", 80);  
    intent.putExtra("return-data", true);  
    return intent;  
}  
  
// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	if(requestCode==10)
	{
   	// Log.e("BSD", "dialog>>>>");
	   	if(_Bundle.getString("Code").contains("0000"))
	   		dialog();
		return;
	}
    if (resultCode != RESULT_OK)  
        return;  
    _PhotoFileName=mCurrentPhotoFile.getPath();
    //Log.e("BSD", "_PhotoFileName>>>>" + mCurrentPhotoFile.getPath());
    switch (requestCode)
    {  
        case PHOTO_PICKED_WITH_DATA: // 调用Gallery返回的  
            Bitmap photo = data.getParcelableExtra("data");  
            photo=toRoundBitmap(photo);
            
            String NewFileName=getPhotoFileName(".png");
            _PhotoFileName =PHOTO_DIR + "/" + NewFileName;
            saveBitmap(photo,NewFileName);
            
            Log.e("BSD", "_PhotoFileName name>>>>" + _PhotoFileName );
                        
            _ibHeadImg.setImageBitmap(photo);
            //缓存用户选择的图片  
            //img = getBitmapByte(photo);  
            //mEditor.setPhotoBitmap(photo);              
            System.out.println("set new photo");  
            break;
        case CAMERA_WITH_DATA: // 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
            doCropPhoto(mCurrentPhotoFile);  
            break;  
        }    
}  
  
/**
 * 转换图片成圆形
 * @param bitmap 传入Bitmap对象
 * @return
 */
private Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
                roundPx = width / 2;
                top = 0;
                bottom = width;
                left = 0;
                right = width;
                height = width;
                dst_left = 0;
                dst_top = 0;
                dst_right = width;
                dst_bottom = width;
        } else {
                roundPx = height / 2;
                float clip = (width - height) / 2;
                left = clip;
                right = width - clip;
                top = 0;
                bottom = height;
                width = height;
                dst_left = 0;
                dst_top = 0;
                dst_right = height;
                dst_bottom = height;
        }
         
        Bitmap output = Bitmap.createBitmap(width,
                        height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
         
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);
         
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
}

protected void dialog() { 
    AlertDialog.Builder builder = new Builder(SettingPage.this); 
    builder.setMessage("是否保存?"); 
    builder.setTitle("提示"); 
    builder.setPositiveButton("确认", 
    new android.content.DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int which) { 
            dialog.dismiss(); 

			UserInfos.UpdateCpm(_PhotoFileName, _txtUserAlias.getText().toString(), Integer.valueOf(_txtNumAge.getSelectedItem().toString()), _Gender, "",
					_txtDomainAddr.getText().toString(), _txtEbUId.getText().toString(), _txtEbPsw.getText().toString());

            finish();
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

protected void doCropPhoto(File f) {  
    try {  
        // 启动gallery去剪辑这个照片  
        //Toast.makeText(this, f.getPath(),Toast.LENGTH_LONG).show();  
        final Intent intent = getCropImageIntent(Uri.fromFile(f));  
        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
    } catch (Exception e) {  
        Toast.makeText(this, "没有选择图片",
                Toast.LENGTH_LONG).show();  
    }  
}  
  
/**  
* Constructs an intent for image cropping. 调用图片剪辑程序  
*/  
public static Intent getCropImageIntent(Uri photoUri) {  
    Intent intent = new Intent("com.android.camera.action.CROP");  
    intent.setDataAndType(photoUri, "image/*");  
    intent.putExtra("crop", "true");  
    intent.putExtra("aspectX", 1);  
    intent.putExtra("aspectY", 1);  
    intent.putExtra("outputX", 70);  
    intent.putExtra("outputY", 70);  
    intent.putExtra("return-data", true);  
    return intent;  
}  

/** 保存方法 */
public void saveBitmap(Bitmap bm,String picName) {
 File f = new File(PHOTO_DIR + "/", picName);
 if (f.exists()) {
  f.delete();
 }
 try {
  FileOutputStream out = new FileOutputStream(f);
  bm.compress(Bitmap.CompressFormat.PNG, 90, out);
  out.flush();
  out.close();
  Log.i("BSD", "已经保存");
 } catch (FileNotFoundException e) {
  e.printStackTrace();
 } catch (IOException e) {
  e.printStackTrace();
 }

}

}
