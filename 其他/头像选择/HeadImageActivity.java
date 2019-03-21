package com.sankeyun.ecg.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sankeyun.ecg.R;
import com.sankeyun.ecg.utils.SPHelper;
import com.sankeyun.ecg.utils.Utils;
import com.sankeyun.ecg.widget.TitleView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

/**
 * Created by ${程杰} on 2018/1/17.
 * 描述:查看大图
 */

public class HeadImageActivity extends Activity {

    private static final String TAG = "HeadImageActivity";
    private static final int PICK_IMAGE = 1;
    private static final int CLIP_IMAGE = 2;
    private TitleView mTitleView;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_head_image);
        mImageView = (ImageView) findViewById(R.id.image);
        mTitleView = (TitleView) findViewById(R.id.title);
        mTitleView.setOnTitleClicListener(new TitleView.OnTitleClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }

            @Override
            public void onRightTextClick(View v) {
                //弹出选择框
                takePhoto();
            }
        });

        Glide.with(this).load(SPHelper.getInstance(this).getHeadIcon()).into(mImageView);

    }

    private void takePhoto() {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) //参数1 显示资源类型 参数2 是否可以同时选择不同的资源类型 true表示不可以 false表示可以
//            .theme(R.style.Matisse_Dracula) //选择主题 默认是蓝色主题，Matisse_Dracula为黑色主题
                .countable(true) //是否显示数字
                .capture(true)  //是否可以拍照
                .captureStrategy(//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                        new CaptureStrategy(true, "com.sankeyun.ecg.fileprovider"))
                .maxSelectable(1)  //最大选择资源数量
                /*.gridExpectedSize( getResources().getDimensionPixelSize(R.dimen.grid_expected_size)) //设置列宽*/
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) //设置屏幕方向
                .thumbnailScale(0.85f)  //图片缩放比例
                .imageEngine(new GlideEngine())  //选择图片加载引擎
                .forResult(PICK_IMAGE);  //设置requestcode,开启Matisse主页面
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode)
            return;
        if(requestCode==PICK_IMAGE){//选择图片
            if(data==null)
                return;
            List<Uri> uris = Matisse.obtainResult(data);
            String realFilePath = Utils.getRealFilePath(this, uris.get(0));
            Log.e(TAG, "onActivityResult: realFilePath = "+realFilePath);
            Intent intent = new Intent(this, ClipImageActivity.class);
            intent.putExtra("path",realFilePath);
            startActivityForResult(intent,CLIP_IMAGE);
        }else if(requestCode==CLIP_IMAGE){//裁剪过后的图片
            String path = data.getStringExtra("path");
            Glide.with(this).load(path).into(mImageView);
            SPHelper.getInstance(this).saveHeadIcon(path);
        }
    }
}
