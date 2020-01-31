package com.example.googlesqk;

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

@Service
public class MyActionsApp extends DialogflowApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyActionsApp.class);
	
	@Autowired
	private TokenDecoder tokenDecoder;

	@ForIntent("Default Welcome Intent")
	public ActionResponse welcome(ActionRequest request) {
		LOGGER.info("Signin is granted: '{}'", request.isSignInGranted());
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (!request.getUser().getUserVerificationStatus().equals("VERIFIED")) {
			responseBuilder.add("Olá Visitante");
			responseBuilder.endConversation();
			return responseBuilder.build();
		}
		if (this.userIsSignedIn(request)) {
			LOGGER.info("Retrieving user from token '{}'", request.getUser().getIdToken());
			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
			responseBuilder
					.add("Olá " + profile.get("given_name") + "!. Vamos começar?");
		} else {
			responseBuilder.add("Usuário não está logado");
		}
		return responseBuilder.build();
		
	}

	@ForIntent("signin")
	public ActionResponse signin(ActionRequest request) {
		return getResponseBuilder(request).add(new SignIn()).build();
	}

	private GoogleIdToken.Payload getUserProfile(String idToken) {
		GoogleIdToken.Payload profile = null;
		try {
			profile = tokenDecoder.decodeIdToken(idToken);
		} catch (Exception e) {
			LOGGER.error("error decoding idtoken");
			LOGGER.error(e.toString());
		}
		return profile;
	}
	
	private boolean userIsSignedIn(ActionRequest request) {
		String idToken = request.getUser().getIdToken();
		LOGGER.info(String.format("Id token: %s", idToken));
		if (idToken == null || idToken.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}

//import com.google.actions.api.ActionRequest;
//import com.google.actions.api.ActionResponse;
//import com.google.actions.api.DialogflowApp;
//import com.google.actions.api.ForIntent;
//import com.google.actions.api.response.ResponseBuilder;
//import com.google.actions.api.response.helperintent.SignIn;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * Tip: Sign In should not happen in the Default Welcome Intent, instead later
// * in the conversation. See `Action discovery` docs:
// * https://developers.google.com/actions/discovery/implicit#action_discovery
// */
//@Service
//public class MyActionsApp extends DialogflowApp {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(MyActionsApp.class);
//
//	@Autowired
//	private TokenDecoder tokenDecoder;
//
//	@ForIntent("Default Welcome Intent")
//	public ActionResponse welcome(ActionRequest request) {
//		LOGGER.info("Welcome intent start.");
//		ResponseBuilder responseBuilder = getResponseBuilder(request);
//
//		// Account Linking is only supported for verified users
//		// https://developers.google.com/actions/assistant/guest-users
//		if (!request.getUser().getUserVerificationStatus().equals("VERIFIED")) {
//			responseBuilder.add("Olá Visitante");
//			responseBuilder.endConversation();
//			return responseBuilder.build();
//		}
//
//		if (userIsSignedIn(request)) {
//			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
//			responseBuilder.add(String.format("Hi %s!", profile.get("given_name")));
//
//		} else {
//			responseBuilder.add(String.format("Hi", ""));
//			responseBuilder.add("What is your favorite color?");
//		}
//		responseBuilder.addSuggestions(new String[] { "Red", "Green", "Blue" });
//		LOGGER.info("Welcome intent end.");
//		return responseBuilder.build();
//	}
//
//	@ForIntent("Give Color")
//	public ActionResponse giveColor(ActionRequest request) {
//		LOGGER.info("Give color intent start.");
//		ResponseBuilder responseBuilder = getResponseBuilder(request);
//		if (userIsSignedIn(request)) {
//			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
//
//			responseBuilder.add("Since you are signed in, I'll remember it next time.").add(profile.getEmail()).endConversation();
//			LOGGER.info("Give color intent end.");
//			return responseBuilder.build();
//		}
//		responseBuilder.add(new SignIn());
//		LOGGER.info("Give color intent end.");
//		return responseBuilder.build();
//	}
//
//	@ForIntent("Get Sign In")
//	public ActionResponse getSignIn(ActionRequest request) {
//		LOGGER.info("Get sign in intent start.");
//		ResponseBuilder responseBuilder = getResponseBuilder(request);
//		if (request.isSignInGranted()) {
//			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
//			responseBuilder.add("Since you are signed in, I'll remember it next time.").add(profile.getEmail()).endConversation();
//		} else {
//			responseBuilder.add("Let's try again next time.").endConversation();
//		}
//		LOGGER.info("Get sign in intent end.");
//		return responseBuilder.build();
//	}
//
//	private boolean userIsSignedIn(ActionRequest request) {
//		String idToken = request.getUser().getIdToken();
//		LOGGER.info(String.format("Id token: %s", idToken));
//		if (idToken == null || idToken.isEmpty()) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	private GoogleIdToken.Payload getUserProfile(String idToken) {
//		LOGGER.info("Retrieving user from token '{}'", idToken);
//		GoogleIdToken.Payload profile = null;
//		try {
//			profile = tokenDecoder.decodeIdToken(idToken);
//		} catch (Exception e) {
//			LOGGER.error("error decoding idtoken", e);
//		}
//		return profile;
//	}
//}