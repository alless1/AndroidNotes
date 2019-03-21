package com.sankeyun.ecg.ui.activity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ${程杰} on 2018/5/21.
 * 描述:限定输入值  @IntDef可以定义int @StringDef可以定义String
 */

public class SexTest {
    public final static int MAN = 1;
    public final static int WOMAN = 0;
    private int mSex ;

    @IntDef({MAN,WOMAN})//限定输入值
    @Retention(RetentionPolicy.SOURCE)//表示注解所存活的时间
    public @interface Sex{//接口，定义新的注解类型

    }

    public void setSex(@Sex int sex){
        mSex = sex;
    }
}
