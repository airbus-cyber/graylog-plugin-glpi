/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration;
import com.google.inject.Inject;

public class GLPI extends AbstractFunction<String> {
	Logger log = LoggerFactory.getLogger(GLPI.class);

	public static final String NAME = "GLPI";
	private static final String QUERY = "query";
	private static final String TYPE = "type";
	private static final String FILTER = "filter";

	private ClusterConfigService clusterConfig;
	private GLPIAPISession session = new GLPIAPISession();
	private GLPIConnection connection = new GLPIConnection();

	private CacheManager cacheManager;

	private ParameterDescriptor<String, String> queryParam = ParameterDescriptor.string(QUERY)
			.description("The query you want to submit into GLPI API.").build();
	private ParameterDescriptor<String, String> typeParam = ParameterDescriptor.string(TYPE)
			.description("The category of the field you want to submit into GLPI API. Can be Computer, Software, ...")
			.build();
	private ParameterDescriptor<String, String> filterParam = ParameterDescriptor.string(FILTER)
			.description("The fields list (comma-separated) you want to be returned").build();

	public GLPI() {
		super();
	}

	public GLPI(ClusterConfigService clusterConfig, GLPIAPISession session, GLPIConnection connection,
			CacheManager cacheManager, ParameterDescriptor<String, String> queryParam,
			ParameterDescriptor<String, String> typeParam, ParameterDescriptor<String, String> filterParam) {
		this.clusterConfig = clusterConfig;
		this.session = session;
		this.connection = connection;
		this.cacheManager = cacheManager;
		this.queryParam = queryParam;
		this.typeParam = typeParam;
		this.filterParam = filterParam;
	}

	@Inject
	public GLPI(final ClusterConfigService clusterConfigService) {
		clusterConfig = clusterConfigService;
		GLPIPluginConfiguration conf = clusterConfig.getOrDefault(GLPIPluginConfiguration.class,
				GLPIPluginConfiguration.createDefault());
		this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("myCache",
						CacheConfigurationBuilder
								.newCacheConfigurationBuilder(String.class, String.class,
										ResourcePoolsBuilder.heap(conf.heapSize()))
								.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(conf.ttl()))))
				.build(true);
	}

	@Override
	public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String query = queryParam.required(functionArgs, evaluationContext);
		String type = typeParam.required(functionArgs, evaluationContext);
		String filter = filterParam.required(functionArgs, evaluationContext);
		String sessionToken;
		String responseStr;
		Map<String, String> response = new HashMap<>();
		Cache<String, String> myCache = cacheManager.getCache("myCache", String.class, String.class);

		GLPIPluginConfiguration config = clusterConfig.get(GLPIPluginConfiguration.class);
		if (config == null) {
			log.error("Config is needed, please fill it");
			return "GLPI=noConfig";
		}

		if (myCache.containsKey(query)) {
			log.info("GLPI: query {} is into cache with response {}", query, myCache.get(query));
			return myCache.get(query);
		} else {
			log.info("GLPI: API URL is {} with user token {}", config.glpiUrl(), config.apiToken());
			session.setApiURL(config.glpiUrl());
			session.setUserToken(config.apiToken());
			session.setTimeout(config.timeout());

			sessionToken = session.getSessionTokenFromAPI(connection);
			if (sessionToken.isEmpty()) {
				log.error("GLPI: Impossible to get session token");
				return "";
			} else {
				session.setSessionToken(sessionToken);
			}

			log.info("GLPI: Searching into {} for param: {} with filter {}", type, query, filter);
			response.putAll(session.getSearchFromAPI(connection, type, query, filter));
			log.info("GLPI: Filtered API response {}", response);
			session.closeSession(connection);
			if (response.isEmpty()) {
				log.warn("GLPI: no result");
				return "GLPI=noResult";
			}
			responseStr = mapToString(response);
			myCache.put(query, responseStr);
			return responseStr;
		}
	}

	public String mapToString(Map<String, String> map) {
		String result;
		StringBuilder bld = new StringBuilder();

		// Put the Map into a key=value String
		for (Entry<String, String> entry : map.entrySet()) {
			bld.append(entry.getKey() + "=" + entry.getValue().replace(" ", "-") + " ");
		}
		result = bld.toString();
		result = result.substring(0, result.length() - 1).replace("\"", "");
		log.info("GLPI: Result {}", result);
		return result;
	}

	@Override
	public FunctionDescriptor<String> descriptor() {
		return FunctionDescriptor.<String>builder().name(NAME)
				.description("Returns key=value string of field from the filter return by the GLPI API")
				.params(queryParam, typeParam, filterParam).returnType(String.class).build();
	}
}
