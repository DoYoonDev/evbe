package com.evolve.evolvebe.log.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

	@GetMapping("/400bad")
	public void bad(String name, String message) {
		throw new IllegalArgumentException("Invalid argument");
	}

	@GetMapping("/500error")
	public void error(String name, String message) {
		throw new RuntimeException("error");
	}
}
