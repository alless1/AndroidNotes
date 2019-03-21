package com.sdk.speed.traffic.socks;

import com.sdk.speed.traffic.core.TrafficLib;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
 
/**
 * 标准的socks代理服务器，支持sock4与sock5代理
 * 
 * @author Administrator
 * 
 */
public class SocksServerOneThread implements Runnable {
 
    /**
     * 来源的代理socket
     */
    private final Socket socket;
    /**
     * 是否开启socks4代理
     */
    private final boolean openSock4;
    /**
     * 是否开启socks5代理
     */
    private final boolean openSock5;
    /**
     * socks5代理的登录用户名，如果 不为空表示需要登录验证
     */
    private final String user;
    /**
     * socks5代理的登录密码，
     */
    private final String pwd;
    /**
     * socks是否需要进行登录验证
     */
    private final boolean socksNeekLogin;
 
    /**
     * @param socket
     *            来源的代理socket
     * @param openSock4
     *            是否开启socks4代理
     * @param openSock5
     *            是否开启socks5代理
     * @param user
     *            socks5代理的登录用户名，如果 不为空表示需要登录验证
     * @param pwd
     *            socks5代理的登录密码，
     */
    protected SocksServerOneThread(Socket socket, boolean openSock4, boolean openSock5, String user, String pwd) {
        this.socket = socket;
        this.openSock4 = openSock4;
        this.openSock5 = openSock5;
        this.user = user;
        this.pwd = pwd;
        this.socksNeekLogin = null != user;
    }
 
    public void run() {
        // 获取来源的地址用于日志打印使用
        String addr = socket.getRemoteSocketAddress().toString();
        log("process one socket : %s", addr);
        // 声明流
        InputStream a_in = null, b_in = null;
        OutputStream a_out = null, b_out = null;
        Socket proxy_socket = null;
        ByteArrayOutputStream cache = null;
        try {
            a_in = socket.getInputStream();
            a_out = socket.getOutputStream();
 
            // 获取协议头。取代理的类型，只有 4，5。
            byte[] tmp = new byte[1];
            int n = a_in.read(tmp);
            if (n == 1) {
                byte protocol = tmp[0];
                if ((openSock4 && 0x04 == protocol)) {// 如果开启代理4，并以socks4协议请求
                    proxy_socket = sock4_check(a_in, a_out);
                } else if ((openSock5 && 0x05 == protocol)) {// 如果开启代理5，并以socks5协议请求
                    proxy_socket = sock5_check(a_in, a_out);
                } else {// 非socks 4 ,5 协议的请求
                    log("not socks proxy : %s  openSock4[] openSock5[]", tmp[0], openSock4, openSock5);
                }
                if (null != proxy_socket) {
                    CountDownLatch latch = new CountDownLatch(1);
                    b_in = proxy_socket.getInputStream();
                    b_out = proxy_socket.getOutputStream();
                    // 交换流数据
                    if (80 == proxy_socket.getPort()) {
                        cache = new ByteArrayOutputStream();
                    }
                    transfer(latch, a_in, b_out, cache);//将客户端发过来的数据，写出到远程输出流
                    transfer(latch, b_in, a_out, cache);//将远程响应的数据流，写到客户端的输出流去
                    try {
                        latch.await();
                    } catch (Exception e) {
                    }
                }
            } else {
                log("socks error : %s", Arrays.toString(tmp));
            }
        } catch (Exception e) {
            log("exception : %s %s", e.getClass(), e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            log("close socket, system cleanning ...  %s ", addr);
            closeIo(a_in);
            closeIo(b_in);
            closeIo(b_out);
            closeIo(a_out);
            closeIo(socket);
            closeIo(proxy_socket);
            if (null != cache) {
                cache2Local(cache);
            }
        }
    }
 
    private void cache2Local(ByteArrayOutputStream cache) {
        // OutputStream result = null;
        // try {
        // String fileName = System.currentTimeMillis() + "_"
        // + Thread.currentThread().getId();
        // result = new FileOutputStream("E:/cache/" + fileName + ".info");
        // result.write(cache.toByteArray());
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // closeIo(result);
        // }
    }
 
    /**
     * 参考http://www.ietf.org/rfc/rfc1928.txt
     * sock5代理头处理
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    private Socket sock5_check(InputStream in, OutputStream out) throws IOException {
    	/*
    	 *客户端发送给服务端，告知客户端用的socks版本和认证方法
    	 *以下客户端发送过来的，数字都代表字节数
				   +----+----------+----------+
                   |VER | NMETHODS | METHODS  |
                   +----+----------+----------+
                   | 1  |    1     | 1 to 255 |
                   +----+----------+----------+
           服务端选取认证方法，返回给客户端
                         +----+--------+
                         |VER | METHOD |
                         +----+--------+
                         | 1  |   1    |
                         +----+--------+
    	 */
        byte[] tmp = new byte[2];//读取版本后面的两个字节(因为NMETHODS和METHODS两个加起来，至少共两个字节)
        in.read(tmp);
        boolean isLogin = false;
        byte method = tmp[1];	
        if (0x02 == tmp[0]) {	//如果第一个字节是2，表示METHODS有2个字节
            method = 0x00;	
            in.read();	//原来METHODS读取了一个自己，所以需要再读一个字节
        }
        if (socksNeekLogin) {	//这里其实不管客户端客户端支持什么认证类型，都是由服务端来控制指定
            method = 0x02;	//如果服务端启动时要求用户名密码认证，则就返回给客户端，一定要用户名密码认证
        }
        tmp = new byte[] { 0x05, method };//返回给客户端的两个字节
        out.write(tmp);
        out.flush();
        // Socket result = null;
        Object resultTmp = null;
        if (0x02 == method) {// 处理登录.
            int b = in.read();
            String user = null;
            String pwd = null;
            if (0x01 == b) {
                b = in.read();
                tmp = new byte[b];
                in.read(tmp);
                user = new String(tmp);
                b = in.read();
                tmp = new byte[b];
                in.read(tmp);
                pwd = new String(tmp);
                if (null != user && user.trim().equals(this.user) && null != pwd && pwd.trim().equals(this.pwd)) {// 权限过滤
                    isLogin = true;
                    tmp = new byte[] { 0x05, 0x00 };// 登录成功
                    out.write(tmp);
                    out.flush();
                    log("%s login success !", user);
                } else {
                    log("%s login faild !", user);
                }
            }
        }
        byte cmd = 0;
        if (!socksNeekLogin || isLogin) {// 验证是否需要登录
        	
        	/*
SOCKS Client提交转发请求
        +----+-----+-------+------+----------+----------+
        |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
        +----+-----+-------+------+----------+----------+
        | 1  |  1  | X'00' |  1   | Variable |    2     |
        +----+-----+-------+------+----------+----------+
VER         对于版本5这里是0x05

CMD         可取如下值:

            0x01    CONNECT
            0x02    BIND
            0x03    UDP ASSOCIATE

RSV         保留字段，必须为0x00

ATYP        用于指明DST.ADDR域的类型，可取如下值:

            0x01    IPv4地址
            0x03    FQDN(全称域名)
            0x04    IPv6地址

DST.ADDR    CMD相关的地址信息，不要为DST所迷惑

            如果是IPv4地址，这里是big-endian序的4字节数据

            如果是FQDN，比如"www.nsfocus.net"，
      	注意，没有结尾的NUL字符，非ASCIZ串，第一字节是长度域      
            这里将是:
            0F 77 77 77 2E 6E 73 66 6F 63 75 73 2E 6E 65 74

            如果是IPv6地址，这里是16字节数据。

DST.PORT    CMD相关的端口信息，big-endian序的2字节数据
        	 */
            tmp = new byte[4];
            in.read(tmp);
            log("proxy header >>  %s", Arrays.toString(tmp));
            cmd = tmp[1];
            String host = getHost(tmp[3], in);	//读取转发目标地址
            tmp = new byte[2];	//读取目标端口
            in.read(tmp);
            int port = ByteBuffer.wrap(tmp).asShortBuffer().get() & 0xFFFF;
            log("connect %s:%s", host, port);
            /*
			发送响应报文:
			        +----+-----+-------+------+----------+----------+
			        |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
			        +----+-----+-------+------+----------+----------+
			        | 1  |  1  | X'00' |  1   | Variable |    2     |
			        +----+-----+-------+------+----------+----------+
             */
            ByteBuffer rsv = ByteBuffer.allocate(10);
            rsv.put((byte) 0x05);	//VER         对于版本5这里是0x05
            try {
                if (0x01 == cmd) {
                    //resultTmp = new Socket(host, port);
                    resultTmp = TrafficLib.getInstance().getProxySocket(host, port, 3000);
                    /*
					REP         可取如下值:
			            0x00        成功
			            0x01        一般性失败
			            0x02        规则不允许转发
			            0x03        网络不可达
			            0x04        主机不可达
			            0x05        连接拒绝
			            0x06        TTL超时
			            0x07        不支持请求包中的CMD
			            0x08        不支持请求包中的ATYP
			            0x09-0xFF   unassigned
                     */
                    rsv.put((byte) 0x00);
                } else if (0x02 == cmd) {
                    resultTmp = new ServerSocket(port);
                    rsv.put((byte) 0x00);
                } else {
                    rsv.put((byte) 0x05);
                    resultTmp = null;
                }
            } catch (Exception e) {
                rsv.put((byte) 0x05);
                resultTmp = null;
            }
            rsv.put((byte) 0x00);	//RSV         保留字段，必须为0x00
            /*
				ATYP        用于指明DST.ADDR域的类型，可取如下值:
		            0x01    IPv4地址
		            0x03    FQDN(全称域名)
		            0x04    IPv6地址
             */
            rsv.put((byte) 0x01);	//0x01    IPv4地址
            rsv.put(socket.getLocalAddress().getAddress());	//地址值
            Short localPort = (short) ((socket.getLocalPort()) & 0xFFFF);	
            rsv.putShort(localPort);
            tmp = rsv.array();
        } else {
            tmp = new byte[] { 0x05, 0x01 };// 登录失败
            log("socks server need login,but no login info .");
        }
        out.write(tmp);
        out.flush();
        if (null != resultTmp && 0x02 == cmd) {
            ServerSocket ss = (ServerSocket) resultTmp;
            try {
                resultTmp = ss.accept();
            } catch (Exception e) {
            } finally {
                closeIo(ss);
            }
        }
        return (Socket) resultTmp;
    }
 
    /**
     * sock4代理的头处理
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    private Socket sock4_check(InputStream in, OutputStream out) throws IOException {
        Socket proxy_socket = null;
        byte[] tmp = new byte[3];
        in.read(tmp);
        // 请求协议|VN1|CD1|DSTPORT2|DSTIP4|NULL1|
        int port = ByteBuffer.wrap(tmp, 1, 2).asShortBuffer().get() & 0xFFFF;
        String host = getHost((byte) 0x01, in);
        in.read();
        byte[] rsv = new byte[8];// 返回一个8位的响应协议
        // |VN1|CD1|DSTPORT2|DSTIP 4|
        try {
            //proxy_socket = new Socket(host, port);
            proxy_socket = TrafficLib.getInstance().getProxySocket(host, port, 3000);
            log("connect [%s] %s:%s", tmp[1], host, port);
            rsv[1] = 90;// 代理成功
        } catch (Exception e) {
            log("connect exception  %s:%s", host, port);
            rsv[1] = 91;// 代理失败.
        }
        out.write(rsv);
        out.flush();
        return proxy_socket;
    }
 
    /**
     * 获取目标的服务器地址
     * 
     * @createTime 2014年12月14日 下午8:32:15
     * @param type
     * @param in
     * @return
     * @throws IOException
     */
    private String getHost(byte type, InputStream in) throws IOException {
        String host = null;
        byte[] tmp = null;
        switch (type) {
        case 0x01:// IPV4协议
            tmp = new byte[4];	//如果是ipv4，则读取4个字节
            in.read(tmp);
            host = InetAddress.getByAddress(tmp).getHostAddress();
            break;
        case 0x03:// 使用域名
            int l = in.read();	//读取一个字节，表示后面域名长度，最多255
            tmp = new byte[l];
            in.read(tmp);
            host = new String(tmp);
            break;
        case 0x04:// 使用IPV6
            tmp = new byte[16];
            in.read(tmp);
            host = InetAddress.getByAddress(tmp).getHostAddress();
            break;
        default:
            break;
        }
        return host;
    }
 
    /**
     * IO操作中共同的关闭方法
     * 
     * @createTime 2014年12月14日 下午7:50:56
     * @param socket
     */
    protected static final void closeIo(Socket closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
 
    /**
     * IO操作中共同的关闭方法
     * 
     * @createTime 2014年12月14日 下午7:50:56
     * @param socket
     */
    protected static final void closeIo(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
 
    /**
     * 数据交换.主要用于tcp协议的交换
     * 
     * @createTime 2014年12月13日 下午11:06:47
     * @param lock
     *            锁
     * @param in
     *            输入流
     * @param out
     *            输出流
     */
    protected static final void transfer(final CountDownLatch latch, final InputStream in, final OutputStream out,
            final OutputStream cache) {
        new Thread() {
            public void run() {
                byte[] bytes = new byte[1024];
                int n = 0;
                try {
                    while ((n = in.read(bytes)) > 0) {
                        out.write(bytes, 0, n);
                        out.flush();
                        if (null != cache) {
                            synchronized (cache) {
                                cache.write(bytes, 0, n);
                            }
                        }
                    }
                } catch (Exception e) {
                }
                if (null != latch) {
                    latch.countDown();
                }
            };
        }.start();
    }
 
    private final static void log(String message, Object... args) {
        Date dat = new Date();
        String msg = String.format("%1$tF %1$tT %2$-5s %3$s%n", dat, Thread.currentThread().getId(),
                String.format(message, args));
        System.out.print(msg);
    }
 
    public static void startServer(int port, boolean openSock4, boolean openSock5, String user, String pwd)
            throws IOException {
        log("config >> port[%s] openSock4[%s] openSock5[%s] user[%s] pwd[%s]", port, openSock4, openSock5, user, pwd);
        ServerSocket ss = new ServerSocket(port);
        Socket socket = null;
        log("Socks server port : %s listenning...", port);
        while (null != (socket = ss.accept())) {
            new Thread(new SocksServerOneThread(socket, openSock4, openSock5, user, pwd)).start();
        }
        ss.close();
    }
 
    public static void main(String[] args) throws IOException {
        java.security.Security.setProperty("networkaddress.cache.ttl", "86400");
        log("\n\tUSing port openSock4 openSock5 user pwd");
        int port = 1080;
        boolean openSock4 = true;
        boolean openSock5 = true;
        String user = null, pwd = null;
        // user = "user";
        pwd = "123456";
        int i = 0;
        if (args.length > i && null != args[i++]) {
            port = Integer.valueOf(args[i].trim());
        }
        if (args.length > i && null != args[i++]) {
            openSock4 = Boolean.valueOf(args[i].trim());
        }
        if (args.length > i && null != args[i++]) {
            openSock5 = Boolean.valueOf(args[i].trim());
        }
        if (args.length > i && null != args[i++]) {
            user = args[i].trim();
        }
        if (args.length > i && null != args[i++]) {
            pwd = args[i].trim();
        }
        SocksServerOneThread.startServer(port, openSock4, openSock5, user, pwd);
    }
}