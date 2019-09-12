package com.airbus_cyber_security.graylog;

import java.io.IOException;
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
	private Session session = new Session();

	private final ParameterDescriptor<String, String> queryParam = ParameterDescriptor.string(QUERY)
			.description("The query you want to submit into GLPI API.").build();
	private final ParameterDescriptor<String, String> typeParam = ParameterDescriptor.string(TYPE)
			.description("The category of the field you want to submit into GLPI API. Can be Computer, Software, ...")
			.build();
	private final ParameterDescriptor<String, String> filterParam = ParameterDescriptor.string(FILTER)
			.description("The fields list (comma-separated) you want to be returned")
			.build();

	@Inject
	public GLPI(final ClusterConfigService clusterConfigService) {
		clusterConfig = clusterConfigService;
	}

	@Override
	public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String query = queryParam.required(functionArgs, evaluationContext);
		String type = typeParam.required(functionArgs, evaluationContext);
		String filter = filterParam.required(functionArgs, evaluationContext);
		String result = "";
		StringBuilder bld = new StringBuilder();
		Map<String, Object> response = new HashMap<>();

		try {
			GLPIPluginConfiguration config = clusterConfig.get(GLPIPluginConfiguration.class);
			if (config == null) {
				LOG.error("Config is needed, please fill it");
				return "";
			}
			session.setApiURL(config.glpiUrl());
			session.setUserToken(config.apiToken());
			session.getSessionTokenFromAPI();
			LOG.info("GLPI: API URL is {} with token {}", config.glpiUrl(), config.apiToken());
			
			LOG.info("GLPI: Searching into {} for param: {} with filter {}", type, query, filter);
			response.putAll(session.getSearchFromAPI(type, query, filter));
			LOG.info("GLPI: Filtered API response {}", response);
			session.closeSession();
			if (response.isEmpty()) {
				return "";
			}
			
			for (Entry<String, Object> entry : response.entrySet()) {
				bld.append(entry.getKey() + "=" + entry.getValue().toString().replace(" ", "-") + " ");
			}
			result = bld.toString();
			result = result.substring(0, result.length() - 1).replace("\"", "");
			LOG.info("GLPI: Result {}", result);
		} catch (IOException e) {
			LOG.error(e.toString());
		}
		return result;
	}

	@Override
	public FunctionDescriptor<String> descriptor() {
		return FunctionDescriptor.<String>builder().name(NAME)
				.description("Returns key=value string of field from the filter return by the GLPI API").params(queryParam, typeParam, filterParam)
				.returnType(String.class).build();
	}
}