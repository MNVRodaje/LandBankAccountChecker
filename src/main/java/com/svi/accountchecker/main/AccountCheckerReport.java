package com.svi.accountchecker.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;
import com.svi.accountchecker.constants.AccountClassAndType;
import com.svi.accountchecker.constants.AccountTag;
import com.svi.accountchecker.constants.AppConfig;
import com.svi.accountchecker.objects.Account;
import com.svi.accountchecker.utils.ExcelReader;
import com.svi.accountchecker.utils.Filewalker;

public class AccountCheckerReport {
	public static void main(String[] args) throws IOException {
		// initialize configuration paths
		try {
			AppConfig.setContext(new FileInputStream("config/config.ini"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		List<Account> accountsMasterListFromExcel = new ArrayList<>();
		
		File accountsListFilesLocation = new File(AppConfig.MASTERLIST_PATH.value());
		File accountFilesLocation = new File(AppConfig.ACCOUNT_PDF_PATH.value());
		File generatedReportsLocation = new File(AppConfig.REPORTS_PATH.value());
		int rowstart = Integer.parseInt(AppConfig.ACCOUNT_LIST_ROW_START.value());
		int acctNumberColumnIndex = Integer.parseInt(AppConfig.ACCOUNT_NO_COL_INDX.value());
		int acctNameColumnIndex = Integer.parseInt(AppConfig.ACCOUNT_NAME_COL_INDX.value());
		String accountFileExtension = AppConfig.ACCOUNT_FILE_EXTENSION.value();
		
		Filewalker accountsListLocationFilewalker = new Filewalker();
		accountsListLocationFilewalker.setFileList(accountsListFilesLocation.toString());
		
		List<Filewalker> listOfAccountsLocation = new ArrayList<>();
		String[] paths = accountFilesLocation.toString().split("\\|");
		System.out.println(Arrays.toString(paths));
		for (String path : paths) {
			Filewalker accountsLocationFilewalker = new Filewalker();
			accountsLocationFilewalker.setFileList(path.trim());
			listOfAccountsLocation.add(accountsLocationFilewalker);
		}
		
		
		
		//every excel file from the masterlist directory
		for (File accountsListFilename : accountsListLocationFilewalker.getFileList()) {
			
			if (!accountsListFilename.toString().endsWith(".xlsx")){
				continue;
			}
			
			//Scanning excel file
			Map<String, Integer> mapHeader = new HashMap<>();
			mapHeader.put("Account Number", acctNumberColumnIndex - 1);
			mapHeader.put("Account Name", acctNameColumnIndex - 1);
			
			ExcelReader.infoHeaderMap = mapHeader;
			
			List<Account> currentExcelAccountsList = new ArrayList<>();
			
			InputStream inputStream = new FileInputStream(accountsListFilename);
			
			List<LinkedHashMap<String, Object>> excelFile = ExcelReader.readExcel(inputStream, rowstart);
			
			//each excel's account list
			for (LinkedHashMap<String, Object> row: excelFile) {
				Account account = new Account();
				account.setAccountName((String) row.get(Account.ACCOUNT_NAME));
				account.setAccountNumber((String) row.get(Account.ACCOUNT_NUMBER));
				
				String string = accountsListFilename.toString();
				
				if (string.contains("INDIVIDUAL")) {
					if (string.contains("ACTIVE")) {
						account.setAccountClassAndType(AccountClassAndType.INDIVIDUAL_ACTIVE);
					} else if (string.contains("DORMANT")) {
						account.setAccountClassAndType(AccountClassAndType.INDIVIDUAL_DORMANT);
					} else if (string.contains("CLOSED")) {
						account.setAccountClassAndType(AccountClassAndType.INDIVIDUAL_CLOSED);
					}
				} else if (string.contains("INSTITUTIONAL")) {
					if (string.contains("ACTIVE")) {
						account.setAccountClassAndType(AccountClassAndType.INSTITUTIONAL_ACTIVE);
					} else if (string.contains("DORMANT")) {
						account.setAccountClassAndType(AccountClassAndType.INSTITUTIONAL_DORMANT);
					} else if (string.contains("CLOSED")) {
						account.setAccountClassAndType(AccountClassAndType.INSTITUTIONAL_CLOSED);
					}
				}
				
				currentExcelAccountsList.add(account);
				accountsMasterListFromExcel.add(account);
			}
		}//end for scanning every excel file in the masterlist directory
		
		List<String> accountsListFromPDF = new ArrayList<>();
		//list of account files in pdf
		for (Filewalker filewalker  : listOfAccountsLocation) {
			for (File accountFilename : filewalker.getFileList()) {
				String stringAccountFilename = accountFilename.getName().toString();
				if (!stringAccountFilename.toString().endsWith(accountFileExtension)){
					continue;
				}
				stringAccountFilename = stringAccountFilename.replaceAll("[^\\d]", "");
				
				accountsListFromPDF.add(stringAccountFilename);
			}
		}
		
		
		List<Account> indivActi = new ArrayList<>();
		List<Account> indivDorm = new ArrayList<>();
		List<Account> indivClos = new ArrayList<>();
		List<Account> instiActi = new ArrayList<>();
		List<Account> instiDorm = new ArrayList<>();
		List<Account> instiClos = new ArrayList<>();
		
		for (Account account : accountsMasterListFromExcel) {
			System.out.println(account.getAccountNumber().toString());
		}
		System.out.println("\n");
		for (String string : accountsListFromPDF) {
			System.out.println(string);
		}
		
		
		//compare 
		for (Account account : accountsMasterListFromExcel) { 
			String string1 = account.getAccountNumber().toString();
			
			if(accountsListFromPDF.contains(string1)) {
				account.setAccountTag(AccountTag.PROCESSED);
				System.out.println("FOUND: " + string1);
				System.out.println(account.getAccountClassAndType().toString());
				System.out.println(account.getAccountTag().toString());
			} else {
				account.setAccountTag(AccountTag.MISSING);
			}
			
			AccountClassAndType sw = account.getAccountClassAndType();
			switch(sw){
			case INDIVIDUAL_ACTIVE :
				indivActi.add(account);
				break;
			case INDIVIDUAL_DORMANT :
				indivDorm.add(account);
				break;
			case INDIVIDUAL_CLOSED :
				indivClos.add(account);
				break;
			case INSTITUTIONAL_ACTIVE :
				instiActi.add(account);
				break;
			case INSTITUTIONAL_DORMANT :
				instiDorm.add(account);
				break;
			case INSTITUTIONAL_CLOSED :
				instiClos.add(account);
				break;
			}
		}
		
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INDIVIDUAL ACTIVE ACCOUNTS REPORT.csv", indivActi);
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INDIVIDUAL DORMANT ACCOUNTS REPORT.csv", indivDorm);
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INDIVIDUAL CLOSED ACCOUNTS REPORT.csv", indivClos);
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INSTITUTIONAL ACTIVE ACCOUNTS REPORT.csv", instiActi);
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INSTITUTIONAL DORMANT ACCOUNTS REPORT.csv", instiDorm);
		csvWriteAccountReport(generatedReportsLocation.toString(), "\\INSTITUTIONAL CLOSED ACCOUNTS REPORT.csv", instiClos);
		csvWriteMasterList(generatedReportsLocation.toString(), "\\MASTER LIST.csv", accountsMasterListFromExcel );
	}

	private static void csvWriteMasterList(String path, String filename, List<Account> accountList) {
		File filePath = new File(path); 
		List<Account> processedAccounts = new ArrayList<>(); 
		List<Account> missingAccounts = new ArrayList<>();
	    try {
	        FileWriter outputfile = new FileWriter(filePath + filename);
	        CSVWriter writer = new CSVWriter(outputfile); 
	        
	        for (Account account : accountList) {
	        	if (AccountTag.PROCESSED==account.getAccountTag()) {
					processedAccounts.add(account);
				} else if (AccountTag.MISSING==account.getAccountTag()) {
					missingAccounts.add(account);
				}
	        }
	        
	        
	        String[] header1 = { "Class and Type", "Processed", "Unprocessed", "Total"}; 
	        writer.writeNext(header1);
	        String[] data1 = { "INDIVIDUAL_ACTIVE", String.valueOf(countProcessed(accountList, "INDIVIDUAL_ACTIVE")), 
	        		String.valueOf(countMissing(accountList, "INDIVIDUAL_ACTIVE")), 
	        		String.valueOf(countTotal(accountList, "INDIVIDUAL_ACTIVE"))};
	        writer.writeNext(data1);
	        String[] data2 = { "INDIVIDUAL_DORMANT", String.valueOf(countProcessed(accountList, "INDIVIDUAL_DORMANT")), 
	        		String.valueOf(countMissing(accountList, "INDIVIDUAL_DORMANT")),
	        		String.valueOf(countTotal(accountList, "INDIVIDUAL_DORMANT"))};
	        writer.writeNext(data2);
	        String[] data3 = { "INDIVIDUAL_CLOSED", String.valueOf(countProcessed(accountList, "INDIVIDUAL_CLOSED")), 
	        		String.valueOf(countMissing(accountList, "INDIVIDUAL_CLOSED")),
	        		String.valueOf(countTotal(accountList, "INDIVIDUAL_CLOSED"))};
	        writer.writeNext(data3);
	        String[] data4 = { "INSTITUTIONAL_ACTIVE", String.valueOf(countProcessed(accountList, "INSTITUTIONAL_ACTIVE")), 
	        		String.valueOf(countMissing(accountList, "INSTITUTIONAL_ACTIVE")),
	        		String.valueOf(countTotal(accountList, "INSTITUTIONAL_ACTIVE"))};
	        writer.writeNext(data4);
	        String[] data5 = { "INSTITUTIONUAL_DORMANT", String.valueOf(countProcessed(accountList, "INSTITUTIONUAL_DORMANT")), 
	        		String.valueOf(countMissing(accountList, "INSTITUTIONUAL_DORMANT")),
	        		String.valueOf(countTotal(accountList, "INSTITUTIONUAL_DORMANT"))};
	        writer.writeNext(data5);
	        String[] data6 = { "INSTITUTIONAL_CLOSED", String.valueOf(countProcessed(accountList, "INSTITUTIONAL_CLOSED")), 
	        		String.valueOf(countMissing(accountList, "INSTITUTIONAL_CLOSED")),
	        		String.valueOf(countTotal(accountList, "INSTITUTIONAL_CLOSED"))};
	        writer.writeNext(data6);
	        String[] data7 = {"", String.valueOf(processedAccounts.size()), String.valueOf(missingAccounts.size()), 
	        		String.valueOf(accountList.size())};
	        writer.writeNext(data7);
	        
	        String[] blank2 = {""};
	        writer.writeNext(blank2);
	        String[] header3 = { "Account Number", "Account Name", "Tag"}; 
	        writer.writeNext(header3);
	        
	        for (Account account : missingAccounts) {
	        	String[] data = { account.getAccountNumber(), account.getAccountName(), account.getAccountTag().toString()}; 
		        writer.writeNext(data);
	        }
	        writer.close(); 
	    } 
	    catch (IOException e) {
	        e.printStackTrace(); 
	    }
		
	}
	
	private static int countTotal(List<Account> accountList, String string) {
		int total = 0;
		for (Account account : accountList) {
			if(account.getAccountClassAndType().toString().contains(string)) {
				total++;
			}
		}
		return total;
	}

	private static int countProcessed(List<Account> accountList, String string) {
		int total = 0;
		for (Account account : accountList) {
			if(account.getAccountClassAndType().toString().contains(string)) {
				if (AccountTag.PROCESSED==account.getAccountTag()) {
					total++;
				}
			}
		}
		return total;
	}
	private static int countMissing(List<Account> accountList, String string) {
		int total = 0;
		for (Account account : accountList) {
			if(account.getAccountClassAndType().toString().contains(string)) {
				if (AccountTag.MISSING==account.getAccountTag()) {
					total++;
				}
			}
		}
		return total;
	}
	
 
	private static Map<String, Integer> countTagType(List<Account> accountList) {
		
		Map<String, Integer> countTagTypeMap = new HashMap<>();
		
		Integer proccesedFiles = 0;
		Integer missingFiles = 0;
		
		for(Account account: accountList) {
			if (AccountTag.PROCESSED==account.getAccountTag()) {
				proccesedFiles++;
			}
			
			 if (AccountTag.MISSING==account.getAccountTag()) {
				missingFiles++;
			}
		}
		
		countTagTypeMap.put(AccountTag.PROCESSED.toString(), proccesedFiles);
		countTagTypeMap.put(AccountTag.MISSING.toString(), missingFiles);
		
		return countTagTypeMap;
	}
	
	private static void csvWriteAccountReport(String path, String filename, List<Account> accountList) {
		
		File filePath = new File(path); 
	    try {
	        FileWriter outputfile = new FileWriter(filePath + filename);
	        CSVWriter writer = new CSVWriter(outputfile);
	        //header
	        String[] header = { "Account Number", "Account Name", "Tag"}; 
	        writer.writeNext(header); 
	        
	        //add every account data
	        for (Account account : accountList) {
	        	String[] data = {account.getAccountNumber().toString(),
	        			account.getAccountName().toString(), 
	        			account.getAccountTag().toString()};
	        	writer.writeNext(data);
	        	
	        }
	        
	        String[] blank = {""};
	        writer.writeNext(blank);
	        
	        String accountClassAndType = accountList.get(0).getAccountClassAndType().toString();
	        String[] accountClassAndType2 = {"Account Class And Type:", accountClassAndType};
	        writer.writeNext(accountClassAndType2);
	        
	        Map<String, Integer> countTypeMap = countTagType(accountList);
	        
	        String[] processedTotal = {"Processed Accounts Total", countTypeMap.get(AccountTag.PROCESSED.toString()).toString()};
	        writer.writeNext(processedTotal);
	        String[] missingTotal = {"Missing Accounts Total", countTypeMap.get(AccountTag.MISSING.toString()).toString()};
	        writer.writeNext(missingTotal);
	        writer.close(); 
	    } 
	    catch (IOException e) {
	        e.printStackTrace(); 
	    }
	}
}