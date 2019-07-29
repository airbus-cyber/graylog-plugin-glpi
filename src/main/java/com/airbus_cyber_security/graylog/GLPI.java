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

    private final ParameterDescriptor<String, String> valueParam = ParameterDescriptor
    		.string(PARAM)
    		.description("A number, negative or positive.")
    		.build();

    @Override
    public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
    	
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
   	
    public static String GETSessionToken(String apiURL, String userToken) throws IOException {
        URL urlForGetRequest = new URL(apiURL + "/initSession");
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "user_token " + userToken);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            Token token = new ObjectMapper().readValue(response.toString(), Token.class);
            return token.getSessionToken();
        } else {
            return null;
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