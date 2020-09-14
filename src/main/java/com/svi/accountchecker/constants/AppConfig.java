package com.svi.accountchecker.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum AppConfig {
	MASTERLIST_PATH("MASTERLIST_PATH"),
	ACCOUNT_PDF_PATH("ACCOUNT_PDF_PATH"), 
	REPORTS_PATH("REPORTS_PATH"),
	ACCOUNT_FILE_EXTENSION("ACCOUNT_FILE_EXTENSION"),
	ACCOUNT_LIST_ROW_START("ACCOUNT_LIST_ROW_START"),
	ACCOUNT_NO_COL_INDX("ACCOUNT_NO_COL_INDX"),
	ACCOUNT_NAME_COL_INDX("ACCOUNT_NAME_COL_INDX");
	
	private String value = "";
	private static Properties prop;

	private AppConfig(String value) {
		this.value = value;
	}

	public String value() {
		return prop.getProperty(value).trim();
	}

	public static void setContext(InputStream inputStream) {
		synchronized (inputStream) {
			if (prop == null) {
				try {
					prop = new Properties();
					prop.load(inputStream);
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					try {
						inputStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
