package com.alless.rxsensedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author:chengjie
 * Date:2018/7/6
 * Description:
 */
public class RxSenseService extends Service {
    private Timer timer = new Timer();
    private TimerTask task;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        task = new TimerTask() {
            @Override
            public void run() {
                CommandExecution.execCommand(CmdString.RXSENSE_STATE,true);
            }
        };
        timer.schedule(task, 2000, 2000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
