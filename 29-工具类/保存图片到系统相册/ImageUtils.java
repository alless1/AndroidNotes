package com.gsl.speed.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.lib.QRCodeUtil.QRCodeUtil;
import com.gsl.speed.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by chengjie on 2018/8/24
 * Description:
     //系统相册路径 path 参数
    public static final String DIRECTORY_DCIM = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            + File.separator + FOLDER + File.separator;
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    /**
     * 保存二维码到系统相册（拼接图片）
     *
     * @param context  上下文
     * @param path     文件路径
     * @param fileName 文件名
     * @param url      二维码内容
     * @return
     */
    public static boolean createQRCodeToLocal(Context context, String path, String fileName, String url) {

        Bitmap picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.qrcode_bg);//背景图片

        Bitmap bitmapBackground = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), picture.getConfig());//空白画布

        int size = 260 * picture.getWidth()/600;
        int left = (picture.getWidth()-size)/2;
        int top = 230*picture.getHeight()/900;

        Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(url, size);//生成二维码

        Canvas canvas = new Canvas(bitmapBackground);

        Paint paint = new Paint();

        //Log.e(TAG, "picture: "+"picture.getWidth()="+picture.getWidth()+" picture.getHeight()="+picture.getHeight() );
        //Log.e(TAG, "qrCode: "+"qrCodeBitmap.getWidth()="+qrCodeBitmap.getWidth()+"qrCodeBitmap.getHeight() ="+qrCodeBitmap.getHeight() );

        canvas.drawBitmap(picture, 0, 0, paint);

        canvas.drawBitmap(qrCodeBitmap, left, top, paint);


        return save(context,path,fileName,bitmapBackground);
    }

    private static boolean save(Context context,  String path,String mImageFileName,Bitmap bitmap){

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        // 系统时间
        long mImageTime = System.currentTimeMillis();
        long dateSeconds = mImageTime / 1000;

        // 文件路径
        //String mImageFilePath = path + File.separator + mImageFileName;
        File file = new File(fileDir,mImageFileName);
        if (file.exists()) {
            return true;
        }
        int mImageWidth = bitmap.getWidth();
        int mImageHeight = bitmap.getHeight();

        // 保存到系统MediaStore
        ContentValues values = new ContentValues();
        ContentResolver resolver = context.getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.ImageColumns.WIDTH, mImageWidth);
        values.put(MediaStore.Images.ImageColumns.HEIGHT, mImageHeight);
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        try {
            OutputStream out = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);// bitmap转换成输出流，写入文件
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        values.clear();
        values.put(MediaStore.Images.ImageColumns.SIZE,
                file.length());
        resolver.update(uri, values, null, null);

        return true;
    }


}
