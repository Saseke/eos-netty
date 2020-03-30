package com.songmengyuan.eos.client;

public class EosClientBootstrap {

	public static void main(String[] args) throws InterruptedException {
        EosClient client = new EosClient("127.0.0.1",9000);
        client.start();
	}
}
