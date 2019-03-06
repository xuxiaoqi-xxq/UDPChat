package com.xxq.chatroom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.*;

/**
 * 客户端服务
 * 
 * @author xu
 *
 */
public class ClientServer implements Runnable {

	DatagramSocket udpSocket;
	Socket tcpSocket;
	// 用户表，包含用户信息与用户当前使用的UDP端口
	List<User> users;
	// 当前用户
	User user;
	// 用户名
	String nickname;

	public ClientServer(Socket tcpSocket) {
		this.tcpSocket = tcpSocket;
	}

	@Override
	public void run() {
		try (InputStream in = tcpSocket.getInputStream(); OutputStream out = tcpSocket.getOutputStream()) {
			// 用户上线
			login(out);
			users = getUser(in);
			printUsers(users);

			/** 创建线程，执行聊天程序 */
			SendTask sendTask = new SendTask(udpSocket, nickname);
			ReciveTask reciveTask = new ReciveTask(udpSocket);
			sendTask.setUsers(users);
			new Thread(sendTask).start();
			new Thread(reciveTask).start();

			/** 接收当前在线用户列表 */
			while (true) {
				//if (sendTask.getState()) {
					users = getUser(in);
					printUsers(users);
					sendTask.setUsers(users);
					Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户上线
	 * 
	 * @param out
	 */
	private void login(OutputStream out) {
		System.out.println("-----登录-----");
		System.out.println("$$用户名:");
		Scanner read = new Scanner(System.in);
		nickname = read.nextLine();
		try {
			udpSocket = new DatagramSocket();
			int targetPort = udpSocket.getLocalPort();// 用户当前使用的UDP端口号
			user = new User(nickname, targetPort, true);
			String json = JSON.toJSONString(user);
			// 上传用户信息至服务器
			out.write(json.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("----登录成功----");
	}

	/**
	 * 用户下线
	 * 
	 * @param out
	 */
	private void logout(OutputStream out) {
		user.setOnline(false);
		String json = new Gson().toJson(user);
		try {
			out.write(json.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("##" + nickname + "下线了");
	}

	/**
	 * 获取当前在线用户
	 * 
	 * 使用Gson反序列化时出现错误：List<User> users = new Gson().fromJson(json, List.class);
	 * 编译错误:java.lang.ClassCastException: com.google.gson.internal.LinkedTreeMap
	 * cannot be cast to com.xxq.chatroom.User, 原因:编译时泛型擦除导致
	 * 
	 * 解决方法之一： List<User> users = new ArrayList<>(); JsonArray array = new
	 * JsonParser().parse(json).getAsJsonArray(); for (final JsonElement e : array){
	 * users.add(new Gson().fromJson(e, User.class)); } 方法可行，但不如使用的方法高效简洁
	 * 
	 * @param in
	 * @return
	 */
	private List<User> getUser(InputStream in) {
		byte[] buf = new byte[1024 * 32];
		int size = 0;
		try {
			size = in.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = new String(buf, 0, size);
		// json反序列化为对象
		List<User> users = new Gson().fromJson(json, new TypeToken<List<User>>() {
		}.getType());
		return users;
	}

	/**
	 * 打印当前在线用户
	 * 
	 * @param users
	 */
	private void printUsers(List<User> users) {
		StringBuilder builder = new StringBuilder();
		for (User u : users) {
			builder.append(u.getNickname() + ",");
		}
		builder.deleteCharAt(builder.lastIndexOf(","));
		System.out.println("##当前用户:[" + builder.toString() + "]");
	}
}
