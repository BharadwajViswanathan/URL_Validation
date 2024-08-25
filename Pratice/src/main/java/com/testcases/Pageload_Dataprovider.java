package com.testcases;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
public class Pageload_Dataprovider {

	
		@DataProvider(name = "URL")
		public static String[][] Dataprovider() throws IOException{
			File file=new File("./src/main/resources/URLValidations.xlsx");
			FileInputStream fis=new FileInputStream(file);
			XSSFWorkbook workbook=new XSSFWorkbook(fis);
			XSSFSheet sheet=workbook.getSheet("Pageload");

			int rows=sheet.getLastRowNum();

			int col=sheet.getRow(1).getLastCellNum();
//			System.out.println("Number of rows present on the File: "+rows);
//			System.out.println("Number of Columns present on the File: "+col);

			String[][] data =new String[rows][col];

			for(int i=1;i<=rows;i++) {
				XSSFRow row=sheet.getRow(i);

				for(int j=0;j<col;j++) {
					XSSFCell Cell=row.getCell(j);
					DataFormatter dataformat=new DataFormatter();
					String Value=dataformat.formatCellValue(Cell);
					data[i-1][j]=Value;
				}
			}

			return data;
		}		

	}


