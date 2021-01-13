package com.songmengyuan.eos.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class EosHeartBeatHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			Channel curChannel = ctx.channel();
			switch (event.state()) {
			case READER_IDLE:
				System.out.println("用户: " + curChannel.remoteAddress() + "一分钟未发送数据 ");
				break;
			case WRITER_IDLE:
				System.out.println("用户: " + curChannel.remoteAddress() + "1分钟未读取服务器信息");
				break;
			case ALL_IDLE:
				System.out.println("用户: " + curChannel.remoteAddress() + "10分钟未与服务器取得联系，连接将会自动断开");
				curChannel.close();
				break;
			}
		}

		super.userEventTriggered(ctx, evt);
	}

}
