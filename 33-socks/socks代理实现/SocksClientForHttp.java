package com.sdk.speed.traffic.socks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * 
 * 入口：传入参数socks5ip=xxxx socks5port=xxxx url=xxxx<br/>
 * url必须是域名，测试远程能否解析域名，例如：www.baidu.com/abc
 * 
 * @author qiuxy
 * 
 */
public class SocksClientForHttp {

	private String socks5Ip;
	private int socks5port;
	private String targetUrl;

	private String domain;

	public SocksClientForHttp(String socks5Ip, int socks5port, String targetUrl) {
		this.socks5Ip = socks5Ip;
		this.socks5port = socks5port;
		this.targetUrl = targetUrl;
		this.domain = this.targetUrl.split("/")[0];
		System.out.println("domain is :" + this.domain);
	}


	/**
	 * 协商认证方式
	 * 
	 * 客户端发送（以下数字代表字节数）：
                   +----+----------+----------+
                   |VER | NMETHODS | METHODS  |
                   +----+----------+----------+
                   | 1  |    1     | 1 to 255 |
                   +----+----------+----------+
		对于SOCKS 5，VER字段为0x05，版本4对应0x04
		NMETHODS字段指定METHODS域的字节 
		目前可用METHOD值有:
			0x00 NO AUTHENTICATION REQUIRED(无需认证) 
			0x01 GSSAPI 
			0x02 USERNAME/PASSWORD(用户名/口令认证机制) 
			0x03-0x7F IANA ASSIGNED 
			0x80-0xFE RESERVED FOR PRIVATE METHODS(私有认证机制) 
			0xFF NO ACCEPTABLE METHODS(完全不兼容) 
			
	服务端响应：
                         +----+--------+
                         |VER | METHOD |
                         +----+--------+
                         | 1  |   1    |
                         +----+--------+

	 * @param is
	 * @param os
	 * @throws IOException
	 */
	private void chooseAuthentication(InputStream is, OutputStream os) throws IOException {
		byte[] req = new byte[] { 0x05, 0x02, 0x00, 0x01 };
		os.write(req);
		os.flush();
		int version = is.read();
		is.read();
	}

	/**
	 * 发送需要代理去的目标地址

   The SOCKS request is formed as follows:

        +----+-----+-------+------+----------+----------+
        |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
        +----+-----+-------+------+----------+----------+
        | 1  |  1  | X'00' |  1   | Variable |    2     |
        +----+-----+-------+------+----------+----------+

     Where:

          o  VER    protocol version: X'05'	表示socks代理版本
          o  CMD
             o  CONNECT X'01'	表示需要连接到外面
             o  BIND X'02'
             o  UDP ASSOCIATE X'03'
          o  RSV    RESERVED	预留，写死0x00即可
          o  ATYP   address type of following address
             o  IP V4 address: X'01'	如果是ipv4，则DST.ADDR用4个字节表示
             o  DOMAINNAME: X'03'	如果是域名，则DST.ADDR第一个字节表示域名长度，后面加上域名值
             o  IP V6 address: X'04'
          o  DST.ADDR       desired destination address	地址值
          o  DST.PORT desired destination port in network octet	两个字节的端口值
             order
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	private void sendTargetAddr(InputStream is, OutputStream os) throws IOException {

		// 传域名的方式
		int domainLen = domain.length();
		byte[] connReq = new byte[] { 0x05, 0x01, 0x00, 0x03 };
		os.write(connReq);
		os.write((byte) domainLen);
		os.write(domain.getBytes());

		// 传ipv4的方式
//		byte[] connReq = new byte[] { 0x05, 0x01, 0x00, 0x01 };
//		os.write(connReq);
//		byte[] hostIp = InetAddress.getByName(domain).getAddress();
//		os.write(hostIp);

		short port = (short) 80;
		byte[] portBytes = new byte[2];
		portBytes[0] = (byte) (port >> 8);
		portBytes[1] = (byte) (port & 0x00ff);
		os.write(portBytes);
		os.flush();
	}

	/**
	 * 获取发送目标地址后，返回的响应

        +----+-----+-------+------+----------+----------+
        |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
        +----+-----+-------+------+----------+----------+
        | 1  |  1  | X'00' |  1   | Variable |    2     |
        +----+-----+-------+------+----------+----------+

     Where:

          o  VER    protocol version: X'05'
          o  REP    Reply field:
             o  X'00' succeeded如果那边是连接远端目标地址成功，则这里返回的是0x00
             o  X'01' general SOCKS server failure
             o  X'02' connection not allowed by ruleset
             o  X'03' Network unreachable
             o  X'04' Host unreachable
             o  X'05' Connection refused
             o  X'06' TTL expired
             o  X'07' Command not supported
             o  X'08' Address type not supported
             o  X'09' to X'FF' unassigned
          o  RSV    RESERVED
          o  ATYP   address type of following address
	 * @param is
	 * @throws IOException
	 */
	private void getReply(InputStream is) throws IOException {
		byte[] connRep = new byte[4];
		is.read(connRep);
		System.out.println("reponse socks version is " + connRep[0]);
		if (connRep[1] == 0x00) {
			System.out.println("reply is success");
		} else {
			System.out.println("Reply failed! code: " + connRep[1]);
		}
		System.out.println("address type is " + connRep[3]);
		if (connRep[3] == 0x03) {
			System.out.println("address type is domain");
		} else if (connRep[3] == 0x01) {
			System.out.println("address type is ipv4");
		} else if (connRep[3] == 0x04) {
			System.out.println("address type is ipv6");
		}

		byte[] ipbytes = new byte[4];
		is.read(ipbytes);
		printIp(ipbytes);

		byte[] portbytes = new byte[2];
		is.read(portbytes);
		printPort(portbytes);
	}

	/**
	 * 发送http请求到代理，并获取代理的返回值
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	private void sendHttpReq(InputStream is, OutputStream os) throws IOException {
		os.write("GET / HTTP/1.1\r\n".getBytes());
		os.write(("Host: " + domain + "\r\n\r\n").getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

	}

	public void connect() throws UnknownHostException, IOException {
		Socket s = new Socket(socks5Ip, socks5port);
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		
		chooseAuthentication(is, os);
		sendTargetAddr(is, os);
		getReply(is);
		sendHttpReq(is, os);

		is.close();
		os.close();
	}

	private void printIp(byte[] ipbytes) {
		for (byte b : ipbytes) {
			System.out.print(b + ".");
		}
		System.out.println();
	}

	private void printPort(byte[] portbytes) {
		short port = (short) ((portbytes[0] << 8) | (portbytes[1]));
		System.out.println(port);
	}
}
