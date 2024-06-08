package com.demo.thebankingproject.service;

import com.demo.thebankingproject.dto.*;

public interface UserService {
	BankResponse createAccount(UserRequest userRequest);
	BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
	String nameEnquiry(EnquiryRequest enquiryRequest);
	BankResponse creditAccount(CreditAndDebitRequest creditAndDebitRequest);
	BankResponse debitAccount(CreditAndDebitRequest creditAndDebitRequest);
	BankResponse transfer(TransferRequest trasnsferRequest);
	BankResponse login(LoginDto loginDto);
}
