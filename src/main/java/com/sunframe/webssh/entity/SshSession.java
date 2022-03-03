package com.sunframe.webssh.entity;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;
import java.io.OutputStream;

public class SshSession {

	private WebSocketSession socketSession;

	private Session sshSession;

	private ChannelShell channelShell;

	private InputStream inputStream;

	private OutputStream outputStream;

	private Thread readThread = null;

	public SshSession(WebSocketSession socketSession, Session sshSession, ChannelShell channelShell, InputStream inputStream, OutputStream outputStream, Thread readThread) {
		this.socketSession = socketSession;
		this.sshSession = sshSession;
		this.channelShell = channelShell;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.readThread = readThread;
	}

	public WebSocketSession getSocketSession() {
		return socketSession;
	}

	public void setSocketSession(WebSocketSession socketSession) {
		this.socketSession = socketSession;
	}

	public Session getSshSession() {
		return sshSession;
	}

	public void setSshSession(Session sshSession) {
		this.sshSession = sshSession;
	}

	public ChannelShell getChannelShell() {
		return channelShell;
	}

	public void setChannelShell(ChannelShell channelShell) {
		this.channelShell = channelShell;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Thread getReadThread() {
		return readThread;
	}

	public void setReadThread(Thread readThread) {
		this.readThread = readThread;
	}
}
