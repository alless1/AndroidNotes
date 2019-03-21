package com.speed.vpnsocks.test.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class SocksInfo implements Parcelable {
    private String address;
    private int port;
    private String user;
    private String password;

    public SocksInfo() {
    }
    public SocksInfo(Parcel in){
        address = in.readString();
        port = in.readInt();
        user = in.readString();
        password = in.readString();
    }

    public SocksInfo(String address, int port, String user, String password) {
        this.address = address;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeInt(port);
        dest.writeString(user);
        dest.writeString(password);
    }

    public static final Parcelable.Creator<SocksInfo> CREATOR = new Creator<SocksInfo>() {
        @Override
        public SocksInfo createFromParcel(Parcel source) {
            return new SocksInfo(source);
        }

        @Override
        public SocksInfo[] newArray(int size) {
            return new SocksInfo[size];
        }
    };

    @Override
    public String toString() {
        return "SocksInfo{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
