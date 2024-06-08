package com.demo.thebankingproject.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferInfo {
	private AccountInfo sourceAccountInfo;
	private AccountInfo destinationAccountInfo;
	private BigDecimal amount;
}
