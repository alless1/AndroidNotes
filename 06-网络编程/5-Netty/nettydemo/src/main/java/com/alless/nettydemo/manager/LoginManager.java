package com.alless.nettydemo.manager;

import com.alless.nettydemo.DataBuffer;
import com.alless.nettydemo.net.SocketThread;
import com.alless.nettydemo.utils.Logger;
import com.google.protobuf.CodedInputStream;
import com.mogujie.tt.proto.BBProtocol;

import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;

public class LoginManager extends BaseManager {
    private static LoginManager inst;
    private SocketThread loginServerThread;
    public static LoginManager instance() {
        synchronized (LoginManager.class) {
            if (inst == null) {
                inst = new LoginManager();
            }
            return inst;
        }
    }


    public void setLoginServerThread(SocketThread loginServerThread) {
        this.loginServerThread = loginServerThread;
    }

    public SocketThread getLoginServerThread(){
        return loginServerThread;
    }


    public static void onLoginRsp(DataBuffer dataBuffer1) {
        CodedInputStream codedInputStream = CodedInputStream.newInstance(new ByteBufInputStream(dataBuffer1.getOrignalBuffer()));
        BBProtocol.PBUserInfo userInfo = null;
        try {
            userInfo = BBProtocol.PBUserInfo.parseFrom(codedInputStream);
        } catch (IOException e) {
            Logger.e("onLoginRsp#", e.getMessage());
            return;
        }

        if (userInfo != null) {
            int departId = userInfo.getDwDptID();
            String strAccount = userInfo.getStrAccount();
            String strName = userInfo.getStrName();
            int dwUserID = userInfo.getDwUserID();
            String strSSO = userInfo.getStrSSO();
            String strToDoUrl = userInfo.getStrToDoUrl();
            String strToReadUrl = userInfo.getStrToReadUrl();
            String strAllAppUrl = userInfo.getStrAllAppUrl();
            String strWorkbenchUrl = userInfo.getStrWorkbenchUrl();
            Logger.e("userInfo: ","departId = "+departId
                    +" strName="+strName
                    +" dwUserID="+dwUserID
                    +" strSSO="+strSSO
                    +" strToDoUrl="+strToDoUrl
                    +" strToReadUrl="+strToReadUrl
                    +" strAllAppUrl="+strAllAppUrl
                    +" strWorkbenchUrl="+strWorkbenchUrl);
        }
    }
}
