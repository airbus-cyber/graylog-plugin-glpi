package com.airbus_cyber_security.graylog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GLPI extends AbstractFunction<String> {

    public static final String NAME = "GLPI";
    private static final String PARAM = "field";
    private static final Session session = new Session();

    private final ParameterDescriptor<String, String> valueParam = ParameterDescriptor
    		.string(PARAM)
    		.description("A number, negative or positive.")
    		.build();

    @Override
    public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
		try {
			session.setApiURL("http://glpi:8080/glpi/apirest.php");
			session.setUserToken("wZngFklVWzFXVYOXhogf0J65N4np6U59TBiWjF1p");
			GETSessionToken(session);
			GETSearch(session.getApiURL(), session.getUserToken(), session.getSessionToken(), "Computer", "foo1");
			CloseSession(session.getApiURL(), session.getUserToken(), session.getSessionToken());
		} catch (IOException e) {
			System.out.println(e);
		}
        return "TODO";
    }
    
   	@Override
   	public FunctionDescriptor<String> descriptor() {
   	    return FunctionDescriptor.<String>builder()
   	            .name(NAME)
   	            .description("TODO")
   	            .params(valueParam)
   	            .returnType(String.class)
   	            .build();
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
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            session.setSessionToken(new ObjectMapper().readValue(response.toString(), Session.class).getSessionToken());
        }
    }

    public static String GETSearch(String apiURL, String userToken, String sessionToken, String category, String search) throws IOException {
        URL urlForGetRequest = new URL(apiURL + "/" + category + "?searchText=" + search);
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "user_token " + userToken);
        connection.setRequestProperty("Session-Token", sessionToken);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            return response.toString();
        } else {
            return null;
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