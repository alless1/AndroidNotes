package com.speed.vpnsocks.test.bean;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class SpeedTestReport {
    private int fileSize;
    private long startTime;
    private long endTime;

    private String fileLocalPath;
    private String fileNetPath;
    private String requestUrl;

    @Override
    public String toString() {
        return "SpeedTestReport{" +
                "fileSize=" + fileSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", fileLocalPath='" + fileLocalPath + '\'' +
                ", fileNetPath='" + fileNetPath + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", isProxy=" + isProxy +
                '}';
    }

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean proxy) {
        isProxy = proxy;
    }

    private boolean isProxy;

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getFileLocalPath() {
        return fileLocalPath;
    }

    public void setFileLocalPath(String fileLocalPath) {
        this.fileLocalPath = fileLocalPath;
    }

    public String getFileNetPath() {
        return fileNetPath;
    }

    public void setFileNetPath(String fileNetPath) {
        this.fileNetPath = fileNetPath;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
