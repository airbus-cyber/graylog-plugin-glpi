package com.airbus_cyber_security.graylog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Session {

	private String apiURL;
	private String userToken;
	@JsonProperty("session_token")
	private String sessionToken;

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}
}
