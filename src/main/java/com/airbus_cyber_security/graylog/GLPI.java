package com.airbus_cyber_security.graylog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLPI extends AbstractFunction<Map<String, Object>> {
	Logger LOG = LoggerFactory.getLogger(Function.class);

	public static final String NAME = "GLPI";
	private static final String PARAM = "field";
	private static final String TYPE = "type";

	private Session session = new Session();

	private final ParameterDescriptor<String, String> valueParam = ParameterDescriptor.string(PARAM)
			.description("The field you want to submit into GLPI API.").build();
	private final ParameterDescriptor<String, String> typeParam = ParameterDescriptor.string(TYPE)
			.description("The category of the field you want to submit into GLPI API. Can be Computer, Software, ...")
			.build();

	@Override
	public Map<String, Object> evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String param = valueParam.required(functionArgs, evaluationContext);
		String type = typeParam.required(functionArgs, evaluationContext);
		Map<String, Object> response = new HashMap<String, Object>();

		try {
			session.setApiURL("http://192.168.43.76/glpi/apirest.php");
			session.setUserToken("wZngFklVWzFXVYOXhogf0J65N4np6U59TBiWjF1p");
			session.GETSessionToken();
			LOG.info("GLPI: Searching into " + type + "for param: " + param);
			response.putAll(session.GETSearch(type, param));
			LOG.info("GLPI: " + response.toString());
			session.CloseSession();
		} catch (IOException e) {
			LOG.error(e.toString());
		}
		return response;
	}

	@Override
	public FunctionDescriptor<Map<String, Object>> descriptor() {
		return FunctionDescriptor.<Map<String, Object>>builder().name(NAME)
				.description("Returns Map of field return by the GLPI API").params(valueParam, typeParam)
				.returnType((Class<Map<String, Object>>) (Class) Map.class).build();
	}
}