package com.airbus_cyber_security.graylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GLPIAPISessionTest {

	private GLPIAPISession session;

	@Before
	public void setUp() throws Exception {
		session = new GLPIAPISession();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void mappingFieldWithoutFilterTest() {
		String filter = "";
		Map<String, Object> actual = new HashMap<>();
		Map<String, Object> expected = new HashMap<>();
		Map<String, Object> apiResult = new HashMap<>();

		apiResult.put("1", "xwing.tuxtrooper.com");
		apiResult.put("31", "used");
		apiResult.put("23", "msi");
		apiResult.put("5", "");
		apiResult.put("4", "desktop");
		apiResult.put("40", null);
		apiResult.put("45", null);
		apiResult.put("3", "Home");
		apiResult.put("19", "2019-09-12 13:59:50");
		apiResult.put("17", null);

		expected.put("Name", "xwing.tuxtrooper.com");
		expected.put("Status", "used");
		expected.put("Manufacturer", "msi");
		expected.put("Serialnumber", "");
		expected.put("Type", "desktop");
		expected.put("Model", null);
		expected.put("OSName", null);
		expected.put("Location", "Home");
		expected.put("Lastupdate", "2019-09-12 13:59:50");
		expected.put("Processor", null);

		actual = session.mappingField(apiResult, GLPIAPISession.getComputerTranslationMatrix(), filter);

		assertEquals(expected, actual);
	}

	@Test
	public void mappingFieldWithFilterTest() {
		GLPIAPISession session = new GLPIAPISession();
		String filter = "name,osname";
		Map<String, Object> actual = new HashMap<>();
		Map<String, Object> expected = new HashMap<>();
		Map<String, Object> apiResult = new HashMap<>();

		apiResult.put("1", "xwing.tuxtrooper.com");
		apiResult.put("31", "used");
		apiResult.put("23", "msi");
		apiResult.put("5", "");
		apiResult.put("4", "desktop");
		apiResult.put("40", null);
		apiResult.put("45", null);
		apiResult.put("3", "Home");
		apiResult.put("19", "2019-09-12 13:59:50");
		apiResult.put("17", null);

		expected.put("Name", "xwing.tuxtrooper.com");
		expected.put("OSName", null);

		actual = session.mappingField(apiResult, GLPIAPISession.getComputerTranslationMatrix(), filter);

		assertEquals(expected, actual);
	}

	@Test
	public void getSessionTokenFromAPITest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		GLPIAPISession session = new GLPIAPISession();
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		StringBuffer response = new StringBuffer();
		response.append("{\"session_token\":\"o1gcobkuste902s3cflv05ovk5\"}");
		session.setApiURL(url);
		session.setUserToken(userToken);

		when(connection.getResponseStream()).thenReturn(response);

		assertEquals("o1gcobkuste902s3cflv05ovk5", session.getSessionTokenFromAPI(connection));
	}
	
	@Test
	public void getSessionTokenFromAPIFailureTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		GLPIAPISession session = new GLPIAPISession();
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		StringBuffer response = new StringBuffer();
		response.append("{\"session_token_error\":\"o1gcobkuste902s3cflv05ovk5\"}");
		session.setApiURL(url);
		session.setUserToken(userToken);

		when(connection.getResponseStream()).thenReturn(response);

		assertEquals("", session.getSessionTokenFromAPI(connection));
	}

	@Test
	public void getSearchFromAPITest() {
		fail("Not implemented");
	}

	@Test
	public void closeSession() {
		fail("Not implemented");
	}
}
