/*
 *  Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved
 *  Airbus Defense and Space owns the copyright of this document.
 */
package com.airbus_cyber_security.graylog;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import java.util.HashMap;
import java.util.Map;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration;

public class GLPITest {
	
	private GLPI plugin;
	private ClusterConfigService clusterConfig;

	@Before
	public void setUp() throws Exception {
		clusterConfig = mock(ClusterConfigService.class);
		plugin = new GLPI(clusterConfig);
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void evaluateNominalTest() {
//		String responseQuery = "vwing.tuxtrooper.com";
//		String responseType = "Computer";
//		String responseFilter = "";
//		StringBuffer tokenBuffer = new StringBuffer();
//		tokenBuffer.append("{\"session_token\":\"fake_token\"}");
//		Map<String, String> response = new HashMap<>();
//		response.put("Name", "xwing.tuxtrooper.com");
//		response.put("Status", "used");
//		response.put("Manufacturer", "msi");
//		response.put("Serialnumber", "");
//		response.put("Type", "desktop");
//		response.put("Model", "null");
//		response.put("OSName", "null");
//		response.put("Location", "Home");
//		response.put("Lastupdate", "2019-09-12 13:59:50");
//		response.put("Processor", "null");
//		String expected= "Name=xwing.tuxtrooper.com Status=used Manufacturer=mis SerialNumber= Type=desktop Model= OSName= Location=Home LastUpdate=2019-09-12-13:59:50 Processor=";
//		
//		GLPIAPISession session = mock(GLPIAPISession.class);
//		GLPIConnection connection = mock(GLPIConnection.class);
//		FunctionArgs functionArgs = mock(FunctionArgs.class);
//		EvaluationContext evaluationContext = mock(EvaluationContext.class);
//		final ParameterDescriptor<String, String> queryParam = mock(ParameterDescriptor.class);
//		final ParameterDescriptor<String, String> typeParam = mock(ParameterDescriptor.class);
//		final ParameterDescriptor<String, String> filterParam = mock(ParameterDescriptor.class);
//		
//		GLPIPluginConfigurationTest config = new GLPIPluginConfigurationTest("http://fakeurl.com", "fake_token");
//		
//		plugin.setConnection(connection);
//		plugin.setSession(session);
//		
//		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);
//		
//		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
//		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
//		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);
//		
//		when(connection.getResponseStream()).thenReturn(tokenBuffer);
//		when(session.getSessionTokenFromAPI(any(GLPIConnection.class))).thenReturn("fake_session_token");
//		when(session.getSearchFromAPI(connection, responseType, responseQuery, responseFilter)).thenReturn(response);
//		
//		String actual = plugin.evaluate(functionArgs, evaluationContext);
//		
//		assertEquals(expected, actual);
//	}
	
	@Test
	public void evaluateNoTokenSessionTest() {
		String expected = "";
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";
		
		GLPIAPISession session = mock(GLPIAPISession.class);
		GLPIConnection connection = mock(GLPIConnection.class);
		FunctionArgs functionArgs = mock(FunctionArgs.class);
		EvaluationContext evaluationContext = mock(EvaluationContext.class);
		final ParameterDescriptor<String, String> queryParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> typeParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> filterParam = mock(ParameterDescriptor.class);

		StringBuffer tokenBuffer = new StringBuffer();
		tokenBuffer.append("{\"session_token\":\"fake_token\"}");
		
		GLPIPluginConfigurationTest config = new GLPIPluginConfigurationTest("http://fakeurl.com", "fake_token");
		plugin.setConnection(connection);
		plugin.setSession(session);
		
		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);
		
		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);
		
		when(connection.getResponseStream()).thenReturn(tokenBuffer);
		when(session.getSessionTokenFromAPI(connection)).thenReturn("");
		String actual = plugin.evaluate(functionArgs, evaluationContext);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void evaluateWithoutConfigTest() {
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";
		String expected = "";
		
		FunctionArgs functionArgs = mock(FunctionArgs.class);
		EvaluationContext evaluationContext = mock(EvaluationContext.class);
		final ParameterDescriptor<String, String> queryParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> typeParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> filterParam = mock(ParameterDescriptor.class);

		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);
		
		String actual = plugin.evaluate(functionArgs, evaluationContext);
		
		assertEquals(expected, actual);
	}

	@Test
	public void evaluateNoResultTest() {
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";
		StringBuffer tokenBuffer = new StringBuffer();
		tokenBuffer.append("{\"session_token\":\"fake_token\"}");
		Map<String, String> response = new HashMap<>();
		String expected= "";
		
		GLPIAPISession session = mock(GLPIAPISession.class);
		GLPIConnection connection = mock(GLPIConnection.class);
		FunctionArgs functionArgs = mock(FunctionArgs.class);
		EvaluationContext evaluationContext = mock(EvaluationContext.class);
		final ParameterDescriptor<String, String> queryParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> typeParam = mock(ParameterDescriptor.class);
		final ParameterDescriptor<String, String> filterParam = mock(ParameterDescriptor.class);
		
		GLPIPluginConfigurationTest config = new GLPIPluginConfigurationTest("http://fakeurl.com", "fake_token");
		
		plugin.setConnection(connection);
		plugin.setSession(session);
		
		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);
		
		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);
		
		when(connection.getResponseStream()).thenReturn(tokenBuffer);
		when(session.getSessionTokenFromAPI(any(GLPIConnection.class))).thenReturn("fake_session_token");
		when(session.getSearchFromAPI(connection, responseType, responseQuery, responseFilter)).thenReturn(response);
		
		String actual = plugin.evaluate(functionArgs, evaluationContext);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void mapToStringTest() {
		Map<String, String> response = new HashMap<>();
		response.put("Name", "xwing.tuxtrooper.com");
		response.put("Status", "used");
		response.put("Manufacturer", "msi");
		response.put("Serialnumber", "");
		response.put("Type", "desktop");
		response.put("Model", "null");
		response.put("OSName", "null");
		response.put("Location", "Home");
		response.put("Lastupdate", "2019-09-12 13:59:50");
		response.put("Processor", "null");
		String expected= "Status=used Type=desktop Processor=null Manufacturer=msi Model=null Serialnumber= OSName=null Lastupdate=2019-09-12-13:59:50 Name=xwing.tuxtrooper.com Location=Home";
		
		assertEquals(expected, plugin.mapToString(response));
	}
}
