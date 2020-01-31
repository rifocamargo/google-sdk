package com.example.googlesqk;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SignIn;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.services.actions_fulfillment.v2.model.User;

@Service
public class MyActionsApp extends DialogflowApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyActionsApp.class);

	@Autowired
	private TokenDecoder tokenDecoder;

	@ForIntent("Default Welcome Intent")
	public ActionResponse welcome(ActionRequest request) {
		LOGGER.info("Signin is granted: '{}'", request.isSignInGranted());
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (this.userIsSignedIn(request)) {
			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
			responseBuilder.add(String.format("Olá %s! Vamos começar?", profile.get("given_name")));
		} else {
			responseBuilder.add("Usuário não está logado");
		}
		return responseBuilder.build();

	}

	@ForIntent("signin")
	public ActionResponse signin(ActionRequest request) {
		return getResponseBuilder(request).add(new SignIn().setContext("Para pegar os seus dados")).build();
	}

	private GoogleIdToken.Payload getUserProfile(String idToken) {
		GoogleIdToken.Payload profile = null;
		try {
			profile = tokenDecoder.decodeIdToken(idToken);
		} catch (Exception e) {
			LOGGER.error("error decoding idtoken", e);
		}
		return profile;
	}

	private boolean userIsSignedIn(ActionRequest request) {
		String idToken = request.getUser().getIdToken();
		if (idToken == null || idToken.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}