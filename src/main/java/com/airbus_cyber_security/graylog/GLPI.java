package com.airbus_cyber_security.graylog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

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

	private Session session = new Session();

	private final ParameterDescriptor<String, String> valueParam = ParameterDescriptor.string(PARAM)
			.description("A number, negative or positive.").build();

	@Override
	public Map<String, Object> evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		String param = valueParam.required(functionArgs, evaluationContext);
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			LOG.info("Defining session");
			session.setApiURL("http://192.168.43.76/glpi/apirest.php");
			session.setUserToken("wZngFklVWzFXVYOXhogf0J65N4np6U59TBiWjF1p");
			LOG.info("Getting session token");
			GETSessionToken(session);
			LOG.info("Searching for param: " + param);
			response.putAll(GETSearch(session, "Computer", param));
			LOG.info(response.toString());
			LOG.info("Closing session");
			CloseSession(session.getApiURL(), session.getUserToken(), session.getSessionToken());
		} catch (IOException e) {
			System.out.println(e);
		}
		return response;
	}

	@Override
	public FunctionDescriptor<Map<String, Object>> descriptor() {
		return FunctionDescriptor.<Map<String, Object>>builder().name(NAME).description(
				"Returns hexadecimal lower case string representation of the given number. No prefix, optionally left padded with zeros.")
				.params(valueParam).returnType((Class<Map<String, Object>>) (Class) Map.class).build();
	}

	static private Map<String, String> software_translation_matrix = new HashMap<String, String>();
	static {
		software_translation_matrix.put("1", "Name");
		software_translation_matrix.put("4", "OS");
		software_translation_matrix.put("5", "Version");
		software_translation_matrix.put("23", "Manufacturer");
	};

	static private Map<String, String> computer_translation_matrix = new HashMap<String, String>();
	static {
		computer_translation_matrix.put("1", "Name");
		computer_translation_matrix.put("3", "Location");
		computer_translation_matrix.put("4", "Type");
		computer_translation_matrix.put("5", "Serial Number");
		computer_translation_matrix.put("19", "Date");
		computer_translation_matrix.put("23", "Manufacturer");
		computer_translation_matrix.put("31", "Status");
		computer_translation_matrix.put("40", "Model");
	};

	public static Map<String, Object> mapping_field(Map<String, Object> map, Map<String, String> translation) {
		Map<String, Object> mapped_map = new HashMap<String, Object>(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			if (translation.get(entry.getKey()) != null) {
				mapped_map.put(translation.get(entry.getKey()), entry.getValue());
			}
		}
		return mapped_map;
	}

	public static void GETSessionToken(Session session) throws IOException {
		URL urlForGetRequest = new URL(session.getApiURL() + "/initSession");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + session.getUserToken());

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			JsonReader reader = Json.createReader(new StringReader(response.toString()));
			JsonObject jsonObject = reader.readObject();
			session.setSessionToken(jsonObject.get("session_token").toString().replaceAll("\"", ""));
		}
	}

	public static Map<String, Object> GETSearch(Session session, String category, String search) throws IOException {
		Map<String, Object> resultList = new HashMap<String, Object>();
		Map<String, Object> blankList = new HashMap<String, Object>();
		URL urlForGetRequest = new URL(session.getApiURL() + "/search/" + category + "?search=" + search);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + session.getUserToken());
		connection.setRequestProperty("Session-Token", session.getSessionToken());

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			JsonReader reader = Json.createReader(new StringReader(response.toString()));
			JsonObject jsonObject = reader.readObject();
			JsonArray jsonArray = jsonObject.getValue("/data").asJsonArray();
			for (JsonValue i : jsonArray) {
				resultList.putAll((Map<String, Object>) i);
			}
			return mapping_field(resultList, software_translation_matrix);
		} else {
			return blankList;
		}
	}

	public static boolean CloseSession(String apiURL, String userToken, String sessionToken) throws IOException {
		URL urlForGetRequest = new URL(apiURL + "/killSession");
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + userToken);
		connection.setRequestProperty("Session-Token", sessionToken);

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return true;
		}
		return false;
	}
}