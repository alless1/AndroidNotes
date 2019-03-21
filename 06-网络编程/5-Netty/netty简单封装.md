### 一、使用方式 ###
#### 1.初始化解析头文件 ####
	//初始化解析头文件
	MessageManager.instance().initDecodeHead(Header.class);
#### 2.连接socket ####
        MessageSocket messageSocket = new MessageSocket("192.168.1.9", "3333", new OnSocketConnectListener() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                Log.e(TAG, "onChannelActive: ");
                //组包
                DataBuffer buffer = PacketManager.instance().packUserLogin("lizebo", Md5Utils.getMd5("88888"));
                //监听
                MessageManager.instance().setOnRespListenerByMsg(ProtocolMsg.AUT_RSP_LOGIN, mPacketListener);
                //发包
                ctx.writeAndFlush(buffer.getOrignalBuffer());
            }

            @Override
            public void onChannelUnregistered(ChannelHandlerContext ctx) {
                Log.e(TAG, "onChannelUnregistered: ");
            }

            @Override
            public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                Log.e(TAG, "onExceptionCaught: " + cause.getMessage());
            }

            @Override
            public void onUserEventTriggered(ChannelHandlerContext ctx, Object evt) {
                Log.e(TAG, "onUserEventTriggered: ");
            }
        });

#### 3.保存socket ####

###  ###