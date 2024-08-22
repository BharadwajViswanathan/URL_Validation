package com.testcases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class URL_Verification {

	// ANSI escape codes for colored text
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";

	// Check and cross marks for validation
	public static final String CHECK_MARK = ANSI_GREEN + "✔" + ANSI_RESET;
	public static final String CROSS_MARK = ANSI_RED + "✘" + ANSI_RESET;

	// Map to store validation results and values for each URL
	private static Map<String, List<String>> resultsMap = new HashMap<>();
	private static Map<String, List<String>> valueMap = new HashMap<>();

	// List of standard keys to search for
	private static final String[] STANDARD_KEYS = { "pageName", "serviceProfileId", "pageType", "siteSection", "trackingId",
			"appVersion", "cvsdkVersion", "deviceModel", "deviceType", "platform", "rsid", "territory", "countryCode", "loginStatus",
			"screenOrientation", "userEntitlement", "customerType", "dayhourminute", "profileSetting", "mpsessionId", "daid" };
	

	@Test(dataProviderClass = Dataprovider.class, dataProvider = "URL")
	public static void urlvalidaton(String sno, String url) {
		String queryString = url;

		// Split the query string into key-value pairs
		String[] pairs = queryString.split("&");

		// Map to store the key-value pairs
		Map<String, String> fieldsMap = new HashMap<>();

		for (String pair : pairs) {
			// Split each pair by the first '=' character
			String[] keyValue = pair.split("=", 2);

			// If there's a value after '=', add to the map, otherwise store "null"
			if (keyValue.length == 2) {
				fieldsMap.put(keyValue[0], keyValue[1]);
			} else {
				fieldsMap.put(keyValue[0], "null");
			}
		}

		// Store results and values for each standard key
		for (String key : STANDARD_KEYS) {
			if (!resultsMap.containsKey(key)) {
				resultsMap.put(key, new ArrayList<>());
				valueMap.put(key, new ArrayList<>());
			}

			if (fieldsMap.containsKey(key)) {
				String value = fieldsMap.get(key);
				resultsMap.get(key).add(value.equals("null") ? CROSS_MARK : CHECK_MARK);
				valueMap.get(key).add(value);
			} else {
				resultsMap.get(key).add(CROSS_MARK);
				valueMap.get(key).add("null");
			}
		}
	}

	@Test(dependsOnMethods = "urlvalidaton")
	public static void printResults() {
		// Determine column widths for proper alignment
		int columnWidth = 25; // Adjust this as needed for your terminal size

		// Print header row
		System.out.printf("%-" + columnWidth + "s", "Name");
		for (int i = 1; i <= resultsMap.get(STANDARD_KEYS[0]).size(); i++) {
			System.out.printf("%-" + columnWidth + "s", "URL" + i);
		}
		System.out.println();

		// Print check/cross marks for each key across URLs
		for (String key : STANDARD_KEYS) {
			System.out.printf("%-" + columnWidth + "s", key);
			for (String result : resultsMap.get(key)) {
				System.out.printf("%-" + columnWidth + "s", result);
			}
			System.out.println();
		}

		System.out.println("\nValues across URLs:");

		// Print the actual values for each key
		for (String key : STANDARD_KEYS) {
			System.out.printf("%-" + columnWidth + "s", key);
			for (String value : valueMap.get(key)) {
				if (value.equals("null")) {
					System.out.printf("%-" + columnWidth + "s", ANSI_RED + "Value Missing" + ANSI_RESET);
				} else {
					System.out.printf("%-" + columnWidth + "s", value);
				}
			}
			System.out.println();
		}
	}
}
