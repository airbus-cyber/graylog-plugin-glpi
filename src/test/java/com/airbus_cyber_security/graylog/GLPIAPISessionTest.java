/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

import static org.junit.Assert.assertEquals;
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
		Map<String, String> actual = new HashMap<>();
		Map<String, String> expected = new HashMap<>();
		Map<String, String> apiResult = new HashMap<>();

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
	public void mappingFieldWithUnknownFieldTest() {
		String filter = "";
		Map<String, String> actual = new HashMap<>();
		Map<String, String> expected = new HashMap<>();
		Map<String, String> apiResult = new HashMap<>();

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
		apiResult.put("56789", "Unknown field");

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
		expected.put("56789", "Unknown field");

		actual = session.mappingField(apiResult, GLPIAPISession.getComputerTranslationMatrix(), filter);

		assertEquals(expected, actual);
	}

	@Test
	public void mappingFieldWithFilterTest() {
		GLPIAPISession session = new GLPIAPISession();
		String filter = "name,osname";
		Map<String, String> actual = new HashMap<>();
		Map<String, String> expected = new HashMap<>();
		Map<String, String> apiResult = new HashMap<>();

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
	public void getSearchFromAPICommputerTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		String sessionToken = "hfuisfjoijoqdza,doia";
		String category = "Computer";
		String search = "vwing.tuxtrooper.com";
		String filter = "";
		StringBuffer response = new StringBuffer();
		Map<String, String> expected = new HashMap<>();

		response.append(
				"{\"totalcount\":1,\"count\":1,\"sort\":1,\"order\":\"ASC\",\"data\":[{\"1\":\"xwing.tuxtrooper.com\",\"31\":\"used\",\"23\":\"msi\",\"5\":\"\",\"4\":\"desktop\",\"40\":null,\"45\":null,\"3\":\"Home\",\"19\":\"2019-09-12 13:59:50\",\"17\":null}],\"content-range\":\"0-0/1\"}");

		expected.put("Name", "xwing.tuxtrooper.com");
		expected.put("Status", "used");
		expected.put("Manufacturer", "msi");
		expected.put("Serialnumber", "");
		expected.put("Type", "desktop");
		expected.put("Model", "null");
		expected.put("OSName", "null");
		expected.put("Location", "Home");
		expected.put("Lastupdate", "2019-09-12 13:59:50");
		expected.put("Processor", "null");

		session.setApiURL(url);
		session.setUserToken(userToken);
		session.setSessionToken(sessionToken);

		when(connection.getResponseStream()).thenReturn(response);

		Map<String, String> actual = session.getSearchFromAPI(connection, category, search, filter);

		assertEquals(expected, actual);
	}

	@Test
	public void getSearchFromAPIParsingErrorTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		String sessionToken = "hfuisfjoijoqdza,doia";
		String category = "Computer";
		String search = "vwing.tuxtrooper.com";
		String filter = "";
		StringBuffer response = new StringBuffer();
		Map<String, String> expected = new HashMap<>();

		response.append(
				"{\"error:[{\"1\":\"xwing.tuxtrooper.com\",\"31\":\"used\",\"23\":\"msi\",\"5\":\"\",\"4\":\"desktop\",\"40\":null,\"45\":null,\"3\":\"Home\",\"19\":\"2019-09-12 13:59:50\",\"17\":null}],\"content-range\":\"0-0/1\"}");

		session.setApiURL(url);
		session.setUserToken(userToken);
		session.setSessionToken(sessionToken);

		when(connection.getResponseStream()).thenReturn(response);

		Map<String, String> actual = session.getSearchFromAPI(connection, category, search, filter);

		assertEquals(expected, actual);
	}

	@Test
	public void getSearchFromAPISoftwareTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		String sessionToken = "hfuisfjoijoqdza,doia";
		String category = "Software";
		String search = "apache";
		String filter = "";
		StringBuffer response = new StringBuffer();
		Map<String, String> expected = new HashMap<>();

		response.append(
				"{\"totalcount\":1,\"count\":1,\"sort\":1,\"order\":\"ASC\",\"data\":[{\"1\":\"apache\",\"23\":\"oracle\",\"5\":[2.6,2.4],\"4\":[\"Archlinux\",\"CentOS\"],\"72\":1,\"163\":1}],\"content-range\":\"0-0/1\"}");

		expected.put("Numberoflicenses", "1");
		expected.put("Publisher", "oracle");
		expected.put("Operatingsystem", "[Archlinux,CentOS]");
		expected.put("Numberofinstallations", "1");
		expected.put("VersionName", "[2.6,2.4]");
		expected.put("Name", "apache");

		session.setApiURL(url);
		session.setUserToken(userToken);
		session.setSessionToken(sessionToken);

		when(connection.getResponseStream()).thenReturn(response);

		Map<String, String> actual = session.getSearchFromAPI(connection, category, search, filter);

		assertEquals(expected, actual);
	}

	@Test
	public void getSearchFromAPIUserTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		String sessionToken = "hfuisfjoijoqdza,doia";
		String category = "User";
		String search = "apache";
		String filter = "";
		StringBuffer response = new StringBuffer();
		Map<String, String> expected = new HashMap<>();

		response.append(
				"{\"totalcount\":1,\"count\":1,\"sort\":1,\"order\":\"ASC\",\"data\":[{\"1\":\"daisuki.xci\",\"34\":\"XCI\",\"5\":\"fake@fake.com\",\"6\":11234567890,\"3\":\"Home\",\"8\":1}],\"content-range\":\"0-0/1\"}");

		expected.put("Active", "1");
		expected.put("Phone", "11234567890");
		expected.put("Lastname", "XCI");
		expected.put("Login", "daisuki.xci");
		expected.put("Emails", "fake@fake.com");
		expected.put("Location", "Home");

		session.setApiURL(url);
		session.setUserToken(userToken);
		session.setSessionToken(sessionToken);

		when(connection.getResponseStream()).thenReturn(response);

		Map<String, String> actual = session.getSearchFromAPI(connection, category, search, filter);

		assertEquals(expected, actual);
	}

	@Test
	public void getSearchFromAPIUnknownTest() {
		GLPIConnection connection = mock(GLPIConnection.class);
		String url = "http://glpi/api";
		String userToken = "lfljfsfjshufhoqhfoihqushfoiu";
		String sessionToken = "hfuisfjoijoqdza,doia";
		String category = "Unknown";
		String search = "doijza";
		String filter = "";
		StringBuffer response = new StringBuffer();
		Map<String, String> expected = new HashMap<>();

		response.append("{\"totalcount\":1,\"count\":1,\"sort\":1,\"order\":\"ASC\",\"data\":[{}]}");

		session.setApiURL(url);
		session.setUserToken(userToken);
		session.setSessionToken(sessionToken);

		when(connection.getResponseStream()).thenReturn(response);

		Map<String, String> actual = session.getSearchFromAPI(connection, category, search, filter);

		assertEquals(expected, actual);
	}
}
