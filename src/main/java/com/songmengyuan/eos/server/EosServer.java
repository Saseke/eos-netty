package com.songmengyuan.eos.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.text.SimpleDateFormat;

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
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("decoder", new StringDecoder());
							pipeline.addLast("encoder", new StringEncoder());
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
