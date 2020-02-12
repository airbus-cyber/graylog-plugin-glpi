/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLPIAPISession {
	private String apiURL;
	private String userToken;
	private String appToken;
	private String sessionToken;
	Logger LOG = LoggerFactory.getLogger(Function.class);

	private static Map<String, String> userTranslationMatrix = new HashMap<>();
	static {
		userTranslationMatrix.put("1", "Login");
		userTranslationMatrix.put("2", "ID");
		userTranslationMatrix.put("3", "Location");
		userTranslationMatrix.put("5", "Emails");
		userTranslationMatrix.put("6", "Phone");
		userTranslationMatrix.put("8", "Active");
		userTranslationMatrix.put("9", "Firstname");
		userTranslationMatrix.put("10", "Phone2");
		userTranslationMatrix.put("11", "Mobilephone");
		userTranslationMatrix.put("13", "Groups");
		userTranslationMatrix.put("14", "Lastlogin");
		userTranslationMatrix.put("15", "Authentication");
		userTranslationMatrix.put("16", "Comments");
		userTranslationMatrix.put("17", "Language");
		userTranslationMatrix.put("19", "Lastupdate");
		userTranslationMatrix.put("20", "Profiles(Entity)");
		userTranslationMatrix.put("21", "UserDN");
		userTranslationMatrix.put("22", "Administrativenumber");
		userTranslationMatrix.put("23", "Lastsynchronization");
		userTranslationMatrix.put("24", "DeleteduserinLDAPdirectory");
		userTranslationMatrix.put("28", "Synchronizationfield");
		userTranslationMatrix.put("30", "LDAPdirectoryforauthentication");
		userTranslationMatrix.put("31", "Emailserverforauthentication");
		userTranslationMatrix.put("34", "Lastname");
		userTranslationMatrix.put("60", "Numberofticketsasrequester");
		userTranslationMatrix.put("61", "Numberofwrittentickets");
		userTranslationMatrix.put("62", "Begindate");
		userTranslationMatrix.put("63", "Enddate");
		userTranslationMatrix.put("64", "Numberofassignedtickets");
		userTranslationMatrix.put("77", "Defaultentity");
		userTranslationMatrix.put("79", "Defaultprofile");
		userTranslationMatrix.put("80", "Entities(Profile)");
		userTranslationMatrix.put("81", "Title");
		userTranslationMatrix.put("82", "Category");
		userTranslationMatrix.put("91", "Buildingnumber");
		userTranslationMatrix.put("92", "Roomnumber");
		userTranslationMatrix.put("93", "Locationcomments");
		userTranslationMatrix.put("101", "Address");
		userTranslationMatrix.put("102", "Postalcode");
		userTranslationMatrix.put("103", "Town");
		userTranslationMatrix.put("104", "State");
		userTranslationMatrix.put("105", "Country");
		userTranslationMatrix.put("119", "Numberofdocuments");
		userTranslationMatrix.put("121", "Creationdate");
		userTranslationMatrix.put("145", "Externallinks");
		userTranslationMatrix.put("150", "Picture");
		userTranslationMatrix.put("998", "Latitude");
		userTranslationMatrix.put("999", "Longitude");

	}

	private static Map<String, String> softwareTranslationMatrix = new HashMap<>();
	static {
		softwareTranslationMatrix.put("1", "Name");
		softwareTranslationMatrix.put("2", "ID");
		softwareTranslationMatrix.put("3", "Location");
		softwareTranslationMatrix.put("4", "Operatingsystem");
		softwareTranslationMatrix.put("5", "VersionName");
		softwareTranslationMatrix.put("16", "Comments");
		softwareTranslationMatrix.put("19", "Lastupdate");
		softwareTranslationMatrix.put("23", "Publisher");
		softwareTranslationMatrix.put("24", "Technicianinchargeofthesoftware");
		softwareTranslationMatrix.put("25", "Immobilizationnumber");
		softwareTranslationMatrix.put("26", "Ordernumber");
		softwareTranslationMatrix.put("27", "Deliveryform");
		softwareTranslationMatrix.put("28", "Invoicenumber");
		softwareTranslationMatrix.put("29", "ContractName");
		softwareTranslationMatrix.put("30", "Number");
		softwareTranslationMatrix.put("31", "Status");
		softwareTranslationMatrix.put("37", "Dateofpurchase");
		softwareTranslationMatrix.put("38", "Startupdate");
		softwareTranslationMatrix.put("49", "Groupinchargeofthesoftware");
		softwareTranslationMatrix.put("50", "Budget");
		softwareTranslationMatrix.put("51", "Warrantyduration");
		softwareTranslationMatrix.put("52", "Warrantyinformation");
		softwareTranslationMatrix.put("53", "Supplier");
		softwareTranslationMatrix.put("54", "Value");
		softwareTranslationMatrix.put("55", "Warrantyextensionvalue");
		softwareTranslationMatrix.put("56", "Amortizationduration");
		softwareTranslationMatrix.put("57", "Amortizationtype");
		softwareTranslationMatrix.put("58", "Amortizationcoefficient");
		softwareTranslationMatrix.put("59", "Emailalarms");
		softwareTranslationMatrix.put("60", "Numberoftickets");
		softwareTranslationMatrix.put("61", "Associabletoaticket");
		softwareTranslationMatrix.put("62", "Category");
		softwareTranslationMatrix.put("63", "Validlicenses");
		softwareTranslationMatrix.put("70", "User");
		softwareTranslationMatrix.put("71", "Group");
		softwareTranslationMatrix.put("72", "Numberofinstallations");
		softwareTranslationMatrix.put("80", "Entity");
		softwareTranslationMatrix.put("86", "Childentities");
		softwareTranslationMatrix.put("91", "Buildingnumber");
		softwareTranslationMatrix.put("92", "Roomnumber");
		softwareTranslationMatrix.put("93", "Locationcomments");
		softwareTranslationMatrix.put("101", "Address");
		softwareTranslationMatrix.put("102", "Postalcode");
		softwareTranslationMatrix.put("103", "Town");
		softwareTranslationMatrix.put("104", "State");
		softwareTranslationMatrix.put("105", "Country");
		softwareTranslationMatrix.put("119", "Numberofdocuments");
		softwareTranslationMatrix.put("120", "Warrantyexpirationdate");
		softwareTranslationMatrix.put("121", "Creationdate");
		softwareTranslationMatrix.put("122", "Commentsonfinancialandadministrativeinformation");
		softwareTranslationMatrix.put("123", "Startdateofwarranty");
		softwareTranslationMatrix.put("124", "Orderdate");
		softwareTranslationMatrix.put("125", "Dateoflastphysicalinventory");
		softwareTranslationMatrix.put("129", "Type");
		softwareTranslationMatrix.put("130", "Duration");
		softwareTranslationMatrix.put("131", "Periodicity");
		softwareTranslationMatrix.put("132", "Startdate");
		softwareTranslationMatrix.put("133", "Accountnumber");
		softwareTranslationMatrix.put("134", "Enddate");
		softwareTranslationMatrix.put("135", "Notice");
		softwareTranslationMatrix.put("136", "Cost");
		softwareTranslationMatrix.put("137", "Invoiceperiod");
		softwareTranslationMatrix.put("138", "Renewal");
		softwareTranslationMatrix.put("139", "Numberofcontracts");
		softwareTranslationMatrix.put("140", "Numberofproblems");
		softwareTranslationMatrix.put("142", "Deliverydate");
		softwareTranslationMatrix.put("145", "Externallinks");
		softwareTranslationMatrix.put("159", "Decommissiondate");
		softwareTranslationMatrix.put("160", "LicenseName");
		softwareTranslationMatrix.put("161", "Serialnumber");
		softwareTranslationMatrix.put("162", "Inventorynumber");
		softwareTranslationMatrix.put("163", "Numberoflicenses");
		softwareTranslationMatrix.put("164", "Type");
		softwareTranslationMatrix.put("165", "Comments");
		softwareTranslationMatrix.put("166", "Expiration");
		softwareTranslationMatrix.put("167", "Valid");
		softwareTranslationMatrix.put("170", "Comments");
		softwareTranslationMatrix.put("173", "Businesscriticity");
		softwareTranslationMatrix.put("200", "Notes");
		softwareTranslationMatrix.put("201", "Creationdate");
		softwareTranslationMatrix.put("202", "Writer");
		softwareTranslationMatrix.put("203", "Lastupdate");
		softwareTranslationMatrix.put("204", "Lastupdater");
		softwareTranslationMatrix.put("998", "Latitude");
		softwareTranslationMatrix.put("999", "Longitude");

	};

	private static Map<String, String> computerTranslationMatrix = new HashMap<>();
	static {
		computerTranslationMatrix.put("1", "Name");
		computerTranslationMatrix.put("2", "ID");
		computerTranslationMatrix.put("3", "Location");
		computerTranslationMatrix.put("4", "Type");
		computerTranslationMatrix.put("5", "Serialnumber");
		computerTranslationMatrix.put("6", "Inventorynumber");
		computerTranslationMatrix.put("7", "Alternateusername");
		computerTranslationMatrix.put("8", "Alternateusernamenumber");
		computerTranslationMatrix.put("12", "Soundcard");
		computerTranslationMatrix.put("13", "Graphicscard");
		computerTranslationMatrix.put("14", "Systemboard");
		computerTranslationMatrix.put("16", "Comments");
		computerTranslationMatrix.put("17", "Processor");
		computerTranslationMatrix.put("18", "processor:numberofcores");
		computerTranslationMatrix.put("19", "Lastupdate");
		computerTranslationMatrix.put("21", "MACaddress");
		computerTranslationMatrix.put("22", "Ethernetoutlet");
		computerTranslationMatrix.put("23", "Manufacturer");
		computerTranslationMatrix.put("24", "Technicianinchargeofthehardware");
		computerTranslationMatrix.put("25", "Immobilizationnumber");
		computerTranslationMatrix.put("26", "Ordernumber");
		computerTranslationMatrix.put("27", "Deliveryform");
		computerTranslationMatrix.put("28", "Invoicenumber");
		computerTranslationMatrix.put("29", "Name");
		computerTranslationMatrix.put("30", "Number");
		computerTranslationMatrix.put("31", "Status");
		computerTranslationMatrix.put("32", "Network");
		computerTranslationMatrix.put("33", "Domain");
		computerTranslationMatrix.put("34", "processor:numberofthreads");
		computerTranslationMatrix.put("36", "Processorfrequency");
		computerTranslationMatrix.put("37", "Dateofpurchase");
		computerTranslationMatrix.put("38", "Startupdate");
		computerTranslationMatrix.put("39", "Powersupply");
		computerTranslationMatrix.put("40", "Model");
		computerTranslationMatrix.put("41", "Servicepack");
		computerTranslationMatrix.put("42", "UpdateSource");
		computerTranslationMatrix.put("43", "Serialnumber");
		computerTranslationMatrix.put("44", "ProductID");
		computerTranslationMatrix.put("45", "OSName");
		computerTranslationMatrix.put("46", "Version");
		computerTranslationMatrix.put("47", "UUID");
		computerTranslationMatrix.put("48", "Kernelversion");
		computerTranslationMatrix.put("49", "Groupinchargeofthehardware");
		computerTranslationMatrix.put("50", "Budget");
		computerTranslationMatrix.put("51", "Warrantyduration");
		computerTranslationMatrix.put("52", "Warrantyinformation");
		computerTranslationMatrix.put("53", "Supplier");
		computerTranslationMatrix.put("54", "Value");
		computerTranslationMatrix.put("55", "Warrantyextensionvalue");
		computerTranslationMatrix.put("56", "Amortizationduration");
		computerTranslationMatrix.put("57", "Amortizationtype");
		computerTranslationMatrix.put("58", "Amortizationcoefficient");
		computerTranslationMatrix.put("59", "Emailalarms");
		computerTranslationMatrix.put("60", "Numberoftickets");
		computerTranslationMatrix.put("61", "Architecture");
		computerTranslationMatrix.put("62", "Networkfiberoutlet");
		computerTranslationMatrix.put("63", "Edition");
		computerTranslationMatrix.put("64", "Kernel");
		computerTranslationMatrix.put("70", "User");
		computerTranslationMatrix.put("71", "Group");
		computerTranslationMatrix.put("80", "Entity");
		computerTranslationMatrix.put("87", "Networkporttype");
		computerTranslationMatrix.put("88", "VLAN");
		computerTranslationMatrix.put("91", "Buildingnumber");
		computerTranslationMatrix.put("92", "Roomnumber");
		computerTranslationMatrix.put("93", "Locationcomments");
		computerTranslationMatrix.put("101", "Address");
		computerTranslationMatrix.put("102", "Postalcode");
		computerTranslationMatrix.put("103", "Town");
		computerTranslationMatrix.put("104", "State");
		computerTranslationMatrix.put("105", "Country");
		computerTranslationMatrix.put("110", "Memorytype");
		computerTranslationMatrix.put("111", "Memory");
		computerTranslationMatrix.put("112", "Networkinterface");
		computerTranslationMatrix.put("113", "MACaddress");
		computerTranslationMatrix.put("114", "Harddrivetype");
		computerTranslationMatrix.put("115", "Harddrivesize");
		computerTranslationMatrix.put("119", "Numberofdocuments");
		computerTranslationMatrix.put("120", "Warrantyexpirationdate");
		computerTranslationMatrix.put("121", "Creationdate");
		computerTranslationMatrix.put("122", "Commentsonfinancialandadministrativeinformation");
		computerTranslationMatrix.put("123", "Startdateofwarranty");
		computerTranslationMatrix.put("124", "Orderdate");
		computerTranslationMatrix.put("125", "Dateoflastphysicalinventory");
		computerTranslationMatrix.put("126", "IP");
		computerTranslationMatrix.put("127", "Networknames");
		computerTranslationMatrix.put("128", "Networkaliases");
		computerTranslationMatrix.put("129", "Type");
		computerTranslationMatrix.put("130", "Duration");
		computerTranslationMatrix.put("131", "Periodicity");
		computerTranslationMatrix.put("132", "Startdate");
		computerTranslationMatrix.put("133", "Accountnumber");
		computerTranslationMatrix.put("134", "Enddate");
		computerTranslationMatrix.put("135", "Notice");
		computerTranslationMatrix.put("136", "Cost");
		computerTranslationMatrix.put("137", "Invoiceperiod");
		computerTranslationMatrix.put("138", "Renewal");
		computerTranslationMatrix.put("139", "Numberofcontracts");
		computerTranslationMatrix.put("140", "Numberofproblems");
		computerTranslationMatrix.put("142", "Deliverydate");
		computerTranslationMatrix.put("145", "Externallinks");
		computerTranslationMatrix.put("150", "Globalsize");
		computerTranslationMatrix.put("151", "Freesize");
		computerTranslationMatrix.put("152", "Freepercentage");
		computerTranslationMatrix.put("153", "Mountpoint");
		computerTranslationMatrix.put("154", "Partition");
		computerTranslationMatrix.put("155", "Filesystem");
		computerTranslationMatrix.put("156", "Name");
		computerTranslationMatrix.put("157", "Wifinetwork");
		computerTranslationMatrix.put("158", "ESSID");
		computerTranslationMatrix.put("159", "Decommissiondate");
		computerTranslationMatrix.put("160", "Name");
		computerTranslationMatrix.put("161", "State");
		computerTranslationMatrix.put("162", "Virtualizationmodel");
		computerTranslationMatrix.put("163", "Virtualizationsystem");
		computerTranslationMatrix.put("164", "processornumber");
		computerTranslationMatrix.put("165", "Memory");
		computerTranslationMatrix.put("166", "UUID");
		computerTranslationMatrix.put("167", "Name");
		computerTranslationMatrix.put("168", "Version");
		computerTranslationMatrix.put("169", "Active");
		computerTranslationMatrix.put("170", "Isuptodate");
		computerTranslationMatrix.put("171", "Signaturedatabaseversion");
		computerTranslationMatrix.put("172", "Expirationdate");
		computerTranslationMatrix.put("173", "Businesscriticity");
		computerTranslationMatrix.put("200", "Notes");
		computerTranslationMatrix.put("201", "Creationdate");
		computerTranslationMatrix.put("202", "Writer");
		computerTranslationMatrix.put("203", "Lastupdate");
		computerTranslationMatrix.put("204", "Lastupdater");
		computerTranslationMatrix.put("998", "Latitude");
		computerTranslationMatrix.put("999", "Longitude");
	};

	protected static Map<String, String> getUserTranslationMatrix() {
		return userTranslationMatrix;
	}

	protected static void setUserTranslationMatrix(Map<String, String> userTranslationMatrix) {
		GLPIAPISession.userTranslationMatrix = userTranslationMatrix;
	}

	protected static Map<String, String> getSoftwareTranslationMatrix() {
		return softwareTranslationMatrix;
	}

	protected static void setSoftwareTranslationMatrix(Map<String, String> softwareTranslationMatrix) {
		GLPIAPISession.softwareTranslationMatrix = softwareTranslationMatrix;
	}

	protected static Map<String, String> getComputerTranslationMatrix() {
		return computerTranslationMatrix;
	}

	protected static void setComputerTranslationMatrix(Map<String, String> computerTranslationMatrix) {
		GLPIAPISession.computerTranslationMatrix = computerTranslationMatrix;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public Map<String, String> mappingField(Map<String, String> map, Map<String, String> translation, String filter) {
		String[] filterArray = filter.toLowerCase().split(",");
		Map<String, String> mappedMap = new HashMap<>(map.size());

		for (Entry<String, String> entry : map.entrySet()) {
			if (translation.containsKey((entry.getKey()))) {
				if (Arrays.stream(filterArray).noneMatch(translation.get(entry.getKey()).toLowerCase()::equals)
						&& !filter.equals("")) {
					LOG.info("GLPI: The key {} is not into filter {}", translation.get(entry.getKey()), filter);
					continue;
				}
				LOG.info("GLPI: The key {} is into filter {}, translate it", translation.get(entry.getKey()), filter);
				mappedMap.put(translation.get(entry.getKey()), entry.getValue());
			} else {
				LOG.warn("GLPI: The key {} is not into the translation matrix", entry.getKey());
				mappedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return mappedMap;
	}

	public String getSessionTokenFromAPI(GLPIConnection connection) {
		LOG.info("GLPI: Getting session token");
		connection.connectToURL(this.getApiURL() + "/initSession", this.userToken, this.appToken);
		try (JsonReader reader = Json.createReader(new StringReader(connection.getResponseStream().toString()))) {
			JsonObject jsonObject = reader.readObject();
			sessionToken = jsonObject.get("session_token").toString().replaceAll("\"", "");
			LOG.info("GLPI: Session token: {}", sessionToken);
			return sessionToken;
		} catch (Exception e) {
			LOG.error("GLPI: Impossible to parse {} to get session token", connection.getResponseStream());
		}
		return "";
	}

	public Map<String, String> getSearchFromAPI(GLPIConnection connection, String category, String search, String filter) {
		Map<String, String> resultList = new HashMap<>();
		Map<String, String> blankList = new HashMap<>();
		Map<String, String> translationMatrix = null;
		String searchURL = this.getApiURL() + "/search/" + category
				+ "?criteria[0][field]=1&criteria[0][searchtype]=contains&criteria[0][value]=" + search;

		LOG.info("GLPI: request URL: {}", searchURL);
		connection.connectToURL(searchURL, this.userToken, this.appToken, this.sessionToken);

		// Interpret JSON and put it in Map
		try (JsonReader reader = Json.createReader(new StringReader(connection.getResponseStream().toString()))) {
			JsonObject jsonObject = reader.readObject();
			for (Entry<String, JsonValue> i : jsonObject.getValue("/data").asJsonArray().get(0).asJsonObject()
					.entrySet()) {
				LOG.info("GLPI: search key {} => search response: {}", i.getKey(), i.getValue());
				resultList.put(i.getKey(), i.getValue().toString().replace("\"", ""));
			}
		} catch (Exception e) {
			LOG.error("GLPI: Impossible to parse {} into json", connection.getResponseStream());
			return blankList;
		}

		switch (category) {
		case "Computer":
			translationMatrix = computerTranslationMatrix;
			break;
		case "Software":
			translationMatrix = softwareTranslationMatrix;
			break;
		case "User":
			translationMatrix = userTranslationMatrix;
			break;
		default:
			LOG.warn("GLPI: Unsupported category: {}", category);
			return blankList;
		}
		LOG.info("GLPI: translation matrix used {}", category);
		return mappingField(resultList, translationMatrix, filter);
	}

	public void closeSession(GLPIConnection connection) {
		LOG.info("GLPI: closing session");
		connection.connectToURL(this.getApiURL() + "/killSession", this.userToken, this.appToken, this.sessionToken);
	}
}
