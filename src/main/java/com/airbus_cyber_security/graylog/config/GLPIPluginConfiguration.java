package com.airbus_cyber_security.graylog.config;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@JsonAutoDetect
@AutoValue
public abstract class GLPIPluginConfiguration {
	@JsonProperty("glpi_url")
	public abstract String glpiUrl();

	@JsonProperty("api_token")
	public abstract String apiToken();

	@JsonCreator
	public static GLPIPluginConfiguration create(@JsonProperty("glpi_url") String glpiUrl,
			@JsonProperty("api_token") String apiToken) {
		return builder().glpiUrl(glpiUrl).apiToken(apiToken).build();
	}

	public static GLPIPluginConfiguration createDefault() {
		return builder().glpiUrl("").apiToken("").build();
	}

	public static Builder builder() {
		return new AutoValue_GLPIPluginConfiguration.Builder();
	}

	public abstract Builder toBuilder();

	@AutoValue.Builder
	public static abstract class Builder {
		public abstract Builder glpiUrl(String glpiUrl);

		public abstract Builder apiToken(String apiToken);

		public abstract GLPIPluginConfiguration build();
	}

}
