package com.demo.thebankingproject.controller;

import com.demo.thebankingproject.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.thebankingproject.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account MAnagement APIs")
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@Operation(
			summary="Create a new User Account",
			description = "Creating a new User and assigning an account Number"
			)
	@ApiResponse(
			responseCode = "201",
			description = "Http Status 201 created"
			)
	@PostMapping
	public BankResponse createAccount(@RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}
	@PostMapping("/login")
	public BankResponse login(@RequestBody LoginDto loginDto){
		return userService.login(loginDto);
	}
	@Operation(
			summary="Balance Enquiry",
			description = "provides account balance for given account number"
			)
	@ApiResponse(
			responseCode = "202",
			description = "Http Status 202 success"
			)
	@GetMapping("balanceEnquiry")
	public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.balanceEnquiry(enquiryRequest);
	}
	
	@GetMapping("nameEnquiry")
	public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.nameEnquiry(enquiryRequest);
	}
	
	@PostMapping("credit")
	public BankResponse creditAccount(@RequestBody CreditAndDebitRequest creditAndDebitRequest) {
		return userService.creditAccount(creditAndDebitRequest);
	}
	
	@PostMapping("debit")
	public BankResponse debitAccount(@RequestBody CreditAndDebitRequest creditAndDebitRequest) {
		return userService.debitAccount(creditAndDebitRequest);
	}
	
	@PostMapping("transfer")
	public BankResponse debitAccount(@RequestBody TransferRequest transferRequest) {
		return userService.transfer(transferRequest);
	}
}
