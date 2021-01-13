package com.songmengyuan.eos.client;

import com.songmengyuan.eos.protocol.Message;
import com.songmengyuan.eos.protocol.MessageDecoder;
import com.songmengyuan.eos.protocol.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EosClient {

	private final String host;

	private final int port;

	public EosClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		try {

			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("decoder", new MessageDecoder());
					pipeline.addLast("encoder", new MessageEncoder());
					pipeline.addLast("clientService", new EosClientServiceHandler());
				}
			});
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			System.out.println("登陆成功");
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNext()) {
				byte[] bytes = scanner.nextLine().getBytes(StandardCharsets.UTF_8);
				Message message = new Message(bytes.length, bytes);
				channelFuture.channel().writeAndFlush(message);
			}

		}
		finally {
			group.shutdownGracefully();
		}
	}

}
