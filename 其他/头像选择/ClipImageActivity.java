package com.sankeyun.ecg.ui.activity.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sankeyun.ecg.R;
import com.sankeyun.ecg.ui.activity.BaseActivity;
import com.sankeyun.ecg.widget.ClipRectView;
import com.sankeyun.ecg.widget.FullImageView;
import com.sankeyun.ecg.widget.TitleView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ${程杰} on 2018/2/28.
 * 描述:
 */

public class ClipImageActivity extends BaseActivity {

    private static final int PICK_IMAGE = 1;
    private FullImageView mFullImageView;
    private TitleView mTitleView;
    private String mOriginalPath;
    private ClipRectView mRectView;
    private GlideDrawable mGlideDrawable1;
    private ImageView mBottom_image;
    private RelativeLayout mFull_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);
        mFullImageView = (FullImageView) findViewById(R.id.image);
        mRectView = (ClipRectView) findViewById(R.id.clip_rect_view);
        mTitleView = (TitleView) findViewById(R.id.title);
        mBottom_image = (ImageView) findViewById(R.id.bottom_image);
        mFull_layout = (RelativeLayout) findViewById(R.id.full_layout);
        mTitleView.setOnTitleClicListener(new TitleView.OnTitleClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }

            @Override
            public void onRightTextClick(View v) {
                //裁剪
                clip();
            }
        });

        mOriginalPath = getIntent().getStringExtra("path");
        Glide.with(this).load(mOriginalPath).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {

                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                mFullImageView.initData(glideDrawable);
                return false;
            }
        }).into(mFullImageView);

    }

    private void clip() {
        Rect cropRect = mRectView.getClipRect();
        Bitmap paramBitmap = mFullImageView.onClipImage(cropRect);

        String filePath = savePicture(paramBitmap);
        Log.e(TAG, "clip: filePath = "+filePath);

        Intent intent = getIntent();
        intent.putExtra("path",filePath);
        setResult(RESULT_OK,intent);
        finish();

    }


    private String savePicture(Bitmap bitmap){
        File filePic ;
        try {
            filePic = new File(getExternalFilesDir("ClipImage")+"/" + System.currentTimeMillis()/1000 + ".jpg");
            if (!filePic.exists()) {
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }


}
