package com.xxq.chatroom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 用户上线服务
 * 
 * @author xu
 *
 */
public class OnlineServer implements Runnable {

	Socket socket;

	/** 所有用户信息表 */
	List<User> users;

	/** 上一个用户 */
	User previousUser = null;

	/** 所有用户数量 */
	int count = 0;

	public OnlineServer(Socket socket, List<User> users) {
		super();
		this.socket = socket;
		this.users = users;
	}

	@Override
	public void run() {
		try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
			// 从inputstream中获得User
			User user = getUser(in);
			// 存储
			users = store(user, users);
			// 给客户端发送信息
			sendMsgToClient(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送用户信息表
	 * 
	 * @param in
	 * 
	 * @param out
	 * @param user
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void sendMsgToClient(InputStream in, OutputStream out) {
		// 用户信息表发送给客户端 users --> XML/JSON
		while (true) {
			if (count != users.size()) {
				count = users.size();
				String json = JSON.toJSONString(users);
				printUsers();
				try {
					out.write(json.getBytes());
					out.flush();
					Thread.sleep(10000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	/**
	 * 从socket的inputstream中获得User
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private User getUser(InputStream in) {
		byte[] buf = new byte[1024];
		int size = 0;
		try {
			size = in.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String userJson = new String(buf, 0, size);
		User user = JSON.parseObject(userJson, User.class);
		return user;
	}

	/**
	 * 若某表中用户不存在，则存储，否则替换
	 * 
	 * @param user
	 * @param previousUser
	 */
	private List<User> store(User user, List<User> us) {
		previousUser = user;
		if (!isExist(user, us)) {
			us.add(user);
		} else {
			us = replace(user, us);
		}
		return us;
	}

	/**
	 * 替换某一表中用户名相同的用户
	 * 
	 * @param us
	 * @param newUser
	 */
	private List<User> replace(User newUser, List<User> us) {
		int index = -1;
		String name = previousUser.getNickname();
		for (int i = 0; i < us.size(); i++) {
			if (us.get(i).getNickname().equals(name)) {
				index = i;
			}
		}
		if (index >= 0) {
			us.remove(index);
			us.add(newUser);
		}
		return us;
	}

	/**
	 * 判断某一表中是否存在某一用户
	 * 
	 * @param user 用户
	 * @param us
	 * @return
	 */
	private boolean isExist(User user, List<User> us) {
		if (us.isEmpty()) {
			return false;
		}
		String name = user.getNickname();
		for (User u : us) {
			if (u.getNickname().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 打印
	 */
	private void printUsers() {
		StringBuilder builder = new StringBuilder("[");
		if (!users.isEmpty()) {
			for (User u : users) {
				builder.append(u.getNickname() + ",");
			}
		} 
		int index = builder.lastIndexOf(",");
		if(index != -1) {
			builder.deleteCharAt(index);
		}
		System.out.println("##当前在线用户:" + builder.toString() + "]");
	}
}
