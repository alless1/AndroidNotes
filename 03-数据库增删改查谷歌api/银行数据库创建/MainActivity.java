package com.itheima.db;

import java.util.Random;

import com.itheima.db.dao.BankDBDao;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BankDBDao dao = new BankDBDao(this);
//        Random random = new Random();
//        for(int i =0;i<20;i++){
//        	 dao.add("уе"+i, random.nextFloat()+random.nextInt(500));
//        }
    }

    
}
