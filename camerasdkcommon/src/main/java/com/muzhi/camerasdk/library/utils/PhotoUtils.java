package com.muzhi.camerasdk.library.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作类
 */
public class PhotoUtils {

	
	/**
	 * 获取图片Bitmap(缩小一倍)
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = 2;
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if(bm == null){
			return  null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm,degree) ;
		
		return bm;
	}
	
	private static int readPictureDegree(String path) {	
       int degree  = 0;  
       try {  
           ExifInterface exifInterface = new ExifInterface(path);  
           int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
           switch (orientation) {  
	           case ExifInterface.ORIENTATION_ROTATE_90: 
	        	   degree = 90;  
	               break;  
	           case ExifInterface.ORIENTATION_ROTATE_180:  
	               degree = 180;  
	               break;  
	           case ExifInterface.ORIENTATION_ROTATE_270:  
	               degree = 270;  
	               break;  
           } 
       }
       catch (IOException e) {  
           e.printStackTrace();  
       }
       return degree;  
	}
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){
		if(bitmap == null)
			return null ;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}
	
	
	/**
	 * 保存图片并返回文件路径
	 * @param bitmap
	 */
	public static String saveAsBitmap(Context context,Bitmap bitmap){
		String finalPath = "";
		String dirPath = "";
		String dirName = "czh_image/PDD";
		String fileNamePrefix = "image_";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			dirPath = context.getExternalCacheDir() + File.separator + dirName; // /mnt/sdcard/Android/data/<package name>/files/...
		} else {
			dirPath = context.getCacheDir() + File.separator + dirName; // /data/data/<package name>/files/...
		}
		File file2 = new File(dirPath);
		if (!file2.exists()) {
			file2.mkdirs();
		}
		finalPath = file2.getAbsolutePath();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSSS", Locale.getDefault())
				.format(new Date());
		String outputName = fileNamePrefix + timeStamp + ".png";
		finalPath = finalPath + "/" + outputName;
		File file1 = new File(finalPath);
		file1.getParentFile().mkdirs();
		Log.e("TAG",file1+"==="+finalPath);
		try {
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(file1));
			MediaScannerConnection.scanFile(context, new String[] { file1.toString() }, null, null);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		bitmap.recycle();
		return file1.getAbsolutePath();
	}
	
	
	
	
	
	/**
	 * 图片旋转
	 * @param bit
	 * 旋转原图像
	 * 
	 * @param degrees
	 * 旋转度数
	 * 
	 * @return
	 * 旋转之后的图像
	 * 
	 */
	public static Bitmap rotateImage(Bitmap bit, int degrees){
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),bit.getHeight(), matrix, true);
		return tempBitmap;
	}
	
	/**
	 * 翻转图像
	 * 
	 * @param bit
	 * 翻转原图像
	 * 
	 * @param x
	 * 翻转X轴
	 * 
	 * @param y
	 * 翻转Y轴
	 * 
	 * @return
	 * 翻转之后的图像
	 * 
	 * 说明:
	 * (1,-1)上下翻转
	 * (-1,1)左右翻转
	 * 
	 */
	public static Bitmap reverseImage(Bitmap bit,int x,int y){
		Matrix matrix = new Matrix();
		matrix.postScale(x, y);		
		Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),bit.getHeight(), matrix, true);
		return tempBitmap;
	}
		
	
	public static Bitmap ResizeBitmap(Bitmap bitmap, int scale){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(1/scale, 1/scale);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}
	

}
