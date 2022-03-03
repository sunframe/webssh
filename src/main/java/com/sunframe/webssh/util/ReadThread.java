package com.sunframe.webssh.util;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;

public class ReadThread implements Runnable {

	private WebSocketSession socketSession;
	private InputStream inputStream;

	public ReadThread(InputStream inputStream, WebSocketSession socketSession) {
		this.inputStream = inputStream;
		this.socketSession = socketSession;
	}


	@Override
	public void run() {
		byte[] tmp = new byte[1024];
		try {
			while(true) {
				while(inputStream.available() > 0) {
					int len = inputStream.read(tmp, 0, 1024);
					if (len < 0 ) {
						break;
					}
					String msg = new String(tmp, 0, len, "utf-8");
					//System.out.println(new Date().getTime());
					//System.out.println(s);
					socketSession.sendMessage(new TextMessage(msg));
				}
				if (!socketSession.isOpen()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
