package com.songmengyuan.eos.client;

import com.songmengyuan.eos.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class EosClientServiceHandler extends SimpleChannelInboundHandler<Message> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
		byte[] content = msg.getContent();
		String str = new String(content, CharsetUtil.UTF_8);
		System.out.println(str);
	}

}
