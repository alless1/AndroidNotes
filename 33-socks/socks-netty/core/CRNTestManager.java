package com.network.vpnsocks.test.core;

import com.network.vpnsocks.test.bean.SocksInfo;
import com.network.vpnsocks.test.listener.CRNConnectionListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class CRNTestManager {


   private ExecutorService mThreadExecutor = Executors.newCachedThreadPool();
   private static CRNTestManager sCRNTestManager;
   private CRNTestManager(){
   }
   public static CRNTestManager getInstance(){
       if(sCRNTestManager ==null){
           synchronized (CRNTestManager.class){
               if(sCRNTestManager ==null)
                   sCRNTestManager = new CRNTestManager();
           }
       }
       return sCRNTestManager;
   }

   public void crnTest(SocksInfo info, CRNConnectionListener listener){
       CRNTest crnTest = new CRNTest(info, listener);
       mThreadExecutor.submit(crnTest);
   }


}
