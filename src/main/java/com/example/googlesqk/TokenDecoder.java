package com.example.googlesqk;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.openidconnect.IdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Clock;

@Service
public class TokenDecoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenDecoder.class);

	private static final String CLIENT_ID = "701662057594-m75o91vf9m9ubtpuatgph570dgl6ak0l.apps.googleusercontent.com";

	public GoogleIdToken.Payload decodeIdToken(String idTokenString) throws GeneralSecurityException, IOException {
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(CLIENT_ID)).build();

		GoogleIdToken idToken = GoogleIdToken.parse(verifier.getJsonFactory(), idTokenString);

		if (this.verify(verifier, idToken)) {
			LOGGER.info("Token is valid");
			return idToken.getPayload();
		} else {
			LOGGER.info("Token is invalid");
			return null;
		}

	}

	public boolean verify(GoogleIdTokenVerifier verifier, GoogleIdToken googleIdToken)
			throws GeneralSecurityException, IOException {
		if (!this.isPaylodOk(verifier, googleIdToken)) {
			LOGGER.info("Paylod is not ok");
			LOGGER.info("googleIdToken.verifyIssuer(verifier.getIssuers()) = {}",
					googleIdToken.verifyIssuer(verifier.getIssuers()));

			LOGGER.info("googleIdToken.verifyAudience(verifier.getAudience()) = {}",
					googleIdToken.verifyAudience(verifier.getAudience()));

			LOGGER.info("googleIdToken.getPayload().getAudienceAsList().isEmpty() = {}",
					googleIdToken.getPayload().getAudienceAsList().isEmpty());
			googleIdToken.getPayload().getAudienceAsList().stream()
					.forEach(aud -> LOGGER.info("googleIdToken.getPayload().getAudienceAsList().stream() = {}", aud));

			LOGGER.info("verifier.getAudience().containsAll(googleIdToken.getPayload().getAudienceAsList()) = {}",
					verifier.getAudience().containsAll(googleIdToken.getPayload().getAudienceAsList()));
			verifier.getAudience().stream().forEach(aud -> LOGGER.info("verifier.getAudience().stream() = {}", aud));

			LOGGER.info("googleIdToken.verifyTime(Clock.SYSTEM.currentTimeMillis(), 300) = {}",
					googleIdToken.verifyTime(Clock.SYSTEM.currentTimeMillis(), 300));
			return false;
		}
		for (PublicKey publicKey : verifier.getPublicKeys()) {
			LOGGER.info("Verifying public kay signature. {}", publicKey.toString());
			if (googleIdToken.verifySignature(publicKey)) {
				LOGGER.info("Signature is valid");
				return true;
			}
		}

		return false;
	}

	private boolean isPaylodOk(GoogleIdTokenVerifier verifier, GoogleIdToken googleIdToken) {
		return googleIdToken.verifyIssuer(verifier.getIssuers()) && googleIdToken.verifyAudience(verifier.getAudience())
				&& googleIdToken.verifyTime(Clock.SYSTEM.currentTimeMillis(), 300);
	}
}
