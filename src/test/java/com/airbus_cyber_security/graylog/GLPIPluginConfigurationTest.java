/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

public class GLPIPluginConfigurationTest extends com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration {
	private String glpiURL;
	private String userToken;
	private String appToken;
	
	public String getGlpiURL() {
		return glpiURL;
	}

	public void setGlpiURL(String glpiURL) {
		this.glpiURL = glpiURL;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String apiToken) {
		this.userToken = apiToken;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String apiToken) {
		this.appToken = apiToken;
	}
	
	@Override
	public String glpiUrl() {
		return glpiURL;
	}

	@Override
	public String userToken() {
		return userToken;
	}

	@Override
	public String appToken() {
		return appToken;
	}

	@Override
	public int heapSize() {
		return 100;
	}

	@Override
	public int ttl() {
		return 60;
	}
	
	@Override
	public Builder toBuilder() {
		return null;
	}

	public GLPIPluginConfigurationTest(String glpiURL, String userToken, String appToken) {
		super();
		this.glpiURL = glpiURL;
		this.userToken = userToken;
		this.appToken = appToken;
	}

}
