package com.demo.thebankingproject.service;

import com.demo.thebankingproject.dto.EmailDetails;

public interface EmailService {
	void sendEmailAlert(EmailDetails emailDetails);
	void sendEmailWithAttachment(EmailDetails emailDetails);
}
