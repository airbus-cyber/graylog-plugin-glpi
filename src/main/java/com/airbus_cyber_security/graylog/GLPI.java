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
	private static final String PARAM = "field";
	private static final String TYPE = "type";

	private ClusterConfigService clusterConfig;
	private Session session = new Session();

	private final ParameterDescriptor<String, String> valueParam = ParameterDescriptor.string(PARAM)
			.description("The field you want to submit into GLPI API.").build();
	private final ParameterDescriptor<String, String> typeParam = ParameterDescriptor.string(TYPE)
			.description("The category of the field you want to submit into GLPI API. Can be Computer, Software, ...")
			.build();

	@Inject
	public GLPI(final ClusterConfigService clusterConfigService) {
		clusterConfig = clusterConfigService;
	}

	@Override
	public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String param = valueParam.required(functionArgs, evaluationContext);
		String type = typeParam.required(functionArgs, evaluationContext);
		String result = "";
		Map<String, Object> response = new HashMap<String, Object>();

		try {
			GLPIPluginConfiguration config = clusterConfig.get(GLPIPluginConfiguration.class);
			if (config == null) {
				LOG.error("Config is needed, please fill it");
				return "";
			}
			session.setApiURL(config.glpiUrl());
			session.setUserToken(config.apiToken());
			session.GETSessionToken();
			LOG.info("GLPI: Searching into " + type + "for param: " + param);
			response.putAll(session.GETSearch(type, param));
			LOG.info("GLPI: " + response.toString());
			for (Entry<String, Object> entry : response.entrySet()) {
				result += entry.getKey() + "=" + entry.getValue().toString().replace(" ", "-") + " ";
			}
			result = result.substring(0, result.length() - 1).replace("\"", "");
			LOG.info(result);
			session.CloseSession();
		} catch (IOException e) {
			LOG.error(e.toString());
		}
		return result;
	}

	@Override
	public FunctionDescriptor<String> descriptor() {
		return FunctionDescriptor.<String>builder().name(NAME)
				.description("Returns Map of field return by the GLPI API").params(valueParam, typeParam)
				.returnType(String.class).build();
	}
}