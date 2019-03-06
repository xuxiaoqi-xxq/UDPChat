package com.xxq.chatroom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 客户端
 * 
 * @author xu
 *
 */
public class Client {

	Socket tcpSocket;
	ExecutorService pool;

	public Client() {
		pool = Executors.newCachedThreadPool();
	}

	/**
	 * 客户端启动
	 */
	public void start() {
		try {
			// 连接服务器
			tcpSocket = new Socket(InetAddress.getByName("127.0.0.1"), 9000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pool.execute(new ClientServer(tcpSocket));
	}

	public static void main(String[] args) {
		Client chat = new Client();
		chat.start();
	}
}
