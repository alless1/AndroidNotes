package com.test.timetask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chengjie on 2/18/22
 * Description:
 * 使用MediaProjectionManager进行截屏。
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapActivity extends AppCompatActivity {
    private static final String TAG = "ScreenCapActivity";
    private static final String ACTION_TEST = "com.test.timetask.test";
    private static final int REQUEST_SCREEN_CAP = 101;
    private Intent mScreenData;
    private int mWidth;
    private int mHeight;
    private int mDensityDpi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_cap);
        //申请截图意图
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_SCREEN_CAP);

        //获取屏幕尺寸和dpi
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        mWidth = point.x;
        mHeight = point.y;
        mDensityDpi = displayMetrics.densityDpi;
        Log.d(TAG, String.format("onCreate: mWidth = %s,mHeight = %s,mDensityDpi = %s", mWidth, mHeight, mDensityDpi));

        //注册测试广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TEST);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_TEST)) {
                Log.d(TAG, "onReceive: 收到广播，开始截图。");
                screenCap();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: resultCode =" + resultCode);
        if (requestCode == REQUEST_SCREEN_CAP && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: ok");
            mScreenData = data;
        }
    }

    public void screenCap() {
        if(mScreenData==null)
            return;
        Log.d(TAG, "screenCap: 执行任务 ");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mScreenData);

        @SuppressLint("WrongConstant") final ImageReader iReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        //没搞清楚几个flag有什么区别，能用就行。
        mediaProjection.createVirtualDisplay("screenCap", mWidth, mHeight, mDensityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, iReader.getSurface(), null, null);

        iReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Image image = imageReader.acquireLatestImage();
                if (image != null) {
                    iReader.setOnImageAvailableListener(null, null);
                    mediaProjection.stop();

                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    //去除宽边
                    Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight);
                    bitmap.recycle();

                    //保存图片
                    try {
                        Date currentDate = new Date();
                        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
                        File dir = getExternalFilesDir(null);
                        String fileName = dir.getAbsolutePath() + "/" + date.format(currentDate) + ".png";
                        FileOutputStream fos = new FileOutputStream(fileName);
                        resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        resultBitmap.recycle();
                        Log.d(TAG, "截图成功:" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    image.close();
                }
            }
        }, null);
    }

}
