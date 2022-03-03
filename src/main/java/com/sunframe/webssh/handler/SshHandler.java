package com.sunframe.webssh.handler;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sunframe.webssh.entity.SshSession;
import com.sunframe.webssh.util.ReadThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SshHandler {

    private static final Logger logger = LoggerFactory.getLogger(SshHandler.class);

    private static final int DEFAULT_SSH_PORT = 22;

    /**
     * sshSession map
     */
    private static final ConcurrentMap<String, SshSession> sshSessionMap = new ConcurrentHashMap();

    /**
     * get sshsession count
     */
    public static int getSshSessionCount() {
        return sshSessionMap.size();
    }


    /**
     *  Get ssh session.
     */
    public static Session getSshSession(String sshHost, String sshUser, String sshPass, int sshPort) throws JSchException {

        if (sshHost == null || sshPort < 0 || sshUser == null || sshPass == null) {
            return null;
        }
        Session sshSession;
        sshSession = new JSch().getSession(sshUser, sshHost, sshPort);
        sshSession.setPassword(sshPass);
        sshSession.setConfig("StrictHostKeyChecking", "no");
        //sshSession.connect(timeout);
        sshSession.connect();

        return sshSession;
    }
    /**
     *  get channel
     */
    public static ChannelShell getChannelShell (Session session) throws JSchException {
        return (ChannelShell) session.openChannel("shell");
    }

    /**
     *  create remote ssh
     */
    public static void CreateExec(String sessionId, WebSocketSession socketSession, Session sshSession) throws JSchException, IOException{

        ChannelShell channelShell = getChannelShell(sshSession);
        // 设置 agent
        channelShell.setAgentForwarding(true);
        channelShell.setPty(true);
        InputStream inputStream = channelShell.getInputStream();
        OutputStream outputStream = channelShell.getOutputStream();
        channelShell.connect();
        // thread sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Thread thread = new Thread(new ReadThread(inputStream, socketSession), String.format("ReadThread_%s", sessionId));
        thread.start();

        SshSession sessionPool=  new SshSession(socketSession,sshSession,channelShell,inputStream,outputStream, thread);
        sshSessionMap.put(sessionId,sessionPool);
    }

    /**
     *  get OutputStream
     */
    public static OutputStream getwriteStream(String sessionId) throws JSchException, IOException {
        OutputStream out = null;
        if (sshSessionMap.containsKey(sessionId)) {
            SshSession sshSession = sshSessionMap.get(sessionId);
            if (sshSession.getChannelShell().isClosed()) {
                removeSshSession(sessionId);
                throw new JSchException("channel is closed , confirm host is online please");
            }
            if (sshSession.getSocketSession() != null && !sshSession.getSocketSession().isOpen()) {
                //createReadThread(sessionId, webSocketSession, sshSession.getChannelShell());
            }
            out =  sshSession.getChannelShell().getOutputStream();
        }

        return out;
    }

    /**
     *  remove sshSession
     */
    public static void removeSshSession(String sessionId) {
        logger.info("remove sshSession {} start", sessionId);
        SshSession sshSession = sshSessionMap.get(sessionId);
        if (sshSession != null) {
            ChannelShell channelShell = sshSession.getChannelShell();
            if (channelShell != null & channelShell.isConnected()) {
                channelShell.disconnect();
            }
            Session session = sshSession.getSshSession();
            if (session != null & session.isConnected()) {
                session.disconnect();
            }
            Thread thread = sshSession.getReadThread();
            if (thread.isAlive() && !thread.isInterrupted()) {
                thread.interrupt();
            }
        }
        sshSessionMap.remove(sessionId);
        logger.info("remove sshSession {} end", sessionId);
    }

}

