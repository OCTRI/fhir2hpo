package org.monarchinitiative.fhir2hpo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.monarchinitiative.fhir2hpo.loinc.Loinc;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.LoincResponseInfo;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Use the LOINC FHIR instance to check the validity of a LOINC and gather additional information. This external service 
 * requires an internet connection and uses a private account key that must be set using VM parameter LOINC_AUTH. 
 * This may not always be reliable.
 */
@Service
public class LoincService {
	
	private static final String URL = "https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code={{CODE}}&&property=SCALE_TYP&_format=json";

	@Value("${loinc.auth}")
	private String auth;

	public Loinc findLoincById(LoincId loincId) throws LoincFormatException {
		try {
			// Get the auth string encoded in Base 64
			// String authString = name + ":" + password;
			// byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
			// String authStringEnc = new String(authEncBytes);

			URL url = new URL(URL.replace("{{CODE}}", loincId.getCode()));
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + auth);
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			LoincResponseInfo info = mapper.readValue(sb.toString(), LoincResponseInfo.class);
			
			return new Loinc(loincId, info.getLoincScale(), info.getDisplayName());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			// This gets thrown if the LoincId does not exist
			throw new LoincFormatException("The code " + loincId.getCode() + " cannot be found in the LOINC web service.");
		}
		
	}

}
