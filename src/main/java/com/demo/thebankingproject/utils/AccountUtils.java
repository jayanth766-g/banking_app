package com.demo.thebankingproject.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {
	//account number - current year + random 6 digit number
	public static final String ACCOUNT_EXISTS_CODE="001";
	public static final String ACCOUNT_EXISTS_MESSAGE="User Account alreay exists";
	public static final String ACCOUNT_CREATION_CODE="002";
	public static final String ACCOUNT_CREATION_MESSAGE="User Account is succesfully created";
	public static final String ACCOUNT_NOT_EXISTS_CODE="003";
	public static final String ACCOUNT_NOT_EXISTS_MESSAGE="User with provided account number does not exist";
	public static final String ACCOUNT_FOUND_CODE="004";
	public static final String ACCOUNT_FOUND_MESSAGE="User with provided account number is found";
	public static final String ACCOUNT_CREDITED_SUCCESS_CODE="005";
	public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE="User account is credited with requested amount successfully";
	public static final String INSUFFICIENT_BALANCE_CODE="006";
	public static final String INSUFFICIENT_BALANCE_MESSAGE="Insufficient balance";
	public static final String ACCOUNT_DEBITED_SUCCESS_CODE="007";
	public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE="User account is debited with requested amount successfully";
	public static final String ACCOUNT_TRANSFER_SUCCESS_CODE="008";
	public static final String ACCOUNT_TRANSFER_SUCCESS_MESSAGE="amount is transferred from source account to destination account successfully";
	
	public static String accountNumber() {
		Year currentYear = Year.now();
		int number = new Random().nextInt(999999);
		String trail = String.format("%06d", number);
		String year=String.valueOf(currentYear);
		return year+trail;
	}
}
