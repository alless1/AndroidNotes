package com.example.aotuman.verification.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.aotuman.verification.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by aotuman on 2016/7/13.
 */
public class VerificationCodeView extends View {
    /**
     * 文本
     */
    private String mTitleText;
    /**
     * 文本的颜色
     */
    private int mTextColor;
    /**
     * 文本的大小
     */
    private int mTextSize;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    /**
     * 初始化画笔
     */
    private Paint mTextPaint;
    private Paint mPointPaint;
    private Paint mPathPaint;
    /**
     * 干扰点坐标的集合
     */
    private ArrayList<PointF> mPoints = new ArrayList<PointF>();
    /**
     * 绘制贝塞尔曲线的路径集合
     */
    private ArrayList<Path> mPaths = new ArrayList<Path>();

    public VerificationCodeView(Context context) {
        this(context, null);
    }

    public VerificationCodeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VerificationCodeView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.VerificationCodeView, defStyle, 0);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int content = typedArray.getIndex(i);
            switch (content) {
                case R.styleable.VerificationCodeView_textContent:
                    mTitleText = typedArray.getString(content);
                    break;
                case R.styleable.VerificationCodeView_textColor:
                    mTextColor = typedArray.getColor(content, Color.BLACK);
                    break;
                case R.styleable.VerificationCodeView_textSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTextSize = typedArray.getDimensionPixelSize(content, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        init();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleText = randomText();
                postInvalidate();
            }
        });
    }

    /**
     * EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
     * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
     * UNSPECIFIED：表示子布局想要多大就多大，很少使用
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = (int) (getPaddingLeft() + mBound.width() + getPaddingRight());
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = (int) (getPaddingTop() + mBound.height() + getPaddingBottom());
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initData();
        Random mRandom = new Random();
        //生成随机的背景颜色
        mTextPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mTextPaint);
        //生成随机的文字颜色
        mTextPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
        //将文字画在布局的中间
        canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mTextPaint);

        // 产生干扰效果1 -- 干扰点
        for (PointF pointF : mPoints) {
            mPointPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawPoint(pointF.x, pointF.y, mPointPaint);
        }
        // 产生干扰效果2 -- 干扰线
        for (Path path : mPaths) {
            mPathPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawPath(path, mPathPaint);
        }

    }

    /**
     * 生成随机的四位数字验证码
     *
     * @return
     */
    private String randomText() {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set) {
            sb.append("" + i);
        }

        return sb.toString();
    }

    private void initData() {
        Random mRandom = new Random();
        // 获取控件的宽和高，此时已经测量完成
        int mHeight = getHeight();
        int mWidth = getWidth();
        mPoints.clear();
        // 生成干扰点坐标
        for (int i = 0; i < 150; i++) {
            PointF pointF = new PointF(mRandom.nextInt(mWidth) + 10, mRandom.nextInt(mHeight) + 10);
            mPoints.add(pointF);
        }
        mPaths.clear();
        // 生成干扰线坐标
        for (int i = 0; i < 2; i++) {
            Path path = new Path();
            int startX = mRandom.nextInt(mWidth / 3) + 10;
            int startY = mRandom.nextInt(mHeight / 3) + 10;
            int endX = mRandom.nextInt(mWidth / 2) + mWidth / 2 - 10;
            int endY = mRandom.nextInt(mHeight / 2) + mHeight / 2 - 10;
            path.moveTo(startX, startY);
            path.quadTo(Math.abs(endX - startX) / 2, Math.abs(endY - startY) / 2, endX, endY);
            mPaths.add(path);
        }
    }

    private void init() {
        // 初始化文字画笔
        /**
         * 获得绘制文本的宽和高
         */
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);

        mBound = new Rect();
        //获取到的存在mBound里面
        mTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
        // 初始化干扰点画笔
        mPointPaint = new Paint();
        mPointPaint.setStrokeWidth(6);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND); // 设置断点处为圆形
        // 初始化干扰线画笔
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(5);
        mPathPaint.setColor(Color.GRAY);
        mPathPaint.setStyle(Paint.Style.STROKE); // 设置画笔为空心
        mPathPaint.setStrokeCap(Paint.Cap.ROUND); // 设置断点处为圆形
    }
}
