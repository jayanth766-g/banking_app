package com.demo.thebankingproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.demo.thebankingproject.dto.EmailDetails;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
	
	@Autowired
	JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String senderMail;
	@Override
	public void sendEmailAlert(EmailDetails emailDetails) {
		// TODO Auto-generated method stub
		try {
			SimpleMailMessage message=new SimpleMailMessage();
			message.setFrom(senderMail);
			message.setTo(emailDetails.getRecepient());
			message.setText(emailDetails.getMessageBody());
			message.setSubject(emailDetails.getSubject());
			javaMailSender.send(message);
			System.out.println("Email sent successfully");
		}
		catch(MailException e) {
			throw new RuntimeException();
		}
		
	}

	@Override
	public void sendEmailWithAttachment(EmailDetails emailDetails) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;
		try {
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(senderMail);
			mimeMessageHelper.setTo(emailDetails.getRecepient());
			mimeMessageHelper.setText(emailDetails.getMessageBody());
			mimeMessageHelper.setSubject(emailDetails.getSubject());
			FileSystemResource fileSystemResource = new FileSystemResource(new File(emailDetails.getAttachment()));
			mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()),fileSystemResource);
			javaMailSender.send(mimeMessage);

			log.info(fileSystemResource.getFilename()+" has been sent to user email "+emailDetails.getRecepient());
		} catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
