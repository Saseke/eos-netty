package com.songmengyuan.eos.protocol;

public class Message {

	private int len;

	private byte[] content;

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	Message() {
	}

	public Message(int len, byte[] content) {
		this.len = len;
		this.content = content;
	}

}
