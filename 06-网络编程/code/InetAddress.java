package com.imooc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/*
 * InetAddress��
 */
public class Test01 {

	public static void main(String[] args) throws UnknownHostException {
		// ��ȡ������InetAddressʵ��
		InetAddress address = InetAddress.getLocalHost();
		System.out.println("��������" + address.getHostName());
		System.out.println("IP��ַ��" + address.getHostAddress());
		byte[] bytes = address.getAddress();// ��ȡ�ֽ�������ʽ��IP��ַ
		System.out.println("�ֽ�������ʽ��IP��" + Arrays.toString(bytes));
		System.out.println(address);// ֱ�����InetAddress����

		// ���ݻ�������ȡInetAddressʵ��
		// InetAddress address2=InetAddress.getByName("laurenyang");
		InetAddress address2 = InetAddress.getByName("1.1.1.10");
		System.out.println("��������" + address2.getHostName());
		System.out.println("IP��ַ��" + address2.getHostAddress());
	}

}
