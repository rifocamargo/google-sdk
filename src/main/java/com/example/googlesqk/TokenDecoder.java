package com.example.googlesqk;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class TokenDecoder {

	private static final String CLIENT_ID = "701662057594-m75o91vf9m9ubtpuatgph570dgl6ak0l.apps.googleusercontent.com";

	public GoogleIdToken.Payload decodeIdToken(String idTokenString) throws GeneralSecurityException, IOException {
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(CLIENT_ID)).build();

		GoogleIdToken idToken = verifier.verify(idTokenString);
		return idToken.getPayload();
	}
}
