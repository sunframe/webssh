package com.sunframe.webssh.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class indexContorller {

	@RequestMapping("/")
	public String index () {
		return "index";
	}

}
