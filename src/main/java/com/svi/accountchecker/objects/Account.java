package com.svi.accountchecker.objects;

import com.svi.accountchecker.constants.AccountClassAndType;
import com.svi.accountchecker.constants.AccountTag;

public class Account {

	public static String ACCOUNT_NUMBER = "Account Number";
	public static String ACCOUNT_NAME = "Account Name";
	
	private String accountName;
	private String accountNumber;
	private AccountClassAndType accountClassAndType;
	private AccountTag accountTag;
	
	public Account() {
		super();
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public AccountTag getAccountTag() {
		return accountTag;
	}

	public void setAccountTag(AccountTag accountTag) {
		this.accountTag = accountTag;
	}

	public AccountClassAndType getAccountClassAndType() {
		return accountClassAndType;
	}

	public void setAccountClassAndType(AccountClassAndType accountClassAndType) {
		this.accountClassAndType = accountClassAndType;
	}

}
