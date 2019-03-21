package com.speed.vpnsocks.test.core;

import android.util.Log;

import com.speed.vpnsocks.test.listener.IPingTestListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PingTest implements Runnable {
    private static final String TAG = "PingTest";
    private String server ;
    private int count;
    private IPingTestListener mListener;
    private int instantRtt = 0;
    private int avgRtt = 0;
    private boolean isRunning = false;
    private boolean isLoss = false;
    private final int PACKET_SIZE = 40;
    private final int TIMEOUT = 5;//总的超时时间，单位s。

    public PingTest(String serverIpAddress, int pingTryCount,IPingTestListener listener) {
        this.server = serverIpAddress;
        this.count = pingTryCount;
        mListener = listener;
    }

    public boolean isRunning(){
        return isRunning;
    }


    @Override
    public void run() {
        isRunning = true;
        isLoss = true;
        instantRtt = 0;
        avgRtt = 0;
        try {
            ProcessBuilder ps = new ProcessBuilder("ping","-c " + count,"-w " + (count+1),"-s "+PACKET_SIZE,this.server);

            ps.redirectErrorStream(true);
            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                //Log.d(TAG, "run: share_line ="+share_line);
                if (line.contains("icmp_seq")) {// 64 bytes from 14.215.177.38: icmp_seq=2 ttl=55 time=26.6 ms
                    instantRtt = (int) Double.parseDouble(line.split(" ")[line.split(" ").length - 2].replace("time=", ""));
                    mListener.onProgress(instantRtt);
                    Log.d(TAG, "run: instantRtt ="+instantRtt);
                }
                if (line.startsWith("rtt ")) {//rtt min/avg/max/mdev = 8.561/21.795/35.351/8.875 ms
                    avgRtt = (int) Double.parseDouble(line.split("/")[4]);
                    Log.d(TAG, "run: avgRtt ="+avgRtt);
                    isLoss = false;
                    break;
                }
            }
            pr.waitFor();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRunning = false;
        mListener.onCompletion(avgRtt>0?avgRtt:0);
    }



}
