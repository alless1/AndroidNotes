package com.speed.vpnsocks.test.utils;

import com.speed.vpnsocks.netgurad.Usage;
import com.speed.vpnsocks.test.bean.UsageFlowBean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * Created by chengjie on 2019/1/11
 * Description:
 */
public class UsageFlowUtils {
    private static List<UsageFlowBean> mList = new ArrayList<>();

    public static void addUsage(Usage usage){
        synchronized (UsageFlowUtils.class){
            if(usage.Uid==-1)
                return;
            for (UsageFlowBean usageFlowBean : mList) {
                if(usageFlowBean.uid == usage.Uid){
                    usageFlowBean.send += usage.Sent;
                    usageFlowBean.receive += usage.Received;
                    return;
                }
            }
            mList.add(new UsageFlowBean(usage.Uid,usage.Sent,usage.Received));
        }
    }

    public static List<UsageFlowBean> getCurrentUsageCopy(){
        synchronized (UsageFlowUtils.class){
            ArrayList<UsageFlowBean> arrayList = new ArrayList<>();
            for (UsageFlowBean usage : mList) {
                arrayList.add(new UsageFlowBean(usage.uid,usage.send,usage.receive));
            }
            return arrayList;
        }
    }

    public static void clear(){
        synchronized (UsageFlowUtils.class){
            mList.clear();
        }
    }
}
