package net.oschina.app.protocol;

import net.oschina.app.base.BaseProtocol;
import net.oschina.app.bean.NewsList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alless on 2017/4/25.
 * 进行网络请求
 */

public class NewsPagerProtocol extends BaseProtocol<NewsList> {
    @Override
    protected String getUrl() {
        return "http://www.oschina.net/action/api/news_list";
    }

    @Override
    protected Map<String, String> getParamsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("pageIndex", curIndex + "");
        map.put("catalog", "1");
        map.put("pageSize", "20");
        return map;
    }
}
