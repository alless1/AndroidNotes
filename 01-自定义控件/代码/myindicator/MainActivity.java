package alless.myindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TriangleIndicator mTriangleIndicator;
    private ViewPager mViewPager;
    private List<String> mTitles = Arrays.asList("标题1", "标题2", "标题3", "标题4",
            "标题5", "标题6", "标题7", "标题8", "标题9");
    private List<IndicatorFragment> mContents = new ArrayList<IndicatorFragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initView() {
        mTriangleIndicator = (TriangleIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    private void initData() {
        addContents();
        mViewPager.setAdapter(mAdapter);
        //Indicator设置tab
        mTriangleIndicator.setTabItemTitles(mTitles);
        //Indicator绑定ViewPager,初始位置0
        mTriangleIndicator.setViewPager(mViewPager, 0);
    }

    private void addContents() {
        for (String title : mTitles) {
            IndicatorFragment fragment = IndicatorFragment.newInstance(title);
            mContents.add(fragment);
        }
    }

    private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mContents.get(position);
        }

        @Override
        public int getCount() {
            return mContents.size();
        }
    };

}
