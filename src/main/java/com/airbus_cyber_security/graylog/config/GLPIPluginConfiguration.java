/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class GLPIPluginConfiguration {
	@JsonProperty("glpi_url")
	public abstract String glpiUrl();

	@JsonProperty("user_token")
	public abstract String userToken();

	@JsonProperty("app_token")
	public abstract String appToken();

	@JsonProperty("heap_size")
	public abstract int heapSize();

	@JsonProperty("ttl")
	public abstract int ttl();

	@JsonCreator
	public static GLPIPluginConfiguration create(@JsonProperty("glpi_url") String glpiUrl,
												 @JsonProperty("user_token") String userToken,
												 @JsonProperty("app_token") String appToken,
												 @JsonProperty("heap_size") int heapSize,
												 @JsonProperty("ttl") int ttl) {
		if(heapSize <= 0)
			heapSize = 100;
		if(ttl <= 0) {
			ttl = 60;
		}
		return builder().glpiUrl(glpiUrl).userToken(userToken).appToken(appToken).heapSize(heapSize).ttl(ttl).build();
	}

	public static GLPIPluginConfiguration createDefault() {
		return builder().glpiUrl("").userToken("").appToken("").heapSize(100).ttl(60).build();
	}

	public static Builder builder() {
		return new AutoValue_GLPIPluginConfiguration.Builder();
	}

	public abstract Builder toBuilder();

	@AutoValue.Builder
	public static abstract class Builder {
		public abstract Builder glpiUrl(String glpiUrl);

		public abstract Builder userToken(String userToken);

		public abstract Builder appToken(String appToken);

		public abstract Builder heapSize(int heapSize);

		public abstract Builder ttl(int ttl);

		public abstract GLPIPluginConfiguration build();
	}

}
