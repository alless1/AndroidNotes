package com.speed.vpnsocks.test.listener;

public interface IPingTestListener {
        void onProgress(int instantRTT);
        void onCompletion(int avgRT);
    }