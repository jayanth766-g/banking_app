package com.demo.thebankingproject.repository;

import com.demo.thebankingproject.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("select t from Transaction t where t.createdAt between ?1 and ?2")
    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
}
