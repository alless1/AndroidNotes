### netty使用 ### 
jar包 netty-3.6.6.Final.jar

1.初始化Bootstrap

	 channelFactory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(),Executors.newSingleThreadExecutor());
	        clientBootstrap = new ClientBootstrap(channelFactory);
	        clientBootstrap.setOption("connectTimeoutMillis", 5000);
	        clientBootstrap.setOption("child.receiveBufferSize", 1024 * 1024);
	        clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	            public ChannelPipeline getPipeline() throws Exception {
	                ChannelPipeline pipeline = Channels.pipeline();
	                pipeline.addLast("encoder", new PacketEncoderHandler());//将数据转换成二进制流的handler。
	                pipeline.addLast("handler", handler);//发送和接收消息时都会走的一个管道。
	                return pipeline;
	            }
	        });
	        clientBootstrap.setOption("tcpNoDelay", true);
	        clientBootstrap.setOption("keepAlive", true);

2.连接socket

	 		channelFuture = clientBootstrap.connect(new InetSocketAddress(
	
	                        strHost, nPort));
	
	                // Wait until the connection attempt succeeds or fails.
	
	                channel = channelFuture.awaitUninterruptibly().getChannel();//这里应该会阻塞的吧。
	
	                if (!channelFuture.isSuccess()) {
	
	                    channelFuture.getCause().printStackTrace();
	
	                    clientBootstrap.releaseExternalResources();
	
	                    return false;
	
	                }
	 			channelFuture.getChannel().getCloseFuture().awaitUninterruptibly();
	
	            // Shut down thread pools to exit.
	
	            clientBootstrap.releaseExternalResources();

阻塞

	        printTime("开始connect： ");
	        // Start the connection attempt.
	        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
	
	        // Wait until the connection is closed or the connection attempt fails.
	        future.getChannel().getCloseFuture().awaitUninterruptibly();
	
	        printTime("connect结束： ");
	        // Shut down thread pools to exit.
	        bootstrap.releaseExternalResources();

非阻塞

		    printTime("开始connect： ");
	        // Start the connection attempt.
	        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
	
	        future.addListener(new ChannelFutureListener()
	        {
	            public void operationComplete(final ChannelFuture future)
	                throws Exception
	            {
	                printTime("connect结束： ");
	            }
	        });
	
	        printTime("异步时间： ");
	
	        // Shut down thread pools to exit.
	        bootstrap.releaseExternalResources();


jar包 netty-4.1.4.Final.jar

1.初始化

	 mWorkerGroup = new NioEventLoopGroup();
        try {
            mBootstrap = new Bootstrap();
            mBootstrap.group(mWorkerGroup);
            mBootstrap.channel(NioSocketChannel.class);
            mBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            mBootstrap.option(ChannelOption.TCP_NODELAY, true);
            mBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new PacketEncoderHandler());
                    socketChannel.pipeline().addLast(handler);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            mWorkerGroup.shutdownGracefully();
        }

2.连接

        try {
            //启动客户端
            mChannelFuture = mBootstrap.connect(strHost, nPort).sync();//这里会阻塞，连接成功进入下一步。失败，跳到catch,finally。
            //等待连接关闭
            mChannelFuture.channel().closeFuture().sync();//这里也会阻塞。
        } catch (Exception e) {
            Logger.e("SocketThread#run():", e.getMessage());
        } finally {
            mWorkerGroup.shutdownGracefully();
        }

详细代码见nettydemo。