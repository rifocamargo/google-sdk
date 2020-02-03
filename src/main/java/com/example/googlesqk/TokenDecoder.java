package com.example.googlesqk;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class TokenDecoder {

	private static final String CLIENT_ID = "377993843761-aosdq6b7gaa4auc5k9ccl1o9fkhkckm0.apps.googleusercontent.com";

	public GoogleIdToken.Payload decodeIdToken(String idTokenString) throws GeneralSecurityException, IOException {
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Collections.singletonList(CLIENT_ID)).build();

		GoogleIdToken idToken = verifier.verify(idTokenString);
		return idToken.getPayload();
	}

	private void getAccessToken() throws GeneralSecurityException, IOException {
		GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
				GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), CLIENT_ID,
				"[[ENTER YOUR CLIENT SECRET]]", Arrays.asList("openid", "profile", "email")).setAccessType("offline")
						.build();
		GoogleAuthorizationCodeTokenRequest newTokenRequest = googleAuthorizationCodeFlow.newTokenRequest("");
		GoogleTokenResponse googleTokenResponse = newTokenRequest.execute();
	}
}
