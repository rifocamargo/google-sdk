package com.example.googlesqk;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SignIn;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class MyActionsApp extends DialogflowApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyActionsApp.class);
	
	private static final String CLIENT_ID = "701662057594-m75o91vf9m9ubtpuatgph570dgl6ak0l.apps.googleusercontent.com";

	@ForIntent("login")
	public ActionResponse welcome(ActionRequest request) {
		return getResponseBuilder(request).add(new SignIn().setContext("To get your account details")).build();
	}

	@ForIntent("actions.intent.SIGN_IN")
	public ActionResponse getSignInStatus(ActionRequest request) {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (request.isSignInGranted()) {
			GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
			responseBuilder
					.add("I got your account details, " + profile.get("given_name") + ". What do you want to do next?");
		} else {
			responseBuilder.add("I won't be able to save your data, but what do you want to do next?");
		}
		return responseBuilder.build();
	}

	private GoogleIdToken.Payload getUserProfile(String idToken) {
		GoogleIdToken.Payload profile = null;
		try {
			profile = decodeIdToken(idToken);
		} catch (Exception e) {
			LOGGER.error("error decoding idtoken");
			LOGGER.error(e.toString());
		}
		return profile;
	}

	private GoogleIdToken.Payload decodeIdToken(String idTokenString) throws GeneralSecurityException, IOException {
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(CLIENT_ID)).build();
		GoogleIdToken idToken = verifier.verify(idTokenString);
		return idToken.getPayload();
	}
}
