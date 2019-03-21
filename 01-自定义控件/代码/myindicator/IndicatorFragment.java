package alless.myindicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/18.
 */

public class IndicatorFragment extends Fragment {
    private static final String BUNDLE_NAME = "title";
    private String mTitle;

    public static IndicatorFragment newInstance(String title){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_NAME, title);
        /**
         * 官方推荐Fragment.setArguments(Bundle bundle)这种方式来传递参数，而不推荐通过构造方法直接来传递参数
         * 这是因为假如Activity重新创建（横竖屏切换）时，会重新构建它所管理的Fragment，原先的Fragment的字段值将会全
         * 部丢失，但是通过Fragment.setArguments(Bundle bundle)方法设置的bundle会保留下来。所以尽量使用
         * Fragment.setArguments(Bundle bundle)方式来传递参数
         */
        IndicatorFragment fragment = new IndicatorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle!=null){
            mTitle = bundle.getString(BUNDLE_NAME);
        }
        //返回一个TextView
        TextView tv = new TextView(getContext());
        tv.setText(mTitle);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
