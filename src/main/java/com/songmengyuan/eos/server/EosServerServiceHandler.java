package com.songmengyuan.eos.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EosServerServiceHandler extends SimpleChannelInboundHandler<String> {

	// 用channelGroup来维护所有注册的channel
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd-hh:mm:ss");

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String msg = sdf.format(new Date()) + "   用户 : " + channel.remoteAddress() + "  上线\n";
		System.out.println(msg);
		// 发送给其他用户
		channelGroup.writeAndFlush(msg);
		// 将当前的channel加入channelGroup中
		channelGroup.add(channel);
	}

	// 自动会把当前的channel从group中移除
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String msg = sdf.format(new Date()) + "   用户: " + channel.remoteAddress() + " 下线\n";
		channelGroup.writeAndFlush(msg);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) {
		Channel channel = ctx.channel();
		String message = "用户: " + channel.remoteAddress() + " 发送了： " + msg;
		System.out.println(message);
		// 发送给除自己之外的其他用户
		channelGroup.forEach(c -> {
			if (c == channel) { // 如果当前channel是自己的话
				String s = sdf.format(new Date()) + "[自己]  :" + msg;
				c.writeAndFlush(s);
			}
			else {
				c.writeAndFlush(message);
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
