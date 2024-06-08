package com.demo.thebankingproject.service;

import java.math.BigDecimal;

import com.demo.thebankingproject.config.JwtTokenProvider;
import com.demo.thebankingproject.dto.*;
import com.demo.thebankingproject.entity.Role;
import com.demo.thebankingproject.entity.Transaction;
import com.demo.thebankingproject.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.thebankingproject.entity.User;
import com.demo.thebankingproject.repository.UserRepository;
import com.demo.thebankingproject.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepository userRepository;
	@Autowired
	EmailService emailService;
	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtTokenProvider jwtTokenProvider;


	@Override
	public BankResponse login(LoginDto loginDto) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
		);
		EmailDetails loginAlert = EmailDetails.builder()
				.subject("You are logged in!")
				.recepient(loginDto.getEmail())
				.messageBody("You have logged in to your account, if you did not initiate this request, please contact bank")
				.build();
		emailService.sendEmailAlert(loginAlert);
		return BankResponse.builder()
				.responseCode("Login Successfull!")
				.responseMessage(jwtTokenProvider.generateToken(authentication)).build();
	}
	@Override
	public BankResponse createAccount(UserRequest userRequest) {
		// TODO Auto-generated method stub
		//creating a new account - saving user details into db
		//check if user already exists
		if(userRepository.existsByEmail(userRequest.getEmail())) {
			return BankResponse.builder().
					responseCode(AccountUtils.ACCOUNT_EXISTS_CODE).
					responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE).
					build();
		}
		User newUser = User.builder().
				firstName(userRequest.getFirstName()).
				lastName(userRequest.getLastName()).
				otherName(userRequest.getOtherName()).
				address(userRequest.getAddress()).
				gender(userRequest.getGender()).
				stateOfOrigin(userRequest.getStateOfOrigin()).
				accountNumber(AccountUtils.accountNumber()).
				accountBalance(BigDecimal.ZERO).
				email(userRequest.getEmail()).
				role(Role.ROLE_ADMIN).
				password(passwordEncoder.encode(userRequest.getPassword())).
				phoneNumber(userRequest.getPhoneNumber()).
				alternatePhoneNumber(userRequest.getAlternatePhoneNumber()).
				status("ACTIVE")
				.build();
		User savedUser = userRepository.save(newUser);
		emailService.sendEmailAlert(EmailDetails.builder().
				recepient(savedUser.getEmail()).
				subject("ACCOUNT CREATION").
				messageBody("Congratulations! your account has been created successfully\n,Your Account Details are \n "
						+"Account Number : "+savedUser.getAccountNumber()+"\nName : "+savedUser.getFirstName()+" "
						+savedUser.getLastName()+" "+savedUser.getOtherName()+"\n")
				
				.build());
		return BankResponse.builder().
				responseCode(AccountUtils.ACCOUNT_CREATION_CODE).
				responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE).
				accountInfo(AccountInfo.builder().
						accountBalance(savedUser.getAccountBalance()).
						accountName(savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName()).
						accountNumber(savedUser.getAccountNumber())
						.build())
				.build();
	}

	@Override
	public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
		// TODO Auto-generated method stub
		// check if provided account number exists from enquiry request
		Boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder().
					responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).
					responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE).
					accountInfo(null)
					.build();
		}
		User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		return BankResponse.builder().
				responseCode(AccountUtils.ACCOUNT_FOUND_CODE).
				responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE).
				accountInfo(AccountInfo.builder().
						accountBalance(foundUser.getAccountBalance()).
						accountNumber(foundUser.getAccountNumber()).
						accountName(foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName()).
						build()).
				build();
	}

	@Override
	public String nameEnquiry(EnquiryRequest enquiryRequest) {
		// TODO Auto-generated method stub
		Boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
		}
		User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		return foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName();
	}

	@Override
	public BankResponse creditAccount(CreditAndDebitRequest creditAndDebitRequest) {
		// TODO Auto-generated method stub
		Boolean isAccountExists = userRepository.existsByAccountNumber(creditAndDebitRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder().
					responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).
					responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE).
					accountInfo(null)
					.build();
		}
		User userToBeCredited = userRepository.findByAccountNumber(creditAndDebitRequest.getAccountNumber());
		userToBeCredited.setAccountBalance(userToBeCredited.getAccountBalance().add(creditAndDebitRequest.getAmount()));
		userRepository.save(userToBeCredited);
		Transaction transaction = Transaction.builder()
				.transactionType("CREDIT")
				.amount(creditAndDebitRequest.getAmount())
				.accountNumber(creditAndDebitRequest.getAccountNumber())
				.status("SUCCESS")
				.build();
		transactionRepository.save(transaction);
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
				.responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(userToBeCredited.getAccountBalance())
						.accountNumber(userToBeCredited.getAccountNumber())
						.accountName(userToBeCredited.getFirstName()+" "+userToBeCredited.getLastName()+" "+userToBeCredited.getOtherName())
						.build())
				.build();
	}

	@Override
	public BankResponse debitAccount(CreditAndDebitRequest creditAndDebitRequest) {
		// TODO Auto-generated method stub
		//checking if account exists in db
		Boolean isAccountExists = userRepository.existsByAccountNumber(creditAndDebitRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder().
					responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).
					responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE).
					accountInfo(null)
					.build();
		}
		//checking if current balance is greater than withdrawl amount
		
		User userDebitedFrom = userRepository.findByAccountNumber(creditAndDebitRequest.getAccountNumber());
		BigDecimal availableBalance = userDebitedFrom.getAccountBalance();
		BigDecimal withdrawAmount = creditAndDebitRequest.getAmount();
		if(availableBalance.compareTo(withdrawAmount)==-1) {
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
					.responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
					.accountInfo(AccountInfo.builder()
							.accountBalance(userDebitedFrom.getAccountBalance())
							.accountNumber(userDebitedFrom.getAccountNumber())
							.accountName(userDebitedFrom.getFirstName()+" "+userDebitedFrom.getLastName()+" "+userDebitedFrom.getOtherName())
							.build())
					.build();
		}
		userDebitedFrom.setAccountBalance(userDebitedFrom.getAccountBalance().subtract(creditAndDebitRequest.getAmount()));
		userRepository.save(userDebitedFrom);
		Transaction transaction = Transaction.builder()
				.transactionType("DEBIT")
				.amount(creditAndDebitRequest.getAmount())
				.accountNumber(creditAndDebitRequest.getAccountNumber())
				.status("SUCCESS")
				.build();
		transactionRepository.save(transaction);
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
				.responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(userDebitedFrom.getAccountBalance())
						.accountNumber(userDebitedFrom.getAccountNumber())
						.accountName(userDebitedFrom.getFirstName()+" "+userDebitedFrom.getLastName()+" "+userDebitedFrom.getOtherName())
						.build())
				.build();
	}

	@Override
	public BankResponse transfer(TransferRequest transferRequest) {
		// TODO Auto-generated method stub
		//check if both source and destination account exists
		//check if source account has the balance less than or equal to credit amount
		Boolean isSourceAccountExist = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
		Boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
		if(!isSourceAccountExist || !isDestinationAccountExist) {
			return BankResponse.builder().
					responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).
					responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE).
					accountInfo(null)
					.build();
		}
		User sourceUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
		User destinationUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
		BigDecimal availableBalance = sourceUser.getAccountBalance();
		BigDecimal creditAmount = transferRequest.getAmount();
		
		if(availableBalance.compareTo(creditAmount)==-1) {
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
					.responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
					.accountInfo(AccountInfo.builder()
							.accountBalance(sourceUser.getAccountBalance())
							.accountNumber(sourceUser.getAccountNumber())
							.accountName(sourceUser.getFirstName()+" "+sourceUser.getLastName()+" "+sourceUser.getOtherName())
							.build())
					.build();
		}
		sourceUser.setAccountBalance(sourceUser.getAccountBalance().subtract(transferRequest.getAmount()));
		destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(transferRequest.getAmount()));
		userRepository.save(sourceUser);
		Transaction debitTransaction = Transaction.builder()
				.transactionType("DEBIT")
				.amount(transferRequest.getAmount())
				.accountNumber(transferRequest.getSourceAccountNumber())
				.status("SUCCESS")
				.build();
		transactionRepository.save(debitTransaction);
		userRepository.save(destinationUser);
		Transaction creditTransaction = Transaction.builder()
				.transactionType("CREDIT")
				.amount(transferRequest.getAmount())
				.accountNumber(transferRequest.getDestinationAccountNumber())
				.status("SUCCESS")
				.build();
		transactionRepository.save(creditTransaction);
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_CODE)
				.responseMessage(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE)
						.transferInfo(TransferInfo.builder()
								.sourceAccountInfo(AccountInfo.builder()
										.accountBalance(sourceUser.getAccountBalance())
										.accountNumber(sourceUser.getAccountNumber())
										.accountName(sourceUser.getFirstName()+" "+sourceUser.getLastName()+" "+sourceUser.getOtherName())
										.build())
								.destinationAccountInfo(AccountInfo.builder()
										.accountBalance(destinationUser.getAccountBalance())
										.accountNumber(destinationUser.getAccountNumber())
										.accountName(destinationUser.getFirstName()+" "+destinationUser.getLastName()+" "+destinationUser.getOtherName())
										.build())
								.amount(transferRequest.getAmount())
								.build())
						.build();
	}



}
