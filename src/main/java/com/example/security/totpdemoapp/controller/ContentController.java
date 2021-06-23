package com.example.security.totpdemoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    @GetMapping("/register")
	public String greeting() {
		return "register";
	}
}
