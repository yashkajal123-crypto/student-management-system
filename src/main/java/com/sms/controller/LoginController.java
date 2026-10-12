package com.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the custom login page (the actual authentication
 * is handled by Spring Security).
 */
@Controller
public class LoginController {

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}
}
