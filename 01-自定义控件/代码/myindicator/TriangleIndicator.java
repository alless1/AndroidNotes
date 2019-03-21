package alless.myindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * @author alless1
 *         自定义Indicator
 *         绑定ViewPager使用
 */

public class TriangleIndicator extends LinearLayout {
    private Paint mPaint;
    private Path mPath;
    private int mTriangleHeight;
    private int mTriangleWidth;
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
    private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
    private int mStratX;
    private int mMoveX;
    private int mTabVisibleCount;
    private List<String> mTabTitles;
    private static final int COUNT_DEFAULT_TAB = 4;
    private static final int COLOR_TEXT_NORMAL = Color.parseColor("#FFFFFF");
    private static final int COLOR_TEXT_HIGHLIGHT = Color.parseColor("#EBC700");
    private ViewPager mViewPager;
    private int mLastPosition;
    private int mTabWidth;

    public TriangleIndicator(Context context) {
        this(context, null);
    }

    public TriangleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化xml设置属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TriangleIndicator);
        mTabVisibleCount = ta.getInt(R.styleable.TriangleIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
        ta.recycle();

        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * 设置三角形的大小
     * onSizeChanged()在控件大小发生变化的时候调用(例如第一次初始化控件的时候) 布局过程中，
     * 先调onMeasure计算每个child的大小， 然后调用onLayout对child进行布局，
     * onSizeChanged（）是在布局发生变化时的回调函数，间接回去调用onMeasure, onLayout函数重新布局
     * onSizeChanged的启动时间在onDraw之前
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) ((w / mTabVisibleCount) * RADIO_TRIANGLE_WIDTH);
        mTriangleWidth = Math.min(mTriangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
        mStratX = w / 2 / mTabVisibleCount - mTriangleWidth / 2;
        initPath();
    }

    /**
     * 初始化三角形路径,角度设为30°
     */
    private void initPath() {
        mTriangleHeight = (int) (mTriangleWidth / 2 * Math.tan(Math.PI / 6));
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * 绘制三角形
     * 绘制VIew本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        /**
         * save：用来保存Canvas的状态,快照。
         *
         * restore：用来恢复Canvas之前保存的状态。
         *
         * save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。
         */
        canvas.save();
        //移动画布到指定位置
        canvas.translate(mStratX + mMoveX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    /**
     * 外部设置可见tab的数量
     */
    public void setTabVisibleCount(int tabVisibleCount) {
        mTabVisibleCount = tabVisibleCount;
    }

    /**
     * 外部设置tab的标题内容
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTabTitles = titles;
            for (String title : mTabTitles) {
                this.addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }

    /**
     * 根据title生成tab(TextView)
     */
    private View generateTextView(String title) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.width = getScreenWidth() / mTabVisibleCount;
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * 给每个Tab设置点击事件
     */
    private void setItemClickEvent() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 设置关联的ViewPager
     * 启用ViewPager的监听,移动三角形,并对外提供监听接口.
     *
     * @param viewpager
     * @param position
     */
    public void setViewPager(ViewPager viewpager, int position) {
        mViewPager = viewpager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 三角形跟随ViewPager移动的距离就是：
                // tabWidth*positionOffset+position*tabWidth
                scroll(position, positionOffset);

                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset,
                            positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }

            }
        });
        mViewPager.setCurrentItem(position);
        highLightTextView(position);
    }

    /**
     * 三角形跟随ViewPager移动,并相应的移动Tab
     *
     * @param position
     * @param positionOffset
     */
    public void scroll(int position, float positionOffset) {
        mTabWidth = getWidth() / mTabVisibleCount;
        mMoveX = (int) (mTabWidth * (positionOffset + position));
        if (position >= (mTabVisibleCount - 2) && positionOffset > 0
                && getChildCount() > mTabVisibleCount && position < getChildCount() - 2) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * mTabWidth
                        + (int) (mTabWidth * positionOffset), 0);
            } else {
                this.scrollTo(position * mTabWidth
                        + (int) (mTabWidth * positionOffset), 0);
            }
        }
        invalidate();
    }

    /**
     * 高亮被点击的tab
     *
     * @param position
     */
    private void highLightTextView(int position) {
        View currentView = getChildAt(position);
        if (currentView instanceof TextView) {
            ((TextView) currentView).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
        if (mLastPosition != position) {
            View lastView = getChildAt(mLastPosition);
            if (lastView instanceof TextView) {
                ((TextView) lastView).setTextColor(COLOR_TEXT_NORMAL);
            }
            mLastPosition = position;
        }
    }

    /**
     * 提供一个接口供外部ViewPager使用
     *
     * @author Administrator
     */
    public interface PageOnChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    public PageOnChangeListener mListener;

    public void setViewPagerOnPageChangeListener(PageOnChangeListener listener) {
        mListener = listener;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


}
