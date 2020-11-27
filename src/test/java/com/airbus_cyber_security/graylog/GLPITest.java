/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
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
	GLPIAPISession session;
	GLPIConnection connection;
	CacheManager cacheManager;
	ParameterDescriptor<String, String> queryParam;
	ParameterDescriptor<String, String> typeParam;
	ParameterDescriptor<String, String> filterParam;
	ParameterDescriptor<String, String> fieldParam;
	FunctionArgs functionArgs;
	EvaluationContext evaluationContext;
	GLPIPluginConfigurationTest config;

	@Before
	public void setUp() throws Exception {
		clusterConfig = mock(ClusterConfigService.class);
		queryParam = mock(ParameterDescriptor.class);
		typeParam = mock(ParameterDescriptor.class);
		filterParam = mock(ParameterDescriptor.class);
		fieldParam = mock(ParameterDescriptor.class);

		session = mock(GLPIAPISession.class);
		connection = mock(GLPIConnection.class);
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("myCache", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(100))
						.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(60))))
				.build(true);
		plugin = new GLPI(clusterConfig, session, connection, cacheManager, queryParam, typeParam, filterParam, fieldParam);

		functionArgs = mock(FunctionArgs.class);
		evaluationContext = mock(EvaluationContext.class);
		config = new GLPIPluginConfigurationTest("http://fakeurl.com", "fake_token", "fake_app-token", 500);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void evaluateNominalTest() {
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";
		StringBuffer tokenBuffer = new StringBuffer();
		tokenBuffer.append("{\"session_token\":\"fake_token\"}");
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
		String expected = "Status=used Type=desktop Processor=null Manufacturer=msi Model=null Serialnumber= OSName=null Lastupdate=2019-09-12-13:59:50 Name=xwing.tuxtrooper.com Location=Home";

		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);

		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);

		when(connection.getResponseStream()).thenReturn(tokenBuffer);
		when(session.getSessionTokenFromAPI(any(GLPIConnection.class))).thenReturn("fake_session_token");
		when(session.getSearchFromAPI(connection, responseType, responseQuery, responseFilter, null)).thenReturn(response);

		String actual = plugin.evaluate(functionArgs, evaluationContext);
		assertEquals(expected, actual);
	}

	@Test
	public void evaluateNominalWithCacheTest() {
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";
		StringBuffer tokenBuffer = new StringBuffer();
		tokenBuffer.append("{\"session_token\":\"fake_token\"}");
		String expected = "Status=used Type=desktop Processor=null Manufacturer=msi Model=null Serialnumber= OSName=null Lastupdate=2019-09-12-13:59:50 Name=xwing.tuxtrooper.com Location=Home";
		String inCache = "Status=used Type=desktop Processor=null Manufacturer=msi Model=null Serialnumber= OSName=null Lastupdate=2019-09-12-13:59:50 Name=xwing.tuxtrooper.com Location=Home";

		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);

		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);

		Cache<String, String> myCache = cacheManager.getCache("myCache", String.class, String.class);
		myCache.put(responseQuery, inCache);
		when(connection.getResponseStream()).thenReturn(tokenBuffer);
		when(session.getSessionTokenFromAPI(any(GLPIConnection.class))).thenReturn("fake_session_token");

		String actual = plugin.evaluate(functionArgs, evaluationContext);
		assertEquals(expected, actual);
	}

	@Test
	public void evaluateNoTokenSessionTest() {
		String expected = "";
		String responseQuery = "vwing.tuxtrooper.com";
		String responseType = "Computer";
		String responseFilter = "";

		StringBuffer tokenBuffer = new StringBuffer();
		tokenBuffer.append("{\"session_token\":\"\"}");

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
		String expected = "GLPI=noConfig";

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
		String expected = "GLPI=noResult";

		when(clusterConfig.get(GLPIPluginConfiguration.class)).thenReturn(config);

		when(queryParam.required(functionArgs, evaluationContext)).thenReturn(responseQuery);
		when(typeParam.required(functionArgs, evaluationContext)).thenReturn(responseType);
		when(filterParam.required(functionArgs, evaluationContext)).thenReturn(responseFilter);

		when(connection.getResponseStream()).thenReturn(tokenBuffer);
		when(session.getSessionTokenFromAPI(any(GLPIConnection.class))).thenReturn("fake_session_token");
		when(session.getSearchFromAPI(connection, responseType, responseQuery, responseFilter, null)).thenReturn(response);

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
		String expected = "Status=used Type=desktop Processor=null Manufacturer=msi Model=null Serialnumber= OSName=null Lastupdate=2019-09-12-13:59:50 Name=xwing.tuxtrooper.com Location=Home";

		assertEquals(expected, plugin.mapToString(response));
	}
}
