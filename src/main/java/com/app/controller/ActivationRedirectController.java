package com.app.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/activation")
public class ActivationRedirectController {

	@GetMapping
	public void redirectToApp(@RequestParam String token, HttpServletResponse response) throws IOException {

		String deepLink = "kidsvt://activate?token=" + token;

		response.sendRedirect(deepLink);
	}

}
