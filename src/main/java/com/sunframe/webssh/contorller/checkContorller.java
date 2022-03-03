package com.sunframe.webssh.contorller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class checkContorller {

	@RequestMapping(value = "/check")
	public JSONObject checkData(HttpServletRequest request, HttpServletResponse response){
		JSONObject result = new JSONObject();
		String hostname = request.getParameter("hostname");
		String port = request.getParameter("port");
		String password = request.getParameter("password");
		if (!StringUtils.isEmpty(hostname) && !StringUtils.isEmpty(port) ) {
			try {
				Socket socket = new Socket(hostname, Integer.valueOf(port));
				socket.close();
				result.put("status", true);
			} catch (Exception e) {
				String msg = "Connection to "+hostname+":"+port +" is not allowed.";
				result.put("msg",msg);
				result.put("status", false);
			}
		}
		result.put("id",getToken());
		return result;
	}


	public Integer getToken() {
		long current = System.currentTimeMillis();

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int r  = random.nextInt(99990) + 10;
		return r;
	}
}
