package com.speed.vpnsocks.test.bean;

import com.speed.vpnsocks.test.listener.ISpeedTestListener;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class SpeedTestRequest {
    private String url;//网络链接

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    private TestType testType;
    private SocksInfo mSocksInfo;
    private String saveFilePath;//保存下载文件的地址
    private ISpeedTestListener listener;

    private String uploadFilePath;//上传文件的本地地址

    private int uploadTimeMax;//ms
    private int downloadTimeMax;//ms
    private int socketSendBufferSize = 1024 * 64;
    private int socketReceiveBufferSize = 1024 * 60;
    private int localWriteReadBufferSize = 1024 * 60;
    public enum TestType{DOWNLOAD,UPLOAD}

    public int getSocketSendBufferSize() {
        return socketSendBufferSize;
    }

    public void setSocketSendBufferSize(int socketSendBufferSize) {
        this.socketSendBufferSize = socketSendBufferSize;
    }

    public int getSocketReceiveBufferSize() {
        return socketReceiveBufferSize;
    }

    public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
        this.socketReceiveBufferSize = socketReceiveBufferSize;
    }

    public int getLocalWriteReadBufferSize() {
        return localWriteReadBufferSize;
    }

    public void setLocalWriteReadBufferSize(int localWriteReadBufferSize) {
        this.localWriteReadBufferSize = localWriteReadBufferSize;
    }


    public int getUploadTimeMax() {
        return uploadTimeMax;
    }

    public void setUploadTimeMax(int uploadTimeMax) {
        this.uploadTimeMax = uploadTimeMax;
    }

    public int getDownloadTimeMax() {
        return downloadTimeMax;
    }

    public void setDownloadTimeMax(int downloadTimeMax) {
        this.downloadTimeMax = downloadTimeMax;
    }






    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public SocksInfo getSocksInfo() {
        return mSocksInfo;
    }

    public void setSocksInfo(SocksInfo socksInfo) {
        this.mSocksInfo = socksInfo;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public ISpeedTestListener getListener() {
        return listener;
    }

    public void setListener(ISpeedTestListener listener) {
        this.listener = listener;
    }


}
