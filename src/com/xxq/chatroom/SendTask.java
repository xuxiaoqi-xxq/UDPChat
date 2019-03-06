package com.xxq.chatroom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * 发送消息任务
 * 
 * @author xu
 *
 */
public class SendTask implements Runnable {

	DatagramSocket socket;

	List<User> users;

	User user;

	// 发送方用户名
	String myname;

	public SendTask(DatagramSocket socket, String myname) {
		super();
		this.socket = socket;
		this.myname = myname;
	}

	@Override
	public void run() {
		/** 选择联系人 */
		Scanner reader = new Scanner(System.in);
		System.out.print("$$选择好友：");
		String nickname = reader.nextLine();

		/** 若目标用户存在则发送消息，否则重新选择目标用户 */
		if (exists(nickname)) {
			// 获取端口号
			int targetPort = user.getPort();
			// 若目标用户在线，则发送消息至目标用户,否则重新选择目标用户
			if (user.isOnline()) {
				sendMessage(reader, nickname, targetPort);
			} else {
				System.out.println("##该用户不在线，无法发送消息！");
				System.out.print("$$选择好友：");
				nickname = reader.nextLine();
				sendMessage(reader, nickname, targetPort);
			}
		} else {
			System.out.println("##该用户不存在，无法发送消息！");
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param reader
	 * @param nickname
	 * @param targetPort
	 */
	private void sendMessage(Scanner reader, String nickname, int targetPort) {
		String msg = null;
		DatagramPacket packet = null;
		byte[] data = null;
		do {
			System.out.print("$$" + myname + "->" + nickname + ":");
			// 从控制台发送消息,在消息头加上发送者昵称，以两个冒号做分隔符
			msg = reader.nextLine();
			data = (myname + "::" + msg).getBytes();
			try {
				packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), targetPort);
				socket.send(packet);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!msg.equalsIgnoreCase("bye"));
	}

	/**
	 * 通过用户名判断该用户是否存在
	 * 
	 * @param nickname
	 * @return
	 */
	private boolean exists(String nickname) {
		for (User u : users) {
			if (u.getNickname().equals(nickname)) {
				user = u;
				return true;
			}
		}
		return false;
	}

	/**
	 * 将当前在线用户列表传入
	 * 
	 * @param users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

}
