package com.sunframe.webssh.handler;


import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SocketHandler extends TextWebSocketHandler {

	public static Logger logger = LoggerFactory.getLogger(SocketHandler.class);


	@Override
	public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception{
		logger.info("Create New WebSocketSession -->{}",socketSession.getId());
	}

	/**
	 * on socketMessage
	 */
	@Override
	public void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {
		JSONObject recvMsg = (JSONObject) JSONObject.parse(message.getPayload());
		//logger.info("ReceiveMsg --> {}", recvMsg.get("data"));
		if (recvMsg.containsKey("init")) {
			logger.info("ConnectInfo --> {}", recvMsg.get("init"));
			JSONObject loginData = (JSONObject) recvMsg.get("init");
			try {
				String hostIp = loginData.get("hostname").toString();
				String userName = loginData.get("username").toString();
				String password = loginData.get("password").toString();
				String port =  loginData.get("port").toString();
				Session sshSession = SshHandler.getSshSession(hostIp, userName, password, Integer.valueOf(port));
				SshHandler.CreateExec(socketSession.getId(), socketSession, sshSession);
			} catch (JSchException e) {
				socketSession.sendMessage(new TextMessage(e.getMessage() + "\n"));
				e.printStackTrace();
			}
			logger.info("Current WebSocketSession -->{}",SshHandler.getSshSessionCount());
		}
		if (recvMsg.containsKey("data")) {
			OutputStream writeStream;
			try {
				writeStream = SshHandler.getwriteStream(socketSession.getId());
				//writeStream.write("ls \n" .getBytes());
				writeStream.write(recvMsg.get("data").toString().getBytes());
				//writeStream.write(message.getPayload().getBytes());
				writeStream.flush();
			} catch (JSchException e) {
				try {
					socketSession.sendMessage(new TextMessage(e.getMessage() + "\n"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("Close WebSocketSession -->{}",session.getId());
		SshHandler.removeSshSession(session.getId());
		logger.info("Current WebSocketSession -->{}",SshHandler.getSshSessionCount());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		super.handleTransportError(session, exception);
		SshHandler.removeSshSession(session.getId());
	}


}
