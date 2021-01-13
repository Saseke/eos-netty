package com.songmengyuan.eos.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int len = in.readInt();
		byte[] content = new byte[len];
		in.readBytes(content);
		Message message = new Message(len, content);
		out.add(message);
	}

}
