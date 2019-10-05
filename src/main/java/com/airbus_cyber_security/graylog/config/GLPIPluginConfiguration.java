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

	@JsonProperty("api_token")
	public abstract String apiToken();

	@JsonProperty("heap_size")
	public abstract int heapSize();

	@JsonProperty("ttl")
	public abstract int ttl();

	@JsonCreator
	public static GLPIPluginConfiguration create(@JsonProperty("glpi_url") String glpiUrl,
			@JsonProperty("api_token") String apiToken, @JsonProperty("heap_size") int heapSize,
			@JsonProperty("ttl") int ttl) {
		return builder().glpiUrl(glpiUrl).apiToken(apiToken).heapSize(heapSize).ttl(ttl).build();
	}

	public static GLPIPluginConfiguration createDefault() {
		return builder().glpiUrl("").apiToken("").heapSize(100).ttl(60).build();
	}

	public static Builder builder() {
		return new AutoValue_GLPIPluginConfiguration.Builder();
	}

	public abstract Builder toBuilder();

	@AutoValue.Builder
	public static abstract class Builder {
		public abstract Builder glpiUrl(String glpiUrl);

		public abstract Builder apiToken(String apiToken);

		public abstract Builder heapSize(int heapSize);

		public abstract Builder ttl(int ttl);

		public abstract GLPIPluginConfiguration build();
	}

}
