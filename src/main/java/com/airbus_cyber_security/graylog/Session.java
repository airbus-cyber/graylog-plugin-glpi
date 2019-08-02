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
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Session {

	private String apiURL;
	private String userToken;
	private String sessionToken;

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
		computer_translation_matrix.put("5", "SerialNumber");
		computer_translation_matrix.put("19", "Date");
		computer_translation_matrix.put("23", "Manufacturer");
		computer_translation_matrix.put("31", "Status");
		computer_translation_matrix.put("40", "Model");
	};

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public static Map<String, Object> mapping_field(Map<String, Object> map, Map<String, String> translation) {
		Map<String, Object> mapped_map = new HashMap<String, Object>(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			if (translation.get(entry.getKey()) != null) {
				mapped_map.put(translation.get(entry.getKey()), entry.getValue());
			}
		}
		return mapped_map;
	}

	public void GETSessionToken() throws IOException {
		URL urlForGetRequest = new URL(this.getApiURL() + "/initSession");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + this.getUserToken());

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
			this.setSessionToken(jsonObject.get("session_token").toString().replaceAll("\"", ""));
		}
	}

	public Map<String, Object> GETSearch(String category, String search) throws IOException {
		Map<String, Object> resultList = new HashMap<String, Object>();
		Map<String, Object> blankList = new HashMap<String, Object>();
		Map<String, String> translation_matrix = null;
		URL urlForGetRequest = new URL(this.getApiURL() + "/search/" + category + "?search=" + search);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + this.getUserToken());
		connection.setRequestProperty("Session-Token", this.getSessionToken());

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// Get the API response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();

			// Interpret JSON and put it in Map
			JsonReader reader = Json.createReader(new StringReader(response.toString()));
			JsonObject jsonObject = reader.readObject();
			for (Entry<String, JsonValue> i : jsonObject.getValue("/data").asJsonArray().get(0).asJsonObject()
					.entrySet()) {
				resultList.put(i.getKey(), i.getValue().toString());
			}

			switch (category) {
			case "Computer":
				translation_matrix = computer_translation_matrix;
				break;
			case "Software":
				translation_matrix = software_translation_matrix;
				break;
			default:
				break;
			}
			return mapping_field(resultList, translation_matrix);
		} else {
			return blankList;
		}
	}

	public boolean CloseSession() throws IOException {
		URL urlForGetRequest = new URL(this.getApiURL() + "/killSession");
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "user_token " + this.getUserToken());
		connection.setRequestProperty("Session-Token", this.getSessionToken());

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return true;
		}
		return false;
	}
}
