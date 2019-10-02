/*
 *  Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved
 *  Airbus Defense and Space owns the copyright of this document.
 */
package com.airbus_cyber_security.graylog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration;
import com.google.inject.Inject;

public class GLPI extends AbstractFunction<String> {
	Logger LOG = LoggerFactory.getLogger(Function.class);

	public static final String NAME = "GLPI";
	private static final String QUERY = "query";
	private static final String TYPE = "type";
	private static final String FILTER = "filter";

	private ClusterConfigService clusterConfig;
	private GLPIAPISession session = new GLPIAPISession();
	private GLPIConnection connection = new GLPIConnection();

	protected void setSession(GLPIAPISession session) {
		this.session = session;
	}

	protected void setConnection(GLPIConnection connection) {
		this.connection = connection;
	}

	private final ParameterDescriptor<String, String> queryParam = ParameterDescriptor.string(QUERY)
			.description("The query you want to submit into GLPI API.").build();
	private final ParameterDescriptor<String, String> typeParam = ParameterDescriptor.string(TYPE)
			.description("The category of the field you want to submit into GLPI API. Can be Computer, Software, ...")
			.build();
	private final ParameterDescriptor<String, String> filterParam = ParameterDescriptor.string(FILTER)
			.description("The fields list (comma-separated) you want to be returned").build();

	@Inject
	public GLPI(final ClusterConfigService clusterConfigService) {
		clusterConfig = clusterConfigService;
	}

	@Override
	public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String query = queryParam.required(functionArgs, evaluationContext);
		String type = typeParam.required(functionArgs, evaluationContext);
		String filter = filterParam.required(functionArgs, evaluationContext);
		String sessionToken;
		Map<String, String> response = new HashMap<>();

		GLPIPluginConfiguration config = clusterConfig.get(GLPIPluginConfiguration.class);
		if (config == null) {
			LOG.error("Config is needed, please fill it");
			return "";
		}
		LOG.info("GLPI: API URL is {} with user token {}", config.glpiUrl(), config.apiToken());
		session.setApiURL(config.glpiUrl());
		session.setUserToken(config.apiToken());

		sessionToken = session.getSessionTokenFromAPI(connection);
		if (sessionToken.isEmpty()) {
			LOG.error("GLPI: Impossible to get session token");
			return "";
		} else {
			session.setSessionToken(sessionToken);
		}

		LOG.info("GLPI: Searching into {} for param: {} with filter {}", type, query, filter);
		response.putAll(session.getSearchFromAPI(connection, type, query, filter));
		LOG.info("GLPI: Filtered API response {}", response);
		session.closeSession(connection);
		if (response.isEmpty()) {
			LOG.warn("GLPI: no result");
			return "";
		}
		return mapToString(response);
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
		LOG.info("GLPI: Result {}", result);
		return result;
	}

	@Override
	public FunctionDescriptor<String> descriptor() {
		return FunctionDescriptor.<String>builder().name(NAME)
				.description("Returns key=value string of field from the filter return by the GLPI API")
				.params(queryParam, typeParam, filterParam).returnType(String.class).build();
	}
}