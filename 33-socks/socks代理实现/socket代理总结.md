### 一、socks代理 ###
说明：socks代理有v4和v5版本，如果是自己实现握手协议，是可以自定义客户端版本的。如果是使用系统api，Proxy.Type.SOCKS，android 7.0 以下的系统是默认v4版本（貌似不支持身份校验），7.0以上才是v5版本。以下是，两种方式的Socket实现。

#### 二、使用socket实现socks5代理 ####

1.创建socket客户端，和普通socket创建没什么区别，区别在于目标地址不同，此处填入代理地址。

            Socket socket = new Socket();
			socket.setSendBufferSize(SOCKET_SEND_BUFFER_SIZE);
            socket.setReceiveBufferSize(SOCKET_RECEIVE_BUFFER_SIZE);
            socket.connect(new InetSocketAddress(ip, port), CONNECTION_TIMEOUT_SECOND * 1000);

2.身份校验，和服务端协商，并发送认证信息。
	
    private boolean chooseAuthentication(InputStream is, OutputStream os, String user, String password) throws IOException {
        byte[] req = new byte[]{0x05, 0x01, 0x02};//05协议版本，01一种方式，02用户名和密码
        os.write(req);
        os.flush();
        int version = is.read();//05,协议版本
        int METHOD = is.read();//02,用户名和密码方式
        //Mylog.d(TAG, "chooseAuthentication: version =" + version + "method =" + METHOD);
        Log.d(TAG, "chooseAuthentication: version =" + version + "METHOD=" + METHOD);
        if (METHOD == 0x02) {
            //校验
            byte[] buff = new byte[1024];
            buff[0] = 0x05;//协议版本
            buff[1] = (byte) user.length();
            System.arraycopy(user.getBytes("UTF-8"), 0, buff, 2, buff[1]);
            int pIndex;
            int pLenIndex;
            pIndex = buff[1] + 2;
            pLenIndex = pIndex + 1;
            buff[pIndex] = (byte) password.length();
            System.arraycopy(password.getBytes("UTF-8"), 0, buff, pLenIndex, buff[pIndex]);
            os.write(buff, 0, pLenIndex + buff[pIndex]);
            os.flush();

            int version1 = is.read();
            int status = is.read();

            if (status == 0x00) {
                Log.d(TAG, "socks5 verify success");
                return true;
            } else {
                Log.e(TAG, "socks5 verify fail status =" + status);
                onError(ErrorCode.CRN_VERIFY_FAIL, "socks5 verify fail status =" + status);
                return false;
            }

        } else {
            Log.e(TAG, "socks5 handshake fail METHOD =" + METHOD);
            onError(ErrorCode.CRN_VERIFY_FAIL, "socks5 handshake fail METHOD =" + METHOD);
            return false;
        }
    }

3.发送需求请求的业务地址。（例如：host=www.baidu.com port=80）

    private void sendTargetAddress(String host, int dport, InputStream is, OutputStream os) throws IOException {

        if (isIpv4(host)) {
            // 传ipv4的方式
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x01};
            os.write(connReq);
            byte[] hostIp = InetAddress.getByName(mHost).getAddress();
            os.write(hostIp);

        } else {
            // 传域名的方式
            int domainLen = host.length();
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x03};
            os.write(connReq);
            os.write((byte) domainLen);
            os.write(host.getBytes());
        }


        short port = (short) dport;
        byte[] portBytes = new byte[2];
        portBytes[0] = (byte) (port >> 8);
        portBytes[1] = (byte) (port & 0x00ff);
        os.write(portBytes);
        os.flush();


        byte[] connRep = new byte[4];
        is.read(connRep);
        Log.d(TAG, "sendTargetAddress:response socks version is " + connRep[0]);
    }


#### 二、使用系统api，实现socks5代理，此方式只在android7.0以上有效 ####

    protected void initProxyClient() {
        InetSocketAddress mInetSocketAddress = new InetSocketAddress(mSocksInfo.getAddress(), mSocksInfo.getPort());
        Proxy mProxy = new Proxy(Proxy.Type.SOCKS, mInetSocketAddress);
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            private PasswordAuthentication authentication = new PasswordAuthentication(mSocksInfo.getUser(), mSocksInfo.getPassword().toCharArray());

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        });
        try {
            mSocket = new Socket(mProxy);
            mSocket.setSendBufferSize(SOCKET_SEND_BUFFER_SIZE);
            mSocket.setReceiveBufferSize(SOCKET_RECEIVE_BUFFER_SIZE);
            mSocket.connect(new InetSocketAddress(mHost, mPort), CONNECTION_TIMEOUT_SECOND * 1000);
            mSocket.setSoTimeout(READ_TIMEOUT_SECOND * 1000);
            mIs = mSocket.getInputStream();
            mOs = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            mSocket = null;
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }
    }