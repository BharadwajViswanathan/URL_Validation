package com.testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

public class Pageload_TC {

    // Map to store validation results and values for each URL
    private static Map<String, List<String>> resultsMap = new HashMap<>();
    private static Map<String, List<String>> valueMap = new HashMap<>();

    // List of standard keys to search for
    private static final String[] STANDARD_KEYS = {
            "pageName", "serviceProfileId", "pageType", "siteSection", "trackingId", "appVersion",
            "cvsdkVersion", "deviceModel", "deviceType", "platform", "rsid", "territory",
            "countryCode", "loginStatus", "screenOrientation", "userEntitlement", "customerType",
            "dayHourMinute", "profileSetting", "mpsessionId", "daid"
    };

    // List to store all URLs for headers
    private static List<String> urlList = new ArrayList<>();

    @Test(dataProviderClass = Pageload_Dataprovider.class, dataProvider = "URL")
    public static void urlValidation(String url) {
        urlList.add(url); // Store the URL for use as header

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
                String result = value.equals("null") ? "Fail" : "Pass";
                resultsMap.get(key).add(result);
                valueMap.get(key).add(value);
            } else {
                resultsMap.get(key).add("Fail");
                valueMap.get(key).add("null");
            }
        }
    }

    @Test(dependsOnMethods = "urlValidation")
    public static void printResults() throws IOException {
        Workbook workbook;
        Sheet sheet;

        File file = new File("./URL_Validation_Results.xlsx");

        if (file.exists() && file.length() > 0) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook(); // Create a new workbook if the file is empty
            }
        } else {
            workbook = new XSSFWorkbook(); // Create a new workbook if the file doesn't exist
        }

        // Check if the "PageLoad" sheet exists, and create it if it doesn't
        sheet = workbook.getSheet("PageLoad");
        if (sheet == null) {
            sheet = workbook.createSheet("PageLoad");
        }

        // Create header row
        Row headerRow = sheet.createRow(0);
        Cell keyHeaderCell = headerRow.createCell(0);
        keyHeaderCell.setCellValue("Key");

        int colNum = 1;
        for (String url : urlList) {
            // Column for the values
            Cell valueHeaderCell = headerRow.createCell(colNum++);
            valueHeaderCell.setCellValue("Values");

            // Column for the pass/fail status
            Cell statusHeaderCell = headerRow.createCell(colNum++);
            statusHeaderCell.setCellValue(url);
        }

        CellStyle failStyle = workbook.createCellStyle();
        Font failFont = workbook.createFont();
        failFont.setColor((short) Font.COLOR_RED); // Red
        failStyle.setFont(failFont);

        // Fill in the data for each key
        int rowNum = 1;
        for (String key : STANDARD_KEYS) {
            Row row = sheet.createRow(rowNum++);
            Cell keyCell = row.createCell(0);
            keyCell.setCellValue(key);

            colNum = 1;
            for (int i = 0; i < urlList.size(); i++) {
                Cell valueCell = row.createCell(colNum++);
                if (valueMap.get(key).get(i).equalsIgnoreCase("null")) {
                    valueCell.setCellValue(valueMap.get(key).get(i));
                    valueCell.setCellStyle(failStyle);
                } else {
                    valueCell.setCellValue(valueMap.get(key).get(i));
                }

                Cell statusCell = row.createCell(colNum++);
                String status = resultsMap.get(key).get(i);
                statusCell.setCellValue(status);

                // Apply the appropriate style based on the status
                if (!"Pass".equals(status)) {
                    statusCell.setCellStyle(failStyle);
                }
            }
        }

        // Auto-size columns for better readability
        for (int i = 0; i < urlList.size() * 2 + 1; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}
