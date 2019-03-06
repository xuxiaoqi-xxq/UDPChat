package com.xxq.chatroom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 接收消息任务
 * 
 * @author xu
 *
 */
public class ReciveTask implements Runnable {

	/** UDP socket */
	private DatagramSocket socket;

	public ReciveTask(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		DatagramPacket packet = null;
		String msg = null;

		/** 接收消息 */
		try {
			do {
				byte[] buf = new byte[1024 * 1];
				// 数据包，存放所有发送到端口的数据
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				byte[] data = packet.getData();
				msg = new String(data, 0, packet.getLength());
				String senderName = getSenderName(msg);
				msg = getMessage(msg);
				System.out.printf("##%s发来消息:%s\n", senderName, msg);
			} while (true);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取发送者用户名
	 * 
	 * @param msg
	 * @return
	 */
	private String getSenderName(String msg) {
		int index = msg.indexOf(":");
		return msg.substring(0, index);
	}

	/**
	 * 获取接收的信息
	 * 
	 * @param msg
	 * @return
	 */
	private String getMessage(String msg) {
		int index = msg.indexOf(":");
		return msg.substring(index + 2, msg.length());
	}
}
