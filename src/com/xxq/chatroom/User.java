package com.xxq.chatroom;

import java.util.Random;

/**
 * 用户信息表
 * 
 * @author xu
 *
 */
public class User {

	/** 用户名 */
	private String nickname;

	/** 用户当前使用UDP端口号，可更改 */
	private int port;

	/** 用户是否在线 */
	private boolean online;

	/** 用户ID,ID唯一，用户不可更改 */
	private final String id = String.valueOf(new Random().nextInt(99999));
	
	public User() {
	}

	public User(String nickname) {
		this.nickname = nickname;
		online = true;
	}

	public User(String nickname, int port, boolean isOnline) {
		this.nickname = nickname;
		this.port = port;
		this.online = isOnline;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean isOnline) {
		this.online = isOnline;
	}
	
	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		if (online) {
			return nickname + ":online";
		}
		return nickname + ":offline";
	}
}
