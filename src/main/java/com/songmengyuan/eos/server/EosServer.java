package com.songmengyuan.eos.server;

import com.songmengyuan.eos.protocol.MessageDecoder;
import com.songmengyuan.eos.protocol.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class EosServer {

	private int port;

	private SimpleDateFormat sdf;

	public EosServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		// boss设置1个EventLoop
		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup workers = new NioEventLoopGroup();
		// 设置启动引导
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(boss, workers).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128).handler(new LoggingHandler(LogLevel.INFO))
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("decoder", new MessageDecoder());
							pipeline.addLast("encoder", new MessageEncoder());
							pipeline.addLast(new IdleStateHandler(60, 60, 600, TimeUnit.SECONDS));
							pipeline.addLast(new EosHeartBeatHandler());
							pipeline.addLast("serviceHandler", new EosServerServiceHandler());
						}
					});
			// 绑定端口
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			System.out.println("服务器启动");
			channelFuture.channel().closeFuture().sync();
		}
		finally {
			boss.shutdownGracefully();
			workers.shutdownGracefully();
		}
	}

}
