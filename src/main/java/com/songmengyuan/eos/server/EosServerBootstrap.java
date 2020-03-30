package com.songmengyuan.eos.server;

public class EosServerBootstrap {

	public static void main(String[] args) throws Exception {
		EosServer server = new EosServer(9000);
		server.start();
	}

}
