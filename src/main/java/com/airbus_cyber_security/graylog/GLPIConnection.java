/*
 *  Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved
 *  Airbus Defense and Space owns the copyright of this document.
 */
package com.airbus_cyber_security.graylog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLPIConnection {
	private StringBuffer responseStream;
	Logger log = LoggerFactory.getLogger(Function.class);

	public StringBuffer getResponseStream() {
		return responseStream;
	}

	public void setResponseStream(StringBuffer responseStream) {
		this.responseStream = responseStream;
	}
	
	public void connectToURL(String url, String userToken) {
		String readLine = null;
		URL urlForGetRequest;
		HttpURLConnection connection;
		
		try {
			log.info("GLPI: Connecting to URL {} with user token {}", url, userToken);
			urlForGetRequest = new URL(url);
			connection = (HttpURLConnection) urlForGetRequest.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "user_token " + userToken);
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				this.responseStream = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					this.responseStream.append(readLine);
				}
				in.close();
				log.info("GLPI: Raw response {}", this.responseStream);
			}
		} catch (MalformedURLException e) {
			log.error("Malformated URL: {}", url);
		} catch (IOException e) {
			log.error("Error trying to connect to {}", url);
			log.error(e.toString());
		}
	}
	
	public void connectToURL(String url, String userToken, String sessionToken) {
		String readLine = null;
		URL urlForGetRequest;
		HttpURLConnection connection;
		
		try {
			log.info("GLPI: Connecting to URL {} with user token {} and session token {}", url, userToken, sessionToken);
			urlForGetRequest = new URL(url);
			connection = (HttpURLConnection) urlForGetRequest.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "user_token " + userToken);
			connection.setRequestProperty("Session-Token", sessionToken);
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				this.responseStream = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					this.responseStream.append(readLine);
				}
				in.close();
				log.info("GLPI: Raw response {}", this.responseStream);
			}
		} catch (MalformedURLException e) {
			log.error("Malformated URL: {}", url);
		} catch (IOException e) {
			log.error("Error trying to connect to {}", url);
			log.error(e.toString());
		}
	}
}
