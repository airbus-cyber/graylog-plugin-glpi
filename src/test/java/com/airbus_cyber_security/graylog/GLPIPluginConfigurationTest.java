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

	public GLPIPluginConfigurationTest(String glpiURL, String apiToken) {
		super();
		this.glpiURL = glpiURL;
		this.apiToken = apiToken;
	}

}
