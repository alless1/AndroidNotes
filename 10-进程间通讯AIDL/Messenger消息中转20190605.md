### Messenger消息中转1.1 ###

#### 实现功能 ####

1.服务端一对多绑定客户端。

2.客户端，订阅事件，取消订阅。

3.客户端一对一发送请求，获取响应，有超时。

4.客户端一对多发送广播，无响应，无超时。


#### 服务端使用  ####


1.开启服务

	startService(new Intent(this, MessengerService.class));


#### 客户端使用 ####

1.绑定服务

	mClient1 = new MessengerClient(this);//初始化完整类名作为标识
	mClient1.bindService(context);

2.设置消息监听（根据订阅的事件类型，收到消息）

    mClient1.setOnHandleMessageListener(new OnHandleMessageListener() {
        @Override
        public void onHandleRequest(SampleMessage smg) {
			//收到其他客户端请求，必须回复
            smg.setMsgBody(smg.getMsgBody()+" - 回复");
            mClient1.sendResponse(smg);
          
        }

        @Override
        public void onBroadCast(SampleMessage smg) {
            //收到广播消息，无需回复
           
        }
    });

3.订阅事件

	mClient1.subscribe(msgTypeString);

4.发送请求消息（一对一，有返回，有超时）

    mClient1.sendRequest(msgType, msgBody, new MsgRequestCallback() {
        @Override
        public void onSuccess(SampleMessage smg) {
            Log.e(TAG, "onSuccess: "+smg );
            //返回结果
        }

        @Override
        public void onFail(int code, String msg) {
            Log.e(TAG, "onFail: code= "+code +" msg = "+msg );
            //无对应消息类型处理客户端，处理响应超时
        }
    });

5.发送广播消息（一对多，无返回，无超时）

	mClient1.sendBroadCast(msgType,msgBody);