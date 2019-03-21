package com.speed.vpnsocks.test.core;

import android.os.Build;

import com.speed.vpnsocks.test.bean.SocksInfo;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.listener.CRNConnectionListener;
import com.speed.vpnsocks.test.listener.IPingTestListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class SpeedTestManager {


   private ExecutorService mThreadExecutor = Executors.newCachedThreadPool();
   private static SpeedTestManager sSpeedTestManager;
   private SpeedTestManager(){
   }
   public static SpeedTestManager getInstance(){
       if(sSpeedTestManager==null){
           synchronized (SpeedTestManager.class){
               if(sSpeedTestManager ==null)
                   sSpeedTestManager = new SpeedTestManager();
           }
       }
       return sSpeedTestManager;
   }

   public void startTask(SpeedTestRequest request){
/*       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){//7.0以上使用okHttp
           mThreadExecutor.submit(new SpeedTestForOkHttp(request));
       }else {
           mThreadExecutor.submit(new SpeedTestForSocket(request));
       }*/
       mThreadExecutor.submit(new SpeedTestForSocket(request));
   }

    /**
     *
     * @param host www.baidu.com or 192.168.0.2
     * @param listener
     */
   public void pingTest(String host, IPingTestListener listener){
       PingTest pingTest = new PingTest(host, 5, listener);
       mThreadExecutor.submit(pingTest);
   }

   public void crnTest(SocksInfo info, CRNConnectionListener listener){
       CRNTest crnTest = new CRNTest(info, listener);
       mThreadExecutor.submit(crnTest);
   }


}
