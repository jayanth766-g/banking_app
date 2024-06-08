package com.demo.thebankingproject.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditAndDebitRequest {
	private String accountNumber;
	private BigDecimal amount;
}
