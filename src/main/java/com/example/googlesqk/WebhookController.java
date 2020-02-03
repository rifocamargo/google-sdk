package com.example.googlesqk;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);
	
	@Autowired
	private MyActionsApp myActionsApp;

	@PostMapping
	public CompletableFuture<String> serveAction(@RequestBody String body, @RequestHeader Map<String, String> headers) {
		LOGGER.info("headers: {}", headers);
		return myActionsApp.handleRequest(body, headers);
	}
}
