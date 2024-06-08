package com.demo.thebankingproject.dto;

import com.demo.thebankingproject.dto.TransferInfo.TransferInfoBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
	private String responseCode;
	private String responseMessage;
	private AccountInfo accountInfo;
	private TransferInfo transferInfo;
}
