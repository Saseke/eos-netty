package com.songmengyuan.eos.server;

import com.songmengyuan.eos.protocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EosServerServiceHandler extends SimpleChannelInboundHandler<Message> {

	// 用channelGroup来维护所有注册的channel
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private final InternalLogger logger = InternalLoggerFactory.getInstance(getClass());

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String msg = sdf.format(new Date()) + "   用户 : " + channel.remoteAddress() + "  上线\n";
		System.out.println(msg);
		// 发送给其他用户
		byte[] content = msg.getBytes(StandardCharsets.UTF_8);
		Message message = new Message(content.length, content);
		channelGroup.writeAndFlush(message);
		// 将当前的channel加入channelGroup中
		channelGroup.add(channel);
	}

	// 自动会把当前的channel从group中移除
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String msg = sdf.format(new Date()) + "   用户: " + channel.remoteAddress() + " 下线\n";
		byte[] content = msg.getBytes(StandardCharsets.UTF_8);
		Message message = new Message(content.length, content);
		channelGroup.writeAndFlush(message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		String content = new String(msg.getContent(), StandardCharsets.UTF_8);
		Channel channel = ctx.channel();
		String message1 = "用户: " + channel.remoteAddress() + " 发送了： " + content;
		System.out.println(message1);
		byte[] messageByte1 = message1.getBytes(StandardCharsets.UTF_8);
		// 发送给除自己之外的其他用户
		channelGroup.forEach(c -> {
			if (c == channel) { // 如果当前channel是自己的话
				String s = sdf.format(new Date()) + "[自己]  :" + content;
				byte[] messageByte2 = s.getBytes(StandardCharsets.UTF_8);
				Message message = new Message(messageByte2.length, messageByte2);
				c.writeAndFlush(message);
			}
			else {
				Message message = new Message(messageByte1.length, messageByte1);
				c.writeAndFlush(message);
			}
		});
	}

}
