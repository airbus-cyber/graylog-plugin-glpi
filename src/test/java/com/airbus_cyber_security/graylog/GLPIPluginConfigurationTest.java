/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

public class GLPIPluginConfigurationTest extends com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration {
	private String glpiURL;
	private String apiToken;
	
	public String getGlpiURL() {
		return glpiURL;
	}

	public void setGlpiURL(String glpiURL) {
		this.glpiURL = glpiURL;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
	
	@Override
	public String glpiUrl() {
		return glpiURL;
	}

	@Override
	public String apiToken() {
		return apiToken;
	}

	@Override
	public Builder toBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	public GLPIPluginConfigurationTest(String glpiURL, String apiToken) {
		super();
		this.glpiURL = glpiURL;
		this.apiToken = apiToken;
	}
}
