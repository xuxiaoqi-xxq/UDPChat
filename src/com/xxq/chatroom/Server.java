package com.xxq.chatroom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器
 * 
 * @author xu
 *
 */
public class Server {
	// TCP socket
	ServerSocket serverSocket;

	// 线程池（并发编程）
	ExecutorService pool;

	/** 所有用户信息表 记录客户端的信息 端口，昵称 */
	List<User> users = new ArrayList<>();

	public Server() {
		pool = Executors.newCachedThreadPool();
	}

	/**
	 * 服务端启动,等待接收数据
	 */
	public void start() {
		try {
			serverSocket = new ServerSocket(9000);
			System.out.println("服务器启动...");
			while (true) {
				// 创建连接
				Socket socket = serverSocket.accept();
				// 执行任务
				pool.execute(new OnlineServer(socket, users));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
}
