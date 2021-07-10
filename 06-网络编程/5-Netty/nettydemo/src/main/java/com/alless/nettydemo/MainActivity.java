package com.alless.nettydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alless.nettydemo.handler.LoginServerHandler;
import com.alless.nettydemo.manager.LoginManager;
import com.alless.nettydemo.net.SocketThread;
import com.alless.nettydemo.packet.LoginPacketOther;
import com.alless.nettydemo.packet.LogoutPacket;
import com.alless.nettydemo.utils.Md5Utils;

public class MainActivity extends AppCompatActivity {

    private String mIp;
    private int mPort;
    private SocketThread mLoginServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //连接
        connect();
        //登录
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginServerThread.sendPacket(new LoginPacketOther("lizebo", Md5Utils.getMd5("88888")));
            }
        });
        //退出
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginServerThread.sendPacket(new LogoutPacket(213));
            }
        });


    }

    private void connect() {
        mIp = "bb.skybluebird.com.cn";
        mPort = 3333;
        mLoginServerThread = new SocketThread(mIp,mPort, new LoginServerHandler());
        mLoginServerThread.start();
        LoginManager.instance().setLoginServerThread(mLoginServerThread);
    }
}
