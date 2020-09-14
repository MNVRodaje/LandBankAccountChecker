package com.svi.accountchecker.utils;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private static String DATE_FORMAT = "MM/dd/yyyy";
	public static Map<String, Integer> infoHeaderMap ;
	private Map<String, Integer> docHeaderMap ;

	private ExcelReader() {
	}

	private static class LazyHolder {
		private static final ExcelReader INSTANCE = new ExcelReader();
	}

	public static ExcelReader getInstance() {
		return LazyHolder.INSTANCE;
	}

	public static List<LinkedHashMap<String, Object>> readExcel(InputStream excelFile, int rowstart) {
		List<LinkedHashMap<String, Object>> dataToLoad = new ArrayList<>();
		try {

			Workbook workbook = WorkbookFactory.create(excelFile);
			Map<String,Integer> headerMap = new HashMap<String, Integer>();

			headerMap = infoHeaderMap;
			if (workbook instanceof HSSFWorkbook) {
				HSSFWorkbook excelBook = (HSSFWorkbook) workbook;
				HSSFSheet excelSheet = excelBook.getSheetAt(0);
				for (int currentRowIdx = rowstart, rowCount = excelSheet.getLastRowNum() + 1; currentRowIdx < rowCount; currentRowIdx++) {
					HSSFRow recordRow = excelSheet.getRow(currentRowIdx);
					if(recordRow!=null){
						LinkedHashMap<String, Object> data = new LinkedHashMap<>();
						for (Entry<String, Integer> temp : headerMap.entrySet()) {
							Cell cell = recordRow.getCell(temp.getValue());
							if (cell != null  && cell.getCellTypeEnum() != CellType.BLANK) {								
								if(cell.getCellTypeEnum() == CellType.NUMERIC){
									if(DateUtil.isCellDateFormatted(cell)){
										DateFormat df = new SimpleDateFormat(DATE_FORMAT);

									    Date date = cell.getDateCellValue();       
										
									    String dateString = df.format(date);
									    data.put(temp.getKey(), dateString);
									} else{
										cell.setCellType(CellType.STRING);
										String cellVal = cell.getStringCellValue().trim();
										if (StringUtils.isNotBlank(cellVal)) {
											data.put(temp.getKey(), cellVal);
										}
									}
								}else if(cell.getCellTypeEnum() == CellType.BOOLEAN){
									data.put(temp.getKey(), cell.getBooleanCellValue());
								} else{
									cell.setCellType(CellType.STRING);
									String cellVal = cell.getStringCellValue().trim();
									if (StringUtils.isNotBlank(cellVal)) {
										data.put(temp.getKey(), cellVal);
									}
								}
							} else{
								data.put(temp.getKey(), "");
							}
						}
						if (!data.isEmpty()) {
							dataToLoad.add(data);
						}
					}
					
				}
				excelBook.close();
			} else if (workbook instanceof XSSFWorkbook) {
				XSSFWorkbook excelBook = (XSSFWorkbook) workbook;
				XSSFSheet excelSheet = excelBook.getSheetAt(0);
				for (int currentRowIdx = rowstart, rowCount = excelSheet.getLastRowNum() + 1; currentRowIdx < rowCount; currentRowIdx++) {
					XSSFRow recordRow = excelSheet.getRow(currentRowIdx);
					if(recordRow!=null){
						LinkedHashMap<String, Object> data = new LinkedHashMap<>();
						for (Entry<String, Integer> temp : headerMap.entrySet()) {
							Cell cell = recordRow.getCell(temp.getValue());
							if (cell != null  && cell.getCellTypeEnum() != CellType.BLANK) {								
								if(cell.getCellTypeEnum() == CellType.NUMERIC){
									if(DateUtil.isCellDateFormatted(cell)){
										DateFormat df = new SimpleDateFormat(DATE_FORMAT);

									    Date date = cell.getDateCellValue();       
										
									    String dateString = df.format(date);
									    data.put(temp.getKey(), dateString);
									} else{
										cell.setCellType(CellType.STRING);
										String cellVal = cell.getStringCellValue().trim();
										if (StringUtils.isNotBlank(cellVal)) {
											data.put(temp.getKey(), cellVal);
										}
									}
								}else if(cell.getCellTypeEnum() == CellType.BOOLEAN){
									data.put(temp.getKey(), cell.getBooleanCellValue());
								}  else{
									cell.setCellType(CellType.STRING);
									String cellVal = cell.getStringCellValue().trim();
									if (StringUtils.isNotBlank(cellVal)) {
										data.put(temp.getKey(), cellVal);
									}
								}
							} else{
								data.put(temp.getKey(), "");
							}
						}
						if (!data.isEmpty()) {
							dataToLoad.add(data);
						}
					}
				}
				excelBook.close();
			} else {
				throw new Exception("Invalid File!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataToLoad;
	}

	public Map<String, Integer> getDocHeaderMap() {
		return docHeaderMap;
	}

	public void setDocHeaderMap(Map<String, Integer> docHeaderMap) {
		this.docHeaderMap = docHeaderMap;
	}

	public Map<String, Integer> getInfoHeaderMap() {
		return infoHeaderMap;
	}

	public void setInfoHeaderMap(Map<String, Integer> infoHeaderMap) {
		ExcelReader.infoHeaderMap = infoHeaderMap;
	}
}

