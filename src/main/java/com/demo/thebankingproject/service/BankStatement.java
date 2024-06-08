package com.demo.thebankingproject.service;

import com.demo.thebankingproject.dto.EmailDetails;
import com.demo.thebankingproject.entity.Transaction;
import com.demo.thebankingproject.entity.User;
import com.demo.thebankingproject.repository.TransactionRepository;
import com.demo.thebankingproject.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    //generate a list of transactions within a specified date range
    //generate a pdf file of transactions
    //send an email

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\Admin\\Downloads\\MyStatement.pdf";

    public List<Transaction> generateBankStatement(String accountNumber, String fromDate, String toDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactions = transactionRepository.findByDateBetween(start,end);
//        List<Transaction> transactions = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
//                .filter(transaction -> transaction.getCreatedAt().isEqual(start))
//                .filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();
        User user = userRepository.findByAccountNumber(accountNumber);
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of the document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Banking App"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);
        PdfPCell bankAddress = new PdfPCell(new Phrase("38, romania, west indies"));
        bankAddress.setBorder(0);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfoTable = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("start date : "+fromDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell endDate = new PdfPCell(new Phrase("End Date : "+toDate));
        endDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name : "+user.getFirstName()+" "+user.getLastName()+" "+user.getOtherName()));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("address : "+user.getAddress()));
        address.setBorder(0);

        statementInfoTable.addCell(customerInfo);
        statementInfoTable.addCell(statement);
        statementInfoTable.addCell(endDate);
        statementInfoTable.addCell(name);
        statementInfoTable.addCell(space);
        statementInfoTable.addCell(address);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Date"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("Status"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);
        transactions.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        document.add(bankInfoTable);
        document.add(statementInfoTable);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recepient(user.getEmail())
                .subject("STATEMENT OF BANK ACCOUNT")
                .messageBody("Kindly find your requested account statement attached")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactions;
    }
}
